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

import pe.edu.unsa.daisi.lis.cel.domain.model.project.Project;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;
import pe.edu.unsa.daisi.lis.cel.service.IProjectService;
import pe.edu.unsa.daisi.lis.cel.service.IScenarioService;
import pe.edu.unsa.daisi.lis.cel.service.IUserService;
import pe.edu.unsa.daisi.lis.cel.util.CustomUserLocal;
import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioManipulation;

/**
 * Scenario Controller 
 */
@Controller
//@SessionAttributes("selectedProjectId")
public class ScenarioController  extends GenericController{

	@Autowired
	IScenarioService scenarioService;
	
	@Autowired
	IUserService userService;
	
	@Autowired
	IProjectService projectService;
		
	@Autowired
	MessageSource messageSource;

	/**
	 * This method will list all existing scenarios.
	 */
	@RequestMapping(value = "/scenario/list", method = RequestMethod.GET)
	public String listScenarios(ModelMap model, @RequestParam(required=false) String success, @RequestParam(required=false) String error) {
		//Get Messages from scenario operations
		if(success != null)
			model.addAttribute("success", success);
		if(error != null)
			model.addAttribute("error", error);
		List<Scenario> scenarios = scenarioService.findAllScenarios();
		model.addAttribute("scenarios", scenarios);
		return "/scenario/list";
	}
	
	/**
	 * This method shows scenario details.
	 */
	@RequestMapping(value = "/scenario/show-scenario-{id}", method = RequestMethod.GET)
	public String showScenario(@PathVariable("id") Long id, @SessionAttribute("selectedProjectId") Long selectedProjectId, ModelMap model) {
		Scenario scenario = scenarioService.findById(id);
		List<Scenario> scenarios = scenarioService.findScenariosByProjectId(selectedProjectId);
		
		scenario = ScenarioManipulation.createHyperLinksToRelatedScenarios(scenario, scenarios);
		model.addAttribute("scenario", scenario);

		return "/scenario/show";

	}


	/**
	 * This method will provide the medium to add a new scenario.
	 */
	@RequestMapping(value = { "/scenario/newscenario" }, method = RequestMethod.GET)
	public String newScenario(ModelMap model) {
		Scenario scenario = new Scenario();
		model.addAttribute("scenario", scenario);
		model.addAttribute("edit", false);
		return "/scenario/registration";
	}

	/**
	 * This method will be called on form submission, handling POST request for
	 * saving scenario in database. It also validates the scenario input
	 * Same URL that GET method: For Save or Update
	 */
	@RequestMapping(value = { "/scenario/newscenario" }, method = RequestMethod.POST)
	public String saveScenario(@Valid @ModelAttribute("scenario") Scenario scenario, BindingResult result,
			@SessionAttribute("selectedProjectId") Long selectedProjectId, ModelMap model) {
						
		if (result.hasErrors()) {
			return "/scenario/registration";
		}

		/*
		 * Preferred way to achieve uniqueness of field [name] should be implementing custom @Unique annotation 
		 * and applying it on field [name] of Model class [ScenarioDTO].
		 * 
		 * Below mentioned peace of code [if block] is to demonstrate that you can fill custom errors outside the validation
		 * framework as well while still using internationalized messages.
		 * 
		 */
		if(!scenarioService.isScenarioTitleUnique(scenario.getId(), scenario.getTitle())){
			FieldError scenarioError =new FieldError("scenario","name",messageSource.getMessage("non.unique.scenario.title", new String[]{scenario.getTitle()}, CustomUserLocal.getCurrentUserLocal()));
		    result.addError(scenarioError);
		    return "/scenario/registration";
		}
		
		scenario.setOwner(userService.findByLogin(getPrincipal()));
		Project project = projectService.findById(selectedProjectId);
		scenario.setProject(project);
		scenarioService.saveScenario(scenario);
		model.addAttribute("success", messageSource.getMessage("scenario.add.success.message", new String[]{scenario.getTitle()}, CustomUserLocal.getCurrentUserLocal()));
		return "redirect:/home/mainPage";
		//return "redirect:/scenario/list";
	}


	/**
	 * This method will provide the medium to update an existing scenario by ID.
	 */
	@RequestMapping(value = { "/scenario/edit-scenario-{id}" }, method = RequestMethod.GET)
	public String editScenario(@PathVariable Long id, ModelMap model) {
		Scenario scenario = scenarioService.findById(id);
		model.addAttribute("scenario", scenario);
		
		model.addAttribute("edit", true);
		return "/scenario/registration";
	}
	
	/**
	 * This method will be called on form submission, handling POST request for
	 * updating scenario in database. It also validates the scenario input
	 * Same URL that GET method: For Save or Update
	 * @throws SQLException 
	 */
	@RequestMapping(value = { "/scenario/edit-scenario-{id}" }, method = RequestMethod.POST)
	public String updateScenario(@Valid @ModelAttribute("scenario") Scenario scenario, BindingResult result,
			ModelMap model, @PathVariable Integer id)  {
		
		if (result.hasErrors()) {
			model.addAttribute("edit", true);
			return "/scenario/registration";
		}

		/*//Uncomment below 'if block' if you WANT TO ALLOW UPDATING NAME in UI which is a unique key to a Scenario.
		if(!scenarioService.isScenarioNameUnique(scenario.getId(), scenario.getName())){
			FieldError loginError =new FieldError("scenario","name",messageSource.getMessage("non.unique.name", new String[]{scenario.getName()}, getCurrentUsertLocal()));
		    result.addError(loginError);
		    return "/scenario/registration";
		}*/
		
		scenarioService.updateScenario(scenario);
		model.addAttribute("success", messageSource.getMessage("scenario.update.success.message", new String[]{scenario.getTitle()}, CustomUserLocal.getCurrentUserLocal()));
		return "redirect:/home/mainPage";
		//return "redirect:/scenario/list";
	}
		
		
	/**
	 * This method will delete an scenario by it's ID value.
	 */
	@RequestMapping(value = { "/scenario/delete-scenario-{id}-{title}" }, method = RequestMethod.GET)
	public String deleteScenario(@PathVariable Long id, @PathVariable String title, ModelMap model) {
		scenarioService.deleteScenarioById(id);
		model.addAttribute("success", messageSource.getMessage("scenario.remove.success.message", new String[]{title}, CustomUserLocal.getCurrentUserLocal()));
		return "redirect:/home/mainPage";
		//return "redirect:/scenario/list";
		
	}
	
	
	
	/**
	 * This method will list - JSON all existing scenarios by project Id. <br/>
	 * And, Put selectedProjectId in the SESSION
	 * @param id project
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/scenario/project/{id}/scenarios", method = RequestMethod.GET)
	public @ResponseBody List<Scenario> listScenarios(@PathVariable("id") Long id, HttpServletRequest request) {
		//Put selectedProjectId in the SESSION
		request.getSession().setAttribute("selectedProjectId", id);
		List<Scenario> scenarios = scenarioService.findScenariosByProjectId(id);
		return scenarios;
		
	}


}
