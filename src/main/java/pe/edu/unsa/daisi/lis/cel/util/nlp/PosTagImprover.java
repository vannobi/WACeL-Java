package pe.edu.unsa.daisi.lis.cel.util.nlp;

import java.util.ArrayList;
import java.util.List;

import pe.edu.unsa.daisi.lis.cel.util.RegularExpression;
import pe.edu.unsa.daisi.lis.cel.util.nlp.dictionary.english.SpecialVerb;

/**
 * It is used for adjusting POSTAGGING of NLP tokens (List<CustomToken>) phase and getting subjects, objects and action-verbs of a sentence (CustomSentence)  
 * @pre-condition 
 * @author Edgar
 *
 */
public final class PosTagImprover {
	
	public static final String EMPTY_CHAR = "";
	public static final String WHITE_SPACE = " ";
	public static final String PREPOSITION_OF = "of";
	public static final String VERB_TO_HAVE = "have";
	public static final String VERB_TO_BE = "be";
	public static final String S_CHAR = "s";
	public static final String POSSESSIVE = "'s";

	/**
	 * @param tokens
	 * @param start from index
	 * @param end to index
	 * @return
	 */
	private static String getPosTagsAsString(List<CustomToken> tokens, int start, int end) {
		//Pos tags
		int currentElement = 0;
		StringBuffer tags = new StringBuffer("");
		if(start >= 0 && end <= tokens.size() && start <= end) {
			for(int i = start; i < end; i++) {
	    		CustomToken token = tokens.get(i) ;
	    		if (currentElement == 0)
	    			tags.append(token.getPosTag());
	    		else
	    			tags.append(" " +token.getPosTag());
	    		currentElement++;
	    	}
		} else {
			for(int i = 0; i < tokens.size(); i++) {
	    		CustomToken token = tokens.get(i) ;
	    		if (currentElement == 0)
	    			tags.append(token.getPosTag());
	    		else
	    			tags.append(" " +token.getPosTag());
	    		currentElement++;
	    	}
		} 
	
    	return tags.toString();
	}
	
	/**
	* @Title: Adjust NOUN/VERB POS TAGS
	* @Goal: Return Tokens from a sentence with adjusted NOUNS/VERBS by applying Rules to adjust the accuracy of POS Tagger
	* @Context:
		- Noun POS tags: 
			- NN Noun, sing. or mass      dog
			- NNS Noun, plural            dogs
		- Action-Verb POS tags: 
				- VB verb, base form          	  eat
				- VBP Verb, infinitive verb       eat
				- VBZ Verb, present tense         eats
	* @Actor: C&l 
	* @Resource: tokens, SpecialVerb.NOUNS_AND_VERBS_HASH
	**/
	public static List<CustomToken> adjustPosTags(List<CustomToken> tokens){
		//System.out.println(getPosTagsAsString(tokens, 0, tokens.size()));
		tokens = adjustNounPosTags(tokens);
		tokens = adjustVerbPosTags(tokens);
		tokens = adjustPrePositionPosTags(tokens);
		tokens = adjustAdjectivePosTags(tokens);
		tokens = adjustUseCaseKeywords(tokens);
		//System.out.println(getPosTagsAsString(tokens, 0, tokens.size()));
		return tokens;
	}
	
	
	
	public static List<CustomToken> updatePosTagWithVerb(List<CustomToken> tokens, CustomToken noun) {
		//Pre-process nouns

		int tokenIndex = noun.getIndex();//Index in Tokens returned by NLP tool
		String posTag = PosTagEnum.VB.name(); //base form
		if (noun.getPosTag().equals(PosTagEnum.NN.name())) {
			if(tokenIndex >  0){
				posTag = PosTagEnum.VBP.name(); //present tense
			}
		} else { //NNS
			posTag = PosTagEnum.VBZ.name(); //present tense
		}
		noun.setPosTag(posTag);
		tokens.set(tokenIndex, noun);
		
		return tokens;
	}
	
	public static List<CustomToken> updatePosTagPrepositionWithVerb(List<CustomToken> tokens, CustomToken preposition) {
		//Pre-process nouns

		int tokenIndex = preposition.getIndex();//Index in Tokens returned by NLP tool
		String posTag = PosTagEnum.VB.name(); //base form
		if(tokenIndex >  0){
				posTag = PosTagEnum.VBP.name(); //present tense
			
		} 
		preposition.setPosTag(posTag);
		tokens.set(tokenIndex, preposition);
		
		return tokens;
	}
	
	public static List<CustomToken> updatePosTagVerbWithAdjective(List<CustomToken> tokens, CustomToken verb) {
		//Pre-process nouns

		int tokenIndex = verb.getIndex();//Index in Tokens returned by NLP tool
		String posTag = PosTagEnum.JJ.name(); //base form
		verb.setPosTag(posTag);
		tokens.set(tokenIndex, verb);
		
		return tokens;
	}
	
	/**
	* 
	* @Title: Adjust NOUN POS TAGS
	* @Goal: Return Tokens from a sentence with adjusted NOUNS by applying Rules to adjust the accuracy of POS Tagger
	* @Context:
		- Noun POS tags: 
			- NN Noun, sing. or mass      dog
			- NNS Noun, plural            dogs
		
	* @Actor: C&l 
	* @Resource: tokens, SpecialVerb.NOUNS_AND_VERBS_HASH
	**/
	public static List<CustomToken> adjustNounPosTags(List<CustomToken> tokens){ 
		List<CustomToken> nouns = new ArrayList<>(); //filtered nouns
		List<CustomToken> allNouns = new ArrayList<>(); 

		if (tokens != null && !tokens.isEmpty()) {
			//Get all nouns
			for(int i = 0; i < tokens.size(); i++ ){
				if (tokens.get(i).getPosTag().contains(PosTagEnum.NN.name())){
					//i: position in the analysis tokens array;
					CustomToken tmpNoun = new CustomToken(tokens.get(i).getWord(), tokens.get(i).getPosTag(), tokens.get(i).getStem(), i) ;
					allNouns.add(tmpNoun);
					
				}
			}
			//PTR1: Check that a 'Noun' is effectively a 'Noun'. Prepositions are most commonly followed by a 'Noun' phrase or 'Pronoun'
			for(int i = 0; i < allNouns.size(); i++ ){
				CustomToken noun = allNouns.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(noun.getStem())) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					//String REGEX_PREV_POS_TAGS = ".*((DT|IN|POS|PRP\\$|JJ.?)|(IN(\\s+DT)?\\s+NN.?))$";
					String REGEX_PREV_POS_TAGS = ".*((DT|PDT|IN|POS|PRP\\$|JJ.?)|(IN(\\s+DT)?\\s+NN.?))$";
					String prevPOSs = getPosTagsAsString(tokens, 0, noun.getIndex());
					//Next Tokens (contains POS tags:)
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS)) {
						//THEN (Adjust Token)
						tokens.get(allNouns.get(i).getIndex()).setConfirmedNoun(true);
						//System.out.println("PTR1 Token: " + noun.getStem());
						//System.out.println("PTR1 Token is Confirmed Noun!");
					}	
				}
			}

			//PTR2: Check that a 'Noun' is a 'Verb'. The 'Noun' position is after a token, which is not a coordinating conjunction (CC) 
			for(int i = 0; i < allNouns.size(); i++ ){
				CustomToken noun = allNouns.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(noun.getStem()) && !tokens.get(allNouns.get(i).getIndex()).isConfirmedNoun()
						&& noun.getIndex() > 0) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = ".*([^(CC)])$";
					String prevPOSs = getPosTagsAsString(tokens, 0, noun.getIndex());
					//Next Tokens (contains POS tags:)
					//String REGEX_NEXT_POS_TAGS = "^(DT|IN|NN.?|PRP|RB.?|JJ.?|VB.?|CD).*";
					//String REGEX_NEXT_POS_TAGS = "^(DT|PDT|IN|NN.?|PRP|RB.?|JJ.?|VB.?|CD).*";
					String REGEX_NEXT_POS_TAGS = "^(DT|PDT|IN|NN.?|PRP.?|RB.?|JJ.?|VB.?|CD).*";
					String nextPOSs = getPosTagsAsString(tokens, noun.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) && nextPOSs.matches(REGEX_NEXT_POS_TAGS)) {
						//THEN (Adjust Token)
						String singularNounPrev = tokens.get(allNouns.get(i).getIndex()-1).getStem();
						if (!SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(singularNounPrev) 
								&& !tokens.get(allNouns.get(i).getIndex()-1).getPosTag().contains(PosTagEnum.VB.name())){
							tokens = updatePosTagWithVerb(tokens, noun);
							//tokens.get(allNouns.get(i).getIndex()).setConfirmedVerb(true);
							//System.out.println("PTR2 Token: " + noun.getStem());
							//System.out.println("PTR2 Token is Verb!");//FIxed in PTR13: System presents a registration data form/VBP and asks to enter the registration data
						}						
					}

				}
			}

			//Update all nouns
			allNouns = new ArrayList<>(); 
			for(int i = 0; i < tokens.size(); i++ ){
				if (tokens.get(i).getPosTag().contains(PosTagEnum.NN.name())){
					//i: position in the analysis tokens array;
					CustomToken tmpNoun = new CustomToken(tokens.get(i).getWord(), tokens.get(i).getPosTag(), tokens.get(i).getStem(), i) ;
					allNouns.add(tmpNoun);
				}
			}			
			//PTR3: Check that a 'Noun' is effectively a 'Noun'. The 'Noun' position is the first or after a coordinating conjunction (CC)  
			for(int i = 0; i < allNouns.size(); i++ ){
				CustomToken noun = allNouns.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(noun.getStem()) && !tokens.get(allNouns.get(i).getIndex()).isConfirmedNoun()) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = "(^|.*CC)$";
					String prevPOSs = getPosTagsAsString(tokens, 0, noun.getIndex());
					//Next Tokens (contains POS tags:)
					String REGEX_NEXT_POS_TAGS = "^(NN.?\\s+(NN.?|VB.?)).*";
					String nextPOSs = getPosTagsAsString(tokens, noun.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) && nextPOSs.matches(REGEX_NEXT_POS_TAGS)) {
						//THEN (Adjust Token)
						if(noun.getIndex() + 2 < tokens.size() && tokens.get(noun.getIndex() + 2) != null) {
							String singularNounNext = tokens.get(allNouns.get(i).getIndex() + 2).getStem();
							if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(singularNounNext)) {
								tokens.get(allNouns.get(i).getIndex()).setConfirmedNoun(true);
								//System.out.println("PTR3 Token: " + noun.getStem());
								//System.out.println("PTR3 Token is Confirmed Noun!");
							}	
						} 											
					}

				}
			}
			
			//PTR4: Check that a 'Noun' is a 'Verb'. The 'Noun' position is the first or after a coordinating conjunction (CC)  
			for(int i = 0; i < allNouns.size(); i++ ){
				CustomToken noun = allNouns.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(noun.getStem()) && !tokens.get(allNouns.get(i).getIndex()).isConfirmedNoun()) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = "(^|.*CC)$";
					String prevPOSs =  getPosTagsAsString(tokens, 0, noun.getIndex());
					//Next Tokens (contains POS tags:)
					//String REGEX_NEXT_POS_TAGS = "^(IN\\s+)?(NN.?|DT|PDT|JJ.?|VBD|VBN|RB.?|TO).*";
					String REGEX_NEXT_POS_TAGS = "^(IN\\s+)?(NN.?|DT|PDT|JJ.?|VBD|VBN|RB.?|TO|PRP.?).*";
					//String REGEX_NEXT_POS_TAGS = "^(IN\\s+)?(NN.?|DT|PDT|JJ.?|VBD|VBN|((VB|VBP)\\s+NN.?)|RB.?|TO).*"; OpenNLP
					String nextPOSs =  getPosTagsAsString(tokens, noun.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) && nextPOSs.matches(REGEX_NEXT_POS_TAGS)) {
						//THEN (Adjust Token)
						tokens = updatePosTagWithVerb(tokens, noun);
						//System.out.println("PTR4 Token: " + noun.getStem());
						//System.out.println("PTR4 Token is Verb!");
																		
					}

				}
			}
			
			//Update all nouns
			allNouns = new ArrayList<>(); 
			for(int i = 0; i < tokens.size(); i++ ){
				if (tokens.get(i).getPosTag().contains(PosTagEnum.NN.name())){
					//i: position in the analysis tokens array;
					CustomToken tmpNoun = new CustomToken(tokens.get(i).getWord(), tokens.get(i).getPosTag(), tokens.get(i).getStem(), i) ;
					allNouns.add(tmpNoun);
					
				}
			}

			//PTR5: Check that a 'Noun' between a Determiner (or Preposition or Noun or Adverb) and a 'TO' is effectively a 'Noun'
			for(int i = 0; i < allNouns.size(); i++ ){
				CustomToken noun = allNouns.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(noun.getStem())) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					//String REGEX_PREV_POS_TAGS = ".*(DT|IN|NN.?|RB)";
					//String REGEX_PREV_POS_TAGS = ".*(((VB|VBP|VBZ)\\s+)(DT|IN|NN.?|RB)+)$";
					String REGEX_PREV_POS_TAGS = ".*(((VB|VBP|VBZ)\\s+)(DT|PDT|IN|NN.?|RB)+)$";
					String prevPOSs =  getPosTagsAsString(tokens, 0, noun.getIndex());
					//Next Tokens (contains POS tags:)
					//String REGEX_NEXT_POS_TAGS = "^(TO\\s+(DT|IN|NN.?|PRP|JJ.?)).*";
					String REGEX_NEXT_POS_TAGS = "^(TO\\s+(DT|PDT|IN|NN.?|PRP|JJ.?)).*";
					String nextPOSs =  getPosTagsAsString(tokens, noun.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) && nextPOSs.matches(REGEX_NEXT_POS_TAGS)) {
						//THEN (Adjust Token)
						tokens.get(allNouns.get(i).getIndex()).setConfirmedNoun(true);
						//System.out.println("PRT5 Token: " + noun.getStem());
						//System.out.println("PTR5 Token is Confirmed Noun!");
						
					}												
				}


			}
			
			//Update all nouns
			allNouns = new ArrayList<>(); 
			for(int i = 0; i < tokens.size(); i++ ){
				if (tokens.get(i).getPosTag().contains(PosTagEnum.NN.name())){
					//i: position in the analysis tokens array;
					CustomToken tmpNoun = new CustomToken(tokens.get(i).getWord(), tokens.get(i).getPosTag(), tokens.get(i).getStem(), i) ;
					allNouns.add(tmpNoun);
					
				}
			}
			//PTR6:	Check that a 'Noun' followed by a gerund verb is effectively a 'Noun'
			for(int i = 0; i < allNouns.size(); i++ ){
				CustomToken noun = allNouns.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(noun.getStem())) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = "";
					String prevPOSs =   getPosTagsAsString(tokens, 0, noun.getIndex());
					//Next Tokens (contains POS tags:)
					String REGEX_NEXT_POS_TAGS = "^(VBG).*";
					String nextPOSs =   getPosTagsAsString(tokens, noun.getIndex()+1, tokens.size());
					if(nextPOSs.matches(REGEX_NEXT_POS_TAGS)) {
						//THEN (Adjust Token)
						tokens.get(allNouns.get(i).getIndex()).setConfirmedNoun(true);
						//System.out.println("PTR6 Token: " + noun.getStem());
						//System.out.println("PTR6 Token is Confirmed Noun!");
					}												
				}

			}
			
			
			//PTR7:	Check that a 'Noun' is effectively a 'Noun'. The 'Noun' is preceded by a Verb + Determiner + Noun or Adjective
			for(int i = 0; i < allNouns.size(); i++ ){
				CustomToken noun = allNouns.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(noun.getStem()) && !tokens.get(allNouns.get(i).getIndex()).isConfirmedNoun()) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = ".*((VB|VBZ|VBP)(\\s+(PDT|DT))?(\\s+(NN.?|JJ.?))*)$";
					String prevPOSs =   getPosTagsAsString(tokens, 0, noun.getIndex());
					//Next Tokens (contains POS tags:)
					String REGEX_NEXT_POS_TAGS = "";
					String nextvPOSs =   getPosTagsAsString(tokens, noun.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS)) {
						//THEN (Adjust Token)
						if(noun.getIndex() -1 > 0 && tokens.get(noun.getIndex() - 1) != null) {
							String singularNounPrev = tokens.get(allNouns.get(i).getIndex() - 1).getStem();
							if(!SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(singularNounPrev)) {
								tokens.get(allNouns.get(i).getIndex()).setConfirmedNoun(true); 
								//System.out.println("PTR7 Token: " + noun.getStem());
								//System.out.println("PTR7 Token is Confirmed Noun!");
							}	
						} 
					}												
				}
				
			}
			
			//PTR8:	Check that a 'Noun' is a 'Verb'. The 'Noun' position is the last or before a coordinating conjunction (CC)
			for(int i = 0; i < allNouns.size(); i++ ){
				CustomToken noun = allNouns.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(noun.getStem()) && !tokens.get(allNouns.get(i).getIndex()).isConfirmedNoun()) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = ".*(((PDT|DT)\\s+)?((NN.?|JJ.?|VBD|VBN)\\s+)*(NN.?))$";
					String prevPOSs =    getPosTagsAsString(tokens, 0, noun.getIndex());
					//Next Tokens (contains POS tags:)
					String REGEX_NEXT_POS_TAGS = "^($|CC\\s+(VB|VBP|VBZ).*)"; //include , ; ex. user register, update or delete transactions
					String nextPOSs =    getPosTagsAsString(tokens, noun.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) && nextPOSs.matches(REGEX_NEXT_POS_TAGS) ) {
						//THEN (Adjust Token)
						if(noun.getIndex() - 1 >= 0 && tokens.get(noun.getIndex() - 1) != null) {
							String singularNounPrev = tokens.get(allNouns.get(i).getIndex() - 1).getStem();
							if(!SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(singularNounPrev)) {
								tokens = updatePosTagWithVerb(tokens, noun);
								//tokens.get(allNouns.get(i).getIndex()).setConfirmedNoun(true); 
								//System.out.println("PTR8 Token: " + noun.getStem()); //No funciona: User fills all required personal client data forms //Corregido en 10
								//System.out.println("PTR8 Token is Verb!");
							}	
						} 
					}												
				}
				
			}
			

		}

		return tokens;
	}
	
	
	
	/**
	* 
	* @Title: Adjust VERB POS TAGS
	* @Goal: Return Tokens from a sentence with adjusted VERBS by applying Rules to adjust the accuracy of POS Tagger
	* @Context:
		- Action-Verb POS tags: 
				- VB verb, base form          	  eat
				- VBP Verb, infinitive verb       eat
				- VBZ Verb, present tense         eats
	* @Actor: C&l 
	* @Resource: tokens, NOUNS_AND_VERBS_HASH
	**/
	public static List<CustomToken> adjustVerbPosTags(List<CustomToken> tokens){ 
		List<CustomToken> allVerbs = new ArrayList<>();
		List<CustomToken> verbs = new ArrayList<>(); //filtered verbs

		if (tokens != null && !tokens.isEmpty()) {
			//Get all verbs 
			for(int i = 0; i < tokens.size(); i++ ){
				if (tokens.get(i).getPosTag().equals(PosTagEnum.VB.name()) || tokens.get(i).getPosTag().equals(PosTagEnum.VBP.name()) || tokens.get(i).getPosTag().equals(PosTagEnum.VBZ.name())){
					//i: position in the analysis tokens array;
					CustomToken tmpVerb = new CustomToken(tokens.get(i).getWord(), tokens.get(i).getPosTag(), tokens.get(i).getStem(), i) ;
					if(tokens.get(i).getWord().length() > 1)//???
						allVerbs.add(tmpVerb);

				}
			}

			//PTR9: Check that a 'Verb' is effectively a 'Verb'. Prepositions are most commonly followed by a 'Noun' phrase or 'Pronoun'   
			for(int i = 0; i < allVerbs.size(); i++ ){
				CustomToken verb = allVerbs.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(verb.getStem())) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = ".*(DT|PDT|IN|POS|PRP\\$|JJ.?)$";
					String prevPOSs = getPosTagsAsString(tokens, 0, verb.getIndex());
					//Next Tokens (contains POS tags:)
					String REGEX_NEXT_POS_TAGS = ""; 
					String nextPOSs =  getPosTagsAsString(tokens, verb.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) ) {
						//THEN (Adjust Token)
						tokens.get(verb.getIndex()).setConfirmedNoun(true);
						tokens = updatePosTagWithNoun(tokens, verb);
						//System.out.println("PTR9 Token: " + verb.getStem());
						//System.out.println("PTR9 Token is Confirmed Noun!");
											
					}

					
				}
			}
			
			//PTR10: Check that a 'Verb' is effectively a 'Verb'. The 'Verb' position is after a token, which is not a coordinating conjunction (CC)    
			for(int i = 0; i < allVerbs.size(); i++ ){
				CustomToken verb = allVerbs.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(verb.getStem())) {
					//WHEN
					String REGEX_PREV_POS_TAGS = ".*([^(CC)])$";
					String prevPOSs = getPosTagsAsString(tokens, 0, verb.getIndex());
					//Next Tokens (contains POS tags:)
					//String REGEX_NEXT_POS_TAGS = "^(DT|IN|NN.?|PRP|RB.?|JJ.?|VB.?).*"; 
					//String REGEX_NEXT_POS_TAGS = "^(DT|PDT|IN|NN.?|PRP|RB.?|JJ.?|VB.?).*";
					String REGEX_NEXT_POS_TAGS = "^(DT|PDT|IN|NN.?|PRP.?|RB.?|JJ.?|VB.?).*";
					String nextPOSs =  getPosTagsAsString(tokens, verb.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) && nextPOSs.matches(REGEX_NEXT_POS_TAGS) ) {
						//THEN (Adjust Token)
						String singularNounPrev = tokens.get(allVerbs.get(i).getIndex()-1).getStem();
						if (!SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(singularNounPrev) ){
							tokens.get(allVerbs.get(i).getIndex()).setConfirmedVerb(true);
							//System.out.println("PTR10 Token: " + verb.getStem());
							//System.out.println("PTR10 Token is confirmed Verb!");//FIxed in PTR13: System presents a registration data form/VBP and asks to enter the registration data
						}						
					}

					
				}
			}
			
			//PTR11: Check that a 'Verb' is effectively a 'Verb'. The 'Verb' is preceded by a Noun and followed by 'TO' + Verb    
			for(int i = 0; i < allVerbs.size(); i++ ){
				CustomToken verb = allVerbs.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(verb.getStem())) {
					//WHEN
					String REGEX_PREV_POS_TAGS = ".*(PRP|NN.?)$";
					String prevPOSs = getPosTagsAsString(tokens, 0, verb.getIndex());
					//Next Tokens (contains POS tags:)
					String REGEX_NEXT_POS_TAGS = "^((TO\\s+)(VB.?|NN.?)).*"; 
					String nextPOSs =  getPosTagsAsString(tokens, verb.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) && nextPOSs.matches(REGEX_NEXT_POS_TAGS)) {
						//THEN (Adjust Token)
						String singularNounPrev = tokens.get(allVerbs.get(i).getIndex()-1).getStem();
						String singularVerbNext = tokens.get(allVerbs.get(i).getIndex()+2).getStem();
						if (!SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(singularNounPrev) && SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(singularVerbNext)){
							tokens.get(allVerbs.get(i).getIndex()).setConfirmedVerb(true);
							//System.out.println("PTR1 Token: " + verb.getStem());
							//System.out.println("PTR11 Token is confirmed Verb!");
						}						
					}

					
				}
			}
			
						
			//PTR12: Check that a 'Verb' followed by "OF" or TO_BE or TO_HAVE is a 'Noun'  
			for(int i = 0; i < allVerbs.size(); i++ ){
				CustomToken verb = allVerbs.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(verb.getStem())) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = "";
					String prevPOSs = getPosTagsAsString(tokens, 0, verb.getIndex());
					//Next Tokens (contains POS tags:)
					String REGEX_NEXT_POS_TAGS = "^(IN|VB.?).*"; 
					String nextPOSs =  getPosTagsAsString(tokens, verb.getIndex()+1, tokens.size());
					if(nextPOSs.matches(REGEX_NEXT_POS_TAGS)) {
						//THEN (Adjust Token)
						String nextWord = tokens.get(allVerbs.get(i).getIndex() + 1).getStem();
						if (nextWord.equals(PREPOSITION_OF) || nextWord.equals(VERB_TO_BE) || nextWord.equals(VERB_TO_HAVE)){ 
							tokens = updatePosTagWithNoun(tokens, verb);
							//System.out.println("PTR12 Token: " + verb.getStem());
							//System.out.println("PTR12 Token is Noun!");
							/**
							 * Exception
							 * I don't approve of hunting animals for their fur.
								Our dog died of old age.
								This shampoo smells of bananas.
								https://learnenglish.britishcouncil.org/grammar/intermediate-to-upper-intermediate/verbs-and-prepositions
								https://7esl.com/verb-preposition-combinations/

							 */
						}						
					}

				}
			}
			
			//PTR13: Check that a 'Verb' is a 'Noun'. The 'Verb' position is the last or before a coordinating conjunction (CC)  
			for(int i = 0; i < allVerbs.size(); i++ ){
				CustomToken verb = allVerbs.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(verb.getStem())) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					//String REGEX_PREV_POS_TAGS = ".*([^((VB|VBZ|VBP)\\s+(TO))])";
					//String REGEX_PREV_POS_TAGS = ".*((VB|VBZ|VBP)\\s+((PDT|DT)\\s+)?((DT|IN|NN.?|JJ.?|VBD|VBN)\\s+)*NN.?)$";//PDT?
					String REGEX_PREV_POS_TAGS = ".*((VB|VBZ|VBP)\\s+((PDT|DT)\\s+)?((DT|IN|TO|NN.?|JJ.?|VBD|VBN)\\s+)*NN.?)$";//PDT?
					String prevPOSs = getPosTagsAsString(tokens, 0, verb.getIndex());
					//Next Tokens (contains POS tags:)
					String REGEX_NEXT_POS_TAGS = "^($|(CC\\s+[^(VB|VBP|VBZ)]))"; 
					String nextPOSs =  getPosTagsAsString(tokens, verb.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) && nextPOSs.matches(REGEX_NEXT_POS_TAGS)) {
						//THEN (Adjust Token)
						tokens = updatePosTagWithNoun(tokens, verb);
						//System.out.println("PTR13 Token: " + verb.getStem());
						//System.out.println("PTR13 Token is Noun!");											
					}

				}
			}
		}

		return tokens;
	}
	public static List<CustomToken> updatePosTagWithNoun(List<CustomToken> tokens, CustomToken verb) {
		//Pre-process verbs

		int tokenIndex = verb.getIndex();//Index in Tokens returned by NLP tool
		String posTag = PosTagEnum.NN.name(); //singular
		if (verb.getPosTag().equals(PosTagEnum.VBZ.name())) {
			posTag = PosTagEnum.NNS.name(); //plural
		}
		verb.setPosTag(posTag);
		tokens.set(tokenIndex, verb);
		
		return tokens;
	}
	
	/**
	* Check that  a PREPOSITION is a VERB
	* @Title: Adjust VERB POS TAGS
	* @Goal: Return Tokens from a sentence with adjusted VERBS by applying Rules to adjust the accuracy of POS Tagger
	* @Context:
		- Action-Verb POS tags: 
				- VB verb, base form          	  eat
				- VBP Verb, infinitive verb       eat
				- VBZ Verb, present tense         eats
	* @Actor: C&l 
	* @Resource: tokens
	**/
	public static List<CustomToken> adjustPrePositionPosTags(List<CustomToken> tokens){ 
		if (tokens != null && !tokens.isEmpty()) {

			//Check PREPOSITIONS that are VERBS
			//Ex. user really like it -> NN RB IN (NN& | PRP& W&) -> IN is VB
			//FIX: Complex verbs? -> carry out, move up? 		
			//Get all Prepositions 
			List<CustomToken> allPrepositions = new ArrayList<>();
			for(int i = 0; i < tokens.size(); i++ ){
				if (tokens.get(i).getPosTag().equals(PosTagEnum.IN.name())){
					//i: position in the analysis tokens array;
					CustomToken tmpPrep = new CustomToken(tokens.get(i).getWord(), tokens.get(i).getPosTag(), tokens.get(i).getStem(), i) ;
					if(tokens.get(i).getWord().length() > 1)//???
						allPrepositions.add(tmpPrep);

				}
			}
			
			//PTR14: Check that a 'Preposition' is a 'Verb'. The 'Preposition' position is the first or after a coordinating conjunction (CC)  
			for(int i = 0; i < allPrepositions.size(); i++ ){
				CustomToken preposition = allPrepositions.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(preposition.getStem())) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = "(^|.*CC)$";
					String prevPOSs = getPosTagsAsString(tokens, 0, preposition.getIndex());
					//Next Tokens (contains POS tags:)
					String REGEX_NEXT_POS_TAGS = "^(NN.?|DT|PDT|JJ.?).*"; 
					String nextPOSs =  getPosTagsAsString(tokens, preposition.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) && nextPOSs.matches(REGEX_NEXT_POS_TAGS)) {
						//THEN (Adjust Token)
						tokens = updatePosTagPrepositionWithVerb(tokens, preposition);
						//System.out.println("PTR14 Token: " + preposition.getStem());
						//System.out.println("PTR14 Token is Verb -Prep!");
											
					}

				}
			}
			
			//PTR15: Check that a 'Preposition' is a 'Verb'. The 'Preposition' position is after a token, which is not a coordinating conjunction (CC)  
			for(int i = 0; i < allPrepositions.size(); i++ ){
				CustomToken preposition = allPrepositions.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(preposition.getStem())) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = ".*(PRP|NN.?|RB.?)$";
					String prevPOSs = getPosTagsAsString(tokens, 0, preposition.getIndex());
					//Next Tokens (contains POS tags:)
					//String REGEX_NEXT_POS_TAGS = "^(NN.?|PRP|WDT|WP.?|WRB).*"; 
					String REGEX_NEXT_POS_TAGS = "^(NN.?|PRP.?|WDT|WP.?|WRB).*";
					String nextPOSs =  getPosTagsAsString(tokens, preposition.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) && nextPOSs.matches(REGEX_NEXT_POS_TAGS) ) {
						//THEN (Adjust Token)
						tokens = updatePosTagPrepositionWithVerb(tokens, preposition);
						//System.out.println("PTR15 Token: " + preposition.getStem());
						//System.out.println("PTR15 Token is Verb -Prep!");
											
					}

				}
			}
		}
		
		return tokens;
	}
	
	/**
	* 
	* Check that an ADJECTIVE is a VERBS (user/NN select/JJ option/NN for/IN editing/VBG -> user/NN select/VB option/NN for/IN editing/VBG) 
	* <br>We use the -ing and -ed forms of regular and irregular verbs as adjectives
	* <br> https://dictionary.cambridge.org/grammar/british-grammar/about-adjectives-and-adverbs/adjectives-forms
	* @Title: Adjust ADJECTIVE POS TAGS
	* @Goal: Return Tokens from a sentence with adjusted NOUNS by applying Rules to adjust the accuracy of POS Tagger
	* @Context:
		- JJ POS tags: 
			- JJ adjective 	interesting, interested
		
	* @Actor: C&l 
	* @Resource: tokens
	**/
	public static List<CustomToken> adjustAdjectivePosTags(List<CustomToken> tokens){ 
		if (tokens != null && !tokens.isEmpty()) {
			List<CustomToken> allAdjectives = new ArrayList<>();
			List<CustomToken> allVerbs = new ArrayList<>();//verbs with -ing and -ed
			for(int i = 0; i < tokens.size(); i++ ) {
				//Check that a VERB is an ADJECTIVE
				//Get all verbs with -ing and -ed - filter VBN (have + verb)
				if (tokens.get(i).getPosTag().equals(PosTagEnum.VBG.name()) || tokens.get(i).getPosTag().equals(PosTagEnum.VBD.name()) || tokens.get(i).getPosTag().equals(PosTagEnum.VBN.name())){ //VBN : error from CoreNLP => all/DT required/VBN ?????
					//i: position in the analysis tokens array;
					CustomToken tmpVerb = new CustomToken(tokens.get(i).getWord(), tokens.get(i).getPosTag(), tokens.get(i).getStem(), i) ;
					if(tokens.get(i).getWord().length() > 1)//???
						allVerbs.add(tmpVerb);
				}
								
				//Check that an ADJECTIVE is a VERB (user/NN select/JJ option/NN for/IN editing/VBG -> user/NN select/VB option/NN for/IN editing/VBG)
				//STANFORD ERROR: For Example "User select option for editing" -> "User/VB select/JJ option/NN for/IN editing/VBG "
				else if (tokens.get(i).getPosTag().equals(PosTagEnum.JJ.name())){//Error CoreNLP
					//i: position in the analysis tokens array;
					CustomToken tmpAdjective = new CustomToken(tokens.get(i).getWord(), tokens.get(i).getPosTag(), tokens.get(i).getStem(), i) ;
					if(tokens.get(i).getWord().length() > 1)//???
						allAdjectives.add(tmpAdjective);
				}
				
			}
			//PTR16: Check that a 'Adjective' is effectively a 'Adjective'. Prepositions are most commonly followed by a 'Noun' phrase or 'Pronoun' or Adjective   
			for(int i = 0; i < allAdjectives.size(); i++ ){
				CustomToken verb = allAdjectives.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(verb.getStem())) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = ".*(DT|PDT|IN|POS|PRP\\$|JJ.?)$";
					String prevPOSs = getPosTagsAsString(tokens, 0, verb.getIndex());
					//Next Tokens (contains POS tags:)
					String REGEX_NEXT_POS_TAGS = ""; 
					String nextPOSs =  getPosTagsAsString(tokens, verb.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) ) {
						//THEN (Adjust Token)
						tokens.get(verb.getIndex()).setConfirmedAdjective(true);
						//System.out.println("PTR16 Token: " + verb.getStem());
						//System.out.println("PTR16 Token is Confirmed Noun!");
											
					}

					
				}
			}
			//PTR17: Check that an 'Adjective' is a 'Verb'. Modifiers are most commonly followed by adjectives
  			for(int i = 0; i < allAdjectives.size(); i++ ){
				CustomToken adjective = allAdjectives.get(i);
				//GIVEN (Antecedent)
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(adjective.getStem()) && !tokens.get(allAdjectives.get(i).getIndex()).isConfirmedAdjective()) {
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = ".*([^(VB.?)])$";
					String prevPOSs = getPosTagsAsString(tokens, 0, adjective.getIndex());
					//Next Tokens (contains POS tags:)
					//String REGEX_NEXT_POS_TAGS = "^(DT|PDT|IN|NN.?|PRP|JJ.?|RB.?|VBD).*"; 
					String REGEX_NEXT_POS_TAGS = "^(DT|PDT|IN|NN.?|PRP.?|JJ.?|RB.?|VBD).*";
					String nextPOSs =  getPosTagsAsString(tokens, adjective.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) && nextPOSs.matches(REGEX_NEXT_POS_TAGS) ) {
						//THEN (Adjust Token)
						tokens = updatePosTagPrepositionWithVerb(tokens, adjective);
						//System.out.println("PTR17 Token: " + adjective.getStem());
						//System.out.println("PTR17 Token is Verb -Adj!");											
					}

				}
			}
  			
  			//PTR18: Check that a 'Verb' is an 'Adjective'. Adjectives are made from regular and irregular verbs by adding the suffixes -ing and -ed. 
  			for(int i = 0; i < allVerbs.size(); i++ ){
				CustomToken verb = allVerbs.get(i);
				//GIVEN (Antecedent)
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = ".*(DT|PDT|JJ.?|IN)$";
					String prevPOSs = getPosTagsAsString(tokens, 0, verb.getIndex());
					//Next Tokens (contains POS tags:)
					String REGEX_NEXT_POS_TAGS = ""; 
					String nextPOSs =  getPosTagsAsString(tokens, verb.getIndex()+1, tokens.size());
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS)) {
						//THEN (Adjust Token)
						if(!tokens.get(allVerbs.get(i).getIndex() - 1).getPosTag().equals(PosTagEnum.IN.name()) && !verb.getPosTag().equals(PosTagEnum.VBG.name())) {
							tokens.get(verb.getIndex()).setConfirmedAdjective(true);
							tokens = updatePosTagVerbWithAdjective(tokens, verb);
							//System.out.println("PTR18 Token: " + verb.getStem());
							//System.out.println("PTR18 Token is Verb -Adj!");
						}
					}

			}
			
  			//PTR19: Check that a 'Verb' with -ing and -ed is an 'Adjective'. The 'Adjective' is preceded by a Verb (+ Determiner + Noun or Adjective)
  			for(int i = 0; i < allVerbs.size(); i++ ){
				CustomToken verb = allVerbs.get(i);
				//GIVEN (Antecedent)
					//WHEN
					//Previous Tokens (contains POS tags:)
					String REGEX_PREV_POS_TAGS = ".*((VB|VBZ|VBP)(\\s+(PDT|DT))?(\\s+(NN.?|JJ.?|RB.?))*)$";
					String prevPOSs = getPosTagsAsString(tokens, 0, verb.getIndex());
					//Next Tokens (contains POS tags:)
					String REGEX_NEXT_POS_TAGS = "^(NN.?).*"; 
					String nextPOSs =  getPosTagsAsString(tokens, verb.getIndex()+1, tokens.size());
					
					if(prevPOSs.matches(REGEX_PREV_POS_TAGS) && nextPOSs.matches(REGEX_NEXT_POS_TAGS)) {
						//THEN (Adjust Token)
						if((verb.getPosTag().matches("(VBD|VBN)") && tokens.get(allVerbs.get(i).getIndex() - 1).getStem().equals(VERB_TO_HAVE))
								|| (verb.getPosTag().equals(PosTagEnum.VBG.name()) && tokens.get(allVerbs.get(i).getIndex() - 1).getStem().equals(VERB_TO_BE)) ) {
							tokens.get(verb.getIndex()).setConfirmedVerb(true);
							//System.out.println("PTR19 Token is confirmed Verb!");
						} else {
							tokens.get(verb.getIndex()).setConfirmedAdjective(true);
							tokens = updatePosTagVerbWithAdjective(tokens, verb);
							//System.out.println("PTR19 Token: " + verb.getStem());
							//System.out.println("PTR19 Token is Verb -Adj!");
						}
					}

			}
  			
		}
		return tokens;
	}
	

	/**
	* Adjust special USE CASE KEYWORDS: 
	* <br>Check that a ADVERB is a VERB: back/RB to/TO step/VB 1/CD
	* <br>Check that a NOUN is a VERB: return/NN to/TO step/VB 1/CD
	* <br>Check that a VERB is a NOUN: go/VB to/TO step/VB 1/CD
	* @Title: Adjust VERB and NOUN POS TAGS
	* @Goal: Return Tokens from a sentence with adjusted VERBS and NOUNS by applying Rules to adjust the accuracy of POS Tagger

	* @Resource: tokens
	**/
	public static List<CustomToken> adjustUseCaseKeywords(List<CustomToken> tokens){ 
		//Search for structures like NOUN + ["to"] + VERB + CARDINAL NUMBER
		// IF NOUN is NOUN_AND_VERB THEN NOUN is a VERB and VERB is a NOUN
		// Ex.return/NN to/TO step/VB 1/CD

		//Search for structures like ADVERB + ["to"] + VERB + CARDINAL NUMBER
		// IF ADVERB is NOUN_AND_VERB THEN ADVERB is a VERB  and VERB is a NOUN
		// Ex. back/RB to/TO step/VB 1/CD

		//Search for structures like VERB + ["to"] + VERB + CARDINAL NUMBER
		// IF VERB is NOUN_AND_VERB THEN VERB is a NOUN
		// Ex. go/VB to/TO step/VB 1/CD

		//String REGEX_USECASE_RETURN_KEYWORD = "(resume|return|back|go|goto)";
		String REGEX_USECASE_RETURN_KEYWORD = "(goto|go[e]?|back|return|resume|proceed)[s]?";

		if (tokens != null && !tokens.isEmpty()) {
			//Get all VERBS that can be NOUN_AND_VERB followed by CARDINAL_NUMBER
			List<CustomToken> allVerbsThatCanBeNouns = new ArrayList<>();
			for(int i = 0; i < tokens.size(); i++ ){
				String baseForm = getVerbBaseFromThirdPerson(tokens.get(i).getWord());
				if(SpecialVerb.NOUNS_AND_VERBS_HASH.containsKey(baseForm)) {					
					if(i < tokens.size() - 1)
						if ( (  tokens.get(i).getPosTag().equals(PosTagEnum.VB.name()) 
								|| tokens.get(i).getPosTag().equals(PosTagEnum.VBP.name()) 
								|| tokens.get(i).getPosTag().equals(PosTagEnum.VBZ.name())
								) && tokens.get(i+1).getPosTag().equals(PosTagEnum.CD.name())
								){

							//i: position in the analysis tokens array;
							CustomToken tmpVerb = new CustomToken(tokens.get(i).getWord(), tokens.get(i).getPosTag(), tokens.get(i).getStem(), i) ;
							if(tokens.get(i).getWord().length() > 1)//???
								allVerbsThatCanBeNouns.add(tmpVerb);
						}						
				}
			}

			//Check that a VERB is a NOUN
			//Update NOUN/ADVERB that is a VERB
			for(int i = 0; i < allVerbsThatCanBeNouns.size(); i++ ){
				//if before token we have "(return|back|go)" [followed by "to"] THEN current token is a NOUN (VB | VBP -> NN,  VBZ -> NNS) 

				//get previous token "(resume|return|back|go)"
				CustomToken prevToken = null;
				if(tokens.get(allVerbsThatCanBeNouns.get(i).getIndex()-1) != null 
						&& tokens.get(allVerbsThatCanBeNouns.get(i).getIndex()-1).getPosTag().equals(PosTagEnum.TO.name())
						&& tokens.get(allVerbsThatCanBeNouns.get(i).getIndex()-2) != null)
					prevToken = tokens.get(allVerbsThatCanBeNouns.get(i).getIndex()-2);
				else if(tokens.get(allVerbsThatCanBeNouns.get(i).getIndex()-1) != null)
					prevToken = tokens.get(allVerbsThatCanBeNouns.get(i).getIndex()-1);	


				if(prevToken != null ) {

					String baseForm = prevToken.getStem();
					//IF previous token is "(resume|return|back|go|goto)" THEN Update tokens
					if(baseForm.replaceAll(REGEX_USECASE_RETURN_KEYWORD, EMPTY_CHAR).equals(EMPTY_CHAR)) {

						//Update current token "VERB" that is a NOUN
						CustomToken  verbAndNoun = allVerbsThatCanBeNouns.get(i);
						int tokenIndex = verbAndNoun.getIndex();//Index in Tokens returned by NLP tool
						if(tokenIndex <  tokens.size()){

							String posTag = PosTagEnum.NN.name(); //singular
							//if current token ends with 's'
							if (verbAndNoun.getWord().toLowerCase().endsWith("s"))
								posTag = PosTagEnum.NNS.name(); //plural

							//if current token ends with 'ss'
							if (verbAndNoun.getWord().toLowerCase().endsWith("ss")) //ex. address
								posTag = PosTagEnum.NN.name();

							verbAndNoun.setPosTag(posTag);
							tokens.set(tokenIndex, verbAndNoun);
						} 					 

						//Update previous token "NOUN/ADVERB" that is a VERB
						tokenIndex = prevToken.getIndex();//Index in Tokens returned by NLP tool
						if(tokenIndex <  tokens.size()){

							String posTag = PosTagEnum.VBP.name(); //present
							//if current token ends with 's'
							if (prevToken.getWord().toLowerCase().endsWith("s"))
								posTag = PosTagEnum.VBZ.name(); //third form

							//if current token ends with 'ss'
							if (prevToken.getWord().toLowerCase().endsWith("ss")) //ex. address
								posTag = PosTagEnum.VBP.name();

							if (posTag.equals(PosTagEnum.VBP.name()) && tokenIndex == 0) //first token
								posTag = PosTagEnum.VB.name();

							prevToken.setPosTag(posTag);
							tokens.set(tokenIndex, prevToken);
						} 			

					}					
				}
			}
		}

		return tokens;
	}
	


	
	/**
	 * Return base verb from a Present Tense - Third Person verb
	 * https://github.com/takafumir/javascript-lemmatizer/blob/master/js/lemmatizer.js
	 * http://www.grammar.cl/Present/Verbs_Third_Person.htm	
	 * @param word
	 * @return
	 */
	public static String getVerbBaseFromThirdPerson(String word) {
		if(word != null && !word.isEmpty()) {
			word = word.toUpperCase();
			String baseForm = word;
			//Regular verbs end with "s" or "es"
			if (word.endsWith("S")) {
				baseForm = word.substring(0, word.length() - 1);
				if (word.endsWith("CESS") || word.endsWith("SESS")) { //ex. process, access, assess, possess
					baseForm = word;
				}	
			//Irregular Verbs
				//If the base_verb ends in SS, X, CH, SH or the letter O, we add + ES in the third person.
				if (word.endsWith("OES") || word.endsWith("SSES") || word.endsWith("XES") || word.endsWith("CHES") || word.endsWith("SHES")) { //ex. goes -> go, kisses -> kiss, fixes -> fix, Watches -> Watch , Crashes -> Crash
					baseForm = word.substring(0, word.length() - 2);
				}
				//If the verb ends in a Consonant + Y, we remove the Y and + IES in the third person
				if (word.endsWith("IES")) { //ex. Carries ->	Carry, 	Hurries -> Hurry, Studies -> Study, Denies -> Deny
					baseForm = word.substring(0, word.length() - 3) + "Y";
				}
			}
			return baseForm.toLowerCase();	
		}
		return word;
	}
	
	public static boolean isVowel(String character) {
		String regExVowel = "[aAeEiIyYoOuUwW]";
		if(character != null && !character.isEmpty() && character.length() == 1) {
			character = character.replaceAll(regExVowel, "");
			if(character.equals("") || character.length() == 0)
				return true;
		}
		return false;
	}
	
	public static boolean isPlural(String word) {
		word = word.toUpperCase();
  		if(word.length() > 1){
  			word = word.substring(word.length() - 2);
			if (word.endsWith("S")) {
				word = word.substring(0, word.length() - 1);
				if (isVowel(word))
					return true;
			}	 
		} else {
			return false;	
		}
		return false;
	}
	
	/**
	 * Check the list of Custom Tokens contains one element equal to informed Token 
	 * @param list
	 * @param token
	 * @return
	 */
	public static boolean containsToken(List<CustomToken> list, CustomToken token){
		if (token != null){
			if (list != null && !list.isEmpty()) {
				for(int i = 0; i < list.size(); i++){
					//equal
					if 	(list.get(i).getWord().equals(token.getWord())) {
						return true;
					}
					
				}
			}	
		}
		return false;
	}
	/**
	 * Check the list contains a similar element like informed word 
	 * @param list
	 * @param word
	 * @return
	 */
	public static boolean containsSimilarWord(List<String> list, String word){
		
		if (word != null && !word.isEmpty()  ){
			word = word.toUpperCase();
			if (list != null && !list.isEmpty()) {
				for(int i = 0; i < list.size(); i++){
					//equal
					if 	(list.get(i).toUpperCase().equals(word)) {
						return true;
					}
					//or substring
					/*
					if (list.get(i).toUpperCase().contains(word) ) { 
						return true;
					}
					if (word.contains(list.get(i).toUpperCase())) { 
						return true;
					}
					*/
				}
			}	
		}
		return false;
	}
	
	/**
	 * Check the list contains one element equal to informed word 
	 * @param list
	 * @param word
	 * @return
	 */
	public static boolean containsWord(List<String> list, String word){
		
		if (word != null && !word.isEmpty()  ){
			word = word.toUpperCase();
			if (list != null && !list.isEmpty()) {
				for(int i = 0; i < list.size(); i++){
					//equal
					if 	(list.get(i).toUpperCase().equals(word)) {
						return true;
					}
					
				}
			}	
		}
		return false;
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
	
	public static void main(String[] args) {
		//System.out.println(isPlural("exercices"));
		//System.out.println(isPlural("shoe"));
		
		//System.out.println(getVerbBaseFromThirdPerson("gets"));
		
		
		INLPAnalyzer nlpAnalyzer = CoreNLPAnalyzer.getInstance();//singleton
		//Text to analyze
		List<CustomToken> tokens = null;		
		//String text1 = "User selects the type and localization of the output file with report";
		//String text1 ="The Broker System displays the count of the customer";
		//String text1 = "user register or delete transactions";
		
		//PTR2
		//String text1 = "System displays the welcome interface";
		//String text1 = "System checks if a group with the given name has not been already defined and if so, inserts the name of a new group into a database";
		//String text1 = "User types in the numbers of his PIN and presses the Enter button";
		
		//PTR3
		//String text1 = "use case ends";
		//String text1 = "download system finishes";
		//String text1 = "process bids";
		
		//PTR6
		//String text1 = "System sends a registration request to the server";
		//String text1 = "system returns to step 1";
		//String text1 = "user proceeds to register";
		
		//PTR7
		//String text1 = "Post containing";
		
		//PTR8
		//String text1 = "user sends user accounts";
		//String text1 = "User fills all required personal client data forms";
		//String text1 = "System presents a registration data form and asks to enter the registration data";
		
		//String text1 = "Candidate fills the registration data form and submits the registration data form";
		
		//PTR9
		//String text1 = "scenario finishes";
		//String text1 = "the scenario finishes";
		//String text1= "logged user ends";
		//String text1= "The broker system finish";
		
		//PTR4
		//String text1 ="system displays list of possible criteria";
		//String text1 ="system displays set of possible criteria";
		//String text1 ="Administrator chooses the browse Candidate's list option";
		
		//PTR9
		//String text1 = "System displays a tree view of available groups and channels and marks it";
		//String text1 = "system queries the database for news messages, whose expiry date and time have passed";
		
		//PTR13
		//String text1 = "User fills all required personal client data forms";
		//String text1 = "System presents a registration data form and asks to enter the registration data";
		
		//String text1 = "user register or system delete the files";
		
		//PTR14
		//String text1 = "post a group message";
		//String text1 = "show alert message";
		
		//PTR15
		String text1 = "user like it";
				
		tokens = nlpAnalyzer.getTokens(text1);
		//Pos tags
		System.out.println("before: " +getPosTagsAsString(tokens, 0, tokens.size()));
    	adjustPosTags(tokens);
    	System.out.println("after: " + getPosTagsAsString(tokens, 0, tokens.size()));
	}
}
