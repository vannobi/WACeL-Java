package pe.edu.unsa.daisi.lis.cel.domain.model.project;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;

/**
 * PK of Project_has_Users
 * @author Edgar
 *
 */
@Embeddable
public class ProjectUserPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5944204722748282075L;

	@ManyToOne
	private Project project;
	@ManyToOne
	private User collaborator;
	
	
	public ProjectUserPK() {
		super();
		
	}

	public ProjectUserPK(Project project, User collaborator) {
		this.project = project;
		this.collaborator = collaborator;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public User getCollaborator() {
		return collaborator;
	}

	public void setCollaborator(User collaborator) {
		this.collaborator = collaborator;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collaborator == null) ? 0 : collaborator.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
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
		ProjectUserPK other = (ProjectUserPK) obj;
		if (collaborator == null) {
			if (other.collaborator != null)
				return false;
		} else if (!collaborator.equals(other.collaborator))
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}

	
	
}
