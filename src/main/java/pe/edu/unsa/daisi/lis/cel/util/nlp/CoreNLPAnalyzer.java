package pe.edu.unsa.daisi.lis.cel.util.nlp;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.EnglishGrammaticalRelations;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalRelations;
import pe.edu.unsa.daisi.lis.cel.util.RegularExpression;
import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioCleaner;

/**
 * Stanford CoreNLP 
 * Singleton Pattern
 * @author Edgar
 *
 */
public class CoreNLPAnalyzer implements INLPAnalyzer {

	private static CoreNLPAnalyzer uniqueInstance; 

	public static final String S_CHAR = "s";
	public static final String POSSESSIVE = "'s";
	public static final String REG_EXP_OTHER_POSSESSIVE_CANDIDATES = "(\\`[sS]|\\´[sS])";
	public static final String WHITE_SPACE = " ";
	public static final String PREPOSITION_OF = "of";
	public static final String REGEX_VERBS = "(VB|VBP|VBZ)";
	//Construct parse tree
	LexicalizedParser lexicalizedParser;
	TreebankLanguagePack treebankLanguagePack;
	GrammaticalStructureFactory grammaticalStructureFactory ;
	TokenizerFactory<CoreLabel> tokenizerFactory;

	private CoreNLPAnalyzer() {

	}

	/**
	 * Returns a unique instance of CoreNLPAnalyzer and initializes the NLP pipeline and parsing models
	 * @return
	 */
	public static synchronized CoreNLPAnalyzer getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new CoreNLPAnalyzer();
			uniqueInstance.initializePipeline();
			uniqueInstance.initializeParsing();
		}    
		return uniqueInstance;
	}

	public void initializePipeline() {
		//Pipeline	 
		// creates a StanfordCoreNLP object, with POS tagging and lemmatization

	}

	@Override
	public void initializeChunking() {
		// Initialize chunk model


	}

	@Override
	public void initializeParsing() {
		// creates a Lexical Parsing object
		// - Parse tree
		lexicalizedParser = LexicalizedParser.loadModel();//default model
		treebankLanguagePack = new PennTreebankLanguagePack();
		// - Dependencies
		grammaticalStructureFactory = treebankLanguagePack.grammaticalStructureFactory();
		lexicalizedParser.setOptionFlags(new String[]{"-maxLength", "500", "-retainTmpSubcategories"});
		//Initialize Tokenizer
		tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	}

	@Override
	public List<CustomToken> getTokens(String text) {
		if(text == null || text.isEmpty())
			return null;
		text = improveTextForNLP(text);
		
		Sentence sentenceNLP = new Sentence(text);
		/*
    	List<CustomToken> sentenceTokens = new ArrayList<>();
    	for(Token token : sentenceNLP.tokens()) {
    		CustomToken customToken = new CustomToken(token.word(), token.posTag(), token.lemma(), token.index);
    		sentenceTokens.add(customToken);
    	}
		 */
		//Get tokens
		List<String> words =  sentenceNLP.words();
		//Get Pos tags
		List<String> tags  =  sentenceNLP.posTags();
		//Get lemmas
		List<String> lemmas =  sentenceNLP.lemmas();
		List<CustomToken> sentenceTokens = new ArrayList<>();
		if(words != null && tags != null && lemmas != null
				&& words.size() == tags.size() && tags.size() == lemmas.size())
			for(int i = 0; i < words.size(); i++) {
				CustomToken customToken = new CustomToken(words.get(i), tags.get(i), lemmas.get(i), i);
				sentenceTokens.add(customToken);
			}
		//Adjust POS TAGS: NOUNS & VERBS
		sentenceTokens = PosTagImprover.adjustPosTags(sentenceTokens);//Update

		return sentenceTokens;
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
		if(text == null || text.isEmpty())
			return null;
		CustomSentenceNlpInfo customSentenceNlpInfo = null;
		
		text = improveTextForNLP(text);
		
		//Get tokens with Adjusted POS tags
		List<CustomToken> sentenceTokens = getTokens(text); //using Simple Core NLP:
		
		//Tokenize
		List<CoreLabel> wordList = tokenizerFactory.getTokenizer(new StringReader(text)).tokenize();
		//Adjust Pos tags and stem/lemma in wordList returned by tokenizer
		
		for(int i = 0; i < sentenceTokens.size(); i++) {
			wordList.get(i).setTag(sentenceTokens.get(i).getPosTag());
			wordList.get(i).setLemma(sentenceTokens.get(i).getStem());
			
		}

		//Get Lexicalized phrase-structure tree
		Tree parseTree = lexicalizedParser.apply(wordList);    
		//Get dependency parsing (relationships between words from tree)
		GrammaticalStructure grammaticalStructure = grammaticalStructureFactory.newGrammaticalStructure(parseTree);
		List<TypedDependency> typedDependencyList = (List<TypedDependency>) grammaticalStructure.typedDependencies();//gs.typedDependenciesCCprocessed(true);

		System.out.println(typedDependencyList.toString());
		//Identification of Subject and Object roles: Algorithm
		//https://www.researchgate.net/publication/50235327_Extracting_Noun_Phrases_in_Subject_and_Object_Roles_for_Exploring_Text_Semantics
		/*
	       Stanford dependencies define the relations, namely nsubj (nominal subject), 
	       		nsubjpass (passive nominal subject) and rcmod(relative clause modifier) to highlight the noun phrases in 
	      subject  role.  

	      (2) Extracting Noun Phrases in Subject and Object Roles for Exploring Text Semantics | Request PDF. Available from: https://www.researchgate.net/publication/50235327_Extracting_Noun_Phrases_in_Subject_and_Object_Roles_for_Exploring_Text_Semantics [accessed Nov 04 2018].
	       The  dependencies  dobj (direct object) and pobj (object of a preposition) occurring most often depict the noun phrases in object role.

		 */

		// Create HashMaps for subjects, objects and action-verbs
		HashMap<Integer, CustomToken> subjectTokens = new HashMap<Integer, CustomToken>();
		HashMap<Integer, CustomToken> directObjectTokens = new HashMap<Integer, CustomToken>();
		HashMap<Integer, CustomToken> indirectObjectTokens = new HashMap<Integer, CustomToken>();
		HashMap<Integer, CustomToken> mainActionVerbTokens = new HashMap<Integer, CustomToken>();
		HashMap<Integer, CustomToken> complementActionVerbTokens = new HashMap<Integer, CustomToken>();
		HashMap<Integer, CustomToken> complementSubjectTokens = new HashMap<Integer, CustomToken>();
		HashMap<Integer, CustomToken> modifierActionVerbTokens = new HashMap<Integer, CustomToken>();
		HashMap<Integer, CustomToken> modifierSubjectTokens = new HashMap<Integer, CustomToken>();
		//Extract subjects, direct/indirect objects and action-verbs
		for(TypedDependency typedDependency : typedDependencyList) {
			//SVOR1:	Extract subject and action-verb from nsubj
			//"Customer examines the bid" --> [nsubj(examines-2, customer-1), root(ROOT-0, examines-2), det(bid-4, the-3), dobj(examines-2, bid-4)]
			//IF relation is NOMINAL_SUBJECT and governor is VERB and dependent is NOUN THEN Put governor in Action-Verbs and dependent in Subjects
			if(typedDependency.reln().getShortName().equals(EnglishGrammaticalRelations.NOMINAL_SUBJECT.getShortName())) {
				//if(typedDependency.gov().tag().contains(PosTagEnum.VB.name()) && (typedDependency.dep().tag().contains(PosTagEnum.NN.name()) || typedDependency.dep().tag().contains(PosTagEnum.PRP.name()))) { 
				if(typedDependency.gov().tag().contains(PosTagEnum.VB.name()) && (typedDependency.dep().tag().contains(PosTagEnum.NN.name()) || typedDependency.dep().tag().contains(PosTagEnum.PRP.name()))) {
					//System.out.print("- relation: " + typedDependency.reln().getShortName());
					//System.out.print("- Subject: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
					//System.out.println("- verb: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check check VB, VBP, VBZ, VBN?
					CustomToken token = new CustomToken(typedDependency.gov().word(), typedDependency.gov().tag(), typedDependency.gov().lemma(), typedDependency.gov().index());
					mainActionVerbTokens.put(token.getIndex(), token);
					//IF VERB is in third-form and in first position (index = 1) AND NOUN is after VERB THEN NOUN is an OBJECT
	    			   //Ex. "Selects envelope";//Error CoreNLP - [root(ROOT-0, Selects-1), nsubj(Selects-1, envelope-2)] - > envelope is object!
	    			   token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
	    			   if(typedDependency.gov().tag().equals(PosTagEnum.VBZ.name()) && typedDependency.gov().index() == 1 && typedDependency.dep().index() > 1)
	    				   directObjectTokens.put(token.getIndex(), token);
	    			   else
	    				   subjectTokens.put(token.getIndex(), token);
				}
			}
			//SVOR2:	Extract subject, direct-object and action-verb from nsubjpass and nmod
			//“File was updated by the user” --> [nsubjpass(updated-3, File-1), auxpass(updated-3, was-2), root(ROOT-0, updated-3), case(user-6, by-4), det(user-6, the-5), nmod(updated-3, user-6)]
			//FIX: It works with nmod(defeated, Clinton) -> subj = Clinton, Obj = Dole
			//IF relation is NOMINAL_PASSIVE_SUBJECT and governor is VERB and dependent is NOUN THEN Put (governor in Action-Verbs and ???) dependent in Subjects
			if(typedDependency.reln().getShortName().equals(EnglishGrammaticalRelations.NOMINAL_PASSIVE_SUBJECT.getShortName())) {
				if(typedDependency.gov().tag().contains(PosTagEnum.VB.name()) && (typedDependency.dep().tag().contains(PosTagEnum.NN.name()) || typedDependency.dep().tag().contains(PosTagEnum.PRP.name()))) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Subject: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
//					System.out.println("- verb: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check check VB, VBP, VBZ, VBN?
					
					//String REGEX_NMOD =".*(nmod\\("+typedDependency.gov().word()+"\\-"+ typedDependency.gov().index() +",\\s(\\w+)\\-(\\d+)\\)).*"; 
					//Matcher matcherNMod = Pattern.compile(REGEX_NMOD).matcher(typedDependencyList.toString());
					TypedDependency nModRel = findTypedDependecyByRelationGovOrDep(typedDependencyList, UniversalEnglishGrammaticalRelations.NOMINAL_MODIFIER.getShortName(), typedDependency.gov().word(), typedDependency.gov().index(), null, 0);
					if(nModRel != null) {
										
						//Action-Verb
						CustomToken token = new CustomToken(typedDependency.gov().word(), typedDependency.gov().tag(), typedDependency.gov().lemma(), typedDependency.gov().index());
						mainActionVerbTokens.put(token.getIndex(), token);
						
						//Direct-Object
						token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
						directObjectTokens.put(token.getIndex(), token);
						
						//Subject
						token = new CustomToken(nModRel.dep().word(), nModRel.dep().tag(), nModRel.dep().lemma(), nModRel.dep().index());
						subjectTokens.put(token.getIndex(), token);
					}
					
				}
			}
			
			//UNNECESSARY
			//The "semantic dependent" grammatical relation has been introduced as a supertype for the controlling subject relation.
			//The "dependent" grammatical relation, which is the inverse of "governor".
			//Ex. I really like it
			//IF relation is SEMANTIC_DEPENDENT and governor is NOUN or PERSONAL_PRONOUN and dependent is VERB THEN Put governor in Subjects and dependent in Action-Verbs
			if(typedDependency.reln().getShortName().equals(GrammaticalRelation.DEPENDENT.getShortName())) {	    	    
				if((typedDependency.gov().tag().contains(PosTagEnum.NN.name()) || typedDependency.gov().tag().contains(PosTagEnum.PRP.name())) && typedDependency.dep().tag().contains(PosTagEnum.VB.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Subject: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check NN
//					System.out.println("- verb: " + typedDependency.dep() + " - " +typedDependency.dep().index()); //check VB, VBP, VBZ, VBN?
					CustomToken token = new CustomToken(typedDependency.gov().word(), typedDependency.gov().tag(), typedDependency.gov().lemma(), typedDependency.gov().index());
					subjectTokens.put(token.getIndex(), token);
					token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					mainActionVerbTokens.put(token.getIndex(), token);
				}
			}


			//DIRECT OBJECTS
			//SVOR3:	Extract direct-object and action-verb from dobj
			//“User creates filter for searching” --> ?[nsubj(creates-2, user-1), root(ROOT-0, creates-2), dobj(creates-2, filter-3), mark(searching-5, for-4), advcl(creates-2, searching-5)]
			//"SYSTEM GO TO STEP 1" --> dobj(STEP, 1)
			//User fills all required personal client data forms --> ...iobj(required-4, data-7), dobj(required-4, forms-8)...
			//IF relation is DIRECT_OBJECT and governor is VERB or ADJECTIVE and dependent is NOUN or PERSONAL_PRONOUN or CARDINAL THEN Put dependent in Objects and governor in Action-Verbs
			if(typedDependency.reln().getShortName().equals(EnglishGrammaticalRelations.DIRECT_OBJECT.getShortName())) {
				if((typedDependency.gov().tag().equals(PosTagEnum.JJ.name()) || typedDependency.gov().tag().contains(PosTagEnum.VB.name())) && (typedDependency.dep().tag().contains(PosTagEnum.NN.name()) || typedDependency.dep().tag().contains(PosTagEnum.PRP.name()) || typedDependency.dep().tag().contains(PosTagEnum.CD.name()))) {
					//System.out.print("- relation: " + typedDependency.reln().getShortName());
					//System.out.print("- Object: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
					//System.out.println("- verb/adjective: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check check VB, VBP, VBZ, VBN?
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					directObjectTokens.put(token.getIndex(), token);
					token = new CustomToken(typedDependency.gov().word(), typedDependency.gov().tag(), typedDependency.gov().lemma(), typedDependency.gov().index());
					if(!token.getPosTag().equals(PosTagEnum.JJ.name()))
						mainActionVerbTokens.put(token.getIndex(), token);
				}
			} 
			
			//INDIRECT OBJECTS
			//SVOR4:	Extract indirect-object from iobj
			//“System sends the server a registra-tion request” --> [nsubj(sends-2, system-1), root(ROOT-0, sends-2), det(server-4, the-3), iobj(sends-2, server-4), det(request-7, a-5), com-pound(request-7, registration-6), dobj(sends-2, request-7)]
			//The system sends the user an email --> ... iobj(sends-3, user-5), det(email-7, an-6), dobj(sends-3, email-7)
			//IF relation is DIRECT_OBJECT and governor is VERB or ADJECTIVE and dependent is NOUN or PERSONAL_PRONOUN or CARDINAL THEN Put dependent in Objects and governor in Action-Verbs
			if(typedDependency.reln().getShortName().equals(EnglishGrammaticalRelations.INDIRECT_OBJECT.getShortName())) {
				//if((typedDependency.gov().tag().equals(PosTagEnum.JJ.name()) || typedDependency.gov().tag().contains(PosTagEnum.VB.name())) && (typedDependency.dep().tag().contains(PosTagEnum.NN.name()) || typedDependency.dep().tag().contains(PosTagEnum.PRP.name()) || typedDependency.dep().tag().contains(PosTagEnum.CD.name()))) {
				if((typedDependency.gov().tag().contains(PosTagEnum.VB.name())) && (typedDependency.dep().tag().contains(PosTagEnum.NN.name()) || typedDependency.dep().tag().contains(PosTagEnum.PRP.name()) || typedDependency.dep().tag().contains(PosTagEnum.CD.name()))) {
					//System.out.print("- relation: " + typedDependency.reln().getShortName());
					//System.out.print("- Object: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
					//System.out.println("- verb/adjective: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check check VB, VBP, VBZ, VBN?
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					indirectObjectTokens.put(token.getIndex(), token);
					token = new CustomToken(typedDependency.gov().word(), typedDependency.gov().tag(), typedDependency.gov().lemma(), typedDependency.gov().index());
					if(!token.getPosTag().equals(PosTagEnum.JJ.name()))
						mainActionVerbTokens.put(token.getIndex(), token);
				}
			} 
			
			//UNNECESSARY
			//PREPOSITIONAL OBJECTS
			//"I sat on the chair" --> pobj(on, chair)
			//IF relation is PREPOSITIONAL_OBJECT and governor is NOUN and dependent is PREPOSITION THEN Put governor in Objects
			if(typedDependency.reln().getShortName().equals(EnglishGrammaticalRelations.PREPOSITIONAL_OBJECT.getShortName())) {
				if(typedDependency.gov().tag().contains(PosTagEnum.NN.name()) && typedDependency.dep().tag().contains(PosTagEnum.IN.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Object: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
//					System.out.println("- Preposition: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check IN
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					//directObjectTokens.put(token.getIndex(), token);
					indirectObjectTokens.put(token.getIndex(), token);
					//ACTION-VERB?
				}
			} 
			
			//SVOR5:	Extract subject, action-verb and indirect-object from nsubj and nmod
			//"User clicks on the screen" --> [nsubj(clicks-2, user-1), root(ROOT-0, clicks-2), case(screen-5, on-3), det(screen-5, the-4), nmod(clicks-2, screen-5)]
			//IF relation is NOMINAL_MODIFIER and governor is VERB and dependent is NOUN THEN Put dependent in Objects and governor in Action-Verbs
			if(typedDependency.reln().getShortName().equals(UniversalEnglishGrammaticalRelations.NOMINAL_MODIFIER.getShortName())) {
				if(typedDependency.gov().tag().contains(PosTagEnum.VB.name()) && typedDependency.dep().tag().contains(PosTagEnum.NN.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Object: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
//					System.out.println("- Verb: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check check VB, VBP, VBZ, VBN?
					TypedDependency nSubjRel = findTypedDependecyByRelationGovOrDep(typedDependencyList, UniversalEnglishGrammaticalRelations.NOMINAL_SUBJECT.getShortName(), typedDependency.gov().word(), typedDependency.gov().index(), null, 0);
					if(nSubjRel != null) {
										
						//Action-Verb
						CustomToken token = new CustomToken(typedDependency.gov().word(), typedDependency.gov().tag(), typedDependency.gov().lemma(), typedDependency.gov().index());
						//if(mainActionVerbTokens.isEmpty())
							mainActionVerbTokens.put(token.getIndex(), token);
						//Indirect-Object
						token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
						indirectObjectTokens.put(token.getIndex(), token);
						
						//Subject
						token = new CustomToken(nSubjRel.dep().word(), nSubjRel.dep().tag(), nSubjRel.dep().lemma(), nSubjRel.dep().index());
						subjectTokens.put(token.getIndex(), token);
					}
					
				}
			}
			
			//SVOR6:	Extract subject, action-verb and indirect-object from dobj and nmod
			//"User clicks the mouse on the screen" --> [nsubj(clicks-2, user-1), root(ROOT-0, clicks-2), det(mouse-4, the-3), dobj(clicks-2, mouse-4), case(screen-7, on-5), det(screen-7, the-6), nmod(mouse-4, screen-7)]
			//IF relation is NOMINAL_MODIFIER and governor is VERB and dependent is NOUN THEN Put dependent in Objects and governor in Action-Verbs
			if(typedDependency.reln().getShortName().equals(UniversalEnglishGrammaticalRelations.NOMINAL_MODIFIER.getShortName())) {
				if(typedDependency.gov().tag().contains(PosTagEnum.NN.name()) && typedDependency.dep().tag().contains(PosTagEnum.NN.name())) {
					//System.out.print("- relation: " + typedDependency.reln().getShortName());
					//System.out.print("- Object: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
					//System.out.println("- Verb: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check NN?
					TypedDependency dObjRel = findTypedDependecyByRelationGovOrDep(typedDependencyList, UniversalEnglishGrammaticalRelations.DIRECT_OBJECT.getShortName(), null, 0, typedDependency.gov().word(), typedDependency.gov().index());
					if(dObjRel != null) {
						//Action-Verb
						CustomToken token = new CustomToken(dObjRel.gov().word(), dObjRel.gov().tag(), dObjRel.gov().lemma(), dObjRel.gov().index());
						//if(mainActionVerbTokens.isEmpty())
							mainActionVerbTokens.put(token.getIndex(), token);
												
						//Direct-Object
						token = new CustomToken(dObjRel.dep().word(), dObjRel.dep().tag(), dObjRel.dep().lemma(), dObjRel.dep().index());
						directObjectTokens.put(token.getIndex(), token);
						
						//Indirect-Object
						token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
						indirectObjectTokens.put(token.getIndex(), token);
					}
					
				}
			}

			//ACTION-VERBS
			//SVOR7:	Extract action-verb from ROOT
			//“selects envelope” --> root(ROOT-0, selects-1), nsubj(selects-1, envelope-2)]
			//IF relation is ROOT and governor is ROOT and dependent is VERB THEN Add dependent in Action-Verbs
			if(typedDependency.reln().getShortName().equals(GrammaticalRelation.ROOT.getShortName())) {
				if(typedDependency.dep().tag().contains(PosTagEnum.VB.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.println("- Verb: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check check VB, VBP, VBZ, VBN?
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					mainActionVerbTokens.put(token.getIndex(), token);
				}  
			}
		}
		
		//UNNECESSARY
		//Fix subjects -> Objects, direct/indirect objects -> subjects created from "semantic DEPENDENT" and NOMINAL_SUBJECT grammatical relation
		for(TypedDependency typedDependency : typedDependencyList) {
			//The "semantic dependent" grammatical relation has been introduced as a supertype for the controlling subject relation.
			//The "dependent" grammatical relation, which is the inverse of "governor".
			if(typedDependency.reln().getShortName().equals(GrammaticalRelation.DEPENDENT.getShortName())) {	    	    
				//Ex. Sam eats 3 sheep --> [dep(eats-2, Sam-1), root(ROOT-0, eats-2), nummod(sheep-4, 3-3), nsubj(eats-2, sheep-4)]
				//Ex. User fills the required personal client data forms
				//IF relation is SEMANTIC_DEPENDENT and dependent is NOUN or PERSONAL_PRONOUN and governor is VERB THEN Put dependent in Subjects and governor in Action-Verbs
				if((typedDependency.dep().tag().contains(PosTagEnum.NN.name()) || typedDependency.dep().tag().contains(PosTagEnum.PRP.name())) && typedDependency.gov().tag().contains(PosTagEnum.VB.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Verb: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check NN
//					System.out.println("- Subject/Object: " + typedDependency.dep() + " - " +typedDependency.dep().index()); //check VB, VBP, VBZ, VBN?
					//Put in Subject
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					subjectTokens.put(token.getIndex(), token);
					//Put in Objects : nsubj(eats-2, sheep-4) -> sheep is Object! No Subject
					//Find nsubj relation with gov=current verb
					//FIX: Last subject token???
					CustomToken lastSubject = subjectTokens.remove(sentenceTokens.size());
					if(lastSubject != null) {   
						String caseMarkerRegExp ="nsubj\\("+typedDependency.gov().word()+"\\-\\d+, " + lastSubject.getWord() + "\\-\\d+\\)"; 
						//IF lastSubject is in Subjects THEN Put lastSubject in Objects(Direct?)
						if(!typedDependencyList.toString().replaceAll(caseMarkerRegExp, WHITE_SPACE).equals(typedDependencyList.toString())) {
							//Concatenate to close (index-1) previous object
							CustomToken prevObject = directObjectTokens.remove(lastSubject.getIndex() - 1);
							if(prevObject != null) {
								System.out.println(prevObject.getWord());
								prevObject.setWord(prevObject.getWord() + WHITE_SPACE + lastSubject.getWord());
								prevObject.setStem(prevObject.getStem() + WHITE_SPACE + lastSubject.getStem());
								prevObject.setIndex(lastSubject.getIndex());
								directObjectTokens.put(prevObject.getIndex(), prevObject);
							} else	
								directObjectTokens.put(lastSubject.getIndex(), lastSubject);

						}
					}
					//Put in Action-verbs
					token = new CustomToken(typedDependency.gov().word(), typedDependency.gov().tag(), typedDependency.gov().lemma(), typedDependency.gov().index());
					mainActionVerbTokens.put(token.getIndex(), token);    			     			   
				}	    	   
			}
		}
		
		//Extract subjects, direct/indirect objects and action-verbs by conjunction (and/or) and nominal modifier relations
		for(TypedDependency typedDependency : typedDependencyList) {
			//SVOR8:	Extract subject, direct-object or indirect-object from conj
			//“User informs their login and password” --> [nsubj(informs-2, user-1), root(ROOT-0, informs-2), nmod:poss(login-4, their-3), dobj(informs-2, login-4), cc(login-4, and-5), conj(login-4, password-6)]
			//IF relation is CONJUNCT (and/or) and governor is NOUN and dependent is NOUN THEN Put dependent in Subjects or Objects
			if(typedDependency.reln().getShortName().equals(EnglishGrammaticalRelations.CONJUNCT.getShortName())) {
				if(typedDependency.gov().tag().contains(PosTagEnum.NN.name()) && typedDependency.dep().tag().contains(PosTagEnum.NN.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Subject: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
//					System.out.println("- Subject: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check NN
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					//IF governor is in Subjects THEN Put dependent in Subjects
					if(subjectTokens.containsKey(typedDependency.gov().index())) {
						subjectTokens.put(token.getIndex(), token);
					}
					//IF governor is in Objects THEN Put dependent in Objects
					else if(directObjectTokens.containsKey(typedDependency.gov().index())) {
						directObjectTokens.put(token.getIndex(), token);
					}
					else if(indirectObjectTokens.containsKey(typedDependency.gov().index())) {
						indirectObjectTokens.put(token.getIndex(), token);
					}
				}
			}

			//SVOR9:	Extract action-verb, complement-action-verb or modifier-action-verb from conj
			//“User register or delete transactions” --> [nsubj(register-2, user-1), root(ROOT-0, register-2), cc(register-2, or-3), conj(register-2, delete-4), dobj(delete-4, trans-actions-5)]
			//IF relation is CONJUNCT (and/or) and governor is VERB and dependent is VERB THEN Put dependent in Action-Verbs
			if(typedDependency.reln().getShortName().equals(EnglishGrammaticalRelations.CONJUNCT.getShortName())) {
				if(typedDependency.gov().tag().contains(PosTagEnum.VB.name()) && typedDependency.dep().tag().contains(PosTagEnum.VB.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Verb: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
//					System.out.println("- Verb: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check NN
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					mainActionVerbTokens.put(token.getIndex(), token);

				}
			}

			
			//SVOR10:	Update subject, direct-object or indirect-object from dobj, case and nmod
			//“system displays set of possible criteria” --> [nsubj(displays-2, system-1), root(ROOT-0, displays-2), dobj(displays-2, set-3), case(criteria-6, of-4), amod(criteria-6, possible-5), nmod(set-3, criteria-6)]
			//IF relation is NOMINAL_MODIFIER and governor is NOUN and dependent is NOUN THEN Put dependent in Subjects or Objects
			if(typedDependency.reln().getShortName().equals(UniversalEnglishGrammaticalRelations.NOMINAL_MODIFIER.getShortName())) {
				if(typedDependency.gov().tag().contains(PosTagEnum.NN.name()) && typedDependency.dep().tag().contains(PosTagEnum.NN.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Object: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
//					System.out.println("- Object: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check NN
					//IF some previous relation is CASE_MARKER (case(<dependent>-<i>, of-<j>), ex. case(criteria-6, of-4)) THEN Remove governor 
					//FIX: The Broker System displays the count of the customer?
					//TO DO: only for list of, set of, ...?
					
					String caseMarkerRegExp ="case\\("+typedDependency.dep().word()+"\\-"+ typedDependency.dep().index() + ",\\s[oO][fF]\\-\\d+\\)"; 
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					if(!typedDependencyList.toString().replaceAll(caseMarkerRegExp, WHITE_SPACE).equals(typedDependencyList.toString())) {
						
						//Subject
						if(subjectTokens.containsKey(typedDependency.gov().index())) {
							subjectTokens.put(token.getIndex(), token);
							subjectTokens.remove(typedDependency.gov().index());
						}
						//Direct-Object
						else if(directObjectTokens.containsKey(typedDependency.gov().index())) {
							directObjectTokens.put(token.getIndex(), token);
							directObjectTokens.remove(typedDependency.gov().index());
							if(indirectObjectTokens.containsKey(typedDependency.dep().index())) //FIX SVOR6
								indirectObjectTokens.remove(typedDependency.dep().index());	
							
						}
						//Indirect-Object
						else {//FIX
							indirectObjectTokens.put(token.getIndex(), token);
							indirectObjectTokens.remove(typedDependency.gov().index());
						}
						
					}
					
				}
			}

			
		}

		//Update multiword subjects, direct/indirect objects and phrasal verbs
		for(TypedDependency typedDependency : typedDependencyList) {
			//SVOR11:	Update multiword subject, direct-object or indirect-object from compound relationship
			//"The broker system broadcasts the order" --> --> [det(system-3, the-1), com-pound(system-3, broker-2), nsubj(broadcasts-4, system-3), root(ROOT-0, broadcasts-4), det(order-6, the-5), dobj(broadcasts-4, order-6)]
			//IF relation is COMPOUND_MODIFIER and governor is NOUN and dependent is NOUN THEN Concatenate dependent and governor in governor and Put it in Subjects or Objects
			if(typedDependency.reln().getShortName().equals(UniversalEnglishGrammaticalRelations.COMPOUND_MODIFIER.getShortName())) {
				if(typedDependency.gov().tag().contains(PosTagEnum.NN.name()) && typedDependency.dep().tag().contains(PosTagEnum.NN.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Subject: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
//				System.out.println("- Subject: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check NN
					//CustomToken token = new CustomToken(typedDependency.dep().word() + WHITE_SPACE + typedDependency.gov().word(), typedDependency.gov().tag(), typedDependency.dep().lemma() + WHITE_SPACE + typedDependency.gov().lemma(), typedDependency.gov().index());
					//IF governor is in Subjects THEN Put dependent in Subjects
					if(subjectTokens.containsKey(typedDependency.gov().index())) {
						CustomToken oldSubject = subjectTokens.get(typedDependency.gov().index());
						oldSubject.setWord(oldSubject.getWord().replace(typedDependency.gov().word(), typedDependency.dep().word() + WHITE_SPACE + typedDependency.gov().word()));
						oldSubject.setStem(oldSubject.getStem().replace(typedDependency.gov().lemma(), typedDependency.dep().lemma() + WHITE_SPACE + typedDependency.gov().lemma()));
						subjectTokens.put(oldSubject.getIndex(), oldSubject);
						//subjectTokens.put(token.getIndex(), token);

					}
					//IF governor is in Objects THEN Put dependent in Objects
					else if(directObjectTokens.containsKey(typedDependency.gov().index())) {
						CustomToken oldObject = directObjectTokens.get(typedDependency.gov().index());
						oldObject.setWord(oldObject.getWord().replace(typedDependency.gov().word(), typedDependency.dep().word() + WHITE_SPACE + typedDependency.gov().word()));
						oldObject.setStem(oldObject.getStem().replace(typedDependency.gov().lemma(), typedDependency.dep().lemma() + WHITE_SPACE + typedDependency.gov().lemma()));
						directObjectTokens.put(oldObject.getIndex(), oldObject);
						//objectTokens.put(token.getIndex(), token);
					}
					else if(indirectObjectTokens.containsKey(typedDependency.gov().index())) {
						CustomToken oldObject = indirectObjectTokens.get(typedDependency.gov().index());
						oldObject.setWord(oldObject.getWord().replace(typedDependency.gov().word(), typedDependency.dep().word() + WHITE_SPACE + typedDependency.gov().word()));
						oldObject.setStem(oldObject.getStem().replace(typedDependency.gov().lemma(), typedDependency.dep().lemma() + WHITE_SPACE + typedDependency.gov().lemma()));
						indirectObjectTokens.put(oldObject.getIndex(), oldObject);
						//objectTokens.put(token.getIndex(), token);
					}
				}    	   
			}
			//SVOR12:	Update multiword subject, direct-object or indirect-object from nmod:poss relationship
			//"Broker system broadcasts customer's information" --> [compound(system-2, broker-1), nsubj(broadcasts-3, system-2), root(ROOT-0, broadcasts-3), nmod:poss(information-6, customer-4), case(customer-4, 's-5), dobj(broadcasts-3, information-6)]
			//IF relation is POSSESSION_MODIFIER and governor is NOUN and dependent is NOUN THEN Concatenate dependent and POSSESIVE and governor in governor and Put it in Subjects or Objects
			if(typedDependency.reln().getShortName().equals(UniversalEnglishGrammaticalRelations.POSSESSION_MODIFIER.getShortName())) {
				if(typedDependency.gov().tag().contains(PosTagEnum.NN.name()) && typedDependency.dep().tag().contains(PosTagEnum.NN.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Subject: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
//					System.out.println("- Subject: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check NN	  
					CustomToken token = new CustomToken(typedDependency.dep().word() + POSSESSIVE + WHITE_SPACE  + typedDependency.gov().word(), typedDependency.gov().tag(), typedDependency.dep().lemma() + POSSESSIVE + WHITE_SPACE +  typedDependency.gov().lemma(), typedDependency.gov().index());
					//IF governor is in Subjects THEN Put dependent in Subjects
					if(subjectTokens.containsKey(typedDependency.gov().index())) {
						subjectTokens.put(token.getIndex(), token);
					}
					//IF governor is in Objects THEN Put dependent in Objects
					else if(directObjectTokens.containsKey(typedDependency.gov().index())) {
						directObjectTokens.put(token.getIndex(), token);
					}
					else if(indirectObjectTokens.containsKey(typedDependency.gov().index())) {
						indirectObjectTokens.put(token.getIndex(), token);
					}
				}

			}
			
			//SVOR13:	Update multiword subject, direct-object or indirect-object from nummod relationship
			//"system return to the step 1.1"  --> [root(ROOT-0, system-1), dep(system-1, return-2), case(step-5, to-3), det(step-5, the-4), nmod(return-2, step-5), nummod(step-5, 1.1-6)]
			//IF relation is NUMERIC_MODIFIER and governor is NOUN and dependent is CARDINAL_NUMBER THEN Concatenate dependent and governor in governor and Put it in Subjects or Objects
			if(typedDependency.reln().getShortName().equals(UniversalEnglishGrammaticalRelations.NUMERIC_MODIFIER.getShortName())) {
				if(typedDependency.gov().tag().contains(PosTagEnum.NN.name()) && typedDependency.dep().tag().contains(PosTagEnum.CD.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Number: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check NN
//					System.out.println("- Object/Subject: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check NN
					//IF governor is in Subjects THEN Put dependent in Subjects
					if(subjectTokens.containsKey(typedDependency.gov().index())) {
						CustomToken oldSubject = subjectTokens.get(typedDependency.gov().index());
						if(typedDependency.gov().index() < typedDependency.dep().index()) {
							oldSubject.setWord(oldSubject.getWord().replace(typedDependency.gov().word(), typedDependency.gov().word() + WHITE_SPACE + typedDependency.dep().word()));
							oldSubject.setStem(oldSubject.getStem().replace(typedDependency.gov().lemma(), typedDependency.gov().lemma() + WHITE_SPACE + typedDependency.dep().lemma()));
						} else {
							oldSubject.setWord(oldSubject.getWord().replace(typedDependency.gov().word(), typedDependency.dep().word() + WHITE_SPACE + typedDependency.gov().word()));
							oldSubject.setStem(oldSubject.getStem().replace(typedDependency.gov().lemma(), typedDependency.dep().lemma() + WHITE_SPACE + typedDependency.gov().lemma()));
						}
						subjectTokens.put(oldSubject.getIndex(), oldSubject);
					}
					//IF governor is in Objects THEN Put dependent in Objects
					else if(directObjectTokens.containsKey(typedDependency.gov().index())) {
						CustomToken oldObject = directObjectTokens.get(typedDependency.gov().index());
						if(typedDependency.gov().index() < typedDependency.dep().index()) {
							oldObject.setWord(oldObject.getWord().replace(typedDependency.gov().word(), typedDependency.gov().word() + WHITE_SPACE + typedDependency.dep().word()));
							oldObject.setStem(oldObject.getStem().replace(typedDependency.gov().lemma(), typedDependency.gov().lemma() + WHITE_SPACE + typedDependency.dep().lemma()));
						} else {
							oldObject.setWord(oldObject.getWord().replace(typedDependency.gov().word(), typedDependency.dep().word() + WHITE_SPACE + typedDependency.gov().word()));
							oldObject.setStem(oldObject.getStem().replace(typedDependency.gov().lemma(), typedDependency.dep().lemma() + WHITE_SPACE + typedDependency.gov().lemma()));
						}
						directObjectTokens.put(oldObject.getIndex(), oldObject);
						//objectTokens.put(token.getIndex(), token);
					}
					else if(indirectObjectTokens.containsKey(typedDependency.gov().index())) {
						CustomToken oldObject = indirectObjectTokens.get(typedDependency.gov().index());
						if(typedDependency.gov().index() < typedDependency.dep().index()) {
							oldObject.setWord(oldObject.getWord().replace(typedDependency.gov().word(), typedDependency.gov().word() + WHITE_SPACE + typedDependency.dep().word()));
							oldObject.setStem(oldObject.getStem().replace(typedDependency.gov().lemma(), typedDependency.gov().lemma() + WHITE_SPACE + typedDependency.dep().lemma()));
						} else {
							oldObject.setWord(oldObject.getWord().replace(typedDependency.gov().word(), typedDependency.dep().word() + WHITE_SPACE + typedDependency.gov().word()));
							oldObject.setStem(oldObject.getStem().replace(typedDependency.gov().lemma(), typedDependency.dep().lemma() + WHITE_SPACE + typedDependency.gov().lemma()));
						}
						indirectObjectTokens.put(oldObject.getIndex(), oldObject);
						//objectTokens.put(token.getIndex(), token);
					}
				}
			}
			

			//UPDATE ACTION-VERBS
			//SVOR14:	Update multiword action-verb from compound:prt relationship
			//"the broker system carry out the order" --> [det(system-3, the-1), com-pound(system-3, broker-2), nsubj(carry-4, system-3), root(ROOT-0, carry-4), com-pound:prt(carry-4, out-5), det(order-7, the-6), dobj(carry-4, order-7)]
			//IF relation is PHRASAL_VERB_PARTICLE and governor is VERB and dependent is VERB PARTICLE THEN Concatenate governor and dependent in governor and Put it in Action-Verbs
			if(typedDependency.reln().getShortName().equals(UniversalEnglishGrammaticalRelations.PHRASAL_VERB_PARTICLE.getShortName())) {
				if(typedDependency.gov().tag().contains(PosTagEnum.VB.name()) && typedDependency.dep().tag().contains(PosTagEnum.RP.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Verb: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check  VB, VBP, VBZ, VBN?
//					System.out.println("- particle: " + typedDependency.dep() + " - " +typedDependency.dep().index());//check RP 
					CustomToken token = new CustomToken(typedDependency.gov().word() + WHITE_SPACE  + typedDependency.dep().word(), typedDependency.gov().tag(), typedDependency.gov().lemma()+ WHITE_SPACE  + typedDependency.dep().lemma(), typedDependency.gov().index());
					mainActionVerbTokens.put(token.getIndex(), token);
				}
			}
			
			/*
			 * //NEW
			 * FIX: which is the action verb?
			 * gov or dep?
			 * Complex complement/modifier verb????
			 */
			
			//UPDATE ACTION-VERBS
			//ADD COMPLEMENT AND MODIFIER ACTION-VERBS
			//SVOR15:	Update action-verb from xcomp relationship
			//"User wants to change his pin" --> [nsubj(wants-2, user-1), root(ROOT-0, wants-2), mark(change-4, to-3), xcomp(wants-2, change-4), nmod:poss(pin-6, his-5), dobj(change-4, pin-6)]
			//IF relation is XCLAUSAL_COMPLEMENT and governor is VERB and dependent is VERB (NO VBG) THEN Remove dependent from Action-Verbs AND Put dependent in Complement-Action-Verbs
			if(typedDependency.reln().getShortName().equals(UniversalEnglishGrammaticalRelations.XCLAUSAL_COMPLEMENT.getShortName()) ) {
				if(typedDependency.gov().tag().contains(PosTagEnum.VB.name()) && typedDependency.dep().tag().contains(PosTagEnum.VB.name())) {// && !typedDependency.dep().tag().equals(PosTagEnum.VBG.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Verb: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check  VB, VBP, VBZ, VBN?
//					System.out.println("- verb complement: " + typedDependency.dep() + " - " +typedDependency.dep().index());//NO VBG 
					//IF dependent is into Action-Verbs THEN Remove existing dependent
					if(mainActionVerbTokens.containsKey(typedDependency.dep().index()))
						mainActionVerbTokens.remove(typedDependency.dep().index());
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					complementActionVerbTokens.put(token.getIndex(), token);
					
				}
			}
			
			//SVOR16:	Update action-verb from ccomp relationship
			//"ATM verifies with the Bank that the User has enough money in account" --> [nsubj(verifies-2, atm-1), root(ROOT-0, verifies-2), case(bank-5, with-3), det(bank-5, the-4), nmod(verifies-2, bank-5), mark(has-9, that-6), det(user-8, the-7), nsubj(has-9, user-8), ccomp(verifies-2, has-9), amod(money-11, enough-10), dobj(has-9, money-11), case(account-13, in-12), nmod(money-11, account-13)]
			//IF relation is CLAUSAL_COMPLEMENT and governor is VERB and dependent is VERB (NO VBG) THEN Remove dependent from Action-Verbs AND Put dependent in Complement-Action-Verbs
			if(typedDependency.reln().getShortName().equals(UniversalEnglishGrammaticalRelations.CLAUSAL_COMPLEMENT.getShortName()) ) {
				if(typedDependency.gov().tag().contains(PosTagEnum.VB.name()) && typedDependency.dep().tag().contains(PosTagEnum.VB.name())) {// && !typedDependency.dep().tag().equals(PosTagEnum.VBG.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Verb: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check  VB, VBP, VBZ, VBN?
//					System.out.println("- verb complement: " + typedDependency.dep() + " - " +typedDependency.dep().index());//NO VBG 
					//IF dependent is into Action-Verbs THEN Remove existing dependent
					if(mainActionVerbTokens.containsKey(typedDependency.dep().index()))
						mainActionVerbTokens.remove(typedDependency.dep().index());
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					complementActionVerbTokens.put(token.getIndex(), token);
					//IF rel is CCOMP THEN remove subject from Subjects and PUT into ComplementSubjects: complement verb with its own subject
					//Get nsubj rel with gov equal to current dep (ccomp(gov, dep)) 
					String REGEX_NSUBJ = ".*(nsubj\\("+typedDependency.dep().word()+"\\-\\d+\\,\\s(\\w+)\\-(\\d+)\\)).*";
					Matcher matcherNSubj = Pattern.compile(REGEX_NSUBJ).matcher(typedDependencyList.toString());
					if (matcherNSubj.matches()) {
						String subject = matcherNSubj.group(2);
						String subjectIndex = matcherNSubj.group(3);
						// remove subject from Subjects and PUT into ComplementSubjects
						if (subject != null && !subject.isEmpty() ) {
							CustomToken complementSubjectToken = null;
							if(subjectTokens.containsKey(new Integer(subjectIndex))) {
								complementSubjectToken = subjectTokens.remove(new Integer(subjectIndex));
								complementSubjectTokens.put(complementSubjectToken.getIndex(), complementSubjectToken);
							}
						}
					}
				}
			}
		
			//SVOR17:	Update action-verb from advcl relationship
			//"user select option for adding new clients" --> ..., advcl(select-2, adding-5), amod(clients-7, new-6), dobj(adding-5, clients-7), , ...
			//IF relation is ADV_CLAUSE_MODIFIER (MODIFY A VERB) and governor is VERB (NO VBG) and dependent is VERB (can be VBG) THEN Remove dependent from Action-Verbs  AND Put dependent from Modifier-Action-Verbs AND Put governor in Action-Verbs
			if(typedDependency.reln().getShortName().equals(UniversalEnglishGrammaticalRelations.ADV_CLAUSE_MODIFIER.getShortName())) {
				if(typedDependency.gov().tag().contains(PosTagEnum.VB.name()) && !typedDependency.gov().tag().equals(PosTagEnum.VBG.name()) && typedDependency.dep().tag().contains(PosTagEnum.VB.name()) ) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Verb: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check  VB, VBP, VBZ, NO VBG
//					System.out.println("- verb modifier: " + typedDependency.dep() + " - " +typedDependency.dep().index());//VB, VBP, VBZ, VBN?
					//IF dependent is into Action-Verbs THEN Remove existing dependent
					if(mainActionVerbTokens.containsKey(typedDependency.dep().index()))
						mainActionVerbTokens.remove(typedDependency.dep().index());
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					modifierActionVerbTokens.put(token.getIndex(), token);
					if(mainActionVerbTokens.containsKey(typedDependency.dep().index()))
						mainActionVerbTokens.remove(typedDependency.dep().index());
					token = new CustomToken(typedDependency.gov().word(), typedDependency.gov().tag(), typedDependency.gov().lemma(), typedDependency.gov().index());
					mainActionVerbTokens.put(token.getIndex(), token); 
				}
			}
			
			//SVOR17:	Update action-verb from acl relationship
			//"user signals the system to proceed the transaction" --> ..., mark(proceed-6, to-5), acl(system-4, proceed-6), det(transaction-8, the-7), dobj(proceed-6, transaction-8), , ...
			//IF relation is CLAUSAL_MODIFIER (MODIFY A NOUN) and governor is NOUN and dependent is VERB (NO VBG???) THEN Remove dependent from Action-Verbs AND Put dependent from Modifier-Action-Verbs
			if(typedDependency.reln().getShortName().equals(UniversalEnglishGrammaticalRelations.CLAUSAL_MODIFIER.getShortName())) {
				//if(typedDependency.gov().tag().contains(PosTag.NN.name()) && !typedDependency.dep().tag().equals(PosTag.VBG.name()) && typedDependency.dep().tag().contains(PosTag.VB.name()) ) {
				if(typedDependency.gov().tag().contains(PosTagEnum.NN.name()) &&  typedDependency.dep().tag().contains(PosTagEnum.VB.name()) ) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Noun: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check  NN
//					System.out.println("- Verb: noun modifier: " + typedDependency.dep() + " - " +typedDependency.dep().index());//VB, VBP, VBZ, NO VBG
					//IF dependent is into Action-Verbs THEN Remove existing dependent
					if(mainActionVerbTokens.containsKey(typedDependency.dep().index()))
						mainActionVerbTokens.remove(typedDependency.dep().index());
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					modifierActionVerbTokens.put(token.getIndex(), token);
				}
			}
			
			//User confirms he/she wants to register
			//Administrator chooses a group containing the channel he wants to delete	--> ..., nsubj(wants-9, he-8), acl:relcl(channel-7, wants-9), mark(delete-11, to-10)
			//IF relation is RELATIVE_CLAUSE_MODIFIER and governor is NOUN and dependent is VERB (NO VBG) THEN Remove dependent from Action-Verbs AND Put dependent in Modifier-Action-Verbs
			if(typedDependency.reln().getShortName().equals(UniversalEnglishGrammaticalRelations.RELATIVE_CLAUSE_MODIFIER.getShortName())) {
				if((typedDependency.gov().tag().contains(PosTagEnum.NN.name()) || typedDependency.gov().tag().contains(PosTagEnum.PRP.name())) && typedDependency.dep().tag().contains(PosTagEnum.VB.name())) {// && !typedDependency.dep().tag().equals(PosTagEnum.VBG.name())) {
					//System.out.print("- relation: " + typedDependency.reln().getShortName());
					//System.out.print("- Verb: " + typedDependency.gov() + " - " +typedDependency.gov().index());//check  NN or PRP?
					//System.out.println("- verb modifier: " + typedDependency.dep() + " - " +typedDependency.dep().index());//NO VBG 
					//IF dependent is into Action-Verbs THEN Remove existing dependent
					if(mainActionVerbTokens.containsKey(typedDependency.dep().index()))
						mainActionVerbTokens.remove(typedDependency.dep().index());
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					modifierActionVerbTokens.put(token.getIndex(), token);
					//IF rel is RELATIVE_CLAUSE_MODIFIER THEN remove subject from Subjects and PUT into ModifierSubjects: complement verb with its own subject
					//Get nsubj rel with gov equal to current dep (acl:relcl(gov, dep)) 
					String REGEX_NSUBJ = ".*(nsubj\\("+typedDependency.dep().word()+"\\-\\d+\\,\\s(\\w+)\\-(\\d+)\\)).*";
					Matcher matcherNSubj = Pattern.compile(REGEX_NSUBJ).matcher(typedDependencyList.toString());
					if (matcherNSubj.matches()) {
						String subject = matcherNSubj.group(2);
						String subjectIndex = matcherNSubj.group(3);
						// remove subject from Subjects and PUT into ModifierSubjects
						if (subject != null && !subject.isEmpty() ) {
							CustomToken modifierSubjectToken = null;
							if(subjectTokens.containsKey(new Integer(subjectIndex))) {
								modifierSubjectToken = subjectTokens.remove(new Integer(subjectIndex));
								modifierSubjectTokens.put(modifierSubjectToken.getIndex(), modifierSubjectToken);
							}
						}
					}
				}
			}
			
			//IF relation is SEMANTIC_DEPENDENT and governor is VERB  and dependent is VERB THEN Remove dependent from Action-Verbs AND Put dependent from Modifier-Action-Verbs
			if(typedDependency.reln().getShortName().equals(GrammaticalRelation.DEPENDENT.getShortName())) {	    	    
				if((typedDependency.gov().tag().equals(PosTagEnum.VB.name()) || typedDependency.gov().tag().equals(PosTagEnum.VBP.name()) || typedDependency.gov().tag().equals(PosTagEnum.VBZ.name())) 
						&& typedDependency.dep().tag().contains(PosTagEnum.VB.name())) {
//					System.out.print("- relation: " + typedDependency.reln().getShortName());
//					System.out.print("- Subject: " + typedDependency.gov() + " - " +typedDependency.gov().index());//VB, VBP, VBZ
//					System.out.println("- verb: " + typedDependency.dep() + " - " +typedDependency.dep().index()); //check VB, VBP, VBZ, VBN?
					if(mainActionVerbTokens.containsKey(typedDependency.dep().index()))
						mainActionVerbTokens.remove(typedDependency.dep().index());
					CustomToken token = new CustomToken(typedDependency.dep().word(), typedDependency.dep().tag(), typedDependency.dep().lemma(), typedDependency.dep().index());
					modifierActionVerbTokens.put(token.getIndex(), token);
				}
			}

		}

		customSentenceNlpInfo = new CustomSentenceNlpInfo(sentenceTokens, subjectTokens, directObjectTokens, indirectObjectTokens, mainActionVerbTokens, complementActionVerbTokens, complementSubjectTokens, modifierActionVerbTokens, modifierSubjectTokens);

		return customSentenceNlpInfo;
	}
	
	private TypedDependency findTypedDependecyByRelationGovOrDep(List<TypedDependency> typedDependencyList, String reln, String govWord, int govIndex, String depWord, int depIndex) { 
		for(TypedDependency typedDependency : typedDependencyList) {
			if(typedDependency.reln().getShortName().equals(reln)) {
				if (govWord != null && govIndex > 0  && depWord != null && depIndex > 0) {
					if((typedDependency.gov().word().equals(govWord) && typedDependency.gov().index() == govIndex )
							&& (typedDependency.dep().word().equals(govWord) && typedDependency.dep().index() == govIndex) ) {
						return typedDependency;
					}
				}
				if (govWord != null && govIndex > 0  && depWord == null) {
					if(typedDependency.gov().word().equals(govWord) && typedDependency.gov().index() == govIndex ) {
						return typedDependency;
					}
				}
				if (govWord == null  && depWord != null && depIndex > 0) {
					if(typedDependency.dep().word().equals(depWord) && typedDependency.dep().index() == depIndex ) {
						return typedDependency;
					}
				}
			}			
		}
		return null;
	}

	@Override
	public List<CustomSentenceNlpInfo> getSentencesComponents(List<String> texts) {
		List<CustomSentenceNlpInfo> componentsOfSentences = new ArrayList<>();
		for(String sentence : texts) {
			CustomSentenceNlpInfo sentenceComponents = getSentenceComponents(sentence);
			componentsOfSentences.add(sentenceComponents);
		}
		return componentsOfSentences;
	}

	@Override
	public boolean isSintacticallySimilar(String text, String otherText) {
		return false;

	}

	@Override
	public boolean isSemanticallySimilar(String text, String otherText) {
		
		return false;
	}


    private String improveTextForNLP(String text) {
    	text = text.toLowerCase();
		text = text.replaceAll(REG_EXP_OTHER_POSSESSIVE_CANDIDATES, POSSESSIVE);
		//REPLACE words between '/' by second text; Ex. User selects/deletes the channels -> deletes
		text = ScenarioCleaner.replaceWordsBetweenSlashBySecondWord(text);
		//REMOVE SPECIAL punctuation at the end
		text = text.replaceAll(RegularExpression.REGEX_PUNCTUATION_SPECIAL_AT_END_LINE, "");
		return text;
    }
    
    public static void main(String[] args) {
		//System.out.println(isPlural("exercices"));
		//System.out.println(isPlural("shoe"));
		
		//System.out.println(getVerbBaseFromThirdPerson("gets"));
		
		
		INLPAnalyzer nlpAnalyzer = CoreNLPAnalyzer.getInstance();//singleton
		
		
		
		//String text1 ="System sends the server a  registration request";
		//String text1 = "broker system carry out  customer's information to suppliers";
		//String text1 = "User fills all required personal client data forms";
		//String text1 ="User creates filter for searching";
		//String text1 ="System sends a registration request to the server";
		
		//String text1 = "System sends to the server of the amazon a registration request";
		
		//String text1 = "system saves the data in repository";
		
		//String text1 = "system gets data from repository";
		
		//String text1 = "Administrator types the name of the channel and the URL of the news service and selects Add channel";
		
		//String text1 = "system prints the name of the user";
		
		//String text1 = "selects envelope";
		//String text1 = "file was updated by the user";
		//String text1 = "user clicks on the screen";
		
		//String text1 = "user clicks the mouse on the screen";
		
		
		//String text1 = "Customer examines the bid";
		//String text1="I really  like it";
		//String text1 ="The system informs the user that the battery is full";
		//String text1 ="The system sends the user an email";
		
		//String text1 ="system returns to step 1.1";
		//String text1 ="system go to step 1";
		//String text1 = "User fills 3 forms";
		
		//String text1 ="User informs their login and password";
		//String text1 ="User register or delete transactions";
		
		//String text1 ="system displays the numbers about the set of possible criteria";
		
		//String text1 ="system displays set of possible criteria";
		
		
		//String text1 = "Set of users register forms";
		//String text1 = "I love French fries";
		
		//String text1 = "The Broker system broadcasts the order";
		//String text1 ="Broker system broadcasts customer's information";
		
		//String text1 = "The broker system carry out the order";
		
		//String text1 = "user log in to the system";
		
		//String text1 = "user wants to change his pin";
		
		//String text1 = "ATM verifies with the Bank that the User has enough money in account";

		//String text1 ="user select option for adding new clients";
		
		//String text1 ="user signals the system to proceed the transaction";
		
		//String text1 = "Administrator chooses a group containing the channel he wants to delete";
		
		//String text1 = "The system shall send a message to the receiver, and it provides an acknowledge message";
		//String text1 = "The system shall send a message to the receiver, and it provides an acknowledgement message";
		
		//String text1 = "The System receives news messages and stores them in a local database";
		
		//String text1 ="user register everyone";
		String text1 ="user informs her login";
		
		
		
		nlpAnalyzer.getTokens(text1);
		CustomSentenceNlpInfo nlpInfo = nlpAnalyzer.getSentenceComponents(text1);
		HashMap<Integer, CustomToken> subjectTokens = nlpInfo.getSubjects();
		HashMap<Integer, CustomToken> mainActionVerbTokens = nlpInfo.getMainActionVerbs();
		HashMap<Integer, CustomToken> complementActionVerbTokens = nlpInfo.getComplementActionVerbs();
		HashMap<Integer, CustomToken> complementSubjectTokens = nlpInfo.getComplementSubjects();
		HashMap<Integer, CustomToken> modifierActionVerbTokens = nlpInfo.getModifierActionVerbs();
		HashMap<Integer, CustomToken> modifierSubjectTokens = nlpInfo.getModifierSubjects();
		HashMap<Integer, CustomToken> directObjectTokens = nlpInfo.getDirectObjects();
		HashMap<Integer, CustomToken> indirectObjectTokens = nlpInfo.getIndirectObjects();
		
		// Displaying HashMap elements
				System.out.println("Subjects HashMap contains: ");
				for (Map.Entry<Integer, CustomToken> entry : subjectTokens.entrySet()) {
					System.out.println(entry.getKey() + " = " + entry.getValue().getWord() + " " + entry.getValue().getPosTag() + " " + entry.getValue().getIndex());
				}
				System.out.println("Action-Verbs HashMap contains: ");
				for (Map.Entry<Integer, CustomToken> entry : mainActionVerbTokens.entrySet()) {
					System.out.println(entry.getKey() + " = " + entry.getValue().getWord() + " " + entry.getValue().getPosTag() + " " + entry.getValue().getIndex());
				}
				System.out.println("Complement Action-Verbs HashMap contains: ");
				for (Map.Entry<Integer, CustomToken> entry : complementActionVerbTokens.entrySet()) {
					System.out.println(entry.getKey() + " = " + entry.getValue().getWord() + " " + entry.getValue().getPosTag() + " " + entry.getValue().getIndex());
				}
				System.out.println("Complement Subjects HashMap contains: ");
				for (Map.Entry<Integer, CustomToken> entry : complementSubjectTokens.entrySet()) {
					System.out.println(entry.getKey() + " = " + entry.getValue().getWord() + " " + entry.getValue().getPosTag() + " " + entry.getValue().getIndex());
				}
				System.out.println("Modifier Action-Verbs HashMap contains: ");
				for (Map.Entry<Integer, CustomToken> entry : modifierActionVerbTokens.entrySet()) {
					System.out.println(entry.getKey() + " = " + entry.getValue().getWord() + " " + entry.getValue().getPosTag() + " " + entry.getValue().getIndex());
				}
				System.out.println("Modifier Subjects HashMap contains: ");
				for (Map.Entry<Integer, CustomToken> entry : modifierSubjectTokens.entrySet()) {
					System.out.println(entry.getKey() + " = " + entry.getValue().getWord() + " " + entry.getValue().getPosTag() + " " + entry.getValue().getIndex());
				}
				System.out.println("Direct Objects HashMap contains: ");
				for (Map.Entry<Integer, CustomToken> entry : directObjectTokens.entrySet()) {
					System.out.println(entry.getKey() + " = " + entry.getValue().getWord() + " " + entry.getValue().getPosTag() + " " + entry.getValue().getIndex());
				}
				System.out.println("Indirect Objects HashMap contains: ");
				for (Map.Entry<Integer, CustomToken> entry : indirectObjectTokens.entrySet()) {
					System.out.println(entry.getKey() + " = " + entry.getValue().getWord() + " " + entry.getValue().getPosTag() + " " + entry.getValue().getIndex());
				}
		
    }

	@Override
	public List<CustomToken> getTokensWithChunkTypes(List<CustomToken> tokens) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getChunkSpans(List<CustomToken> tokens) {
		// TODO Auto-generated method stub
		return null;
	}


}
