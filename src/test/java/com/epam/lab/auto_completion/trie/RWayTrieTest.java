package com.epam.lab.auto_completion.trie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.epam.lab.auto_completion.trie.Trie.Tuple;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RWayTrieTest {

	private static final String DIC_FILEPATH = "src/test/resources/dic.txt";
	private static final String DELIMITER_REGEX = "\\s(?=[a-z])";
	private static final int ALPHABET_LENGTH = 26;
	private static final int ALPHABET_FIRST_LETTER_ASCII = 97;
	private static final int ASCII_END = 255;
	private static final String EMPTY_STRING = "";

	private static Map<String, String> dic;
	private RWayTrie<String> trie;

	@BeforeClass
	public static void setupDic() throws IOException {
		Scanner scanner = new Scanner(new File(DIC_FILEPATH));
		dic = new HashMap<>();
		String[] wordAr;
		try {
			while (scanner.hasNextLine()) {
				wordAr = scanner.nextLine().split(DELIMITER_REGEX);
				dic.put(wordAr[1].trim(), wordAr[0].trim());
			}
		} finally {
			scanner.close();
		}
	}

	@Before
	public void setupTest() {
		trie = new RWayTrie<>();
		for (String str : dic.keySet()) {
			trie.add(new Tuple<String>(str, dic.get(str)));
		}
	}

	@Test
	public void test01GetAdd_shouldGetCorrectValues() {
		for (String str : dic.keySet()) {
			assertEquals(dic.get(str), trie.get(str));
		}
	}

	@Test
	public void test02AddSize_shouldIncreseSizeOnAddCall() {
		trie = new RWayTrie<>();
		int count = 0;
		for (String str : dic.keySet()) {
			trie.add(new Tuple<String>(str, EMPTY_STRING));
			assertEquals(++count, trie.size());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void test03Add_invalidTuple_shouldNotIncreaseSize() {
		int alphHiBorder = ALPHABET_FIRST_LETTER_ASCII + ALPHABET_LENGTH;
		for (char c = 0; c <= ASCII_END; c++) {
			if (c <= alphHiBorder && c >= ALPHABET_FIRST_LETTER_ASCII) {
				continue;
			}
			trie.add(new Tuple(String.valueOf(c), EMPTY_STRING));
			assertEquals(dic.size(), trie.size());
		}
		trie.add(new Tuple(null, EMPTY_STRING));
		assertEquals(dic.size(), trie.size());
		trie.add(new Tuple(EMPTY_STRING, null));
		assertEquals(dic.size(), trie.size());
	}

	@Test
	public void test04Contains_correctInput_shouldContain() {
		for (String str : dic.keySet()) {
			assertTrue(trie.contains(str));
		}
	}

	@Test
	public void test05Contains_invalidInput_alwaysShouldNotContain() {
		int alphHiBorder = ALPHABET_FIRST_LETTER_ASCII + ALPHABET_LENGTH;
		for (char c = 0; c <= ASCII_END; c++) {
			if (c <= alphHiBorder && c >= ALPHABET_FIRST_LETTER_ASCII) {
				continue;
			}
			assertFalse(trie.contains(String.valueOf(c)));
		}
		assertFalse(trie.contains(null));
	}

	@Test
	public void test06Delete_correctInput_shouldNotContainAfterDelete() {
		for (String str : dic.keySet()) {
			assertTrue(trie.contains(str));
			trie.delete(str);
			assertFalse(trie.contains(str));
		}
	}

	@Test
	public void test07DeleteSize_correctInput_shouldDecreaseSizeOnDeleteCall() {
		for (String str : dic.keySet()) {
			assertTrue(trie.contains(str));
			trie.delete(str);
			assertFalse(trie.contains(str));
		}
	}

	@Test
	public void test08Delete_invalidInput_alwaysShouldNotDelete() {
		int alphHiBorder = ALPHABET_FIRST_LETTER_ASCII + ALPHABET_LENGTH;
		for (char c = 0; c <= ASCII_END; c++) {
			if (c <= alphHiBorder && c >= ALPHABET_FIRST_LETTER_ASCII) {
				continue;
			}
			assertFalse(trie.contains(String.valueOf(c)));
			trie.delete(String.valueOf(c));
			assertEquals(dic.size(), trie.size());
		}
		trie.delete(null);
		assertEquals(dic.size(), trie.size());
	}

	@Test
	public void test09Words_shouldFindAllReferenceWords() {
		List<String> toCheckList = (List<String>) trie.words();
		assertEquals(dic.size(), toCheckList.size());
		List<String> refList = new ArrayList<>(dic.keySet());
		Collections.sort(refList);
		Collections.sort(toCheckList);
		for (int i = 0; i < refList.size(); i++) {
			assertEquals(refList.get(i), toCheckList.get(i));
		}
	}

	@Test
	public void test10WordsWithPrefix_oneLetterPrefixWordsFromRefListShouldBeSame() {
		List<String> refList = new ArrayList<>(dic.keySet());
		List<String> refPrefixList = new ArrayList<>();
		Collections.sort(refList);
		String prefix = refList.get(0);
		for (String str : refList) {
			if (str.indexOf(prefix) == 0) {
				refPrefixList.add(str);
			} else {
				List<String> toCheckList = (List<String>) trie.wordsWithPrefix(prefix);
				Collections.sort(toCheckList);
				assertEquals(refPrefixList.size(), toCheckList.size());
				for (int i = 0; i < refPrefixList.size(); i++) {
					assertEquals(refPrefixList.get(i), toCheckList.get(i));
				}
				refPrefixList.clear();
				refPrefixList.add(prefix = str);
			}
		}
	}

	@Test
	public void test11WordsWithPrefix_invalidInput_alwaysShouldReturnEmptyData() {
		List<String> toCheck = (List<String>) trie.wordsWithPrefix(null);
		assertEquals(0, toCheck.size());
		int alphHiBorder = ALPHABET_FIRST_LETTER_ASCII + ALPHABET_LENGTH;
		for (char c = 0; c <= ASCII_END; c++) {
			if (c <= alphHiBorder && c >= ALPHABET_FIRST_LETTER_ASCII) {
				continue;
			}
			toCheck = (List<String>) trie.wordsWithPrefix(String.valueOf(c));
			assertEquals(0, toCheck.size());
		}
	}
}
