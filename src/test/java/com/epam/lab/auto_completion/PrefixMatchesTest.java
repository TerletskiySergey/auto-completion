package com.epam.lab.auto_completion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.epam.lab.auto_completion.trie.RWayTrie;
import com.epam.lab.auto_completion.trie.Trie.Tuple;

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
	public void test05WordsWithPrefixString_argLessThenMinWordLength_shouldReturnEmptyData() {
		pm = new PrefixMatches(new RWayTrie<>());
		for (String str : dic.keySet()) {
			if (str.length() < MIN_WORD_LENGTH) {
				Iterable<String> result = pm.wordsWithPrefix(str);
				assertFalse(result.iterator().hasNext());
			}
		}
	}

	@Test
	public void test06WordsWithPrefixString_correctInput_shouldReturnDefaultNumberOfWordSets() {
		trie = new RWayTrie<>();
		for (String str : dic.keySet()) {
			trie.add(new Tuple<Integer>(str, str.length()));
		}
		pm = new PrefixMatches(trie);
		for (String str : dic.keySet()) {
			if (str.length() >= MIN_PREFIX_LENGTH) {
				List<String> trieResult = (List<String>) trie.wordsWithPrefix(str);
				// Filtering reference collection from words with length less
				// than
				// MIN_WORD_LENGTH
				for (int i = 0; i < trieResult.size(); i++) {
					if (trieResult.get(i).length() < MIN_WORD_LENGTH) {
						trieResult.remove(i);
					} else {
						break;
					}
				}
				int lenCount = 0;
				List<String> pmResult = (List<String>) pm.wordsWithPrefix(str);
				// Estimating actual number of word sets
				for (int i = 0, curLen = 0; i < pmResult.size(); i++) {
					if (curLen < pmResult.get(i).length()) {
						curLen = pmResult.get(i).length();
						lenCount++;
					}
					assertEquals(trieResult.get(i), pmResult.get(i));
				}
				if (!pmResult.isEmpty()) {
					assertTrue(lenCount >= 1);
				}
				assertTrue(lenCount <= DEFAULT_WORD_SET_LENGTH);
			}
		}
	}

	@Test
	public void test07WordsWithPrefixStringInt_correctInput_shouldReturnAppropNumberOfWordSets() {
		trie = new RWayTrie<>();
		for (String str : dic.keySet()) {
			trie.add(new Tuple<Integer>(str, str.length()));
		}
		pm = new PrefixMatches(trie);
		for (String str : dic.keySet()) {
			if (str.length() >= MIN_PREFIX_LENGTH) {
				List<String> trieResult = (List<String>) trie.wordsWithPrefix(str);
				int refLenCount = 0;
				// Filtering reference collection from words with length less
				// than MIN_WORD_LENGTH and estimating number of word sets in
				// reference collection
				for (int i = 0, curLen = 0; i < trieResult.size(); i++) {
					if (trieResult.get(i).length() < MIN_WORD_LENGTH) {
						trieResult.remove(i);
						continue;
					}
					if (curLen < trieResult.get(i).length()) {
						curLen = trieResult.get(i).length();
						refLenCount++;
					}
				}
				for (int i = 1; i <= refLenCount; i++) {
					int lenCount = 0;
					List<String> pmResult = (List<String>) pm.wordsWithPrefix(str, i);
					// Estimating actual number of word sets
					for (int j = 0, curLen = 0; j < pmResult.size(); j++) {
						if (curLen < pmResult.get(j).length()) {
							curLen = pmResult.get(j).length();
							lenCount++;
						}
						assertEquals(trieResult.get(j), pmResult.get(j));
					}
					assertEquals(i, lenCount);
				}
			}
		}
	}

	@Test
	public void test08WordsWithPrefixStringInt_invalidIntArg_shouldReturnEmptyData() {
		trie = new RWayTrie<>();
		for (String str : dic.keySet()) {
			trie.add(new Tuple<Integer>(str, str.length()));
		}
		pm = new PrefixMatches(trie);
		for (String str : dic.keySet()) {
			assertEquals(0, ((List<String>) pm.wordsWithPrefix(str, -1)).size());
			assertEquals(0, ((List<String>) pm.wordsWithPrefix(str, 0)).size());
		}
	}
}
