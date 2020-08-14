package pe.edu.unsa.daisi.lis.cel.util.nlp;

import java.util.List;

/**
 * Defines the methods that must be implemented for selected NLP Tools
 * @author Edgar
 *
 */
public interface INLPAnalyzer {
		
	/**
	 * Create a NLP object, with Tokenization, POS tagging and Lemmatization models
	 * Identification of tokens with word, tag/stem and lemma information
	 */
	void initializePipeline();
	
	/**
	 * Create a NLP object, with Parse model
	 * Identification of Subject, verb and Object roles . 
	 */
	void initializeParsing();
	
	/**
	 * Create a NLP object, with Chunking model
	 * Text chunking consists of dividing a text in syntactically correlated parts of words, like noun groups, verb groups, but does not specify their internal structure, nor their role in the main sentence. 
	 */
	void initializeChunking();
	
	/**
	 * Return a list of Tokens (CustomToken) from a text
	 * <br/> It also adjusts the precision of Postagging phase
	 * - Tokenization
	 * - Part-of-Speech Tagging
	 * - Lemmatization
	 * @resource AnalyzerUtil
	 * @param sentence
	 * @return
	 */
	List<CustomToken> getTokens(String text);
	
	/**
	 * Return a list of lists of Tokens (CustomToken) from one list of texts
	 * @param texts
	 * @return
	 */
	List<List<CustomToken>> getTokens(List<String> texts);
	
	/**
	 * Return a list of Tokens (CustomToken) with the corresponding chunk type (shallow parsing), 
	 * <br> for example I-NP for noun phrase words, I-VP for verb phrase words, B-PP for preposition phrase words, B-ADVP for adverb phrase words, B-ADJP for adjective phrase words. Most chunk types have three types of chunk tags: B | I | O. "B" represents the start of a chunk, "I" represents the continuation of the chunk and "O" represents no chunk.
	 * @resource 
	 * @param tokens
	 * @return
	 */
	List<CustomToken> getTokensWithChunkTypes(List<CustomToken> tokens);
	
	
	/**
	 * Return an array of spans (group of words - phrase) with chunk tag for each span in a sequence, 
	 * <br> for example [0..2) NP for noun phrases, [2..3) VP for verb phrases, [3..5) PP for preposition phrases, [6..7) ADVP for adverb phrases, [7..8) ADJP for adjective phrases. Most spans have [start..end): "start" represents the begin of a span and "end" represents the end of the span, which is +1 more than the last element in the span
	 * @resource 
	 * @param tokens
	 * @return
	 */
	List<String> getChunkSpans(List<CustomToken> tokens);
	
	/**
	 * Return a processed sentence (CustomSentenceNlpInfo) with Tokens (List<CustomToken>),  Subjects (HashMap<Integer, CustomToken>), Objects (HashMap<Integer, CustomToken>) and Action-Verbs (HashMap<Integer, CustomToken>)
	 * It uses Dependency parsing of CoreNLP
	 * @pre-condition initializeParsing();
	 * @param text
	 * @return
	 */
	CustomSentenceNlpInfo getSentenceComponents(String text);	
		
	/**
	 * Return a list of of processed sentences (CustomSentenceNlpInfo) from one list of texts
	 * @param texts
	 * @return
	 */
	List<CustomSentenceNlpInfo> getSentencesComponents(List<String> texts);
	
	/**
	 * Measure Syntactic Similarity between two texts (used to evaluate duplicity and coherency) by comparing action-verbs and direct objects
	 * <br/>
	 * IF two sentences have the same Action-Verb AND Total-Matching-Objects / Total-Distinct-Objects > 0 THEN They are pontentially duplicated 
	 * @param text
	 * @param otherText
	 * @return
	 */
	boolean isSintacticallySimilar(String text, String otherText);
	
	/**
	 * Measure Semantic Similarity between two texts (used to evaluate duplicity and coherency) by comparing action-verbs (or their synonyms) and direct objects (or their synonyms)
	 * @resource WordNet
	 * @param text
	 * @param otherText
	 * @return
	 */
	boolean isSemanticallySimilar(String text, String otherText);
}
