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

    String test = "A $gg ";//<B> Testing \\item<testing>and one  \\emph{two} three \\begin{four}\n \\end{dffff} five. the \\end{test}";
 //     String test = "test test\n * comm @tag comm \n test test\n*comm comm\ntest test";

    System.out.println(test);
//         DefaultWordFinder dwf = new DefaultWordFinder(test);
 //      XMLWordFinder dwf = new XMLWordFinder(test);
       TeXWordFinder dwf = new TeXWordFinder(test);
 //        JavaWordFinder dwf = new JavaWordFinder(test);

        int i = 1;
            while(dwf.hasNext()){
    //    for (; i < 5; i++) {
    
          Word w = dwf.next();
          System.out.println("Word " + i++ + ": " + w.toString());
        }
//            System.out.println(dwf.toString());
//            System.out.println("C: " + dwf.current());
//            System.out.println("N: " + dwf.next());
//            System.out.println("C: " + dwf.current());
//            dwf.replace("R");
//            System.out.println(dwf.toString());
//            System.out.println("C: " + dwf.current());
//            System.out.println("N: " + dwf.next());
//            System.out.println("C: " + dwf.current());
//            System.out.println("N: " + dwf.next());
//            System.out.println("C: " + dwf.current());
//            System.out.println("N: " + dwf.next());
    
    //        System.out.println("Start?" + dwf.startsSentence);
    //        dwf.next();
    //        dwf.next();
    //        System.out.println(dwf.current().toString());
    //        dwf.replace("BALONEY");
    //        System.out.println(dwf.current().toString());
    //        System.out.println(dwf.toString());
    //        System.out.println(dwf.next());
  }

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

      currentWord.copy(nextWord);
      setSentenceIterator(currentWord);

    int i = currentWord.getEnd();
    boolean finished = false;
    boolean started = false;

	search:
    while (i < text.length() && !finished) {

      if (!started && isWordChar(i)) {
        nextWord.setStart(i);
        started = true;
      }
			else if (started && !isWordChar(i)) {
        nextWord.setText(text.substring(nextWord.getStart(), i));
        finished = true;
				break search;
      }
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

}
