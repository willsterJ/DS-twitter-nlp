import java.util.*;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;


public class CoreNLPHandler {
	
	public ArrayList<CoreLabel> outputtokenList;
	
	public CoreNLPHandler(String input, StanfordCoreNLP pipeline) {
		_doStanfordNLP(input.replaceAll("[^a-zA-Z0-9 @#:/)(]", ""), pipeline);
		//printWords();
		//printPOS();
		//printNER();
	}
	
	private void _doStanfordNLP(String text, StanfordCoreNLP pipeline) {

        // create an empty Annotation just with the given text
        //Annotation document = new Annotation(text);	// this is from the tutorial. However, to get NER to work use CoreDocument
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
	
	
	
	
	// PRINTS
	private void printWords() {
		List<String> newList = new ArrayList<String>();
		
		for (int i=0; i<outputtokenList.size(); i++) {
			String word = outputtokenList.get(i).get(TextAnnotation.class);
			newList.add(word);
		}
		System.out.println(newList.toString());
	}
	private void printPOS() {
		List<String> newList = new ArrayList<String>();
		
		for (int i=0; i<outputtokenList.size(); i++) {
			String pos = outputtokenList.get(i).get(PartOfSpeechAnnotation.class);
			newList.add(pos);
		}
		System.out.println(newList.toString());
	}
	
	private void printNER() {
		List<String> newList = new ArrayList<String>();
		
		for (int i=0; i<outputtokenList.size(); i++) {
			String ner = outputtokenList.get(i).get(NamedEntityTagAnnotation.class);
			newList.add(ner);
		}
		System.out.println(newList.toString());
	}
	
	
	
	
}
