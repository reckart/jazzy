package com.swabunga.spell.event;

import java.text.BreakIterator;

/**
 * Defines common methods and behaviour for the various word finding
 * subclasses.
 * 
 * @author Anthony Roy  (ajr¤antroy.co.uk)
 */
public abstract class AbstractWordFinder
    implements WordFinder {

    //~ Instance/static variables .............................................

    protected Word currentWord       = new Word("", 0);
    protected Word nextWord          = new Word("", 0);
    protected boolean startsSentence = true;
    protected String text;
    protected BreakIterator sentenceIterator;

    //~ Constructors ..........................................................

    /**
     * Creates a new AbstractWordFinder object.
     * 
     * @param inText the String to iterate through.
     */
    public AbstractWordFinder(String inText) {
        text                         = inText;
        init();

        try {
            next();
        } catch (WordNotFoundException e) {
            currentWord = null;
            nextWord    = null;
        }
    }

    //~ Methods ...............................................................

    /**
     * This method scans the text from the end of the last word,  and returns
     * a new Word object corresponding to the next word.
     * 
     * @return the next word.
     */
    public abstract Word next();

    /**
     * Return the text being searched. May have changed since first set
     * through calls to replace.
     * 
     * @return the text being searched.
     */
    public String getText() {

        return text;
    }

    /**
     * Returns the current word in the iteration .
     * 
     * @return the current word.
     * @throws WordNotFoundException current word has not yet been set.
     */
    public Word current() {

        if (currentWord == null) {
            throw new WordNotFoundException("No Words in current String");
        }

        return currentWord;
    }

    /**
     * @return true if there are further words in the string.
     */
    public boolean hasNext() {

        return nextWord != null;

    }

    /**
     * Replace the current word in the search with a replacement string.
     * 
     * @param newWord the replacement string.
     * @throws WordNotFoundException current word has not yet been set.
     */
    public void replace(String newWord) {

        if (currentWord == null) {
            throw new WordNotFoundException("No Words in current String");
        }

        StringBuffer sb = new StringBuffer(text.substring(0, 
                                                          currentWord.getStart()));
        sb.append(newWord);
        sb.append(text.substring(currentWord.getEnd()));
        int diff = newWord.length() - currentWord.getText().length();
        currentWord.setText(newWord);
        /* Added Conditional to ensure a NullPointerException is avoided (11 Feb 2003) */
        if (nextWord != null){
          nextWord.setStart(nextWord.getStart() + diff);
        }
        text = sb.toString();
        
        sentenceIterator.setText(text);
        int start = currentWord.getStart();
        sentenceIterator.following(start);
        startsSentence = sentenceIterator.current() == start;

    }

    /**
     * @return true if the current word starts a new sentence.
     * @throws WordNotFoundException current word has not yet been set.
     */
    public boolean startsSentence() {

        if (currentWord == null) {
            throw new WordNotFoundException("No Words in current String");
        }

        return startsSentence;
    }

    /**
     * Return the text being searched. May have changed since first set
     * through calls to replace.
     * 
     * @return the text being searched.
     */
    public String toString() {

        return text;
    }

    protected void setSentenceIterator(Word wd){
           int current = sentenceIterator.current();

      if (current == currentWord.getStart())
        startsSentence = true;
      else {
        startsSentence = false;

        if (currentWord.getEnd() > current) {
          sentenceIterator.next();
        }
      }
    }
    
    //Added more intelligent character recognition (11 Feb '03)
    protected boolean isWordChar(int posn) {
         boolean out = false;

         char curr = text.charAt(posn);
         
         if ((posn == 0) || (posn == text.length()-1)) {
           return Character.isLetterOrDigit(curr);
         }
         
         char prev = text.charAt(posn -1);
         char next = text.charAt(posn +1);
        
         
         switch (curr){
           case '\'': 
           case '@' :
           case '.' :
           case '_' :
                      out = (Character.isLetterOrDigit(prev)&&Character.isLetterOrDigit(next));
                      break;
           default  : out = Character.isLetterOrDigit(curr);
         }
         
        return out;
    }

    protected boolean isWordChar(char c) {
        boolean out = false;

        if (Character.isLetterOrDigit(c) || (c == '\'')) {
            out = true;
        }

        return out;
    }

    protected int ignore(int index, char startIgnore) {
        int newIndex = index;

        if (newIndex < text.length()) {
            char curChar = text.charAt(newIndex);

            if (curChar == startIgnore) {

                while ((++newIndex) < text.length()) {
                    curChar = text.charAt(newIndex);

                    if (!Character.isLetterOrDigit(curChar)) {

                        break;
                    }
                }
            }
        }

        return newIndex;
    }

    protected int ignore(int index, char startIgnore, char endIgnore) {
        int newIndex = index;

        if (newIndex < text.length()) {
            char curChar = text.charAt(newIndex);

            if (curChar == startIgnore) {

                while ((++newIndex) < text.length()) {
                    curChar = text.charAt(newIndex);

                    if (curChar == endIgnore) {

                        break;
                    }
                }
            }
        }

        return newIndex;
    }

    protected int ignore(int index, String startIgnore, String endIgnore) {

        //{{{
        int newIndex = index;
        int len      = text.length();
        int slen     = startIgnore.length();
        int elen     = endIgnore.length();

        if (!((newIndex + slen) >= len)) {
            String seg = text.substring(newIndex, newIndex + slen);

            //            System.out.println(seg + ":" + seg.length()+ ":" + startIgnore + ":" + slen);
            if (seg.equals(startIgnore)) {
                newIndex += slen;
cycle:          while (true) {

                    if (newIndex == (text.length() - elen)) {

                        break cycle;
                    }

                    String ss = text.substring(newIndex, newIndex + elen);

                    if (ss.equals(endIgnore)) {
                        newIndex += elen;

                        break cycle;
                    } else {
                        newIndex++;
                    }
                }
            }
        }

        return newIndex;
    } //}}}

    protected void init() {
      sentenceIterator = BreakIterator.getSentenceInstance();
      sentenceIterator.setText(text);
    }
}
