package com.swabunga.spell.examples;

import java.io.*;
import java.util.*;
import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;

/** This class shows an example of how to use the spell checking capability.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public class SpellCheckExample implements SpellCheckListener {

  private static String dictFile = "dict/english.0";
  private SpellChecker spellCheck = null;


  public SpellCheckExample() {
    try {
      SpellDictionary dictionary = new SpellDictionaryHashMap(new File(dictFile));

      spellCheck = new SpellChecker(dictionary);
      spellCheck.addSpellCheckListener(this);
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      while (true) {
	System.out.print("Enter text to spell check: ");
	String line = in.readLine();

	if (line.length() <=0)
	  break;
		spellCheck.checkSpelling( new StringWordTokenizer(line) );
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void spellingError(SpellCheckEvent event) {
    List suggestions = event.getSuggestions();
    if (suggestions.size() > 0) {
      System.out.println("MISSPELT WORD: "+event.getInvalidWord());
      for (Iterator suggestedWord=suggestions.iterator(); suggestedWord.hasNext();) {
        System.out.println("Suggested Word: ="+suggestedWord.next());
      }
    }
    //Null actions
  }

  public static void main(String[] args) {
    new SpellCheckExample();
  }
}
