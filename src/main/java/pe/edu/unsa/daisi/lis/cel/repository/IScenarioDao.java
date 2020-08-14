package pe.edu.unsa.daisi.lis.cel.repository;

import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;



public interface IScenarioDao {

	Scenario findById(Long id);
	
	Scenario findByTitle(String title);
	
	void save(Scenario scenario);
	
	void deleteById(Long id);
	
	List<Scenario> findAllScenarios();
	
	List<Scenario> findAllArchivedScenarios();
	
	List<Scenario> findScenariosByProjectId(Long projectId);

}

