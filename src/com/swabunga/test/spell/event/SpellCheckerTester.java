package com.swabunga.test.spell.event;

import junit.framework.*;
import junit.textui.*;
import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;
import java.io.*;
import java.util.*;

public class SpellCheckerTester extends TestCase implements SpellCheckListener {

  SpellChecker checker;
  ArrayList misspelled;
  
  public SpellCheckerTester(String name){
    super(name);
  }
  
  protected void setUp(){
    File dict = new File("dict/english.0");
    try{
      checker = new SpellChecker(new SpellDictionaryHashMap(dict));
    }catch (FileNotFoundException e){
      System.err.println("Dictionary File " + dict + " not found! " + e);
    }catch (IOException ex){
      System.err.println("IO problem: " + ex);
    }
    FileWordTokenizer texTok = new FileWordTokenizer(new File("src/com/swabunga/test/spell/event/test.tex"),
                                   new TeXWordFinder());
    misspelled = new ArrayList();
    
    checker.addSpellCheckListener(this);
    checker.checkSpelling(texTok);
  }
  
  protected void tearDown(){
    checker = null;
  }

  public void testTeXNext(){
    
    Iterator it = misspelled.iterator();
    assertEquals("Anthony", it.next());
    assertEquals("Stell", it.next());
    assertEquals("Leeds", it.next());
    assertEquals("heere", it.next());
    assertEquals("Coinvexity", it.next());
    assertEquals("serction", it.next());
    assertEquals("conccepts", it.next());
  }
  
  public void spellingError(SpellCheckEvent event){
    event.ignoreWord(true);
    misspelled.add(event.getInvalidWord());
  }
  
  public static void main(String[] args){
    //System.out.println("No tests currently written for FileWordTokenizerTester.");
    TestRunner.run(new TestSuite(SpellCheckerTester.class));
  }

  
  
}
