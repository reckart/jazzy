/* Created by bgalbs on Jan 30, 2003 at 11:45:25 PM */
package com.swabunga.spell.engine;

import java.util.*;

/**
 * Container for various methods that any <code>SpellDictionary</code> based on the original
 * Jazzy aspell port will use.
 */
public abstract class SpellDictionaryASpell implements SpellDictionary {
    /** The replace list is used in the getSuggestions method*/
    protected static final char[] replacelist =
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

    /**The reference to a Transformator, used to transform a word into it's.
     * phonetic code.
     */
    protected Transformator tf = new DoubleMeta();

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
