package pe.edu.unsa.daisi.lis.cel.view;



import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
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

import pe.edu.unsa.daisi.lis.cel.domain.model.lexicon.LexiconTypeEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.project.LanguageEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.project.Project;
import pe.edu.unsa.daisi.lis.cel.domain.model.project.ProjectUser;
import pe.edu.unsa.daisi.lis.cel.domain.model.user.User;
import pe.edu.unsa.daisi.lis.cel.domain.model.user.UserProfile;
import pe.edu.unsa.daisi.lis.cel.service.IProjectService;
import pe.edu.unsa.daisi.lis.cel.service.IUserService;
import pe.edu.unsa.daisi.lis.cel.util.CustomUserLocal;
import pe.edu.unsa.daisi.lis.cel.view.dto.ProjectDTO;

/**
 * Project Controller 
 */
@Controller
public class ProjectController  extends GenericController{

	@Autowired
	IProjectService projectService;
	
	@Autowired
	IUserService userService;
		
	@Autowired
	MessageSource messageSource;

	/**
	 * This method will list all existing projects by logged user.
	 */
	@RequestMapping(value = "/project/list", method = RequestMethod.GET)
	public String listProjects(ModelMap model, @RequestParam(required=false) String success, @RequestParam(required=false) String error) {
		//Get Messages from project operations
		if(success != null)
			model.addAttribute("success", success);
		if(error != null)
			model.addAttribute("error", error);
		//project Created by LoogedUser
		User user = userService.findByLogin(getPrincipal());
		List<Project> projects = projectService.findProjectsCreatedByUserId(user.getId());
		model.addAttribute("projects", projects);
		return "/project/list";
	}
	
	/**
	 * This method shows project details.
	 */
	@RequestMapping(value = "/project/show-project-{id}", method = RequestMethod.GET)
	public String showProject(@PathVariable("id") Long id, ModelMap model) {
		Project project = projectService.findById(id);
		model.addAttribute("project", project);

		return "/project/show";

	}


	/**
	 * This method will provide the medium to add a new project.
	 */
	@RequestMapping(value = { "/project/newproject" }, method = RequestMethod.GET)
	public String newProject(ModelMap model) {
		ProjectDTO projectDto = new ProjectDTO();
		projectDto.setLanguage(LanguageEnum.ENGLISH.getValue());
		model.addAttribute("projectDto", projectDto);
		//Add user candidates for collaboration
		List<User> collaboratorCandidates = userService.findAllUsers();
		model.addAttribute("collaboratorCandidates", collaboratorCandidates);
		model.addAttribute("languageList", LanguageEnum.values());
		model.addAttribute("edit", false);
		return "/project/registration";
	}

	/**
	 * This method will be called on form submission, handling POST request for
	 * saving project in database. It also validates the project input
	 * Same URL that GET method: For Save or Update
	 */
	@RequestMapping(value = { "/project/newproject" }, method = RequestMethod.POST)
	public String saveProject(@Valid @ModelAttribute("projectDto") ProjectDTO projectDto, BindingResult result,
			ModelMap model) {
		
		if (result.hasErrors()) {
			//Add user candidates for collaboration
			List<User> collaboratorCandidates = userService.findAllUsers();
			model.addAttribute("collaboratorCandidates", collaboratorCandidates);
			model.addAttribute("languageList", LanguageEnum.values());
			return "/project/registration";
		}

		/*
		 * Preferred way to achieve uniqueness of field [name] should be implementing custom @Unique annotation 
		 * and applying it on field [name] of Model class [ProjectDTO].
		 * 
		 * Below mentioned peace of code [if block] is to demonstrate that you can fill custom errors outside the validation
		 * framework as well while still using internationalized messages.
		 * 
		 */
		if(!projectService.isProjectNameUnique(projectDto.getId(), projectDto.getName())){
			FieldError projectError =new FieldError("project","name",messageSource.getMessage("non.unique.project.name", new String[]{projectDto.getName()}, CustomUserLocal.getCurrentUserLocal()));
		    result.addError(projectError);
		    return "/project/registration";
		}
		Project project = convertToEntity(projectDto);
		project.setOwner(userService.findByLogin(getPrincipal()));
		projectService.saveProject(project);
		model.addAttribute("success", messageSource.getMessage("project.add.success.message", new String[]{project.getName()}, CustomUserLocal.getCurrentUserLocal()));
		return "redirect:/project/list";
	}


	/**
	 * This method will provide the medium to update an existing project by ID.
	 */
	@RequestMapping(value = { "/project/edit-project-{id}" }, method = RequestMethod.GET)
	public String editProject(@PathVariable Long id, ModelMap model) {
		Project project = projectService.findById(id);
		ProjectDTO projectDto = convertToDto(project);
		model.addAttribute("projectDto", projectDto);
		//Add user candidates for collaboration
		List<User> collaboratorCandidates = userService.findAllUsers();
		model.addAttribute("collaboratorCandidates", collaboratorCandidates);
		model.addAttribute("languageList", LanguageEnum.values());
		model.addAttribute("edit", true);
		return "/project/registration";
	}
	
	/**
	 * This method will be called on form submission, handling POST request for
	 * updating project in database. It also validates the project input
	 * Same URL that GET method: For Save or Update
	 * @throws SQLException 
	 */
	@RequestMapping(value = { "/project/edit-project-{id}" }, method = RequestMethod.POST)
	public String updateProject(@Valid @ModelAttribute("projectDto") ProjectDTO projectDto, BindingResult result,
			ModelMap model, @PathVariable Integer id)  {
		
		if (result.hasErrors()) {
			model.addAttribute("edit", true);
			//Add user candidates for collaboration
			List<User> collaboratorCandidates = userService.findAllUsers();
			model.addAttribute("collaboratorCandidates", collaboratorCandidates);
			model.addAttribute("languageList", LanguageEnum.values());
			return "/project/registration";
		}

		/*//Uncomment below 'if block' if you WANT TO ALLOW UPDATING NAME in UI which is a unique key to a Project.
		if(!projectService.isProjectNameUnique(project.getId(), project.getName())){
			FieldError loginError =new FieldError("project","name",messageSource.getMessage("non.unique.name", new String[]{project.getName()}, getCurrentUsertLocal()));
		    result.addError(loginError);
		    return "/project/registration";
		}*/
		Project project = convertToEntity(projectDto);
		projectService.updateProject(project);
		model.addAttribute("success", messageSource.getMessage("project.update.success.message", new String[]{project.getName()}, CustomUserLocal.getCurrentUserLocal()));
		return "redirect:/project/list";
	}
		
		
	/**
	 * This method will delete an project by it's ID value.
	 */
	@RequestMapping(value = { "/project/delete-project-{id}-{name}" }, method = RequestMethod.GET)
	public String deleteProject(@PathVariable Long id, @PathVariable String name, ModelMap model) {
		projectService.deleteProjectById(id);
		model.addAttribute("success", messageSource.getMessage("project.remove.success.message", new String[]{name}, CustomUserLocal.getCurrentUserLocal()));
		return "redirect:/project/list";
		
	}
	
	/**
	 * Entity - DTO - Entity
	 */
	
	private ProjectDTO convertToDto(Project project) {
		/*
		ModelMapper modelMapper = new ModelMapper();
		//Map Set<ProjectUser> to Set<User>
		modelMapper.addMappings(new PropertyMap<Project, ProjectDTO>() {
			  @Override
			  protected void configure() {
				Set<User> collaborators = new HashSet<User>();
				for(ProjectUser projectUser: source.getCollaborators()) {
					collaborators.add(projectUser.getCollaborator());
				}
			    map().setCollaborators(collaborators);
			    
			  }
			});
		
		
		ProjectDTO projectDto = modelMapper.map(project, ProjectDTO.class);
		*/
		ProjectDTO projectDto = new ProjectDTO();
		projectDto.setId(project.getId());
		projectDto.setName(project.getName());
		projectDto.setDescription(project.getDescription());
		projectDto.setLanguage(project.getLanguage());
		projectDto.setCaseSensitive(project.getCaseSensitive());
		Set<User> collaborators = new HashSet<User>(); 
		for(ProjectUser projectUser: project.getCollaborators()) {
			collaborators.add(projectUser.getCollaborator());
		}
		projectDto.setCollaborators(collaborators);
			
	    return projectDto;
	}
	
	private Project convertToEntity(ProjectDTO projectDto)  {
		/*
		ModelMapper modelMapper = new ModelMapper();
		//Map Set<ProjectUser> to Set<User>
				modelMapper.addMappings(new PropertyMap<ProjectDTO, Project>() {
					  @Override
					  protected void configure() {
						Set<ProjectUser> collaborators = new HashSet<ProjectUser>();
						for(User user: source.getCollaborators()) {
							//convert to ProjectUser
							ProjectUser projectUser = new ProjectUser(); //ProjectUser(project, user);
							projectUser.setProject(destination);
							projectUser.setCollaborator(user);
							projectUser.setAuthorizer(userService.findByLogin(getPrincipal()));
							projectUser.setInclusionDate(Calendar.getInstance());
							projectUser.setExclusionFlag(false);
							
							collaborators.add(projectUser);
						}
					    map().setCollaborators(collaborators);
					    
					  }
					});
				
				
		Project project = modelMapper.map(projectDto, Project.class);
		*/
		Project project;
		if(projectDto.getId() == null) {
			project = new Project();
		}
		else {
			project = projectService.findById(projectDto.getId());
		}
		//project.setId(projectDto.getId());
		project.setName(projectDto.getName());
		project.setLanguage(projectDto.getLanguage());
		project.setDescription(projectDto.getDescription());
		project.setCaseSensitive(projectDto.getCaseSensitive());
		Set<ProjectUser> collaborators = new HashSet<ProjectUser>(); 
		for(User user: projectDto.getCollaborators()) {
			//convert to ProjectUser
			ProjectUser projectUser = new ProjectUser(); //ProjectUser(project, user);
			projectUser.setProject(project);
			projectUser.setCollaborator(user);
			projectUser.setAuthorizer(userService.findByLogin(getPrincipal()));
			projectUser.setInclusionDate(Calendar.getInstance());
			projectUser.setExclusionFlag(false);
			collaborators.add(projectUser);
		}
		
		project.setCollaborators(collaborators);
	    return project;
	}
	
	/**
	 * Multiple SELECT-FORM-JSP-TO-MODEL Converter: Set<String> to Set<User>
	 * Gets User by Id
	 * We need to add a CustomCollectionEditor to the controller as follows:
	 * @param binder
	 */
	@InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Set.class, "collaborators", new CustomCollectionEditor(Set.class)
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
                	User user=  new User();
            		user.setId(id);
            		return user;
                } else {
                	return null;
                }
                
            }
          });
        
		
    }

}
