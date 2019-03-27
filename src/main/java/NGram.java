import java.sql.Array;
import java.util.*;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;

public class NGram {
	public static HashMap<String, Integer> OneGramHash = new HashMap<String, Integer>();
	public static HashMap<String, Integer> TwoGramHash = new HashMap<String, Integer>();
	public static HashMap<String, Integer> ThreeGramHash = new HashMap<String, Integer>();
	public static HashMap<String, Integer> FourGramHash = new HashMap<String, Integer>();

	// constructor that takes in list of words and the n-gram spec
	public NGram(int n, ArrayList<String> inputlist, int debug) {
		generateMap(n, inputlist, debug);
	}
	
	// overloaded constructor that additionally takes in list of core label tokens(from nlp)
	public NGram(int n, ArrayList<CoreLabel>tokenList, List<HashSet<String>>patterns) {
		generateMapForCoreLabel(n, tokenList, patterns);
	}
	
	// method to generate n-gram hash map given n and a list. Implements a Sliding
	// Window approach.
	// It uses recursion to iterate through the last remaining n elements of the
	// list
	private void generateMap(int n, ArrayList<String> list, int debug) {
		// error check
		if (list.size() == 0 || n <= 0)
			return;
		if (list.size() < n) {
			//System.out.println("ERROR: n-gram is bigger than the array list of tokens");
			n = list.size();	// let n=remaining size of list
		}

		String s = "";
		// base case
		if (n == 1) {
			s = list.get(list.size() - 1); // insert the last remaining element to hash
			insertToHash(s,0);
			return;
		}
		// insert all words at index i ... i+n to hash
		for (int i = 0; i < list.size() - n + 1; i++) {
			s = "";
			for (int j = 0; j < n; j++) {
				s = s + " " + list.get(i + j);
				insertToHash(s, j);
			}
		}
		// Now take care of the n remainder of the list
		int begin = list.size() - n + 1;
		int end = list.size();
		try {
			ArrayList<String> subList = new ArrayList<String>(list.subList(begin, end));
			this.generateMap(n - 1, subList, debug); // recurse
		}catch(Exception e) {System.out.println("ERROR HERE. listSize = " + list.size() + " n=" + n + " begin=" + begin + " end=" + end + " AT line index " + debug);}
		
	}
	
	// method for POS patterns
	// patterns is a list. Each element is a Hashset of tags.
	// for instance, at i = 0 => {NN, NNS, NNP, NNPS}, i=1 => {VB, VBD, VBG, VBN, VBP, VBZ}
	private void generateMapForCoreLabel(int n, ArrayList<CoreLabel> list, List<HashSet<String>>patterns) {
		// error check
		if (list.size() == 0 || n <= 0)
			return;
		if (list.size() < n) {
			//System.out.println("ERROR: n-gram is bigger than the array list of tokens");
			n = list.size();	// let n=remaining size of list
		}

		String s = "";
		CoreLabel tok = null;
		// base case
		if (n == 1) {
			tok = list.get(list.size() - 1); // insert the last remaining element to hash
			s = tok.get(TextAnnotation.class);
			insertToHash(s,0);
			return;
		}
		
		boolean isNextPattern = false;
		// insert all words at index i ... i+n to hash
		for (int i = 0; i < list.size() - n + 1; i++) {
			s = "";
			int patternIndex = 0;
			// if first token's tag does not belong to hashset, skip
			if (!patterns.get(patternIndex).contains(list.get(i).get(PartOfSpeechAnnotation.class)))
				continue;
			else isNextPattern = true;
			for (int j = 0; j < n; j++) {
				if (patternIndex < patterns.size()) {  // if pattern list continues
					// if pattern does not follow
					if (!patterns.get(patternIndex++).contains(list.get(i + j).get(PartOfSpeechAnnotation.class))) {
						isNextPattern = false;
						break; // break from loop, move on to next i
					}
					else {
						s = s + " " + list.get(i + j).get(TextAnnotation.class);	// concatenate string
						//insertToHash(s, j);
					}
				}
				else {
					if (isNextPattern == true) // we've reached pattern size, now check if boolean is satisfied
						insertToHash(s,j);	// add pattern strings so far
					
					s = s + " " + list.get(i + j).get(TextAnnotation.class);
					insertToHash(s, j);
				}
			}
		}
		// Now take care of the n remainder of the list
		int begin = list.size() - n + 1;
		int end = list.size();
		ArrayList<CoreLabel> subList = new ArrayList<CoreLabel>(list.subList(begin, end));
		this.generateMapForCoreLabel(n - 1, subList, patterns); // recurse
	}
	
	
	
	// method to insert an element into the hash map
	private void insertToHash(String instring, int ngramlength) {
		// choose which hash table to put entry in
		HashMap<String,Integer> hash = null;
		switch(ngramlength) {
		case 0: hash = OneGramHash; break;
		case 1: hash = TwoGramHash; break;
		case 2: hash = ThreeGramHash; break;
		case 3: hash = FourGramHash; break;
		default: break;}
		
		String s = instring;
		// get rid of plurals (i.e. banks and bank are the same. We only need one)
		if (instring.substring(instring.length() - 1).equals("s"))
			s = instring.substring(0, instring.length() - 1);

		if (hash.containsKey(s)) { // if key already exists, increment its val by 1
			int curr = hash.get(s);
			hash.put(s, curr + 1);
		} else {
			hash.put(s, 1);
		}
	}
	
	// method to sort all hash maps
	public void sortAllHash() {
		OneGramHash = sortByValue(OneGramHash);
		TwoGramHash = sortByValue(TwoGramHash);
		ThreeGramHash = sortByValue(ThreeGramHash);
		FourGramHash = sortByValue(FourGramHash);
	}

	// method that sorts local document hash map
	public HashMap<String, Integer> sortByValue(HashMap<String, Integer> hash) {
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(hash.entrySet());
		// Sort the list
		hashComparator hashCompare = new hashComparator();
		Collections.sort(list, hashCompare);

		// put data from sorted list to hashmap
		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	// method to print topics in hashTable
	public static void printTopics(HashMap<String, Integer> hash, int n) {
		System.out.println("----------------------------------NGrams------------------------------------");
		Iterator<Map.Entry<String, Integer>> itr = hash.entrySet().iterator();
		int count = 0;
		while (itr.hasNext() && count < 20) {
			Map.Entry<String, Integer >pair = itr.next();
			System.out.println(pair.getKey() + " = " + pair.getValue());
			count++;
		}
		System.out.println("----------------------------------END-----------------------------------");

	}


}
	
	
