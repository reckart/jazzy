package com.swabunga.spell.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import com.swabunga.spell.engine.SpellDictionary;

/**
 * @author aim4min
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class JSpellApplet extends JApplet {

	private static final String dictionaryFile = "dictionaries/english.0";
	private SpellDictionary dictionary;
	JTextComponent text = null;
	JButton spell = null;

	/**
	 * @see java.awt.Component#paint(Graphics)
	 */
	public void paint(Graphics arg0) {
		super.paint(arg0);

	}

	/**
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		super.init();
		InputStream fInput = getClass().getResourceAsStream("../dictionaries/english.0");
		if (fInput !=null) {
			try {
				dictionary = new SpellDictionary(new InputStreamReader(fInput));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			initGUI();
		}
		

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
		spell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread() {
					public void run() {
						try {
							JTextComponentSpellChecker sc =
								new JTextComponentSpellChecker(dictionary);
							sc.spellCheck(text);
						} catch (Exception ex) {
							ex.printStackTrace();
							
						}
					}
				};
				t.start();
			}
		});
		addToFrame(frame, spell, gridbag, c, 0, 1, 1, 1);
	}

	// Helps build gridbaglayout.
	private void addToFrame(
		Container f,
		Component c,
		GridBagLayout gbl,
		GridBagConstraints gbc,
		int x,
		int y,
		int w,
		int h) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbl.setConstraints(c, gbc);
		f.add(c);
	}
}
