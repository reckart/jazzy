/* Created by bgalbs on Jan 30, 2003 at 11:45:25 PM */
package com.swabunga.spell.engine;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;

/**
 * Container for various methods that any <code>SpellDictionary</code> will use. 
 * Based on the original Jazzy <a href="http://aspell.net/">aspell</a> port.
 * <p/>
 * 
 * 
 */
public abstract class SpellDictionaryASpell implements SpellDictionary {


    /** The reference to a Transformator, used to transform a word into it's phonetic code. */
    protected Transformator tf;

	public SpellDictionaryASpell(File phonetic) throws IOException
	{
		if (phonetic == null)
			tf = new DoubleMeta();
		else
			tf = new GenericTransformator(phonetic);
	}
	
    /**
     * Returns a list of Word objects that are the suggestions to an
     * incorrect word.
     * <p>
     * @param word Suggestions for given mispelt word
     * @param threshold The lower boundary of similarity to mispelt word
     * @return Vector a List of suggestions
     */
    public List getSuggestions(String word, int threshold) {

		Hashtable nearmisscodes = new Hashtable();
        String code = getCode(word);

        // add all words that have the same phonetics
		nearmisscodes.put(code, code);
		Vector phoneticList = getWordsFromCode(word, nearmisscodes);

        // do some tranformations to pick up more results
        //interchange
		nearmisscodes = new Hashtable();
        char[] charArray = word.toCharArray();
        for (int i = 0; i < word.length() - 1; i++) {
            char a = charArray[i];
            char b = charArray[i + 1];
            charArray[i] = b;
            charArray[i + 1] = a;
            String s = getCode(new String(charArray));
			nearmisscodes.put(s, s);
            charArray[i] = a;
            charArray[i + 1] = b;
        }
        
        char[] replacelist = tf.getReplaceList();
        
        //change
        charArray = word.toCharArray();
        for (int i = 0; i < word.length(); i++) {
            char original = charArray[i];
            for (int j = 0; j < replacelist.length; j++) {
                charArray[i] = replacelist[j];
                String s = getCode(new String(charArray));
				nearmisscodes.put(s, s);
			}
            charArray[i] = original;
        }
        
        //add
        charArray = (word += " ").toCharArray();
        int iy = charArray.length - 1;
        while (true) {
            for (int j = 0; j < replacelist.length; j++) {
                charArray[iy] = replacelist[j];
				String s = getCode(new String(charArray));
				nearmisscodes.put(s, s);
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
			String s = getCode(new String(charArray));
			nearmisscodes.put(s, s);
            if (ii == 0)
                break;
            b = a;
            a = charArray2[ii - 1];
            charArray2[ii - 1] = b;
            --ii;
        }

		nearmisscodes.remove(code); //already accounted for in phoneticList

        Vector wordlist = getWordsFromCode(word, nearmisscodes);

		if (wordlist.size() == 0 && phoneticList.size() == 0)
			addBestGuess(word,phoneticList);
        
		
		// We sort a Vector at the end instead of maintaining a
        // continously sorted TreeSet because everytime you add a collection
        // to a treeset it has to be resorted. It's better to do this operation
        // once at the end.

//		Collections.sort( phoneticList, new Word()); //always sort phonetic matches along the top
//        Collections.sort( wordlist, new Word()); //the non-phonetic matches can be listed below
        
        phoneticList.addAll(wordlist);
        return phoneticList;
    }

	/**	 
	 * When we don't come up with any suggestions (probably because the threshold was too strict), 
	 * then pick the best guesses from the those words that have the same phonetic code. 
	 * @param word - the word we are trying spell correct
	 * @param wordList - the linked list that will get the best guess
	 */
	private void addBestGuess(String word, Vector wordList)
	{
		if (wordList.size() != 0)
			throw new InvalidParameterException("the wordList vector must be empty");
			
		int bestScore = Integer.MAX_VALUE;

		String code = getCode(word);
		List simwordlist = getWords(code);
		
		LinkedList candidates = new LinkedList();
		
		for (Iterator j = simwordlist.iterator(); j.hasNext();)
		{
			String similar = (String) j.next();
			int distance = EditDistance.getDistance(word, similar);
			if (distance <= bestScore)
			{
				bestScore = distance;
				Word goodGuess = new Word(similar, distance);
				candidates.add(goodGuess);
			}
		}

		//now, only pull out the guesses that had the best score
		for (Iterator iter = candidates.iterator(); iter.hasNext();)
		{
			Word candidate = (Word) iter.next();
			if (candidate.getCost() == bestScore)
				wordList.add(candidate);
		}

	}

	private Vector getWordsFromCode(String word, Hashtable codes) {
        Configuration config = Configuration.getConfiguration();
        Vector result = new Vector();
		final int configDistance = config.getInteger(Configuration.SPELL_THRESHOLD);
		
		for (Enumeration i = codes.keys();i.hasMoreElements();) {		
			String code = (String) i.nextElement();
			
			List simwordlist = getWords(code);
			for (Iterator iter = simwordlist.iterator(); iter.hasNext();)
			{
				String similar = (String) iter.next();
                int distance = EditDistance.getDistance(word, similar);
				if (distance < configDistance) {
                    Word w = new Word(similar, distance);
					result.addElement(w);
				}
			}
		}
        return result;
    }

    /**
     * Returns the phonetic code representing the word.
     */
    public String getCode(String word) {
        return tf.transform(word);
    }

    /**
     * Returns a list of words that have the same phonetic code.
     */
    protected abstract List getWords(String phoneticCode);
}
