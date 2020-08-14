package pe.edu.unsa.daisi.lis.cel.util.nlp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.Span;
import pe.edu.unsa.daisi.lis.cel.util.nlp.dictionary.english.SpecialVerb;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
/**
 * OpenNLP 
 * Singleton Pattern
 * @author Edgar
 *
 */
public class OpenNLPAnalyzer implements INLPAnalyzer {
	
	private static OpenNLPAnalyzer uniqueInstance; 
	
	public static final String S_CHAR = "s";
	public static final String POSSESSIVE = "'s";
	public static final String REG_EXP_OTHER_POSSESSIVE_CANDIDATES = "(\\`[sS]|\\´[sS])";

	InputStream inputStreamTokenizer;
	InputStream inputStreamPOSTagger;
	InputStream dictLemmatizer;
	InputStream inputStreamChunker;
	
	TokenizerModel tokenModel;
	POSModel posModel;
	DictionaryLemmatizer lemmatizer;
	ChunkerModel chunkerModel;

	private OpenNLPAnalyzer() {
		
	}
	
	/**
	 * Returns a unique instance of OpenNLPAnalyzer and initializes the NLP pipeline and parsing models
	 * @return
	 */
	public static synchronized OpenNLPAnalyzer getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new OpenNLPAnalyzer();
			//uniqueInstance.initializePipeline();
			//uniqueInstance.initializeParsing();
			uniqueInstance.initializeChunking();
		}
		return uniqueInstance;
	}
	
	public void initializePipeline() {
		try {
			inputStreamTokenizer = new ClassPathResource("/main/resources/opennlp/models/en-token.bin").getInputStream();
			tokenModel = new TokenizerModel(inputStreamTokenizer);
			inputStreamPOSTagger = new ClassPathResource("/main//resources/opennlp/models/en-pos-maxent.bin").getInputStream();
			posModel = new POSModel(inputStreamPOSTagger);
			dictLemmatizer = new ClassPathResource("/main/resources/opennlp/models/en-lemmatizer.dict").getInputStream();
			lemmatizer = new DictionaryLemmatizer(dictLemmatizer);
			

		}  catch (FileNotFoundException e){
			// TODO Auto-generated catch block
						e.printStackTrace();
		}  catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Override
	public void initializeChunking() {
		try {
			inputStreamChunker = new ClassPathResource("/main/resources/opennlp/models/en-chunker.bin").getInputStream();
			chunkerModel = new ChunkerModel(inputStreamChunker);

		}  catch (FileNotFoundException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Override
	public void initializeParsing() {
		// parser model
	}

	@Override
	public List<CustomToken> getTokens(String text) {
		text = text.replaceAll(REG_EXP_OTHER_POSSESSIVE_CANDIDATES, POSSESSIVE);
		text = text.toLowerCase();
		List<CustomToken> tokens = new ArrayList<>();
		//Get tokens
		TokenizerME tokenizer = new TokenizerME(tokenModel);
		String[] words =  tokenizer.tokenize(text);
		//Get Pos tags
		POSTaggerME posTagger = new POSTaggerME(posModel);
		String tags[]  =  posTagger.tag(words);
		//Get lemmas
		String[] lemmas  = lemmatizer.lemmatize(words, tags);
		List<CustomToken> sentenceTokens = new ArrayList<>();
		if(words != null && tags != null && lemmas != null
				&& words.length == tags.length && tags.length == lemmas.length)
			for(int i = 0; i < words.length; i++) {
				CustomToken customToken = new CustomToken(words[i], tags[i], lemmas[i], i);
				sentenceTokens.add(customToken);
			}
		//Adjust POS TAGS: NOUNS & VERBS
		tokens = PosTagImprover.adjustPosTags(sentenceTokens);

		return tokens;
	}


	public List<CustomToken> getTokensWithChunkTypes(List<CustomToken> tokens) {
		
		ChunkerME chunker = new ChunkerME(chunkerModel);
		String[] chunks = {};
		if(tokens != null && !tokens.isEmpty()) {
			List<String> words = new ArrayList<>();
			List<String> tags = new ArrayList<>();
			for(int i = 0; i < tokens.size(); i++) {
				words.add(tokens.get(i).getWord());
				tags.add(tokens.get(i).getPosTag());
			}
			
			String[] wordsArray = words.toArray(new String[0]);
			String[] tagsArray = tags.toArray(new String[0]);
			chunks = chunker.chunk(wordsArray, tagsArray);
			    
			
		}
		//Add chunk Information
		for(int i = 0; i < chunks.length; i++) {
			tokens.get(i).setChunkTag(chunks[i].toString());
		}
		//Tokens with Chunk Info
	    return tokens;
	}
	
	@Override
	public List<String> getChunkSpans(List<CustomToken> tokens) {
		ChunkerME chunker = new ChunkerME(chunkerModel);
		Span[] spans = {};
		if(tokens != null && !tokens.isEmpty()) {
			List<String> words = new ArrayList<>();
			List<String> tags = new ArrayList<>();
			for(int i = 0; i < tokens.size(); i++) {
				words.add(tokens.get(i).getWord());
				tags.add(tokens.get(i).getPosTag());
			}
			
			String[] wordsArray = words.toArray(new String[0]);
			String[] tagsArray = tags.toArray(new String[0]);
			
			//Generating the tagged chunk spans 
		   spans = chunker.chunkAsSpans(wordsArray, tagsArray); 
		   
			
		}
		//Add chunk Information
		List<String> strSpans = new ArrayList<String>();
		for(int i = 0; i < spans.length; i++) {
			strSpans.add(spans[i].toString());
		}
		//Span Info
	    return strSpans;
	}

	@Override
	public List<List<CustomToken>> getTokens(List<String> texts) {
		List<List<CustomToken>> tokensOfSentences = new ArrayList<>();
		for(String sentence : texts) {
			List<CustomToken> tokens = new ArrayList<>();
			List<CustomToken> sentenceTokens = new ArrayList<>();
			sentenceTokens = getTokens(sentence);
			tokens = PosTagImprover.adjustPosTags(sentenceTokens);
			tokensOfSentences.add(tokens);
		}
		return tokensOfSentences;
	}

	@Override
	public CustomSentenceNlpInfo getSentenceComponents(String text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CustomSentenceNlpInfo> getSentencesComponents(List<String> texts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSintacticallySimilar(String text, String otherText) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSemanticallySimilar(String text, String otherText) {
		// TODO Auto-generated method stub
		return false;
	}
	
	 public static void main(String[] args) {
			
		 	
		 	OpenNLPAnalyzer openNlpAnalyzer = OpenNLPAnalyzer.getInstance();//singleton
		 	CoreNLPAnalyzer coreNlpAnalyzer = CoreNLPAnalyzer.getInstance();//singleton
			
		 	//String text1 = "The system shall send a message to the receiver, and it provides an acknowledge message";
			//String text1 = "The system shall send a message to the receiver, and it provides an acknowledgement message";
		 	
		 	String text1 = "The System receives news messages and stores them in a local database";
		 	//String text1 = "The System receives news messages and stores them";
		 	
		 	//String text1 = "System saves the data";
		 	
		 	//String text1 = "The System receives news messages. They contain letters";
		 	
		 	//String text1 = "it shall be possible";
		 	
		 	//String text1 = "System displays a list of groups with subscribed channels and the number of new messages in each of them";
			
			List<CustomToken> tokens = coreNlpAnalyzer.getTokens(text1);
			System.out.println(tokens.toString());
			//System.out.println(getPosTagsAsString(tokens, 0, tokens.size()));
			
			tokens = openNlpAnalyzer.getTokensWithChunkTypes(tokens);
			System.out.println(tokens.toString());
			System.out.println(getChunksAsString(tokens, 0, tokens.size()));
			
			List<String> spans = openNlpAnalyzer.getChunkSpans(tokens);
			System.out.println(spans.toString());		
			
			String REGEXP_IMPLICIT = "(.*NP.*(.*NP.*)+.*(PP.*|PR.*))";
			if(spans.toString().matches(REGEXP_IMPLICIT)) {
				System.out.println("Implicit word");
			}
			      
	 }
	 
	 /**
		 * @param tokens
		 * @param start from index
		 * @param end to index
		 * @return
		 */
			
		private static String getChunksAsString(List<CustomToken> chunks, int start, int end) {
			//Pos tags
			int currentElement = 0;
			StringBuffer tags = new StringBuffer("");
			if(start >= 0 && end <= chunks.size() && start <= end) {
				for(int i = start; i < end; i++) {
		    		CustomToken token = chunks.get(i) ;
		    		if (currentElement == 0)
		    			tags.append(token.getChunkTag());
		    		else
		    			tags.append(" " +token.getChunkTag());
		    		currentElement++;
		    	}
			} else {
				for(int i = 0; i < chunks.size(); i++) {
		    		CustomToken token = chunks.get(i) ;
		    		if (currentElement == 0)
		    			tags.append(token.getChunkTag());
		    		else
		    			tags.append(" " +token.getChunkTag());
		    		currentElement++;
		    	}
			} 
		
	    	return tags.toString();
		}

	

}
