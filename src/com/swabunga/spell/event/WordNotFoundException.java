package com.swabunga.spell.event;

public class WordNotFoundException
  extends RuntimeException {

  //~ Constructors ............................................................

  /**
   * Creates a new WordNotFoundException object.
   */
  public WordNotFoundException() {
    super();
  }

  /**
   * Creates a new WordNotFoundException object.
   * 
   * @param s a message.
   */
  public WordNotFoundException(String s) {
    super(s);
  }
}
