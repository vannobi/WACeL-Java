package pe.edu.unsa.daisi.lis.cel.repository;

import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;



public interface IUserDao {

	User findById(Long id);
	
	User findByLogin(String login);
	
	void save(User user);
	
	void deleteByLogin(String login);
	
	void deleteById(Long id);
	
	List<User> findAllUsers();
	
	List<User> findAllArchivedUsers();

}

