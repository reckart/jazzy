package com.swabunga.spell.jsp;


import java.util.LinkedList;


public class SpellerBean {

    private String word;
    boolean correct;
    private String code;
    private LinkedList suggestions = new LinkedList();

    public void setSuggestions(LinkedList suggestions) {
        this.suggestions = suggestions;
    }

    public LinkedList getSuggestions() {
        return suggestions;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean b) {
        correct = b;
    }
    /*
	public static void main(String[] args) {
		SpellerBean sbean = new SpellerBean();
		try {
		    if( args[0] == null ) {
			BufferedReader stdin = new BufferedReader( new InputStreamReader(System.in));
			String input = "";
			LinkedList results = null;
			while( input != null) {
				input = stdin.readLine();
				sbean.setWord(input);
				System.out.println("CODE: "+sbean.getCode());
				results = sbean.getSuggestions();
				if( results != null ) {
					for( int i = 0;i<results.size();i++) {
						System.out.println(results.get(i));
					}
				}
			}
		    }
		    BufferedReader fin = new BufferedReader( new FileReader(args[0]) );
		    String input = "";
		    while( input != null) {
			input = fin.readLine();
			StringTokenizer st = new StringTokenizer(input);
			while( st.hasMoreTokens() ) {
				sbean.setWord( st.nextToken() );
				sbean.getSuggestions();
			}
		    }
		} catch (Exception e ){
			e.printStackTrace();
		}
	}
	*/
}
