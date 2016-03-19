package com.epam.lab.auto_completion;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.epam.lab.auto_completion.trie.Trie;

public class PrefixMatchesTest {

	private static final String DIC_FILEPATH = "src/test/resources/dic.txt";
	private static final String DELIMITER_REGEX = "\\s(?=[a-z])";

	private static Map<String, String> dic;

	@Mock
	private Trie<Integer> trie;
	@InjectMocks
	private PrefixMatches pm;

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
		pm = new PrefixMatches();
		for (String str : dic.keySet()) {
			pm.add(str);
		}
	}

	@Test
	@Ignore
	public void testAdd() {

	}

	@Test
	@Ignore
	public void testContains() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testSize() {

		System.out.println("OK");
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testWordsWithPrefixStringInt() {
		for (String str : pm.wordsWithPrefix("aaaa", 2)) {
			System.out.println(str);
		}
	}

	@Test
	// @Ignore
	public void testWordsWithPrefixString() {
		for (String str : pm.wordsWithPrefix("aaa")) {
			System.out.println(str);
		}
	}

}
