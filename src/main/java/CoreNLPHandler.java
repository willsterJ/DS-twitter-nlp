import java.io.*;
import java.util.*;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.parser.nndep.demo.DependencyParserDemo;
import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations.DependencyAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.DependencyParseAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.util.logging.Redwood;

public class CoreNLPHandler {

	public ArrayList<CoreLabel> outputtokenList;
	public ArrayList<String> entityList;

	public CoreNLPHandler(String input, StanfordCoreNLP pipeline, String command) {
		_doStanfordNLP(input.replaceAll("[^a-zA-Z0-9 @#:/)(]", ""), pipeline);
		if (command.equals("entities"))
			combineSameNER();
		// printWords();
		// printPOS();
		// printNER();
	}
	// overloaded constructor used for dependency parsing
	public CoreNLPHandler(boolean depenency, String input, MaxentTagger tagger, DependencyParser parser) {
		_doStanfordDependency(input, tagger, parser);
	}

	// method to tokenise and apply nlp to string
	private void _doStanfordNLP(String text, StanfordCoreNLP pipeline) {
		// create an empty Annotation just with the given text
		// Annotation document = new Annotation(text); // this is from the tutorial.
		// However, to get NER to work use CoreDocument
		CoreDocument document = new CoreDocument(text);
		// run all Annotators on this text
		pipeline.annotate(document);

		List<CoreLabel> tokenList = new ArrayList<CoreLabel>();

		for (CoreSentence sentence : document.sentences()) {
			// traversing the words in the current sentence
			for (CoreLabel token : sentence.tokens()) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				tokenList.add(token);
			}
		}
		outputtokenList = (ArrayList<CoreLabel>) tokenList; // set final output token list
	}

	// Dependencies Parser
	private void _doStanfordDependency(String text, MaxentTagger tagger, DependencyParser parser) {

		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		for (List<HasWord> sentence : tokenizer) {
			List<TaggedWord> tagged = tagger.tagSentence(sentence);
			GrammaticalStructure gs = parser.predict(tagged);
			// Print typed dependencies
			System.out.println(gs.toString());
		}
	}
	
	// method that combines tokens of the same NER
		private void combineSameNER() {
			ArrayList<String> newList = new ArrayList<String>();
			String prevNER = "";	// previous NER tag
			String ss = "";		// accumulator string for combination
			
			// create empty token and add to list so that the for-loop can look one iteration ahead
			CoreLabel empty = new CoreLabel();
			empty.set(TextAnnotation.class, " ");
			empty.set(NamedEntityTagAnnotation.class, " ");
			outputtokenList.add(empty);
			
			for (int i=0; i<outputtokenList.size(); i++) {
				String s = outputtokenList.get(i).get(TextAnnotation.class);
				String ner = outputtokenList.get(i).get(NamedEntityTagAnnotation.class);
				// if ner is O (i.e. regular word), skip
				if (ner.equals("O")) {
					//newList.add(s);
					prevNER = "O";
				}
				// for entity words, concatenate same entity words to form new word. Then add it to newList
				else {
					if (ner.equals(prevNER)){
						ss = ss.concat("_" + s);
						// if next words has a different ner, end ss accumulation and add to list
						if (!ner.equals(outputtokenList.get(i+1).get(NamedEntityTagAnnotation.class))) {
							newList.add(ss);
						}
					}
					// begin new entity word if new ner is encountered
					else {
						ss = s;
					}
					prevNER = ner;	// advance ner tag pointer
				}
			}
			entityList = newList; // set the output list
		}

	// PRINTS ----------------------------------------------------
	private void printWords() {
		List<String> newList = new ArrayList<String>();

		for (int i = 0; i < outputtokenList.size(); i++) {
			String word = outputtokenList.get(i).get(TextAnnotation.class);
			newList.add(word);
		}
		System.out.println(newList.toString());
	}

	private void printPOS() {
		List<String> newList = new ArrayList<String>();

		for (int i = 0; i < outputtokenList.size(); i++) {
			String pos = outputtokenList.get(i).get(PartOfSpeechAnnotation.class);
			newList.add(pos);
		}
		System.out.println(newList.toString());
	}

	private void printNER() {
		List<String> newList = new ArrayList<String>();

		for (int i = 0; i < outputtokenList.size(); i++) {
			String ner = outputtokenList.get(i).get(NamedEntityTagAnnotation.class);
			newList.add(ner);
		}
		System.out.println(newList.toString());
	}

	private void printDependency() {
		List<String> newList = new ArrayList<String>();

		for (int i = 0; i < outputtokenList.size(); i++) {
			SemanticGraph dep = outputtokenList.get(i).get(DependencyAnnotation.class);
			// newList.add(dep);
			System.out.println(dep.toCompactString());
		}
		System.out.println(newList.toString());
	}

}
