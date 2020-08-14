package pe.edu.unsa.daisi.lis.cel.repository.mysql.user;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.UserProfile;
import pe.edu.unsa.daisi.lis.cel.repository.AbstractDao;
import pe.edu.unsa.daisi.lis.cel.repository.IUserProfileDao;





@Repository("userProfileDao")
public class UserProfileDaoImpl extends AbstractDao<Long, UserProfile>implements IUserProfileDao{

	public UserProfile findById(Long id) {
		return getByKey(id);
	}

	public UserProfile findByType(String type) {
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<UserProfile> query = builder.createQuery(UserProfile.class);
	    Root<UserProfile> root = query.from(UserProfile.class);
	    Predicate cond = builder.equal(root.get("type"), type);
	    query.where(cond);
	    TypedQuery<UserProfile> typedQuery = getEntityManager().createQuery(query);
	    List<UserProfile> list = typedQuery.getResultList();
	    if (list != null && list.size() > 0)
	    {
	    	UserProfile userProfile = (UserProfile)  list.get(0);
	   	
	    	return userProfile;
	    }
	    return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<UserProfile> findAll(){
		CriteriaBuilder builder = createCriteriaBuilder(); 
		CriteriaQuery<UserProfile> query = builder.createQuery(UserProfile.class);
	    Root<UserProfile> root = query.from(UserProfile.class);
	    query.distinct(true);
	    Order order = builder.asc(root.get("type"));
	    query.orderBy(order);
	    List<UserProfile> userProfiles = (List<UserProfile>) getEntityManager().createQuery(query).getResultList();
				
		return userProfiles;
	}
	
}
