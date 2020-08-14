package pe.edu.unsa.daisi.lis.cel.repository;

import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.lexicon.Lexicon;



public interface ILexiconDao {

	Lexicon findById(Long id);
	
	Lexicon findByName(String name);
	
	void save(Lexicon lexicon);
	
	void deleteById(Long id);
	
	List<Lexicon> findAllLexicons();
	
	List<Lexicon> findAllArchivedLexicons();
	
	List<Lexicon> findLexiconsByProjectId(Long projectId);

}

