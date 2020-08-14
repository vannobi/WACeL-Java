package pe.edu.unsa.daisi.lis.cel.service;

import java.util.HashMap;
import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;



public interface IScenarioService {
	
	Scenario findById(Long id);
	
	Scenario findByTitle(String title);
	
	void saveScenario(Scenario scenario);
	
	void updateScenario(Scenario scenario);
	
	
	void deleteScenarioById(Long id);

	List<Scenario> findAllScenarios(); 
	
	List<Scenario> findAllArchivedScenarios();
	
	List<Scenario> findScenariosByProjectId(Long projectId);
	
	/**
	 * @param id
	 * @param name
	 * @return
	 */
	boolean isScenarioTitleUnique(Long id, String name);
	
	/**
	 * Parse one textual scenario to one structured scenario format
	 * 
	 * @param scenario
	 * @return
	 */
	StructuredScenario convertToStructuredScenario(Scenario scenario);
	
	
	/**
	 * Pre-condition (not described as another scenario) of a scenario (Context) should be satisfied by a Post-condition of other scenario (pre-condition/post-condition relationship)
	 * <br/>This method returns the list of scenarios whose Context Post-conditions satisfy the Context Pre-conditions of current scenario
	 * @param structuredScenario
	 * @param sequentiallyRelatedScenarios
	 * @return
	 */
	List<StructuredScenario> findSequentiallyRelatedScenariosByPreConditionAndPostCondition(StructuredScenario structuredScenario, List<StructuredScenario> sequentiallyRelatedScenarios);

	/**
	 * if we include the title of another scenario (UPPERCASE sentence) within the 
	 * <br/>context (pre-condition or post-condition), 
	 * <br/>an episode (sentence), 
	 * <br/>an alternative (solution) or a 
	 * <br/>constraint; 
	 * <br/>this context, episode, alternative or constraint element will be treated by this last scenario. 
	 * Through from these relationships it is possible to determine the order in which the scenarios should be executed
	 * 
	 * @param structuredScenario
	 * @param scenarios
	 * @return
	 */
	HashMap<String, List<StructuredScenario>> findSequentiallyRelatedScenarios(StructuredScenario structuredScenario, List<StructuredScenario> scenarios);
	
	/**
	 * Non-sequential relationships are explicitly described using the structure for grouping non-sequential episodes (#<episodes series>#), 
	 * <br/> i.e., if any or all episodes (UPPERCASE sentence) inside a non-sequential group are detailed in another scenarios, then last sub-scenarios interacts non-sequentially. 
	 * <br/> 
	 * <br/> 
	 * However, in most of projects, given scenarios interact by non-explicit non-sequential relationships; i.e., they could interact or compete with each other by shared resources.
	 * <br/> In order to identify non-explicit relationships between any two scenarios, we use the Proximity Index measure.
	 * <br/> 
	 * <br/> 
	 * Through from these relationships it is possible to identify concurrent or indistinct order execution of related scenarios
	 * FIX: Use HashMap -> explicit-non-sequentially and non-explicit-non-sequentially relationships 
	 * @param structuredScenario
	 * @param scenarios
	 * @param sequentiallyRelatedScenarios
	 * @return
	 */
	HashMap<String, List<StructuredScenario>> findNonSequentiallyRelatedScenarios(StructuredScenario structuredScenario, List<StructuredScenario> scenarios, HashMap<String, List<StructuredScenario>> sequentiallyRelatedScenarios);
	
	
}
	
