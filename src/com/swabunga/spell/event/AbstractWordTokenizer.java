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
  protected BreakIterator sentenceIterator;

  /** The cumulative word count that have been processed */
  protected int wordCount = 0;

  //~ Constructors ............................................................

  /**
   * Creates a new AbstractWordTokenizer object.
   * 
   * @param text the text to process.
   */
  public AbstractWordTokenizer(String text) {
    this(new DefaultWordFinder(text));
  }

  /**
   * Creates a new AbstractWordTokenizer object.
   * 
   * @param wf the custom WordFinder to use in searching for words.
   */
  public AbstractWordTokenizer(WordFinder wf) {
    this.finder = wf;
  }

  //~ Methods .................................................................

  /**
   * Returns the current number of words that have been processed
   * 
   * @return number of words so far iterated.
   */
  public int getCurrentWordCount() {

    return wordCount;
  }

  /**
   * Returns the end of the current word in the text
   * 
   * @return index in string of the end of the current word.
   * @throws WordNotFoundException current word has not yet been set.
   */
  public int getCurrentWordEnd() {

    if (currentWord == null) {
      throw new WordNotFoundException("No Words in current String");
    }

    return currentWord.getEnd();
  }

  /**
   * Returns the index of the start of the curent word in the text
   * 
   * @return index in string of the start of the current word.
   * @throws WordNotFoundException current word has not yet been set.
   */
  public int getCurrentWordPosition() {

    if (currentWord == null) {
      throw new WordNotFoundException("No Words in current String");
    }

    return currentWord.getStart();
  }

  /**
   * Returns true if there are more words that can be processed in the string
   * 
   * @return true if there are further words in the text.
   */
  public boolean hasMoreWords() {

    return finder.hasNext();
  }

  /**
   * Returns searches for the next word in the text, and returns that word.
   * 
   * @return the string representing the current word.
   * @throws WordNotFoundException search string contains no more words.
   */
  public String nextWord() {
    currentWord = finder.next();

    return currentWord.getText();
  }

  /**
   * Replaces the current word token
   * 
   * @param newWord replacement word.
   * @throws WordNotFoundException current word has not yet been set.
   */
  public abstract void replaceWord(String newWord);

  /**
   * Returns the current text that is being tokenized (includes any changes
   * that have been made)
   * 
   * @return the text being tokenized.
   */
  public String getContext() {

    return finder.toString();
  }

  /**
   * returns true if the current word is at the start of a sentence
   * 
   * @return true if the current word starts a sentence.
   * @throws WordNotFoundException current word has not yet been set.
   */
  public boolean isNewSentence() {

    return finder.startsSentence();
  }
}