package com.rd.lab.auto_completion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.rd.lab.auto_completion.trie.RWayTrie;
import com.rd.lab.auto_completion.trie.Trie.Tuple;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrefixMatchesTest {

	private static final String DELIMITER_REGEX = "\\s(?=[a-z])";
	private static Map<String, String> dic;
	private static final String DIC_FILEPATH = "src/test/resources/dic.txt";
	private static final int ITERATION_LIMIT = 50000;
	private static final int MIN_WORD_LENGTH = 3;
	private static final int DEFAULT_WORD_SET_LENGTH = 3;
	private static final int MIN_PREFIX_LENGTH = 2;

	private static final String SPACE = " ";

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

	@InjectMocks
	private PrefixMatches pm;

	@Mock
	private RWayTrie<Integer> trie;

	@Before
	public void setupTest() {

		initMocks(this);
		when(trie.contains(anyString()))
				.thenAnswer((inv) -> ((String) inv.getArguments()[0]).length() >= MIN_WORD_LENGTH);
		when(trie.delete(anyString()))
				.thenAnswer((inv) -> ((String) inv.getArguments()[0]).length() >= MIN_WORD_LENGTH);
		when(trie.size()).then(new Answer<Integer>() {
			private int count;

			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				return (++count % 2 == 0) ? count / 2 : (count + 1) / 2;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test01Add_shouldSplitAndAddAllWordsThatSatisfyMinLength() {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (String str : dic.keySet()) {
			if (str.length() < MIN_WORD_LENGTH) {
				continue;
			}
			sb.append(str).append(SPACE);
			count++;
		}
		assertEquals(count, pm.add(sb.toString()));
		verify(trie, times(count)).add(isA(Tuple.class));
		verifyNoMoreInteractions(trie);
	}

	@Test
	public void test02Contains_shouldAlwaysCallAndReturnTrieMethod() {
		int count = 0;
		for (String str : dic.keySet()) {
			if (count + 1 > ITERATION_LIMIT) {
				break;
			}
			assertEquals(trie.contains(str), pm.contains(str));
			count++;
		}
		verify(trie, times(2 * count)).contains(anyString());
	}

	@Test
	public void test03Delete_shouldAlwaysCallAndReturnTrieMethod() {
		int count = 0;
		for (String str : dic.keySet()) {
			if (count + 1 > ITERATION_LIMIT) {
				break;
			}
			assertEquals(trie.delete(str), pm.delete(str));
			count++;
		}
		verify(trie, times(2 * count)).delete(anyString());
	}

	@Test
	public void test04Size_shouldAlwaysCallAndReturnTrieMethod() {
		for (int i = 0; i < ITERATION_LIMIT; i++) {
			assertEquals(trie.size(), pm.size());
		}
		verify(trie, times(2 * ITERATION_LIMIT)).size();
	}

	@Test
	public void test05WordsWithPrefixString_argLessThenMinPrefixLength_shouldReturnEmptyData() {
		pm = new PrefixMatches(new RWayTrie<>());
		for (String str : dic.keySet()) {
			if (str.length() < MIN_PREFIX_LENGTH) {
				Iterable<String> result = pm.wordsWithPrefix(str);
				assertFalse(result.iterator().hasNext());
			}
		}
	}

	@Test(expected = NoSuchElementException.class)
	public void test06WordsWithPrefixString_correctInput_emptyContainer_shouldReturnEmptyData() {
		pm = new PrefixMatches(new RWayTrie<>());
		Iterator<String> iter = pm.wordsWithPrefix("prefix").iterator();
		assertFalse(iter.hasNext());
		iter.next();
	}

	@Test
	public void test07WordsWithPrefixString_correctInput_shouldReturnDefaultNumberOfWordSets() {
		trie = new RWayTrie<>();
		pm = new PrefixMatches(trie);
		pm.add("abc", "abcd", "abcde", "abcdef");
		List<String> toCheck = iterableToList(pm.wordsWithPrefix("abc"));
		assertEquals("abc", toCheck.get(0));
		assertEquals("abcd", toCheck.get(1));
		assertEquals("abcde", toCheck.get(2));
		assertEquals(DEFAULT_WORD_SET_LENGTH, toCheck.size());
		toCheck = iterableToList(pm.wordsWithPrefix("abcd"));
		assertEquals("abcd", toCheck.get(0));
		assertEquals("abcde", toCheck.get(1));
		assertEquals("abcdef", toCheck.get(2));
		assertEquals(DEFAULT_WORD_SET_LENGTH, toCheck.size());
		toCheck = iterableToList(pm.wordsWithPrefix("abcde"));
		assertEquals("abcde", toCheck.get(0));
		assertEquals("abcdef", toCheck.get(1));
		assertEquals(DEFAULT_WORD_SET_LENGTH - 1, toCheck.size());
		toCheck = iterableToList(pm.wordsWithPrefix("abcdef"));
		assertEquals("abcdef", toCheck.get(0));
		assertEquals(DEFAULT_WORD_SET_LENGTH - 2, toCheck.size());
	}

	@Test
	public void test08WordsWithPrefixStringInt_correctInput_shouldReturnAppropNumberOfWordSets() {
		trie = new RWayTrie<>();
		pm = new PrefixMatches(trie);
		pm.add("abc", "abcd", "abcde", "abcdef", "abcdefg");
		List<String> toCheck = iterableToList(pm.wordsWithPrefix("abc", 3));
		assertEquals("abc", toCheck.get(0));
		assertEquals("abcd", toCheck.get(1));
		assertEquals("abcde", toCheck.get(2));
		assertEquals(3, toCheck.size());
		toCheck = iterableToList(pm.wordsWithPrefix("abc", 4));
		assertEquals("abc", toCheck.get(0));
		assertEquals("abcd", toCheck.get(1));
		assertEquals("abcde", toCheck.get(2));
		assertEquals("abcdef", toCheck.get(3));
		assertEquals(4, toCheck.size());
		toCheck = iterableToList(pm.wordsWithPrefix("abc", 5));
		assertEquals("abc", toCheck.get(0));
		assertEquals("abcd", toCheck.get(1));
		assertEquals("abcde", toCheck.get(2));
		assertEquals("abcdef", toCheck.get(3));
		assertEquals("abcdefg", toCheck.get(4));
		assertEquals(5, toCheck.size());
	}

	@Test
	public void test09WordsWithPrefixStringInt_invalidIntArg_shouldReturnEmptyData() {
		trie = new RWayTrie<>();
		pm = new PrefixMatches(trie);
		pm.add("aaa");
		assertFalse(pm.wordsWithPrefix("aaa", 0).iterator().hasNext());
		assertFalse(pm.wordsWithPrefix("aaa", -1).iterator().hasNext());
	}

	private List<String> iterableToList(Iterable<String> iter) {
		List<String> toReturn = new ArrayList<>();
		for (String str : iter) {
			toReturn.add(str);
		}
		return toReturn;
	}
}
