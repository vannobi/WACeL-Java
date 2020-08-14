package pe.edu.unsa.daisi.lis.cel.repository.mysql.security;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.unsa.daisi.lis.cel.domain.model.security.PersistentLogin;
import pe.edu.unsa.daisi.lis.cel.repository.AbstractDao;



@Repository("tokenRepositoryDao")
@Transactional
public class HibernateTokenRepositoryImpl extends AbstractDao<String, PersistentLogin>
		implements PersistentTokenRepository {

	static final Logger logger = LoggerFactory.getLogger(HibernateTokenRepositoryImpl.class);

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		logger.info("Creating Token for user : {}", token.getUsername());
		PersistentLogin persistentLogin = new PersistentLogin();
		persistentLogin.setUsername(token.getUsername());
		persistentLogin.setSeries(token.getSeries());
		persistentLogin.setToken(token.getTokenValue());
		persistentLogin.setLast_used(token.getDate());
		persist(persistentLogin);

	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		logger.info("Fetch Token if any for seriesId : {}", seriesId);
		try {
		
			CriteriaBuilder builder = createCriteriaBuilder(); 
			CriteriaQuery<PersistentLogin> query = builder.createQuery(PersistentLogin.class);
		    Root<PersistentLogin> root = query.from(PersistentLogin.class);
		    Predicate cond = builder.equal(root.get("series"), seriesId);
		    query.where(cond);
		    TypedQuery<PersistentLogin> typedQuery = getEntityManager().createQuery(query);
		    PersistentLogin persistentLogin = (PersistentLogin)  typedQuery.getSingleResult();
		    
			return new PersistentRememberMeToken(persistentLogin.getUsername(), persistentLogin.getSeries(),
					persistentLogin.getToken(), persistentLogin.getLast_used());
		} catch (Exception e) {
			logger.info("Token not found...");
			return null;
		}
	}

	@Override
	public void removeUserTokens(String username) {
		logger.info("Removing Token if any for user : {}", username);
			
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<PersistentLogin> query = builder.createQuery(PersistentLogin.class);
	    Root<PersistentLogin> root = query.from(PersistentLogin.class);
	    Predicate cond = builder.equal(root.get("username"), username);
	    query.where(cond);
	    TypedQuery<PersistentLogin> typedQuery = getEntityManager().createQuery(query);
	    List<PersistentLogin> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	PersistentLogin persistentLogin = (PersistentLogin)  list.get(0);
			if (persistentLogin != null) {
				logger.info("rememberMe was selected");
				delete(persistentLogin);
			}
	    }
	}

	@Override
	public void updateToken(String seriesId, String tokenValue, Date lastUsed) {
		logger.info("Updating Token for seriesId : {}", seriesId);
		PersistentLogin persistentLogin = getByKey(seriesId);
		persistentLogin.setToken(tokenValue);
		persistentLogin.setLast_used(lastUsed);
		merge(persistentLogin);
	}

}
