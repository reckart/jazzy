package com.swabunga.spell.event;

import java.text.BreakIterator;


/**
 * A word finder for TeX and LaTeX documents, which searches text for
 * sequences of letters, but ignores any  commands and environments as well
 * as  Math environments.
 * 
 * @author Anthony Roy  (ajr@antroy.co.uk)
 */
public class TeXWordFinder
  extends AbstractWordFinder {

  //~ Instance/static variables ...............................................

  private BreakIterator sentanceIterator;

  //~ Constructors ............................................................

  /**
   * Creates a new DefaultWordFinder object.
   * 
   * @param inText the text to search.
   */
  public TeXWordFinder(String inText) {
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

    String endIgnore = "";

    search: 
     while (i < text.length() && !finished) {

      char currentLetter = text.charAt(i);
         //Ignore Math environments.
//        if (text.substring(i,i+2).equals("$$")) {
//          i++;
//          while (true){
//            i++;
//            if (text.substring(i,i+2).equals("$$")){
//              i = i+2;
//              continue search;
//            }
//          }
//        }else 

//NOTE: Following code will not exit if a $ is missing!
        if (currentLetter=='$') {
          while (true){
            if (text.charAt(++i)=='$') {
              i++;
              continue search;
            }
          }
        }
     
      if (currentLetter == '\\'){
        //Ignore environment names.
        if (text.substring(i+1,i+6).equals("begin")||
            text.substring(i+1,i+4).equals("end")) {
          i = i+5;
          while (true){
            currentLetter = text.charAt(++i);
            if (currentLetter == '}') continue search;
          }
        }
        //Ignore commands.
        else{
          do {currentLetter = text.charAt(++i);}
          while (Character.isLetterOrDigit(currentLetter));

        }
      }
    
      //Find words.
      if (!started && Character.isLetter(currentLetter)) {
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
