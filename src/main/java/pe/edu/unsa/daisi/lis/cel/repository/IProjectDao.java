package pe.edu.unsa.daisi.lis.cel.repository;

import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.project.Project;



public interface IProjectDao {

	Project findById(Long id);
	
	Project findByName(String name);
	
	void save(Project project);
	
	Project update(Project project);
	
	void flush();
	
	void delete(Project project);
	
	void deleteById(Long id);
	
	List<Project> findAllProjects();
	
	List<Project> findAllArchivedProjects();
	
	List<Project> findProjectsCreatedByUserId(Long userId);
	
	List<Project> findAssociatedProjectsByUserId(Long userId);
	
	List<Project> findAssociatedProjectsByUserLogin(String userLogin);

}

