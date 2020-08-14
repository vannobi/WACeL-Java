package pe.edu.unsa.daisi.lis.cel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.UserProfile;
import pe.edu.unsa.daisi.lis.cel.repository.IUserProfileDao;




@Service("userProfileService")
@Transactional
public class UserProfileServiceImpl implements IUserProfileService{
	
	@Autowired
	IUserProfileDao dao;
	
	public UserProfile findById(Long id) {
		return dao.findById(id);
	}

	public UserProfile findByType(String type){
		return dao.findByType(type);
	}

	public List<UserProfile> findAll() {
		return dao.findAll();
	}
}
