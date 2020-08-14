package pe.edu.unsa.daisi.lis.cel.service.analysis;

import java.util.HashMap;
import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.Defect;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;



public interface IConsistencyAnalysisService {
	
	/**
	 * Evaluate properties that contribute to Consistency by by deriving a Petri-Net from it and its related scenarios, 
	 * by generating a reachability graph, and searching for indicators (wrong information) using reachability analysis 
	 * @param mainScenario
	 * @param sequentiallyRelatedScenariosHashMap sequentially related scenarios
	 * @param nonSeqRelatedScenarioHashMap non sequentially related scenarios
	 * @param scenarios all project scenarios 
	 **/
	List<Defect> analyze(StructuredScenario mainScenario, HashMap<String, List<StructuredScenario>> sequentiallyRelatedScenariosHashMap, HashMap<String, List<StructuredScenario>> nonSeqRelatedScenarioHashMap);
	
}