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
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;

//}}}
public class JazzyOptionPane
    extends AbstractOptionPane {

    //~ Instance/static variables .............................................

    private JCheckBox defaultChecker;
    protected JTextField dictDir;
    protected JCheckBox disk_based;
    private JCheckBox loadOnStart;
    private JCheckBox resetDict;

    //~ Constructors ..........................................................

    /**
     * Creates a new JazzyOptionPane object.
     */
    public JazzyOptionPane() {
        super("jazzy");
    }

    //~ Methods ...............................................................

    protected void _init() {
        JTextArea jta = new JTextArea();
        jta.setEditable(false);
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        jta.setText(
                "Changing these properties will not have an effect until jEdit is restarted.");
        dictDir = new JTextField(jEdit.getProperty("options.jazzy.dictionary"), 
                                 20);
        JLabel userDirLab = new JLabel("Dictionary Location (restart jEdit to enable)");
        JPanel p          = new JPanel();
        p.add(userDirLab);
        p.add(dictDir);
	/* 4 Feb '03: Added filechooser to options. */
	p.add(new JButton(new AbstractAction("Browse..."){
		public void actionPerformed(ActionEvent e){
			int mode = (disk_based.getModel().isSelected() 
				? VFSBrowser.CHOOSE_DIRECTORY_DIALOG
				: VFSBrowser.OPEN_DIALOG);
			String[] files = GUIUtilities.showVFSFileDialog(jEdit.getActiveView(),dictDir.getText(),mode,false);
			dictDir.setText((files[0]!=null) ? files[0] : "");
		}
	}));
        addComponent(p);
        addComponent(loadOnStart = new JCheckBox("Load Dictionary on Start?"));
        loadOnStart.getModel().setSelected(jEdit.getBooleanProperty(
                                                   "options.jazzy.load-dictionary"));
        addComponent(
                resetDict = new JCheckBox(
                                    "Reset Ignored Words after each spellcheck?"));
        resetDict.getModel().setSelected(jEdit.getBooleanProperty(
                                                 "options.jazzy.reset-spellchecker"));
        addComponent(
                defaultChecker = new JCheckBox(
                                         "Disable mode-specific checking?"));
        defaultChecker.getModel().setSelected(jEdit.getBooleanProperty(
                                                      "options.jazzy.default-checker"));
        addComponent(disk_based = new JCheckBox("Use disk based dictionary?"));
        disk_based.getModel().setSelected(jEdit.getBooleanProperty(
                                                  "options.jazzy.disk-based"));
    }

    protected void _save() {
        jEdit.setBooleanProperty("options.jazzy.load-dictionary", 
                                 loadOnStart.getModel().isSelected());
        jEdit.setBooleanProperty("options.jazzy.reset-spellchecker", 
                                 resetDict.getModel().isSelected());
        jEdit.setBooleanProperty("options.jazzy.default-checker", 
                                 defaultChecker.getModel().isSelected());
        jEdit.setBooleanProperty("options.jazzy.disk-based", 
                                 disk_based.getModel().isSelected());
        jEdit.setProperty("options.jazzy.dictionary", 
                          dictDir.getText());
    }
}