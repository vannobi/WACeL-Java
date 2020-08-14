package pe.edu.unsa.daisi.lis.cel.util.nlp;

import java.io.Serializable;

/**
 * Alphabetical list of part-of-speech tags used in the Penn Treebank Project:
 * https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
 * @author Edgar
 *
 */
public enum PosTagEnum implements Serializable{
	
	CC("Coordinating conjunction"), //and, but, or
	CD("Cardinal number"),//one, two, 1, 2
	DT("Determiner"),//the, some
	EX("Existential there"),//there
	FW("Foreign word"),//mon dieu
	IN("Preposition or subordinating conjunction"),//of, in, by
	JJ("Adjective"),//big
	JJR("Adjective, comparative"),//bigger
	JJS("Adjective, superlative"),//biggest
	LS("List item marker"),//1, One  --> Stanfor CoreNLP : I = LS
	MD("Modal"),//can, should
	NN("Noun, singular or mass"),//dog
	NNS("Noun, plural"),//dogs
	NNP("Proper noun, singular"),//Edinburg
	NNPS("Proper noun, plural"),//Smiths
	PDT("Predeterminer"),//all, both
	POS("Possessive ending"),//'s
	PRP("Personal pronoun"),//I, you, she
	PRP$("Possessive pronoun"),//my, one's
	RB("Adverb"),//quickly, not
	RBR("Adverb, comparative"),//faster
	RBS("Adverb, superlative"),//fastest
	RP("Particle"),//up, off
	SYM("Symbol"),//+, %, &
	TO("to"),//to
	UH("Interjection"),//oh, oops
	VB("Verb, base form"),//eat
	VBD("Verb, past tense"),//ate
	VBG("Verb, gerund or present participle"),//eating
	VBN("Verb, past participle"),//eaten
	VBP("Verb, non-3rd person singular present"),//eat
	VBZ("Verb, 3rd person singular present"),//eats
	WDT("Wh-determiner"),//which, that
	WP("Wh-pronoun"),//who, what
	WP$("Possessive wh-pronoun"),//whose
	WRB("Wh-adverb");//how, where
	
	String posTag;
	
	private PosTagEnum(String posTag){
		this.posTag = posTag;
	}
	
	public String getPosTag(){
		return posTag;
	}

}
