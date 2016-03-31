package com.rd.lab.auto_completion.trie;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * String tree ('trie') based implementation of Trie interface. Alphabet of the
 * class consists of 26 lowercase letters of English alphabet. In addition to
 * the methods specified in the Trie interface, class also provides method for
 * value retrieval by the key.
 * 
 * @param <T>
 *            - the type of mapped values
 *
 * @author Sergey Terletskiy
 * @version 1.1 03/28/2016
 */
public class RWayTrie<T> implements Trie<T> {
	/**
	 * Instance of the class encapsulates value, to which key is mapped, and an
	 * array of descendants of the same Node type.
	 */
	@SuppressWarnings("unchecked")
	private static class Node<T> {
		/**
		 * Array of descendants of Node type
		 */
		Node<T>[] next = (Node<T>[]) new Node[ALPHABET_LENGTH];
		/**
		 * Value, to which key is mapped
		 */
		T value;
	}

	private class RWayIter implements Iterable<String>, Iterator<String> {
		int modCount = -1;
		String nextWord;
		Queue<Node<T>> nodeQ;
		Queue<String> wordQ;

		RWayIter(String prefix) {
			this.nextWord = prefix;
		}

		@Override
		public boolean hasNext() {
			init();
			return nextWord != null;
		}

		@Override
		public Iterator<String> iterator() {
			return this;
		}

		@Override
		public String next() {
			init();
			checkForComodification();
			String toReturn = nextWord;
			nextWord = getNextWord();
			return toReturn;
		}

		void checkForComodification() {
			if (RWayTrie.this.expectedModCount != modCount) {
				throw new ConcurrentModificationException();
			}
		}

		String getNextWord() {
			try {
				while (!nodeQ.isEmpty()) {
					Node<T> curNode = nodeQ.poll();
					String curWord = wordQ.poll();
					for (int i = 0; i < ALPHABET_LENGTH; i++) {
						if (curNode.next[i] != null) {
							char curChar = (char) (i + ALPHABET_FIRST_LETTER_ASCII);
							wordQ.offer(curWord.concat(String.valueOf(curChar)));
							nodeQ.offer(curNode.next[i]);
						}
					}
					if (curNode.value != null) {
						return curWord;
					}
				}
				if (nextWord == null) {
					throw new NoSuchElementException();
				}
			} catch (NullPointerException ex) {
				throw new NoSuchElementException();
			}
			return null;
		}

		void init() {
			if (modCount == -1) {
				Node<T> node = RWayTrie.this.get(RWayTrie.this.root, nextWord, 0);
				modCount = expectedModCount;
				if (node != null) {
					nodeQ = new LinkedList<>();
					wordQ = new LinkedList<>();
					nodeQ.offer(node);
					wordQ.offer(nextWord);
					nextWord = getNextWord();
				} else {
					nextWord = null;
				}
			}
		}
	}

	/**
	 * ASCII code of the alphabet's first letter that corresponds to the
	 * lowercase 'a' letter of English alphabet.
	 */
	private static final int ALPHABET_FIRST_LETTER_ASCII = 97;
	/**
	 * String tree alphabet length
	 */
	private static final int ALPHABET_LENGTH = 26;

	/**
	 * Alias for zero length string.
	 */
	private static final String EMPTY_STRING = "";

	/**
	 * The number of times this string ('tree') has been <i>structurally
	 * modified</i>. Structural modifications are those that change the size of
	 * the list, or otherwise perturb it in such a fashion that iterations in
	 * progress may yield incorrect results.
	 */
	private int expectedModCount;

	/**
	 * Root of this string tree ('trie')
	 */
	private Node<T> root = new Node<>();
	/**
	 * Current length of this string tree ('trie')
	 */
	private int size;

	/**
	 * Associates the specified value with the specified key in this tree. If
	 * the map previously contained a mapping for the key, the old value is
	 * replaced. Specified value and key are encapsulated in Tuple instance.
	 * Preliminary check of the passing Tuple instance is performed (key or/and
	 * a value is/are null values or string key contains chars that are not
	 * specified in classes' alphabet).
	 * 
	 * @param tuple
	 *            - object encapsulating key with which the specified value is
	 *            to be associated and value to be associated with the specified
	 *            key.
	 */
	public void add(Trie.Tuple<T> tuple) {
		if (isTermValid(tuple.getTerm()) && tuple.getValue() != null) {
			root = add(root, tuple.getTerm(), tuple.getValue(), 0);
		}
	}

	/**
	 * Returns true if this trie contains a mapping for the specified word,
	 * which serves as a key.
	 * 
	 * @param word
	 *            - key whose presence in this trie is to be tested
	 * @return Boolean value indicating if the mapping exists for the specified
	 *         key.
	 */
	public boolean contains(String word) {
		if (isTermValid(word)) {
			Node<T> node = get(root, word, 0);
			if (node != null && node.value != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the mapping for a key from this trie if it is present. Returns
	 * whether the key to delete was found in this trie.
	 * 
	 * @param word
	 *            - key whose mapping is to be removed from the trie
	 * @return true if key to delete was found in the trie.
	 */
	public boolean delete(String word) {
		if (isTermValid(word)) {
			int initSize = size;
			delete(root, word, 0);
			if (initSize != size) {
				expectedModCount++;
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the value to which the specified string key is mapped, or null if
	 * this map contains no mapping for the key.
	 * 
	 * @param key
	 *            - the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this
	 *         map contains no mapping for the key
	 */
	public T get(String key) {
		Node<T> found = get(root, key, 0);
		return (found == null) ? null : found.value;
	}

	/**
	 * Returns the number of key-value mappings in this trie.
	 * 
	 * @return the number of key-value mappings in this trie.
	 */
	public int size() {
		return size;
	}

	/**
	 * Returns an Iterable view of the string keys contained in this trie.
	 * 
	 * @return an Iterable view of the string keys contained in this trie.
	 */
	public Iterable<String> words() {
		return wordsWithPrefix(EMPTY_STRING);
	}

	/**
	 * Returns an Iterable view of the string keys with a specific prefix
	 * contained in this trie.
	 * 
	 * @param pref
	 *            - prefix of the string values should contained in returned
	 *            Iterable object.
	 * @return an Iterable view of the string keys with a specific prefix
	 *         contained in this trie.
	 */
	public Iterable<String> wordsWithPrefix(String pref) {
		return (isTermValid(pref)) ? new RWayIter(pref) : new ArrayList<>();
	}

	/**
	 * Repeats the functionality of public add() method. Adds recursively the
	 * association between the specified string key and value to the string tree
	 * ('trie') with the specified root node.
	 * 
	 * @param node
	 *            - root node of the string tree ('trie') the association to be
	 *            added in.
	 * @param term
	 *            - key with which the specified value is to be associated.
	 * @param value
	 *            - value to be associated with the specified key
	 * @param k
	 *            - current string key length
	 */
	private Node<T> add(Node<T> node, String term, T value, int k) {
		if (node == null) {
			node = new Node<>();
		}
		if (term.length() == k) {
			if (node.value == null) {
				size++;
				expectedModCount++;
			}
			node.value = value;
			return node;
		}
		int childIndex = term.charAt(k) - ALPHABET_FIRST_LETTER_ASCII;
		node.next[childIndex] = add(node.next[childIndex], term, value, k + 1);
		return node;
	}

	/**
	 * Repeats the functionality of public delete() method. Removes recursively
	 * the mapping for a string key from a string tree ('trie'), specified by a
	 * passing root node.
	 * 
	 * @param node
	 *            - root node of the string tree ('trie') the key association to
	 *            be removed from.
	 * @param word
	 *            - key whose mapping is to be removed from the trie
	 * @param k
	 *            - current string key length
	 */
	private Node<T> delete(Node<T> node, String word, int k) {
		if (node == null) {
			return null;
		}
		if (word.length() == k && node.value != null) {
			node.value = null;
			size--;
			if (isLeaf(node)) {
				return null;
			}
			return node;
		} else if (k >= word.length()) {
			return null;
		}
		int childIndex = word.charAt(k) - ALPHABET_FIRST_LETTER_ASCII;
		Node<T> toDelete = delete(node.next[childIndex], word, k + 1);
		node.next[childIndex] = toDelete;
		if (toDelete == null && node.value == null && isLeaf(node)) {
			return null;
		}
		return node;
	}

	/**
	 * Searches recursively for the node associated with passing string key
	 * value in the string tree ('trie') with passing root node.
	 * 
	 * @param node
	 *            - root node of the string tree to search in.
	 * @param key
	 *            - string key to search for.
	 * @param k
	 *            - current string key length
	 * @return String tree node, to which passing key value corresponds, or null
	 *         if the key association is absent.
	 */
	private Node<T> get(Node<T> node, String key, int k) {
		if (node == null) {
			return null;
		}
		if (key.length() == k) {
			return node;
		}
		int childIndex = key.charAt(k) - ALPHABET_FIRST_LETTER_ASCII;
		return get(node.next[childIndex], key, k + 1);
	}

	/**
	 * Checks if the passing node is a leaf in the string tree (i.e. if the
	 * passing node has no non-null descendants).
	 * 
	 * @param node
	 *            - node to be checked
	 * @return Boolean value indicating if the passing node is a leaf.
	 */
	private boolean isLeaf(Node<T> node) {
		for (Node<T> child : node.next) {
			if (child != null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the passing string value can serve as a key in this string
	 * tree. Check includes null verification and verification of belonging of
	 * each string char to the alphabet of this tree.
	 * 
	 * @param term
	 *            - string value to be checked.
	 * @return Boolean value indicating whether the passing string value can
	 *         serve as a key.
	 * 
	 */
	private boolean isTermValid(String term) {
		if (term == null) {
			return false;
		}
		int hiBorder = ALPHABET_FIRST_LETTER_ASCII + ALPHABET_LENGTH;
		for (char c : term.toCharArray()) {
			if (c < ALPHABET_FIRST_LETTER_ASCII || c > hiBorder) {
				return false;
			}
		}
		return true;
	}
}
