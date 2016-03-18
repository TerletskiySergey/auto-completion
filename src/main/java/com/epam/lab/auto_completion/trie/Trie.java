package com.epam.lab.auto_completion.trie;

public interface Trie<T> {

	public static class Tuple<T> {
		private String term;
		private T value;

		public Tuple(String term, T value) {
			super();
			this.term = term;
			this.value = value;
		}

		public String getTerm() {
			return term;
		}

		public T getValue() {
			return value;
		}

		public void setTerm(String term) {
			this.term = term;
		}

		public void setValue(T value) {
			this.value = value;
		}
	}

	// Добавляет в Trie пару слово - term, и его вес - weight.
	// В качестве веса используйте длину слова
	public void add(Tuple<T> tuple);

	// есть ли слово в Trie
	public boolean contains(String word);

	// удаляет слово из Trie
	public boolean delete(String word);

	// итератор по всем словам, обход в ширину
	public Iterable<String> words();

	// итератор по всем словам, начинающимся с pref, обход в ширину
	public Iterable<String> wordsWithPrefix(String pref);

	// к-во слов в Trie
	public int size();
}
