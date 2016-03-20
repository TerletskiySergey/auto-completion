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

	public void add(Tuple<T> tuple);

	public boolean contains(String word);

	public boolean delete(String word);

	public Iterable<String> words();

	public Iterable<String> wordsWithPrefix(String pref);

	public int size();
}
