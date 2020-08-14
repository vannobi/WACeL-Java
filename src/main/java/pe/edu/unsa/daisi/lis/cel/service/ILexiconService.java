package pe.edu.unsa.daisi.lis.cel.service;

import java.util.List;

import pe.edu.unsa.daisi.lis.cel.domain.model.lexicon.Lexicon;



public interface ILexiconService {
	
	Lexicon findById(Long id);
	
	Lexicon findByName(String name);
	
	void saveLexicon(Lexicon lexicon);
	
	void updateLexicon(Lexicon lexicon);
	
	
	void deleteLexiconById(Long id);

	List<Lexicon> findAllLexicons();
	
	List<Lexicon> findAllArchivedLexicons(); 
	
	List<Lexicon> findLexiconsByProjectId(Long projectId);
	
	boolean isLexiconNameUnique(Long id, String name);
	
	List<Lexicon> sortlexicons(List<Lexicon> lexicons, int low, int high);

}