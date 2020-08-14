package pe.edu.unsa.daisi.lis.cel.repository;

import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.lexicon.Synonym;



public interface ISynonymDao {

	Synonym findById(Long id);
	
	Synonym findByName(String name);
	
	void save(Synonym synonym);
	
	void deleteById(Long id);
	
	List<Synonym> findAllSynonyms();
	
	List<Synonym> findAllArchivedSynonyms();

}

