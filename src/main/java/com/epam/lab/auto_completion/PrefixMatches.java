package com.epam.lab.auto_completion;

import java.util.ArrayList;
import java.util.List;

import com.epam.lab.auto_completion.trie.Trie;
import com.epam.lab.auto_completion.trie.Trie.Tuple;

/**
 * Abstraction of the class represents in-memory string dictionary. The class
 * provides methods for filling out a dictionary, removing words from
 * dictionary, estimating dictionary volume and returning all the dictionary
 * words with the specified prefix. Instance of the class uses the functionality
 * of the encapsulated Trie interface instance by delegating it the major part
 * of method calls.
 * 
 * @author Serhii Terletskyi
 * @version 1.0 03/18/2016
 */
public class PrefixMatches {
	/**
	 * Regular expression for splitting strings in add() method.
	 */
	private static final String DELIMITER_REGEX = "\\s+";
	/**
	 * Minimal length of the string value that can be regarded as prefix.
	 */
	private static final int MIN_PREFIX_LENGTH = 2;
	/**
	 * Minimal length of the string value that can be stored in the dictionary.
	 */
	private static final int MIN_WORD_LENGTH = 3;
	/**
	 * Default number of word-sets that has wordsWithPrefix(String) method to
	 * return. Words of the same word-set have the same length.
	 */
	private static final int DEFAULT_WORD_SET_LENGTH = 3;

	/**
	 * Encapsulated instance of the Trie interface, to which the major part of
	 * this object method calls are delegated.
	 */
	private Trie<Integer> trie;

	public PrefixMatches(Trie<Integer> trie) {
		super();
		this.trie = trie;
	}

	public void setTrie(Trie<Integer> trie) {
		this.trie = trie;
	}

	/**
	 * Splits input strings, trim them and add those of them to the dictionary
	 * whose length is bigger than MIN_WORD_LENGTH. However, not all the words
	 * satisfying previous condition can be added to the dictionary, to be added
	 * to the dictionary input strings should contain characters satisfying
	 * inner Trie instance alphabet.
	 * 
	 * @param strings
	 *            - input array of the strings to be added.
	 * @return number of words added to the dictionary.
	 */
	public int add(String... strings) {
		int count = 0;
		for (String str : strings) {
			for (String arStr : str.split(DELIMITER_REGEX)) {
				arStr = arStr.trim();
				if (arStr.length() >= MIN_WORD_LENGTH) {
					trie.add(new Tuple<Integer>(arStr, arStr.length()));
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Returns true if this dictionary contains the specified word.
	 * 
	 * @param word
	 *            - word whose presence in this dictionary is to be tested.
	 * @return Boolean value indicating if this dictionary contains the
	 *         specified word.
	 */
	public boolean contains(String word) {
		return trie.contains(word);
	}

	/**
	 * Removes the specified word from this dictionary if it is present. Returns
	 * whether the specified word to delete was found.
	 * 
	 * @param word
	 *            - word to be deleted from dictionary.
	 * @return Boolean value indicating if the specified word to delete was
	 *         found in the dictionary.
	 */
	public boolean delete(String word) {
		return trie.delete(word);
	}

	/**
	 * Returns number of words containing in this dictionary.
	 * 
	 * @return Number of available dictionary words.
	 */
	public int size() {
		return trie.size();
	}

	/**
	 * Returns an Iterable view containing all the dictionary words with
	 * specified prefix. Result view contains at most specified number of the
	 * length-sets. Words of the same length-set have the same length. Words of
	 * the result view are length sorted.
	 * 
	 * @param pref
	 *            - prefix of the string values should contained in returned
	 *            Iterable object.
	 * @param k
	 *            - number of word-sets containing in result Iterable view.
	 * @return Iterable view containing all the length sorted dictionary words
	 *         with specified prefix within specified number of length-sets.
	 */
	public Iterable<String> wordsWithPrefix(String pref, int k) {
		List<String> result = new ArrayList<>();
		if (pref.length() >= MIN_PREFIX_LENGTH && k > 0) {
			int curLen = 0;
			for (String str : trie.wordsWithPrefix(pref)) {
				if (str.length() >= MIN_WORD_LENGTH) {
					if (str.length() > curLen && --k == -1) {
						break;
					}
					curLen = str.length();
					result.add(str);
				}
			}
		}
		return result;
	}

	/**
	 * Returns an Iterable view containing all the dictionary words with
	 * specified prefix. Result view contains at most number of the length-sets
	 * equals to the DEFAULT_WORD_SET_LENGTH. Words of the same length-set have
	 * the same length. Words of the result view are length sorted.
	 * 
	 * @param pref
	 *            - prefix of the string values should contained in returned
	 *            Iterable object.
	 * @return Iterable view containing all the length sorted dictionary words
	 *         with specified prefix within default number of length-sets.
	 */
	public Iterable<String> wordsWithPrefix(String pref) {
		return wordsWithPrefix(pref, DEFAULT_WORD_SET_LENGTH);
	}
}
