package com.swabunga.spell.event;

public interface WordFinder {

  //~ Methods .................................................................

  /**
   * This method returns the text through which the WordFinder is iterating.
   * The text may have been modified through calls to replace().
   * 
   * @return the (possibly modified) text being searched.
   */
  public String getText();

  /**
   * This method should return the  Word object representing the current word
   * in the iteration (the first word if next() has not yet been called.)
   * This method should not affect the state of the  WordFinder object.
   * 
   * @return the current Word object.
   */
  public Word current();

  /**
   * Tests the finder to see if any more words are available.
   * 
   * @return true if more words are available.
   */
  public boolean hasNext();

  /**
   * This method should return the  Word object representing the current word
   * in the iteration (the first word if next() has not yet been called.)
   * This method should move the iteration on to the next  word.
   * 
   * @return true if more words are present.
   */
  public Word next();

  /**
   * This method should replace the current Word object with a Word object
   * representing the String newWord.
   * 
   * @param newWord the word to replace the current word with.
   */
  public void replace(String newWord);

  /**
   * @return true if the current word starts a new sentence.
   */
  public boolean startsSentence();

  // public void setText();
}
