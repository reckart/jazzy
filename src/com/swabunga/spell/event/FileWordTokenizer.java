package com.swabunga.spell.event;

import java.io.File;


/**
 * This class tokenizes a input file.
 * 
 * <p>
 * Any takers to do this efficiently?? Doesnt need to replace any words to
 * start with . I need this to get an idea of how quick the spell checker is.
 * </p>
 */
public class FileWordTokenizer
  extends AbstractWordTokenizer {

  //~ Instance/static variables ...............................................

//  private File inFile;

  //~ Constructors ............................................................

  /**
   * Creates a new FileWordTokenizer object.
   * 
   * @param inputFile ¤
   */
  public FileWordTokenizer(File inputFile) {
    super(stringValue(inputFile));
  }

  //~ Methods .................................................................

  /**
   * ¤
   * 
   * @param s ¤
   * @throws WordNotFoundException current word not yet set.
   */
  public void replaceWord(String s) {
  }

  private static String stringValue(File inFile) {
//    File stringFile = inFile;

    String out = "";

    return out;
  }
}