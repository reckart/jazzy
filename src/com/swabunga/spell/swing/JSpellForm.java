package com.swabunga.spell.swing;

import com.swabunga.spell.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.io.File;
import java.awt.*;
import java.awt.event.*;

/** Implementation of a spell check form.
 *  <p>
 *  This is no where near complete. It isnt even functioning yet.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
class JSpellForm extends JPanel {
  /* GUI COMPONENTS*/
  private JButton ignoreBtn = new JButton("Ignore");
  private JButton ignoreAllBtn = new JButton("Ignore All");
  private JButton addBtn = new JButton("Add");
  private JButton changeBtn = new JButton("Change");
  private JButton changeAllBtn = new JButton("Change All");
  private JButton cancelBtn = new JButton("Cancel");
  private JList suggestList = new JList();
  private JTextArea checkText = new JTextArea();

  public JSpellForm() {
    initialiseGUI();
  }

  protected JPanel makeEastPanel() {
    JPanel jPanel1 = new JPanel();
    jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));
    jPanel1.add(ignoreBtn);
    jPanel1.add(ignoreAllBtn);
    jPanel1.add(addBtn);
    jPanel1.add(changeBtn);
    jPanel1.add(changeAllBtn);
    jPanel1.add(cancelBtn);
    return jPanel1;
  }

  protected JPanel makeCentrePanel() {
    JPanel jPanel2 = new JPanel();
    jPanel2.setLayout(new BoxLayout(jPanel2, BoxLayout.Y_AXIS));

    JLabel lbl1 = new JLabel("Not in dictionary:");
    jPanel2.add(lbl1);

    jPanel2.add(new JScrollPane(checkText));

    JLabel lbl2 = new JLabel("Suggestions:");
    jPanel2.add(lbl2);

    jPanel2.add(new JScrollPane(suggestList));
    return jPanel2;
  }

  protected void initialiseGUI() {
    setLayout(new BorderLayout());
    this.add(makeEastPanel(), BorderLayout.EAST);
    this.add(makeCentrePanel(), BorderLayout.CENTER);
  }

  /** Shows the form in modal mode and when the user selects a response the
   *  correct value in the event is set*/
  public void showModalForm(SpellCheckEvent event) {
    //JMH TBD
  }

  public static void main(String[] args) {
    try {
      JSpellForm pane = new JSpellForm();
      JFrame frm = new JFrame("Spelling");
      frm.getContentPane().add(pane);
      frm.setSize(300,300);
      frm.setVisible(true);
      frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
