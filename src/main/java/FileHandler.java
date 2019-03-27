import java.io.*;
import java.util.*;

public class FileHandler {
	List<String> records;	// stores each tweet's text message
	
	public FileHandler(String filename) {
		records = new ArrayList<String>();
		read_file(filename);
	}
	
	private void read_file(String filename) {
		Scanner input;
		try {
			input = new Scanner(new FileReader(filename));
			while (input.hasNextLine()) {
				String token = input.nextLine();
				records.add(getTextFromLine(token));
			}
			input.close();
		}
		catch(IOException e){ System.out.println("ERROR: unable to read file!");}
	}
	
	private String getTextFromLine(String line) {
		List<String> words = new ArrayList<String>();
		try {
			Scanner input = new Scanner(line);
			input.useDelimiter(",");
			while (input.hasNext()) {
				words.add(input.next());
			}
			input.close();
		}catch (Exception e) {System.out.println("ERROR: reading text from line");}
		
		String text = "";
		for (int i=5; i<words.size(); i++) {
			text += words.get(i);
		}
		return text;
	}
	
	// method to convert a string record list to a list of string (To be used for NGram)
	public List<String> convertStringtoListToken(String intext){
		String text = intext.replaceAll("[^a-zA-Z @#]", "");
		List<String> stringlist = new ArrayList<String>();
		
		Scanner input = new Scanner(text);
		while (input.hasNext()) {
			stringlist.add(input.next());
		}
		return stringlist;
		
	}

}
