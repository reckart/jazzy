package com.swabunga.test.spell.event;

import junit.framework.*;
import junit.textui.*;
import com.swabunga.spell.event.*;
import java.io.File;

public class FileWordTokenizerTester extends TestCase {

  FileWordTokenizer texTok;
  
  public FileWordTokenizerTester(String name){
    super(name);
  }
  
  protected void setUp(){
    texTok = new FileWordTokenizer(new File("src/com/swabunga/test/spell/event/test.tex"), new TeXWordFinder());
  }
  
  protected void tearDown(){
    texTok = null;
  }

  public void testRead(){
    assertTrue(!texTok.getContext().equals(""));
  }
  
  public void testTeXWordA(){
    assertEquals("width", texTok.nextWord());
    assertEquals("1", texTok.nextWord());
    assertEquals("1", texTok.nextWord());
    assertEquals("1cm", texTok.nextWord());
    assertEquals("1", texTok.nextWord());
    assertEquals("Key", texTok.nextWord());
    assertEquals("Words", texTok.nextWord());
  }
  
  public static void main(String[] args){
    //System.out.println("No tests currently written for FileWordTokenizerTester.");
    TestRunner.run(new TestSuite(FileWordTokenizerTester.class));
  }

  
}
