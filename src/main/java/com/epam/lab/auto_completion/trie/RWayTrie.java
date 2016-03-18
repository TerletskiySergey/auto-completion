package com.epam.lab.auto_completion.trie;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RWayTrie<T> implements Trie<T> {

	private static final int ALPHABET_LENGTH = 26;
	private static final int ALPHABET_FIRST_LETTER_ASCII = 97;
	private static final String EMPTY_STRING = "";

	@SuppressWarnings("unchecked")
	private static class Node<T> {
		Node<T>[] next = (Node<T>[]) new Node[ALPHABET_LENGTH];
		T value;
	}

	private Node<T> root = new Node<>();
	private int size;

	public void add(Trie.Tuple<T> tuple) {
		if (isTermValid(tuple.getTerm()) && tuple.getValue() != null) {
			root = add(root, tuple.getTerm().toLowerCase(), tuple.getValue(), 0);
			size++;
		}
	}

	private boolean isTermValid(String term) {
		if (term == null) {
			return false;
		}
		if (term.length() != 0) {
			int hiBorder = ALPHABET_FIRST_LETTER_ASCII + ALPHABET_LENGTH;
			for (char c : term.toCharArray()) {
				if (c < ALPHABET_FIRST_LETTER_ASCII || c > hiBorder) {
					return false;
				}
			}
		}
		return true;
	}

	private Node<T> add(Node<T> node, String term, T value, int k) {
		if (node == null) {
			node = new Node<>();
		}
		if (term.length() == k) {
			node.value = value;
			return node;
		}
		int childIndex = term.charAt(k) - ALPHABET_FIRST_LETTER_ASCII;
		node.next[childIndex] = add(node.next[childIndex], term, value, k + 1);
		return node;
	}

	public boolean contains(String word) {
		if (isTermValid(word)) {
			Node<T> node = get(root, word, 0);
			if (node != null && node.value != null) {
				return true;
			}
		}
		return false;
	}

	public boolean delete(String word) {
		if (isTermValid(word)) {
			int initSize = size;
			delete(root, word, 0);
			if (initSize != size) {
				return true;
			}
		}
		return false;
	}

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

	private boolean isLeaf(Node<T> node) {
		for (Node<T> child : node.next) {
			if (child != null) {
				return false;
			}
		}
		return true;
	}

	public T get(String key) {
		Node<T> found = get(root, key, 0);
		return (found == null) ? null : found.value;
	}

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

	public Iterable<String> words() {
		return wordsWithPrefix(EMPTY_STRING);
	}

	public Iterable<String> wordsWithPrefix(String pref) {
		List<String> result = new ArrayList<>();
		if (isTermValid(pref)) {
			Queue<Node<T>> nodeQ = new LinkedList<>();
			Queue<String> wordQ = new LinkedList<>();
			nodeQ.offer(root);
			while (!nodeQ.isEmpty()) {
				Node<T> curNode = nodeQ.poll();
				String curPrefix = (wordQ.peek() == null) ? EMPTY_STRING : wordQ.poll();
				if (curNode.value != null && curPrefix.length() >= pref.length()) {
					result.add(curPrefix);
				}
				for (int i = 0; i < ALPHABET_LENGTH; i++) {
					if (curNode.next[i] != null) {
						char curChar = (char) (i + ALPHABET_FIRST_LETTER_ASCII);
						String curWord = curPrefix.concat(String.valueOf(curChar));
						if (pref.length() >= curWord.length()) {
							int index = curWord.length() - 1;
							if (pref.charAt(index) != curWord.charAt(index)) {
								continue;
							}
						}
						wordQ.offer(curWord);
						nodeQ.offer(curNode.next[i]);
					}
				}
			}
		}
		return result;
	}

	public int size() {
		return size;
	}

	public static void main(String[] arg) {

		RWayTrie<Integer> trie = new RWayTrie<>();

		for (String str : trie.wordsWithPrefix(null)) {
			System.out.println(str);
		}
	}
}
