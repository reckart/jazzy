
package com.swabunga.test.spell.event;

import junit.framework.*;
import junit.textui.*;
import com.swabunga.spell.event.*;


public class TeXWordFinderTester extends TestCase {

  public TeXWordFinderTester(String name){
    super(name);
  }
  
  protected void setUp(){
    
  }
  
  protected void tearDown(){
    
  }
  
  public void testWordFindA(){
    assertFound("string of words","string");
  }
  
  public void testWordFindB(){
    assertFound("\\string of words","of");
  }
  
  public void testWordFindC(){
    assertFound("\\begin{testing}\nthe text \n\\end{testing}","the");
  }
  
  public void testWordFindD(){
    assertFound("%testing\ntime","time");
  }
  
  public void testWordFindE(){
    assertFound("$testing$time","time");
  }
  
  public void testWordFindF(){
   assertFound("$$testing$$time","time");
  }
  
  public void testUnaryWordFindA(){
    assertFound("a test of words","a");
  }
  
  public void testUnaryWordFindB(){
    assertFound("\\string a test","a");
  }
  
  public void testUnaryWordFindC(){
    assertFound("\\begin{testing}\na test \n\\end{testing}","a");
  }
  
  public void testUnaryWordFindD(){
    assertFound("\\begin{testing}a\n\\end{testing}","a");
  }
  
  public void testUnaryWordFindE(){
    assertFound("%testing\na time","a");
  }
  
  public void testUnaryWordFindF(){
    assertFound("$testing$a time","a");
  }
  
  public void testUnaryWordFindG(){
    assertFound("$$testing$$a time","a");
  }
  
  public void testUnaryWordFindH(){
    assertFound("\\newcommand{not these}\\newcommand{not these}a time","a");
  }

  public void testNoWordsFoundA(){
    assertNotFound("");
  }
  
  public void testNoWordsFoundB(){
    assertNotFound("\\string  \\of\\words");
  }
  
  public void testNoWordsFoundC(){
    assertNotFound("\\begin{testing}\n\\end{testing}");
  }
  
  public void testNoWordsFoundD(){
    assertNotFound("%testing time");    
  }
  
  public void testNoWordsFoundE(){
    assertNotFound("$testing time$");    
  }
  
  public void testNoWordsFoundF(){
    assertNotFound("$$testing time$$");    
  }
  
  public void testFindAllWordsA(){
    String[] expected = {"This","is","a","simple","test","string"};
    assertAllWordsFound("This is a simple test string.", expected);
  }
  
  public void testFindAllWordsB(){
    String[] expected = {"is","a","test"};
    assertAllWordsFound("\\This is a \\simple test \\string.", expected);
  }
  
  public void testFindAllWordsC(){
    String[] expected = {"This","is","a","simple","test","string"};
    assertAllWordsFound("This is a simple test string.", expected);
  }
  
  public void testFindAllWordsD(){
    String[] expected = {"This","is","a","simple","test","string"};
    assertAllWordsFound("This is a simple test string.", expected);
  }
  
  private void assertAllWordsFound(String text, String[] expected){
    TeXWordFinder finder = new TeXWordFinder(text);
    Word found;
    int wordCount = 0;
    try{
      for (int i = 0; i < expected.length; i++){
        found = finder.next();
        wordCount++;
        assertEquals(expected[i],found.getText());
      }
      found = finder.next();
      fail("No more words should have been found\nString: '" + found + "'");
    }catch(Exception e){
    }finally{
      assertEquals(expected.length, wordCount);
    }

  }
  
  private void assertNotFound(String testText){
    TeXWordFinder finder = new TeXWordFinder(testText);
    try{
      finder.next();
      fail("\nNo words should have been found!\nWordNotFoundException should have been thrown.\nString: " + testText);
    }catch (Exception e){
    }
   }
  
  private void assertFound(String testText, String expected){
    TeXWordFinder finder = new TeXWordFinder(testText);
    Word found = finder.next();
    assertEquals(expected,found.getText());
   }
  
  public static void main(String[] args){
    TestRunner.run(new TestSuite(TeXWordFinderTester.class));
  }
  
}
