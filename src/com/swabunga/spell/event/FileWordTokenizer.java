package com.swabunga.spell.event;



import java.util.*;

import java.io.*;



/** This class tokenizes a input file.

 *  <p>

 *  Any takers to do this efficiently?? Doesnt need to replace any words to start with

 *  . I need this to get an idea of how quick the spell checker is.

 */

public class FileWordTokenizer extends AbstractWordTokenizer {



  public FileWordTokenizer(File inputFile) {
     super(stringValue(inputFile));
  }

  private static String stringValue(File inFile){
	  String out = "";
	  return out;
  }
  
  public void replaceWord(String s){

  }
  
}