package com.swabunga.spell.event;

import java.util.*;

/** This class tokenizes a input string
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public class WordTokenizer {
  private int currentPos = 0;
  private int nextWordPos = -1;
  private char[] text;

  public WordTokenizer(String text) {
    this.text = text.toCharArray();
    currentPos = getNextWordStart(this.text, 0);
    nextWordPos = getNextWordEnd(this.text, currentPos);
  }

  private static int getNextWordStart(char[] text, int startPos) {
    int size = text.length;
    for (int i=startPos;i<size;i++) {
      if (Character.isLetterOrDigit(text[i])) {
        return i;
      }
    }
    return -1;
  }

  private static int getNextWordEnd(char[] text, int startPos) {
    int size = text.length;
    for (int i=startPos;i<size;i++) {
      if (!Character.isLetterOrDigit(text[i])) {
        return i;
      }
    }
    return size;
  }


  public boolean hasMoreWords() {
    return (nextWordPos != -1);
  }

  public int getCurrentPosition() {
    return currentPos;
  }

  public String nextWord() {
    //The nextWordPos has already been populated
    String word = new String(text, currentPos, (nextWordPos-currentPos));

    currentPos = getNextWordStart(text, nextWordPos+1);
    if (currentPos != -1)
      nextWordPos = getNextWordEnd(text, currentPos);
    else nextWordPos = -1;
    return word;
  }

  public static void main(String args[]) {
    WordTokenizer t = new WordTokenizer("  This is a test  problem");
    while(t.hasMoreWords()) {
      System.out.println("Word is '"+t.nextWord()+"'");
    }
  }

}