package pe.edu.unsa.daisi.lis.cel.service;

import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.UserProfile;




public interface IUserProfileService {

	UserProfile findById(Long id);

	UserProfile findByType(String type);
	
	List<UserProfile> findAll();
	
}
