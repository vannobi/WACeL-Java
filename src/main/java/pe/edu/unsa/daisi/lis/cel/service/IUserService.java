package pe.edu.unsa.daisi.lis.cel.service;

import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;



public interface IUserService {
	
	User findById(Long id);
	
	User findByLogin(String login);
	
	void saveUser(User user);
	
	void updateUser(User user);
	
	void deleteUserByLogin(String login);
	
	void deleteUserById(Long id);

	List<User> findAllUsers(); 
	
	List<User> findAllArchivedUsers();
	
	boolean isUserLoginUnique(Long id, String login);

}