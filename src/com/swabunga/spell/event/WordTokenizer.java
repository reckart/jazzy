package com.swabunga.spell.event;

/** This interface returns words.
 *  It also allows for the current word to be mutated
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public interface WordTokenizer {
  /** Returns true iif there are more words left*/
  public boolean hasMoreWords();
  /**Returns an index representing the start location in the original set of words*/
  public int getCurrentWordPosition();
  /**Returns an index representing the end location in the original set of words*/
  public int getCurrentWordEnd();

  /** Returns the next word token*/
  public String nextWord();
  /** Returns the number of word tokens that have been processed thus far*/
  public int getCurrentWordCount();
  /** Replaces the current word token
   * <p>When a word is replaced care should be taken that the WordTokenizer
   * repositions itself such that the words that were added arent rechecked. Of
   * course this is not mandatory, maybe there is a case when an application
   * doesnt need to do this.</p>
   */
  public void replaceWord(String newWord);
  /** Returns the context text that is being tokenized (should include any changes
   *  that have been made)
   */
  public String getContext();
  /** Returns true iif the current word is at the start of a sentance*/
  public boolean isNewSentance();
}