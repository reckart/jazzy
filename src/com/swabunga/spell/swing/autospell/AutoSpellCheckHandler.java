/*
 * Created on 2004-apr-26
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.swabunga.spell.swing.autospell;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;

/**
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 * 
 * @author rogus
 *
 */
public class AutoSpellCheckHandler extends MouseAdapter implements DocumentListener, 
																   AutoSpellConstants{

	private SpellChecker			sCheck=null;
	private Configuration 			config = Configuration.getConfiguration();
	private ResourceBundle			messages=null;
	
	public AutoSpellCheckHandler(SpellChecker sc){
		sCheck=sc;
	}
	
	public AutoSpellCheckHandler(SpellChecker sc, ResourceBundle rs){
		this(sc);
		messages=rs;
	}
	
	public void addJEditorPane(JEditorPane pane){
		StyledDocument doc=(StyledDocument)pane.getDocument();
		markupSpelling(doc, 0, doc.getLength()-1);
		doc.addDocumentListener(this);
		pane.addMouseListener(this);
	}
	
	public void removeJEditorPane(JEditorPane pane){
		Document doc=pane.getDocument();
		pane.removeMouseListener(this);
		doc.removeDocumentListener(this);
	}
	
	private void markupSpelling(StyledDocument doc, int start, int end){
		int						wordStart=-1,
								wordEnd=-1;
		String					word;
		DocumentWordTokenizer	docTok;
		Segment					seg=new Segment();
		
		docTok=new DocumentWordTokenizer(doc);
		
		if(start>0){
			docTok.posStartFullWordFrom(start);
		}
		
		while(docTok.hasMoreWords() && docTok.getCurrentWordPosition()<=end){
			word=docTok.nextWord();
			
			wordStart=docTok.getCurrentWordPosition();
			if(wordEnd!=-1)
				markAsCorrect(doc, wordEnd, wordStart);
			wordEnd=docTok.getCurrentWordEnd();
			
			if(wordEnd>=doc.getLength())
				wordEnd=doc.getLength()-1;
			if(wordStart>=wordEnd)
				continue;
			if(sCheck.isCorrect(word) || sCheck.isIgnored(word)){
				markAsCorrect(doc, wordStart, wordEnd);
			}else{
				markAsMisspelled(doc, wordStart, wordEnd);
			}
		}
		if(wordEnd!=-1){
			markAsCorrect(doc, wordEnd, end);
		}
	}
	
	private void markAsMisspelled(StyledDocument doc, int start, int end){
		SimpleAttributeSet attr;
		attr=new SimpleAttributeSet();
		attr.addAttribute(wordMisspelled, wordMisspelledTrue);
		doc.setCharacterAttributes(start, end-start, attr, false);
	}
	
	private void markAsCorrect(StyledDocument doc, int start, int end){
		SimpleAttributeSet attr;
		attr=new SimpleAttributeSet(doc.getCharacterElement((start+end)/2).getAttributes());
		attr.removeAttribute(wordMisspelled);
		doc.setCharacterAttributes(start, end-start, attr, true);
	}
	
	private void handleDocumentChange(DocumentEvent evt){
		StyledDocument			doc;
		
		if(evt.getDocument() instanceof StyledDocument){
			doc=(StyledDocument)evt.getDocument();
			markupSpelling(doc,evt.getOffset()-1, evt.getOffset()+evt.getLength());
		}		
	}
	
	private void showSuggestionPopup(JEditorPane pane, Point p){
		StyledDocument 			doc;
		JMenuItem 				item;
		AttributeSet			attr;
		int 					pos = pane.viewToModel(p);
		DocumentWordTokenizer	docTok;
		String					word;
		java.util.List			suggestions;
		JPopupMenu 				popup;
		ReplaceListener			repList;
		
		if (pos >= 0) {
			doc=(StyledDocument)pane.getDocument();
			attr=doc.getCharacterElement(pos).getAttributes();
			if(attr.containsAttribute(wordMisspelled, wordMisspelledTrue)){
				docTok=new DocumentWordTokenizer(doc);
				docTok.posStartFullWordFrom(pos);
				word=docTok.nextWord();
				suggestions=sCheck.getSuggestions(word, config.getInteger(Configuration.SPELL_THRESHOLD));
				
				popup=new JPopupMenu();
				repList=new ReplaceListener(docTok);
				for(int i=0;i<suggestions.size();i++) {
					com.swabunga.spell.engine.Word w = (com.swabunga.spell.engine.Word) suggestions.get(i);
					item = new JMenuItem(w.getWord());
					item.setActionCommand(w.getWord());
					item.addActionListener(repList);
					popup.add(item);
				}
				popup.addSeparator();
				item = new JMenuItem();
				if(messages!=null)
					item.setText(messages.getString("IGNOREALL"));
				else
					item.setText("Ignore All");
				item.setActionCommand(word);
				item.addActionListener(new IgnoreAllListener(doc));
				popup.add(item);
				item = new JMenuItem();
				if(messages!=null)
					item.setText(messages.getString("ADD"));
				else
					item.setText("Add word to wordlist");
				item.setActionCommand(word);
				item.addActionListener(new AddToDictListener(doc));
				popup.add(item);
				popup.show(pane, p.x, p.y);	
			}
		}
	}
	
	// DocumentListener implementation
	// ------------------------------------------------------------------
	public void changedUpdate(DocumentEvent evt){
	}
	
	public void insertUpdate(DocumentEvent evt){
		Runnable r=new SpellCheckChange(evt);
		SwingUtilities.invokeLater(r);
	}
	
	public void removeUpdate(DocumentEvent evt){
		Runnable r=new SpellCheckChange(evt);
		SwingUtilities.invokeLater(r);
	}
	
	// MouseListener implementation
	// ------------------------------------------------------------------
	public void mouseReleased(MouseEvent evt){
		JEditorPane pane;
		if(!(evt.getComponent() instanceof JEditorPane))
			return;
		
		if(evt.isPopupTrigger()){
			pane=(JEditorPane)evt.getComponent();
			if(pane.isEditable())
				showSuggestionPopup(pane, new Point(evt.getX(), evt.getY()));
		}
	}
	
	// INNER CLASSES
	// ------------------------------------------------------------------
	private class SpellCheckChange implements Runnable{
	
		private DocumentEvent evt;
		
		public SpellCheckChange(DocumentEvent evt){
			this.evt=evt;
		}
		
		public void run(){
			handleDocumentChange(evt);
		}
		
	}
	
	private class ReplaceListener implements ActionListener{
		
		DocumentWordTokenizer	tok;
		
		public ReplaceListener(DocumentWordTokenizer tok){
			this.tok=tok;
		}
		
		public void actionPerformed(ActionEvent evt){
			tok.replaceWord(evt.getActionCommand());
		}
	}
	
	private class AddToDictListener implements ActionListener{
		
		private StyledDocument	doc;
		
		public AddToDictListener(StyledDocument doc){
			this.doc=doc;
		}
		
		public void actionPerformed(ActionEvent evt){
			sCheck.addToDictionary(evt.getActionCommand());
			Runnable r=new MarkUpSpellingAll(doc);
			SwingUtilities.invokeLater(r);
		}
	}
	
	private class IgnoreAllListener implements ActionListener{
		
		private StyledDocument	doc;
		
		public IgnoreAllListener(StyledDocument doc){
			this.doc=doc;
		}
		
		public void actionPerformed(ActionEvent evt){
			sCheck.ignoreAll(evt.getActionCommand());
			Runnable r=new MarkUpSpellingAll(doc);
			SwingUtilities.invokeLater(r);
		}
	}
	
	private class MarkUpSpellingAll implements Runnable{
		
		private StyledDocument doc;
		
		public MarkUpSpellingAll(StyledDocument doc){
			this.doc=doc;
		}
		
		public void run(){
			markupSpelling(doc,0,doc.getLength());
		}
	}
	
}