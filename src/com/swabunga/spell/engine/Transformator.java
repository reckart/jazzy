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

}
