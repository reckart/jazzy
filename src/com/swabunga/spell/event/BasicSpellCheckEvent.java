package com.swabunga.spell.event;

import java.util.*;

/** This event is fired off by the SpellChecker and is passed to the
 *  registered SpellCheckListeners
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
class BasicSpellCheckEvent implements SpellCheckEvent {
  /**The list holding the suggested Word objects for the misspelt word*/
  private List suggestions;
  /**The misspelt word*/
  private String invalidWord;
  /**The action to be done when the event returns*/
  private short action = INITIAL;
  /**Contains the word to be replaced if the action is REPLACE or REPLACEALL*/
  private String replaceWord = null;

  private String context;
  private int startPosition;


  /**Consructs the SpellCheckEvent
   * @param String invalidWord The word that is misspelt
   * @param List suggestions A list of Word objects that are suggested to replace the currently mispelt word
   * @param WordTokenizer tokenizer The reference to the tokenizer that caused this
   * event to fire.
   */
  public BasicSpellCheckEvent(String invalidWord, List suggestions, WordTokenizer tokenizer) {
    this.invalidWord = invalidWord;
    this.suggestions = suggestions;
    this.context = tokenizer.getContext();
    this.startPosition = tokenizer.getCurrentWordPosition();
  }

  /** Returns the list of suggested Word objects*/
  public List getSuggestions() {
    return suggestions;
  }

  /** Returns the currently misspelt word*/
  public String getInvalidWord() {
    return invalidWord;
  }

  public String getWordContext() {
    //JMH TBD
    return null;
  }

  /** Returns the start position of the misspelt word in the context*/
  public int getWordContextPosition() {
    return startPosition;
  }

  public short getAction() {
    return action;
  }

  public String getReplaceWord() {
    return replaceWord;
  }

  /** Set the action to replace the currently misspelt word with the new word
   *  @param String newWord The word to replace the currently misspelt word
   *  @param boolean replaceAll If set to true, the SpellChecker will replace all
   *  further occurances of the misspelt word without firing a SpellCheckEvent.
   */
  public void replaceWord(String newWord, boolean replaceAll) {
    if (action != INITIAL)
      throw new IllegalStateException("The action can can only be set once");
    if (replaceAll)
      action = REPLACEALL;
    else action = REPLACE;
    replaceWord = newWord;
  }

  /** Set the action it ignore the currently misspelt word.
   *  @param boolean ignoreAll If set to true, the SpellChecker will replace all
   *  further occurances of the misspelt word without firing a SpellCheckEvent.
   */
  public void ignoreWord(boolean ignoreAll) {
    if (action != INITIAL)
      throw new IllegalStateException("The action can can only be set once");
    if (ignoreAll)
      action = IGNOREALL;
    else action = IGNORE;
  }

  /** Set the action to add a new word into the dictionary. This will also replace the
   *  currently misspelt word.
   */
  public void addToDictionary(String newWord) {
    if (action != INITIAL)
      throw new IllegalStateException("The action can can only be set once");
    action = ADDTODICT;
    replaceWord = newWord;
  }

  public void cancel() {
    if (action != INITIAL)
      throw new IllegalStateException("The action can can only be set once");
    action = CANCEL;
  }
}