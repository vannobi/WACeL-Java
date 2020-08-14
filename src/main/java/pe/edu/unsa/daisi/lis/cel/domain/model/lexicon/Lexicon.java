package pe.edu.unsa.daisi.lis.cel.domain.model.lexicon;

import java.io.Serializable;
import java.util.Set;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

import pe.edu.unsa.daisi.lis.cel.domain.model.project.Project;
import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;

@Entity
@Table(name="LEXICON")
public class Lexicon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8811403186229289362L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	@Column(name="NAME", nullable=false, length = 500)
	private String name;

	@NotEmpty
	@Lob
	@Column(name="NOTION", nullable=false)
	private String notion;

	@NotEmpty
	@Lob
	@Column(name="IMPACT", nullable=false)
	private String impact;
	
	@NotEmpty
	//@Enumerated(EnumType.STRING)
    //private LexiconType lexiconType;
	@Column(name="TYPE", nullable=false, length=7)
	private String lexiconType;
		
	@Column(name = "INCLUSION_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Calendar inclusionDate;

	@Column(name="EXCLUSION_FLAG")
	private Boolean exclusionFlag;

	//creator
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OWNER_ID")
	private User owner;
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PROJECT_ID")
	private Project project;
	
	@JsonIgnore
	@OneToMany(mappedBy="lexicon", fetch=FetchType.LAZY)
	private Set<Synonym> synonyms;

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

	public String getNotion() {
		return notion;
	}

	public void setNotion(String notion) {
		this.notion = notion;
	}

	public String getImpact() {
		return impact;
	}

	public void setImpact(String impact) {
		this.impact = impact;
	}

	public String getLexiconType() {
		return lexiconType;
	}

	public void setLexiconType(String lexiconType) {
		this.lexiconType = lexiconType;
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

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Set<Synonym> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(Set<Synonym> synomyms) {
		this.synonyms = synomyms;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exclusionFlag == null) ? 0 : exclusionFlag.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((impact == null) ? 0 : impact.hashCode());
		result = prime * result + ((inclusionDate == null) ? 0 : inclusionDate.hashCode());
		result = prime * result + ((lexiconType == null) ? 0 : lexiconType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notion == null) ? 0 : notion.hashCode());
		//result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		//result = prime * result + ((project == null) ? 0 : project.hashCode());
		//result = prime * result + ((synonyms == null) ? 0 : synonyms.hashCode());
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
		Lexicon other = (Lexicon) obj;
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
		if (impact == null) {
			if (other.impact != null)
				return false;
		} else if (!impact.equals(other.impact))
			return false;
		if (inclusionDate == null) {
			if (other.inclusionDate != null)
				return false;
		} else if (!inclusionDate.equals(other.inclusionDate))
			return false;
		if (lexiconType != other.lexiconType)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (notion == null) {
			if (other.notion != null)
				return false;
		} else if (!notion.equals(other.notion))
			return false;
		/*
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
			
		if (synonyms == null) {
			if (other.synonyms != null)
				return false;
		} else if (!synonyms.equals(other.synonyms))
			return false;
			*/
		return true;
	}


	
	
}