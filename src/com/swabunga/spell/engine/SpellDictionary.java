package com.swabunga.spell.engine;

import java.util.List;

public interface SpellDictionary {
	/**
	 * Add a word permanantly to the dictionary (and the dictionary file).
	 * <p>This needs to be made thread safe (synchronized)</p>
	 */
	public void addWord(String word);

	/**
	 * Returns the code representing the word.
	 */
	public String getCode(String word);

	/**
	 * Returns a list of strings (words) for the code.
	 */
	public List getWords(String code);

	/**
	 * Returns true if the word is correctly spelled against the current word list.
	 */
	public boolean isCorrect(String word);

	/**
	 * Returns a linked list of Word objects that are the suggestions to an
	 * incorrect word.
	 * <p>
	 * @param word Suggestions for given mispelt word
	 * @param threshold The lower boundary of similarity to mispelt word
	 * @return List a List of suggestions
	 */
	public List getSuggestions(String word, int threshold);
}
