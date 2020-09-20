package pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;
import pe.edu.unsa.daisi.lis.cel.util.ListManipulation;
import pe.edu.unsa.daisi.lis.cel.util.RegularExpression;
import pe.edu.unsa.daisi.lis.cel.util.StringManipulation;

public class ScenarioManipulation {
	public static final String EMPTY_CHAR = "";
	public static final String WHITESPACE_CHAR = " ";
	public static final String NON_SEQUENTIAL_GROUP_CHAR = "#";
	public static final String NEW_LINE = "\\n";
	public static final String HTML_BREAK = "<br/>";
	
	public static final String PRE_CONDITION_RELATIONSHIP = "PRE_CONDITION";
	public static final String POST_CONDITION_RELATIONSHIP = "POST_CONDITION";
	public static final String CONSTRAINT_RELATIONSHIP = "CONSTRAINT";
	public static final String SUB_SCENARIO_RELATIONSHIP = "SUB_SCENARIO";
	public static final String ALTERNATIVE_RELATIONSHIP = "ALTERNATIVE";
	public static final String EXPLICIT_NON_SEQ_RELATIONSHIP = "EXPLICIT";
	public static final String NON_EXPLICIT_NON_SEQ_RELATIONSHIP = "NON_EXPLICIT";
	
	/**
	 * Sort scenarios by TITLE length, i.e, by number of words: QuickSort 
	 * Put in front Scenarios with high number of words: DESC
	 * based on https://gist.github.com/ShaunPlummer/006fe6f932f3463543e3
	 * @param scenarios StructuredScenario or Scenario Type
	 * @param low begin index
	 * @param high last index
	 * @return	 
	 */
	public static <T> List<T> sortScenarios(List<T> scenarios, int low, int high) {
		if(scenarios != null && !scenarios.isEmpty()) {
			int i = low, j = high;
			//Find the item in the middle of the list
			T pivot = scenarios.get(low + (high-low)/2);
			//Split the list into two groups
			while (i <= j) {
				// If the current value from the left list is greater then the pivot
				// element then get the next element from the left list
				if(scenarios.get(i) instanceof Scenario) {
					//Select an element from the first half that is greater than the middle value
					while (StringManipulation.getNumberOfWords(((Scenario) scenarios.get(i)).getTitle()) > StringManipulation.getNumberOfWords(((Scenario) pivot).getTitle())) {
						i++;
					}
					//Select an element from the second half that is less than middle value 
					while (StringManipulation.getNumberOfWords(((Scenario) scenarios.get(j)).getTitle()) < StringManipulation.getNumberOfWords(((Scenario) pivot).getTitle())) {
						j--;
					}
				} else {
					//Select an element from the first half that is greater than the middle value
					while (StringManipulation.getNumberOfWords(((StructuredScenario) scenarios.get(i)).getTitle()) > StringManipulation.getNumberOfWords(((StructuredScenario) pivot).getTitle())) {
						i++;
					}
					//Select an element from the second half that is less than middle value 
					while (StringManipulation.getNumberOfWords(((StructuredScenario) scenarios.get(j)).getTitle()) < StringManipulation.getNumberOfWords(((StructuredScenario) pivot).getTitle())) {
						j--;
					}
				}
				
				//if the selected value from the left list is less than or equal to the element in the right list.
				//Exchange them. Before moving to the next element
				if (i <= j) {
					//exchange(i, j);
					T temp = scenarios.get(i);
					scenarios.set(i, scenarios.get(j));
					scenarios.set(j, temp);
					i++;
					j--;
				}
			}
			// Recursion
		    if (low < j)
		    	sortScenarios(scenarios, low, j);
		    if (i < high)
		    	sortScenarios(scenarios, i, high);
			
		}
		return scenarios;
	}
	
	 /**
	 * Sort scenarios by Episodes length, i.e, by number of episodes: QuickSort 
	 * Put in front Scenarios with high number of episodes: DESC
	 * based on https://gist.github.com/ShaunPlummer/006fe6f932f3463543e3
	 * @param scenarios StructuredScenario or Scenario Type
	 * @param low begin index
	 * @param high last index
	 * @return	 
	 */
	public static <T> List<T> sortScenariosByEpisodes(List<T> scenarios, int low, int high) {
		if(scenarios != null && !scenarios.isEmpty()) {
			int i = low, j = high;
			//Find the item in the middle of the list
			T pivot = scenarios.get(low + (high-low)/2);
			//Split the list into two groups
			while (i <= j) {
				// If the current value from the left list is greater then the pivot
				// element then get the next element from the left list
				if(scenarios.get(i) instanceof StructuredScenario) {
					//Select an element from the first half that is greater than the middle value
					while (((StructuredScenario) scenarios.get(i)).getEpisodes().size() > ((StructuredScenario) pivot).getEpisodes().size()) {
						i++;
					}
					//Select an element from the second half that is less than middle value 
					while (((StructuredScenario) scenarios.get(j)).getEpisodes().size() < ((StructuredScenario) pivot).getEpisodes().size()) {
						j--;
					}
				} else {
					return scenarios;
				}
			
				//if the selected value from the left list is less than or equal to the element in the right list.
				//Exchange them. Before moving to the next element
				if (i <= j) {
					//exchange(i, j);
					T temp = scenarios.get(i);
					scenarios.set(i, scenarios.get(j));
					scenarios.set(j, temp);
					i++;
					j--;
				}
			}
			// Recursion
		    if (low < j)
		    	sortScenariosByEpisodes(scenarios, low, j);
		    if (i < high)
		    	sortScenariosByEpisodes(scenarios, i, high);
			
		}
		return scenarios;
	}
	
	/**
	 * Create HyperLinks in scenario elements to Sequentially Related Scenarios
	 * 
	 * @param scenario
	 * @param List<StructuredScenario> sequentiallyRelatedScenarios
	 * @return
	 */
	public static Scenario createHyperLinksToRelatedScenarios(Scenario scenario, List<Scenario> sequentiallyRelatedScenarioss) {
		List<Scenario> sortedScenarios = new ArrayList<Scenario>();  
		if(sequentiallyRelatedScenarioss != null && !sequentiallyRelatedScenarioss.isEmpty()) {
			//Sort scenarios
			sortedScenarios = ListManipulation.copyList(sequentiallyRelatedScenarioss);
			sortedScenarios = sortScenarios(sortedScenarios, 0, sortedScenarios.size() - 1) ;
			//Replace in scenario elements by HyperLink to related scenario
			for(Scenario relatedScenario : sortedScenarios) {
				//Replace by HyperLink in Context
				String urlScenario = " <a title=\"Scenario\" href=\"../scenario/show-scenario-" + relatedScenario.getId() + "\">" + relatedScenario.getTitle().toUpperCase() +"</a>";
				String regExpTitle = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + relatedScenario.getTitle().toUpperCase() + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT;
				Pattern patternTitle = Pattern.compile(regExpTitle);
				
				if (scenario.getContext().toUpperCase().contains(relatedScenario.getTitle().toUpperCase())) {
					
					String context = scenario.getContext().toUpperCase();
					Matcher matcherTitle = patternTitle.matcher(context);
					int startIndex = -1;
					int endIndex = -1;
					while(matcherTitle.find()) {
						startIndex = matcherTitle.start();
						endIndex = matcherTitle.end();
						break;//firs occurrence
					}
					if (startIndex >= 0 && endIndex > 0) {
						StringBuilder newContext = new StringBuilder(scenario.getContext());
						newContext.replace(startIndex, endIndex, urlScenario);
						scenario.setContext(newContext.toString());
					}
				}
				//Replace by HyperLink in Episodes
				if (scenario.getEpisodes().toUpperCase().contains(relatedScenario.getTitle().toUpperCase())) {
					
					String episodeSentence = scenario.getEpisodes().toUpperCase();
					Matcher matcherTitle = patternTitle.matcher(episodeSentence);
					int startIndex = -1;
					int endIndex = -1;
					while(matcherTitle.find()) {
						startIndex = matcherTitle.start();
						endIndex = matcherTitle.end();
						break;//firs occurrence
					}
					if (startIndex >= 0 && endIndex > 0) {
						StringBuilder newEpisodes = new StringBuilder(scenario.getEpisodes());
						newEpisodes.replace(startIndex, endIndex, urlScenario);
						scenario.setEpisodes(newEpisodes.toString());
					}					
					
				}
				//Replace by HyperLink in Alternatives
				if (scenario.getAlternative().toUpperCase().contains(relatedScenario.getTitle().toUpperCase())) {
					
					String solutionStep = scenario.getAlternative().toUpperCase();
					Matcher matcherTitle = patternTitle.matcher(solutionStep);
					int startIndex = -1;
					int endIndex = -1;
					while(matcherTitle.find()) {
						startIndex = matcherTitle.start();
						endIndex = matcherTitle.end();
						break;//firs occurrence
					}
					if (startIndex >= 0 && endIndex > 0) {
						StringBuilder newAlternative = new StringBuilder(scenario.getAlternative());
						newAlternative.replace(startIndex, endIndex, urlScenario);
						scenario.setAlternative(newAlternative.toString());
					}	
				}
			}
			
		}
		//Print with new lines
		scenario.setGoal(scenario.getGoal().replaceAll(NEW_LINE, HTML_BREAK));
		scenario.setActors(scenario.getActors().replaceAll(NEW_LINE, HTML_BREAK));
		scenario.setResources(scenario.getResources().replaceAll(NEW_LINE, HTML_BREAK));
		scenario.setContext(scenario.getContext().replaceAll(NEW_LINE, HTML_BREAK));
		scenario.setEpisodes(scenario.getEpisodes().replaceAll(NEW_LINE, HTML_BREAK));
		scenario.setAlternative(scenario.getAlternative().replaceAll(NEW_LINE, HTML_BREAK));
		
		return scenario;		
	}
	
	/**
	 * A scenario references within its internal section/element another related scenario
	 * @param scenario
	 * @param element
	 * @param relatedScenario
	 * @return
	 */
	public static Boolean isSEquentiallyRelated(StructuredScenario scenario, String element, StructuredScenario relatedScenario) {
		String regExpTitle = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + relatedScenario.getTitle().toUpperCase() + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT;
		Pattern patternTitle = Pattern.compile(regExpTitle);
		String context = element.toUpperCase();
		Matcher matcherTitle = patternTitle.matcher(context);
		int startIndex = -1;
		int endIndex = -1;
		while(matcherTitle.find()) {
			startIndex = matcherTitle.start();
			endIndex = matcherTitle.end();
			break;//firs occurrence
		}
		if (startIndex >= 0 && endIndex > 0) {
			return true;
		}			
		return false;
	}
	
	
	/**
	 * In order to identify non-explicit relationships between any two scenarios, we use the Proximity Index measure.
	 * <br/>
	 * Two or more scenarios are likely related when they share common portions in their descriptions, i.e., they involve the participation of common actors, they access shared resources or they are executed in the same context. They have the same importance. 
	 * <br/>Thus, if two scenarios have common actors, share resources or have common context pre-conditions, they could be related.
	 * @param scenario
	 * @param otherScenario
	 * @return
	 */
	public static double getProximityIndex(StructuredScenario scenario, StructuredScenario otherScenario) {
		List<String> unionActors = new ArrayList<>();
		List<String> intersectionActors = new ArrayList<>();
		List<String> unionResources = new ArrayList<>();
		List<String> intersectionResources = new ArrayList<>();
		List<String> unionPreconditions = new ArrayList<>();
		List<String> intersectionPreconditions = new ArrayList<>();
		double numActorsUnion = 0;
		double numActorsIntersection = 0;
		double numResourcesUnion = 0;	
		double numResourcesIntersection = 0;
		double numPreconditionsUnion = 0;	
		double numPreconditionsIntersection = 0;
		
		//union atores
		if (scenario.getActors() != null && !scenario.getActors().isEmpty()) 
			for (String actor : scenario.getActors())
				unionActors.add(actor.toLowerCase());
		
		if 	(otherScenario.getActors() != null && !otherScenario.getActors().isEmpty()) 
			for (String actor : otherScenario.getActors())
				if(!unionActors.contains(actor.toLowerCase()))
					unionActors.add(actor.toLowerCase());
				else //intersection
					intersectionActors.add(actor.toLowerCase());
			
		//union resources
		if (scenario.getResources() != null && !scenario.getResources().isEmpty()) 
			for (String resource : scenario.getResources())
				unionResources.add(resource.toLowerCase());
		
		if 	(otherScenario.getResources() != null && !otherScenario.getResources().isEmpty()) 
			for (String resource : otherScenario.getResources())
				if(!unionResources.contains(resource.toLowerCase()))
					unionResources.add(resource.toLowerCase());
				else //intersection
					intersectionResources.add(resource.toLowerCase());
		
		//union Preconditions
		if (scenario.getContext().getPreConditions() != null && !scenario.getContext().getPreConditions().isEmpty()) 
			for (String condition : scenario.getContext().getPreConditions())
				unionPreconditions.add(condition.toLowerCase());
		
		if 	(otherScenario.getContext().getPreConditions() != null && !otherScenario.getContext().getPreConditions().isEmpty()) 
			for (String condition : otherScenario.getContext().getPreConditions())
				if(!unionPreconditions.contains(condition.toLowerCase()))
					unionPreconditions.add(condition.toLowerCase());
				else //intersection
					intersectionPreconditions.add(condition.toLowerCase());
		
		
		numActorsUnion = unionActors.size();
		numActorsIntersection = intersectionActors.size();
		numResourcesUnion = unionResources.size();
		numResourcesIntersection = intersectionResources.size();
		numPreconditionsUnion = unionPreconditions.size();
		numPreconditionsIntersection = intersectionPreconditions.size();
		
		if(numActorsUnion + numResourcesUnion + numPreconditionsUnion > 0) {	
			double proximityIndex = (numActorsIntersection + numResourcesIntersection + numPreconditionsIntersection)/
																(numActorsUnion + numResourcesUnion + numPreconditionsUnion);
			return proximityIndex;
			
		}	
		else 
			return 0;
	}
	

}
