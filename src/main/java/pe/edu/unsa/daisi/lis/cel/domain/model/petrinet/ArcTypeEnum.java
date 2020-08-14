package pe.edu.unsa.daisi.lis.cel.domain.model.petrinet;

import java.io.Serializable;

/**
 * Lexicon symbol has a Type
 * TBD: Use Internationalization
 * @author Edgar
 *
 */
public enum ArcTypeEnum implements Serializable{
	ARC("A", "ARC");
	
	private String acronym;
	 
	private String description;
	
	private ArcTypeEnum(String acronym, String description ) {
		this.acronym = acronym;
		this.description = description;		
	}
	 
	public String getValue() {
		return name();
	}
	 
	public void setValue(String value) {}
	 
	public String getDescription() {
		return description;
	}
	 
	public void setDescription(String description) {
		this.description = description;
	}

	public String getAcronym() {
		return acronym;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}
	
	
	
}
