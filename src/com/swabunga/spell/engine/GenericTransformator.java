package  com.swabunga.spell.engine;

import java.io.*;
import java.util.*;

/** A Generic implementation of a transformator takes an aspell
 *  phonetics file and constructs some sort of transformationtable using
 *  the inner class Rule.
 *
 * @author Robert Gustavsson (robert@lindesign.se)
 */
public class GenericTransformator implements Transformator{
    
    public static final char STARTMULTI='(';
    public static final char ENDMULTI=')';

    Object[] ruleArray=null;

    public GenericTransformator(File phonetic)throws IOException{
        buildRules(new BufferedReader(new FileReader(phonetic)));
    }

    /**
    * Returns the phonetic code of the word.
    */
    public String transform(String word) {       
        if(ruleArray==null)
            return null;
        TransformationRule rule;
        StringBuffer str=new StringBuffer(word.toUpperCase());
        int strLength=str.length();
        int startPos=0, add=1;

        while(startPos<strLength){
            //System.out.println("StartPos:"+startPos);
            add=1;
            for(int i=0;i<ruleArray.length;i++){
                //System.out.println("Testing rule#:"+i);
                rule=(TransformationRule)ruleArray[i];
                if(rule.startsWithExp() && startPos>0)
                    continue;
                if(startPos+rule.lengthOfMatch()>=strLength)
                    continue;
                if(rule.isMatching(str,startPos)){
                    str.replace(startPos,startPos+rule.getTakeOut(),rule.getReplaceExp());
                    add=rule.getReplaceExp().length();
                    strLength-=rule.getTakeOut();
                    strLength+=add;
                    //System.out.println("Replacing with rule#:"+i+" add="+add);
                    break;
                }
            }
            startPos+=add;
        }
        return str.toString();
    }

    // Used to build up the transformastion table.
    private void buildRules(BufferedReader in)throws IOException{
        String read=null;
        LinkedList ruleList=new LinkedList();
        while((read=in.readLine())!=null){
            buildRule(realTrimmer(read),ruleList);
        }
        ruleArray=ruleList.toArray();
    }
    
    // Here is where the real work of reading the phonetics file is done.
    private void buildRule(String str, LinkedList ruleList){
        if(str.length()<1)
            return;
        if(str.startsWith("version"))
            return;
        
        TransformationRule rule=null;
        StringBuffer matchExp=new StringBuffer();
        StringBuffer replaceExp=new StringBuffer();
        boolean start=false, end=false;
        int takeOutPart=0, matchLength=0;
        boolean match=true, inMulti=false;
        for(int i=0;i<str.length();i++){
            if(Character.isWhitespace(str.charAt(i))){
                match=false;
            }else{
                if(match){
                    if (!isReservedChar(str.charAt(i))){
                        matchExp.append(str.charAt(i));
                        if(!inMulti){
                            takeOutPart++;
                            matchLength++;
                        }
                        if(str.charAt(i)==STARTMULTI || str.charAt(i)==ENDMULTI)
                            inMulti=!inMulti;
                    }
                    if (str.charAt(i)=='-')
                        takeOutPart--;
                    if (str.charAt(i)=='^')
                        start=true;
                    if (str.charAt(i)=='$')
                        end=true;
                }else{
                    replaceExp.append(str.charAt(i));
                }
            }
        }
        rule=new TransformationRule(matchExp.toString(), replaceExp.toString()
                                        , takeOutPart, matchLength, start, end);
        ruleList.add(rule);
    }
    
    // Chars with special meaning to aspell. Not everyone is implemented here.
    private boolean isReservedChar(char ch){
        if(ch=='<' || ch=='>' || ch=='^' || ch=='$' || ch=='-' || Character.isDigit(ch))
            return true;
        return false;
    }

    // Trims off everything we don't care about.
    private String realTrimmer(String row){
        int pos=row.indexOf('#');
        if(pos!=-1){
            row=row.substring(0,pos);
        }
        return row.trim();
    }

    // Inner Classes
    /*
    * Holds the match string and the replace string and all the rule attributes.
    * Is responsible for indicating matches.
    */
    private class TransformationRule{

        private String replace;
        private char[] match;
        // takeOut=number of chars to replace; 
        // matchLength=length of matching string counting multies as one.
        private int takeOut, matchLength;
        private boolean start, end;

        // Construktor
        public TransformationRule(String match, String replace, int takeout
                                  , int matchLength, boolean start, boolean end){
            this.match=match.toCharArray();
            this.replace=replace;
            this.takeOut=takeout;
            this.matchLength=matchLength;
            this.start=start;
            this.end=end;
        }

        /*
        * Returns true if word from pos and forward matches the match string.
        * Precondition: wordPos+matchLength<word.length()
        */
        public boolean isMatching(StringBuffer word, int wordPos){
            boolean matching=true, inMulti=false, multiMatch=false;
            char matchCh;
            
            for(int matchPos=0;matchPos<match.length;matchPos++){
                matchCh=match[matchPos];
                if(matchCh==STARTMULTI || matchCh==ENDMULTI){
                    inMulti=!inMulti;
                    if(!inMulti)
                        matching=matching & multiMatch;
                    else
                        multiMatch=false;
                }else{
                    if(matchCh!=word.charAt(wordPos)){
                        if(inMulti)
                            multiMatch=multiMatch | false;
                        else
                            matching=false;
                    }else{
                        if(inMulti)
                            multiMatch=multiMatch | true;
                        else
                            matching=true;
                    }
                    if(!inMulti)
                        wordPos++;
                    if(!matching)
                        break;
                }
            }
            if(end && wordPos!=word.length()-1)
                matching=false;
            return matching;
        }

        public String getReplaceExp(){
            return  replace;
        }

        public int getTakeOut(){
            return takeOut;
        }

        public boolean startsWithExp(){
            return start;
        }
        
        public int lengthOfMatch(){
            return matchLength;
        }
        
        // Just for debugging purposes.
        public String toString(){
            return "Match:"+String.valueOf(match)
                   +" Replace:"+replace
                   +" TakeOut:"+takeOut
                   +" MatchLength:"+matchLength
                   +" Start:"+start
                   +" End:"+end;
        }

    }
}
