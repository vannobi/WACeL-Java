package pe.edu.unsa.daisi.lis.cel.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioCleaner;

public final class StringManipulation {
	
	public static final String EMPTY_CHAR = "";
	/**
	 * Get uppercase phrases from a sentence
	 * @param sentence
	 * @return
	 */
	public static List<String> getUpperCasePhrases(String sentence){
				
		List<String> results = new ArrayList<String>();
		//Find UPPERCASE phrases
		Matcher matcherUpperCasePhrases = RegularExpression.PATTERN_UPPERCASE_PHRASES.matcher(sentence);
		while(matcherUpperCasePhrases.find()) {
			String phrase = matcherUpperCasePhrases.group();
			phrase = phrase.replaceFirst(RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT, EMPTY_CHAR);
			phrase = phrase.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT, EMPTY_CHAR);
			results.add(phrase);
			
		}
		//Sort results by length?
		
		return results;
	}
	
	/**
	 * Get number of words from a sentence
	 * @param sentence
	 * @return
	 */
	public static int getNumberOfWords(String sentence){
				
		sentence = sentence.trim();
		String[] words = sentence.split(RegularExpression.REGEX_WHITE_SPACE);
		
		return words.length;
	}
	
	public static int getNumberOfSentences(String sentence) {
		// We add the "." for the sentence extractor to pick the last sentence,
		// if it does not end with a punctuation mark.
		sentence = sentence + ".";
		//test how many sentences
		String wordDelimiters = ".!?"; // ".!?:;";
		StringTokenizer tokenSentence = new StringTokenizer(sentence, wordDelimiters);
		int l = tokenSentence.countTokens();	
		
		if (l > 0)
			return l;
		else if (sentence.length() > 0)
			return 1;
		else
			return 0;
	}
	
	public static void main(String[] args) {
		System.out.println(getUpperCasePhrases(" #HOLA AS.   MUND_H__AS.,. o adshBASBANMB ASDJKSahdjk HI_VB"));
		System.out.println(getUpperCasePhrases("#HOLA AAA. ASAS"));
		System.out.println(getUpperCasePhrases("10. # LOCAL SUPPLIER BID FOR ORDER."));
		String text = "as asa aasss.";
		text = text.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_LINE, "");
		System.out.println(text);
		System.out.println(ScenarioCleaner.cleanSentence(",. asagsdh. jkhsakjdkjsa.jkhasd   ...."));
		System.out.println(ScenarioCleaner.cleanSentence(" asagsdh. jkhsakjdkjsa.jkhasd   ...."));
		System.out.println(ScenarioCleaner.cleanSentence("Administrator adds more channels. Proceed to step 7"));
		
		String regex = ".*(system|use\\s+case|scenario).*";
		if("use case".toLowerCase().matches(regex)) {
			System.out.println("match");
		}
		
		if("use case ends".toLowerCase().matches(regex)) { 
			System.out.println("match2");
		} else {
			System.out.println("no match");
		}
		
		text = "If the user is already registered, the system automatically updates news messages from subscribed channels (refer to DOWNLOAD NEWS use case). If no, the system attempts to REGISTER A NEW USER (refer to Register a new user use case)";
		System.out.println(getNumberOfSentences(text));
		
		String regExpWord = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + "she" + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT; 
		String replacement = "<mark>" + "she" + "</mark>"; 
			String title = "User selects the channels he/she wants to subscribe and/or deselects already subscribed channels to unsubscribe them and chooses the Change subscription options";
			title = ScenarioCleaner.cleanSentence(title.toLowerCase());
			String newTitle = title.replaceAll(regExpWord, replacement);
			if (!newTitle.equals(title)) {
				System.out.println("Implict SHE");
		}
		
	}
}
