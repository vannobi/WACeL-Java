package pe.edu.unsa.daisi.lis.cel.domain.model.project;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.NotEmpty;

import pe.edu.unsa.daisi.lis.cel.domain.model.lexicon.Lexicon;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;
import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;

@Entity
@Table(name="PROJECT")
public class Project implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2149587683893946378L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	@Column(name="NAME", unique=true, nullable=false, length = 500)
	private String name;

	@NotEmpty
	@Lob
	@Column(name="DESCRIPTION", nullable=false)
	private String description;

	
	@Column(name = "INCLUSION_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Calendar inclusionDate;

	@Column(name="EXCLUSION_FLAG")
	private Boolean exclusionFlag;

	
	@Column(name="LANGUAGE", nullable=false, length=10)
	private String language;
	
	@Column(name="CASE_SENSITIVE")
	private Boolean caseSensitive;

	//creator
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OWNER_ID")
	private User owner;

	//project collaborators
	//ManyToMany relationship, but the relational join table has additional data. 
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "id.project", cascade=CascadeType.ALL)
	private Set<ProjectUser> collaborators = new HashSet<ProjectUser>();;
	
	//project scenarios
	@OneToMany(mappedBy="project")
	private Set<Scenario> scenarios  = new HashSet<Scenario>();;
	
	//project lexicons
	@OneToMany(mappedBy="project")
	private Set<Lexicon> lexicons  = new HashSet<Lexicon>();;

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

	

	public java.util.Calendar getInclusionDate() {
		return inclusionDate;
	}

	public void setInclusionDate(java.util.Calendar inclusionDate) {
		this.inclusionDate = inclusionDate;
	}

	public Boolean getExclusionFlag() {
		return exclusionFlag;
	}

	public void setExclusionFlag(Boolean exclusionFlag) {
		this.exclusionFlag = exclusionFlag;
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

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Set<ProjectUser> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(Set<ProjectUser> collaborators) {
		this.collaborators = collaborators;
	}

	public Set<Scenario> getScenarios() {
		return scenarios;
	}

	public void setScenarios(Set<Scenario> scenarios) {
		this.scenarios = scenarios;
	}

	public Set<Lexicon> getLexicons() {
		return lexicons;
	}

	public void setLexicons(Set<Lexicon> lexicons) {
		this.lexicons = lexicons;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caseSensitive == null) ? 0 : caseSensitive.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((exclusionFlag == null) ? 0 : exclusionFlag.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inclusionDate == null) ? 0 : inclusionDate.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		//result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		if (caseSensitive == null) {
			if (other.caseSensitive != null)
				return false;
		} else if (!caseSensitive.equals(other.caseSensitive))
			return false;
		if (collaborators == null) {
			if (other.collaborators != null)
				return false;
		} else if (!collaborators.equals(other.collaborators))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (exclusionFlag == null) {
			if (other.exclusionFlag != null)
				return false;
		} else if (!exclusionFlag.equals(other.exclusionFlag))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (inclusionDate == null) {
			if (other.inclusionDate != null)
				return false;
		} else if (!inclusionDate.equals(other.inclusionDate))
			return false;
		if (lexicons == null) {
			if (other.lexicons != null)
				return false;
		} else if (!lexicons.equals(other.lexicons))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (scenarios == null) {
			if (other.scenarios != null)
				return false;
		} else if (!scenarios.equals(other.scenarios))
			return false;
		return true;
	}

	

}