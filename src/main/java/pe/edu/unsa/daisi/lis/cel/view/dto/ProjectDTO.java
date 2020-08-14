package pe.edu.unsa.daisi.lis.cel.view.dto;

import java.io.Serializable;
import java.util.Set;

import org.hibernate.validator.constraints.NotEmpty;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;

/**
 * Class to deal with Project and Collaborators (<ProjectUser>) in HTML Forms (Registration or Update)
 * @author Edgar
 *
 */
public class ProjectDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8253653911819168248L;

	private Long id;
	@NotEmpty
	private String name;
	@NotEmpty
	private String description;
	
	private String language;
	
	private Boolean caseSensitive;

	private Set<User> collaborators; //ProjectUser is hard to process in forms
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Boolean getCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(Boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public Set<User> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(Set<User> collaborators) {
		this.collaborators = collaborators;
	}
		
}