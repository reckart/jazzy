package com.swabunga.spell.swing;

import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.event.DocumentWordTokenizer;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.SpellChecker;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/** This class spellchecks a JTextComponent throwing up a Dialog everytime
 *  it encounters a misspelled word.
 *
 * @author Robert Gustavsson (robert@lindesign.se)
 */

public class JTextComponentSpellChecker implements SpellCheckListener {

//    private static final String COMPLETED="COMPLETED";
  private String dialogTitle = null;

  private SpellChecker spellCheck = null;
  private JSpellDialog dlg = null;
  private JTextComponent textComp = null;
  private ResourceBundle messages;

  // Constructor
  public JTextComponentSpellChecker(SpellDictionary dict) {
    this(dict, null);
  }

  // Convinient Constructors, for those lazy guys.
  public JTextComponentSpellChecker(String dictFile) throws IOException {
    this(dictFile, null);
  }

  public JTextComponentSpellChecker(String dictFile, String title) throws IOException {
    this(new SpellDictionaryHashMap(new File(dictFile)), title);
  }

  public JTextComponentSpellChecker(String dictFile, String phoneticFile, String title) throws IOException {
    this(new SpellDictionaryHashMap(new File(dictFile), new File(phoneticFile)), title);
  }

  public JTextComponentSpellChecker(SpellDictionary dict, String title) {
    spellCheck = new SpellChecker(dict);
    spellCheck.addSpellCheckListener(this);
    dialogTitle = title;
    messages = ResourceBundle.getBundle("com.swabunga.spell.swing.messages", Locale.getDefault());
  }

  // MEMBER METHODS
  
  /**
   * Set user dictionary (used when a word is added)
   */
  public void setUserDictionary(SpellDictionary dictionary) {
    if (spellCheck != null)
      spellCheck.setUserDictionary(dictionary);
  }

  private void setupDialog(JTextComponent textComp) {

    Component comp = SwingUtilities.getRoot(textComp);

    // Probably the most common situation efter the first time.
    if (dlg != null && dlg.getOwner() == comp)
      return;

    if (comp != null && comp instanceof Window) {
      if (comp instanceof Frame)
        dlg = new JSpellDialog((Frame) comp, dialogTitle, true);
      if (comp instanceof Dialog)
        dlg = new JSpellDialog((Dialog) comp, dialogTitle, true);
      // Put the dialog in the middle of it's parent.
      if (dlg != null) {
        Window win = (Window) comp;
        int x = (int) (win.getLocation().getX() + win.getWidth() / 2 - dlg.getWidth() / 2);
        int y = (int) (win.getLocation().getY() + win.getHeight() / 2 - dlg.getHeight() / 2);
        dlg.setLocation(x, y);
      }
    } else {
      dlg = new JSpellDialog((Frame) null, dialogTitle, true);
    }
  }

  public synchronized int spellCheck(JTextComponent textComp) {
    setupDialog(textComp);
    this.textComp = textComp;

    DocumentWordTokenizer tokenizer = new DocumentWordTokenizer(textComp.getDocument());
    int exitStatus = spellCheck.checkSpelling(tokenizer);

    textComp.requestFocus();
    textComp.setCaretPosition(0);
    this.textComp = null;
    return exitStatus;
  }

  public void spellingError(SpellCheckEvent event) {

//        java.util.List suggestions = event.getSuggestions();
    event.getSuggestions();
    int start = event.getWordContextPosition();
    int end = start + event.getInvalidWord().length();

    // Mark the invalid word in TextComponent
    textComp.requestFocus();
    textComp.setCaretPosition(0);
    textComp.setCaretPosition(start);
    textComp.moveCaretPosition(end);

    dlg.show(event);
  }
}
