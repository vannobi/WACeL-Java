package pe.edu.unsa.daisi.lis.cel.repository.mysql.lexicon;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import pe.edu.unsa.daisi.lis.cel.domain.model.lexicon.Synonym;
import pe.edu.unsa.daisi.lis.cel.repository.AbstractDao;
import pe.edu.unsa.daisi.lis.cel.repository.ISynonymDao;





@Repository("synonymDao")
public class SynonymDaoImpl extends AbstractDao<Long, Synonym> implements ISynonymDao {

	static final Logger logger = LoggerFactory.getLogger(SynonymDaoImpl.class);
	
	public Synonym findById(Long id) {
		Synonym synonym = getByKey(id);
		
		return synonym;
	}

	public Synonym findByName(String name) {
		logger.info("Name : {}", name);
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Synonym> query = builder.createQuery(Synonym.class);
	    Root<Synonym> root = query.from(Synonym.class);
	    Predicate cond = builder.equal(root.get("name"), name);
	    query.where(cond);
	    TypedQuery<Synonym> typedQuery = getEntityManager().createQuery(query);
	    List<Synonym> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	Synonym synonym = (Synonym) list.get(0);
			
			return synonym;
	    }
	    return null;
	}

	@SuppressWarnings("unchecked")
	public List<Synonym> findAllSynonyms() {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Synonym> query = builder.createQuery(Synonym.class);
	    Root<Synonym> root = query.from(Synonym.class);
	    query.distinct(true);
	    Predicate cond = builder.equal(root.get("exclusionFlag"), false);
	    query.where(cond);
	    Order order = builder.asc(root.get("name"));
	    query.orderBy(order);
	    List<Synonym> synonyms = (List<Synonym>) getEntityManager().createQuery(query).getResultList();
		
		return synonyms;
	}

	@SuppressWarnings("unchecked")
	public List<Synonym> findAllArchivedSynonyms() {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Synonym> query = builder.createQuery(Synonym.class);
	    Root<Synonym> root = query.from(Synonym.class);
	    query.distinct(true);
	    Predicate cond = builder.equal(root.get("exclusionFlag"), true);
	    query.where(cond);
	    Order order = builder.asc(root.get("name"));
	    query.orderBy(order);
	    List<Synonym> synonyms = (List<Synonym>) getEntityManager().createQuery(query).getResultList();
		
		return synonyms;
	}
	public void save(Synonym synonym) {
		persist(synonym);
	}
	
	public void deleteById(Long id) {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<Synonym> query = builder.createQuery(Synonym.class);
	    Root<Synonym> root = query.from(Synonym.class);
	    Predicate cond = builder.equal(root.get("id"), id);
	    query.where(cond);
	    TypedQuery<Synonym> typedQuery = getEntityManager().createQuery(query);
	    List<Synonym> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	Synonym synonym = (Synonym) list.get(0);
				
	    	delete(synonym);
	    }
	}

}
