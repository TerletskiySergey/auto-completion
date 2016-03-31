package com.rd.lab.auto_completion.trie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.rd.lab.auto_completion.trie.RWayTrie;
import com.rd.lab.auto_completion.trie.Trie.Tuple;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RWayTrieTest {

	private static final int ALPHABET_FIRST_LETTER_ASCII = 97;
	private static final int ALPHABET_LENGTH = 26;
	private static final int ASCII_END = 255;
	private static final String DELIMITER_REGEX = "\\s(?=[a-z])";
	private static Map<String, String> dic;
	private static final String DIC_FILEPATH = "src/test/resources/dic.txt";

	private static final String EMPTY_STRING = "";

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

	private RWayTrie<String> trie;

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
		assertEquals(dic.size(), trie.size());
		trie = new RWayTrie<>();
		assertEquals(0, trie.size());
		int count = 0;
		for (String str : dic.keySet()) {
			trie.add(new Tuple<String>(str, EMPTY_STRING));
			assertEquals(++count, trie.size());
		}
	}

	@Test
	public void test03Size_emptyTrie_shouldBeZero() {
		trie = new RWayTrie<>();
		assertEquals(0, trie.size());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void test04Add_invalidTuple_shouldNotIncreaseSize() {
		int alphHiBorder = ALPHABET_FIRST_LETTER_ASCII + ALPHABET_LENGTH;
		for (char c = 0; c <= ASCII_END; c++) {
			c = String.valueOf(c).toLowerCase().charAt(0);
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
	public void test05AddSize_addingTheSameKey_sizeShouldNotChange() {
		trie = new RWayTrie<>();
		trie.add(new Tuple<String>("key", "word"));
		assertEquals(1, trie.size());
		trie.add(new Tuple<String>("key", "word"));
		assertEquals(1, trie.size());
	}

	@Test
	public void test06Contains_correctInput_shouldContain() {
		for (String str : dic.keySet()) {
			assertTrue(trie.contains(str));
		}
	}

	@Test
	public void test07Contains_invalidInput_alwaysShouldNotContain() {
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
	public void test08Delete_correctInput_shouldNotContainAfterDelete() {
		for (String str : dic.keySet()) {
			assertTrue(trie.contains(str));
			trie.delete(str);
			assertFalse(trie.contains(str));
		}
	}

	@Test
	public void test09DeleteSize_correctInput_shouldDecreaseSizeOnDeleteCall() {
		int count = dic.size();
		for (String str : dic.keySet()) {
			trie.delete(str);
			assertEquals(--count, trie.size());
		}
	}

	@Test
	public void test10Delete_invalidInput_alwaysShouldNotDelete() {
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
	public void test11Delete_emptyTrie_shouldNotDeleteOrThrowException() {
		trie = new RWayTrie<>();
		try {
			for (String str : dic.keySet()) {
				trie.delete(str);
				assertEquals(0, trie.size());
			}
		} catch (Exception ex) {
			throw new AssertionError();
		}
	}

	@Test
	public void test12Delete_emptyTrie_incorrectInput_shouldNotDeleteOrThrowException() {
		trie = new RWayTrie<>();
		int alphHiBorder = ALPHABET_FIRST_LETTER_ASCII + ALPHABET_LENGTH;
		try {
			for (char c = 0; c <= ASCII_END; c++) {
				if (c <= alphHiBorder && c >= ALPHABET_FIRST_LETTER_ASCII) {
					continue;
				}
				trie.delete(String.valueOf(c));
				assertEquals(0, trie.size());
			}
			trie.delete(null);
			assertEquals(0, trie.size());
		} catch (Exception ex) {
			throw new AssertionError();
		}
	}

	@Test
	public void test13Words_shouldFindAllReferenceWords() {
		List<String> toCheckList = iterableToList(trie.words());
		assertEquals(dic.size(), toCheckList.size());
		List<String> refList = new ArrayList<>(dic.keySet());
		Collections.sort(refList);
		Collections.sort(toCheckList);
		for (int i = 0; i < refList.size(); i++) {
			assertEquals(refList.get(i), toCheckList.get(i));
		}
	}

	@Test(expected = NoSuchElementException.class)
	public void test14Words_emptyTrie_shouldReturnEmptyData() {
		trie = new RWayTrie<>();
		Iterator<String> iter = trie.words().iterator();
		assertFalse(iter.hasNext());
		iter.next();
	}

	@Test(expected = ConcurrentModificationException.class)
	public void test15Words_concurAddCallWithAdding_shouldThrowException() {
		trie = new RWayTrie<>();
		Iterator<String> iter = trie.words().iterator();
		trie.add(new Tuple<String>("a", EMPTY_STRING));
		iter.next();
		trie.add(new Tuple<String>("b", EMPTY_STRING));
		iter.next();
	}

	@Test
	public void test16Words_concurAddCallWithoutAdding_shouldNotThrowException() {
		trie = new RWayTrie<>();
		Iterator<String> iter = trie.words().iterator();
		trie.add(new Tuple<String>("a", EMPTY_STRING));
		trie.add(new Tuple<String>("b", EMPTY_STRING));
		iter.next();
		trie.add(new Tuple<String>("a", EMPTY_STRING));
		iter.next();
	}

	@Test(expected = ConcurrentModificationException.class)
	public void test17Words_concurDeleteCallWithDeleting_shouldThrowException() {
		Iterator<String> iter = trie.words().iterator();
		iter.next();
		trie.delete("a");
		iter.next();
	}

	@Test
	public void test18Words_concurDeleteCallWithoutDeleting_shouldNotThrowException() {
		trie = new RWayTrie<>();
		Iterator<String> iter = trie.words().iterator();
		trie.add(new Tuple<String>("a", EMPTY_STRING));
		trie.add(new Tuple<String>("b", EMPTY_STRING));
		iter.next();
		trie.delete("c");
		iter.next();
	}

	@Test(expected = NoSuchElementException.class)
	public void test19Words_nextOnEmptyIterator_shouldThrowException() {
		trie = new RWayTrie<>();
		Iterator<String> iter = trie.words().iterator();
		iter.next();
	}

	@Test(expected = NoSuchElementException.class)
	public void test20Words_nextWhenNoMoreWords_shouldThrowException() {
		int count = 0;
		Iterator<String> iter = trie.words().iterator();
		try {
			for (; count++ < dic.size();) {
				iter.next();
			}
		} catch (NoSuchElementException ex) {
			throw new AssertionError();
		}
		iter.next();
	}

	@Test
	public void test21WordsWithPrefix_correctInput_shouldFindAppropWords() {
		trie = new RWayTrie<>();
		trie.add(new Tuple<String>("a", EMPTY_STRING));
		trie.add(new Tuple<String>("ab", EMPTY_STRING));
		trie.add(new Tuple<String>("abc", EMPTY_STRING));
		trie.add(new Tuple<String>("abcd", EMPTY_STRING));
		List<String> toCheck = iterableToList(trie.wordsWithPrefix("a"));
		assertEquals("a", toCheck.get(0));
		assertEquals("ab", toCheck.get(1));
		assertEquals("abc", toCheck.get(2));
		assertEquals("abcd", toCheck.get(3));
		assertEquals(4, toCheck.size());
		toCheck = iterableToList(trie.wordsWithPrefix("ab"));
		assertEquals("ab", toCheck.get(0));
		assertEquals("abc", toCheck.get(1));
		assertEquals("abcd", toCheck.get(2));
		assertEquals(3, toCheck.size());
		toCheck = iterableToList(trie.wordsWithPrefix("abc"));
		assertEquals("abc", toCheck.get(0));
		assertEquals("abcd", toCheck.get(1));
		assertEquals(2, toCheck.size());
	}

	@Test
	public void test22WordsWithPrefix_invalidInput_alwaysShouldReturnEmptyData() {
		List<String> toCheck = iterableToList(trie.wordsWithPrefix(null));
		assertEquals(0, toCheck.size());
		int alphHiBorder = ALPHABET_FIRST_LETTER_ASCII + ALPHABET_LENGTH;
		for (char c = 0; c <= ASCII_END; c++) {
			if (c <= alphHiBorder && c >= ALPHABET_FIRST_LETTER_ASCII) {
				continue;
			}
			toCheck = iterableToList(trie.wordsWithPrefix(String.valueOf(c)));
			assertEquals(0, toCheck.size());
		}
	}

	private List<String> iterableToList(Iterable<String> iter) {
		List<String> toReturn = new ArrayList<>();
		for (String str : iter) {
			toReturn.add(str);
		}
		return toReturn;
	}
}
