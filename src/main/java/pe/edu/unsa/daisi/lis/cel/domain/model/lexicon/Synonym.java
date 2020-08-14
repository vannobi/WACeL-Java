package pe.edu.unsa.daisi.lis.cel.domain.model.lexicon;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.NotEmpty;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;

@Entity
@Table(name="SYNONYM")
public class Synonym implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7483333982803860434L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	@Column(name="NAME",  nullable=false, length = 500)
	private String name;
		
	@Column(name = "INCLUSION_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Calendar inclusionDate;

	@Column(name="EXCLUSION_FLAG")
	private Boolean exclusionFlag;

	//creator
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OWNER_ID")
	private User owner;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="LEXICON_ID")
	private Lexicon lexicon;


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


	public Lexicon getLexicon() {
		return lexicon;
	}


	public void setLexicon(Lexicon lexicon) {
		this.lexicon = lexicon;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exclusionFlag == null) ? 0 : exclusionFlag.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inclusionDate == null) ? 0 : inclusionDate.hashCode());
		result = prime * result + ((lexicon == null) ? 0 : lexicon.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
		Synonym other = (Synonym) obj;
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
		if (lexicon == null) {
			if (other.lexicon != null)
				return false;
		} else if (!lexicon.equals(other.lexicon))
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
		return true;
	}


	
	
}