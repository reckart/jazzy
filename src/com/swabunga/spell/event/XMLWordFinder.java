package com.swabunga.spell.event;

import java.text.BreakIterator;


/**
 * A word finder for XML or HTML documents, which searches text for sequences
 * of letters, but ignores the text inside any tags.
 * 
 * @author Anthony Roy  (ajr@antroy.co.uk)
 */
public class XMLWordFinder
  extends AbstractWordFinder {

  //~ Instance/static variables ...............................................

  private BreakIterator sentanceIterator;

  //~ Constructors ............................................................

  /**
   * Creates a new DefaultWordFinder object.
   * 
   * @param inText the text to search.
   */
  public XMLWordFinder(String inText) {
    super(inText);
  }

  //~ Methods .................................................................

  /**
   * This method scans the text from the end of the last word,  and returns a
   * new Word object corresponding to the next word.
   * 
   * @return the next word.
   */
  public Word next() {

    Word tempWord = new Word(currentWord);

    if (nextWord != null) {
      currentWord.copy(nextWord);

      int current = sentanceIterator.current();

      if (current == currentWord.getStart())
        startsSentence = true;
      else {
        startsSentence = false;

        if (currentWord.getEnd() > current) {
          sentanceIterator.next();
        }
      }
    } else {
      currentWord = null;

      return tempWord;
    }

    int i = currentWord.getEnd();
    boolean finished = false,
            started  = false,
            ignore   = false;

    while (i < text.length() && !finished) {

      char currentLetter = text.charAt(i);

      //Ignore things inside tags.
      if (currentLetter == '<') {
        ignore = true;
      } else if (currentLetter == '>') {
        ignore = false;
      }
      
      //Find words.
      if (!ignore && !started && Character.isLetter(currentLetter)) {
        nextWord.setStart(i);
        started = true;
      } else if (started && !Character.isLetter(currentLetter)) {
        nextWord.setText(text.substring(nextWord.getStart(), i));
        finished = true;
      }

      i++;
    }

    if (!started) {
      nextWord = null;
    }

    return tempWord;
  }

  /**
   * ¤
   * 
   * @param newWord ¤
   */
  public void replace(String newWord) {
    super.replace(newWord);
    sentanceIterator.setText(text);

    int start = currentWord.getStart();
    sentanceIterator.following(start);
    startsSentence = sentanceIterator.current() == start;
  }

  protected void init() {
    sentanceIterator = BreakIterator.getSentenceInstance();
    sentanceIterator.setText(text);
  }
}
