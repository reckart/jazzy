package com.swabunga.spell.engine;

import java.util.*;

/**
 * An interface for all dictionary implementations. It defines the most basic
 * operations on a dictionary: adding words, checking if a word is correct, and getting a list
 * of suggestions for misspelled words. 
 */
public interface SpellDictionary {
	
	/**
	 * Add a word permanently to the dictionary.
	 */
	public void addWord(String word);

	/**
	 * Returns true if the word is correctly spelled against the dictionary.
	 */
	public boolean isCorrect(String word);

	/**
	 * Returns a list of Word objects that are the suggestions to any word. 
	 * If the word is correctly spelled, then this method
	 * could return just that one word, or it could still return a list
	 * of words with similar spellings. 
	 * <br/>
	 * Each suggested word has a score, which is an integer
	 * that represents how different the suggested word is from the sourceWord.
	 * If the words are the exactly the same, then the score is 0. 
	 * You can get the dictionary to only return the most similiar words by setting
	 * an appropriately low threshold value.
	 * If you set the threshold value too low, you may get no suggestions for a given word.  
	 * <p>
	 * @param sourceWord the string that we want to get a list of spelling suggestions for
	 * @param scoreThreshold Any words that have score less than this number are returned.
	 * @return List a List of suggested words
	 * @see com.swabunga.spell.engine.Word
	 */
	public List getSuggestions(String sourceWord, int scoreThreshold);
}
