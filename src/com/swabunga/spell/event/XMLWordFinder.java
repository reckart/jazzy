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
   * @throws WordNotFoundException search string contains no more words.
   */
  public Word next() {

    if (currentWord == null)
      throw new WordNotFoundException("No more words found.");

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

    int i = currentWord.getEnd();
    boolean finished = false;
    boolean started = false;
    boolean ignore = false;
  
	search:
    while (i < text.length() && !finished) {

      char currentLetter = text.charAt(i);
//{{{ Find words.
      if (!started && isWordChar(currentLetter)) {
        nextWord.setStart(i++);
        started = true;
        continue search;
      } else if (started) {
          if (isWordChar(currentLetter)){
						i++;
						continue search;
          }else {
						nextWord.setText(text.substring(nextWord.getStart(), i));
						finished = true;
						break search;
					}
      }  //}}}

      //Ignore things inside tags.
			i = ignore(i,'<','>');
			
      i++;
    }

    if (!started) {
      nextWord = null;
    }
		else if (!finished){
        nextWord.setText(text.substring(nextWord.getStart(), i));			
		}

    return currentWord;
  }

  /**
   * Replace the current word in the search with a replacement string.
   * 
   * @param newWord the replacement string.
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
