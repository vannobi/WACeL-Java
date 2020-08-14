package pe.edu.unsa.daisi.lis.cel.util.nlp;

/**
 * It is used to represent a Custom Token returned by a NLP tool: stanford, openNLP, ...
 * It contains information like word, postag, stem (word into its root/base form), and index_position_in_sentence 
 * <br/>
 *  am, are, is --> be 
 *  car, cars, car's, cars' --> car
 * @author Edgar
 *
 */
public class CustomToken {
	private String word;
	private String posTag;
	private String stem;
	private int index;
	private boolean isConfirmedNoun = false;
	private boolean isConfirmedVerb = false;
	private boolean isConfirmedAdjective = false;
	private boolean nounAndVerb = false; //Token is NOUN and VERB, and it is necessary define its correct POST TAG
	private boolean adjectiveAndVerb = false; //Token is ADJECTIVE and VERB, and it is necessary define its correct POST TAG
	private String chunkTag;//The chunk tags contain the name of the chunk type, for example I-NP for noun phrase words, I-VP for verb phrase words, B-PP for preposition phrase words, B-ADVP for adverb phrase words, B-ADJP for adjective phrase words. Most chunk types have three types of chunk tags: B | I | O. "B" represents the start of a chunk, "I" represents the continuation of the chunk and "O" represents no chunk.
	
	
	public CustomToken(String word, String posTag, String stem, int index) {
		super();
		this.word = word;
		this.posTag = posTag;
		this.stem = stem;
		this.index = index;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getPosTag() {
		return posTag;
	}
	public void setPosTag(String posTag) {
		this.posTag = posTag;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getStem() {
		return stem;
	}
	public void setStem(String stem) {
		this.stem = stem;
	}
	public boolean isNounAndVerb() {
		return nounAndVerb;
	}
	public void setNounAndVerb(boolean nounAndVerb) {
		this.nounAndVerb = nounAndVerb;
	}
	
	public boolean isConfirmedNoun() {
		return isConfirmedNoun;
	}
	/**
	 * NOUN confirmed by NOUN PosTag Adjusting phase
	 * @param isConfirmedNoun
	 */
	public void setConfirmedNoun(boolean isConfirmedNoun) {
		this.isConfirmedNoun = isConfirmedNoun;
	}
	public boolean isConfirmedVerb() {
		return isConfirmedVerb;
	}
	public void setConfirmedVerb(boolean isConfirmedVerb) {
		this.isConfirmedVerb = isConfirmedVerb;
	}
	
	
	public boolean isConfirmedAdjective() {
		return isConfirmedAdjective;
	}
	public void setConfirmedAdjective(boolean isConfirmedAdjective) {
		this.isConfirmedAdjective = isConfirmedAdjective;
	}
	public boolean isAdjectiveAndVerb() {
		return adjectiveAndVerb;
	}
	public void setAdjectiveAndVerb(boolean adjectiveAndVerb) {
		this.adjectiveAndVerb = adjectiveAndVerb;
	}
	public String getChunkTag() {
		return chunkTag;
	}
	public void setChunkTag(String chunkTag) {
		this.chunkTag = chunkTag;
	}

	
	
	
}
