package pe.edu.unsa.daisi.lis.cel.util.nlp.readability;


import java.math.BigDecimal;
import java.util.StringTokenizer;

import pe.edu.unsa.daisi.lis.cel.util.nlp.readability.engine.Syllabify;
import pe.edu.unsa.daisi.lis.cel.util.nlp.readability.enums.MetricType;



/**
 * Implements various readability indexes
 * https://github.com/ipeirotis/ReadabilityMetrics
 * 
 * @author Panos Ipeirotis
 * 
 */
public class Readability {

	//private static SentenceExtractor se = new SentenceExtractor();

	Integer sentences;

	Integer complex;

	Integer words;

	Integer syllables;

	Integer characters;

	public Readability(String text) {

		// We add the "." for the sentence extractor to pick the last sentence,
		// if it does not end with a punctuation mark.
		this.sentences = getNumberOfSentences(text + ".");
		this.complex = getNumberOfComplexWords(text);
		this.words = getNumberOfWords(text);
		this.syllables = getNumberOfSyllables(text);
		this.characters = getNumberOfCharacters(text);

	}
	
	public double getMetric(MetricType t) throws IllegalArgumentException {

		switch (t) {
		case SMOG:
			return getSMOG();
		case FLESCH_READING:
			return getFleschReadingEase();
		case FLESCH_KINCAID:
			return getFleschKincaidGradeLevel();
		case ARI:
			return getARI();
		case GUNNING_FOG:
			return getGunningFog();
		case COLEMAN_LIAU:
			return getColemanLiau();
		case SMOG_INDEX:
			return getSMOGIndex();
		case CHARACTERS:
			return getCharacters();
		case SYLLABLES:
			return getSyllables();
		case WORDS:
			return getWords();
		case COMPLEXWORDS:
			return getComplex();
		case SENTENCES:
			return getSentences();

		default:
			throw new IllegalArgumentException("Readability Metric '"
					+ t.toString() + "' not supported");
		}
	}

	private int getCharacters() {
		return characters;
	}

	private int getComplex() {
		return complex;
	}

	private int getSentences() {
		return sentences;
	}

	private int getSyllables() {
		return syllables;
	}

	private int getWords() {
		return words;
	}



	/**
	 * Returns true is the word contains 3 or more syllables
	 * 
	 * @param w
	 * @return
	 */
	private static boolean isComplex(String w) {
		int syllables = Syllabify.syllable(w);
		return (syllables > 2);
	}

	/**
	 * Returns the number of letter characters in the text
	 * 
	 * @return
	 */
	private static int getNumberOfCharacters(String text) {
		String cleanText = cleanLine(text);
		String[] word = cleanText.split(" ");

		int characters = 0;
		for (String w : word) {
			characters += w.length();
		}
		return characters;
	}

	/**
	 * Returns the number of words with 3 or more syllables
	 * 
	 * @param text
	 * @return the number of words in the text with 3 or more syllables
	 */
	private static int getNumberOfComplexWords(String text) {
		String cleanText = cleanLine(text);
		String[] words = cleanText.split(" ");
		int complex = 0;
		for (String w : words) {
			if (isComplex(w))
				complex++;
		}
		return complex;
	}

	private static int getNumberOfWords(String text) {
		String cleanText = cleanLine(text);
		String[] word = cleanText.split(" ");
		int words = 0;
		for (String w : word) {
			if (w.length() > 0)
				words++;
		}
		return words;
	}

	/**
	 * Returns the total number of syllables in the words of the text
	 * 
	 * @param text
	 * @return the total number of syllables in the words of the text
	 */
	private static int getNumberOfSyllables(String text) {
		String cleanText = cleanLine(text);
		String[] word = cleanText.split(" ");
		int syllables = 0;
		for (String w : word) {
			if (w.length() > 0) {
				syllables += Syllabify.syllable(w);
			}
		}
		return syllables;
	}

	private static String cleanLine(String line) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c < 128 && Character.isLetter(c)) {
				buffer.append(c);
			} else {
				buffer.append(' ');
			}
		}
		return buffer.toString().toLowerCase();
	}

	private static int getNumberOfSentences(String text) {
		//int l = se.getSentences(text).length;
		//test how many sentences
		String wordDelimiters = ".!?:;";
		StringTokenizer tokenSentence = new StringTokenizer(text, wordDelimiters);
		int l = tokenSentence.countTokens();	
		
		if (l > 0)
			return l;
		else if (text.length() > 0)
			return 1;
		else
			return 0;
	}

	/**
	 * https://raven.zendesk.com/hc/en-us/articles/202308564-What-are-Readability-Metrics-
	 */
	
	/**
	 * 
	 * http://en.wikipedia.org/wiki/SMOG_Index
	 *  It's debatable whether SMOG is short for "Simple Measure of Gobbledygook," but this index developed in 1969 is still a common measure of readability. Take 30 sentences (10 from the beginning, middle and end of your text), then count every word with three or more syllables in each group of sentences, then calculate the square root of that number and round it to the nearest 10, then add 3 to that number. You have the U.S. grade level that should be able to read that text. 
	 *  The recommended grade level for this score is 7 to 8, depending on your audience.
	 * @param text
	 * @return The SMOG index of the text
	 */
	private double getSMOGIndex() {
		double score = Math.sqrt(complex * (30.0 / sentences)) + 3;
		return round(score, 3);
	}

	/**
	 * 
	 * http://en.wikipedia.org/wiki/SMOG
	 * 
	 * @param text
	 * @return Retugns the SMOG value for the text
	 */
	private double getSMOG() {
		double score = 1.043 * Math.sqrt(complex * (30.0 / sentences)) + 3.1291;
		return round(score, 3);
	}

	/**
	 * 
	 * http://en.wikipedia.org/wiki/Flesch-Kincaid_Readability_Test
	 * The Flesch-Kincaid Reading Ease score is the result of a mathematical formula that incorporated the average number of syllables per word and the average number of words per sentence for the 100-word block of text. Results are measured on a scale from 1 to 100, with 1 being very complicated to read and 100 being very easy to read. 
	 * Most readability resources recommend writing for the 60 to 70 range.
	 * @param text
	 * @return Returns the Flesch_Reading Ease value for the text
	 */
	private double getFleschReadingEase() {

		double score = 206.835 - 1.015 * words / sentences - 84.6 * syllables
				/ words;

		return round(score, 3);
	}

	/**
	 * 
	 * http://en.wikipedia.org/wiki/Flesch-Kincaid_Readability_Test
	 * The Flesch-Kincaid Grade Level score was developed after the Reading Ease score to make it easier for parents, librarians and others to make decisions about reading content for children. 
	 * The recommending writing level here depends on your audience, but 7th to 8th grade is a good standard - that captures more than 80% of the U.S. adults.
	 * @param text
	 * @return Returns the Flesch-Kincaid_Readability_Test value for the text
	 */
	private double getFleschKincaidGradeLevel() {
		double score = 0.39 * words / sentences + 11.8 * syllables / words
				- 15.59;
		return round(score, 3);
	}

	/**
	 * 
	 * http://en.wikipedia.org/wiki/Automated_Readability_Index
	 * The Automated Readability Index (ARI) mathematical formula has two variables: characters per word (instead of syllables, similar to the Coleman Liau Index) and words per sentence. It has been around since 1967. Its scores correspond to U.S. grade level. If you get a score with a decimal, round up to the next whole number. 
	 * Again, the recommended reading level is 7 to 8, depending on your audience.
	 * @param text
	 * @return the Automated Readability Index for text
	 */
	private double getARI() {
		double score = 4.71 * characters / words + 0.5 * words / sentences
				- 21.43;
		return round(score, 3);
	}

	/**
	 * 
	 * http://en.wikipedia.org/wiki/Gunning-Fog_Index
	 * 
	 * @param text
	 * @return the Gunning-Fog Index for text
	 */
	private double getGunningFog() {
		double score = 0.4 * (words / sentences + 100 * complex / words);
		return round(score, 3);
	}

	/**
	 * 
	 * http://en.wikipedia.org/wiki/Coleman-Liau_Index
	 * Unlike most other readability tests, the Coleman Liau Index relies on the number of characters instead of syllables per words for its calculation. It returns a U.S. grade-level score from 1-12. 
	 * The recommended writing level for this score is 7 to 8, depending on your audience.
	 * @return The Coleman-Liau_Index value for the text
	 */
	private double getColemanLiau() {
		double score = (5.89 * characters / words) - (30 * sentences / words)
				- 15.8;
		return round(score, 3);
	}

	private static Double round(double d, int decimalPlace) {
		// see the Javadoc about why we use a String in the constructor
		// http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(double)
		BigDecimal bd = new BigDecimal(Double.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
} 
