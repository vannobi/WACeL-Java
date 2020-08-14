package pe.edu.unsa.daisi.lis.cel.service;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.unsa.daisi.lis.cel.domain.model.project.Project;
import pe.edu.unsa.daisi.lis.cel.domain.model.project.ProjectUser;
import pe.edu.unsa.daisi.lis.cel.repository.IProjectDao;

@Service("projectService")
@Transactional
public class ProjectServiceImpl implements IProjectService{

	@Autowired
	private IProjectDao dao;

	
	public Project findById(Long id) {
		return dao.findById(id);
	}

	public Project findByName(String name) {
		Project project = dao.findByName(name);
		return project;
	}

	public void saveProject(Project project) {
		project.setInclusionDate(Calendar.getInstance());
		project.setExclusionFlag(false);
		
		dao.save(project);
	}

	/*
	 * Since the method is running with Transaction, No need to call hibernate update explicitly.
	 * Just fetch the entity from db and update it with proper values within transaction.
	 * It will be updated in db once transaction ends. 
	 */
	public void updateProject(Project project) {
		Project entity = dao.findById(project.getId());
		if(entity!=null){
			entity.setName(project.getName());
			entity.setDescription(project.getDescription());
			entity.setLanguage(project.getLanguage());
			entity.setCaseSensitive(project.getCaseSensitive());
			//Collaborators were not updated:
			//FIX: Merge is not updating relationships 
			
			entity.setCollaborators(project.getCollaborators());
			Project managedEntity = dao.update(entity);
			managedEntity.setCollaborators(project.getCollaborators());
			//dao.flush();
			
		}
	}

	
	
	public void deleteProjectById(Long id) {
		//set exclusion flag to true
		Project entity = dao.findById(id);
		if(entity!=null){
			entity.setExclusionFlag(true);
		}
		//dao.delete(entity);
	}

	public List<Project> findAllProjects() {
		return dao.findAllProjects();
	}
	
	public List<Project> findAllArchivedProjects() {
		return dao.findAllArchivedProjects();
	}

	public boolean isProjectNameUnique(Long id, String name) {
		Project project = findByName(name);
		return ( project == null || ((id != null) && (project.getId() == id)));
	}

	public List<Project> findProjectsCreatedByUserId(Long id) {
		return dao.findAssociatedProjectsByUserId(id);
	}
	
	public List<Project> findAssociatedProjectsByUserId(Long id) {
		return dao.findAssociatedProjectsByUserId(id);
	}
	
	public List<Project> findAssociatedProjectsByUserLogin(String userLogin){
		return dao.findAssociatedProjectsByUserLogin(userLogin);
	}
	
}
