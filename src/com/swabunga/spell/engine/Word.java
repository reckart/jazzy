package com.swabunga.spell.engine;

/** 
 * The Word object holds information for one suggested spelling. 
 * It contains both the suggested word string and the distance cost, which represents how different the suggested
 * word is from the misspelling.
 *  <p>This class is now immutable.
 *  </p>
 */
public class Word{ //implements Comparator {
    private String word;
    private int score;

    public Word(String word, int score) {
      this.word = word;
      this.score = score;
    }

    /**
	 * Compares two words, mostly for the purpose of sorting words. 
	 * @param the first word
	 * @param the second word
	 * @return -1 if the first word is more similar to the mispelled word
	 * <br>1 if the second word is more similar to the misspelled word
	 * <br>0 if both words are equally similar
	 * 
	 */
    public int compare(Object o1, Object o2) {
        if (((Word) o1).getCost() < ((Word) o2).getCost()) return -1;
        if (((Word) o1).getCost() == ((Word) o2).getCost()) return 0;
        return 1;
    }

    /**
	 * gets suggested spelling
	 * @return the actual text of the suggest spelling
	 */
	public String getWord() {
        return word;
    }

    /**
	 * A cost measures how close a match this word was to the original word
	 * @return 0 if an exact match. Higher numbers are worse matches.
	 * @see EditDistance 
	 */
	public int getCost() {
        return score;
    }

    /** 
	 * returns the suggested spelling
	 */
	public String toString() {
      return word;
    }
}

