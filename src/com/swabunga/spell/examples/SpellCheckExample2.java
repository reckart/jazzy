package com.swabunga.spell.examples;

import java.io.*;
import java.util.*;
import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;

/** This class shows an example of how to use the spell checking capability.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public class SpellCheckExample2 implements SpellCheckListener {

  private static String dictFile = "dict/english.0";
  private SpellChecker spellCheck = null;


  public SpellCheckExample2() {
    try {
      BufferedReader in = new BufferedReader(new FileReader("README.TXT"));
      SpellDictionary dictionary = new SpellDictionaryHashMap(new File(dictFile));
      spellCheck = new SpellChecker(dictionary);
      spellCheck.addSpellCheckListener(this);

      while (true) {
	String line = in.readLine();

	if (line.length() == -1)
	  break;

		spellCheck.checkSpelling( new StringWordTokenizer(line) );
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void spellingError(SpellCheckEvent event) {
    List suggestions = event.getSuggestions();
    for (Iterator suggestedWord=suggestions.iterator(); suggestedWord.hasNext();) {
      System.out.println("Suggested Word: ="+suggestedWord.next());
    }
    //Null actions
  }

  public static void main(String[] args) {
    new SpellCheckExample2();
  }
}
