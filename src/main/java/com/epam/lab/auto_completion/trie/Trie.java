package com.epam.lab.auto_completion.trie;

/**
 * An object that maps keys to values. An object of interface cannot contain
 * duplicate keys; each key can map to at most one value. String values serve as
 * keys. Interface implementation infers that implementor class stores data in
 * string tree, so called 'trie', where key is not stored in tree node, but path
 * from the tree root to the tree node determines node key. Interface provides
 * methods for adding, deleting mappings, checking of mapping presence,
 * returning of mappings quantity as well as of all the contained string keys
 * and of all the contained string keys with specified prefix.
 * 
 * @param <T>
 *            - the type of mapped values
 * @author Serhii Terletskyi
 * @version 1.0 03/18/2016
 */
public interface Trie<T> {

	/**
	 * Wrapper class that encapsulates key and value. Class instance is passed
	 * as a parameter to the Trie.add() method.
	 * 
	 * @param <T>
	 *            - the type of encapsulating values
	 */
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

	/**
	 * Associates the specified value with the specified key in this object. If
	 * the object previously contained a mapping for the key, the old value is
	 * replaced by the specified value. Specified value and key are encapsulated
	 * in Tuple instance.
	 * 
	 * @param tuple
	 *            - object encapsulating key with which the specified value is
	 *            to be associated and value to be associated with the specified
	 *            key.
	 */
	public void add(Tuple<T> tuple);

	/**
	 * Returns true if this trie contains a mapping for the specified word,
	 * which serves as a key.
	 * 
	 * @param word
	 *            - key whose presence in this trie is to be tested
	 */
	public boolean contains(String word);

	/**
	 * Removes the mapping for a key from this trie if it is present. Returns
	 * whether the key to delete was found in this trie.
	 * 
	 * @param word
	 *            - key whose mapping is to be removed from the trie
	 * @return true if key to delete was found in the trie.
	 */
	public boolean delete(String word);

	/**
	 * Returns an Iterable view of the string keys contained in this trie.
	 * 
	 * @return an Iterable view of the string keys contained in this trie.
	 */
	public Iterable<String> words();

	/**
	 * Returns an Iterable view of the string keys with a specific prefix
	 * contained in this trie.
	 * 
	 * @param pref
	 *            - prefix of the string values should contained in returned
	 *            Iterable object.
	 * @return an Iterable view of the string keys with a specific prefix
	 *         contained in this trie.
	 */
	public Iterable<String> wordsWithPrefix(String pref);

	/**
	 * Returns the number of key-value mappings in this trie.
	 * 
	 * @return the number of key-value mappings in this trie.
	 */
	public int size();
}
