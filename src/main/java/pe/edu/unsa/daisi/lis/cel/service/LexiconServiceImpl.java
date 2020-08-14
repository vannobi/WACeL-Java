package pe.edu.unsa.daisi.lis.cel.service;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.unsa.daisi.lis.cel.domain.model.lexicon.Lexicon;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;
import pe.edu.unsa.daisi.lis.cel.repository.ILexiconDao;
import pe.edu.unsa.daisi.lis.cel.util.StringManipulation;

@Service("lexiconService")
@Transactional
public class LexiconServiceImpl implements ILexiconService{

	@Autowired
	private ILexiconDao dao;

	
	public Lexicon findById(Long id) {
		return dao.findById(id);
	}

	public Lexicon findByName(String name) {
		Lexicon lexicon = dao.findByName(name);
		return lexicon;
	}

	public void saveLexicon(Lexicon lexicon) {
		lexicon.setInclusionDate(Calendar.getInstance());
		lexicon.setExclusionFlag(false);
		
		dao.save(lexicon);
	}

	/*
	 * Since the method is running with Transaction, No need to call hibernate update explicitly.
	 * Just fetch the entity from db and update it with proper values within transaction.
	 * It will be updated in db once transaction ends. 
	 */
	public void updateLexicon(Lexicon lexicon) {
		Lexicon entity = dao.findById(lexicon.getId());
		if(entity!=null){
			entity.setName(lexicon.getName());
			entity.setNotion(lexicon.getNotion());
			entity.setImpact(lexicon.getImpact());
			entity.setLexiconType(lexicon.getLexiconType());
			entity.setSynonyms(lexicon.getSynonyms());
			
		}
	}

	
	
	public void deleteLexiconById(Long id) {
		//set exclusion flag to true
		Lexicon entity = dao.findById(id);
		if(entity!=null){
			entity.setExclusionFlag(true);
		}
		//dao.deleteById(id);
	}

	public List<Lexicon> findAllLexicons() {
		return dao.findAllLexicons();
	}
	
	public List<Lexicon> findAllArchivedLexicons() {
		return dao.findAllArchivedLexicons();
	}

	public List<Lexicon> findLexiconsByProjectId(Long projectId){
		return dao.findLexiconsByProjectId(projectId);
	}
	
	public boolean isLexiconNameUnique(Long id, String name) {
		Lexicon lexicon = findByName(name);
		return ( lexicon == null || ((id != null) && (lexicon.getId() == id)));
	}

	/**
	 * Sort scenarios by NAME length: QuickSort by number of words
	 * Put in front NAMES with high number of words: DESC
	 * based on https://gist.github.com/ShaunPlummer/006fe6f932f3463543e3
	 * @param lexicons
	 * low begin index
	 * high last index
	 * @return
	 */
	public List<Lexicon> sortlexicons(List<Lexicon> lexicons, int low, int high) {
		if(lexicons != null && !lexicons.isEmpty()) {

			int i = low, j = high;
			//Find the item in the middle of the list
			Lexicon pivot = lexicons.get(low + (high-low)/2);
			//Split the list into two groups
			while (i <= j) {
				// If the current value from the left list is greater then the pivot
				// element then get the next element from the left list

				//Select an element from the first half that is greater than the middle value
				while (StringManipulation.getNumberOfWords(lexicons.get(i).getName()) > StringManipulation.getNumberOfWords(pivot.getName())) {
					i++;
				}
				//Select an element from the second half that is less than middle value 
				while (StringManipulation.getNumberOfWords(lexicons.get(j).getName()) < StringManipulation.getNumberOfWords(pivot.getName())) {
					j--;
				}

				//if the selected value from the left list is less than or equal to the element in the right list.
				//Exchange them. Before moving to the next element
				if (i <= j) {
					//exchange(i, j);
					Lexicon temp = lexicons.get(i);
					lexicons.set(i, lexicons.get(j));
					lexicons.set(j, temp);
					i++;
					j--;
				}
			}
			// Recursion
		    if (low < j)
		    	sortlexicons(lexicons, low, j);
		    if (i < high)
		    	sortlexicons(lexicons, i, high);
			
		}
		return lexicons;
	}
	
}
