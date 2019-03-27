/*
 * Class that extracts top @ and # mentions
 */
import java.io.PrintWriter;
import java.util.*;

enum whichHash {	// define enum to be used for hashing
	At, Hashtag, Entity;
}

public class TopMentions {
	List<String> records; // records obtained from input
	HashMap<String, Integer> ATMentionsHash;  // hash storing @ mentions
	HashMap<String, Integer> HashtagMentionsHash;  // hash storing # mentions
	public static HashMap<String, Integer> EntityHash = new HashMap<String, Integer>();

	public TopMentions(List<String> records) {
		this.records = records;
		ATMentionsHash = new HashMap<String, Integer>();
		HashtagMentionsHash = new HashMap<String, Integer>();
		searchMentions();
		ATMentionsHash = sortHashTables(ATMentionsHash);
		HashtagMentionsHash = sortHashTables(HashtagMentionsHash);
		ATMentionsHash = getTopAT(20);
		HashtagMentionsHash = getTopHtag(20);
	}
	
	public TopMentions(List<String> entityList, String command) {
		generateEntities(entityList);
	}
	
	// method to search list of records for @ or #
	private void searchMentions() {
		for (int i = 0; i < records.size(); i++) {
			String line = records.get(i);
			if (line.contains("@") || line.contains("#")) {
				// extract @ or #, and put it in hash map
				getMention(line.replaceAll("\"", ""));	//some @ come included with "@. Remove as necessary
			}
		}
	}

	// method to extract the mention handle name
	private void getMention(String line) {
		Scanner input = new Scanner(line);
		String token = "";

		while (input.hasNext()) {
			token = input.next();
			if (token.contains("@") && token.length() > 1)
				addToHashMap(token, whichHash.At);
			if (token.contains("#") && token.length() > 1)
				addToHashMap(token, whichHash.Hashtag);
		}

		input.close();
	}

	// method to add @ or # mentions to hash map
	private void addToHashMap(String mention, whichHash hashChoice) {
		if (hashChoice.equals(whichHash.At)) {
			if (ATMentionsHash.containsKey(mention)) { // if mention exists, increment value in hash
				int val = ATMentionsHash.get(mention) + 1;
				ATMentionsHash.put(mention, val);
			} else { // if not, create new entry
				ATMentionsHash.put(mention, 1);
			}
		} else if (hashChoice.equals(whichHash.Hashtag)) {
			if (HashtagMentionsHash.containsKey(mention)) {
				int val = HashtagMentionsHash.get(mention) + 1;
				HashtagMentionsHash.put(mention, val);
			} else {
				HashtagMentionsHash.put(mention, 1);
			}
		}
		else {
			if (EntityHash.containsKey(mention)) {
				int val = EntityHash.get(mention) + 1;
				EntityHash.put(mention, val);
			} else
				EntityHash.put(mention, 1);
		}
	}

	// method to filter results -- OBSOLETE
	private void filterMentionsBy(int num) {
		Iterator<Map.Entry<String, Integer>> itr = ATMentionsHash.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, Integer> pair = itr.next();
			if (pair.getValue() < num)
				itr.remove();
		}

		Iterator<Map.Entry<String, Integer>> itr2 = HashtagMentionsHash.entrySet().iterator();
		while (itr2.hasNext()) {
			Map.Entry<String, Integer> pair = itr2.next();
			if (pair.getValue() < num)
				itr2.remove();
		}
	}

	// method to reverse-sort the hash table
	public HashMap<String, Integer> sortHashTables(HashMap<String, Integer> hash) {
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

	// method to get top @ hash entries. Return the hash map with the results
	private HashMap<String, Integer> getTopAT(int n) {
		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		int count = 0;

		Iterator<Map.Entry<String, Integer>> itr = ATMentionsHash.entrySet().iterator();
		while (itr.hasNext() && count < 20) {
			Map.Entry<String, Integer> pair = itr.next();
			temp.put(pair.getKey(), pair.getValue());
			count++;
		}
		return temp;
	}

	// method to get top # hash entries. Return the hash map with the results
	private HashMap<String, Integer> getTopHtag(int n) {
		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		int count = 0;

		Iterator<Map.Entry<String, Integer>> itr = HashtagMentionsHash.entrySet().iterator();
		while (itr.hasNext() && count < 20) {
			Map.Entry<String, Integer> pair = itr.next();
			temp.put(pair.getKey(), pair.getValue());
			count++;
		}
		return temp;
	}

	// method to write hash entries to a .txt file
	public void writeToFile(HashMap<String, Integer> hash, String destination) {
		try {
			PrintWriter writer = new PrintWriter(destination, "UTF-8");
			Iterator<Map.Entry<String, Integer>> itr = hash.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, Integer> entry = itr.next();
				writer.println(entry.getKey() + " " + entry.getValue());
			}
			writer.close();
		} catch (Exception e) {
		}
	}
	
	// method to generate entities hashmap
	private void generateEntities(List<String> list) {
		for (String s : list) {
			addToHashMap(s, whichHash.Entity);
		}
	}
	// print top entity mentions
	public static void printTopEntities() {
		int count = 0;
		Iterator<Map.Entry<String, Integer>> itr = EntityHash.entrySet().iterator();
		while (itr.hasNext() && count < 10) {
			Map.Entry<String, Integer> entry = itr.next();
			System.out.println(entry.getKey() + ": " + entry.getValue());
			count++;
		}
	}

}

//implement a comparator class
class hashComparator implements Comparator<Map.Entry<String, Integer>> {
	public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
		return o2.getValue().compareTo(o1.getValue());
	}
}
