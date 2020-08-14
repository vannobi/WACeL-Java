package pe.edu.unsa.daisi.lis.cel.repository.mysql.user;

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

import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;
import pe.edu.unsa.daisi.lis.cel.repository.AbstractDao;
import pe.edu.unsa.daisi.lis.cel.repository.IUserDao;





@Repository("userDao")
public class UserDaoImpl extends AbstractDao<Long, User> implements IUserDao {

	static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);
	
	public User findById(Long id) {
		User user = getByKey(id);
		if(user!=null){
			//TBD: Use FetchType.EAGER?
			Hibernate.initialize(user.getUserProfiles());
		}
		return user;
	}

	public User findByLogin(String login) {
		logger.info("Login : {}", login);
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<User> query = builder.createQuery(User.class);
	    Root<User> root = query.from(User.class);
	    Predicate cond = builder.equal(root.get("login"), login);
	    query.where(cond);
	    TypedQuery<User> typedQuery = getEntityManager().createQuery(query);
	    List<User> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	User user = (User) list.get(0);
				
			if(user!=null){
				Hibernate.initialize(user.getUserProfiles());
			}
			
		return user;
	    }
	    return null;
	}

	@SuppressWarnings("unchecked")
	public List<User> findAllUsers() {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<User> query = builder.createQuery(User.class);
	    Root<User> root = query.from(User.class);
	    query.distinct(true);
	    Predicate cond = builder.equal(root.get("exclusionFlag"), false);
	    query.where(cond);
	    Order order = builder.asc(root.get("firstName"));
	    query.orderBy(order);
	    List<User> users = (List<User>) getEntityManager().createQuery(query).getResultList();
		
		// No need to fetch userProfiles since we are not showing them on list page. Let them lazy load. 
		// Uncomment below lines for eagerly fetching of userProfiles if you want.
		/*
		for(User user : users){
			Hibernate.initialize(user.getUserProfiles());
		}*/
		return users;
	}
	
	@SuppressWarnings("unchecked")
	public List<User> findAllArchivedUsers() {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<User> query = builder.createQuery(User.class);
	    Root<User> root = query.from(User.class);
	    query.distinct(true);
	    Predicate cond = builder.equal(root.get("exclusionFlag"), true);
	    query.where(cond);
	    Order order = builder.asc(root.get("firstName"));
	    query.orderBy(order);
	    List<User> users = (List<User>) getEntityManager().createQuery(query).getResultList();
		
		// No need to fetch userProfiles since we are not showing them on list page. Let them lazy load. 
		// Uncomment below lines for eagerly fetching of userProfiles if you want.
		/*
		for(User user : users){
			Hibernate.initialize(user.getUserProfiles());
		}*/
		return users;
	}

	public void save(User user) {
		persist(user);
	}

	public void deleteByLogin(String login) {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<User> query = builder.createQuery(User.class);
	    Root<User> root = query.from(User.class);
	    Predicate cond = builder.equal(root.get("login"), login);
	    query.where(cond);
	    TypedQuery<User> typedQuery = getEntityManager().createQuery(query);
	    List<User> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	User user = (User) list.get(0);
				
	    	delete(user);
	    }
	}
	
	public void deleteById(Long id) {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<User> query = builder.createQuery(User.class);
	    Root<User> root = query.from(User.class);
	    Predicate cond = builder.equal(root.get("id"), id);
	    query.where(cond);
	    TypedQuery<User> typedQuery = getEntityManager().createQuery(query);
	    List<User> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	User user = (User) list.get(0);
				
	    	delete(user);
	    }
	}

}
