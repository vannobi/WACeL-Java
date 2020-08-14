package pe.edu.unsa.daisi.lis.cel.repository.mysql.scenario;

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

import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;
import pe.edu.unsa.daisi.lis.cel.repository.AbstractDao;
import pe.edu.unsa.daisi.lis.cel.repository.IScenarioDao;





@Repository("scenarioDao")
public class ScenarioDaoImpl extends AbstractDao<Long, Scenario> implements IScenarioDao {

	static final Logger logger = LoggerFactory.getLogger(ScenarioDaoImpl.class);
	
	public Scenario findById(Long id) {
		Scenario scenario = getByKey(id);
		if(scenario != null){
			//TBD: Use FetchType.EAGER?
			Hibernate.initialize(scenario.getProject());
			
		}
		return scenario;
	}

	public Scenario findByTitle(String title) {
		logger.info("Title : {}", title);
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Scenario> query = builder.createQuery(Scenario.class);
	    Root<Scenario> root = query.from(Scenario.class);
	    Predicate cond = builder.equal(root.get("title"), title);
	    query.where(cond);
	    TypedQuery<Scenario> typedQuery = getEntityManager().createQuery(query);
	    List<Scenario> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	Scenario scenario = (Scenario) list.get(0);
						
			return scenario;
	    }
	    return null;
	}

	@SuppressWarnings("unchecked")
	public List<Scenario> findAllScenarios() {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Scenario> query = builder.createQuery(Scenario.class);
	    Root<Scenario> root = query.from(Scenario.class);
	    query.distinct(true);
	    Predicate cond = builder.equal(root.get("exclusionFlag"), false);
	    query.where(cond);
	    Order order = builder.asc(root.get("title"));
	    query.orderBy(order);
	    List<Scenario> scenarios = (List<Scenario>) getEntityManager().createQuery(query).getResultList();
		
		return scenarios;
	}

	@SuppressWarnings("unchecked")
	public List<Scenario> findAllArchivedScenarios() {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Scenario> query = builder.createQuery(Scenario.class);
	    Root<Scenario> root = query.from(Scenario.class);
	    query.distinct(true);
	    Predicate cond = builder.equal(root.get("exclusionFlag"), true);
	    query.where(cond);
	    Order order = builder.asc(root.get("title"));
	    query.orderBy(order);
	    List<Scenario> scenarios = (List<Scenario>) getEntityManager().createQuery(query).getResultList();
		
		return scenarios;
	}
	
	public List<Scenario> findScenariosByProjectId(Long projectId){
		List<Scenario> scenarios = new ArrayList<Scenario>();
	    final StringBuilder query = new StringBuilder()
				.append(" SELECT distinct scenario ")
				.append(" FROM Scenario scenario \n")
				.append(" WHERE scenario.exclusionFlag = :exclusionFlag")
				.append(" AND scenario.project.id = :projectId");
		
		final TypedQuery<Scenario> typedQuery = this.getEntityManager().createQuery(query.toString(), Scenario.class);

		typedQuery.setParameter("projectId", projectId).setParameter("exclusionFlag", false);

		scenarios = typedQuery.getResultList();
		return scenarios;
	}
	
	public void save(Scenario scenario) {
		persist(scenario);
	}
	
	public void deleteById(Long id) {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Scenario> query = builder.createQuery(Scenario.class);
	    Root<Scenario> root = query.from(Scenario.class);
	    Predicate cond = builder.equal(root.get("id"), id);
	    query.where(cond);
	    TypedQuery<Scenario> typedQuery = getEntityManager().createQuery(query);
	    List<Scenario> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	Scenario scenario = (Scenario) list.get(0);
				
	    	delete(scenario);
	    }
	}

}
