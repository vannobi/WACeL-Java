package pe.edu.unsa.daisi.lis.cel.view;



import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import pe.edu.unsa.daisi.lis.cel.domain.model.project.Project;
import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;
import pe.edu.unsa.daisi.lis.cel.domain.model.user.UserProfile;
import pe.edu.unsa.daisi.lis.cel.service.IProjectService;
import pe.edu.unsa.daisi.lis.cel.service.IUserProfileService;
import pe.edu.unsa.daisi.lis.cel.service.IUserService;

/**
 * Index Page Controller 
 */
@Controller
@SessionAttributes({"loggedinuser", "roles", "projectsUser"})
public class HomeController  extends GenericController{

	@Autowired
	IUserService userService;
	
	@Autowired
	IUserProfileService userProfileService;
	
	@Autowired
	IProjectService projectService;
	
	@Autowired
	MessageSource messageSource;

	@Autowired
	PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;
	
	
	/**
	 * This method will provide logged user to views <br/>
	 * Works with @SessionAttributes
	 * Add values in the Session which will be identified globally, and It runs before any request and automatically
	 */
	@ModelAttribute("loggedinuser")
	public String setUpLoggedUserForm() {
		return getPrincipal();
	}
	
	/**
	 * This method will provide UserProfile list to views <br/>
	 * Works with @SessionAttributes
	 * Add values in the Session which will be identified globally, and It runs before any request and automatically
	 */
	@ModelAttribute("roles")
	public List<UserProfile> initializeProfiles() {
		return userProfileService.findAll();
	}
	
	/**
	 * This method will provide associated projects of the logged user to views <br/>
	 * Works with @SessionAttributes
	 * Add values in the Session which will be identified globally, and It runs before any request and automatically
	 */
	@ModelAttribute("projectsUser")
	public List<Project> setUpProjectsUserForm() {
		if(!isCurrentAuthenticationAnonymous()) {
			User user = userService.findByLogin(getPrincipal());
			return projectService.findAssociatedProjectsByUserId(user.getId());
		}
		return new ArrayList<Project>();
	}
	
	
	/**
	 * This method handles Access-Denied redirect.
	 */
	@RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
	public String accessDeniedPage(ModelMap model) {
		return "/home/accessDenied";
	}

	/**
	 * This method handles login GET requests.
	 * If users is already logged-in and tries to goto login page again, will be redirected to list page.
	 */
	@RequestMapping(value = {"/login", "/home/login"}, method = RequestMethod.GET)
	public String loginPage() {
		if (isCurrentAuthenticationAnonymous()) {
			return "/index";
	    } else {
	    	return "/home/mainPage";  
	    }
	}
	
	/**
	 * This method handles Main Page redirect.
	 */
	@RequestMapping(value = "/home/mainPage", method = RequestMethod.GET)
	public String mainPage(ModelMap model, @RequestParam(required=false) String success, @RequestParam(required=false) String error) {
		//Get Messages from lexicon or scenario operations
		if(success != null)
			model.addAttribute("success", success);
		if(error != null)
			model.addAttribute("error", error);
		return "/home/mainPage";
	}
	
	/**
	 * This method handles logout requests.
	 * Toggle the handlers if you are RememberMe functionality is useless in your app.
	 */
	@RequestMapping(value="/home/logout", method = RequestMethod.GET)
	public String logoutPage (HttpServletRequest request, HttpServletResponse response){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null){    
			//Destroy Session
			HttpSession session = request.getSession(false);
			if (session != null) {
			    session.invalidate();
			}
			//new SecurityContextLogoutHandler().logout(request, response, auth);
			persistentTokenBasedRememberMeServices.logout(request, response, auth);
			SecurityContextHolder.getContext().setAuthentication(null);
			
		}
		return "redirect:/index?logout";//"/login?logout";
	}
	
	@RequestMapping(value="/home/logout", method = RequestMethod.POST)
	public String logout(HttpServletRequest request, HttpServletResponse response){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null){    
			//Destroy Session
			HttpSession session = request.getSession(false);
			if (session != null) {
			    session.invalidate();
			}
			//new SecurityContextLogoutHandler().logout(request, response, auth);
			persistentTokenBasedRememberMeServices.logout(request, response, auth);
			SecurityContextHolder.getContext().setAuthentication(null);
		}
		return "redirect:/login?logout";//"/index?logout";
	}

	

}
