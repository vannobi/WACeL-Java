package pe.edu.unsa.daisi.lis.cel.domain.model.project;

import java.io.Serializable;

/**
 * Project has a language
 * TBD: Use Internationalization
 * @author Edgar
 *
 */
public enum LanguageEnum implements Serializable{
	ENGLISH("ENGLISH"),
	SPANISH("SPANISH"),
	PORTUGUESE("PORTUGUESE"),
	OTHER("OTHER");
	
	private String description;
	 
	private LanguageEnum(String description) {
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
