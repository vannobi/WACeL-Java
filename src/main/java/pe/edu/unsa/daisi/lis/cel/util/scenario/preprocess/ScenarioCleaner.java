package pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess;

import java.util.ArrayList;
import java.util.List;

import pe.edu.unsa.daisi.lis.cel.util.RegularExpression;

/**
 * This class cleans a text from extra information
 * @Titulo: Pre-process
	@Objetivo: Implementa passos para limpar um texto para análise.
	@Contexto:
		Localização: camada de util.
	@Atores: sistema.
	@Recursos: texto.
 * @author Edgar
 *
 */
public class ScenarioCleaner {
	public static final String EMPTY_CHAR = "";
	public static final String WHITESPACE_CHAR = " ";
	
	//BRACKETS
	public static final String REGEX_PARENTHESES = "\\(.*\\)";
	public static final String REGEX_BRACKETS = "\\[.*\\]";
	public static final String REGEX_BRACES = "\\{.*\\}";
	
	//HTML TAGS
	public static final String REGEX_HTML_TAGS = "<(.|\\n)+?>"; //"(<[^<>]*>)"; //Ex. <a> .... </a>
	
	//URLs
	public static final String REGEX_HTTP_URLS = "((https?)\\:[(\\/\\/)(\\\\\\\\)]+[\\w\\d\\:\\#\\@\\%\\/\\;\\$\\(\\)\\~\\_\\?\\\\\\+\\-\\=\\!\\.\\:\\,\\&]*)";
	public static final String REGEX_FTP_URLS = "((ftps?)\\:[(\\/\\/)(\\\\\\\\)]+[\\w\\d\\:\\#\\@\\%\\/\\;\\$\\(\\)\\~\\_\\?\\\\\\+\\-\\=\\!\\.\\:\\,\\&]*)";
	public static final String REGEX_FILE_URLS = "((\\w+)\\:[(\\/\\/)(\\\\\\\\)]+[\\w\\d\\:\\#\\@\\%\\/\\;\\$\\(\\)\\~\\_\\?\\\\\\+\\-\\=\\!\\.\\:\\,\\&]*)";
	public static final String REGEX_EMAIL = "[A-Za-z0-9\\.\\%\\+\\-]+@[A-Za-z0-9\\.\\%\\+\\-]+\\.\\w\\w\\w?\\w?";
	
	//PUCTUATION
	public static final String REGEX_SPECIAL_PUNCTUATION = "(\\p{Punct})"; //It does not include text like "re-enter" 
	public static final String REGEX_PUNCTUATION_AT_BEGIN_AND_END_SENTENCE = "(^(\\p{Punct})*|(\\p{Punct})*$)"; // $##ASJAJDHAKJDAHDK)_+
	
	public static final String REGEX_DELIMITING_CONDITIONS = "\\s+(AND|E|Y)\\s+|\\s+(OR|OU|O)\\s+"; //Ex. a OR b AND c.
	
	/*
	@Titulo: Remover o ruido de texto
	@Objetivo: Remover o ruido do texto.
	@Contexto:
		Localização: camada de util.
		Pré-condição:
	@Atores: sistema.
	@Recursos: texto.
	*/
	public static String cleanSentence(String sentence) {

		String result = EMPTY_CHAR;

		if (sentence != null && !sentence.isEmpty()) {
			result = sentence.trim();
			//@Episódio 1: Removal of text between Brackets
			result = sentence.replaceAll(REGEX_PARENTHESES, EMPTY_CHAR);
			//result = string.gsub(result, remove_brackets_reg_ex , EMPTY_CHAR)
			//result = string.gsub(result, "%[" , EMPTY_CHAR)
			//result = string.gsub(result, "%]" , EMPTY_CHAR)
			result = result.replaceAll(REGEX_BRACES, EMPTY_CHAR);
			//@Episódio 2: Removal of URLs
			result = result.replaceAll(REGEX_HTTP_URLS, WHITESPACE_CHAR);
			result = result.replaceAll(REGEX_FTP_URLS, WHITESPACE_CHAR);
			result = result.replaceAll(REGEX_FILE_URLS, WHITESPACE_CHAR);
			result = result.replaceAll(REGEX_EMAIL, WHITESPACE_CHAR);
			//@Episódio 3: Removal of HTML Markup
			result = result.replaceAll(REGEX_HTML_TAGS, EMPTY_CHAR);
			//@Episódio 4: Removal Punctuation  before and after text: replace by white space: 
			//result = result.replaceAll(REGEX_SPECIAL_PUNCTUATION, WHITESPACE_CHAR);
			result = result.replaceFirst(RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT, EMPTY_CHAR);
			result = result.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_LINE, EMPTY_CHAR);
			result = result.trim();
		}
		//@Episódio 5: retornar o texto limpo.
		return result;

	}
	
	/*
	@Titulo: Remover o ruido de uma lista de textos
	@Objetivo: Remover o ruido de uma lista de textos
	@Contexto:
		Localização: camada de util.
		Pré-condição:
	@Atores: sistema.
	@Recursos: lista de textos.
	*/
	public static List<String> cleanListOfSentences(List<String> sentences) {

		List<String> results = new ArrayList<String>();

		if (sentences != null && !sentences.isEmpty()) {
			//@Episódio 1: pre-processar cada sentenca
			for(String sentence: sentences) {
			//@Episódio 2: adicionar a sentenca limpa a lista	
				results.add(cleanSentence(sentence));
			}
		}
		//@Episódio 3: retornar os textos limpos.
		return results;

	}
	
	/**
	 * Check unnecessary extra information from a sentence
	 * @param sentence
	 * @return
	 */
	public static List<String> checkExtraInformation(String sentence){
				
		List<String> results = new ArrayList<String>();
		if (sentence != null && !sentence.isEmpty()) {
			sentence = sentence.trim();
			//@Episódio 1: Find of Brackets
			String result = sentence.replaceAll(REGEX_PARENTHESES, EMPTY_CHAR);
			if(!result.equals(sentence))
				results.add("contains text between parentheses;");
			//result = string.gsub(result, remove_brackets_reg_ex , EMPTY_CHAR)
			//result = string.gsub(result, "%[" , EMPTY_CHAR)
			//result = string.gsub(result, "%]" , EMPTY_CHAR)
			result = result.replaceAll(REGEX_BRACES, EMPTY_CHAR);
			if(!result.equals(sentence))
				results.add("contains text between brace brackets;");
			//@Episódio 2: Find of URLs
			result = result.replaceAll(REGEX_HTTP_URLS, EMPTY_CHAR);
			if(!result.equals(sentence))
				results.add("contains HTTP URL;");
			result = result.replaceAll(REGEX_FTP_URLS, EMPTY_CHAR);
			if(!result.equals(sentence))
				results.add("contains FTP URL;");
			result = result.replaceAll(REGEX_FILE_URLS, EMPTY_CHAR);
			if(!result.equals(sentence))
				results.add("contains FILE URL;");
			result = result.replaceAll(REGEX_EMAIL, EMPTY_CHAR);
			if(!result.equals(sentence))
				results.add("contains E-MAIL address;");
			//@Episódio 3: Find of HTML Markup
			result = result.replaceAll(REGEX_HTML_TAGS, EMPTY_CHAR);
			if(!result.equals(sentence))
				results.add("contains HTML MARKUP;");
			//@Episódio 4: Find of Punctuation before and after the text: replace by white space: 
			result = result.replaceFirst(RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT, EMPTY_CHAR);
			result = result.replaceAll(RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_LINE, EMPTY_CHAR);
			result = result.trim();
			if(!result.equals(sentence))
				results.add("contains unnecessary punctuation;");
		}

		//@Episódio 5: retornar uma lista com as informacoes extras encontradas.
	  return results;
	}
	
	
	
	/**
	 * REPLACE words between '/' by second text
	 * Ex. User selects/deletes the channels -> deletes
	 * Ex. e wants to subscribe and/or -> or
	 * Ex. his/her -> her
	 * Improve the Pos-tagging phase in NLP
	 * @param sentence
	 * @return
	 */
	public static String replaceWordsBetweenSlashBySecondWord(String sentence) {
		String regEx = "\\b(\\w+\\s*/)(?=(\\s*\\w+)\\b)";
		String result = EMPTY_CHAR;

		if (sentence != null && !sentence.isEmpty()) {
			//@Episódio 1: Removal of text between Brackets
			result = sentence.replaceAll("\\b(\\w+\\s*/)(?=(\\s*\\w+)\\b)", EMPTY_CHAR);
			
		}
		//@Episódio 5: retornar o texto limpo.
		return result;

	}
	
	 public static void main(String[] args) { 
		System.out.println(cleanSentence("administrator chooses the browse candidate's list option"));
		System.out.println(cleanSentence("#administrator chooses the browse candidate's list option#"));
	 }

}
