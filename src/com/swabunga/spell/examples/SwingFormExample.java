package com.swabunga.spell.examples;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;
import com.swabunga.spell.swing.*;

/** This class shows an example of how to use the spell checking capability
 *  for a text area on a swing form.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public class SwingFormExample extends JFrame implements SpellCheckListener, ActionListener {
  private static int threshold = 200;
  private static String dictFile = "dict/english.0";
  private SpellChecker spellCheck = null;
  private JTextArea textArea;
  private JSpellDialog dlg = new JSpellDialog(this, "Check spelling", true);

  public SwingFormExample() {
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosed(WindowEvent e) {
        System.exit(0);
      }
    });
    try {
      SpellDictionary dictionary = new SpellDictionary(new File(dictFile));

      spellCheck = new SpellChecker(dictionary);
      spellCheck.addSpellCheckListener(this);

      getContentPane().setLayout(new FlowLayout());
      textArea = new JTextArea("invalad invilid Spell Check example", 5, 20);
      getContentPane().add(textArea);
      JButton button = new JButton("Check");
      button.addActionListener(this);
      getContentPane().add(button);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void actionPerformed(ActionEvent e) {
    spellCheck.checkSpelling(new DocumentWordTokenizer(textArea.getDocument()));
    //StringWordTokenizer tokenizer = new StringWordTokenizer(textArea.getText());
    //spellCheck.checkSpelling(tokenizer);
    //textArea.setText(tokenizer.getFinalText());
  }

  public void spellingError(SpellCheckEvent event) {
    System.out.println("SpellingError fired");
    System.out.println("Event Dump. Word = "+event.getInvalidWord());
    dlg.show(event);
  }

  public static void main(String[] args) {
    SwingFormExample ex = new SwingFormExample();
    System.out.println("Showing form");
    ex.setSize(400,200);
    ex.setVisible(true);
  }
}
