package com.swabunga.spell.event;

/**
 * Defines common methods and behaviour for the various word finding subclasses.
 * 
 * @author Anthony Roy  (ajr@antroy.co.uk)
 */

public abstract class AbstractWordFinder
  implements WordFinder {

  //~ Instance/static variables ...............................................

  protected Word currentWord = new Word("", 0);
  protected Word nextWord = new Word("", 0);
  protected boolean startsSentence = true;
  protected String text;

  //~ Constructors ............................................................

  /**
   * Creates a new AbstractWordFinder object.
   * 
   * @param inText the String to iterate through.
   */
  public AbstractWordFinder(String inText) {
    text = inText;
    init();
    next();
    next();
  }

  //~ Methods .................................................................

  /**
   * This method scans the text from the end of the last word,  and returns a
   * new Word object corresponding to the next word.
   * 
   * @return the next word.
   */
  public abstract Word next();

  /**
   * Return the text being searched. May have changed since first set through
   * calls to replace.
   * 
   * @return the text being searched.
   */
  public String getText() {

    return text;
  }

  /**
   * Returns the current word in the iteration - the first word  if next() has
   * not been called.
   * 
   * @return the current word.
   */
  public Word current() {

    return currentWord;
  }

  /**
   * @return true if there are further words in the string.
   */
  public boolean hasNext() {

    return currentWord != null;
  }

  /**
   * Replace the current word in the search with a replacement string.
   * 
   * @param newWord the replacement string.
   */
  public void replace(String newWord) {

    int newEnd = currentWord.getEnd() + 
                 (newWord.length() - currentWord.length());
    StringBuffer sb = new StringBuffer(text.substring(0, 
                                                      currentWord.getStart()));
    sb.append(newWord);
    sb.append(text.substring(currentWord.getEnd()));
    currentWord.setText(newWord);
    text = sb.toString();
  }

  /**
   * @return true if the current word starts a new sentence.
   */
  public boolean startsSentence() {

    return startsSentence;
  }

  /**
   * Return the text being searched. May have changed since first set through
   * calls to replace.
   * 
   * @return the text being searched.
   */
  public String toString() {

    return text;
  }

  protected void init() {
  }
}
