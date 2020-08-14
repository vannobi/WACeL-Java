package pe.edu.unsa.daisi.lis.cel.repository;

import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.UserProfile;




public interface IUserProfileDao {

	List<UserProfile> findAll();
	
	UserProfile findByType(String type);
	
	UserProfile findById(Long id);
}
