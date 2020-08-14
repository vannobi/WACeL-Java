package pe.edu.unsa.daisi.lis.cel.view;



import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;
import pe.edu.unsa.daisi.lis.cel.domain.model.user.UserProfile;
import pe.edu.unsa.daisi.lis.cel.service.IUserProfileService;
import pe.edu.unsa.daisi.lis.cel.service.IUserService;
import pe.edu.unsa.daisi.lis.cel.util.CustomUserLocal;
import pe.edu.unsa.daisi.lis.cel.util.exception.ViewException;

/**
 * User Controller 
 */
@Controller
public class UserController  extends GenericController{

	@Autowired
	IUserService userService;
	
	@Autowired
	IUserProfileService userProfileService;
	
	@Autowired
	MessageSource messageSource;

	/**
	 * This method will list all existing users.
	 */
	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public String listUsers(ModelMap model, @RequestParam(required=false) String success, @RequestParam(required=false) String error) {
		//Get Messages from user operations
		if(success != null)
			model.addAttribute("success", success);
		if(error != null)
			model.addAttribute("error", error);
		List<User> users = userService.findAllUsers();
		model.addAttribute("users", users);
		return "/user/list";
	}
	
	/**
	 * This method shows user details.
	 */
	@RequestMapping(value = "/user/show-user-{id}", method = RequestMethod.GET)
	public String showUser(@PathVariable("id") Long id, ModelMap model) {
		User user = userService.findById(id);
		model.addAttribute("user", user);

		return "user/show";

	}


	/**
	 * This method will provide the medium to add a new user.
	 */
	@RequestMapping(value = { "/user/newuser" }, method = RequestMethod.GET)
	public String newUser(ModelMap model) {
		User user = new User();
		model.addAttribute("user", user);
		model.addAttribute("edit", false);
		return "/user/registration";
	}

	/**
	 * This method will be called on form submission, handling POST request for
	 * saving user in database. It also validates the user input
	 * Same URL that GET method: For Save or Update
	 * @throws IOException 
	 * @throws ViewException 
	 */
	@RequestMapping(value = { "/user/newuser" }, method = RequestMethod.POST)
	public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult result,
			ModelMap model) {
		
		if (result.hasErrors()) {
			return "/user/registration";
		}

		/*
		 * Preferred way to achieve uniqueness of field [login] should be implementing custom @Unique annotation 
		 * and applying it on field [login] of Model class [User].
		 * 
		 * Below mentioned peace of code [if block] is to demonstrate that you can fill custom errors outside the validation
		 * framework as well while still using internationalized messages.
		 * 
		 */
		if(!userService.isUserLoginUnique(user.getId(), user.getLogin())){
			FieldError loginError =new FieldError("user","login",messageSource.getMessage("non.unique.user.login", new String[]{user.getLogin()}, CustomUserLocal.getCurrentUserLocal()));
		    result.addError(loginError);
		    return "/user/registration";
		}
		
		userService.saveUser(user);
		model.addAttribute("success", messageSource.getMessage("user.add.success.message", new String[]{user.getFirstName(), user.getLastName()}, CustomUserLocal.getCurrentUserLocal()));
		return "redirect:/user/list";
	}


	/**
	 * This method will provide the medium to update an existing user by ID.
	 */
	@RequestMapping(value = { "/user/edit-user-{id}" }, method = RequestMethod.GET)
	public String editUser(@PathVariable Long id, ModelMap model) {
		User user = userService.findById(id);
		model.addAttribute("user", user);
		model.addAttribute("edit", true);
		return "/user/registration";
	}
	
	/**
	 * This method will be called on form submission, handling POST request for
	 * updating user in database. It also validates the user input
	 * Same URL that GET method: For Save or Update
	 * @throws SQLException 
	 */
	@RequestMapping(value = { "/user/edit-user-{id}" }, method = RequestMethod.POST)
	public String updateUser(@Valid @ModelAttribute("user") User user, BindingResult result,
			ModelMap model, @PathVariable Integer id)  {
		
		if (result.hasErrors()) {
			model.addAttribute("edit", true);
			return "/user/registration";
		}

		/*//Uncomment below 'if block' if you WANT TO ALLOW UPDATING LOGIN in UI which is a unique key to a User.
		if(!userService.isUserLoginUnique(user.getId(), user.getLogin())){
			FieldError loginError =new FieldError("user","login",messageSource.getMessage("non.unique.login", new String[]{user.getLogin()}, getCurrentUserLocal()));
		    result.addError(loginError);
		    return "/user/registration";
		}*/

		userService.updateUser(user);
		model.addAttribute("success", messageSource.getMessage("user.update.success.message", new String[]{user.getFirstName(), user.getLastName()}, CustomUserLocal.getCurrentUserLocal()));
		return "redirect:/user/list";
	}
	
	/**
	 * This method will provide the medium to update an existing user by Login.
	 */
	@RequestMapping(value = { "/user/update-user-{login}" }, method = RequestMethod.GET)
	public String editUserByLogin(@PathVariable String login, ModelMap model) {
		User user = userService.findByLogin(login);
		model.addAttribute("user", user);
		model.addAttribute("edit", true);
		return "/user/registration";
	}
	
	/**
	 * This method will be called on form submission, handling POST request for
	 * updating user in database. It also validates the user input
	 * Same URL that GET method: For Save or Update
	 * @throws SQLException 
	 */
	@RequestMapping(value = { "/user/update-user-{login}" }, method = RequestMethod.POST)
	public String editUserByLogin(@Valid @ModelAttribute("user") User user, BindingResult result,
			ModelMap model, @PathVariable String login)  {
		
		if (result.hasErrors()) {
			model.addAttribute("edit", true);
			return "/user/registration";
		}

		/*//Uncomment below 'if block' if you WANT TO ALLOW UPDATING LOGIN in UI which is a unique key to a User.
		if(!userService.isUserLoginUnique(user.getId(), user.getLogin())){
			FieldError loginError =new FieldError("user","login",messageSource.getMessage("non.unique.login", new String[]{user.getLogin()}, getCurrentUserLocal()));
		    result.addError(loginError);
		    return "/user/registration";
		}*/

		userService.updateUser(user);
		model.addAttribute("success", messageSource.getMessage("user.update.success.message", new String[]{user.getFirstName(), user.getLastName()}, CustomUserLocal.getCurrentUserLocal()));
		return "/home/mainPage";
	}

	
	/**
	 * This method will delete an user by it's LOGIN value.
	 */
	@RequestMapping(value = { "/user/delete-user-{id}-{login}" }, method = RequestMethod.GET)
	public String deleteUser(@PathVariable Long id, @PathVariable String login, ModelMap model) {
		userService.deleteUserById(id);
		model.addAttribute("success", messageSource.getMessage("user.remove.success.message", new String[]{login}, CustomUserLocal.getCurrentUserLocal()));
		return "redirect:/user/list";
		
	}
	
	
	/**
	 * Multiple SELECT-FORM-JSP-TO-MODEL Converter: Set<String> to Set<UserProfile>
	  * Gets UserProfile by Id
	 * We need to add a CustomCollectionEditor to the controller as follows:
	 * @param binder
	 */
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Set.class, "userProfiles", new CustomCollectionEditor(Set.class)
          {
            @Override
            protected Object convertElement(Object element)
            {
                Long id = null;

                if(element instanceof String && !((String)element).equals("")){
                    //From the JSP 'element' will be a String
                    try{
                        id = Long.parseLong((String) element);
                    }
                    catch (NumberFormatException e) {
                        //System.out.println("Element was " + ((String) element));
                        //e.printStackTrace();
                    }
                }
                else if(element instanceof Long) {
                    //From the database 'element' will be a Long
                    id = (Long) element;
                }

                if (id != null) {
                	UserProfile profile= new UserProfile();
            		profile.setId(id);
            		return profile;
                } else {
                	return null;
                }
                
            }
          });
        	
    }

}
