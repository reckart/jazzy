package com.swabunga.spell.swing;

import javax.swing.text.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;

/** This class spellchecks a JTextComponent throwing up a Dialog everytime
 *  it encounters a misspelled word.
 *
 * @author Robert Gustavsson (robert@lindesign.se)
 */

public class JTextComponentSpellChecker implements SpellCheckListener {
    private JTextComponent textComp=null;
    private SpellChecker spellCheck = null;
    private JSpellDialog dlg=null;

    public JTextComponentSpellChecker(JTextComponent textComp, String dictFile)
                                                        throws IOException{
        this(textComp, dictFile, null);
    }

    public JTextComponentSpellChecker(JTextComponent textComp, String dictFile, String title)
                                                        throws IOException{
        this(textComp,new SpellDictionary(new File(dictFile)),title);
    }

    public JTextComponentSpellChecker(JTextComponent textComp, SpellDictionary dict){
        this(textComp, dict, null);
    }

    public JTextComponentSpellChecker(JTextComponent textComp, SpellDictionary dict, String title){
        this.textComp=textComp;
        spellCheck = new SpellChecker(dict);
        spellCheck.addSpellCheckListener(this);
        setupDialog(textComp, title);
    }

    private void setupDialog(JTextComponent textComp, String title){

        Component comp=SwingUtilities.getRoot(textComp);
        if (comp!=null && comp instanceof Window) {
            if(comp instanceof Frame)
                dlg = new JSpellDialog((Frame)comp, title, true);
            if(comp instanceof Dialog)
                dlg = new JSpellDialog((Dialog)comp, title, true);
            // Put the dialog in the middle of it's parent.
            if(dlg!=null){
                Window win=(Window)comp;
                int x=(int)(win.getLocation().getX()+win.getWidth()/2-dlg.getWidth()/2);
                int y=(int)(win.getLocation().getY()+win.getHeight()/2-dlg.getHeight()/2);
                dlg.setLocation(x,y);
            }

        } else {
            dlg = new JSpellDialog((Frame)null, title, true);
        }
    }

    public void spellCheck(){
        DocumentWordTokenizer tokenizer = new DocumentWordTokenizer(textComp.getDocument());
        spellCheck.checkSpelling(tokenizer);
//        dlg.dispose();
//        dlg=null;
    }

    public void spellingError(SpellCheckEvent event) {

        java.util.List suggestions = event.getSuggestions();
        int start=event.getWordContextPosition();
        int end=start+event.getInvalidWord().length();

        // Mark the invalid word in TextComponent
        textComp.requestFocus();
        textComp.setCaretPosition(0);
        textComp.setCaretPosition(start);
        textComp.moveCaretPosition(end);

        dlg.show(event);
    }
}
