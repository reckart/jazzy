package com.swabunga.spell.swing;

import com.swabunga.spell.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.File;
import java.awt.*;
import java.awt.event.*;

/** Implementation of a spell check dialog.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public class JSpellDialog extends JDialog implements ActionListener {
  private JSpellForm form = new JSpellForm();

  public JSpellDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    initialiseDialog();
  }

  public JSpellDialog(Dialog owner, String title, boolean modal) {
    super(owner, title, modal);
    initialiseDialog();
  }

  private void initialiseDialog() {
    getContentPane().add(form);
    form.addActionListener(this);
    //setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    pack();
  }


  public void show(SpellCheckEvent e) {
    System.out.println("Show");
    form.setSpellEvent(e);
    show();
  }

  public void actionPerformed(ActionEvent e) {
    hide();
  }
}
