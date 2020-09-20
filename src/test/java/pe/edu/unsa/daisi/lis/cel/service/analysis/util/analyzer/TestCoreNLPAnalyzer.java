package pe.edu.unsa.daisi.lis.cel.service.analysis.util.analyzer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import pe.edu.unsa.daisi.lis.cel.util.RegularExpression;
import pe.edu.unsa.daisi.lis.cel.util.nlp.CoreNLPAnalyzer;
import pe.edu.unsa.daisi.lis.cel.util.nlp.CustomSentenceNlpInfo;
import pe.edu.unsa.daisi.lis.cel.util.nlp.CustomToken;
import pe.edu.unsa.daisi.lis.cel.util.nlp.INLPAnalyzer;
import pe.edu.unsa.daisi.lis.cel.util.nlp.OpenNLPAnalyzer;
import pe.edu.unsa.daisi.lis.cel.util.nlp.dictionary.english.Unambiguity;
import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioCleaner;

public class TestCoreNLPAnalyzer {

	INLPAnalyzer nlpAnalyzer;
	
	@Before
	public void setUp() {
		nlpAnalyzer = CoreNLPAnalyzer.getInstance();//singleton
	}
	
	@Test
	public void testGetTokensWithSuccess( ) {
		
		//Text to analyze
		List<CustomToken> tokens = null;		
		String text1 = "broker system types the PIN and Numbers";
				tokens = nlpAnalyzer.getTokens(text1);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, NN, VBZ, DT, NN, CC, NNS]", getPosTagsAsString(tokens));
				
				String text2 = "broker system carry out  customer's information to suppliers";
				tokens = nlpAnalyzer.getTokens(text2);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, NN, VBP, RP, NN, POS, NN, TO, NNS]", getPosTagsAsString(tokens));
		    	
				String text3 = "you are feeling hungry";
				tokens = nlpAnalyzer.getTokens(text3);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[PRP, VBP, VBG, JJ]", getPosTagsAsString(tokens));
				
				String text4 = "The Broker System displays a payment denied page";
				tokens = nlpAnalyzer.getTokens(text4);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[DT, NN, NN, VBZ, DT, NN, JJ, NN]", getPosTagsAsString(tokens));
		    	
				String text5 = "User really like it";
				tokens = nlpAnalyzer.getTokens(text5);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, RB, VBP, PRP]", getPosTagsAsString(tokens));
		    	
				String text6 = "system queries the database for news messages, whose expiry date and time have passed";
				tokens = nlpAnalyzer.getTokens(text6);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, IN, NN, NNS, ,, WP$, NN, NN, CC, NN, VBP, VBN]", getPosTagsAsString(tokens)); //Core NLP: expiry/NN, OpenNLP expiry/JJ
		    	
				String text7 = "system displays set OF possible criteria";
				tokens = nlpAnalyzer.getTokens(text7);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, NN, IN, JJ, NNS]", getPosTagsAsString(tokens));
		    	
				String text8 = "system displays set of broker systems";
				tokens = nlpAnalyzer.getTokens(text8);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, NN, IN, NN, NNS]", getPosTagsAsString(tokens));
		    	
				String text9 = "He send information to some suppliers";
				tokens = nlpAnalyzer.getTokens(text9);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[PRP, VBP, NN, TO, DT, NNS]", getPosTagsAsString(tokens));
		    	
				String text10 ="system saves the data in repository";//repository/JJ? - error CoreNLP
				tokens = nlpAnalyzer.getTokens(text10);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NNS, IN, JJ]", getPosTagsAsString(tokens));
		    	
				String text11 ="The Broker System displays the count of the customer";
				tokens = nlpAnalyzer.getTokens(text11);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[DT, NN, NN, VBZ, DT, NN, IN, DT, NN]", getPosTagsAsString(tokens));
		    	
				String text12 = "User wants to change their PIN";
				tokens = nlpAnalyzer.getTokens(text12);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, TO, VB, PRP$, NN]", getPosTagsAsString(tokens));
		    	
				String text13 = "Change PIN";
				tokens = nlpAnalyzer.getTokens(text13);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, NN]", getPosTagsAsString(tokens));
		    	
				String text14 = "Customer signals the system to proceed with bid";
				tokens = nlpAnalyzer.getTokens(text14);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, TO, VB, IN, NN]", getPosTagsAsString(tokens));
		    	
		    	
				String text15 ="local Supplier bid for order";
				tokens = nlpAnalyzer.getTokens(text15);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[JJ, NN, VBP, IN, NN]", getPosTagsAsString(tokens));
		    	
				String text16 ="The Broker System broadcast the Order to the Suppliers ";//broadcast/VBD ????? Error CoreNLP
				tokens = nlpAnalyzer.getTokens(text16);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[DT, NN, NN, VBD, DT, NN, TO, DT, NNS]", getPosTagsAsString(tokens));
		    	
				String text17 = "show alert message";
				tokens = nlpAnalyzer.getTokens(text17);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, JJ, NN]", getPosTagsAsString(tokens));
		    	

				String text18 = "user select option for searching";
				tokens = nlpAnalyzer.getTokens(text18);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBP, NN, IN, VBG]", getPosTagsAsString(tokens));
		    	
				String text19 = "user select option to adding new clients";
				tokens = nlpAnalyzer.getTokens(text19);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBP, NN, TO, VBG, JJ, NNS]", getPosTagsAsString(tokens));


				String text20 = "user wants to change his pin";
				tokens = nlpAnalyzer.getTokens(text20);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, TO, VB, PRP$, NN]", getPosTagsAsString(tokens));
		    	
				String text21 = "user select option for adding new clients";//advcl(select-2, adding-5) -> remove verb adding
				tokens = nlpAnalyzer.getTokens(text21);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBP, NN, IN, VBG, JJ, NNS]", getPosTagsAsString(tokens));
				
				String text22 = "user signals the system to proceed the transaction";//acl(system-4, proceed-6) -> remove verb proceed
				tokens = nlpAnalyzer.getTokens(text22);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, TO, VB, DT, NN]", getPosTagsAsString(tokens));
				
				String text23 = "User fills the required personal client data forms";
				tokens = nlpAnalyzer.getTokens(text23);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, JJ, JJ, NN, NNS, NNS]", getPosTagsAsString(tokens));
		    	
				String text24 = "User fills all required personal client data forms";
				tokens = nlpAnalyzer.getTokens(text24);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, JJ, JJ, NN, NNS, NNS]", getPosTagsAsString(tokens));
		    	
				String text25 = "Reagan has died";
				tokens = nlpAnalyzer.getTokens(text25);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, VBN]", getPosTagsAsString(tokens));
				
				String text26 = "system fills customer's information";
				tokens = nlpAnalyzer.getTokens(text26);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, NN, POS, NN]", getPosTagsAsString(tokens));
		    	
				String text27 ="supplier was called by user";
				tokens = nlpAnalyzer.getTokens(text27);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBD, VBN, IN, NN]", getPosTagsAsString(tokens));
		    	
				String text28 = "system register suppliers";
				tokens = nlpAnalyzer.getTokens(text28);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBP, NNS]", getPosTagsAsString(tokens)); //register/VBP
		    	
				
		    	String text29 = "system returns to step 1.1";//"1. system go to step 1" - Error CoreNLP
				tokens = nlpAnalyzer.getTokens(text29);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, TO, NN, CD]", getPosTagsAsString(tokens));
		    	
		    	
				String text30 = "Sam eats 3 sheep";
				tokens = nlpAnalyzer.getTokens(text30);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, CD, NN]", getPosTagsAsString(tokens));

				String text31 = "System verifies possibility to perform deleting";
				tokens = nlpAnalyzer.getTokens(text31);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, NN, TO, VB, VBG]", getPosTagsAsString(tokens)); //deleting --> VBG
		    	
				String text32 = "User may view details about selected client";
				tokens = nlpAnalyzer.getTokens(text32);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, MD, VB, NNS, IN, VBN, NN]", getPosTagsAsString(tokens));
		    	
				String text33 = "User select a client for whom new contract will be added";
				tokens = nlpAnalyzer.getTokens(text33);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBP, DT, NN, IN, WP, JJ, NN, MD, VB, VBN]", getPosTagsAsString(tokens));
		    	
				String text34 = "System displays transaction form";
				tokens = nlpAnalyzer.getTokens(text34);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, NN, NN]", getPosTagsAsString(tokens));
		    	
				String text35 = "User downloads the licence file";
				tokens = nlpAnalyzer.getTokens(text35);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, NN]", getPosTagsAsString(tokens));
		    	
		    	
				String text36 = "User selects one contract for licence request";
				tokens = nlpAnalyzer.getTokens(text36);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, CD, NN, IN, NN, NN]", getPosTagsAsString(tokens));
		    	
				/*
				 * Replace ", | ;" by "or"
				 */
				String text37 = "System displays the list of licences, keys, contracts and installations (grouping by type)";
				tokens = nlpAnalyzer.getTokens(text37);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, IN, NNS, ,, NNS, ,, NNS, CC, NNS, -LRB-, VBG, IN, NN, -RRB-]", getPosTagsAsString(tokens));
		    	
				String text38 = "System displays the list of licences or keys or contracts and installations (grouping by type)";
				tokens = nlpAnalyzer.getTokens(text38);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, IN, NNS, CC, NNS, CC, NNS, CC, NNS, -LRB-, VBG, IN, NN, -RRB-]", getPosTagsAsString(tokens));
		    	
				
				String text39 = "User register or delete transactions";
				tokens = nlpAnalyzer.getTokens(text39);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBP, CC, VB, NNS]", getPosTagsAsString(tokens));
		    	
				String text40 = "Process bids";
				tokens = nlpAnalyzer.getTokens(text40);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, NNS]", getPosTagsAsString(tokens));
		    	
				String text41  = "System displays the welcome interface";
				tokens = nlpAnalyzer.getTokens(text41);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, JJ, NN]", getPosTagsAsString(tokens));
		    	

				String text42 = "Submit order";
				tokens = nlpAnalyzer.getTokens(text42);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, NN]", getPosTagsAsString(tokens));
				
				String text43 = "User selects the type and localisation of the output file with report";//file is a verb? - problem adjusting (Check that a NOUN is a VERB - line 75)
				tokens = nlpAnalyzer.getTokens(text43);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, CC, NN, IN, DT, NN, NN, IN, NN]", getPosTagsAsString(tokens));
				
				String text44 = "System displays the list of user's contracts";
				tokens = nlpAnalyzer.getTokens(text44);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, IN, NN, POS, NNS]", getPosTagsAsString(tokens));
		    	
				String text45 = "System asks for data again";
				tokens = nlpAnalyzer.getTokens(text45);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, IN, NNS, RB]", getPosTagsAsString(tokens));
		    	
		    	
		    	
				String text46 = "Scenario ends";
				tokens = nlpAnalyzer.getTokens(text46);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ]", getPosTagsAsString(tokens));
		    	
		    	
				String text47 = "the broker system displays user's current balance"; //User's balance???
				tokens = nlpAnalyzer.getTokens(text47);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[DT, NN, NN, VBZ, NN, POS, JJ, NN]", getPosTagsAsString(tokens));
		    	
				String text48 = "Bank retrieves User's current balance from their account"; //FIX: Bank is not a VERB
				tokens = nlpAnalyzer.getTokens(text48);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, NN, POS, JJ, NN, IN, PRP$, NN]", getPosTagsAsString(tokens));
		    	
		    	
				String text49 = "Selects envelope";
				tokens = nlpAnalyzer.getTokens(text49);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VBZ, NN]", getPosTagsAsString(tokens));
		    			    	
				String text50 = "process bid";
				tokens = nlpAnalyzer.getTokens(text50);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, NN]", getPosTagsAsString(tokens));
		    	
				String text51 = "submit supplier";
				tokens = nlpAnalyzer.getTokens(text51);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, NN]", getPosTagsAsString(tokens));

				String text52 = "Post a group message";
				tokens = nlpAnalyzer.getTokens(text52);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, DT, NN, NN]", getPosTagsAsString(tokens));//POST/VB !

				String text53 = "System finds all users matching deletion criteria and deletes found user accounts";
				tokens = nlpAnalyzer.getTokens(text53);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NNS, JJ, NN, NNS, CC, VBZ, JJ, NN, NNS]", getPosTagsAsString(tokens)); //matching//JJ?
		    	
				String text54 = "Server sends separate news messages from all subscribed channels";
				tokens = nlpAnalyzer.getTokens(text54);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, JJ, NN, NNS, IN, DT, JJ, NNS]", getPosTagsAsString(tokens));
		    	
				String text55 = "System receives a RSS-like formatted news file";
				tokens = nlpAnalyzer.getTokens(text55);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, JJ, JJ, NN, NN]", getPosTagsAsString(tokens));
		    	
				String text56 = "System displays a tree view of available groups and channels and marks it";// those already subscribed by the user"; --> Constraint????
				tokens = nlpAnalyzer.getTokens(text56);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, NN, IN, JJ, NNS, CC, NNS, CC, VBZ, PRP]", getPosTagsAsString(tokens));
		    	
				String text57 = "System sends a registration request to the server";
				tokens = nlpAnalyzer.getTokens(text57);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, NN, TO, DT, NN]", getPosTagsAsString(tokens));
		    	
				String text58 = "User configures the option according to his/her preferences and confirm the changes";
				tokens = nlpAnalyzer.getTokens(text58);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, VBG, TO, PRP$, NNS, CC, VBP, DT, NNS]", getPosTagsAsString(tokens));//his/her is VBP ????
		    	
				String text59 = "User configures the option according to his / her preferences and confirm the changes";
				tokens = nlpAnalyzer.getTokens(text59);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, VBG, TO, PRP$, NNS, CC, VBP, DT, NNS]", getPosTagsAsString(tokens));
		    	
				
		    	String text60 = "users select/delete transactions"; //Error select/delete is JJ????
				tokens = nlpAnalyzer.getTokens(text60);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NNS, VBP, NNS]", getPosTagsAsString(tokens));
		    			    	
		    	
				String text61 = "User selects the channels he wants to subscribe and/or deselects already subscribed channels to unsubscribe them and chooses the Change subscription options";
				tokens = nlpAnalyzer.getTokens(text61);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NNS, PRP, VBZ, TO, VB, CC, VBZ, RB, JJ, NNS, TO, VB, PRP, CC, VBZ, DT, NN, NN, NNS]", getPosTagsAsString(tokens));
		    	
				String text62 = "user divide 3/5";
				tokens = nlpAnalyzer.getTokens(text62);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBP, CD]", getPosTagsAsString(tokens));
		    	
		    	
				String text63 = "user proceeds to walk";
				tokens = nlpAnalyzer.getTokens(text63);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, TO, VB]", getPosTagsAsString(tokens));
		    	
				String text64 = "System finds all users matching deletion criteria and deletes found user accounts";
				tokens = nlpAnalyzer.getTokens(text64);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NNS, JJ, NN, NNS, CC, VBZ, JJ, NN, NNS]", getPosTagsAsString(tokens));
		    	
				String text65 = "System displays a tree view of available groups and channels and marks those already subscribed by the user";
				tokens = nlpAnalyzer.getTokens(text65);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, NN, IN, JJ, NNS, CC, NNS, CC, VBZ, DT, RB, VBN, IN, DT, NN]", getPosTagsAsString(tokens));
		    	
				String text66 = "System displays an information that it cannot be used without prior registration"; //used is not a verb -> nsubjpass- Subject: it/PRP - 6- verb: used/VBN - 10 
				tokens = nlpAnalyzer.getTokens(text66);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	//assertEquals("", getPosTagsAsString(tokens));
		    	
				String text67 = "System checks if a group with the given name has not been already defined and if so, inserts the name of a new group into a database";
				tokens = nlpAnalyzer.getTokens(text67);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, IN, DT, NN, IN, DT, JJ, NN, VBZ, RB, VBN, RB, VBN, CC, IN, RB, ,, VBZ, DT, NN, IN, DT, JJ, NN, IN, DT, NN]", getPosTagsAsString(tokens));
		    	
				String text68 = "Administrator chooses a group containing the channel he wants to delete"; //containing is the VERB?: acl(group-4, containing-5)
				tokens = nlpAnalyzer.getTokens(text68);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, VBG, DT, NN, PRP, VBZ, TO, VB]", getPosTagsAsString(tokens));//DELETE is VERB!!!
		    	
				String text69 = "user signals the system to proceed the transaction";// acl(system-4, proceed-6)
				tokens = nlpAnalyzer.getTokens(text69);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, TO, VB, DT, NN]", getPosTagsAsString(tokens));
		    	
				String text70 = "Subscribe/unsubscribe news channels"; //without VERB?
				tokens = nlpAnalyzer.getTokens(text70);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, NN, NNS]", getPosTagsAsString(tokens));//UNSUBSCRIBE JJ ????
		    	
				String text71 = "use case ends"; 
				tokens = nlpAnalyzer.getTokens(text71);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, NN, VBZ]", getPosTagsAsString(tokens));
				
				String text72 = "download system finishes";
				tokens = nlpAnalyzer.getTokens(text72);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, NN, VBZ]", getPosTagsAsString(tokens));
		    	
		    	String text73 = "Broker System asks for Customer name, date of birth and address";
		    			tokens = nlpAnalyzer.getTokens(text73);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, NN, VBZ, IN, NN, NN, ,, NN, IN, NN, CC, NN]", getPosTagsAsString(tokens));
		    	
		    	String text74 = "resume step 1.1";//"1. resume step 1" - Error CoreNLP
				tokens = nlpAnalyzer.getTokens(text74);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, NN, CD]", getPosTagsAsString(tokens));
		    	
		    	String text75 = "Candidate fills the registration data form and submits the registration data form";
				tokens = nlpAnalyzer.getTokens(text75);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, NNS, NN, CC, VBZ, DT, NN, NNS, NN]", getPosTagsAsString(tokens)); //data/NNS  ???
		    	
		    	String text76 = "Check application status";
		    	tokens = nlpAnalyzer.getTokens(text76);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, NN, NN]", getPosTagsAsString(tokens));
		    	
		    	String text77 = "system stops computation and highlights line being processed";
		    	tokens = nlpAnalyzer.getTokens(text77);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, NN, CC, VBZ, NN, VBG, VBN]", getPosTagsAsString(tokens));
		    	
		    	String text78 = "User takes deposit receipt";
		    	tokens = nlpAnalyzer.getTokens(text78);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, NN, NN]", getPosTagsAsString(tokens));
		    	
		    	
		    	String text79 = "User creates filter for searching";
		    	tokens = nlpAnalyzer.getTokens(text79);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, NN, IN, VBG]", getPosTagsAsString(tokens));
		    	
		    	String text80 = "Request for licence";
		    	tokens = nlpAnalyzer.getTokens(text80);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, IN, NN]", getPosTagsAsString(tokens));
		    			
		    	
		    	String text81= "log  to the system";
		    	tokens = nlpAnalyzer.getTokens(text81);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, TO, DT, NN]", getPosTagsAsString(tokens));
		    	
		    	String text82= "log in to the system";
		    	tokens = nlpAnalyzer.getTokens(text82);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB, IN, TO, DT, NN]", getPosTagsAsString(tokens));
		    	
		    	String text83 = "System is loading files";
		    	tokens = nlpAnalyzer.getTokens(text83);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, VBG, NNS]", getPosTagsAsString(tokens));
		    	
		    	String text84 = "search";
		    	tokens = nlpAnalyzer.getTokens(text84);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[VB]", getPosTagsAsString(tokens)); //FIX PTR4
		    	
		    	String text85 = "Administrator types the name of the channel and the URL of the news service and selects Add channel";
		    	tokens = nlpAnalyzer.getTokens(text85);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, IN, DT, NN, CC, DT, NN, IN, DT, NN, NN, CC, VBZ, VB, NN]", getPosTagsAsString(tokens));
		    	
		    	String text86 = "System sends the server a  registration request";
		    	tokens = nlpAnalyzer.getTokens(text86);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, DT, NN, NN]", getPosTagsAsString(tokens));
		    	
		    	String text87 = "System sends a registration request to the server of the amazon";
		    	tokens = nlpAnalyzer.getTokens(text87);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, NN, TO, DT, NN, IN, DT, NN]", getPosTagsAsString(tokens));
		    	
		    	String text88 = "System sends to the server of the amazon a registration request";
		    	tokens = nlpAnalyzer.getTokens(text88);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, TO, DT, NN, IN, DT, NN, DT, NN, NN]", getPosTagsAsString(tokens));
		    	
		    	
		    	String text89 = "The System receives news messages and stores them in a local database";
		    	tokens = nlpAnalyzer.getTokens(text89);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[DT, NN, VBZ, NN, NNS, CC, VBZ, PRP, IN, DT, JJ, NN]", getPosTagsAsString(tokens));
		    	
		    	String text90 = "user Log in to anyone";
		    	tokens = nlpAnalyzer.getTokens(text90);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBP, IN, TO, NN]", getPosTagsAsString(tokens));
		    	
		    	
		    	String text91 = "User proceeds to print";
				tokens = nlpAnalyzer.getTokens(text91);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, TO, VB]", getPosTagsAsString(tokens));
		    	
		    	String text92 = "User proceeds to print";
				tokens = nlpAnalyzer.getTokens(text92);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, TO, VB]", getPosTagsAsString(tokens));
		    	
		    	
		    	String text93 = "System assigns an expiry date and time to each incoming message";
		    	tokens = nlpAnalyzer.getTokens(text93);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBZ, DT, NN, NN, CC, NN, TO, DT, JJ, NN]", getPosTagsAsString(tokens)); //FIX Rule PTR4: time/VBP  ????
		    			    	
		    	
		    	/*
		    	String text83 = "user Log in to the system";
		    	tokens = nlpAnalyzer.getTokens(text83);
				//Tokens - words
		    	System.out.println(getWordsAsString(tokens));
		    	//Pos tags
		    	System.out.println(getPosTagsAsString(tokens));
		    	assertEquals("[NN, VBP, TO, DT, NN]", getPosTagsAsString(tokens));
		    	*/
		    	
				
	}
	
	private static String getWordsAsString(List<CustomToken> tokens) {
		//Tokens - words
    	int i = 0;
    	StringBuffer words = new StringBuffer("[");
    	for(CustomToken token : tokens) {
    		if (i == 0)
    			words.append(token.getWord());
    		else
    			words.append(", " +token.getWord());
    		i++;
    	}
    	words.append("]");
    	return words.toString();
	}
	
	private static String getPosTagsAsString(List<CustomToken> tokens) {
		//Pos tags
    	int i = 0;
    	StringBuffer tags = new StringBuffer("[");
    	for(CustomToken token : tokens) {
    		if (i == 0)
    			tags.append(token.getPosTag());
    		else
    			tags.append(", " +token.getPosTag());
    		i++;
    	}		    	
    	tags.append("]");
    	return tags.toString();
	}
	
	@Test
	public void testGetSentenceComponentsWithSuccess( ) {
		
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
		
		//String text1 = "user wants to change their pin";
		
		//String text1 = "ATM verifies with the Bank that the User has enough money in account";

		//String text1 ="user select option for adding new clients";
		
		//String text1 ="user signals the system to proceed the transaction";
		
		//String text1 = "Administrator chooses a group containing the channel he wants to delete";
		
		//String text1 = "The system shall send a message to the receiver, and it provides an acknowledge message";
		//String text1 = "The system shall send a message to the receiver, and it provides an acknowledgement message";
		
		//String text1 = "The System receives news messages and stores them in a local database";
		
		//String text1 ="user register everyone";
		//String text1 ="user informs her login";
		
		//String text1 = "The Customer provides her Credit Card information";
		
		//String text1 ="The Broker System asks the Customer for Credit Card information";
		
		//String text1 = "The Broker System asks a Payment System to process the Customer's Payment";
		
		//String text1 ="ATM prompts her to enter new PIN";

		
		
		//String text1 = "System adds a new client to the database and informs user about it";
		
		
		
		//String text1 = "search";
		
		//String text1 = "User creates filter for searching";
		
		
		
		//String text1 = "Administrator types the message and posts it";
		
		//String text1 = "System displays a list of defined channel groups and an add/delete group menu";
		
		//String text1 = "Administrator chooses a group to which he wants to ADD A NEW CHANNEL";
		
		
		//String text1 = "User types in the numbers of his PIN and presses the Enter button"; //PIN: Indirect?
		
		//String text1 = "User selects the channels he/she wants to subscribe and/or deselects already subscribed channels to unsub-scribe them and chooses the Change subscription options";
		
		
		
		
		//FIX
		
		//String text1 = "user signals you to proceed the transaction";
		
		//String text1 = "System informs user about that fact";
		
		String text1 = "User select a client for whom new contract will be added";
		
		
		//String text1 = "User selects fields to be included in the report and rules to filter values from database"; //rules: VBZ? --> OK, Confusing-complex sentence
		
		//String text1 = "System assigns an expiry date and time to each incoming message"; //Time: verb?  -> FIXED
		
		//String text1 = "System displays a list of groups with subscribed channels and the number of new messages in each of them";//them: implicit
		
		//String text1 = "Bank retrieves User`s current balance from their account"; // their: implicit
		
		//String text1 = "Use case ends when user logs out or selects different option"; // selects: Action-Verb? --> modifier-action-verb
		
		//String text1 = "Administrator selects the channel(s) he wants to delete and chooses the Delete option";
		
		//String text1 = "Administrator chooses a group to which he wants to ADD A NEW CHANNEL";
		
		//String text1 = "System asks the user if he/she wants to register";
				
		//String text1 = "User confirms he/she wants to register";
		
		//String text1 = "User selects the channels he/she wants to subscribe and/or deselects already subscribed channels to unsubscribe them and chooses the Change subscription options";
		
		//String text1 = "If the user is already registered, the system automatically updates news messages from subscribed channels (refer to DOWNLOAD NEWS use case). If no, the system attempts to REGISTER A NEW USER (refer to Register a new user use case)";
		
				
		
		//String text1 ="Subscribe/unsubscribe news channels"; // FIX: unsubscribe is NN?
		
				
		//String text1 = "System queries the database for news messages, whose expiry date and time have passed";
		
		//String text1 = "search";
		
		 
		//String text1 = "Log in to the system";
		
		//String text1 = "back to step 2";
		
		//String text1 = "Administrator adds more channels. Proceed to step 7";//channels: subject?
		
		
		//System displays a tree view of available groups and channels and marks those already subscribed by the user
		//marks is OK VERB
		
		
		
		
		
		//String text1 = "System informs user about that";
		
		//String text1 = "System displays an information that it cannot be used without prior registration"; //registration : subject?
		
		//String text1 = "User reads the message and closes it or uses a hyperlink to go to the full message";
		
		text1 = text1.toLowerCase();
		text1 = ScenarioCleaner.cleanSentence(text1);
				
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
}

