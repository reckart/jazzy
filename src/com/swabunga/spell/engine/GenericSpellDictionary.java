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

    /**
     * This replace list is used if no phonetic file is supplied or it doesn't
     * contain the alphabet.
     */
    protected static final char[] englishAlphabet =
    {
        'A',
        'B',
        'C',
        'D',
        'E',
        'F',
        'G',
        'H',
        'I',
        'J',
        'K',
        'L',
        'M',
        'N',
        'O',
        'P',
        'Q',
        'R',
        'S',
        'T',
        'U',
        'V',
        'W',
        'X',
        'Y',
        'Z'};

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
     * Dictionary Convienence Constructor.
     */
    public GenericSpellDictionary(File wordList)
    throws FileNotFoundException, IOException {
        dictFile = wordList;
        replacelist=washAlphabetIntoReplaceList(englishAlphabet);
        //System.out.println("DoubleMeta");
        createDictionary(new BufferedReader(new FileReader(wordList)));
    }

    /**
    * Dictionary constructor that uses an aspell phonetic file to
    * build the transformation table.
    */
    public GenericSpellDictionary(File wordList, File phonetic)
    throws FileNotFoundException, IOException {
        char[]  alphabet=null;
        dictFile = wordList;
        //System.out.println("Phonetic");
        tf=new GenericTransformator(phonetic);
        alphabet=((GenericTransformator)tf).getAlphaReplaceList();
        // If no alphabet is availible use the english.
        if(alphabet==null)
            alphabet=englishAlphabet;
        replacelist=washAlphabetIntoReplaceList(alphabet);
        createDictionary(new BufferedReader(new FileReader(wordList)));
    }

    /**
     * Goes through an alphabet and makes sure that only one of those letters
     * that are coded equaly will be in the replace list. This is done to 
     * improve speed in the getSuggestion method,
     * 
     * @param alphabet The complete alphabet to wash.
     * @return The washed alphabet to be used as replace list.
     */
    private char[] washAlphabetIntoReplaceList(char[] alphabet){
        String      tmp,code;
        HashMap     letters=new HashMap(alphabet.length);
        Object[]    tmpCharacters;
        char[]      washedArray;
        //System.out.println("Original:");
        for(int i=0;i<alphabet.length;i++){
            tmp=String.valueOf(alphabet[i]);
            //System.out.print(tmp);
            code=tf.transform(tmp);
            //System.out.print(" Coded as:"+code);
            if(!letters.containsKey(code)){
                letters.put(code,new Character(alphabet[i]));
                //System.out.print(" add");
            }
            //System.out.println();
        }
        //System.out.print("Washed:");
        tmpCharacters=letters.values().toArray();
        washedArray=new char[tmpCharacters.length];
        for(int i=0;i<tmpCharacters.length;i++){
            washedArray[i]=((Character)tmpCharacters[i]).charValue();
            //System.out.print(" "+washedArray[i]);
        }
        //System.out.println();
        return washedArray;
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
        LinkedList mainDictResult = (LinkedList) mainDictionary.get(code);
        if (mainDictResult == null)
            return new LinkedList();
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

    /**
     * Returns a linked list of Word objects that are the suggestions to an
     * incorrect word.
     * <p>
     * @param word Suggestions for given mispelt word
     * @param threshold The lower boundary of similarity to mispelt word
     * @return LinkedList a List of suggestions
     */
    public List getSuggestions(String word, int threshold) {

        HashSet nearmisscodes = new HashSet();
        String code = getCode(word);

        // add all words that have the same codeword
        nearmisscodes.add(code);

        // do some tranformations to pick up more results
        //interchange
        char[] charArray = word.toCharArray();
        for (int i = 0; i < word.length() - 1; i++) {
            char a = charArray[i];
            char b = charArray[i + 1];
            charArray[i] = b;
            charArray[i + 1] = a;
            nearmisscodes.add(getCode(new String(charArray)));
            charArray[i] = a;
            charArray[i + 1] = b;
        }
        //change
        charArray = word.toCharArray();
        for (int i = 0; i < word.length(); i++) {
            char original = charArray[i];
            for (int j = 0; j < replacelist.length; j++) {
                charArray[i] = replacelist[j];
                nearmisscodes.add(getCode(new String(charArray)));
            }
            charArray[i] = original;
        }
        //add
        charArray = (word += " ").toCharArray();
        int iy = charArray.length - 1;
        while (true) {
            for (int j = 0; j < replacelist.length; j++) {
                charArray[iy] = replacelist[j];
                nearmisscodes.add(getCode(new String(charArray)));
            }
            if (iy == 0)
                break;
            charArray[iy] = charArray[iy - 1];
            --iy;
        }
        //delete
        word = word.trim();
        charArray = word.toCharArray();
        char[] charArray2 = new char[charArray.length - 1];
        for (int ix = 0; ix < charArray2.length; ix++) {
            charArray2[ix] = charArray[ix];
        }
        char a, b;
        a = charArray[charArray.length - 1];
        int ii = charArray2.length;
        while (true) {
            nearmisscodes.add(getCode(new String(charArray)));
            if (ii == 0)
                break;
            b = a;
            a = charArray2[ii - 1];
            charArray2[ii - 1] = b;
            --ii;
        }

        LinkedList wordlist = getWordsFromCode(word, nearmisscodes);
        // We sort a linkedlist at the end instead of maintaining a
        // continously sorted TreeSet because everytime you add a collection
        // to a treeset it has to be resorted. It's better to do this operation
        // once at the end.
        Collections.sort( wordlist, new Word());
        return wordlist;
    }

    private LinkedList getWordsFromCode(String word, Collection codes) {
        Configuration config = Configuration.getConfiguration();
        LinkedList result = new LinkedList();
        for (Iterator i = codes.iterator(); i.hasNext();) {
            String code = (String) i.next();
            List simwordlist = getWords(code);
            for (Iterator j = simwordlist.iterator(); j.hasNext();) {
                String similar = (String) j.next();
                //System.out.println(similar);
                int distance = EditDistance.getDistance(word, similar);
                if (distance < config.getInteger(Configuration.SPELL_THRESHOLD)) {
                    Word w = new Word(similar, distance);
                    result.add(w);
                }
            }
        }
        return result;
    }
}
