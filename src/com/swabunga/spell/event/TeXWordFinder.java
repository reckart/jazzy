//{{{ package and imports
//:folding=explicit:
package com.swabunga.spell.event;

import java.util.Collection;
import java.util.HashSet;

/**
 * A word finder for TeX and LaTeX documents, which searches text for
 * sequences of letters, but ignores any  commands and environments as well
 * as  Math environments.
 *
 * @author Anthony Roy  (ajr@antroy.co.uk)
 */

//}}}

public class TeXWordFinder extends AbstractWordFinder {

//{{{ ~ Instance/static variables ...............................................

  private boolean IGNORE_COMMENTS = true;
  private HashSet user_defined_ignores = new HashSet();
  private int regex_user_defined_ignores = STRING_EXPR;
  public static final int STRING_EXPR = 0;
  public static final int REG_EXPR = 1;
//  public static final int GLOB_EXPR = 2;
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
  
  public TeXWordFinder() {
    super();
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

    if (!hasNext())//currentWord == null)
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
          if (isWordChar(i)) {
            i++;
            continue search;
          } else {
            nextWord.setText(text.substring(nextWord.getStart(), i));
            finished = true;
            break search;
          }
        }  //}}}
// Ignores should be in order of importance and then specificity.
        int j = i;
// Ignore Comments:
        j = ignore(j, '%', '\n');
        
// Ignore Maths:
        j = ignore(j, "$$", "$$");
        j = ignore(j, '$', '$');
        
// Ignore user defined.
        j = ignoreUserDefined(j);
        
// Ignore certain command parameters.
        j = ignore(j, "\\newcommand", "}");
        j = ignore(j, "\\documentclass", "}");
        j = ignore(j, "\\usepackage", "}");
        j = ignore(j, "\\newcounter{", "}");
        j = ignore(j, "\\setcounter{", "}");
        j = ignore(j, "\\addtocounter{", "}");
        j = ignore(j, "\\value{", "}");
        j = ignore(j, "\\arabic{", "}");
        j = ignore(j, "\\usecounter{", "}");
        j = ignore(j, "\\newenvironment", "}");
        j = ignore(j, "\\setlength", "}");
        j = ignore(j, "\\setkeys", "}");
        
// Ignore environment names.
        j = ignore(j, "\\begin{", "}");
        j = ignore(j, "\\end{", "}");        
        if (i != j){
          i = j;
          continue search;
        }
        
// Ignore commands.
        j = ignore(j, '\\');
        
        if (i != j){
          i = j;
          continue search;
        }
        i++;
      }

    if (!started) {
      nextWord = null;
    } else if (!finished) {
      nextWord.setText(text.substring(nextWord.getStart(), i));
    }

    return currentWord;
  }
//}}}
  /**
   * This method is used to import a user defined set of either strings or regular expressions to ignore.
   * @param expressions a collection of of Objects whose toString() value should be the expression. Typically String objects.
   * @param regex is an integer specifying the type of expression to use. e.g. REG_EXPR, STRING_EXPR.
   */
  public void addUserDefinedIgnores(Collection expressions, int regex){
    user_defined_ignores.addAll(expressions);
    regex_user_defined_ignores = regex;
  }

  private int ignoreUserDefined(int i){
    return i;
  }
  
  public void setIgnoreComments(boolean ignore) {
    IGNORE_COMMENTS = ignore;
  }
//}}}
}
