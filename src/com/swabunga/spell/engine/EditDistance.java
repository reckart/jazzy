package com.swabunga.spell.engine;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/** JMH Again, there is no need to have a global class matrix variable
 *  in this class. I have removed it and made the getDistance static final
 */
public class EditDistance {

	public static Configuration config = Configuration.getConfiguration();

    public static final int getDistance(String word, String similar) {
        int a_size = word.length() + 1;
        int b_size = similar.length() + 1;
        int[][] matrix = new int[a_size][b_size];
        matrix[0][0] = 0;
        for (int j = 1; j != b_size; ++j)
            matrix[0][j] = matrix[0][j - 1] + config.getInteger(Configuration.EDIT_DEL1);
        word = " " + word;
        similar = " " + similar;
        int te;
        for (int i = 1; i != a_size; ++i) {
            matrix[i][0] = matrix[i - 1][0] + config.getInteger(Configuration.EDIT_DEL2);
            for (int j = 1; j != b_size; ++j) {
                if (word.charAt(i) == similar.charAt(j)) {
                    matrix[i][j] = matrix[i - 1][j - 1];
                } else {
                    matrix[i][j] = config.getInteger(Configuration.EDIT_SUB) + matrix[i - 1][j - 1];
                    if (i != 1 && j != 1 &&
                            word.charAt(i) == similar.charAt(j - 1) && word.charAt(i - 1) == similar.charAt(j)) {
                        te = config.getInteger(Configuration.EDIT_SWAP) + matrix[i - 2][j - 2];
                        if (te < matrix[i][j]) matrix[i][j] = te;
                    }
                    te = config.getInteger(Configuration.EDIT_DEL1) + matrix[i - 1][j];
                    if (te < matrix[i][j]) matrix[i][j] = te;
                    te = config.getInteger(Configuration.EDIT_DEL2) + matrix[i][j - 1];
                    if (te < matrix[i][j]) matrix[i][j] = te;
                }
            }
        }
        return matrix[a_size - 1][b_size - 1];
    }

    public static void main(String[] args) throws Exception {
        EditDistance ed = new EditDistance();
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        String input2 = "";
        while (input != null) {
            input = stdin.readLine();
            input2 = stdin.readLine();
            System.out.println(ed.getDistance(input, input2));
        }
    }
}


