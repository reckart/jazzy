package  com.swabunga.spell.engine;

import java.io.*;

/** A Generic implementation of a spell dictionary witch constructs 
 *  generic transformator out of an aspell phonetics file.
 *
 * @author Robert Gustavsson (robert@lindesign.se)
 */
public class GenericSpellDictionary extends SpellDictionary{
    
    Transformator tf=null;

    /**     
    * Dictionary Convienence Constructor.     
    */
    public GenericSpellDictionary (File wordList, File phonetic) throws FileNotFoundException, IOException
    {
        tf=new GenericTransformator(phonetic);
        dictFile=wordList;
        long time=System.currentTimeMillis();
        createDictionary(new BufferedReader(new FileReader(wordList)));
        System.out.println("It took "+((System.currentTimeMillis()-time)/1000)+" seconds.");
    }

    public String getCode(String word) {
        return tf.transform(word);
    }
}
