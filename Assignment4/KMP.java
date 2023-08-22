import java.util.ArrayList;

/**
 * A new KMP instance is created for every substring search performed. Both the
 * pattern and the text are passed to the constructor and the search method. You
 * could, for example, use the constructor to create the match table and the
 * search method to perform the search itself.
 */
public class KMP {
	/**
	 * Perform KMP substring search on the given text with the given pattern.
	 * 
	 * This should return the starting index of the first substring match if it
	 * exists, or -1 if it doesn't.
	 */

	String pattern;
	String text;


	public static int search(String pattern, String text) {
		// TODO fill this in.

		/** Creating Data Structures and variables*/
		String[] s = pattern.split("");
		String[] t = text.split("");

		int[] jumpTable = new int[pattern.length()];
		matchTable(jumpTable, s);
		//System.out.println(jumpTable);

		int k = 0;		// start of match in text
		int i = 0;		// position of char in pattern

		while (k + i < text.length()){			// KMP search
			if(s[i].equals(t[k + i])){			// match
				i++;
				if (i == s.length) return k;
			}
			else if (jumpTable[i] == -1){		// No overlap or jump
				k = i + k + 1;
				i = 0;
			}
			else {								// There is an overlap
				k = i + k + 1;
				i = jumpTable[i];
			}
		}

		return -1;		// No match found
	}


	/** Creates the match table for KMP
	 *
	 * Args:
	 * m     ->    int[]:     stores the partial match table
	 * p     ->    String: 	  stores the pattern
	 * s     ->    String[]:  stores the split pattern
	 **/
	private static void matchTable(int[] m, String[] s){
		m[0] = -1;								// Initialising jump table
		m[1] = 0;

		int j = 0;								// Creating index location
		int pos = 2;							// Creating match table location

		while (pos < s.length){
			if (s[pos - 1].equals(s[j])){
				m[pos] = j+1;
				pos++;
				j++;
			}
			else if (j > 0){
				j = m[j];
			}
			else{
				m[pos] = 0;
				pos++;
			}
		}

	}

}


/** PAIR CLASS
 * 		Used for storing the pairs used in match tables.
 */
class Pair {
	String letter;
	Integer jump;

	public Pair (String key, Integer value){
		this.letter = key;
		this.jump = value;
	}

	/** Getter for letter */
	public String letter(){
		return this.letter;
	}

	/** Getter for jump */
	public Integer jump(){
		return this.jump;
	}

}