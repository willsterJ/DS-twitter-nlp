import java.util.*;

import org.apache.log4j.BasicConfigurator;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

public class Default {

	public static void obtainTop20Mentions(FileHandler test) {
		TopMentions top = new TopMentions(test.records);
		top.writeToFile(top.ATMentionsHash, "./top@.txt");
		top.writeToFile(top.HashtagMentionsHash, "./top#.txt");
	}
	
	public static void report20MostFrequentNGram(FileHandler file) {
		List<String> temp = null;
		NGram ngram = null;
		for (int i=0; i<file.records.size(); i++) {	// for each record string
			temp = file.convertStringtoListToken(file.records.get(i));
			ngram = new NGram(4, (ArrayList<String>)temp, i);
		}
		
		ngram.sortAllHash();
		
		NGram.printTopics(NGram.OneGramHash, 20);
		NGram.printTopics(NGram.TwoGramHash, 20);
		NGram.printTopics(NGram.ThreeGramHash, 20);
		NGram.printTopics(NGram.FourGramHash, 20);
	}
	
	// method to initialize core nlp only once. For performance reasons.
	static StanfordCoreNLP pipeline;
	public static void initializeCoreNLP() {
		// config to remove console loggging
		RedwoodConfiguration.current().clear().apply();
		
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
        // NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos,lemma,ner");
        pipeline = new StanfordCoreNLP(props);
	}
	
	// You must manually add patterns to a set as shown below
	public static void POSPattern(FileHandler file) {
		List<HashSet<String>> patterns = new ArrayList<HashSet<String>>();
		
		HashSet<String> setAdjectives = new HashSet<String>(Arrays.asList("JJ", "JJR", "JJS"));
		HashSet<String> setNouns = new HashSet<String>(Arrays.asList("NN", "NNS", "NNP", "NNPS"));
		HashSet<String> setVerbs = new HashSet<String>(Arrays.asList("VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
		HashSet<String> setAdverbs = new HashSet<String>(Arrays.asList("RB", "RBR", "RBS"));
		//patterns.add(setAdjectives);
		patterns.add(setAdverbs);
		
		//patterns.add(setNouns);
		patterns.add(setVerbs);
		
		CoreNLPHandler handler = null;
		NGram ngram = null;
		for (int i=0; i< file.records.size(); i++) {
			handler = new CoreNLPHandler(file.records.get(i), pipeline);
			ngram = new NGram(4, handler.outputtokenList, patterns);
		}
		
		ngram.sortAllHash();
		
		NGram.printTopics(NGram.OneGramHash, 10);
		NGram.printTopics(NGram.TwoGramHash, 10);
		NGram.printTopics(NGram.ThreeGramHash, 10);
		NGram.printTopics(NGram.FourGramHash, 10);
	}
	
	public static void main(String[] args) {
		BasicConfigurator.configure(); // setup for Stanford CoreNLP to work

		FileHandler file = new FileHandler("sentiment140.csv");
		
		
		// Here are the assigned tasks:
		
		//obtainTop20Mentions(file);
		//report20MostFrequentNGram(file);
		
		System.out.println("adverb verb");
		initializeCoreNLP();
		POSPattern(file);
		
		
		
	}

}
