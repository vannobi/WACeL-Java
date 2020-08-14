package pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pe.edu.unsa.daisi.lis.cel.util.nlp.CustomSentenceNlpInfo;

/**
 * Structured Episode
 * @author Edgar
 *
 */
public class StructuredEpisode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;//step
	private String type; //SIMPLE, CONDITIONAL, OPTIONAL, DO_WHILE, WHILE_DO, FOR_EACH_DO
	private String sentence;
	private List<String> conditions; //In case of CONDITIONAL or LOOP type
	private List<String> preConditions;
	private List<String> postConditions;
	private List<String> constraints;
		
	private String rawEpisode; //not parsed

	//NLP info about sentence: tokens, subjetcs, verbs, objects, ....
	private CustomSentenceNlpInfo sentenceNlp;

	public StructuredEpisode() {

	}

	/**
	 * Initialize Simple Episode
	 * @param id
	 * @param sentence
	 * @return
	 */
	public void createSimpleEpisode(String id, String sentence) {

		this.id = id;
		this.type = "S";
		this.sentence = sentence;
		this.conditions = null;
		this.preConditions  = new ArrayList<String>();
		this.postConditions  = new ArrayList<String>();
		this.constraints  = new ArrayList<String>();
		
	}

	/**
	 * Initialize Conditional (IF <> THEN <>) Episode
	 * @param id
	 * @param sentence
	 * @return
	 */
	public void createConditionalEpisode(String id, String sentence) {
		this.id = id;
		this.type = "C";
		this.sentence = sentence;
		this.conditions  = new ArrayList<String>();
		this.preConditions  = new ArrayList<String>();
		this.postConditions  = new ArrayList<String>();
		this.constraints  = new ArrayList<String>();
	}

	/**
	 * Initialize Optional ([...]) Episode
	 * @param id
	 * @param sentence
	 * @return
	 */
	public void createOptionalEpisode(String id, String sentence) {
		this.id = id;
		this.type = "O";
		this.sentence = sentence;
		this.conditions  = new ArrayList<String>();
		this.preConditions  = new ArrayList<String>();
		this.postConditions  = new ArrayList<String>();
		this.constraints  = new ArrayList<String>();
	}

	/**
	 * Initialize Iterative (DO <> WHILE <> or REPEAT <> UNTIL <>) Episode
	 * @param id
	 * @param sentence
	 * @return
	 */
	public void createIterativeDoWhileEpisode(String id, String sentence) {
		this.id = id;
		this.type = "DW";
		this.sentence = sentence;
		this.conditions  = new ArrayList<String>();
		this.preConditions  = new ArrayList<String>();
		this.postConditions  = new ArrayList<String>();
		this.constraints  = new ArrayList<String>();
	}
	
	/**
	 * Initialize Iterative (WHILE <> DO <> or WHILE <> REPEAT <>) Episode
	 * @param id
	 * @param sentence
	 * @return
	 */
	public void createIterativeWhileDoEpisode(String id, String sentence) {
		this.id = id;
		this.type = "WD";
		this.sentence = sentence;
		this.conditions  = new ArrayList<String>();
		this.preConditions  = new ArrayList<String>();
		this.postConditions  = new ArrayList<String>();
		this.constraints  = new ArrayList<String>();
	}

	/**
	 * Initialize Iterative (FOR EACH <> DO <> or FOO EACH <> REPEAT <>) Episode
	 * @param id
	 * @param sentence
	 * @return
	 */
	public void createIterativeForEachDoEpisode(String id, String sentence) {
		this.id = id;
		this.type = "FED";
		this.sentence = sentence;
		this.conditions  = new ArrayList<String>();
		this.preConditions  = new ArrayList<String>();
		this.postConditions  = new ArrayList<String>();
		this.constraints  = new ArrayList<String>();
	}
	
	public Boolean isSimple() {
		if (this.type.equals("S"))
			return true;
		return false;
	}

	public Boolean isConditional() {
		if (this.type.equals("C"))
			return true;
		return false;
	}

	public Boolean isOptional() {
		if (this.type.equals("O"))
			return true;
		return false;
	}

	public Boolean isIterativeDoWhile() {
		if (this.type.equals("DW"))
			return true;
		return false;
	}

	public Boolean isIterativeWhileDo() {
		if (this.type.equals("WD"))
			return true;
		return false;
	}
	
	public Boolean isIterativeForEachDo() {
		if (this.type.equals("FED"))
			return true;
		return false;
	}
	
	public void addCondition(String Condition) {
		if (!this.type.equals("S"))
			getConditions().add(Condition);
	}

	public void addPreCondition(String preCondition) {
		getPreConditions().add(preCondition);
	}

	public void addPostCondition(String postCondition) {
		getPostConditions().add(postCondition);
	}

	public void addConstraint(String constraint) {
		getConstraints().add(constraint);
	}


	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public List<String> getConditions() {
		return conditions;
	}
	public void setConditions(List<String> conditions) {
		this.conditions = conditions;
	}
	public List<String> getPreConditions() {
		return preConditions;
	}
	public void setPreConditions(List<String> preConditions) {
		this.preConditions = preConditions;
	}
	public List<String> getPostConditions() {
		return postConditions;
	}
	public void setPostConditions(List<String> postConditions) {
		this.postConditions = postConditions;
	}
	public List<String> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<String> constraints) {
		this.constraints = constraints;
	}

	public String getRawEpisode() {
		return rawEpisode;
	}

	public void setRawEpisode(String rawEpisode) {
		this.rawEpisode = rawEpisode;
	}

	public CustomSentenceNlpInfo getSentenceNlp() {
		return sentenceNlp;
	}

	public void setSentenceNlp(CustomSentenceNlpInfo sentenceNlp) {
		this.sentenceNlp = sentenceNlp;
	}

	
}