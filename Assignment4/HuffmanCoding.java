/**
 * A new instance of HuffmanCoding is created for every run. The constructor is
 * passed the full text to be encoded or decoded, so this is a good place to
 * construct the tree. You should store this tree in a field and then use it in
 * the encode and decode methods.
 */

import java.util.*;
public class HuffmanCoding {

	HuffmanNode root = null;


	/**
	 * This would be a good place to compute and store the tree.
	 */
	public HuffmanCoding(String text) {
		// TODO fill this in.

		// TODO I personally did not like this, I used my own methods

		//encode(text);

	}

	/**
	 * Take an input string, text, and encode it with the stored tree. Should
	 * return the encoded text as a binary string, that is, a string containing
	 * only 1 and 0.
	 *
	 * It will also store the root node of the huffman tree
	 */
	public String encode(String text) {
		// TODO fill this in.

		PriorityQueue<HuffmanNode> q = new PriorityQueue<>(new HuffmanComparator());
		Map<Character, Integer> charFreq = findFreq(text);
		Map<Character, String> charCodes = new HashMap<>();
		StringBuilder encodedText = new StringBuilder();

		for(Map.Entry<Character, Integer> freq : charFreq.entrySet()){	// Add to queue
			q.add(new HuffmanNode(freq.getKey(), freq.getValue()));
		}

		while(q.size() > 1){				 // Creates the tree
			HuffmanNode node1 = q.poll();
			HuffmanNode node2 = q.poll();
			HuffmanNode newNode = new HuffmanNode(node1.aChar, node1.freq + node2.freq, node1, node2);

			q.add(newNode);
		}
		findCodes(q.peek(), charCodes);
		this.root = q.peek();       		// Stores the huffman tree as a field

		int ctr = 0;
		for (Character c : text.toCharArray()){
			//System.out.println(charCodes.get(c));
			encodedText.append(charCodes.get(c));
			ctr++;
		}

		return encodedText.toString();
	}

	/** Takes text and returns frequencies for each character. Splits the text
	 * and counts each occurrence.
	 *
	 * Args:
	 * t 	-> String:  				 Text undergoing frequency analysis
	 * freq -> Hashmap<String, Integer>: Frequencies for each char
	 * split-> String[]:			     Each individual character (in string form)
	 *
	 * */
	private HashMap<Character, Integer> findFreq(String t){
		String[] split = t.split("");
		HashMap<Character, Integer> freq = new HashMap<Character, Integer>();

		for(String s : split){				// Iterates through split, adding all values
			freq.put(s.charAt(0), (freq.get(s.charAt(0)) != null ? freq.get(s.charAt(0)) + 1: 1));
		}

		return freq;
	}

	/** Takes the final node and finds all the relevant codes with a dfs.
	 *
	 */
	private void findCodes(HuffmanNode root, Map<Character, String> map){
		String code = "";
		if (root.left != null) findCodes(root.left, map, code.concat("0"));
		if (root.right != null) findCodes(root.right, map, code.concat("1"));
	}

	private void findCodes(HuffmanNode node, Map<Character, String>  map, String code){

		if (node.left == null && node.right == null){
			map.put(node.aChar, code);
		}
		else{
			findCodes(node.left, map, code.concat("0"));
			findCodes(node.right, map, code.concat("1"));
		}

	}

	/**
	 * Take encoded input as a binary string, decode it using the stored tree,
	 * and return the decoded text as a text string.
	 */
	public String decode(String encoded) {
		// TODO fill this in.
		String decoded = "";							// Stores decoded string
		StringBuilder output = new StringBuilder();
		char[] encodedArray = encoded.toCharArray();


		HuffmanNode current = this.root;				// Stores the root node as initial one
		StringBuilder currentCode = new StringBuilder();


		for (int i = 0; i < encodedArray.length; i++){	// for loop goes through tree, adding characters when reaching leaf node

			if(encodedArray[i] == '0'){					// If going left
				current = current.left;
			}
			else {
				current = current.right;
			}

			if (current.left == null && current.right == null){	            // If leaf node
				//decoded = decoded.concat(String.valueOf(current.aChar));
				output.append(String.valueOf(current.aChar));
				current = this.root;
				currentCode = new StringBuilder();
			}
			currentCode.append(String.valueOf(encodedArray[i]));

		}


		return output.toString();
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't want to. It is called on every run and its return
	 * value is displayed on-screen. You could use this, for example, to print
	 * out the encoding tree.
	 */
	public String getInformation() {
		return "";
	}
}

/**
 * Binary nodes storing information on the Huffman Tree.
 *
 * Will store left and right children (in alternative constructor), char as well as a frequency value.
 */
class HuffmanNode{
	int freq;
	char aChar;

	HuffmanNode left = null;
	HuffmanNode right = null;

	HuffmanNode(Character c, int f){
		this.freq = f;
		this.aChar = c;
	}

	HuffmanNode(Character c, int f, HuffmanNode left, HuffmanNode right){
		this.freq = f;
		this.aChar = c;
		this.left = left;
		this.right = right;
	}

}

/**
 * Comparator for the huffman tree's priority queue.
 *
 * Here it will be on the basis of the frequency value in the huffman nodes.
 */
class HuffmanComparator implements Comparator<HuffmanNode>{

	public int compare(HuffmanNode x, HuffmanNode y){
		if(x.freq == y.freq) {						// Equal frequencies, compare chars.
			return Character.compare(x.aChar,y.aChar);
		}
		else return x.freq-y.freq;					// Prioritises smaller ones
	}

}