package pe.edu.unsa.daisi.lis.cel.service;

import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.project.Project;



public interface IProjectService {
	
	Project findById(Long id);
	
	Project findByName(String name);
	
	void saveProject(Project project);
	
	void updateProject(Project project);
	
	
	void deleteProjectById(Long id);

	List<Project> findAllProjects();
	
	List<Project> findAllArchivedProjects(); 
	
	boolean isProjectNameUnique(Long id, String name);
	
	List<Project> findProjectsCreatedByUserId(Long userId);
	
	List<Project> findAssociatedProjectsByUserId(Long userId);
	
	List<Project> findAssociatedProjectsByUserLogin(String userLogin);

}