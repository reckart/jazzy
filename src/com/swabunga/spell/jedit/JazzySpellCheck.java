/*
 * $Date: 2003/01/31 07:45:59 $
 * $Author: bgalbs $
 *
 * Copyright (C) 2002 Anthony Roy
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.swabunga.spell.jedit;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.*;
import com.swabunga.spell.event.StringWordTokenizer;
import com.swabunga.spell.swing.JSpellDialog;

import java.io.*;
import java.net.*;

import org.gjt.sp.jedit.*;
import org.gjt.sp.util.*;
import org.gjt.sp.jedit.textarea.Selection;
import org.gjt.sp.jedit.textarea.JEditTextArea;

public class JazzySpellCheck
  implements SpellCheckListener {

  //~ Instance/static variables ...............................................

  public static final int LOAD_DICTIONARY = 1;
  public static final int RESET_SPELLCHECKER = 2;
  private SpellDictionary dictionary;
  private File dictionaryFile;
  private JSpellDialog dlg;
  private SpellChecker spellChecker;
  private JEditTextArea area;
  private int flags,
	            offset,
              caretPosn;
  private boolean LOADED = false,
                  noerrors = true;

  //~ Constructors ............................................................

  /**
   * Creates a new JazzySpellCheck object.
   * 
   * @param dict ¤
   * @param load ¤
   */
  public JazzySpellCheck(String dict, int flags) {
    this.dictionaryFile = new File(dict);
    this.flags = flags;
    if ((flags & LOAD_DICTIONARY)==LOAD_DICTIONARY){
      loadDictionary();
    }

    setupDialog();
  }

  //~ Methods .................................................................

  /**
   * ¤
   * 
   * @return ¤ 
   */
  public boolean isLoaded() {

    return LOADED;
  }

  /**
   * ¤
   * 
   * @param input ¤
   * @return ¤ 
   */
  public String checkText(String input, String mode, int offset, int caret) {

			if (!LOADED){
				return null;
			}

			this.offset = offset;
		  this.caretPosn = caret;
      WordFinder wf;
      View view = jEdit.getActiveView();
      area = view.getTextArea();
      boolean defaultChecker = jEdit.getBooleanProperty("options.jazzy.default-checker", false);
      
      if (!defaultChecker){
        if (mode.equals("java")) {wf = new JavaWordFinder(input);}
        else if (mode.equals("tex")) {wf = new TeXWordFinder(input);}
        else if ((mode.equals("html"))||(mode.equals("xml"))) {wf = new XMLWordFinder(input);}
        else  {wf = new DefaultWordFinder(input);}
      }
      else{
        wf = new DefaultWordFinder(input);
      }
      
    StringWordTokenizer toks = new StringWordTokenizer(wf);
    
    spellChecker.checkSpelling(toks);

    if (noerrors) {
      Macros.message(view,"No Spelling Errors Found");
    }else{
      noerrors = true;
    }
    
    area.setCaretPosition(caretPosn);
    
    String output = toks.getFinalText();
    if ((flags & RESET_SPELLCHECKER)==RESET_SPELLCHECKER) spellChecker.reset();

    return output;
  }

  /**
   * ¤
   * 
   * @return ¤ 
   */
  public boolean loadDictionary() {

    if (!LOADED && dictionaryFile.exists()) {

      try {
        dictionary = new SpellDictionaryHashMap(dictionaryFile);
      } catch (Exception e) {
        Log.log(Log.MESSAGE,this,"TextSpellCheck: error loading dictionary: " + e);
        LOADED = false;

        return LOADED;
      }

      LOADED = true;
    }
    else{
      try{
        InputStream in = this.getClass().getResourceAsStream("/english.0");
        InputStreamReader reader = new InputStreamReader(in);
        dictionary = new SpellDictionaryHashMap(reader);
      }catch(Exception e) {
        Log.log(Log.MESSAGE,this,"TextSpellCheck: error loading default dictionary: " + e);
        LOADED = false;

        return LOADED;
      }
      LOADED = true;
 //     Log.log(Log.MESSAGE,this,"File: " + b);
    }

    if (LOADED){
      spellChecker = new SpellChecker(dictionary);
      spellChecker.addSpellCheckListener(this);
    }

    return LOADED;
  }

  /**
   * ¤
   * 
   * @param event ¤
   */
  public void spellingError(SpellCheckEvent event) {
		noerrors = false;
    int oldLength = event.getInvalidWord().length();
		int start=event.getWordContextPosition() + offset;
    int end=start+oldLength;

		Selection s = new Selection.Range(start, end);
		area.setCaretPosition(start);
		area.scrollToCaret(true);
		area.setSelection(s);
		
    dlg.show(event);
    
    String replace = event.getReplaceWord();
    
    if (replace!=null){
      area.setSelectedText(replace);
      if (caretPosn > start){
        int diff = replace.length() - oldLength;
        caretPosn += diff;
      }
    }
    
  }

  /**
   * ¤
   */
  public void unloadDictionary() {
    if (!LOADED) return;
    
    spellChecker.removeSpellCheckListener(this);
    spellChecker = null;
    dictionary = null;
    LOADED = false;
    System.gc();
  }

  private void setupDialog() {
    dlg = new JSpellDialog(jEdit.getActiveView(), "Spell", true);
  }
}
