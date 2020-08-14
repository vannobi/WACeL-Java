package pe.edu.unsa.daisi.lis.cel.view;



import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import pe.edu.unsa.daisi.lis.cel.domain.model.lexicon.Lexicon;
import pe.edu.unsa.daisi.lis.cel.domain.model.lexicon.LexiconTypeEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.project.Project;
import pe.edu.unsa.daisi.lis.cel.service.ILexiconService;
import pe.edu.unsa.daisi.lis.cel.service.IProjectService;
import pe.edu.unsa.daisi.lis.cel.service.IUserService;
import pe.edu.unsa.daisi.lis.cel.util.CustomUserLocal;

/**
 * Lexicon Controller 
 */
@Controller
public class LexiconController  extends GenericController{

	@Autowired
	ILexiconService lexiconService;
	
	@Autowired
	IUserService userService;
	
	@Autowired
	IProjectService projectService;
		
	@Autowired
	MessageSource messageSource;

	/**
	 * This method will list all existing lexicons.
	 */
	@RequestMapping(value = "/lexicon/list", method = RequestMethod.GET)
	public String listLexicons(ModelMap model, @RequestParam(required=false) String success, @RequestParam(required=false) String error) {
		//Get Messages from lexicon operations
		if(success != null)
			model.addAttribute("success", success);
		if(error != null)
			model.addAttribute("error", error);
		List<Lexicon> lexicons = lexiconService.findAllLexicons();
		model.addAttribute("lexicons", lexicons);
		return "/lexicon/list";
	}
	
	/**
	 * This method shows lexicon details.
	 */
	@RequestMapping(value = "/lexicon/show-lexicon-{id}", method = RequestMethod.GET)
	public String showLexicon(@PathVariable("id") Long id, ModelMap model) {
		Lexicon lexicon = lexiconService.findById(id);
		model.addAttribute("lexicon", lexicon);

		return "/lexicon/show";

	}


	/**
	 * This method will provide the medium to add a new lexicon.
	 */
	@RequestMapping(value = { "/lexicon/newlexicon" }, method = RequestMethod.GET)
	public String newLexicon(ModelMap model) {
		Lexicon lexicon = new Lexicon();
		model.addAttribute("lexiconTypes", LexiconTypeEnum.values());
		model.addAttribute("lexicon", lexicon);
		model.addAttribute("edit", false);
		return "/lexicon/registration";
	}

	/**
	 * This method will be called on form submission, handling POST request for
	 * saving lexicon in database. It also validates the lexicon input
	 * Same URL that GET method: For Save or Update
	 */
	@RequestMapping(value = { "/lexicon/newlexicon" }, method = RequestMethod.POST)
	public String saveLexicon(@Valid @ModelAttribute("lexicon") Lexicon lexicon, BindingResult result,
			@SessionAttribute("selectedProjectId") Long selectedProjectId, ModelMap model) {
		
		if (result.hasErrors()) {
			model.addAttribute("lexiconTypes", LexiconTypeEnum.values());
			return "/lexicon/registration";
		}

		/*
		 * Preferred way to achieve uniqueness of field [name] should be implementing custom @Unique annotation 
		 * and applying it on field [name] of Model class [LexiconDTO].
		 * 
		 * Below mentioned peace of code [if block] is to demonstrate that you can fill custom errors outside the validation
		 * framework as well while still using internationalized messages.
		 * 
		 */
		if(!lexiconService.isLexiconNameUnique(lexicon.getId(), lexicon.getName())){
			FieldError lexiconError =new FieldError("lexicon","name",messageSource.getMessage("non.unique.lexicon.name", new String[]{lexicon.getName()}, CustomUserLocal.getCurrentUserLocal()));
		    result.addError(lexiconError);
		    return "/lexicon/registration";
		}
		
		lexicon.setOwner(userService.findByLogin(getPrincipal()));
		Project project = projectService.findById(selectedProjectId);
		lexicon.setProject(project);
		lexiconService.saveLexicon(lexicon);
		model.addAttribute("success", messageSource.getMessage("lexicon.add.success.message", new String[]{lexicon.getName()}, CustomUserLocal.getCurrentUserLocal()));
		return "redirect:/home/mainPage";
		//return "redirect:/lexicon/list";
	}


	/**
	 * This method will provide the medium to update an existing lexicon by ID.
	 */
	@RequestMapping(value = { "/lexicon/edit-lexicon-{id}" }, method = RequestMethod.GET)
	public String editLexicon(@PathVariable Long id, ModelMap model) {
		Lexicon lexicon = lexiconService.findById(id);
		model.addAttribute("lexiconTypes", LexiconTypeEnum.values());
		model.addAttribute("lexicon", lexicon);
		
		model.addAttribute("edit", true);
		return "/lexicon/registration";
	}
	
	/**
	 * This method will be called on form submission, handling POST request for
	 * updating lexicon in database. It also validates the lexicon input
	 * Same URL that GET method: For Save or Update
	 * @throws SQLException 
	 */
	@RequestMapping(value = { "/lexicon/edit-lexicon-{id}" }, method = RequestMethod.POST)
	public String updateLexicon(@Valid @ModelAttribute("lexicon") Lexicon lexicon, BindingResult result,
			ModelMap model, @PathVariable Integer id)  {
		
		if (result.hasErrors()) {
			model.addAttribute("lexiconTypes", LexiconTypeEnum.values());
			model.addAttribute("edit", true);
			return "/lexicon/registration";
		}

		/*//Uncomment below 'if block' if you WANT TO ALLOW UPDATING NAME in UI which is a unique key to a Lexicon.
		if(!lexiconService.isLexiconNameUnique(lexicon.getId(), lexicon.getName())){
			FieldError loginError =new FieldError("lexicon","name",messageSource.getMessage("non.unique.name", new String[]{lexicon.getName()}, getCurrentUsertLocal()));
		    result.addError(loginError);
		    return "/lexicon/registration";
		}*/
		
		lexiconService.updateLexicon(lexicon);
		model.addAttribute("success", messageSource.getMessage("lexicon.update.success.message", new String[]{lexicon.getName()}, CustomUserLocal.getCurrentUserLocal()));
		return "redirect:/home/mainPage";
		//return "redirect:/lexicon/list";
	}
		
		
	/**
	 * This method will delete an lexicon by it's ID value.
	 */
	@RequestMapping(value = { "/lexicon/delete-lexicon-{id}-{title}" }, method = RequestMethod.GET)
	public String deleteLexicon(@PathVariable Long id, @PathVariable String title, ModelMap model) {
		lexiconService.deleteLexiconById(id);
		model.addAttribute("success", messageSource.getMessage("lexicon.remove.success.message", new String[]{title}, CustomUserLocal.getCurrentUserLocal()));
		return "redirect:/home/mainPage";
		//return "redirect:/lexicon/list";
		
	}
	
	
	
	/**
	 * This method will list all existing lexicons by project Id in JSON format.
	 * And, Put selectedProjectId in the SESSION
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/lexicon/project/{id}/lexicons", method = RequestMethod.GET)
	public @ResponseBody List<Lexicon> listLexicons(@PathVariable("id") Long id, HttpServletRequest request) {
		//Put selectedProjectId in the SESSION
		request.getSession().setAttribute("selectedProjectId", id);
		List<Lexicon> lexicons = lexiconService.findLexiconsByProjectId(id);
		return lexicons;
		
	}


}
