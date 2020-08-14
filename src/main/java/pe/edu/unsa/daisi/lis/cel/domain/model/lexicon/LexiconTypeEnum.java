package pe.edu.unsa.daisi.lis.cel.domain.model.lexicon;

import java.io.Serializable;

/**
 * Lexicon symbol has a Type
 * TBD: Use Internationalization
 * @author Edgar
 *
 */
public enum LexiconTypeEnum implements Serializable{
	OBJECT("OBJECT"),
	SUBJECT("SUBJECT"),
	VERB("VERB"),
	STATE("STATE");
	
	private String description;
	 
	private LexiconTypeEnum(String description) {
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
	
}
