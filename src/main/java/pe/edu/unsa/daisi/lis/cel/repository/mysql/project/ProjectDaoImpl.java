package pe.edu.unsa.daisi.lis.cel.repository.mysql.project;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import pe.edu.unsa.daisi.lis.cel.domain.model.project.Project;
import pe.edu.unsa.daisi.lis.cel.repository.AbstractDao;
import pe.edu.unsa.daisi.lis.cel.repository.IProjectDao;





@Repository("projectDao")
public class ProjectDaoImpl extends AbstractDao<Long, Project> implements IProjectDao {

	static final Logger logger = LoggerFactory.getLogger(ProjectDaoImpl.class);
	
	public Project findById(Long id) {
		Project project = getByKey(id);
		if(project!=null){
			//TBD: Use FetchType.EAGER?
			Hibernate.initialize(project.getScenarios());
			Hibernate.initialize(project.getLexicons());
			Hibernate.initialize(project.getCollaborators());
		}
		return project;
	}

	public Project findByName(String name) {
		logger.info("Name : {}", name);
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Project> query = builder.createQuery(Project.class);
	    Root<Project> root = query.from(Project.class);
	    Predicate cond = builder.equal(root.get("name"), name);
	    query.where(cond);
	    TypedQuery<Project> typedQuery = getEntityManager().createQuery(query);
	    List<Project> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	Project project = (Project) list.get(0);
						
			return project;
	    }
	    return null;
	}

	@SuppressWarnings("unchecked")
	public List<Project> findAllProjects() {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Project> query = builder.createQuery(Project.class);
	    Root<Project> root = query.from(Project.class);
	    query.distinct(true);
	    Predicate cond = builder.equal(root.get("exclusionFlag"), false);
	    query.where(cond);
	    Order order = builder.asc(root.get("name"));
	    query.orderBy(order);
	    List<Project> projects = (List<Project>) getEntityManager().createQuery(query).getResultList();
		
		// No need to fetch userProfiles since we are not showing them on list page. Let them lazy load. 
		// Uncomment below lines for eagerly fetching of userProfiles if you want.
		/*
		for(User user : users){
			Hibernate.initialize(user.getUserProfiles());
		}*/
		return projects;
	}
	
	@SuppressWarnings("unchecked")
	public List<Project> findAllArchivedProjects() {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Project> query = builder.createQuery(Project.class);
	    Root<Project> root = query.from(Project.class);
	    query.distinct(true);
	    Predicate cond = builder.equal(root.get("exclusionFlag"), true);
	    query.where(cond);
	    Order order = builder.asc(root.get("name"));
	    query.orderBy(order);
	    List<Project> projects = (List<Project>) getEntityManager().createQuery(query).getResultList();
		
		// No need to fetch userProfiles since we are not showing them on list page. Let them lazy load. 
		// Uncomment below lines for eagerly fetching of userProfiles if you want.
		/*
		for(User user : users){
			Hibernate.initialize(user.getUserProfiles());
		}*/
		return projects;
	}

	public void save(Project project) {
		persist(project);
	}
	
	public Project update(Project project) {
		Project managedProject = merge(project);
		return managedProject;
	}
	
	public void deleteById(Long id) {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Project> query = builder.createQuery(Project.class);
	    Root<Project> root = query.from(Project.class);
	    Predicate cond = builder.equal(root.get("id"), id);
	    query.where(cond);
	    TypedQuery<Project> typedQuery = getEntityManager().createQuery(query);
	    List<Project> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	Project project = (Project) list.get(0);
				
	    	delete(project);
	    }
	}

	public List<Project> findProjectsCreatedByUserId(Long userId) {
		List<Project> projects = new ArrayList<Project>();
	    final StringBuilder query = new StringBuilder()
				.append(" SELECT distinct project ")
				.append(" FROM Project project \n")
				.append(" WHERE project.exclusionFlag = :exclusionFlag")
				.append(" AND project.owner.id = :userId");
			

		final TypedQuery<Project> typedQuery = this.getEntityManager().createQuery(query.toString(), Project.class);

		typedQuery.setParameter("userId", userId).setParameter("exclusionFlag", false);

		projects = typedQuery.getResultList();
		return projects;
	}
	
	public List<Project> findAssociatedProjectsByUserId(Long userId) {
		List<Project> projects = new ArrayList<Project>();
	    final StringBuilder query = new StringBuilder()
				.append(" SELECT distinct project ")
				.append(" FROM Project project \n")
				.append(" 	INNER JOIN ProjectUser projectUser ON (project.id = projectUser.id.project.id AND projectUser.id.collaborator.id = :userId )")
				.append(" WHERE project.exclusionFlag = :exclusionFlag");
			

		final TypedQuery<Project> typedQuery = this.getEntityManager().createQuery(query.toString(), Project.class);

		typedQuery.setParameter("userId", userId).setParameter("exclusionFlag", false);

		projects = typedQuery.getResultList();
		return projects;
	}

	//TBD: FIX-> userLogin not exist in PROJECT_USER
	public List<Project> findAssociatedProjectsByUserLogin(String userLogin) {
		
		List<Project> projects = new ArrayList<Project>();
	    final StringBuilder query = new StringBuilder()
				.append(" SELECT distinct project ")
				.append(" FROM Project project \n")
				.append(" 	INNER JOIN ProjectUser projectUser ON (project.id = projectUser.id.project.id AND projectUser.id.collaborator.id = :userLogin )")
				.append(" WHERE project.exclusionFlag = :exclusionFlag");
			

		final TypedQuery<Project> typedQuery = this.getEntityManager().createQuery(query.toString(), Project.class);

		typedQuery.setParameter("userLogin", userLogin).setParameter("exclusionFlag", false);

		projects = typedQuery.getResultList();
		return projects;
	}
}
