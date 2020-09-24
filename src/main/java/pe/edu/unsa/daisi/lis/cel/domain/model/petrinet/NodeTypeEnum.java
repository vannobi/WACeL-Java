package pe.edu.unsa.daisi.lis.cel.domain.model.petrinet;

import java.io.Serializable;

/**
 * Lexicon symbol has a Type
 * TBD: Use Internationalization
 * @author Edgar
 *
 */
public enum NodeTypeEnum implements Serializable{
	PLACE("P", "PLACE"),
	PLACE_WITH_TOKEN("PT", "PLACE WITH TOKEN"),
	TRANSITION("T", "TRANSITION FROM EPISODE"),
	TRANSITION_ALTERNATIVE("TA", "TRANSITION FROM ALTERNATIVE");
	
	private String acronym;
	private String description;
	 
	private NodeTypeEnum(String acronym, String description) {
		this.acronym = acronym;
		this.description = description;
	}
	 
	public String getValue() {
		return name();
	}
	 
	public void setValue(String value) {}
	 
	
	
	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public String getDescription() {
		return description;
	}
	 
	public void setDescription(String description) {
		this.description = description;
	}
	
}
