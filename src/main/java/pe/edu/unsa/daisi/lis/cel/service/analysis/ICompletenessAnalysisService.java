package pe.edu.unsa.daisi.lis.cel.service.analysis;

import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.Defect;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;



public interface ICompletenessAnalysisService {
	
	/**
	 * Evaluate properties that contribute to Completeness by reading the scenario elements like TITLE, GOAL, EPISODES and ALTERNATIVES; 
	 * and searching for indicators (missing and wrong information) that provide evidence of violation of Completeness properties
	 * @param structuredScenario
	 * @param sequentiallyRelatedScenarios sequentially related scenarios
	 * @param sequentiallyRelatedScenariosByPrePost Pre-condition (not described as another scenario) of a scenario (Context) should be satisfied by a Post-condition of other scenario
	 * @param scenarios all project scenarios 
	 **/
	List<Defect> analyze(StructuredScenario structuredScenario, List<StructuredScenario> sequentiallyRelatedScenarios, List<StructuredScenario> sequentiallyRelatedScenariosByPrePost, List<StructuredScenario> scenarios);
	
}