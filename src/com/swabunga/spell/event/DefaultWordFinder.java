package com.swabunga.spell.event;

import java.text.BreakIterator;


/**
 * A basic word finder, which searches text for sequences of letters.
 * 
 * @author Anthony Roy  (ajr@antroy.co.uk)
 */
public class DefaultWordFinder
  extends AbstractWordFinder {

  //~ Instance/static variables ...............................................

  private BreakIterator sentanceIterator;

  //~ Constructors ............................................................

  /**
   * Creates a new DefaultWordFinder object.
   * 
   * @param inText the String to search
   */
  public DefaultWordFinder(String inText) {
    super(inText);
  }

  //~ Methods .................................................................

  /**
   * Main method
   * 
   * @param args command line input.
   */
  public static void main(String[] args) {

    String test = "Testing \\item testing $one$  \\emph{two} $$three$$ \\begin{four} \\end{dffff} five.";
//    DefaultWordFinder dwf = new DefaultWordFinder(test);
//    XMLWordFinder dwf = new XMLWordFinder(test);
    TeXWordFinder dwf = new TeXWordFinder(test);

        int i = 1;
        while(dwf.hasNext()){
           Word w = dwf.next();
           System.out.println("Word " + i++ + ": " +w.toString());
        }
//    System.out.println(dwf.current().toString());
//    System.out.println("Start?" + dwf.startsSentence);
//    dwf.next();
//    dwf.next();
//    System.out.println(dwf.current().toString());
//    dwf.replace("BALONEY");
//    System.out.println(dwf.current().toString());
//    System.out.println(dwf.toString());
//    System.out.println(dwf.next());
//    System.out.println(dwf.next());
  }

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
    boolean finished = false;
    boolean started = false;

    while (i < text.length() && !finished) {

      char currentLetter = text.charAt(i);

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
