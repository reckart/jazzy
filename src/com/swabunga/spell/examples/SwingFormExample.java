package com.swabunga.spell.examples;

import com.swabunga.spell.swing.JSpellApplet;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** This class shows an example of how to use the spell checking capability
 *  for a text area on a swing form.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public class SwingFormExample extends JFrame {

  public SwingFormExample() {
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosed(WindowEvent e) {
        System.exit(0);
      }
    });

    JSpellApplet spellapplet = new JSpellApplet();
    spellapplet.init();
    getContentPane().add(spellapplet);

  }


  public static void main(String[] args) {
    SwingFormExample ex = new SwingFormExample();
    System.out.println("Showing form");
    ex.setSize(400, 200);
    ex.setVisible(true);
  }
}
