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
import org.springframework.web.bind.annotation.SessionAttribute;

import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.Defect;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.QualityPropertyEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.project.Project;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;
import pe.edu.unsa.daisi.lis.cel.service.IProjectService;
import pe.edu.unsa.daisi.lis.cel.service.IScenarioService;
import pe.edu.unsa.daisi.lis.cel.service.IUserService;
import pe.edu.unsa.daisi.lis.cel.service.analysis.ICompletenessAnalysisService;
import pe.edu.unsa.daisi.lis.cel.service.analysis.IConsistencyAnalysisService;
import pe.edu.unsa.daisi.lis.cel.service.analysis.IUnambiguityAnalysisService;
import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioAnnotation;

/**
 * Lexicon Controller 
 */
@Controller
public class AnalysisController  extends GenericController{

	@Autowired
	IScenarioService scenarioService;
	
	@Autowired
	IProjectService projectService;
	
	@Autowired
	IUnambiguityAnalysisService unambiguityAnalysisService;
	
	@Autowired
	ICompletenessAnalysisService completenessAnalysisService; 
	
	@Autowired
	IConsistencyAnalysisService consistencyAnalysisService; 
	
	@Autowired
	IUserService userService;
			
	@Autowired
	MessageSource messageSource;

	@RequestMapping(value = "/analysis/analyze-scenario-{id}", method = RequestMethod.GET)
	public String analyzeScenario(@PathVariable("id") Long id, @SessionAttribute("selectedProjectId") Long selectedProjectId, ModelMap model) {
		Scenario scenario = scenarioService.findById(id);
		StructuredScenario structuredScenario = scenarioService.convertToStructuredScenario(scenario);
		
		List<Scenario> scenarios = scenarioService.findScenariosByProjectId(selectedProjectId);
		
		
		
		List<StructuredScenario> structuredScenarios = new ArrayList<StructuredScenario>();
		HashMap<String, List<StructuredScenario>> sequentiallyRelatedScenariosHashMap = new HashMap<>();
		HashMap<String, List<StructuredScenario>> nonSequentiallyRelatedScenariosHashMap =  new HashMap<>();
		List<StructuredScenario> sequentiallyRelatedScenarios = new ArrayList<StructuredScenario>();
		List<StructuredScenario> sequentiallyRelatedScenariosByPrePost  = new ArrayList<StructuredScenario>();
		if(scenarios != null && !scenarios.isEmpty()) {
			for(Scenario relatedScenario : scenarios)
				structuredScenarios.add(scenarioService.convertToStructuredScenario(relatedScenario));
					
			sequentiallyRelatedScenariosHashMap = scenarioService.findSequentiallyRelatedScenarios(structuredScenario, structuredScenarios);
			for (Map.Entry<String, List<StructuredScenario>> entry : sequentiallyRelatedScenariosHashMap.entrySet()) {
				sequentiallyRelatedScenarios.addAll(entry.getValue());
			}
		
			sequentiallyRelatedScenariosByPrePost = scenarioService.findSequentiallyRelatedScenariosByPreConditionAndPostCondition(structuredScenario, structuredScenarios);
			
			nonSequentiallyRelatedScenariosHashMap = scenarioService.findNonSequentiallyRelatedScenarios(structuredScenario, structuredScenarios, sequentiallyRelatedScenariosHashMap);
		}
		//Annotate with NLP info
		structuredScenario = ScenarioAnnotation.annotateScenario(structuredScenario);
		
		List<Defect> unambiguityDefects = unambiguityAnalysisService.analyze(structuredScenario);
		
		List<Defect> completenessDefects = completenessAnalysisService.analyze(structuredScenario, sequentiallyRelatedScenarios, sequentiallyRelatedScenariosByPrePost, structuredScenarios);
		
		List<Defect> consistencyDefects = consistencyAnalysisService.analyze(structuredScenario, sequentiallyRelatedScenariosHashMap, nonSequentiallyRelatedScenariosHashMap);
		
		List<String> unambiguityProperties = QualityPropertyEnum.getUnambiguityProperties();
		List<String> completenessProperties = QualityPropertyEnum.getCompletenessProperties();
		List<String> consistencyProperties = QualityPropertyEnum.getConsistencyProperties();
		
		model.addAttribute("scenario", scenario);
		model.addAttribute("unambiguityProperties", unambiguityProperties);
		model.addAttribute("completenessProperties", completenessProperties);
		model.addAttribute("consistencyProperties", consistencyProperties);
		
		model.addAttribute("unambiguityDefects", unambiguityDefects);
		model.addAttribute("completenessDefects", completenessDefects);
		model.addAttribute("consistencyDefects", consistencyDefects);
		
		return "/analysis/showScenarioDefects";

	}
	
	@RequestMapping(value = "/analysis/analyze-project-{id}", method = RequestMethod.GET)
	public String analyzeProject(@PathVariable("id") Long id, ModelMap model) {
		Project project = projectService.findById(id);
		//Get all scenarios from project
		List<Scenario> scenarios = scenarioService.findScenariosByProjectId(id);
		List<StructuredScenario> structuredScenarios = new ArrayList<StructuredScenario>();
		if(scenarios != null && !scenarios.isEmpty()) {
			for(Scenario relatedScenario : scenarios) {
				StructuredScenario structuredScenario = scenarioService.convertToStructuredScenario(relatedScenario); 
				structuredScenarios.add(structuredScenario);
			}
		}
			
		// Create HashMaps of defects for each scenario (id)
		HashMap<Long, List<Defect>> unambiguityDefectsHashMap = new HashMap<Long, List<Defect>>();
		HashMap<Long, List<Defect>> completenessDefectsHashMap = new HashMap<Long, List<Defect>>();
		HashMap<Long, List<Defect>> consistencyDefectsHashMap = new HashMap<Long, List<Defect>>();
				
		//analyze scenarios
		if(structuredScenarios != null && !structuredScenarios.isEmpty()) {
			for(StructuredScenario structuredScenario : structuredScenarios) {
				HashMap<String, List<StructuredScenario>> sequentiallyRelatedScenariosHashMap = scenarioService.findSequentiallyRelatedScenarios(structuredScenario, structuredScenarios);
				List<StructuredScenario> sequentiallyRelatedScenarios = new ArrayList<>();
				for (Map.Entry<String, List<StructuredScenario>> entry : sequentiallyRelatedScenariosHashMap.entrySet()) {
					sequentiallyRelatedScenarios.addAll(entry.getValue());
				}
				//List<StructuredScenario> sequentiallyRelatedScenariosByPrePost  = scenarioService.getSequentiallyRelatedScenariosByPreConditionAndPostCondition(structuredScenario, sequentiallyRelatedScenarios);
				List<StructuredScenario> sequentiallyRelatedScenariosByPrePost  = scenarioService.findSequentiallyRelatedScenariosByPreConditionAndPostCondition(structuredScenario, structuredScenarios);
				HashMap<String, List<StructuredScenario>> nonSequentiallyRelatedScenariosHashMap = scenarioService.findNonSequentiallyRelatedScenarios(structuredScenario, structuredScenarios, sequentiallyRelatedScenariosHashMap);
				
				//Annotate with NLP info
				structuredScenario = ScenarioAnnotation.annotateScenario(structuredScenario);
				
				List<Defect> unambiguityDefects = unambiguityAnalysisService.analyze(structuredScenario);
				unambiguityDefectsHashMap.put(structuredScenario.getId(), unambiguityDefects);
				
				List<Defect> completenessDefects = completenessAnalysisService.analyze(structuredScenario, sequentiallyRelatedScenarios, sequentiallyRelatedScenariosByPrePost, structuredScenarios);
				completenessDefectsHashMap.put(structuredScenario.getId(), completenessDefects);
				
				List<Defect> consistencyDefects = consistencyAnalysisService.analyze(structuredScenario, sequentiallyRelatedScenariosHashMap, nonSequentiallyRelatedScenariosHashMap);
				consistencyDefectsHashMap.put(structuredScenario.getId(), consistencyDefects);
			}
		}
				
		List<String> unambiguityProperties = QualityPropertyEnum.getUnambiguityProperties();
		List<String> completenessProperties = QualityPropertyEnum.getCompletenessProperties();
		List<String> consistencyProperties = QualityPropertyEnum.getConsistencyProperties();
		
		model.addAttribute("project", project);
		model.addAttribute("scenarios", scenarios);
		
		model.addAttribute("unambiguityProperties", unambiguityProperties);
		model.addAttribute("completenessProperties", completenessProperties);
		model.addAttribute("consistencyProperties", consistencyProperties);
		
		model.addAttribute("unambiguityDefects", unambiguityDefectsHashMap);
		model.addAttribute("completenessDefects", completenessDefectsHashMap);
		model.addAttribute("consistencyDefects", consistencyDefectsHashMap);
		
		return "/analysis/showProjectDefects";

	}

}
