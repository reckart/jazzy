package com.swabunga.spell.engine;

import java.io.*;
import java.util.*;

/**
 * The SpellDictionary class holds the instance of the dictionary.
 * <p>
 * This class is thread safe. Derived classes should ensure that this preserved.
 * </p>
 * <p>
 * There are many open source dictionary files. For just a few see:
 * http://wordlist.sourceforge.net/
 * </p>
 * <p>
 * This dictionary class reads words one per line. Make sure that your word list
 * is formatted in this way (most are).
 * </p>
 */
public class GenericSpellDictionary extends SpellDictionaryASpell {

//tech_monkey: the alphabet / replace list stuff has been moved into the Transformator classes, 
//since they are so closely tied to how the phonetic transformations are done. 
//    /**
//     * This replace list is used if no phonetic file is supplied or it doesn't
//     * contain the alphabet.
//     */
//    protected static final char[] englishAlphabet = 


    /** A field indicating the initial hash map capacity (16KB) for the main
     *  dictionary hash map. Interested to see what the performance of a
     *  smaller initial capacity is like.
     */
    private final static int INITIAL_CAPACITY = 16 * 1024;

    /**
     * The hashmap that contains the word dictionary. The map is hashed on the doublemeta
     * code. The map entry contains a LinkedList of words that have the same double meta code.
     */
    protected HashMap mainDictionary = new HashMap(INITIAL_CAPACITY);

    /** Holds the dictionary file for appending*/
    private File dictFile = null;



    /**
     * Dictionary constructor that uses the DoubleMeta class with the 
     * english alphabet.
     */
    public GenericSpellDictionary(File wordList)
    throws FileNotFoundException, IOException {
		this(wordList,(File)null);
    }

    /**
    * Dictionary constructor that uses an aspell phonetic file to
    * build the transformation table.
    * If phonetic is null, then DoubleMeta is used with the english alphabet
    */
    public GenericSpellDictionary(File wordList, File phonetic)
    throws FileNotFoundException, IOException {
		
		super(phonetic);
        dictFile = wordList;
        createDictionary(new BufferedReader(new FileReader(wordList)));
    }


    /**
     * Add a word permanantly to the dictionary (and the dictionary file).
     * <p>This needs to be made thread safe (synchronized)</p>
     */
    public void addWord(String word) {
        putWord(word);
        if (dictFile == null)
            return;
        try {
            FileWriter w = new FileWriter(dictFile.toString(), true);
            // Open with append.
            w.write(word);
            w.write("\n");
            w.close();
        } catch (IOException ex) {
            System.out.println("Error writing to dictionary file");
        }
    }

    /**
     * Constructs the dictionary from a word list file.
     * <p>
     * Each word in the reader should be on a seperate line.
     * <p>
     * This is a very slow function. On my machine it takes quite a while to
     * load the data in. I suspect that we could speed this up quite alot.
     */
    protected void createDictionary(BufferedReader in) throws IOException {
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
     * Allocates a word in the dictionary
     */
    protected void putWord(String word) {
        String code = getCode(word);
        LinkedList list = (LinkedList) mainDictionary.get(code);
        if (list != null) {
            list.add(word);
        } else {
            list = new LinkedList();
            list.add(word);
            mainDictionary.put(code, list);
        }
    }

    /**
     * Returns a list of strings (words) for the code.
     */
    public List getWords(String code) {
        //Check the main dictionary.
        List mainDictResult = (List) mainDictionary.get(code);
        if (mainDictResult == null)
            return new Vector();
        return mainDictResult;
    }

    /**
     * Returns true if the word is correctly spelled against the current word list.
     */
    public boolean isCorrect(String word) {
        List possible = getWords(getCode(word));
        if (possible.contains(word))
            return true;
        //JMH should we always try the lowercase version. If I dont then capitalised
        //words are always returned as incorrect.
        else if (possible.contains(word.toLowerCase()))
            return true;
        return false;
    }
}
