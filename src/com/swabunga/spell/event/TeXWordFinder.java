//{{{ package and imports
//:folding=explicit:
package com.swabunga.spell.event;

import java.text.BreakIterator;


/**
 * A word finder for TeX and LaTeX documents, which searches text for
 * sequences of letters, but ignores any  commands and environments as well
 * as  Math environments.
 * 
 * @author Anthony Roy  (ajr@antroy.co.uk)
 */
  //}}}
public class TeXWordFinder
  extends AbstractWordFinder {

//{{{ ~ Instance/static variables ...............................................

  private boolean IGNORE_COMMENTS = true;
 //}}}
//{{{ ~ Constructors ............................................................

  /**
   * Creates a new DefaultWordFinder object.
   * 
   * @param inText the text to search.
   */
  public TeXWordFinder(String inText) {
    super(inText);
  }
//}}}
//{{{ ~ Methods .................................................................

  /**
   * This method scans the text from the end of the last word,  and returns a
   * new Word object corresponding to the next word.
   * 
   * @return the next word.
   * @throws WordNotFoundException search string contains no more words.
   */
  public Word next() {
//{{{ 

    if (currentWord == null)
      throw new WordNotFoundException("No more words found.");

      currentWord.copy(nextWord);
      setSentenceIterator(currentWord);


    int i = currentWord.getEnd();
    boolean finished = false;
    boolean started = false;

  search: 
    while (i < text.length() && !finished) {

//{{{ Find words.
      if (!started && isWordChar(i)) {
        nextWord.setStart(i++);
        started = true;
        continue search;
      } else if (started) {
          if (isWordChar(i)){
						i++;
						continue search;
          }else {
						nextWord.setText(text.substring(nextWord.getStart(), i));
						finished = true;
						break search;
					}
      }  //}}}

// Ignore Comments: 
	i = ignore(i,'%','\n');
// Ignore Maths:
	i = ignore(i,"$$","$$");
	i = ignore(i,'$','$');
// Ignore environment names.
	i = ignore(i,"\\begin{","}");
	i = ignore(i,"\\end{","}");
// Ignore commands.
	i = ignore(i,'\\');
	
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
 //}}}
 
  public void setIgnoreComments(boolean ignore){
//{{{ 
        IGNORE_COMMENTS = ignore;
  }
 //}}}  
//}}}
}
