package pe.edu.unsa.daisi.lis.cel.repository.mysql.lexicon;

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

import pe.edu.unsa.daisi.lis.cel.domain.model.lexicon.Lexicon;
import pe.edu.unsa.daisi.lis.cel.repository.AbstractDao;
import pe.edu.unsa.daisi.lis.cel.repository.ILexiconDao;





@Repository("lexiconDao")
public class LexiconDaoImpl extends AbstractDao<Long, Lexicon> implements ILexiconDao {

	static final Logger logger = LoggerFactory.getLogger(LexiconDaoImpl.class);
	
	public Lexicon findById(Long id) {
		Lexicon lexicon = getByKey(id);
		if(lexicon!=null){
			//TBD: Use FetchType.EAGER?
			Hibernate.initialize(lexicon.getSynonyms());
		}
		return lexicon;
	}

	public Lexicon findByName(String name) {
		logger.info("Name : {}", name);
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Lexicon> query = builder.createQuery(Lexicon.class);
	    Root<Lexicon> root = query.from(Lexicon.class);
	    Predicate cond = builder.equal(root.get("name"), name);
	    query.where(cond);
	    TypedQuery<Lexicon> typedQuery = getEntityManager().createQuery(query);
	    List<Lexicon> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	Lexicon lexicon = (Lexicon) list.get(0);
				
			if(lexicon != null){
				//TBD: Use FetchType.EAGER?
				Hibernate.initialize(lexicon.getSynonyms());
				
			}
			
			return lexicon;
	    }
	    return null;
	}

	@SuppressWarnings("unchecked")
	public List<Lexicon> findAllLexicons() {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Lexicon> query = builder.createQuery(Lexicon.class);
	    Root<Lexicon> root = query.from(Lexicon.class);
	    query.distinct(true);
	    Predicate cond = builder.equal(root.get("exclusionFlag"), false);
	    query.where(cond);
	    Order order = builder.asc(root.get("name"));
	    query.orderBy(order);
	    List<Lexicon> lexicons = (List<Lexicon>) getEntityManager().createQuery(query).getResultList();
		
		// No need to fetch userProfiles since we are not showing them on list page. Let them lazy load. 
		// Uncomment below lines for eagerly fetching of userProfiles if you want.
		/*
		for(User user : users){
			Hibernate.initialize(user.getUserProfiles());
		}*/
		return lexicons;
	}
	
	@SuppressWarnings("unchecked")
	public List<Lexicon> findAllArchivedLexicons() {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Lexicon> query = builder.createQuery(Lexicon.class);
	    Root<Lexicon> root = query.from(Lexicon.class);
	    query.distinct(true);
	    Predicate cond = builder.equal(root.get("exclusionFlag"), true);
	    query.where(cond);
	    Order order = builder.asc(root.get("name"));
	    query.orderBy(order);
	    List<Lexicon> lexicons = (List<Lexicon>) getEntityManager().createQuery(query).getResultList();
		
		// No need to fetch userProfiles since we are not showing them on list page. Let them lazy load. 
		// Uncomment below lines for eagerly fetching of userProfiles if you want.
		/*
		for(User user : users){
			Hibernate.initialize(user.getUserProfiles());
		}*/
		return lexicons;
	}
	
	public List<Lexicon> findLexiconsByProjectId(Long projectId){
		List<Lexicon> lexicons = new ArrayList<Lexicon>();
	    final StringBuilder query = new StringBuilder()
				.append(" SELECT distinct lexicon ")
				.append(" FROM Lexicon lexicon \n")
				.append(" WHERE lexicon.exclusionFlag = :exclusionFlag")
				.append(" AND lexicon.project.id = :projectId");
		
		final TypedQuery<Lexicon> typedQuery = this.getEntityManager().createQuery(query.toString(), Lexicon.class);

		typedQuery.setParameter("projectId", projectId).setParameter("exclusionFlag", false);

		lexicons = typedQuery.getResultList();
		return lexicons;
	}

	public void save(Lexicon lexicon) {
		persist(lexicon);
	}
	
	public void deleteById(Long id) {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Lexicon> query = builder.createQuery(Lexicon.class);
	    Root<Lexicon> root = query.from(Lexicon.class);
	    Predicate cond = builder.equal(root.get("id"), id);
	    query.where(cond);
	    TypedQuery<Lexicon> typedQuery = getEntityManager().createQuery(query);
	    List<Lexicon> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	Lexicon lexicon = (Lexicon) list.get(0);
				
	    	delete(lexicon);
	    }
	}

}
