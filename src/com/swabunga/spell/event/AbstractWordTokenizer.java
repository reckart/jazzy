package com.swabunga.spell.event;

import java.text.BreakIterator;


/**
 * This class tokenizes a input string.
 * 
 * <p>
 * It also allows for the string to be mutated. The result after the spell
 * checking is completed is available to the call to getFinalText
 * </p>
 * 
 * @author Jason Height(jheight@chariot.net.au)
 * @author Anthony Roy  (ajr@antroy.co.uk)
 */
public abstract class AbstractWordTokenizer
  implements WordTokenizer {

  //~ Instance/static variables ...............................................

  protected Word currentWord;
  protected WordFinder finder;
  protected BreakIterator sentanceIterator;

  /** The cumulative word count that have been processed */
  protected int wordCount = 0;

  //~ Constructors ............................................................

  /**
   * Creates a new AbstractWordTokenizer object.
   * 
   * @param text ¤
   */
  public AbstractWordTokenizer(String text) {
    this(new DefaultWordFinder(text));
  }

  /**
   * Creates a new AbstractWordTokenizer object.
   * 
   * @param wf ¤
   */
  public AbstractWordTokenizer(WordFinder wf) {
    this.finder = wf;
  }

  //~ Methods .................................................................

  /**
   * Returns the current number of words that have been processed
   * @return ¤
   */
  public int getCurrentWordCount() {

    return wordCount;
  }

  /**
   * Returns the end of the current word in the text
   * @return ¤
   */
  public int getCurrentWordEnd() {

    return currentWord.getEnd();
  }

  /**
   * Returns the index of the start of the curent word in the text
   * @return ¤
   */
  public int getCurrentWordPosition() {

    return currentWord.getStart();
  }

  /**
   * Returns true if there are more words that can be processed in the string
   * @return ¤
   */
  public boolean hasMoreWords() {

    return finder.hasNext();
  }

  /**
   * Returns the next word in the text
   * @return ¤
   */
  public String nextWord() {

    String out = null;

    if (finder.hasNext()) {
      currentWord = finder.next();
      wordCount++;
      out = currentWord.getText();
    }

    return out;
  }

  /**
   * Replaces the current word token
   * @param newWord ¤
   */
  public abstract void replaceWord(String newWord);

  /**
   * Returns the current text that is being tokenized (includes any changes
   * that have been made)
   * @return ¤
   */
  public String getContext() {

    return finder.toString();
  }

  /**
   * returns true if the current word is at the start of a sentance
   * @return ¤
   */
  public boolean isNewSentance() {

    return finder.startsSentence();
  }
}