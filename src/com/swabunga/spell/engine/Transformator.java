package  com.swabunga.spell.engine;

/** 
 * An interface for all Transformators - which take a dictionary word and converts into its
 * phonetic hash. These phonetic hashs are useful for determining what other words are
 * similiar to it, and then list those words as suggestsions. 
 *
 * @author Robert Gustavsson (robert@lindesign.se)
 */
public interface Transformator{

    /**
	 * Take the given word, and return the best phonetic hash for it. 
	 */
	public String transform(String word);

	/** 
	 * gets the list of characters that should be swapped in to the misspelled word
	 * in order to try to find more suggestions. 
	 * In general, this list represents all of the unique phonetic characters
	 * for this Tranformator. 
	 * <p/>
     * The replace list is used in the getSuggestions method.
     * All of the letters in the misspelled word are replaced with the characters from 
     * this list to try and generate more suggestions, which implies l*n tries,
     * if l is the size of the string, and n is the size of this list.
	 * <p/>
     * In addition to that, each of these letters is added to the mispelled word. 
	 * <p/>
	 * @return char[] misspelled words should try replacing with these characters to get more suggestions
	 */
	public char[] getReplaceList();
}
