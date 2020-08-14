package pe.edu.unsa.daisi.lis.cel.service;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;
import pe.edu.unsa.daisi.lis.cel.repository.IUserDao;




@Service("userService")
@Transactional
public class UserServiceImpl implements IUserService{

	@Autowired
	private IUserDao dao;

	@Autowired
    private PasswordEncoder passwordEncoder;
	
	public User findById(Long id) {
		return dao.findById(id);
	}

	public User findByLogin(String login) {
		User user = dao.findByLogin(login);
		return user;
	}

	public void saveUser(User user) {
		user.setInclusionDate(Calendar.getInstance());
		user.setExclusionFlag(false);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		dao.save(user);
	}

	/*
	 * Since the method is running with Transaction, No need to call hibernate update explicitly.
	 * Just fetch the entity from db and update it with proper values within transaction.
	 * It will be updated in db once transaction ends. 
	 */
	public void updateUser(User user) {
		User entity = dao.findById(user.getId());
		if(entity!=null){
			entity.setLogin(user.getLogin());
			if(!user.getPassword().equals(entity.getPassword())){
				entity.setPassword(passwordEncoder.encode(user.getPassword()));
			}
			entity.setFirstName(user.getFirstName());
			entity.setLastName(user.getLastName());
			entity.setEmail(user.getEmail());
			entity.setUserProfiles(user.getUserProfiles());
		}
	}

	public void deleteUserById(Long id) {
		//set exclusion flag to true
		User entity = dao.findById(id);
		if(entity!=null){
			entity.setExclusionFlag(true);
		}
		//dao.deleteById(id);
	}
	
	public void deleteUserByLogin(String login) {
		//set exclusion flag to true
		User entity = dao.findByLogin(login);
		if(entity!=null){
			entity.setExclusionFlag(true);
		}
		//dao.deleteByLogin(login);
	}
	
	

	public List<User> findAllUsers() {
		return dao.findAllUsers();
	}
	
	public List<User> findAllArchivedUsers() {
		return dao.findAllArchivedUsers();
	}


	public boolean isUserLoginUnique(Long id, String login) {
		User user = findByLogin(login);
		return ( user == null || ((id != null) && (user.getId() == id)));
	}
	
}
