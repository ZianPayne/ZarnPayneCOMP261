import java.util.*;

public class BoyerMoore{

	public static int search(String pattern, String text) {
		// TODO fill this in.

		int uniqueCharacters = getUniqueCharacterCount(pattern);		// Sequences of methods to create bmt names
		HashMap<String, Integer> bmtNames = new HashMap<>();
		//char[] bmtNames = new char[uniqueCharacters + 1];
		createBMTNames(pattern, bmtNames);

		int[] bmt = new int[uniqueCharacters + 1];						// Sequence of initialisations and methods to create bmt
		createBMT(pattern, bmt, bmtNames);

		int[] gst = new int[pattern.length() + 1];							// Sequence creating the gst
		ArrayList<String> gstName = new ArrayList<>();
		createGST(pattern, gst, gstName);

		int cursor = pattern.length()-1;					// Cursor of search
		int m = pattern.length();							// Pattern length
		char[] charText = text.toCharArray();				// Char array of text
		char[] charPat = pattern.toCharArray();				// Char array of pattern


		while (cursor < charText.length){
			int k = 0;
			for (int i = charPat.length - 1; i >= 0; i--){				// Goes through pattern, finds match if 0

				String currentPat = String.valueOf(charPat[i]);
				String currentTxt = String.valueOf(charText[cursor - m + i + 1]);

				if (!currentPat.equals(currentTxt)){
					int bcText = getBadCharacterValue(bmt, bmtNames, currentTxt);	// Bad Character text shift
					int bcPat  = getBadCharacterValue(bmt, bmtNames, currentPat);	// Bad Character text shift

					int d1 = Math.max(bcText - k, 1);

					int d2 = gst[k];

					cursor += Math.max(d1,d2);
					break;
				}

				k++;
			}

			if (k == m) return cursor - k + 1;

		}

		return -1;
	}

	/** Creates the bmt's names for matching later
	 *
	 * @param pattern	pattern being matched
	 * @param bmtNames	table being created
	 */
	private static void createBMTNames(String pattern, HashMap<String, Integer> bmtNames){
		int i = 0;

		HashSet<Character> used = new HashSet<>();
		for (Character c : pattern.toCharArray()){
			if (!used.contains(c)) {
				bmtNames.put(String.valueOf(c), i);

				used.add(c);
				i++;
			}
		}
		bmtNames.put("*", i);
	}


	/** Creates a bmt based on a pattern and an array.
	 *
	 * @param pattern   The pattern which has a bmt created for it
	 * @param bmt		The BMT being made.
	 */
	private static void createBMT(String pattern, int[] bmt, Map<String, Integer> bmtNames){
		int m = pattern.length();				// Length of pattern
		char[] pat = pattern.toCharArray();		// Array of characters in pattern

		for (int i = 0; i < pat.length; i++){
			int index = bmtNames.get(String.valueOf(pat[i]));

			if (i == pattern.length() - 1){			// If last character
				if (bmt[index] != 0) break;				// Ignore if already exists
				bmt[index] = m;
				break;
			}

			bmt[index] = m - i - 1;
		}
		bmt[bmt.length-1] = m;
	}

	/** Creates the gst based on a pattern
	 *
	 * @param pattern The pattern used for the GST
	 * @param gst	  The gst being made
	 * @param gstName The names of each suffix
	 */
	private static void createGST(String pattern, int[] gst, ArrayList<String> gstName){
		int m = pattern.length();							    // Pattern length

		gst[0] = 1;
		gstName.add("");

		for(int i = 0; i < gst.length-1; i++){					// Creates the gst for the rest of the tree
			String suffix = pattern.substring(m - i - 1, m);	// Gets the relevant substring
			gstName.add(suffix);

			int jump = suffixMatch(pattern, suffix);			// Finds the jump to next possible suffix

			int suffixLength = suffix.length();
			while (jump == -1){
				if (suffixLength == 1) break;

				suffixLength--;
				suffix = pattern.substring(m - suffixLength, m);
				jump = suffixMatch(pattern, suffix);
			}

			if (jump == -1) gst[i+1] = m;
			else gst[i+1] = jump;

			//System.out.println(gstName.get(i));
		}
		//System.out.println(gstName.get(gst.length-1));
	}

	/** Generates the closest suffix match for first rule of GST
	 *
	 * @param pattern	pattern
	 * @param suf		prefix searched for
	 * @return			integer of matching pattern, -1 if no prev match
	 */
	private static int suffixMatch(String pattern, String suf){
		int m = pattern.length();	  // Pattern length
		int k = suf.length();		  // suffix length
		if (m - k - 1 < 0) return -1; // In the case of max

		String prevChar = pattern.substring(m - k - 1, m - k);

		for (int j = 1; j < m - k; j++){			// Goes from right to left, looking for a suffix match
			String cur = pattern.substring(m - j - k, m - j);
			if (cur.equals(suf) && m - j - 2 >= 0){
				String matchSuffix = (pattern.substring(m - j - 2, m - j - 1));
				if (!prevChar.equals(matchSuffix))return j;

			}
		}
		return -1;
	}

	/** Works out the shift from bad character rules
	 *
	 * @param bmt		bad matching table
	 * @param bmtNames  bad matching names, with corresponding indexes for bmt
	 * @param s			character being matched
	 * @return			returns shift value
	 */
	private static int getBadCharacterValue(int[] bmt, HashMap<String, Integer> bmtNames, String s){
		int shift = 0;

		if(bmtNames.get(s) == null) shift = bmt[bmt.length - 1];	// If not existing character
		else shift = bmt[bmtNames.get(s)];

		return shift;
	}


	/**Finds the number of unique chars in pattern
	 *
	 * @param str: The pattern in question
	 * @return:	the number of unique chars
	 */
	private static int getUniqueCharacterCount(String str){
		HashSet<Character> existing = new HashSet<>();

		for (Character c : str.toCharArray()){
			if (!existing.contains(c)) existing.add(c);
		}

		return existing.size();
	}

}
