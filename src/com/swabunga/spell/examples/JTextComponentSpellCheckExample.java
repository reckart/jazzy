/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package com.swabunga.spell.examples;

import com.swabunga.spell.engine.GenericSpellDictionary;
import com.swabunga.spell.engine.SpellDictionary;
import com.swabunga.spell.swing.JTextComponentSpellChecker;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;


/** This class shows an example of how to use the spell checking capability
 *  on a JTextComponent.
 *
 * @author Robert Gustavsson (robert@lindesign.se)
 */
public class JTextComponentSpellCheckExample extends JFrame {
  private static final String dictionaryFile = "english.0";
  private static final String phoneticFile = "phonet.en";
  protected SpellDictionary dictionary;
  JTextComponent text = null;
  JButton spell = null;

  public JTextComponentSpellCheckExample() {
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    addWindowListener(new WindowAdapter() {

      public void windowClosed(WindowEvent e) {
        System.exit(0);
      }
    });
    try {
      dictionary = new GenericSpellDictionary(new File("dict/" + dictionaryFile), new File("dict/" + phoneticFile));
      //dictionary = new GenericSpellDictionary(new File("dict/"+dictionaryFile));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    initGUI();
    pack();
  }

  private void initGUI() {
    Container frame = getContentPane();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    frame.setLayout(gridbag);
    c.anchor = GridBagConstraints.CENTER;
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(5, 5, 5, 5);
    c.weightx = 1.0;
    c.weighty = 1.0;
    text = new JTextArea(10, 40);
    addToFrame(frame, text, gridbag, c, 0, 0, 1, 1);
    spell = new JButton("spell");
    spell.addActionListener(new ButtonListener());
    addToFrame(frame, spell, gridbag, c, 0, 1, 1, 1);
  }

  // Helps build gridbaglayout.
  private void addToFrame(Container f, Component c, GridBagLayout gbl, GridBagConstraints gbc, int x, int y, int w, int h) {
    gbc.gridx = x;
    gbc.gridy = y;
    gbc.gridwidth = w;
    gbc.gridheight = h;
    gbl.setConstraints(c, gbc);
    f.add(c);
  }

  public static void main(String[] args) {
    JTextComponentSpellCheckExample d = new JTextComponentSpellCheckExample();
    d.show();
  }

  // INNER CLASSES
  private class ButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      Thread t = new SpellThread();
      t.start();
    }
  }

  private class SpellThread extends Thread {

    public void run() {
      try {
        JTextComponentSpellChecker sc = new JTextComponentSpellChecker(dictionary);
        sc.spellCheck(text);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}



