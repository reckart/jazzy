/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */

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
public class SpellDictionary {


	/** The replace list is used in the getSuggestions method*/
	private static char[] replacelist =
		{
			'A',
			'B',
			'X',
			'S',
			'K',
			'J',
			'T',
			'F',
			'H',
			'L',
			'M',
			'N',
			'P',
			'R',
			'0' };

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
	/**The reference to a Transformator, used to transform a word into it's.
	 * phonetic code.
	 */
	private Transformator tf = null;


	/** Holds the dictionary file for appending*/
	private File dictFile = null;

	/**
	 * Dictionary Constructor.
	 */
	public SpellDictionary(Reader wordList) throws IOException {
		tf = new DoubleMeta();
		createDictionary(new BufferedReader(wordList));
	}

	/**
	 * Dictionary Convienence Constructor.
	 */
	public SpellDictionary(File wordList)
		throws FileNotFoundException, IOException {
		this(new FileReader(wordList));
		dictFile = wordList;
	}

	/**
	* Dictionary constructor that uses an aspell phonetic file to
	* build the transformation table.
	*/
	public SpellDictionary(File wordList, File phonetic)
		throws FileNotFoundException, IOException {
		tf = new GenericTransformator(phonetic);
                replacelist=((GenericTransformator)tf).getReplaceList();
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
	 * Returns the code representing the word.
	 */
	public String getCode(String word) {
		return tf.transform(word);
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
	public LinkedList getWords(String code) {
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
		LinkedList possible = getWords(getCode(word));
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
	public LinkedList getSuggestions(String word, int threshold) {

		HashSet nearmisscodes = new HashSet();
		String code = getCode(word);

		// add all words that have the same codeword
                nearmisscodes.add(code);

		// do some tranformations to pick up more results
		//interchange 
		char[] charArray = code.toCharArray();
		for (int i = 0; i < code.length() - 1; i++) {
			char a = charArray[i];
			char b = charArray[i + 1];
			charArray[i] = b;
			charArray[i + 1] = a;
			nearmisscodes.add(new String(charArray));
			charArray[i] = a;
			charArray[i + 1] = b;
		}
		//change
		charArray = code.toCharArray();
		for (int i = 0; i < code.length(); i++) {
			char original = charArray[i];
			for (int j = 0; j < replacelist.length; j++) {
				charArray[i] = replacelist[j];
				nearmisscodes.add(new String(charArray));
			}
			charArray[i] = original;
		}
		//add
		charArray = (code += " ").toCharArray();
		int iy = charArray.length - 1;
		while (true) {
			for (int j = 0; j < replacelist.length; j++) {
				charArray[iy] = replacelist[j];
				nearmisscodes.add(new String(charArray));
			}
			if (iy == 0)
				break;
			charArray[iy] = charArray[iy - 1];
			--iy;
		}
		//delete
		word = code.trim();
		charArray = code.toCharArray();
		char[] charArray2 = new char[charArray.length - 1];
		for (int ix = 0; ix < charArray2.length; ix++) {
			charArray2[ix] = charArray[ix];
		}
		char a, b;
		a = charArray[charArray.length - 1];
		int ii = charArray2.length;
		while (true) {
			nearmisscodes.add(new String(charArray));
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
			LinkedList simwordlist = getWords(code);
			for (Iterator j = simwordlist.iterator(); j.hasNext();) {
				String similar = (String) j.next();
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
