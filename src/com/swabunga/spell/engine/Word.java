package com.swabunga.spell.engine;

//import java.util.Comparator;

/** The Word object holds both the string and the score.
 *  <p>This class is now immutable.
 *  </p>
 */
public class Word{ //implements Comparator {
    private String word;
    private int score;

	public Word() {
	}
	
    public Word(String word, int score) {
      this.word = word;
      this.score = score;
    }

    /** The comparator interface*/
    public int compare(Object o1, Object o2) {
        if (((Word) o1).getScore() < ((Word) o2).getScore()) return -1;
        if (((Word) o1).getScore() == ((Word) o2).getScore()) return 0;
        return 1;
    }

    /**
	 * @return the actual text of the word
	 */
	public String getWord() {
        return word;
    }

    /**
	 * @return the number that represents how good a match this word was, probably based on 
	 * EditDistantce. The lower the score, the better the match. A score of 0 is an exact match. 
	 */
	public int getScore() {
        return score;
    }

    public String toString() {
      return word;
    }
}

