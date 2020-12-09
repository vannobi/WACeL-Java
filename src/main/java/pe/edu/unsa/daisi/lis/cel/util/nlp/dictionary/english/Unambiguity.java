package pe.edu.unsa.daisi.lis.cel.util.nlp.dictionary.english;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Weak/Vague/Subjective,Quantity/Option/Multiple/Implicit/Non-minimal words or phrases
 */
public final class Unambiguity {
	
	public static final ArrayList<String> WEAK_WORDS_PHRASES = 
			new ArrayList<String>(Arrays.asList("can", "could", "may", "might", "ought", "preferred", "should", "will", "would")); //"ought to"
	
	public static final ArrayList<String> VAGUE_WORDS_PHRASES = 
			new ArrayList<String>(Arrays.asList("adaptability", "additionally", "adequate", "aggregate", "also", "ancillary", "arbitrary", 
					"appropriate", "as appropriate", "available", "as far as", "at last", "as few as possible", "as little as possible", 
					"as many as possible", "as much as possible", "as required", "as well as", "bad", "both", "but", "but also", "but not limited to", "capable of", "capable to", 
					"capability of", "capability", "common", "correctly", "consistent", "contemporary",  
					"convenient", "credible", "custom", "customary", "default", "definable", "easily", "easy", 
					"effective", "efficient", "episodic", "equitable", 
					"equitably",  "eventually", "exist", "exists", 
					"expeditiously", "fast", "fair", "fairly", "finally", "frequently", "full", "general", "generic", "good", 
					"high-level", "impartially", "infrequently", "insignificant", "intermediate", "interactive", 
					"in terms of", "less", "lightweight", "logical", "low-level", "maximum", "minimum", "more", 
					"mutually-agreed", "mutually-exclusive", "mutually-inclusive", "near", "necessary", "neutral", "not only", "only", "on the fly", "particular", 
					"physical", "powerful", "practical", "prompt", "provided", "quickly", "random", "recent"
					, "regardless of", "relevant", "respective", 
					"robust", "routine", "sufficiently", "sequential", "significant", "simple", "specific", "strong", 
					"there", "there is", "transient", "transparent", "timely", "undefinable", "understandable", 
					"unless", "unnecessary", "useful", "various", "varying", "required"));
	
	public static final ArrayList<String> SUBJECTIVE_WORDS_PHRASES = 
			new ArrayList<String>(Arrays.asList("similar", "better",  "similarly", "worse", "having in mind", "take into account", "take into consideration", "as possible"));
	
	public static final ArrayList<String> QUANTITY_WORDS_PHRASES = 
			new ArrayList<String>(Arrays.asList("all", "any", "few", "little", "many", "much", "several", "some"));
	
	public static final ArrayList<String> OPTION_WORDS_PHRASES = 
			new ArrayList<String>(Arrays.asList("as desired", "at last", 
					"either", "eventually", "if appropriate", "if desired", "in case of", "if necessary", "if needed", 
					"neither", "nor", "optionally", "otherwise", "possibly", "probably", "whether"));
	
	public static final ArrayList<String> MULTIPLE_WORDS_PHRASES = 
			new ArrayList<String>(Arrays.asList("and", "or", "and/or", "/"));
	
	public static final ArrayList<String> IMPLICIT_WORDS_PHRASES = 
			new ArrayList<String>(Arrays.asList("anyone", "anybody", 
					"anything", "everyone", "everybody", "everything"
					, "he", "she", "her", "hers", "herself", "him", "himself", 
					"his", "i", "it", "its", "itself", "me", "mine", "most", "my", "myself", "nobody", "none", "no one", "nothing", 
					"our", "ours", "ourselves", "she", "someone", "somebody", "something",  "their", "theirs", 
					"them", "themselves", "these", "they", "this", "those", "us", "we", "what", "whatever", "which",  
					"whichever", "who", "whoever", "whom", "whomever", "whose", "whosever", "you", "your", "yours", "yourself", "yourselves", "previous"
					, "next", "following", "last", "above", "below", "that"));
	
	public static final ArrayList<String> NON_MINIMAL_WORDS_PHRASES = new ArrayList<String>(Arrays.asList(".", ";", ":", "!", "?")); 
			//new ArrayList<String>(Arrays.asList("\\.", "\\;", "\\:", "\\!", "\\?"));
	
	//Positive Indicators	
	public static final ArrayList<String> DIRECTIVE_WORDS_PHRASES = 
			new ArrayList<String>(Arrays.asList("e.g", "etc", "figure","for example", "i.e", "note", "table"));
	
	public static final ArrayList<String> CONTINUANCE_WORDS_PHRASES = 
			new ArrayList<String>(Arrays.asList("as follows", "below", "following", "in addition", "in particular", "listed", "meantime", 
					"meanwhile", "on one hand", "on the other hand", "whereas"));
	
	
}
