/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  com.swabunga.spell.event;

import  java.util.*;
import com.swabunga.spell.engine.*;


/** This is the main class for spell checking (using the new event based spell
 *  checking).
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

  /** This method clears the words that are currently being remembered as
   *  Ignore All words and Replace All words.
   */
  public void reset() {
    ignoredWords.clear();
    autoReplaceWords.clear();
  }

  /** Checks the text string.
   *  <p>
   *  Returns the corrected string.
   *  @deprecated use checkSpelling(WordTokenizer)
   */
  public String checkString (String text) {
    StringWordTokenizer tokens = new StringWordTokenizer(text);
    checkSpelling(tokens);
    return tokens.getFinalText();
  }

  /** This method is called to check the spelling of the words that are returned
   *  by the WordTokenizer.
   *  <p>For each invalid word the action listeners will be informed with a new SpellCheckEvent</p>
   */
  public final void checkSpelling(WordTokenizer tokenizer) {
    //Dont bother to execute if no-one is listening ;-)
    if (eventListeners.size() > 0) {
      boolean terminated = false;
      while (tokenizer.hasMoreWords() && !terminated) {
        String word = tokenizer.nextWord();
        //Check the spelling of the word
        if (!dictionary.isCorrect(word)) {
          //For this invalid word are we ignoreing the misspelling?
          if (!ignoredWords.contains(word)) {
            //Is this word being automagically replaced
            if (autoReplaceWords.containsKey(word)) {
              tokenizer.replaceWord((String)autoReplaceWords.get(word));
            }
            else {
              System.out.println("Current word position="+tokenizer.getCurrentWordPosition());
              //Fire the event.
              SpellCheckEvent event = new BasicSpellCheckEvent(word, dictionary.getSuggestions(word,
                  threshold), tokenizer);
              fireSpellCheckEvent(event);
              //Work out what to do in response to the event.
              switch (event.getAction()) {
                case SpellCheckEvent.INITIAL:
                  break;
                case SpellCheckEvent.IGNORE:
                  break;
                case SpellCheckEvent.IGNOREALL:
                  if (!ignoredWords.contains(word))
                    ignoredWords.add(word);
                  break;
                case SpellCheckEvent.REPLACE:
                  tokenizer.replaceWord(event.getReplaceWord());
                  break;
                case SpellCheckEvent.REPLACEALL:
                  String replaceAllWord = event.getReplaceWord();
                  if (!autoReplaceWords.containsKey(word)) {
                    autoReplaceWords.put(word, replaceAllWord);
                  }
                  tokenizer.replaceWord(replaceAllWord);
                  break;
                  //JMH TBD        case SpellCheckEvent.ADDTODICT:
                  case SpellCheckEvent.CANCEL:
                    terminated = true;
                    break;
                default:
                  throw  new IllegalArgumentException("Unhandled case.");
              }
            }
          }
        }
      }
    }
  }
}



