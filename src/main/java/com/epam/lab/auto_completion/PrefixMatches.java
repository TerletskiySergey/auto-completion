package com.epam.lab.auto_completion;

import java.util.ArrayList;
import java.util.List;

import com.epam.lab.auto_completion.trie.RWayTrie;
import com.epam.lab.auto_completion.trie.Trie;
import com.epam.lab.auto_completion.trie.Trie.Tuple;

public class PrefixMatches {

	private static final String DELIMITER_REGEX = "\\s+";
	private static final int MIN_PREFIX_LENGTH = 2;
	private static final int MIN_WORD_LENGTH = 3;
	private static final int DEFAULT_WORD_SET_LENGTH = 3;

	private Trie<Integer> trie = new RWayTrie<>();

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

	public boolean contains(String word) {
		return trie.contains(word);
	}

	public boolean delete(String word) {
		return trie.delete(word);
	}

	public int size() {
		return trie.size();
	}

	public Iterable<String> wordsWithPrefix(String pref, int k) {
		List<String> result = new ArrayList<>();
		if (pref.length() >= MIN_PREFIX_LENGTH && k > 0) {
			int curLen = 0;
			boolean isFirst = true;
			for (String str : trie.wordsWithPrefix(pref)) {
				if (isFirst) {
					curLen = str.length();
					isFirst = false;
				} else if (str.length() > curLen && --k == 0) {
					break;
				}
				curLen = str.length();
				result.add(str);
			}
		}
		return result;
	}

	public Iterable<String> wordsWithPrefix(String pref) {
		return wordsWithPrefix(pref, DEFAULT_WORD_SET_LENGTH);
	}

}
