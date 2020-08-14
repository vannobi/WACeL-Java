package pe.edu.unsa.daisi.lis.cel.view;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pe.edu.unsa.daisi.lis.cel.domain.model.lexicon.Lexicon;
import pe.edu.unsa.daisi.lis.cel.domain.model.petrinet.PetriNet;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;
import pe.edu.unsa.daisi.lis.cel.service.IPetriNetService;
import pe.edu.unsa.daisi.lis.cel.service.IProjectService;
import pe.edu.unsa.daisi.lis.cel.service.IScenarioService;

/**
 * PetriNet Controller 
 */
@Controller
public class PetriNetController  extends GenericController{

	@Autowired
	IPetriNetService petriNetService;
	
	@Autowired
	IScenarioService scenarioService;
	
	@Autowired
	IProjectService projectService;
		
	@Autowired
	MessageSource messageSource;

	
	/**
	 * This method return a PetriNet by scenario Id in JSON format.
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/petriNet/scenario/{id}", method = RequestMethod.GET)
	public @ResponseBody PetriNet getPetriNet(@PathVariable("id") Long id) {
		Scenario scenario = scenarioService.findById(id);
		StructuredScenario structuredScenario = scenarioService.convertToStructuredScenario(scenario);
		PetriNet petriNet = petriNetService.transformScenario(structuredScenario, 300, 50);
		
		//PNML
		petriNet.setPnml(petriNetService.createPNML(petriNet));
		return petriNet;		
	}

	/**
	 * This method return an Integrated PetriNet by scenario Id in JSON format.
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/petriNet/integrated/project/{projectId}/scenario/{scenarioId}", method = RequestMethod.GET)
	public @ResponseBody PetriNet getIntegratedPetriNet(@PathVariable("projectId") Long projectId, @PathVariable("scenarioId") Long scenarioId) {
		PetriNet mainPetriNet = new PetriNet(null, null);
		Scenario scenario = scenarioService.findById(scenarioId);
		StructuredScenario structuredMainScenario = scenarioService.convertToStructuredScenario(scenario);
		
		List<Scenario> scenarios = scenarioService.findScenariosByProjectId(projectId);
			
		List<StructuredScenario> structuredScenarios = new ArrayList<StructuredScenario>();

		if(scenarios != null && !scenarios.isEmpty()) {
			for(Scenario relatedScenario : scenarios)
				structuredScenarios.add(scenarioService.convertToStructuredScenario(relatedScenario));
					
			HashMap<String, List<StructuredScenario>> sequentiallyRelatedScenariosHashMap = scenarioService.findSequentiallyRelatedScenarios(structuredMainScenario, structuredScenarios);
			
			HashMap<String, List<StructuredScenario>> nonSeqRelatedScenarioHashMap = scenarioService.findNonSequentiallyRelatedScenarios(structuredMainScenario, structuredScenarios, sequentiallyRelatedScenariosHashMap);
			
			mainPetriNet = petriNetService.integratePetriNetsFromMainScenario(structuredMainScenario, sequentiallyRelatedScenariosHashMap, nonSeqRelatedScenarioHashMap);			
		
		}
		//PNML
		mainPetriNet.setPnml(petriNetService.createPNML(mainPetriNet));
		return mainPetriNet;		
	}
	
	
	/*
	 * TEST
	 */
	@RequestMapping(value = "/petrinet/show-petriNet-{id}", method = RequestMethod.GET)
	public String showPetriNet(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("scenarioId", id);
		return "/petrinet/showPetriNet";
	}
		
	@RequestMapping(value = "/petrinet/show-integratedPetriNet-{projectId}-{scenarioId}", method = RequestMethod.GET)
	public String showIntegratedPetriNet(@PathVariable("projectId") Long projectId, @PathVariable("scenarioId") Long scenarioId, ModelMap model) {
		model.addAttribute("scenarioId", scenarioId);
		model.addAttribute("projectId", projectId);
		return "/petrinet/showIntegratedPetriNet";
	}
}
