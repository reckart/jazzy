package com.swabunga.spell.event;

import java.util.*;
import java.text.*;

/** This class tokenizes a input string.
 *  <p>
 *  It also allows for the string to be mutated. The result after the spell
 *  checking is completed is available to the call to getFinalText</p>
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public class StringWordTokenizer implements WordTokenizer {
  /** Holds the start character position of the current word*/
  private int currentWordPos = 0;
  /** Holds the end character position of the current word*/
  private int currentWordEnd = 0;
  /** Holds the start character position of the next word*/
  private int nextWordPos = -1;
  /** The actual text that is being tokenized*/
  private StringBuffer text;
  /** The cumulative word count that have been processed*/
  private int wordCount = 0;
  /** Flag indicating if there are any more tokens (words) left*/
  private boolean moreTokens = true;
  /** Is this a special case where the currentWordStart, currntWordEnd and
   *  nextWordPos have already been calculated. (see nextWord)
   */
  private boolean first = true;

  private BreakIterator sentanceIterator;
  private boolean startsSentance = true;


  public StringWordTokenizer(String text) {
    sentanceIterator = BreakIterator.getSentenceInstance();
    sentanceIterator.setText(text);
    sentanceIterator.first();
    //Wrap a string buffer to hopefully make things a bit easier and efficient to
    //replace words etc.
    this.text = new StringBuffer(text);
    currentWordPos = getNextWordStart(this.text, 0);
    //If the current word pos is -1 then the string was all white space
    if (currentWordPos != -1) {
      currentWordEnd = getNextWordEnd(this.text, currentWordPos);
      nextWordPos = getNextWordStart(this.text, currentWordEnd);
    } else {
      moreTokens = false;
    }
  }

  /** This helper method will return the start character of the next
   * word in the buffer from the start position
   */
  private static int getNextWordStart(StringBuffer text, int startPos) {
    int size = text.length();
    for (int i=startPos;i<size;i++) {
      if (Character.isLetterOrDigit(text.charAt(i))) {
        return i;
      }
    }
    return -1;
  }

  /** This helper method will return the end of the next word in the buffer.
   *
   */
  private static int getNextWordEnd(StringBuffer text, int startPos) {
    int size = text.length();
    for (int i=startPos;i<size;i++) {
      if (!Character.isLetterOrDigit(text.charAt(i))) {
        return i;
      }
    }
    return size;
  }


  /** Returns true if there are more words that can be processed in the string
   *
   */
  public boolean hasMoreWords() {
    return moreTokens;
  }

  /** Returns the current character position in the text
   *
   */
  public int getCurrentWordPosition() {
    return currentWordPos;
  }

  /** Returns the current end word position in the text
   *
   */
  public int getCurrentWordEnd() {
    return currentWordEnd;
  }

  /** Returns the next word in the text
   *
   */
  public String nextWord() {
    if (!first) {
      currentWordPos = nextWordPos;
      currentWordEnd = getNextWordEnd(text, currentWordPos);
      nextWordPos = getNextWordStart(text, currentWordEnd+1);
      int current = sentanceIterator.current();
      if (current == currentWordPos)
        startsSentance = true;
      else {
        startsSentance = false;
        if (currentWordEnd > current)
          sentanceIterator.next();
      }
    }
    //The nextWordPos has already been populated
    String word = text.substring(currentWordPos, currentWordEnd);
    wordCount++;
    first = false;
    if (nextWordPos == -1)
      moreTokens = false;
    return word;
  }

  /** Returns the current number of words that have been processed
   *
   */
  public int getCurrentWordCount() {
    return wordCount;
  }

  /** Replaces the current word token*/
  public void replaceWord(String newWord) {
    if (currentWordPos != -1) {
      text.replace(currentWordPos, currentWordEnd, newWord);
      //Position after the newly replaced word(s)
      first = true;
      currentWordPos = getNextWordStart(text, currentWordPos+newWord.length());
      if (currentWordPos != -1) {
        currentWordEnd = getNextWordEnd(text, currentWordPos);
        nextWordPos = getNextWordStart(text, currentWordEnd);
        sentanceIterator.setText(text.toString());
        sentanceIterator.following(currentWordPos);
      } else moreTokens = false;
    }
  }

  /** returns true iif the current word is at the start of a sentance*/
  public boolean isNewSentance() {
    return startsSentance;
  }

  /** Returns the current text that is being tokenized (includes any changes
   *  that have been made)
   */
  public String getContext() {
    return text.toString();
  }

  /** This method can be used to return the final text after the schecking is complete.*/
  public String getFinalText() {
    return getContext();
  }


  public static void main(String args[]) {
    StringWordTokenizer t = new StringWordTokenizer("  This is a  test   problem");
    while(t.hasMoreWords()) {
      String word = t.nextWord();
      System.out.println("Word is '"+word+"'");
      if ("test".equals(word)) t.replaceWord("mightly big");
    }
    System.out.println("End text is: '"+t.getFinalText()+"'");

    t = new StringWordTokenizer("    README   ");
    while(t.hasMoreWords()) {
      String word = t.nextWord();
      System.out.println("Word is '"+word+"'");
    }
    System.out.println("End text is: '"+t.getFinalText()+"'");

    t = new StringWordTokenizer("This is a acronym (A.C.M.E). This is the second sentance.");
    while(t.hasMoreWords()) {
      String word = t.nextWord();
      System.out.println("Word is '"+word+"'. Starts Sentance?="+t.isNewSentance());
      if (word.equals("acronym"))
        t.replaceWord("test");
    }
  }
}