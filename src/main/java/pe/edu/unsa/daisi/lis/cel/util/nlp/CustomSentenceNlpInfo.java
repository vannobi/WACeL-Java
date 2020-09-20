package pe.edu.unsa.daisi.lis.cel.util.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * It is used to represent a Custom Sentence with tokenization, postagging and parsing information returned by a NLP tool: stanford, openNLP, ...
 * <br/>
 * It contains information like Tokens (List<CustomToken>) with words, stems/lemmas and pos tags; Subjects (HashMap<Integer, CustomToken>); Objects (HashMap<Integer, CustomToken>); and Action-Verbs (HashMap<Integer, CustomToken>) of a textual sentence
 * @author Edgar
 *
 */
public class CustomSentenceNlpInfo {
	Integer numSentences;
	List<CustomToken> tokens;
	HashMap<Integer, CustomToken> subjects;//key = position of token in sentence
	HashMap<Integer, CustomToken> directObjects;//key = position of token in sentence
	HashMap<Integer, CustomToken> indirectObjects;//key = position of token in sentence
	HashMap<Integer, CustomToken> mainActionVerbs;//key = position of token in sentence
	HashMap<Integer, CustomToken> complementActionVerbs;//VB is a clause complement without (xcomp) or with (ccomp) its own subject
	HashMap<Integer, CustomToken> complementSubjects;//key = position of token in sentence, clause complement with (ccomp) its own subject
	HashMap<Integer, CustomToken> modifierActionVerbs;//VB is a clause which modifies a noun, verb or other predicate(adjective, etc)
	HashMap<Integer, CustomToken> modifierSubjects;//key = position of token in sentence, relative clause modifier of a noun with (acl:relcl) its own subject
	public CustomSentenceNlpInfo(Integer numSentences, List<CustomToken> tokens, HashMap<Integer, CustomToken> subjects,
			HashMap<Integer, CustomToken> directObjects, HashMap<Integer, CustomToken> indirectObjects, HashMap<Integer, CustomToken> mainActionVerbs,
			HashMap<Integer, CustomToken> complementActionVerbs, HashMap<Integer, CustomToken> complementSubjects, 
			HashMap<Integer, CustomToken> modifierActionVerbs, HashMap<Integer, CustomToken> modifierSubjects) {
		super();
		this.numSentences = numSentences;
		this.tokens = tokens;
		this.subjects = subjects;
		this.directObjects = directObjects;
		this.indirectObjects = indirectObjects;
		this.mainActionVerbs = mainActionVerbs;
		this.complementActionVerbs = complementActionVerbs;
		this.complementSubjects = complementSubjects;
		this.modifierActionVerbs = modifierActionVerbs;	
		this.modifierSubjects = modifierSubjects;
	}
	
	
	public Integer getNumSentences() {
		return numSentences;
	}


	public void setNumSentences(Integer numSentences) {
		this.numSentences = numSentences;
	}


	public List<CustomToken> getTokens() {
		return tokens;
	}
	public void setTokens(List<CustomToken> tokens) {
		this.tokens = tokens;
	}
	public HashMap<Integer, CustomToken> getSubjects() {
		return subjects;
	}
	public void setSubjects(HashMap<Integer, CustomToken> subjects) {
		this.subjects = subjects;
	}
	public HashMap<Integer, CustomToken> getDirectObjects() {
		return directObjects;
	}
	public void setDirectObjects(HashMap<Integer, CustomToken> objects) {
		this.directObjects = objects;
	}
	
	public HashMap<Integer, CustomToken> getIndirectObjects() {
		return indirectObjects;
	}
	public void setIndirectObjects(HashMap<Integer, CustomToken> indirectObjects) {
		this.indirectObjects = indirectObjects;
	}
			
	public HashMap<Integer, CustomToken> getMainActionVerbs() {
		return mainActionVerbs;
	}
	public void setMainActionVerbs(HashMap<Integer, CustomToken> mainActionVerbs) {
		this.mainActionVerbs = mainActionVerbs;
	}
	public HashMap<Integer, CustomToken> getComplementActionVerbs() {
		return complementActionVerbs;
	}
	public void setComplementActionVerbs(HashMap<Integer, CustomToken> complementActionVerbs) {
		this.complementActionVerbs = complementActionVerbs;
	}
	public HashMap<Integer, CustomToken> getModifierActionVerbs() {
		return modifierActionVerbs;
	}
	
	public HashMap<Integer, CustomToken> getComplementSubjects() {
		return complementSubjects;
	}
	public void setComplementSubjects(HashMap<Integer, CustomToken> complementSubjects) {
		this.complementSubjects = complementSubjects;
	}
	public void setModifierActionVerbs(HashMap<Integer, CustomToken> modifierActionVerbs) {
		this.modifierActionVerbs = modifierActionVerbs;
	}
	
	
	public HashMap<Integer, CustomToken> getModifierSubjects() {
		return modifierSubjects;
	}
	public void setModifierSubjects(HashMap<Integer, CustomToken> modifierSubjects) {
		this.modifierSubjects = modifierSubjects;
	}
	
	
	public String getSubjectsAsString() {
		String subjects = "";
		if (this.getSubjects() != null) {
			subjects = "[";
			int cont = 0;
			for (Map.Entry<Integer, CustomToken> entry : this.getSubjects().entrySet()) {
	 		    if(cont == 0)
	 		    	subjects = subjects + entry.getValue().getWord();
	 		    else
	 		    	subjects = subjects + ", " + entry.getValue().getWord();
	 		    cont++;
	        }
			subjects = subjects + "]";
		}
		return subjects;
	}
	
	public String getDirectObjectsAsString() {
		String objects = "";
		if (this.getDirectObjects() != null) {
			objects = "[";
			int cont = 0;
			for (Map.Entry<Integer, CustomToken> entry : this.getDirectObjects().entrySet()) {
	 		    if(cont == 0)
	 		    	objects = objects + entry.getValue().getWord();
	 		    else
	 		    	objects = objects + ", " + entry.getValue().getWord();
	 		    cont++;
	        }
			objects = objects + "]";
		}
		return objects;
	}
	
	public String getIndirectObjectsAsString() {
		String objects = "";
		if (this.getIndirectObjects() != null) {
			objects = "[";
			int cont = 0;
			for (Map.Entry<Integer, CustomToken> entry : this.getIndirectObjects().entrySet()) {
	 		    if(cont == 0)
	 		    	objects = objects + entry.getValue().getWord();
	 		    else
	 		    	objects = objects + ", " + entry.getValue().getWord();
	 		    cont++;
	        }
			objects = objects + "]";
		}
		return objects;
	}
	
	public String getMainActionVerbsAsString() {
		String verbs = "";
		if (this.getMainActionVerbs() != null) {
			verbs = "[";
			int cont = 0;
			for (Map.Entry<Integer, CustomToken> entry : this.getMainActionVerbs().entrySet()) {
	 		    if(cont == 0)
	 		    	verbs = verbs + entry.getValue().getWord();
	 		    else
	 		    	verbs = verbs + ", " + entry.getValue().getWord();
	 		    cont++;
	        }
			verbs = verbs + "]";
		}
		return verbs;
	}
	
	public String getComplementActionVerbsAsString() {
		String verbs = "";
		if (this.getComplementActionVerbs() != null) {
			verbs = "[";
			int cont = 0;
			for (Map.Entry<Integer, CustomToken> entry : this.getComplementActionVerbs().entrySet()) {
	 		    if(cont == 0)
	 		    	verbs = verbs + entry.getValue().getWord();
	 		    else
	 		    	verbs = verbs + ", " + entry.getValue().getWord();
	 		    cont++;
	        }
			verbs = verbs + "]";
		}
		return verbs;
	}
	
	public String getComplementSubjectsAsString() {
		String subjects = "";
		if (this.getComplementSubjects() != null) {
			subjects = "[";
			int cont = 0;
			for (Map.Entry<Integer, CustomToken> entry : this.getComplementSubjects().entrySet()) {
	 		    if(cont == 0)
	 		    	subjects = subjects + entry.getValue().getWord();
	 		    else
	 		    	subjects = subjects + ", " + entry.getValue().getWord();
	 		    cont++;
	        }
			subjects = subjects + "]";
		}
		return subjects;
	}
	
	public String getModifierSubjectsAsString() {
		String subjects = "";
		if (this.getModifierSubjects() != null) {
			subjects = "[";
			int cont = 0;
			for (Map.Entry<Integer, CustomToken> entry : this.getModifierSubjects().entrySet()) {
	 		    if(cont == 0)
	 		    	subjects = subjects + entry.getValue().getWord();
	 		    else
	 		    	subjects = subjects + ", " + entry.getValue().getWord();
	 		    cont++;
	        }
			subjects = subjects + "]";
		}
		return subjects;
	}
	
	public String getModifierActionVerbsAsString() {
		String verbs = "";
		if (this.getModifierActionVerbs() != null) {
			verbs = "[";
			int cont = 0;
			for (Map.Entry<Integer, CustomToken> entry : this.getModifierActionVerbs().entrySet()) {
	 		    if(cont == 0)
	 		    	verbs = verbs + entry.getValue().getWord();
	 		    else
	 		    	verbs = verbs + ", " + entry.getValue().getWord();
	 		    cont++;
	        }
			verbs = verbs + "]";
		}
		return verbs;
	}
	
	public List<String> getDirectObjectsAsStringList() {
		List<String> objects = new ArrayList<>();
		if (this.getDirectObjects() != null) {
			for (Map.Entry<Integer, CustomToken> entry : this.getDirectObjects().entrySet()) {
	 		  	objects.add(entry.getValue().getWord());
	 		
	        }
			
		}
		return objects;
	}
	
	public List<String> getIndirectObjectsAsStringList() {
		List<String> objects = new ArrayList<>();
		if (this.getIndirectObjects() != null) {
			for (Map.Entry<Integer, CustomToken> entry : this.getIndirectObjects().entrySet()) {
	 		  	objects.add(entry.getValue().getWord());
	 		
	        }
			
		}
		return objects;
	}
	
	public List<String> getSubjectsAsStringList() {
		List<String> subjects = new ArrayList<>();
		if (this.getSubjects() != null) {
			for (Map.Entry<Integer, CustomToken> entry : this.getSubjects().entrySet()) {
	 		  	subjects.add(entry.getValue().getWord());
	 		
	        }
			
		}
		return subjects;
	}
	
	public List<String> getMainActionVerbsAsStringList() {
		List<String> verbs = new ArrayList<>();
		if (this.getMainActionVerbs() != null) {
			for (Map.Entry<Integer, CustomToken> entry : this.getMainActionVerbs().entrySet()) {
	 		  	verbs.add(entry.getValue().getWord());
	 		
	        }
			
		}
		return verbs;
	}
	
	public List<String> getComplementActionVerbsAsStringList() {
		List<String> verbs = new ArrayList<>();
		if (this.getComplementActionVerbs() != null) {
			for (Map.Entry<Integer, CustomToken> entry : this.getComplementActionVerbs().entrySet()) {
	 		  	verbs.add(entry.getValue().getWord());
	 		
	        }
			
		}
		return verbs;
	}
	
	public List<String> getComplementSubjectsAsStringList() {
		List<String> subjects = new ArrayList<>();
		if (this.getComplementSubjects() != null) {
			for (Map.Entry<Integer, CustomToken> entry : this.getComplementSubjects().entrySet()) {
	 		  	subjects.add(entry.getValue().getWord());
	 		
	        }
			
		}
		return subjects;
	}
	
	public List<String> getModifierSubjectsAsStringList() {
		List<String> subjects = new ArrayList<>();
		if (this.getModifierSubjects() != null) {
			for (Map.Entry<Integer, CustomToken> entry : this.getModifierSubjects().entrySet()) {
	 		  	subjects.add(entry.getValue().getWord());
	 		
	        }
			
		}
		return subjects;
	}
	
	public List<String> getModifierActionVerbsAsStringList() {
		List<String> verbs = new ArrayList<>();
		if (this.getModifierActionVerbs() != null) {
			for (Map.Entry<Integer, CustomToken> entry : this.getModifierActionVerbs().entrySet()) {
	 		  	verbs.add(entry.getValue().getWord());
	 		
	        }
			
		}
		return verbs;
	}
	
}
