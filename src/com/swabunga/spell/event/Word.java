package com.swabunga.spell.event;

public class Word {

  //~ Instance/static variables ...............................................

  private int end;
  private int start;
  private String text;

  //~ Constructors ............................................................

  /**
   * Creates a new Word object.
   * 
   * @param text the String representing the word.
   * @param start the start index of the word.
   */
  public Word(String text, int start) {
    this.text = text;
    this.start = start;
    setEnd();
  }

  /**
   * Creates a new Word object by cloning an existing Word object.
   * 
   * @param w the word object to clone.
   */
  public Word(Word w) {
    this.copy(w);
  }

  //~ Methods .................................................................

  /**
   * @return the end index of the word.
   */
  public int getEnd() {

    return end;
  }

  /**
   * Set the start index of the word.
   * 
   * @param s the start index.
   */
  public void setStart(int s) {
    start = s;
    setEnd();
  }

  /**
   * @return the start index.
   */
  public int getStart() {

    return start;
  }

  /**
   * Set the text to a new string value.
   * 
   * @param s the new text
   */
  public void setText(String s) {
    text = s;
    setEnd();
  }

  /**
   * @return the String representing the word.
   */
  public String getText() {

    return text;
  }

  /**
   * Sets the value of this Word to be a copy of another.
   * 
   * @param w the Word to copy.
   */
  public void copy(Word w) {
    text = w.toString();
    start = w.getStart();
    setEnd();
  }

  /**
   * @return the length of the word.
   */
  public int length() {

    return text.length();
  }

  /**
   * @return the text representing the word.
   */
  public String toString() {

    return text;
  }

  /**
   * Set the end index of the word.
   * 
   */
  private void setEnd() {
    end = start + text.length();
  }
}
