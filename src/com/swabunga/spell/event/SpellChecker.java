/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  com.swabunga.spell.event;

import  java.util.*;
import com.swabunga.spell.engine.*;


/** This is the main class for spell checking (using the new event based spell
 *  checking).
 *  <p>
 *  Some changes in the event handeling will be necessary, since the user may want
 *  to present some context information etc that is not currently available in the
 *  SpellCheckEvent.
 *
 * @author Jason Height (jheight@chariot.net.au)
 */
public class SpellChecker {
  private List eventListeners = new ArrayList();
  private SpellDictionary dictionary;
  private int threshold = 100;

  /**This variable holds all of the words that are to be always ignored*/
  private Set ignoredWords = new HashSet();
  private Map autoReplaceWords = new HashMap();

  //JMH This is probably the place to put a list that holds the ignorable words
  //and the change all words. As such the Spell Checker becomes the userobject that
  //cannot be shared and the dictionary could become the thread safe shared object.
  //Currently the SpellDictionary is not thread safe, certainly the DoubleMeta isnt.
  /** Constructs the SpellChecker. The default threshold is used*/
  public SpellChecker (SpellDictionary dictionary) {
    if (dictionary == null)
      throw  new IllegalArgumentException("dictionary must no be null");
    this.dictionary = dictionary;
  }

  /** Constructs the SpellChecker with a threshold*/
  public SpellChecker (SpellDictionary dictionary, int threshold) {
    this(dictionary);
    this.threshold = threshold;
  }

  /**Adds a SpellCheckListener*/
  public void addSpellCheckListener (SpellCheckListener listener) {
    eventListeners.add(listener);
  }

  /**Removes a SpellCheckListener*/
  public void removeSpellCheckListener (SpellCheckListener listener) {
    eventListeners.remove(listener);
  }

  /**Alter the threshold*/
  public void setThreshold (int threshold) {
    this.threshold = threshold;
  }

  /**Returns the threshold*/
  public int getThreshold () {
    return  threshold;
  }

  /** Fires off a spell check event to the listeners.
   */
  protected void fireSpellCheckEvent (SpellCheckEvent event) {
    for (int i = eventListeners.size() - 1; i >= 0; i--) {
      ((SpellCheckListener)eventListeners.get(i)).spellingError(event);
    }
  }

  /** Checks the text string.
   *  <p>
   *  Returns the corrected string.
   */
  public String checkString (String text) {
    //Dont bother to execute if no-one is listening ;-)
    if (eventListeners.size() > 0) {
      int wordCount = 0;
      long startTime = System.currentTimeMillis();
      WordTokenizer tokens = new WordTokenizer(text);
      while (tokens.hasMoreWords()) {
        wordCount++;
        String word = tokens.nextWord();
        //Check the spelling of the word
        if (!dictionary.isCorrect(word)) {
          //For this invalid word are we ignoreing the misspelling?
          if (!ignoredWords.contains(word)) {
            if (autoReplaceWords.containsKey(word)) {
            //JMH TBD
            }
            else {
              //JMH TBD replace the string tokenizer with something that remembers its current position
              SpellCheckEvent event = new BasicSpellCheckEvent(word, dictionary.getSuggestions(word,
                  threshold), text, tokens.getCurrentPosition());
              fireSpellCheckEvent(event);
              switch (event.getAction()) {
                case SpellCheckEvent.INITIAL:
                  break;
                case SpellCheckEvent.IGNORE:
                  break;
                case SpellCheckEvent.IGNOREALL:
                  String ignoreWord = event.getReplaceWord();
                  if (!ignoredWords.contains(ignoreWord))
                    ignoredWords.add(ignoreWord);
                  break;
                case SpellCheckEvent.REPLACE:
                  String newWord = event.getReplaceWord();
                  //JMH TBD
                  break;
                case SpellCheckEvent.REPLACEALL:
                  String replaceAllWord = event.getReplaceWord();
                  if (!autoReplaceWords.containsKey(word)) {
                    autoReplaceWords.put(word, replaceAllWord);
                  }
                  //JMH TBD
                  break;
                  //JMH TBD        case SpellCheckEvent.ADDTODICT:
                default:
                  throw  new IllegalArgumentException("Unhandled case.");
              }
            }
          }
        }
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime != 0) {
          long wordsPerSecond = (wordCount)*1000/(elapsedTime);
          System.out.println("Word count=" + wordCount + " Elapsed Time =" +
              (elapsedTime));
          System.out.println("Checking rate = " + wordsPerSecond + " words Per second");
        }
      }
      //JMH return a corrected text
      return  text;
    }
    return  null;
  }
}



