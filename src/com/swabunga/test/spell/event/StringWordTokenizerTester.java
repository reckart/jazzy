package com.swabunga.test.spell.event;

import junit.framework.*;
import junit.textui.*;
import com.swabunga.spell.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class StringWordTokenizerTester extends TestCase {

  StringWordTokenizer texTok;
  
  public StringWordTokenizerTester(String name){
    super(name);
  }
  
  protected void setUp(){
    texTok = new StringWordTokenizer(
        stringValue(new File("src/com/swabunga/test/spell/event/test.tex")), 
        new TeXWordFinder()
    );
  }
  
  protected void tearDown(){
    texTok = null;
  }

  public void testRead(){
    assertTrue(!texTok.getContext().equals(""));
  }
  
  public void testWordA(){
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
    TestRunner.run(new TestSuite(StringWordTokenizerTester.class));
  }

  private static String stringValue(File inFile) {
    File stringFile = inFile;
    StringBuffer out = new StringBuffer("");

    try{
      BufferedReader in = new BufferedReader(new FileReader(inFile));
      char[] c = new char[100];
      int count;
      while ((count = in.read(c, 0, c.length)) != -1){
         out.append(c,0,count);
      }
      in.close();
    } catch(IOException e){
      System.err.println("File input error trying to open " + inFile.toString() + " : " + e);
    }
    return out.toString();
  }

}
