package com.swabunga.spell.event;

import java.text.BreakIterator;


/**
 * A basic word finder, which searches text for sequences of letters.
 * 
 * @author Anthony Roy  (ajr¤antroy.co.uk)
 */
public class JavaWordFinder
  extends AbstractWordFinder {

  //~ Instance/static variables ...............................................

	private BreakIterator sentenceIterator;
	private boolean inComment;

  //~ Constructors ............................................................

  /**
   * Creates a new DefaultWordFinder object.
   * 
   * @param inText the String to search
   */
  public JavaWordFinder(String inText) {
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

    if (nextWord == null) {
      throw new WordNotFoundException("No more words found.");
    }

//    Word tempWord = new Word(currentWord);

//    if (nextWord != null) {
      currentWord.copy(nextWord);

      int current = sentenceIterator.current();

      if (current == currentWord.getStart())
        startsSentence = true;
      else {
        startsSentence = false;

        if (currentWord.getEnd() > current) {
          sentenceIterator.next();
        }
      }
//    } else {
//      currentWord = null;
//
//      return tempWord;
//    }

    int i = currentWord.getEnd();
    boolean finished = false;
    boolean started = false;

   search:
		while (i < text.length() && !finished) {

			i = ignore(i,'@');
			i = ignore(i,"<code>","</code>");
			i = ignore(i,"<CODE>","</CODE>");
			i = ignore(i,'<','>');
			
			if (i >= text.length()) break search;
      
      char currentLetter = text.charAt(i);
			/* Changed isWordChar() method in following block to use new improved position based version (11 Feb '03) */
			if (inComment){
				//Reset on new line.
				if (currentLetter == '\n'){
					inComment = false;
					i++;
				continue search;
			  }
				else if (!isWordChar(i)){
					i++;
					continue search;
				}
				//Find words.
				while (i < text.length()-1) {
					if (!started && isWordChar(i)) {
						nextWord.setStart(i);
						started = true;
					} else if (started && !isWordChar(i)) {
						nextWord.setText(text.substring(nextWord.getStart(), i));
						finished = true;
						break search;
					}
					
					currentLetter =  text.charAt(++i);
				}
			}
			else if (currentLetter == '*'){
				inComment = true;
				i++;
			}
			else {
				i++;
			}
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
    sentenceIterator.setText(text);

    int start = currentWord.getStart();
    sentenceIterator.following(start);
    startsSentence = sentenceIterator.current() == start;
  }

  protected void init() {
    sentenceIterator = BreakIterator.getSentenceInstance();
    sentenceIterator.setText(text);
		inComment = false;
  }
}
