/*
 * JazzyOptionPane.java 
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
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

//{{{ Imports
package com.swabunga.spell.jedit;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;


//}}}
public class JazzyOptionPane
  extends AbstractOptionPane {

  //~ Instance/static variables ...............................................

  private JTextField dictDir;
  private JCheckBox loadOnStart,
                    resetDict,
		    defaultChecker;

  //~ Constructors ............................................................

  /**
   * Creates a new JazzyOptionPane object.
   */
  public JazzyOptionPane() {
    super("jazzy");
  }

  //~ Methods .................................................................

  protected void _init() {

    JTextArea jta = new JTextArea();
    jta.setEditable(false);
    jta.setLineWrap(true);
    jta.setWrapStyleWord(true);
    jta.setText(
          "Changing these properties will not have an effect until jEdit is restarted.");
    dictDir = new JTextField(jEdit.getProperty("options.jazzy.dictionary"),20);

    JLabel userDirLab = new JLabel("Dictionary Location (restart jEdit to enable)");
    JPanel p = new JPanel();
    p.add(userDirLab);
    p.add(dictDir);
    addComponent(p);
    addComponent(loadOnStart = new JCheckBox("Load Dictionary on Start?"));
    loadOnStart.getModel().setSelected(jEdit.getBooleanProperty(
                                             "options.jazzy.load-dictionary"));
    addComponent(resetDict = new JCheckBox("Reset Ignored Words after each spellcheck?"));
    resetDict.getModel().setSelected(jEdit.getBooleanProperty(
                                             "options.jazzy.reset-spellchecker"));
    addComponent(defaultChecker = new JCheckBox("Disable mode-specific checking?"));
    defaultChecker.getModel().setSelected(jEdit.getBooleanProperty(
                                             "options.jazzy.default-checker"));
  }


  protected void _save() {
    jEdit.setBooleanProperty("options.jazzy.load-dictionary", 
                             loadOnStart.getModel().isSelected());
    jEdit.setBooleanProperty("options.jazzy.reset-spellchecker", 
                             resetDict.getModel().isSelected());
    jEdit.setBooleanProperty("options.jazzy.default-checker", 
                             defaultChecker.getModel().isSelected());
    jEdit.setProperty("options.jazzy.dictionary", dictDir.getText());
  }
}