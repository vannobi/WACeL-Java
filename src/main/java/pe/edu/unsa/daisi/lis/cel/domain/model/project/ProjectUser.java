package pe.edu.unsa.daisi.lis.cel.domain.model.project;

import java.io.Serializable;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;

/**
 * A Project has many Users: many to many relationship<br/>
 * A User can be lead of many projects<br/>
 * A User can be associated to may projects<br/>
 * A project has a lead<br/>
 * @author Edgar
 *
 */
@Entity
@Table(name="PROJECT_USER")
@AssociationOverrides({
	@AssociationOverride(name = "id.project", 
		joinColumns = @JoinColumn(name = "PROJECT_ID")),
	@AssociationOverride(name = "id.collaborator", 
		joinColumns = @JoinColumn(name = "USER_ID")) })
public class ProjectUser implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1907100267865488589L;


	@EmbeddedId
    private ProjectUserPK id = new ProjectUserPK();
     
	@Column(name="IS_PROJECT_LEAD")
	private boolean isProjectLead;

	//association authorized by
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="AUTHORIZER_ID")
	private User authorizer;

	@Column(name = "INCLUSION_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Calendar inclusionDate;

	@Column(name="EXCLUSION_FLAG")
	private Boolean exclusionFlag;

	public ProjectUser() {
		super();
		this.id = new ProjectUserPK();
	}
	
	public ProjectUser(ProjectUserPK id) {
		super();
		this.id = id;
	}
	
	/**
	 * Create Project_User relationship 
	 * @param project
	 * @param collaborator
	 */
	public ProjectUser(Project project, User collaborator) {
		super();
		// create primary key
		this.id = new ProjectUserPK(project, collaborator);
		// initialize attributes
		
		// update relationships to assure referential integrity

	}
	
	@Transient
	public Project getProject() {
		return getId().getProject();
	}

	public void setProject(Project project) {
		getId().setProject(project);
	}

	@Transient
	public User getCollaborator() {
		return getId().getCollaborator();
	}

	public void setCollaborator(User collaborator) {
		getId().setCollaborator(collaborator);
	}

	public ProjectUserPK getId() {
		return id;
	}

	public void setId(ProjectUserPK id) {
		this.id = id;
	}

	public boolean isProjectLead() {
		return isProjectLead;
	}

	public void setProjectLead(boolean isProjectLead) {
		this.isProjectLead = isProjectLead;
	}

	public User getAuthorizer() {
		return authorizer;
	}

	public void setAuthorizer(User authorizer) {
		this.authorizer = authorizer;
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
	
	
}
