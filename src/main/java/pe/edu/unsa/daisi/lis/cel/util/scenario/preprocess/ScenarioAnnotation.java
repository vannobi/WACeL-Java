package pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredAlternative;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredEpisode;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;
import pe.edu.unsa.daisi.lis.cel.util.nlp.CoreNLPAnalyzer;
import pe.edu.unsa.daisi.lis.cel.util.nlp.CustomSentenceNlpInfo;
import pe.edu.unsa.daisi.lis.cel.util.nlp.INLPAnalyzer;

/**
 * Annotate one structured scenario with NLP info (tokens with POSTAG-STEM, subjects, verbs, objects): 
 * <br> - title
 * <br> - goal
 * <br> - episodes
 * <br> - alternatives
 * @author edgar
 *
 */
public class ScenarioAnnotation {

	static INLPAnalyzer nlpAnalyzer = CoreNLPAnalyzer.getInstance();//singleton
	
	/**
	 * Annotate one structured scenario with NLP info (tokens with POSTAG-STEM, subjects, verbs, objects)
	 * <br> - title
	 * <br> - goal
	 * <br> - episodes
	 * <br> - alternatives
	 * @param scenario
	 * @return
	 */
	public static StructuredScenario annotateScenario(StructuredScenario scenario) {
		StructuredScenario annotatedScenario = new StructuredScenario();
		annotatedScenario.createScenario(scenario.getId(), scenario.getTitle(), scenario.getGoal(), scenario.getInclusionDate(), scenario.getExclusionFlag(), scenario.getId(), "scenario.getProject().getName()");
		annotatedScenario.setContext(scenario.getContext());
		annotatedScenario.setActors(scenario.getActors());
		annotatedScenario.setResources(scenario.getResources());
		annotatedScenario.setEpisodes(scenario.getEpisodes());
		annotatedScenario.setAlternative(scenario.getAlternative());
		
		//Annotate with NLP Info
		CustomSentenceNlpInfo sentenceComponents = nlpAnalyzer.getSentenceComponents(ScenarioCleaner.cleanSentence(scenario.getTitle()));
		annotatedScenario.setTitleNlp(sentenceComponents);
		
		sentenceComponents = nlpAnalyzer.getSentenceComponents(ScenarioCleaner.cleanSentence(scenario.getGoal()));
		annotatedScenario.setGoalNlp(sentenceComponents);
		
		if(!scenario.getEpisodes().isEmpty()) {
			ListIterator<StructuredEpisode> episodeIterator = annotatedScenario.getEpisodes().listIterator();
			while(episodeIterator.hasNext()){
				StructuredEpisode episode = episodeIterator.next();
				if(episode.getSentence() != null && !episode.getSentence().isEmpty()) {
					sentenceComponents = nlpAnalyzer.getSentenceComponents(ScenarioCleaner.cleanSentence(episode.getSentence()));
					episode.setSentenceNlp(sentenceComponents);
					episodeIterator.set(episode);
				}
			}
		}
		
		if(!scenario.getAlternative().isEmpty()) {
			ListIterator<StructuredAlternative> alternativeIterator = annotatedScenario.getAlternative().listIterator();
			while(alternativeIterator.hasNext()){
				StructuredAlternative alternative = alternativeIterator.next();
				if(alternative.getSolution() != null && !alternative.getSolution().isEmpty()) {
					for(String solution : alternative.getSolution()) {
						if(solution != null && !solution.isEmpty()) {
							sentenceComponents = nlpAnalyzer.getSentenceComponents(ScenarioCleaner.cleanSentence(solution));
							alternative.addSolutionNlp(sentenceComponents);								
						} else 
							alternative.addSolutionNlp(null);
					}
					alternativeIterator.set(alternative);
				}
			}
		}
		
		return annotatedScenario;
	}
	
	/**
	 * Annotate a list of structured scenarios with NLP info (tokens with POSTAG-STEM, subjects, verbs, objects)
	 * <br> - title
	 * <br> - goal
	 * <br> - episodes
	 * <br> - alternatives
	 * @param scenario
	 * @return
	 */
	public static List<StructuredScenario> annotateScenario(List<StructuredScenario> scenarios) {
		List<StructuredScenario> annotatedScenarios = new ArrayList<StructuredScenario>();
		for (StructuredScenario scenario : scenarios) 
			annotatedScenarios.add(annotateScenario(scenario));
		
		return annotatedScenarios;
	}
	public static void main(String[] args) {
		

	}

}
