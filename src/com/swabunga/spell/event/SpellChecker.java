/*
 * put your module comment here
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 *
 * formatted with JxBeauty (c) johann.langhofer@nextra.at
 */


package  com.swabunga.spell.event;

import  java.util.*;
import  com.swabunga.spell.engine.*;


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
  /** Field indicating if upper case words be ignored during spell checking
   */
  private boolean ignoreUpperCaseWords = false;
  /** Field indicating if mixed case words be ignored during spell checking
   */
  private boolean ignoreMixedCaseWords = false;

  /** Field indicating if words that appear to be internet addresses should be ingored*/
  private boolean ignoreInternetAddresses = false;

  /** Field indicating that words containing a digit(s) should be ignored*/
  private boolean ignoreDigitWords = true;
  /** Field indicating that words containing multiple occurences of a word straight after each other
   * should be ignored
   */
  private boolean ignoreMultipleWords = false;
  /** Field indicating that words that start a sentance can ignore capitalisation
   */
  private boolean ignoreSentanceCapitalisation = false;

  /** Constructs the SpellChecker. The default threshold is used*/
  public SpellChecker (SpellDictionary dictionary) {
    if (dictionary == null)
      throw  new IllegalArgumentException("dictionary must non-null");
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

  /** Sets whether upper case words be ignored during spell checking
   */
  public void setIgnoreUpperCaseWord (boolean ignore) {
    ignoreUpperCaseWords = ignore;
  }

  public boolean getIgnoreUpperCaseWord () {
    return  ignoreUpperCaseWords;
  }

  /** Sets whether words that appear to be internet addresses should be ingored*/
  public void setIgnoreINETAddresses (boolean ignore) {
    ignoreInternetAddresses = ignore;
  }

  public boolean getIgnoreINETAddresses () {
    return  ignoreInternetAddresses;
  }

  /** Sets whether words containing a digit(s) should be ignored*/
  public void setIgnoreDigitWords (boolean ignore) {
    ignoreDigitWords = ignore;
  }

  public boolean getIgnoreDigitWords () {
    return  ignoreDigitWords;
  }

  /** Sets whether words containing multiple occurences of a word straight after each other
   * should be ignored
   */
  public void setIgnoreMultipleWords (boolean ignore) {
    ignoreMultipleWords = ignore;
  }

  public boolean getIgnoreMultipleWords () {
    return  ignoreMultipleWords;
  }

  /** Sets whether words containing multiple occurences of a word straight after each other
   * should be ignored
   */
  public void setIgnoreSentanceCapitalisation (boolean ignore) {
    ignoreSentanceCapitalisation = ignore;
  }

  public boolean getIgnoreSentanceCapitalisation () {
    return  ignoreSentanceCapitalisation;
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
  public void reset () {
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
    return  tokens.getFinalText();
  }

  /** Returns true iif this word contains a digit*/
  private static final boolean isDigitWord (String word) {
    for (int i = word.length() - 1; i >= 0; i--) {
      if (Character.isDigit(word.charAt(i)))
        return  true;
    }
    return  false;
  }

  /** Returns true iif this word looks like an internet address*/
  private static final boolean isINETWord (String word) {
    //JMH TBD
    return  false;
  }

  /** Returns true iif this word contains all upper case characters*/
  private static final boolean isUpperCaseWord (String word) {
    for (int i = word.length() - 1; i >= 0; i--) {
      if (Character.isLowerCase(word.charAt(i)))
        return  false;
    }
    return  true;
  }

  /** Returns true iif this word contains mixed case characters*/
  private static final boolean isMixedCaseWord (String word) {
    int strLen = word.length();
    boolean isUpper = Character.isUpperCase(word.charAt(strLen - 1));
    if (isUpper) {
      //JMH This is not quite right because a word that starts a sentance can
      //Have the first char upper case and the rest lower case. This
      //should be fixed.
      for (int i = word.length() - 2; i >= 0; i--) {
        if (Character.isLowerCase(word.charAt(i)))
          return  true;
      }
    }
    else {
      for (int i = word.length() - 2; i >= 0; i--) {
        if (Character.isUpperCase(word.charAt(i)))
          return  true;
      }
    }
    return  false;
  }

  protected boolean fireAndHandleEvent (WordTokenizer tokenizer, SpellCheckEvent event) {
    boolean terminated = false;
    fireSpellCheckEvent(event);
    String word = event.getInvalidWord();
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
      case SpellCheckEvent.ADDTODICT:
        String addWord = event.getReplaceWord();
        tokenizer.replaceWord(addWord);
        dictionary.addWord(addWord);
        break;
      case SpellCheckEvent.CANCEL:
        terminated = true;
        break;
      default:
        throw  new IllegalArgumentException("Unhandled case.");
    }
    return  terminated;
  }

  /**
   /** This method is called to check the spelling of the words that are returned
   *  by the WordTokenizer.
   *  <p>For each invalid word the action listeners will be informed with a new SpellCheckEvent</p>
   */
  public final void checkSpelling (WordTokenizer tokenizer) {
    //Dont bother to execute if no-one is listening ;-)
    if (eventListeners.size() > 0) {
      boolean terminated = false;
      while (tokenizer.hasMoreWords() && !terminated) {
        String word = tokenizer.nextWord();
        //Check the spelling of the word
        if (!dictionary.isCorrect(word)) {
          if ((ignoreMixedCaseWords && isMixedCaseWord(word))||
              (ignoreUpperCaseWords && isUpperCaseWord(word)) ||
              (ignoreDigitWords && isDigitWord(word)) ||
              (ignoreInternetAddresses && isINETWord(word))) {
            //Null event. Since we are ignoring this word due
            //to one of the above cases.
          } else {
            //We cant ignore this misspelt word
            //For this invalid word are we ignoreing the misspelling?
            if (!ignoredWords.contains(word)) {
              //Is this word being automagically replaced
              if (autoReplaceWords.containsKey(word)) {
                tokenizer.replaceWord((String)autoReplaceWords.get(word));
              }
              else {
                //JMH Need to somehow capitalise the suggestions if
                //ignoreSentanceCapitalisation is not set to true
                //Fire the event.
                SpellCheckEvent event = new BasicSpellCheckEvent(word, dictionary.getSuggestions(word,
                    threshold), tokenizer);
                terminated = fireAndHandleEvent(tokenizer, event);

              }
            }
          }
        }
        else {
          //This is a correctly spelt word. However perform some extra checks
/*JMH TBD          //Check for multiple words
          if (!ignoreMultipleWords &&) {
          }
*/
          //Check for capitalisation
          if ((!ignoreSentanceCapitalisation) && (tokenizer.isNewSentance())
              && (Character.isLowerCase(word.charAt(0)))) {
            StringBuffer buf = new StringBuffer(word);
            buf.setCharAt(0, Character.toUpperCase(word.charAt(0)));
            List suggestion = new LinkedList();
            suggestion.add(new Word(buf.toString(), 0));
            SpellCheckEvent event = new BasicSpellCheckEvent(word, suggestion,
                tokenizer);
            terminated = fireAndHandleEvent(tokenizer, event);
          }
        }
      }
    }
  }
}



