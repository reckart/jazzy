package com.swabunga.spell.engine;

import java.io.*;
import java.util.*;

/**
 * Another implementation of <code>SpellDictionary</code> that doesn't cache any words in memory. Avoids the huge
 * footprint of <code>SpellDictionaryHashMap</code> at the cost of relatively minor latency. A future version
 * of this class that implements some caching strategies might be a good idea in the future, if there's any
 * demand for it.
 *
 * This implementation requires a special dictionary file, with "code*word" lines sorted by code.
 * It's using a dichotomy algorithm to search for words in the dictionary
 *
 * @author Damien Guillaume
 * @version 0.1
 */
public class SpellDictionaryDichoDisk extends SpellDictionaryASpell {

  /** A field indicating the initial hash map capacity (16KB) for the main
   *  dictionary hash map. Interested to see what the performance of a
   *  smaller initial capacity is like.
   */
  private final static int INITIAL_CAPACITY = 16 * 1024;
  /**
   * The hashmap that contains the personal word dictionary. The map is hashed on the doublemeta
   * code. The map entry contains a LinkedList of words that have the same double meta code.
   */
  protected HashMap persoDictionary = new HashMap(INITIAL_CAPACITY);
  
  /** Holds the dictionary file for reading*/
  private RandomAccessFile dictFile = null;
  
  /** Holds the personal dictionary file for appending*/
  private File persoFile = null;

  /** dictionary and phonetic file encoding */
  private String encoding = null;

  /**
   * Dictionary Convienence Constructor.
   */
  public SpellDictionaryDichoDisk(File wordList)
    throws FileNotFoundException, IOException {
    super((File) null);
    dictFile = new RandomAccessFile(wordList, "r");
    readPersoFile();
  }

  /**
   * Dictionary Convienence Constructor.
   */
  public SpellDictionaryDichoDisk(File wordList, String encoding)
    throws FileNotFoundException, IOException {
    super((File) null);
    this.encoding = encoding;
    dictFile = new RandomAccessFile(wordList, "r");
    readPersoFile();
  }

  /**
  * Dictionary constructor that uses an aspell phonetic file to
  * build the transformation table.
  */
  public SpellDictionaryDichoDisk(File wordList, File phonetic)
    throws FileNotFoundException, IOException {
    super(phonetic);
    dictFile = new RandomAccessFile(wordList, "r");
    readPersoFile();
  }
  
  /**
  * Dictionary constructor that uses an aspell phonetic file to
  * build the transformation table.
  */
  public SpellDictionaryDichoDisk(File wordList, File phonetic, String encoding)
    throws FileNotFoundException, IOException {
    super(phonetic, encoding);
    this.encoding = encoding;
    dictFile = new RandomAccessFile(wordList, "r");
    readPersoFile();
  }
  
  /**
    * Locate and read the personal dictionary
    */
  protected void readPersoFile() {
    String userHome = System.getProperty("user.home");
    String osName = System.getProperty("os.name");
    String filename;
    if (osName.indexOf("Windows") != -1)
      filename = "preferences";
    else
      filename = userHome + File.separator + ".jaxe_pers_dict";
    persoFile = new File(filename);
    try {
      if (persoFile.exists())
        createPersoDictionary(new BufferedReader(new FileReader(persoFile)));
    } catch (Exception ex) {
      System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
      persoFile = null;
    }
  }
  
  /**
   * Add a word permanantly to the dictionary (and the dictionary file).
   * <p>This needs to be made thread safe (synchronized)</p>
   */
  public void addWord(String word) {
    putWord(word);
    if (persoFile == null)
      return;
    try {
      persoFile.createNewFile(); // create new file if and only if necessary
      FileWriter w = new FileWriter(persoFile.toString(), true); // Open with append.
      w.write(word);
      w.write("\n");
      w.close();
    } catch (IOException ex) {
      System.out.println("Error writing to personal dictionary file (" + persoFile.getPath() + ")");
    }
  }

  /**
   * Constructs the personal dictionary from a word list file.
   * <p>
   * Each word in the reader should be on a seperate line.
   * <p>
   * This is a very slow function, which is why it should be used
   * only for very small dictionaries
   */
  protected void createPersoDictionary(BufferedReader in) throws IOException {
    String line = "";
    while (line != null) {
      line = in.readLine();
      if (line != null) {
        line = new String(line.toCharArray());
        putWord(line);
      }
    }
  }

  /**
   * Allocates a word in the personal dictionary
   */
  protected void putWord(String word) {
    String code = getCode(word);
    LinkedList list = (LinkedList) persoDictionary.get(code);
    if (list != null) {
      list.add(word);
    } else {
      list = new LinkedList();
      list.add(word);
      persoDictionary.put(code, list);
    }
  }

  /**
    * Search the dictionary file for the words corresponding to the code
    * within positions p1 - p2
    */
   private LinkedList dichoFind(String code, long p1, long p2) throws IOException {
     //System.out.println("dichoFind("+code+","+p1+","+p2+")");
     long pm = (p1 + p2) / 2;
    dictFile.seek(pm);
    String l;
    if (encoding == null)
      l = dictFile.readLine();
    else
      l = dictReadLine();
    pm = dictFile.getFilePointer();
    if (encoding == null)
      l = dictFile.readLine();
    else
      l = dictReadLine();
    long pm2 = dictFile.getFilePointer();
    if (pm2 >= p2)
      return(seqFind(code, p1, p2));
    int istar = l.indexOf('*');
    if (istar == -1)
      throw new IOException("bad format: no * !");
    String testcode = l.substring(0, istar);
    int comp = code.compareTo(testcode);
    if (comp < 0)
      return(dichoFind(code, p1, pm-1));
    else if (comp > 0)
      return(dichoFind(code, pm2, p2));
    else {
      LinkedList l1 = dichoFind(code, p1, pm-1);
      LinkedList l2 = dichoFind(code, pm2, p2);
      String word = l.substring(istar+1);
      l1.add(word);
      l1.addAll(l2);
      return(l1);
    }
   }
   
   private LinkedList seqFind(String code, long p1, long p2) throws IOException {
     //System.out.println("seqFind("+code+","+p1+","+p2+")");
     LinkedList list = new LinkedList();
    dictFile.seek(p1);
    while (dictFile.getFilePointer() < p2) {
      String l;
      if (encoding == null)
        l = dictFile.readLine();
      else
        l = dictReadLine();
      int istar = l.indexOf('*');
      if (istar == -1)
        throw new IOException("bad format: no * !");
      String testcode = l.substring(0, istar);
      if (testcode.equals(code)) {
        String word = l.substring(istar+1);
        list.add(word);
      }
    }
    return(list);
   }
   
   /**
     * Read a line of dictFile with a specific encoding
     */
   private String dictReadLine() throws IOException {
     int max = 255;
     byte b=0;
    byte[] buf = new byte[max];
    int i=0;
     try {
       for (; b != '\n' && b != '\r' && i<max-1; i++) {
        b = dictFile.readByte();
         buf[i] = b;
      }
    } catch (EOFException ex) {
    }
    if (i == 0)
      return("");
    String s = new String(buf, 0, i-1, encoding);
    return(s);
   }
   
  /**
   * Returns a list of strings (words) for the code.
   */
  public List getWords(String code) {
     //System.out.println("getWords("+code+")");
    LinkedList list;
    try {
      list = dichoFind(code, 0, dictFile.length()-1);
      //System.out.println(list);
    } catch (IOException ex) {
      System.err.println("IOException: " + ex.getMessage());
      list = new LinkedList();
    }
    LinkedList persoDictResult = (LinkedList) persoDictionary.get(code);
    if (persoDictResult != null)
      list.addAll(persoDictResult);
    return list;
  }

}
