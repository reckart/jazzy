package com.swabunga.spell.event;

import java.util.*;
import java.io.*;

/** This class tokenizes a input file.
 *  <p>
 *  Any takers to do this efficiently?? Doesnt need to replace any words to start with
 *  . I need this to get an idea of how quick the spell checker is.
 */
public class FileWordTokenizer implements WordTokenizer {

  public FileWordTokenizer(File inputFile) {
  }

  public boolean hasMoreWords() {
    return false;
  }

  public int getCurrentWordPosition() {
    return 0;
  }

  /** Returns the current end word position in the text
   *
   */
  public int getCurrentWordEnd() {
    return 0;
  }

  public String nextWord() {
    return null;
  }

  public int getCurrentWordCount() {
    return 0;
  }

  /** Replaces the current word token*/
  public void replaceWord(String newWord) {
  }

  /** Returns the current text that is being tokenized (includes any changes
   *  that have been made)
   */
  public String getContext() {
    return null;
  }

  /** returns true iif the current word is at the start of a sentance*/
  public boolean isNewSentance() {
    return false;
  }
}