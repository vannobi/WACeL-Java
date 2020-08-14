package pe.edu.unsa.daisi.lis.cel.util;

import java.util.regex.Pattern;

/**
 * Regular Expressions for transforming a Scenario to Structured Scenario
 * @author Edgar
 *
 */
public final class RegularExpression {
	//SCENARIO TO STRUCTURED SCENARIO 
	//REGULAR EXPRESSIONS
	public static final String REGEX_WHITE_SPACE = "(\\s+)";
	public static final String REGEX_SEPARATOR_ITEMS = "[\\,\\;]"; //Ex. a, b, c, d
	public static final String REGEX_END_INDICATOR_ITEMS = "(\\.|\\n|\\r|$)"; //(\\.|\\n|\\r|$) --> ex. (a, b, c.). 
	
	public static final String REGEX_SEPARATOR_CONDITIONS = "(\\s+([aA][nN][dD]|[eE]|[yY])\\s+|\\s+([oO][rR]|[oO][uU]|[oO])\\s+|\\;)"; //Ex. a OR b AND c.
	
	public static final String REGEX_START_DELIMITING_CONDITIONAL_EPISODE_CONDITIONS = "^#*\\s*([iI][fF]|[wW][hH][eN][nN]|[sS][eE]|[sS][iI])\\s+"; //Ex. IF/WHEN ... THEN .... 
	//public static final String REGEX_START_DELIMITING_CONDITIONAL_EPISODE_ACTION = "\\s+([tT][hH][eE][nN]|[eE][nN][tT][aA]?[„√]?[oO]|[eE][nN][tT][oO][nN][cC][eE][sS]?)(\\n|\\r|$|\\s+)"; //Ex. IF ... THEN ....
	public static final String REGEX_START_DELIMITING_CONDITIONAL_EPISODE_ACTION = "([\\,\\;]?\\s+([tT][hH][eE][nN]|[eE][nN][tT][aA]?[„√]?[oO]|[eE][nN][tT][oO][nN][cC][eE][sS]?)|[\\,\\;])(\\n|\\r|$|\\s+)"; //Ex. IF ... THEN .... IF ..., ....
	
	//TBD: Non explict conditional episodes/exceptions (<sentence> IF | WHEN) - Ex. User adds a new item when system is online
	public static final String REGEX_START_DELIMITING_CONDITIONAL_EPISODE_NON_EXPLICIT_CONDITIONS = "[\\,\\;]?\\s+([iI][fF]|[wW][hH][eE][tT][hH][eE][rR]|[wW][hH][eN][nN]|[sS][eE]|[sS][iI]|[cCqQ][uU][aA][nN][dD][oO])\\s+"; //Ex. <sentence> IF | WHEN ....
	
	//COCKBURN: VALIDATION ACTION -> "Verifies" / 	"validates" / "ensures" / "establishes" are good, goal-achieving action verbs.
	//Therefore, avoid the verbs "checks" and "sees whether". Use instead one of the other goal achieving action verbs for a validation step. Let the presence of the word "if" trigger your 	thinking.
	public static final String REG_EX_VALIDATION_ACTION_INDICATOR =             "(verif[yi][e]?[s]?|validate[s]?|ensure[s]?|establish[e]?[s]?|check[s]?|see[s]?|verifica|valida|v[eÈÍ])\\s+([tTwW][hH][aA][tT]|[qq][uU][eÈÍE… ])\\s+";
	public static final String REG_EX_COMPLICATED_VALIDATION_ACTION_INDICATOR = "(verif[yi][e]?[s]?|validate[s]?|ensure[s]?|establish[e]?[s]?|check[s]?|see[s]?|verifica|valida|v[eÈÍ])\\s+([iI][fF]|[wW][hH][eE][tT][hH][eE][rR]|[sS][eE]|[sS][iI])\\s+";
	
	public static final String REGEX_START_DELIMITING_OPTIONAL_EPISODE = "#*\\s*\\[\\s*"; //Ex. [...]
	public static final String REGEX_END_DELIMITING_OPTIONAL_EPISODE = "\\s*\\]\\s*#*"; //Ex. [...]
	
	public static final String REGEX_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_ACTION = "^#*\\s*([dD][oO]|[rR][eE][pP][eE][aA]?[tT]|[rR][eE][pP][eE][tT][iI][rR])\\s+"; //eX. DO ... WHILE ...
	public static final String REGEX_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_CONDITIONS = "\\s+([wW][hH][iI][lL][eE]?|[uU][nN][tT][iI][lL]|[mM][iI][eE][nN][tT][rR][aA][sS]?|[eE][nNmM][qQcC][uU][aA][nN][tT][oO]|[cCQq][uU][aA][nN][dD][oO])(\\n|\\r|$|\\s+)"; //eX. DO ... WHILE ...
	
	public static final String REGEX_START_DELIMITING_ITERATIVE_EPISODE_WHILE_DO_CONDITIONS = "^#*\\s*([wW][hH][iI][lL][eE]?|[uU][nN][tT][iI][lL]|[mM][iI][eE][nN][tT][rR][aA][sS]?|[eE][nNmM][qQcC][uU][aA][nN][tT][oO]|[cCQq][uU][aA][nN][dD][oO])\\s+"; //eX. WHILE ... DO ...
	public static final String REGEX_START_DELIMITING_ITERATIVE_EPISODE_WHILE_DO_ACTION = "([\\,\\;]?\\s+([dD][oO]|[rR][eE][pP][eE][aA]?[tT]|[rR][eE][pP][eE][tT][iI][rR])|[\\,\\;])(\\n|\\r|$|\\s+)"; //eX. WHILE ... DO ...
	
	public static final String REGEX_START_DELIMITING_ITERATIVE_EPISODE_FOREACH_DO_ITEMS = "^#*\\s*([fF][oO][rR]\\s*[-]?\\s*[eE][aA][cC][hH]|[fF][oO][rR]|[pP][oO][rR]\\s*[-]?\\s*[cC][aA][dD][aA]|[pP][aA][rR][aA]\\s*[-]?\\s*[cC][aA][dD][aA])\\s+"; //eX. FOR EACH ... DO ...
	public static final String REGEX_START_DELIMITING_ITERATIVE_EPISODE_FOREACH_DO_ACTION = "([\\,\\;]?\\s+([dD][oO]|[rR][eE][pP][eE][aA]?[tT]|[rR][eE][pP][eE][tT][iI][rR])|[\\,\\;])(\\n|\\r|$|\\s+)"; //eX. WHILE ... DO ...
	
	//Accept "Step 1. action"
	//public static final String REGEX_EPISODE_ID =     "^((\\D+\\s?\\d+([\\.\\,\\;\\:]\\d+)*)|(\\d+([\\.\\,\\;\\:]\\d+)*))[\\.\\:\\,\\;\\s+]"; //Ex. Step 1. <sentence>, 1: <sentence>, Step 1: <sentence> 
	//public static final String REGEX_ALTERNATIVE_ID = "^((\\D+\\s?\\d+([\\.\\,\\;\\:]\\d+)*)|(\\d+([\\.\\,\\;\\:]\\d+)*))[\\.\\:\\,\\;\\s+]"; //Ex. Sep 1.1. <sentence>, Step 1.1: <sentence>
	//Accept only digits
	//public static final String REGEX_EPISODE_ID =     "^((\\d+([\\.\\,\\;\\:]\\d+)*))[\\.\\:\\,\\;\\s+]"; //Ex. 1. <sentence>, 1: <sentence>, Step 1: <sentence> 
	public static final String REGEX_EPISODE_ID =     "^((\\d+)([\\.\\,\\;\\:]\\d+|[\\.\\,\\;\\:]*[A-Za-z])?([\\.\\,\\;\\:]*\\d+|[\\.\\,\\;\\:]*[A-Za-z])*)($|\\.|\\:|\\,|\\;|\\s+)"; //Ex. 1. <sentence>, 1: <sentence>, Step 1: <sentence> , a. <sentence>
	public static final String REGEX_ALTERNATIVE_ID = "^((\\d+)([\\.\\,\\;\\:]\\d+|[\\.\\,\\;\\:]*[A-Za-z])?([\\.\\,\\;\\:]*\\d+|[\\.\\,\\;\\:]*[A-Za-z])*)($|\\.|\\:|\\,|\\;|\\s+)"; //Ex. 1.1. <sentence>, Step 1.1: <sentence> --- 1.a.2 <sentence> --- 4a. <sentene> ---- 4a1. <sentence>  --- 4a.1. <sentence>  --- 4.1a. <sentence>
	
	public static final String REGEX_DELIMITING_EPISODE_ID_FROM_ALTERNATIVE_ID = "[\\.\\,\\;\\:]"; //Ex. 1.1. Alternative --> Episode Id = 1, Alternative Id = 1
	public static final String REGEX_DELIMITING_EPISODE_ID_FROM_SUB_EPISODE_ID = "[\\.\\,\\;\\:]"; //Ex. 1.1. Sub-Episode --> Episode Id = 1, Sub-Episode Id = 1
	public static final String REGEX_DELIMITING_ALTERNATIVE_ID_FROM_SUB_ALTERNATIVE_ID = "[\\.\\:\\,\\;\\:]"; //Ex. 1.1. Sub-Alternative --> Alternative Id = 1, Sub-Alternative Id = 1
	
	public static final String REGEX_ALTERNATE_GO_TO = ".*((go[e]?|back|return|resume|proceed)[s]?\\s*to\\s+(step\\s+|episode\\s+)?)((\\d+([\\.\\,\\;\\:]\\d+)*)\\s*(\\.|\\:|\\,|\\;|$|\\s+))";
	
	public static final String REGEX_ALTERNATIVE_ENDS = ".*((system|use\\s*case|scenario)\\s+(end|finish[e]?|terminate)[s]?)\\s*(\\p{Punct})";
	
	public static final String REGEX_LEFT_SIDE_CONSTRAINT = "(^|\\s+)([cC][oO][nN][sS][tT][rR][aA][iI][nN][tT][sS]?|[rR][eE][sS][tT][rR][iI][cCÁ«][oOı’][eE][sS]|[rR][eE][sS][tT][rR][iI][cCÁ«][aA„√][oO]|[rR][eE][sS][tT][rR][iI][cC][cC]?[iI][oOÛ”][nN][eE]?[sS]?)\\:"; //Ex. Constraint: 
	/*//Validate
	public static final String REGEX_LEFT_SIDE_NON_EXPLICIT_CONSTRAINT = "([wW][hH][oO][sS][eE][sS]?|[tTwW][hH][aA][tT]|[wW][hH][oO][mM]?|[wW][hH][iI][cC][hH]|[wW][hH][eE][rR][eE]" +
																		   "|[cC][uU][yYjJ][oOaA][sS]?|[qQ][uU][eEÈ…]|[qQ][uU][iI][eEÈ…][nN][eE]?[sS]?|[dD][oOÛ”][nN][dD][eE]|[cCqQ][uU][aA·¡][lL]" +
																			")\\s+"; //Ex. WHOSE ...
	*/
	public static final String REGEX_LEFT_SIDE_NON_EXPLICIT_CONSTRAINT = "[\\,\\;]?\\s+([wW][hH][oO][sS][eE][sS]?|[wW][hH][oO][mM]?|[wW][hH][iI][cC][hH]|[wW][hH][eE][rR][eE]|[cC][uU][yYjJ][oOaA][sS]?|[qQ][uU][eEÈ…]|[qQ][uU][iI][eEÈ…][nN][eE]?[sS]?|[dD][oOÛ”][nN][dD][eE]|[cCqQ][uU][aA·¡][lL])\\s+"; //Ex. WHOSE ...
	
	public static final String REGEX_LEFT_SIDE_POST_CONDITION = "(^|\\s+)([pP][oOÛ”][sS][tT]?\\s*[\\W]*\\s*[cC][oO][nN][dD][iI][tT][iI][oO][nN][sS]?|[pP][oOÛ”][sS][tT]?\\s*[\\W]*\\s*[cC][oO][nN][dD][iI][cCÁ«][oOı’][eE][sS]?|[pP][oOÛ”][sS][tT]?\\s*[\\W]*\\s*[cC][oO][nN][dD][iI][cCÁ«][aA„√][oO]|[pP][oOÛ”][sS][tT]?\\s*[\\W]*\\s*[cC][oO][nN][dD][iI][cC]?[iI][oOÛ”][nN][eE]?[sS]?)\\:"; //Ex. Post-condition:
	public static final String REGEX_LEFT_SIDE_PRE_CONDITION = "(^|\\s+)([pP][rR][eEÈ…]\\s*[\\W]*\\s*[cC][oO][nN][dD][iI][tT][iI][oO][nN][sS]?|[pP][rR][eEÈ…]?\\s*[\\W]*\\s*[cC][oO][nN][dD][iI][cCÁ«][oOı’][eE][sS]?|[pP][rR][eEÈ…]?\\s*[\\W]*\\s*[cC][oO][nN][dD][iI][cCÁ«][aA„√][oO]|[pP][rR][eEÈ…]?\\s*[\\W]*\\s*[cC][oO][nN][dD][iI][cC]?[iI][oOÛ”][nN][eE]?[sS]?)\\:"; //Ex. Pre-condition:
	public static final String REGEX_LEFT_SIDE_TEMPORAL_LOCATION = "(^|\\s+)(TEMPORAL\\s+LOCATION|LOCALIZA[«C][A√]O\\s+TEMPORAL|LOCALIZACI[O”]N\\s+TEMPORAL)\\:"; //Ex. LOCALIZACION TEMPORAL:
	public static final String REGEX_LEFT_SIDE_GEOGRAPHICAL_LOCATION = "(^|\\s+)(GEOGRAPHICAL\\s+LOCATION|LOCALIZA[«C][A√]O\\s+GEOGR[A¡]FICA|LOCALIZACI[O”]N\\s+GEOGR[A¡]FICA)\\:"; //Ex. LOCALIZACION geogr·fica:
	
	//Punctuation marks:  One of !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
	public static final String REGEX_PUNCTUATION_MARK_AT_BEGIN_LINE = "(^)(\\p{Punct})*"; // $##ASJAJDHAKJDAHDK)_+
	public static final String REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT = "(\\s+|^)(\\p{Punct})*"; // $##ASJAJDHAKJDAHDK)_+
	public static final String REGEX_PUNCTUATION_MARK_AT_END_TEXT = "(\\p{Punct})*(?=(\\s+|$))";
	public static final String REGEX_PUNCTUATION_MARK_AT_END_LINE = "(\\p{Punct})*($)"; // $##ASJAJDHAKJDAHDK)_+
	public static final String REGEX_PUNCTUATION_MARK = "(\\p{Punct})"; // $## _+=
	public static final String REGEX_TEXT_AFTER_PUNCTUATION_MARK = "(\\p{Punct})+."; //HELLO. YOU
	
	//Special Punctuation: [\#\$\%\&\*\+\,\-\.\/\:\;\@\^\_\~]
	public static final String REGEX_PUNCTUATION_SPECIAL_AT_BEGIN_LINE = "(^)([\\#\\$\\%\\&\\*\\+\\,\\-\\.\\/\\:\\;\\@\\^\\_\\~])*"; // $##ASJAJDHAKJDAHDK)_+
	public static final String REGEX_PUNCTUATION_SPECIAL_AT_END_LINE = "([\\#\\$\\%\\&\\*\\+\\,\\-\\.\\/\\:\\;\\@\\^\\_\\~])*($)"; // $##ASJAJDHAKJDAHDK)_+
	
	public static final String REGEX_ANY_CHARACTER = "."; //HELLO. YOU
	
	public static final String REGEX_TEXT_IN_UPPERCASE = "[A-Z]+((\\s+|_+)[A-Z]+)+";//"(\\p{Punct})*[A-Z]+(\\s+[A-Z]+)+(\\p{Punct})*";//Mais de 2 palavras consequtivas em MAIUSCULA
	
	//PATTERNS: COMPILED REGULAR EXPRESSIONS
	
	public static final Pattern PATTERN_LEFT_SIDE_PRE_COND = Pattern.compile(RegularExpression.REGEX_LEFT_SIDE_PRE_CONDITION); //Pattern usado para identificar o inicio de uma lista de Pre-condicoes:
	public static final Pattern PATTERN_LEFT_SIDE_POST_COND = Pattern.compile(RegularExpression.REGEX_LEFT_SIDE_POST_CONDITION); //Pattern usado para identificar o inicio de uma lista de Pos-condicoes:
	public static final Pattern PATTERN_LEFT_SIDE_CONSTRAINT = Pattern.compile(RegularExpression.REGEX_LEFT_SIDE_CONSTRAINT); //Pattern usado para identificar o inicio de uma lista de Restricoes:
	public static final Pattern PATTERN_LEFT_SIDE_NON_EXPLICIT_CONSTRAINT = Pattern.compile(RegularExpression.REGEX_LEFT_SIDE_NON_EXPLICIT_CONSTRAINT); //Pattern usado para identificar o inicio de uma lista de Restricoes (Ex. c˙ja data tem ...)
	public static final Pattern PATTERN_LEFT_SIDE_TEMPORAL_LOC = Pattern.compile(RegularExpression.REGEX_LEFT_SIDE_TEMPORAL_LOCATION); //Pattern usado para identificar o inicio de uma lista de Temporal Location:
	public static final Pattern PATTERN_LEFT_SIDE_GEOGRAPHICAL_LOC = Pattern.compile(RegularExpression.REGEX_LEFT_SIDE_GEOGRAPHICAL_LOCATION); //Pattern usado para identificar o inicio de uma lista de Geographical Location:
	
	public static final Pattern PATTERN_EPISODE_ID = Pattern.compile(RegularExpression.REGEX_EPISODE_ID); //Extract Id from Episode OR Alternative
	public static final Pattern PATTERN_ALTERNATIVE_ID = Pattern.compile(RegularExpression.REGEX_ALTERNATIVE_ID); //Extract Id from Alternative
	
	public static final Pattern PATTERN_ALTERNATE_GO_TO = Pattern.compile(RegularExpression.REGEX_ALTERNATE_GO_TO); //Extract Id/Step from Alternative - solution step
	public static final Pattern PATTERN_ALTERNATIVE_ENDS = Pattern.compile(RegularExpression.REGEX_ALTERNATIVE_ENDS); //system or use case or scenario ends - solution step
	
	
	public static final Pattern PATTERN_START_DELIMITING_CONDITIONAL_EPISODE_CONDITION = Pattern.compile(RegularExpression.REGEX_START_DELIMITING_CONDITIONAL_EPISODE_CONDITIONS); //PROCURAR POR: IF
	public static final Pattern PATTERN_START_DELIMITING_CONDITIONAL_EPISODE_ACTION = Pattern.compile(RegularExpression.REGEX_START_DELIMITING_CONDITIONAL_EPISODE_ACTION); //Find first occurrence of "THEN" (end of conditions)
	
	public static final Pattern PATTERN_COMPLICATED_VALIDATION_ACTION_INDICATOR = Pattern.compile(RegularExpression.REG_EX_COMPLICATED_VALIDATION_ACTION_INDICATOR); //Find first occurrence of "CHECK" "IF" (end of conditions)
	
	public static final Pattern PATTERN_START_DELIMITING_CONDITIONAL_EPISODE_NON_EXPLICIT_CONDITION = Pattern.compile(RegularExpression.REGEX_START_DELIMITING_CONDITIONAL_EPISODE_NON_EXPLICIT_CONDITIONS); //PROCURAR POR: <sentence> IF/WHEN <condition>
	
	
	public static final Pattern PATTERN_START_DELIMITING_OPTIONAL_EPISODE = Pattern.compile(RegularExpression.REGEX_START_DELIMITING_OPTIONAL_EPISODE);// PROCURAR POR: [
	public static final Pattern PATTERN_END_DELIMITING_OPTIONAL_EPISODE = Pattern.compile(RegularExpression.REGEX_END_DELIMITING_OPTIONAL_EPISODE);//Find first occurrence of "]" (end of optional episode)
	
	public static final Pattern PATTERN_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_ACTION = Pattern.compile(RegularExpression.REGEX_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_ACTION);// PROCURAR POR: DO ..
	public static final Pattern PATTERN_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_CONDITIONS = Pattern.compile(RegularExpression.REGEX_START_DELIMITING_ITERATIVE_EPISODE_DO_WHILE_CONDITIONS);//Find first occurrence of "WHILE" (end of ACTION)
	
	public static final Pattern PATTERN_START_DELIMITING_ITERATIVE_EPISODE_WHILE_DO_ACTION = Pattern.compile(RegularExpression.REGEX_START_DELIMITING_ITERATIVE_EPISODE_WHILE_DO_ACTION);// PROCURAR POR: DO ..(end of WHILE)
	public static final Pattern PATTERN_START_DELIMITING_ITERATIVE_EPISODE_WHILE_DO_CONDITIONS = Pattern.compile(RegularExpression.REGEX_START_DELIMITING_ITERATIVE_EPISODE_WHILE_DO_CONDITIONS);//Find first occurrence of "WHILE" (end of ACTION)
	
	public static final Pattern PATTERN_START_DELIMITING_ITERATIVE_EPISODE_FOREACH_DO_ACTION = Pattern.compile(RegularExpression.REGEX_START_DELIMITING_ITERATIVE_EPISODE_FOREACH_DO_ACTION);// PROCURAR POR: DO ..(end of WHILE)
	public static final Pattern PATTERN_START_DELIMITING_ITERATIVE_EPISODE_FOREACH_DO_ITEMS = Pattern.compile(RegularExpression.REGEX_START_DELIMITING_ITERATIVE_EPISODE_FOREACH_DO_ITEMS);//Find first occurrence of "WHILE" (end of ACTION)
		
	public static final Pattern PATTERN_END_INDICATOR_LIST_ITEMS = Pattern.compile(RegularExpression.REGEX_END_INDICATOR_ITEMS); //Pattern usado para identificar o final de uma lista de items ou condicoes
	
	public static final Pattern PATTERN_UPPERCASE_PHRASES = Pattern.compile(REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + RegularExpression.REGEX_TEXT_IN_UPPERCASE + REGEX_PUNCTUATION_MARK_AT_END_TEXT); //Pattern usado para identificar frases em MAIUSCULA
	//public static final Pattern PATTERN_UPPERCASE_PHRASES = Pattern.compile(RegularExpression.REGEX_TEXT_IN_UPPERCASE); //Pattern usado para identificar frases em MAIUSCULA
	
	
	//STRUCTURED SCENARIO TO PETRI-NET
}
