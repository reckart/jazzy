package com.swabunga.spell.engine;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class is based on Levenshtein Distance algorithms, and it calculates how similar two words are. 
 * If the words are identitical, then the distance is 0. The more that the words have in common, the lower the distance value.
 * The distance value is based on how many operations it takes to get from one word to the other. Possible operations are  
 * swapping characters, adding a character, deleting a character, and substituting a character.  
 * The resulting distance is the sum of these operations weighted by their cost, which can be set in the Configuration object.
 * When there are multiple ways to convert one word into the other, the lowest cost distance is returned.
 * <br/> 
 * Another way to think about this: what are the cheapest operations that would have to be done on the "original" word to end up 
 * with the "similar" word? Each operation has a cost, and these are added up to get the distance.
 * <br/>
 *  
 * @see com.swabunga.spell.engine.Configuration#COST_REMOVE_CHAR
 * @see com.swabunga.spell.engine.Configuration#COST_INSERT_CHAR
 * @see com.swabunga.spell.engine.Configuration#COST_SUBST_CHARS
 * @see com.swabunga.spell.engine.Configuration#COST_SWAP_CHARS
 * 
 */

public class EditDistance {

	/** 
	 * JMH Again, there is no need to have a global class matrix variable
	 *  in this class. I have removed it and made the getDistance static final
	 * 
	 * DMV: I refactored this method to make it more efficient, more readable, and simpler. 
	 * I also fixed a bug with how the distance was being calculated. You could get wrong
	 * distances if you compared ("abc" to "ab") depending on what you had setup your 
	 * COST_REMOVE_CHAR and EDIT_INSERTION_COST values to - that is now fixed.  
	 */

	public static Configuration config = Configuration.getConfiguration();

	public static final int getDistance(String word, String similar) {

		//get the weights for each possible operation       
		final int costOfDeletingSourceCharacter = config.getInteger(Configuration.COST_REMOVE_CHAR);
		final int costOfInsertingSourceCharacter = config.getInteger(Configuration.COST_INSERT_CHAR);
		final int costOfSubstitutingLetters = config.getInteger(Configuration.COST_SUBST_CHARS);
		final int costOfSwappingLetters = config.getInteger(Configuration.COST_SWAP_CHARS);

		int a_size = word.length() + 1;
		int b_size = similar.length() + 1;
		int[][] matrix = new int[a_size][b_size];
		matrix[0][0] = 0;

		for (int i = 1; i != a_size; ++i) 
			matrix[i][0] = matrix[i - 1][0] + costOfInsertingSourceCharacter; //initialize the first column

		for (int j = 1; j != b_size; ++j)
			matrix[0][j] = matrix[0][j - 1] + costOfDeletingSourceCharacter; //initalize the first row
            
		word = " " + word;
		similar = " " + similar;
        
		for (int i = 1; i != a_size; ++i) {
            char sourceChar = word.charAt(i);
			for (int j = 1; j != b_size; ++j) {

				char otherChar = similar.charAt(j);
				if (sourceChar == otherChar) {
					matrix[i][j] = matrix[i - 1][j - 1]; //no change required, so just carry the current cost up
					continue;
				} 
                
				int costOfSubst = costOfSubstitutingLetters + matrix[i - 1][j - 1];
				//if needed, add up the cost of doing a swap
				int costOfSwap = Integer.MAX_VALUE;
				boolean isSwap = (i != 1) && (j != 1) && sourceChar == similar.charAt(j - 1) && word.charAt(i - 1) == otherChar;
				if (isSwap) 
					costOfSwap = costOfSwappingLetters + matrix[i - 2][j - 2];
				int costOfDelete = costOfDeletingSourceCharacter + matrix[i][j-1];
				int costOfInsertion = costOfInsertingSourceCharacter + matrix[i-1][j];
					
				matrix[i][j] = minimum(costOfSubst,costOfSwap,costOfDelete,costOfInsertion);
			}
		}
		int cost = matrix[a_size - 1][b_size - 1]; 
		
		if (false)
			System.out.println(dumpMatrix(word, similar, matrix));
		
		return cost;
	}
	
	/**
	 * For debugging, this creates a string that represents the matrix. To read the matrix, look at any square. That is the cost to get from 
	 * the partial letters along the top to the partial letters along the side. 
	 * @param src - the source string that the matrix columns are based on
	 * @param dest - the dest string that the matrix rows are based on
	 * @param matrix - a two dimensional array of costs (distances)
	 * @return String
	 */
	static private String dumpMatrix(String src, String dest, int matrix[][])
	{
		StringBuffer s= new StringBuffer("");
		
		int cols = matrix.length;
		int rows = matrix[0].length; 
		
		for (int i = 0; i < cols+1; i++)
		{
			for (int j = 0; j < rows+1; j++)
			{
				if (i == 0 && j == 0)
				{
					s.append(" ");
					continue;

				}
				if (i == 0)
				{
					s.append("|   ");
					s.append(dest.charAt(j-1));
					continue;
				}
				if (j == 0)
				{
					s.append(src.charAt(i-1));
					continue;
				}
				String num = Integer.toString(matrix[i-1][j-1]);
				int padding = 4 - num.length();
				s.append("|");
				for (int k = 0; k < padding; k++ )
					s.append(' ');
				s.append(num);
			}
			s.append('\n');
		}
		return s.toString();
		
	}
	
	static private int minimum(int a, int b, int c, int d)
	{
		int mi = a;
		if (b < mi)
			mi = b;
		if (c < mi)
			mi = c;
		if (d < mi)
			mi = d;
		return mi;
	}
	
    public static void main(String[] args) throws Exception {
		BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        
		while (true) {

			String input1 = stdin.readLine();
			if (input1 == null || input1.length() == 0)
				break;

			String input2 = stdin.readLine();
			if (input2 == null || input2.length() == 0)
				break;

			System.out.println(EditDistance.getDistance(input1, input2));
		}
		System.out.println("done");
    }
}


