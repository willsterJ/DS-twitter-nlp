import java.util.*;

import org.apache.log4j.BasicConfigurator;

import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

public class Default {
	
	// functions ---------------------------------------------------------------------------
	
	// obtain top @ and # mentions
	public static void obtainTop20Mentions(FileHandler test) {
		TopMentions top = new TopMentions(test.records);
		top.writeToFile(top.ATMentionsHash, "./top@.txt");
		top.writeToFile(top.HashtagMentionsHash, "./top#.txt");
	}
	
	// get top 20 4,3,2,1 ngrams
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
	
	// method to find patterns and extract ngrams 
	// You must manually add patterns to a set as shown below
	public static void POSPattern(FileHandler file) {
		List<HashSet<String>> patterns = new ArrayList<HashSet<String>>();
		
		HashSet<String> setAdjectives = new HashSet<String>(Arrays.asList("JJ", "JJR", "JJS"));
		HashSet<String> setNouns = new HashSet<String>(Arrays.asList("NN", "NNS", "NNP", "NNPS"));
		HashSet<String> setVerbs = new HashSet<String>(Arrays.asList("VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
		HashSet<String> setAdverbs = new HashSet<String>(Arrays.asList("RB", "RBR", "RBS"));
		//patterns.add(setAdjectives);
		//patterns.add(setAdverbs);
		//patterns.add(setNouns);
		patterns.add(setVerbs);
		patterns.add(setNouns);
		
		CoreNLPHandler handler = null;
		NGram ngram = null;
		for (int i=0; i< file.records.size(); i++) {
			handler = new CoreNLPHandler(file.records.get(i), pipeline, "");
			ngram = new NGram(4, handler.outputtokenList, patterns);
		}
		
		ngram.sortAllHash();
		
		NGram.printTopics(NGram.OneGramHash, 10);
		NGram.printTopics(NGram.TwoGramHash, 10);
		NGram.printTopics(NGram.ThreeGramHash, 10);
		NGram.printTopics(NGram.FourGramHash, 10);
	}
	
	// method to do dependency parsing
	public static void dependencyParser(FileHandler file) {
		String modelPath = DependencyParser.DEFAULT_MODEL;
		String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";

		MaxentTagger tagger = new MaxentTagger(taggerPath);
		DependencyParser parser = DependencyParser.loadFromModelFile(modelPath);
		
		CoreNLPHandler handler = null;
		
		handler = new CoreNLPHandler(true, file.records.get(0), tagger, parser);
		handler = new CoreNLPHandler(true, file.records.get(1), tagger, parser);
	}
	
	// method to get top named-entities
	public static void topEntities(FileHandler file) {
		CoreNLPHandler handler = null;
		TopMentions entities = null;
		for (int i=0; i< file.records.size(); i++) {
			handler = new CoreNLPHandler(file.records.get(i), pipeline, "entities");
			entities = new TopMentions(handler.entityList, "entities");
		}
		TopMentions.EntityHash = entities.sortHashTables(TopMentions.EntityHash);
		TopMentions.printTopEntities();
	}
	
	// top named-entities, but with sentiments
	public static void topEntitiesWithSentiments(FileHandler file, int sentiment) {
		file.filterRecordsBySentiment(sentiment);
		CoreNLPHandler handler = null;
		TopMentions entities = null;
		System.out.println("~~~~~~~~~~~~~~Top Entities by sentiment=" + sentiment + "~~~~~~~~~~~~~~~~~");
		
		if (file.records.size() == 0) {
			System.out.println("\n ERROR: Not enough records with that sentiment");
			return;
		}
		for (int i=0; i< file.records.size(); i++) {
			handler = new CoreNLPHandler(file.records.get(i), pipeline, "entities");
			entities = new TopMentions(handler.entityList, "entities");
		}
		TopMentions.EntityHash = entities.sortHashTables(TopMentions.EntityHash);
		TopMentions.printTopEntities();
	}
	
	// main-------------------------------------------------------------------------------
	
	public static void main(String[] args) {
		BasicConfigurator.configure(); // setup for Stanford CoreNLP to work

		FileHandler file = new FileHandler("sentiment140.csv");
		
		
		// Here are the assigned tasks:
		
		//obtainTop20Mentions(file);
		//report20MostFrequentNGram(file);
		
		//System.out.println("verb noun");
		initializeCoreNLP();
		//POSPattern(file);
		//dependencyParser(file);
		//topEntities(file);
		topEntitiesWithSentiments(file, 4);
		
		
	}

}
