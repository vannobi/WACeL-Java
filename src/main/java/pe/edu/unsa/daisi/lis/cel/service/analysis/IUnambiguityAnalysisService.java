package pe.edu.unsa.daisi.lis.cel.service.analysis;

import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.Defect;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;



public interface IUnambiguityAnalysisService {
	
	/**
	 * Evaluate properties that contribute to Unambiguity by reading the scenario elements like TITLE, GOAL, EPISODES and ALTERNATIVES
	 * @param structuredScenario
	 * @return
	 */
	List<Defect> analyze(StructuredScenario structuredScenario);
	
}