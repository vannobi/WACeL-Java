package pe.edu.unsa.daisi.lis.cel.domain.model.scenario;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pe.edu.unsa.daisi.lis.cel.domain.model.project.Project;
import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;

@Entity
@Table(name="SCENARIO")
public class Scenario implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4703394162935030207L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	@Column(name="TITLE", nullable=false, length = 500)
	private String title;

	@NotEmpty
	@Lob
	@Column(name="GOAL", nullable=false)
	private String goal;

	@NotEmpty
	@Lob
	@Column(name="CONTEXT", nullable=false)
	private String context;
	
	@NotEmpty
	@Lob
	@Column(name="ACTORS", nullable=false)
	private String actors; //names separated by ","
	
	@NotEmpty
	@Lob
	@Column(name="RESORCES", nullable=false)
	private String resources; //names separated by ","
	
	@NotEmpty
	@Lob
	@Column(name="EPISODES", nullable=false)
	private String episodes; //Steps separated by "\n", and straing with an id ("1. ...")
	
	@Lob
	@Column(name="ALTERNATIVE", nullable=false)
	private String alternative; //Alternate/Exception Steps separated by "\n", and starting with an <Step><Ref> ("1.1 ...")
	
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


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getGoal() {
		return goal;
	}


	public void setGoal(String goal) {
		this.goal = goal;
	}


	public String getContext() {
		return context;
	}


	public void setContext(String context) {
		this.context = context;
	}


	public String getActors() {
		return actors;
	}


	public void setActors(String actors) {
		this.actors = actors;
	}


	public String getResources() {
		return resources;
	}


	public void setResources(String resources) {
		this.resources = resources;
	}


	public String getEpisodes() {
		return episodes;
	}


	public void setEpisodes(String episodes) {
		this.episodes = episodes;
	}


	public String getAlternative() {
		return alternative;
	}


	public void setAlternative(String alternative) {
		this.alternative = alternative;
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actors == null) ? 0 : actors.hashCode());
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((episodes == null) ? 0 : episodes.hashCode());
		result = prime * result + ((alternative == null) ? 0 : alternative.hashCode());
		result = prime * result + ((exclusionFlag == null) ? 0 : exclusionFlag.hashCode());
		result = prime * result + ((goal == null) ? 0 : goal.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inclusionDate == null) ? 0 : inclusionDate.hashCode());
		//result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		//result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((resources == null) ? 0 : resources.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Scenario other = (Scenario) obj;
		if (actors == null) {
			if (other.actors != null)
				return false;
		} else if (!actors.equals(other.actors))
			return false;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		if (episodes == null) {
			if (other.episodes != null)
				return false;
		} else if (!episodes.equals(other.episodes))
			return false;
		if (alternative == null) {
			if (other.alternative != null)
				return false;
		} else if (!alternative.equals(other.alternative))
			return false;
		if (exclusionFlag == null) {
			if (other.exclusionFlag != null)
				return false;
		} else if (!exclusionFlag.equals(other.exclusionFlag))
			return false;
		if (goal == null) {
			if (other.goal != null)
				return false;
		} else if (!goal.equals(other.goal))
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
			*/
		if (resources == null) {
			if (other.resources != null)
				return false;
		} else if (!resources.equals(other.resources))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	

	
}