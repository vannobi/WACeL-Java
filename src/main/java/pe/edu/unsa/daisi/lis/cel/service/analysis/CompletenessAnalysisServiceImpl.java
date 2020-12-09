package pe.edu.unsa.daisi.lis.cel.service.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.Defect;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.DefectCategoryEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.DefectIndicatorEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.QualityPropertyEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.ScenarioElement;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredAlternative;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredEpisode;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;
import pe.edu.unsa.daisi.lis.cel.util.DamerauLevenshteinAlgorithm;
import pe.edu.unsa.daisi.lis.cel.util.ListManipulation;
import pe.edu.unsa.daisi.lis.cel.util.RegularExpression;
import pe.edu.unsa.daisi.lis.cel.util.StringManipulation;
import pe.edu.unsa.daisi.lis.cel.util.nlp.CoreNLPAnalyzer;
import pe.edu.unsa.daisi.lis.cel.util.nlp.CustomSentenceNlpInfo;
import pe.edu.unsa.daisi.lis.cel.util.nlp.CustomToken;
import pe.edu.unsa.daisi.lis.cel.util.nlp.INLPAnalyzer;
import pe.edu.unsa.daisi.lis.cel.util.nlp.PosTagEnum;
import pe.edu.unsa.daisi.lis.cel.util.nlp.dictionary.english.Unambiguity;
import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioAnnotation;
import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioCleaner;
import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioParser;

@Service("completenessAnalysisService")

public class CompletenessAnalysisServiceImpl implements ICompletenessAnalysisService{

		
	public static final String EMPTY_CHAR = "";
	public static final String WHITESPACE_CHAR = " ";
	public static final String NON_SEQUENTIAL_GROUP_CHAR = "#";
	public static final String SYSTEM_DEVELOPED = ".*(system|use\\s+case|scenario).*";
	
	
	/**
	 * A scenario express exactly one situation
	 * @param structuredScenario with NLP annotation info
	 * @return
	 */
	public List<Defect> checkAtomicity(StructuredScenario structuredScenario) {
		List<Defect> defects = new ArrayList<Defect>();
		CustomSentenceNlpInfo titleNlp = structuredScenario.getTitleNlp();
		
		//@Episode 1: Check that Title defines exactly one situation (WARNING);
		for (String indicator : Unambiguity.MULTIPLE_WORDS_PHRASES) {
			if (titleNlp != null) {
				//Indicator: The title contains more than one action-verb, subject or object
				for (CustomToken token : titleNlp.getTokens()) {
					if(token.getWord().equals(indicator)) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.ATOMICITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
						defect.setIndicator(DefectIndicatorEnum.ATOMICITY_TITLE_MULTIPLE_SITUATION_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.ATOMICITY_TITLE_MULTIPLE_SITUATION_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						break;
					}
				}
			}
			
			//@Episode 2: Check that Goal defines exactly one purpose (WARNING)
			if (structuredScenario.getGoal() != null && !structuredScenario.getGoal().isEmpty()) {
				//Indicator: The goal contains more than one action-verb, subject or object			
				
			}
		}

		//@Episode 3: Check that Title contains a verb in infinitive (base) form and an object (WARNING)
		//NLP
		if(titleNlp != null) {
			//More that one sentences
			
			//Indicator: Unnecessary Subjects in the title
			if(titleNlp.getSubjects() != null && !titleNlp.getSubjects().isEmpty()) {
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.ATOMICITY.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
				defect.setIndicator(DefectIndicatorEnum.ATOMICITY_TITLE_UNNECESSARY_SUBJECT_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
				defect.setIndicator(defect.getIndicator().replace("<indicator>", titleNlp.getSubjectsAsString()));
				defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.ATOMICITY_TITLE_UNNECESSARY_SUBJECT_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			}
			//Indicator: Missing Object in the title
			if((titleNlp.getDirectObjects() == null || titleNlp.getDirectObjects().isEmpty())
					&& (titleNlp.getIndirectObjects() == null || titleNlp.getIndirectObjects().isEmpty())) {//OK?
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.ATOMICITY.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
				defect.setIndicator(DefectIndicatorEnum.ATOMICITY_TITLE_MISSING_OBJECT_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
				//defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
				defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.ATOMICITY_TITLE_MISSING_OBJECT_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			}
			//Indicator: Missing Action-Verb in the title
			if(titleNlp.getMainActionVerbs() == null || titleNlp.getMainActionVerbs().isEmpty()) {
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.ATOMICITY.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
				defect.setIndicator(DefectIndicatorEnum.ATOMICITY_TITLE_MISSING_ACTION_VERB_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
				//defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
				defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.ATOMICITY_TITLE_MISSING_ACTION_VERB_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			} else {
				//Indicator: The title contains more than one Action-Verb
				if(titleNlp.getMainActionVerbs().entrySet().size() > 1) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.ATOMICITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
					defect.setIndicator(DefectIndicatorEnum.ATOMICITY_TITLE_MORE_THAN_ONE_ACTION_VERB_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", titleNlp.getMainActionVerbsAsString()));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.ATOMICITY_TITLE_MORE_THAN_ONE_ACTION_VERB_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
				}
				//Indicator: Action-Verb in the title is not in INFINITIVE (base) FORM
				for (Map.Entry<Integer, CustomToken> entry : titleNlp.getMainActionVerbs().entrySet()) {
		 		    if(!entry.getValue().getPosTag().equals(PosTagEnum.VB.name()) && !entry.getValue().getPosTag().equals(PosTagEnum.VBP.name())) {
		 		    	Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.ATOMICITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
						defect.setIndicator(DefectIndicatorEnum.ATOMICITY_TITLE_ACTION_VERB_NOT_IN_INFINITIVE_FORM_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", entry.getValue().getWord()));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.ATOMICITY_TITLE_ACTION_VERB_NOT_IN_INFINITIVE_FORM_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
		 		    	break;
		 		    }
		        }
				
			}
		}
		
		return defects;
	}

	/**
	 * A scenario should be as readable as possible
	 * @param structuredScenario with NLP annotation info
	 * @return
	 */
	public List<Defect> checkSimplicity(StructuredScenario structuredScenario) {
		List<Defect> defects = new ArrayList<Defect>();
		//@Episódio 1: Check that Episode-Sentence is described from user point of view (Subject + present simple tense and active form of verb + Object), or by another scenario (infinitive verb - base form + Object) [8][9][24][42] (Warning)
		//NLP
		
		if(!structuredScenario.getEpisodes().isEmpty()) {
			int numEpisode = 1;
			for (StructuredEpisode episode : structuredScenario.getEpisodes()) {
				String episodeId = "Episode number " + numEpisode;
				numEpisode++;	
				if(episode.getId() != null && !episode.getId().isEmpty())
					episodeId = episode.getId(); 
				CustomSentenceNlpInfo episodeNlpInfo = episode.getSentenceNlp();
				if(episodeNlpInfo != null) {
					//Indicator: The episode sentence contains more than one Sentence
					if(episodeNlpInfo.getNumSentences() > 1) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
						defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
						defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_MORE_THAN_ONE_SENTENCE_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_MORE_THAN_ONE_SENTENCE_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
					}
					//Check that sentence reference to another scenario (sub-scenario)
					boolean referenceSubscenario = false;
					String actionVerb = null;
					if(episodeNlpInfo.getMainActionVerbs() != null && !episodeNlpInfo.getMainActionVerbs().isEmpty() && episodeNlpInfo.getMainActionVerbs().size() > 0)
						actionVerb = episodeNlpInfo.getMainActionVerbsAsStringList().get(0);
					if(actionVerb != null && episode.getSentence().contains(actionVerb.toUpperCase())) //FIX: get referenced scenario
						referenceSubscenario = true;
					
					//Indicator: The episode sentence contains more than one Subject
					if(episodeNlpInfo.getSubjects() != null && !episodeNlpInfo.getSubjects().isEmpty() && episodeNlpInfo.getSubjects().size() > 1) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
						defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
						defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_MORE_THAN_ONE_SUBJECT_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", episodeNlpInfo.getSubjectsAsString()));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_MORE_THAN_ONE_SUBJECT_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						
					} else {
					//IF Episode-Sentence does not reference another scenario THEN Check Missing Subject
						//Indicator: Missing Subject in the episode sentence
						if(!referenceSubscenario && (episodeNlpInfo.getSubjects() == null || episodeNlpInfo.getSubjects().isEmpty())) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
							defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_MISSING_SUBJECT_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
							//defect.setIndicator(defect.getIndicator().replace("<indicator>", sentenceComponents.getSubjectsAsString()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_MISSING_SUBJECT_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					}
					//Indicator: Missing Object in the episode sentence
					if((episodeNlpInfo.getDirectObjects() == null || episodeNlpInfo.getDirectObjects().isEmpty())
							&& (episodeNlpInfo.getIndirectObjects() == null || episodeNlpInfo.getIndirectObjects().isEmpty())) {
						//IF subject is different of "system|use case|scenario"
						//Ex. System terminates; use case ends
						boolean isSystemActor = false;
						if(episodeNlpInfo.getSubjects() != null && !episodeNlpInfo.getSubjects().isEmpty()
								&& episodeNlpInfo.getSubjects().size()==1) {
							CustomToken subject = (CustomToken) episodeNlpInfo.getSubjects().entrySet().iterator().next().getValue();
							if(subject.getWord().toLowerCase().matches(SYSTEM_DEVELOPED)) {
								isSystemActor = true;			 		    		
			 		    	}
						}
						if(!isSystemActor) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
							defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_MISSING_OBJECT_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
							//defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_MISSING_OBJECT_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					} 
					//Indicator: Missing Action-Verb in the episode sentence
					if(episodeNlpInfo.getMainActionVerbs() == null || episodeNlpInfo.getMainActionVerbs().isEmpty()) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
						defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
						defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_MISSING_ACTION_VERB_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
						//defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_MISSING_ACTION_VERB_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
					} else {
												
						//Indicator: The episode sentence contains more than one Action-Verb
						if(episodeNlpInfo.getMainActionVerbs().entrySet().size() > 1) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
							defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_MORE_THAN_ONE_ACTION_VERB_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
							defect.setIndicator(defect.getIndicator().replace("<indicator>", episodeNlpInfo.getMainActionVerbsAsString()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_MORE_THAN_ONE_ACTION_VERB_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
							
						}

						for (Map.Entry<Integer, CustomToken> entry : episodeNlpInfo.getMainActionVerbs().entrySet()) {
							//IF Episode-Sentence does not reference another scenario THEN action-verb must be described from user point of view, i.e., the present simple tense and active form;
							//Indicator: The episode sentence contains an Action-verb not in the third form or infinitive form (when reference to other scenario)
							if(!referenceSubscenario) {
								if(!entry.getValue().getPosTag().equals(PosTagEnum.VBZ.name())) {
									Defect defect = new Defect(); 
									defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
									defect.setScenarioId(structuredScenario.getId());
									defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
									defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
									defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_ACTION_VERB_NOT_IN_THIRD_FORM_INDICATOR.getDefectIndicator());
									defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
									defect.setIndicator(defect.getIndicator().replace("<indicator>", entry.getValue().getWord()));
									defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
									defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_ACTION_VERB_NOT_IN_THIRD_FORM_INDICATOR.getFixRecomendation());
									defects.add(defect)	;
									break;
								}
							} else {
							//IF Episode-Sentence reference another scenario THEN action-verb must be in infinite or base form
								//Indicator: The episode sentence references to another scenario and contains an Action-verb not in the infinitive form
								break;
							}
						}
						
						//Indicator: The episode sentence contains more than one Complement-Action-Verb
						if(episodeNlpInfo.getComplementActionVerbs().entrySet().size() > 1) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
							defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_MORE_THAN_ONE_COMPLEMENT_ACTION_VERB_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
							defect.setIndicator(defect.getIndicator().replace("<indicator>", episodeNlpInfo.getComplementActionVerbsAsString()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_MORE_THAN_ONE_COMPLEMENT_ACTION_VERB_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
							
						}
						
						//Indicator: The episode sentence contains more than one Modifier-Action-Verb
						if(episodeNlpInfo.getModifierActionVerbs().entrySet().size() > 1) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
							defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_MORE_THAN_ONE_MODIFIER_ACTION_VERB_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
							defect.setIndicator(defect.getIndicator().replace("<indicator>", episodeNlpInfo.getModifierActionVerbsAsString()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_MORE_THAN_ONE_MODIFIER_ACTION_VERB_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
							
						}
					}
				}
				
			}
		}
		

		//@Episódio 2: Check that Alternative-Solution-Step-Sentence is described: from user point of view (present simple tense and active form of verb + Object), or by another scenario (infinitive base form of verb + Object). Optionally, it contains a Subject [8][9][24][42]; (WARNING);
		//NLP
		if(!structuredScenario.getAlternative().isEmpty()) {
			int numAlternative = 1;
			for (StructuredAlternative alternative : structuredScenario.getAlternative()) {
				String alternativeId = "Alternate/Exception number " + numAlternative;
				numAlternative++;	
				if(alternative.getId() != null && !alternative.getId().isEmpty())
					alternativeId = alternative.getId(); 

				if(alternative.getSolution() != null && !alternative.getSolution().isEmpty()) {
					for (int i = 0; i < alternative.getSolution().size(); i++) {
						String solution = alternative.getSolution().get(i);
						if(solution != null && !solution.isEmpty()) {
							CustomSentenceNlpInfo solutionNlpInfo = alternative.getSolutionNlp().get(i);
							if(solutionNlpInfo != null) {
								//Indicator: The episode sentence contains more than one Sentence
								if(solutionNlpInfo.getNumSentences() > 1) {
									Defect defect = new Defect(); 
									defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
									defect.setScenarioId(structuredScenario.getId());
									defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
									defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
									defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MORE_THAN_ONE_SENTENCE_INDICATOR.getDefectIndicator());
									defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
									defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
									defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MORE_THAN_ONE_SENTENCE_INDICATOR.getFixRecomendation());
									defects.add(defect)	;
								}

								//Check that sentence reference to another scenario (sub-scenario)
								boolean referenceSubscenario = false;
								String actionVerb = null;
								if(solutionNlpInfo.getMainActionVerbs() != null && !solutionNlpInfo.getMainActionVerbs().isEmpty() && solutionNlpInfo.getMainActionVerbs().size() > 0)
									actionVerb = solutionNlpInfo.getMainActionVerbsAsStringList().get(0);
								if(actionVerb != null && solution.contains(actionVerb.toUpperCase()))
									referenceSubscenario = true;

								//Indicator: The alternative solution step contains more than one Subject
								if(solutionNlpInfo.getSubjects() != null && !solutionNlpInfo.getSubjects().isEmpty() && solutionNlpInfo.getSubjects().size() > 1) {
									Defect defect = new Defect(); 
									defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
									defect.setScenarioId(structuredScenario.getId());
									defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
									defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
									defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MORE_THAN_ONE_SUBJECT_INDICATOR.getDefectIndicator());
									defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
									defect.setIndicator(defect.getIndicator().replace("<indicator>", solutionNlpInfo.getSubjectsAsString()));
									defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
									defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MORE_THAN_ONE_SUBJECT_INDICATOR.getFixRecomendation());
									defects.add(defect)	;
								} else {
									//IF Episode-Sentence does not reference another scenario THEN Check Missing Subject

								}
								////Indicator: Missing Object in the alternative solution step
								if((solutionNlpInfo.getDirectObjects() == null || solutionNlpInfo.getDirectObjects().isEmpty())
										&& (solutionNlpInfo.getIndirectObjects() == null || solutionNlpInfo.getIndirectObjects().isEmpty())) {
									//IF subject is different of "system|use case|scenario"
									//Ex. System terminates; use case ends
									boolean isSystemActor = false;
									if(solutionNlpInfo.getSubjects() != null && !solutionNlpInfo.getSubjects().isEmpty()
											&& solutionNlpInfo.getSubjects().size()==1) {
										CustomToken subject = (CustomToken) solutionNlpInfo.getSubjects().entrySet().iterator().next().getValue();
										if(subject.getWord().toLowerCase().matches(SYSTEM_DEVELOPED)) {
											isSystemActor = true;			 		    		
						 		    	}
									}
									if(!isSystemActor) {
										Defect defect = new Defect(); 
										defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
										defect.setScenarioId(structuredScenario.getId());
										defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
										defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
										defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MISSING_OBJECT_INDICATOR.getDefectIndicator());
										defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
										//defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
										defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
										defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MISSING_OBJECT_INDICATOR.getFixRecomendation());
										defects.add(defect)	;
									}
								}
								//Indicator: Missing Action-Verb in the alternative solution step
								if(solutionNlpInfo.getMainActionVerbs() == null || solutionNlpInfo.getMainActionVerbs().isEmpty()) {
									Defect defect = new Defect(); 
									defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
									defect.setScenarioId(structuredScenario.getId());
									defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
									defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
									defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MISSING_ACTION_VERB_INDICATOR.getDefectIndicator());
									defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
									//defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
									defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
									defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MISSING_ACTION_VERB_INDICATOR.getFixRecomendation());
									defects.add(defect)	;
								} else {
									//Indicator: The alternative solution step contains more than one Action-Verb
									if(solutionNlpInfo.getMainActionVerbs().entrySet().size() > 1) {
										Defect defect = new Defect(); 
										defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
										defect.setScenarioId(structuredScenario.getId());
										defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
										defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
										defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MORE_THAN_ONE_ACTION_VERB_INDICATOR.getDefectIndicator());
										defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
										defect.setIndicator(defect.getIndicator().replace("<indicator>", solutionNlpInfo.getMainActionVerbsAsString()));
										defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
										defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MORE_THAN_ONE_ACTION_VERB_INDICATOR.getFixRecomendation());
										defects.add(defect)	;
									}
								
									//Indicator: The alternative solution step contains an Action-verb not in the third or infinitive form 
									for (Map.Entry<Integer, CustomToken> entry : solutionNlpInfo.getMainActionVerbs().entrySet()) {
										if(!entry.getValue().getPosTag().equals(PosTagEnum.VBZ.name()) && !entry.getValue().getPosTag().equals(PosTagEnum.VBP.name()) && !entry.getValue().getPosTag().equals(PosTagEnum.VB.name())) {
											Defect defect = new Defect(); 
											defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
											defect.setScenarioId(structuredScenario.getId());
											defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
											defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
											defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_ACTION_VERB_NOT_IN_THIRD_OR_BASE_FORM_INDICATOR.getDefectIndicator());
											defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
											defect.setIndicator(defect.getIndicator().replace("<indicator>", entry.getValue().getWord()));
											defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
											defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_ACTION_VERB_NOT_IN_THIRD_OR_BASE_FORM_INDICATOR.getFixRecomendation());
											defects.add(defect)	;
											break;
										}

									}
									
									//Indicator: The alternative solution step contains more than one Complement-Action-Verb
									if(solutionNlpInfo.getComplementActionVerbs().entrySet().size() > 1) {
										Defect defect = new Defect(); 
										defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
										defect.setScenarioId(structuredScenario.getId());
										defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
										defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
										defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MORE_THAN_ONE_COMPLEMENT_ACTION_VERB_INDICATOR.getDefectIndicator());
										defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
										defect.setIndicator(defect.getIndicator().replace("<indicator>", solutionNlpInfo.getComplementActionVerbsAsString()));
										defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
										defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MORE_THAN_ONE_COMPLEMENT_ACTION_VERB_INDICATOR.getFixRecomendation());
										defects.add(defect)	;
									}
									
									//Indicator: The alternative solution step contains more than one Modifier-Action-Verb
									if(solutionNlpInfo.getModifierActionVerbs().entrySet().size() > 1) {
										Defect defect = new Defect(); 
										defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
										defect.setScenarioId(structuredScenario.getId());
										defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
										defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
										defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MORE_THAN_ONE_MODIFIER_ACTION_VERB_INDICATOR.getDefectIndicator());
										defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
										defect.setIndicator(defect.getIndicator().replace("<indicator>", solutionNlpInfo.getModifierActionVerbsAsString()));
										defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
										defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_MORE_THAN_ONE_MODIFIER_ACTION_VERB_INDICATOR.getFixRecomendation());
										defects.add(defect)	;
									}
								}
							}
						}
					}
				}
			}
		}
		
		

		//@Episódio 3: Check that Title does not contain extra unnecessary information  (INFORMATION)
		List<String> extraInfTitle = ScenarioCleaner.checkExtraInformation(structuredScenario.getTitle());
		//Indicator: The Title contains <i>unnecessary information
		if (extraInfTitle != null && extraInfTitle.size() > 0 ) {
			Defect defect = new Defect(); 
			defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
			defect.setScenarioId(structuredScenario.getId());
			defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_TITLE_UNNECESSARY_INFORMATION_INDICATOR.getDefectIndicator());
			defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
			String indicators = EMPTY_CHAR;
			for(String indicator : extraInfTitle)
				indicators = indicators + ", " + indicator;
			defect.setIndicator(defect.getIndicator().replace("<indicator>", indicators));

			defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_TITLE_UNNECESSARY_INFORMATION_INDICATOR.getFixRecomendation());
			defects.add(defect)	;

		}

		//INTER-SCENARIO (Leite et al., 2000)
		//@Episódio 6: Check that Episode coincidence only takes place in different situations (WARNING);
		if(!structuredScenario.getEpisodes().isEmpty()) {
			DamerauLevenshteinAlgorithm distanceAlgorithm = new DamerauLevenshteinAlgorithm(1, 1, 1, 1);
			for (int i = 0; i < structuredScenario.getEpisodes().size(); i++) {
				StructuredEpisode episodeI = structuredScenario.getEpisodes().get(i);
				String episodeIId = "Episode number " + i+1;
				if(episodeI.getId() != null && !episodeI.getId().isEmpty())
					episodeIId = episodeI.getId(); 
				
				for (int j = 0; j < structuredScenario.getEpisodes().size(); j++) {
					StructuredEpisode episodeJ = structuredScenario.getEpisodes().get(j);
					String episodeJId = "Episode number " + j+1;
					if(episodeJ.getId() != null && !episodeJ.getId().isEmpty())
						episodeJId = episodeJ.getId(); 
					
					if(i != j) {
						//Indicator: Duplicated Episode Sentence
						int distance = distanceAlgorithm.execute(episodeI.getSentence().toUpperCase(), episodeJ.getSentence().toUpperCase(), 2);
						if (distance < 2) {
							//Create Defect
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeIId));
							defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_DUPLICATED_SENTENCE_INDICATOR.getDefectIndicator());
							String indicators = episodeIId  + WHITESPACE_CHAR + episodeI.getSentence() + " and "+ episodeJId  + WHITESPACE_CHAR + episodeJ.getSentence();
							defect.setIndicator(defect.getIndicator().replace("<indicator>", indicators));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_DUPLICATED_SENTENCE_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
						//Indicator: Duplicated Episode Id/Step
						if(episodeIId.toUpperCase().equals(episodeJId.toUpperCase())) {
							//Create Defect
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.EPISODE_ID.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeIId));
							defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_DUPLICATED_ID_INDICATOR.getDefectIndicator());
							String indicators = episodeIId  + " and "+ episodeJId;
							defect.setIndicator(defect.getIndicator().replace("<indicator>", indicators));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_DUPLICATED_ID_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					}	
					
				}
				
			}
		}
			
		//@Episódio 7: Check that episodes involving validation are described using the verbs "verify" / "validate" / "ensure" / "establish" and followed by "that"; i.e., avoid verbs like "check" / "see" followed by "If" / "Whether". Complicated validation steps can confuse the user and be difficult to undestand. (WARNING) 

		//IF complicated conditional episode THE re-write episode
		if(!structuredScenario.getEpisodes().isEmpty()) {
			int numEpisode = 1;
			for (StructuredEpisode episode : structuredScenario.getEpisodes()) {
				String episodeId = "Episode number " + numEpisode;
				numEpisode++;	
				if(episode.getId() != null && !episode.getId().isEmpty())
					episodeId = episode.getId(); 
				//Indicator: The Episode Sentence involves a validation action and it is hard to understand and follow (contain structures like checks if / see whether)
				if(ScenarioParser.isEpisodeWithComplicatedValidationStep(episode.getRawEpisode())) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
					defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
					defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_COMPLICATED_VALIDATION_SENTENCE_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getRawEpisode()));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_COMPLICATED_VALIDATION_SENTENCE_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
				}
			}
		}
		
		
		//@Episódio 8: Check that nested IF statement is not used in a Conditional Episode, i.e., it can confuse the user and be difficult to read.  (WARNING);
		//Id 1.x
		//Indicator: More than one Episode-Sentence inside a nested IF structure
		String prevEpisodeId = null;
		int numEpisode = 0;
		for(StructuredEpisode episode: structuredScenario.getEpisodes()) {
			if(episode.getId() != null && !episode.getId().trim().isEmpty()) {
				if(numEpisode == 0)
					prevEpisodeId = episode.getId();
				else {
					prevEpisodeId = structuredScenario.getEpisodes().get(numEpisode - 1).getId();
					//Ckeck: next id is included in previous id
					if(prevEpisodeId != null && !prevEpisodeId.trim().isEmpty()) {
						//IDENTIFICAR O EPISODIO QUE TEM MAIS DE UMA SENTENCA
						//AQUELE EPISODIO QUE TEM O ID IGUAL AO INICIO DO ID DO EPISODIO ATUAL (INICIO -> ATE O ". | , | ;"), EX. 1.1 EPISODIO ---> 1. EPISODIO  ANTERIOR 
						String[] episodeId = episode.getId().split(RegularExpression.REGEX_DELIMITING_EPISODE_ID_FROM_SUB_EPISODE_ID);  // split text followed by ". , ;"
						if (episodeId != null && episodeId.length > 1 ) {
							if (episodeId[0].trim().toUpperCase().equals(prevEpisodeId.toUpperCase())) {
								Defect defect = new Defect(); 
								defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
								defect.setScenarioId(structuredScenario.getId());
								defect.setScenarioElement(ScenarioElement.EPISODE_ID.getScenarioElement());
								defect.setScenarioElement(defect.getScenarioElement().replace("<id>", prevEpisodeId));
								defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_EPISODE_NESTED_SENTENCE_INDICATOR.getDefectIndicator());
								String indicators = prevEpisodeId  + " and "+ episode.getId();
								defect.setIndicator(defect.getIndicator().replace("<indicator>", indicators));
								defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
								defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_EPISODE_NESTED_SENTENCE_INDICATOR.getFixRecomendation());
								defects.add(defect)	;
								break;								
							} 
								
						}
					}
				}
			}
			numEpisode++;				
		}
		

		//@Episódio 9: Check that alternate/exception is handled by a simple action [9], i.e, if the interruption is treated by a repeatable sequence of sentences (> 2), this sequence should be extracted to a separate scenario; (WARNING);
		//@Episódio 10: Check that every alternate flow returns to a specific episode of the main flow and an exception finishes the scenario. (WARNING)
		if(structuredScenario.getAlternative() != null && !structuredScenario.getAlternative().isEmpty()) {
			int numAlternative = 1;
			for(StructuredAlternative alternative: structuredScenario.getAlternative()) {
				//Indicator: The Alternate/Exception has too many steps (more than 3)
				String alternativeId = "Alternate/Exception number " + numAlternative;
				if(alternative.getId() != null && !alternative.getId().isEmpty())
					alternativeId = alternative.getId(); 
				if (alternative.getSolution() != null && alternative.getSolution().size() > 3 ) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.ALTERNATIVE_ID.getScenarioElement());
					defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
					defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_COMPLICATED_SOLUTION_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", alternative.getRawAlternative()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", alternative.getSolution().size() + " steps"));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_COMPLICATED_SOLUTION_INDICATOR.getFixRecomendation());
					defects.add(defect)	;								
				}
								
				
				if(!alternative.getSolution().isEmpty() && alternative.getSolutionStepWithGoToEpisode() > 0) {
					//Indicator: The Alternate does not return to the main flow in the last solution step
					if(!alternative.getSolutionStepWithGoToEpisode().equals(alternative.getSolution().size())) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.ALTERNATIVE_ID.getScenarioElement());
						defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
						defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_GOTO_NOT_IN_LAST_ALTERNATE_SOLUTION_STEP_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", alternative.getRawAlternative()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", alternative.getSolutionStepWithGoToEpisode().toString()));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_GOTO_NOT_IN_LAST_ALTERNATE_SOLUTION_STEP_INDICATOR.getFixRecomendation());
						defects.add(defect)	;	
					}
					//Indicator: The Alternate returns to the main flow using an invalid episode Id/Step
					if(alternative.getGoToEpisode() == null) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.ALTERNATIVE_ID.getScenarioElement());
						defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
						defect.setIndicator(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_GOTO_WITHOUT_EPISODE_IN_ALTERNATE_SOLUTION_STEP_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", alternative.getRawAlternative()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", alternative.getSolutionStepWithGoToEpisode().toString()));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.SIMPLICITY_ALTERNATIVE_GOTO_WITHOUT_EPISODE_IN_ALTERNATE_SOLUTION_STEP_INDICATOR.getFixRecomendation());
						defects.add(defect)	;	
					}
				}
				
				

				numAlternative++;				
			}
		}
		
		

		return defects;
	}


	/**
	 * Each scenario element should be described with significant information.
	 * @param structuredScenario
	 * @return
	 */
	public List<Defect> checkUniformity(StructuredScenario structuredScenario) {
		List<Defect> defects = new ArrayList<Defect>();
		//@Episódio 1: Ensure that Title is present [9]; (ERROR)
		//Indicator: Missing Title
		if(structuredScenario.getTitle() == null || structuredScenario.getTitle().isEmpty()) {
			Defect defect = new Defect(); 
			defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
			defect.setScenarioId(structuredScenario.getId());
			defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_TITLE_INDICATOR.getDefectIndicator());
			defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_TITLE_INDICATOR.getFixRecomendation());
			defects.add(defect)	;
		}

		//@Episódio 2: Ensure that Goal is present [9]; (ERROR)
		//Indicator: Missing Goal
		if(structuredScenario.getGoal() == null || structuredScenario.getGoal().isEmpty()) {
			Defect defect = new Defect(); 
			defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
			defect.setScenarioId(structuredScenario.getId());
			defect.setScenarioElement(ScenarioElement.GOAL.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_GOAL_INDICATOR.getDefectIndicator());
			defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_GOAL_INDICATOR.getFixRecomendation());
			defects.add(defect)	;
		}
		//@Episódio 3: Check the existence of more than one Actor per Scenario [9]; (ERROR)
		//Indicator: Missing Actors
		if(structuredScenario.getActors() == null || structuredScenario.getActors().isEmpty() ) { //|| structuredScenario.getActors().size() == 1) {
			Defect defect = new Defect(); 
			defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
			defect.setScenarioId(structuredScenario.getId());
			defect.setScenarioElement(ScenarioElement.ACTORS.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_ACTOR_INDICATOR.getDefectIndicator());
			defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_ACTOR_INDICATOR.getFixRecomendation());
			defects.add(defect)	;
		}

		//@Episódio 4: Check the existence of more than one resource per Scenario [9]; (ERROR)
		//Indicator: Missing Resources
		if(structuredScenario.getResources() == null || structuredScenario.getResources().isEmpty()) {
			Defect defect = new Defect(); 
			defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
			defect.setScenarioId(structuredScenario.getId());
			defect.setScenarioElement(ScenarioElement.RESOURCES.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_RESOURCES_INDICATOR.getDefectIndicator());
			defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_RESOURCES_INDICATOR.getFixRecomendation());
			defects.add(defect)	;
		}
		//@Episódio 5: Ensure that Context contains its relevant sub-components [9]; (ERROR)
		//Indicator: Context does not contain its relevant subcomponents
		if( (structuredScenario.getContext().getPreConditions() == null || structuredScenario.getContext().getPreConditions().isEmpty() )
			&& (structuredScenario.getContext().getPostConditions() == null || structuredScenario.getContext().getPostConditions().isEmpty() )
			&& (structuredScenario.getContext().getTemporalLocation() == null || structuredScenario.getContext().getTemporalLocation().isEmpty() )
			&& (structuredScenario.getContext().getGeographicalLocation() == null || structuredScenario.getContext().getGeographicalLocation().isEmpty() )
				) {
			Defect defect = new Defect(); 
			defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
			defect.setScenarioId(structuredScenario.getId());
			defect.setScenarioElement(ScenarioElement.CONTEXT.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_CONTEXT_SUBCOMPONENTS_INDICATOR.getDefectIndicator());
			defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_CONTEXT_SUBCOMPONENTS_INDICATOR.getFixRecomendation());
			defects.add(defect)	;
		}

		//@Episódio 6: Check the existence of more than one Episode per Scenario [9]; (ERROR)
		//Indicator: Missing Episodes
		if(structuredScenario.getEpisodes() == null || structuredScenario.getEpisodes().isEmpty() || structuredScenario.getEpisodes().size() == 1) {
			Defect defect = new Defect(); 
			defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
			defect.setScenarioId(structuredScenario.getId());
			defect.setScenarioElement(ScenarioElement.EPISODES.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_EPISODES_INDICATOR.getDefectIndicator());
			defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_EPISODES_INDICATOR.getFixRecomendation());
			defects.add(defect)	;
		}
		//@Episódio 7: Ensure that Episode contains its relevant parts [9]; (ERROR)
		int numEpisode = 1;
		int numEpisodesGroupStart = 0; //Episodes delimited by # ... #
		int numEpisodesGroupEnd = 0;
		for(StructuredEpisode episode: structuredScenario.getEpisodes()) {
			String episodeId = "Episode number " + numEpisode;
			if(episode.getId() != null && !episode.getId().isEmpty())
				episodeId = episode.getId(); 
			else { //Indicator: The episode does not contain its relevant parts - Id/Step
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.EPISODE_ID.getScenarioElement());
				defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
				defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_EPISODE_SUBCOMPONENTS_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getRawEpisode()));
				defect.setIndicator(defect.getIndicator().replace("<indicator>", "ID"));
				defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_EPISODE_SUBCOMPONENTS_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			}
			if(episode.getSentence() == null || episode.getSentence().isEmpty()) {
				//Indicator: The episode does not contain its relevant parts - Sentence
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
				defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
				defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_EPISODE_SUBCOMPONENTS_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getRawEpisode()));
				defect.setIndicator(defect.getIndicator().replace("<indicator>", "Sentence"));
				defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_EPISODE_SUBCOMPONENTS_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			} else {
				
				//Count num episodes groups
				if(episode.getSentence().startsWith(NON_SEQUENTIAL_GROUP_CHAR))
					numEpisodesGroupStart++;
				if(episode.getSentence().endsWith(NON_SEQUENTIAL_GROUP_CHAR))
					numEpisodesGroupEnd++;
			}
							
			if(!episode.isSimple())
				if(episode.getConditions() == null || episode.getConditions().isEmpty()) {
					//Indicator: IF episode is not conditional THEN The episode does not contain its relevant parts - Conditions
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.EPISODE_CONDITION.getScenarioElement());
					defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
					defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_EPISODE_SUBCOMPONENTS_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getRawEpisode()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", "Conditions"));
					defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_EPISODE_SUBCOMPONENTS_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
				}
			numEpisode++;				
		}
		//@Episódio 8: Ensure that non-sequential episodes constructs have a begin and an end keywords; (ERROR)
		//Indicator: Incomplete non-sequential construct in non-sequential episodes delimited by # ... #
		if(numEpisodesGroupStart != numEpisodesGroupEnd) {
			Defect defect = new Defect(); 
			defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
			defect.setScenarioId(structuredScenario.getId());
			defect.setScenarioElement(ScenarioElement.EPISODES.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_EPISODES_GROUP_INDICATOR.getDefectIndicator());
			defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_EPISODES_GROUP_INDICATOR.getFixRecomendation());
			defects.add(defect)	;
		}
		//@Episódio 9: Ensure that Alternative contains its relevant parts [9]; (ERROR)
		int numAlternative = 1;
		for(StructuredAlternative alternative: structuredScenario.getAlternative()) {
			String alternativeId = "Alternate/Exception number " + numAlternative;
			if(alternative.getId() != null && !alternative.getId().isEmpty())
				alternativeId = alternative.getId(); 
			else { //indicator: The alternate/exception does not contain its relevant parts - Id/StepRef
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.ALTERNATIVE_ID.getScenarioElement());
				defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
				defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_ALTERNATIVE_SUBCOMPONENTS_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<sentence>", alternative.getRawAlternative()));
				defect.setIndicator(defect.getIndicator().replace("<indicator>", "ID"));
				defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_ALTERNATIVE_SUBCOMPONENTS_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			}
			if(alternative.getSolution() == null || alternative.getSolution().isEmpty()) {
				//indicator: The alternate/exception does not contain its relevant parts -  Solution
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
				defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
				defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_ALTERNATIVE_SUBCOMPONENTS_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<sentence>", alternative.getRawAlternative()));
				defect.setIndicator(defect.getIndicator().replace("<indicator>", "Solution"));
				defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_ALTERNATIVE_SUBCOMPONENTS_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			}
			
			
			if(alternative.getCauses() == null || alternative.getCauses().isEmpty()) {
				//indicator: The alternate/exception does not contain its relevant parts -  Causes
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.ALTERNATIVE_CAUSE.getScenarioElement());
				defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
				defect.setIndicator(DefectIndicatorEnum.UNIFORMITY_MISSING_ALTERNATIVE_SUBCOMPONENTS_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<sentence>", alternative.getRawAlternative()));
				defect.setIndicator(defect.getIndicator().replace("<indicator>", "Causes"));
				defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.UNIFORMITY_MISSING_ALTERNATIVE_SUBCOMPONENTS_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			}
			numAlternative++;				
		}
		return defects;

	}
	/**
	 * A scenario does not contain superfluous information, i.e., there should be consistency among scenario elements
	 * @param structuredScenario
	 * @return
	 */
	public List<Defect> checkUsefulness(StructuredScenario structuredScenario) {
		List<Defect> defects = new ArrayList<Defect>();
		//@Episódio 1: Check that every Actor participates in at least one episode (WARNING)
		//Indicator: Actor does not participate in the situation - Episodes
		for (String actor : structuredScenario.getActors()) {
			String newActor = ScenarioCleaner.cleanSentence(actor).toUpperCase();
			boolean isUsed = false;
			for(StructuredEpisode episode: structuredScenario.getEpisodes()) {
				if(episode.getSentence() != null && !episode.getSentence().isEmpty()) {
					if(episode.getSentence().toUpperCase().contains(newActor)) {
						isUsed = true;
						break;
					}
				}
			}
			if(!isUsed) {
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.USEFULNESS.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.ACTORS.getScenarioElement());
				defect.setIndicator(DefectIndicatorEnum.USEFULNES_UNUSED_ACTOR_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<indicator>", actor));
				defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.USEFULNES_UNUSED_ACTOR_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			}
		}
				
		//@Episódio 2: Check that every Resource is used in at least one episode  (WARNING)
		//Indicator: Resource does not participate in the situation - Episodes
		for (String resource : structuredScenario.getResources()) {
			String newResource = ScenarioCleaner.cleanSentence(resource).toUpperCase();
			boolean isUsed = false;
			for(StructuredEpisode episode: structuredScenario.getEpisodes()) {
				if(episode.getSentence() != null && !episode.getSentence().isEmpty()) {
					if(episode.getSentence().toUpperCase().contains(newResource)) {
						isUsed = true;
						break;
					}
				}
			}
			if(!isUsed) {
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.USEFULNESS.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.RESOURCES.getScenarioElement());
				defect.setIndicator(DefectIndicatorEnum.USEFULNES_UNUSED_RESOURCE_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<indicator>", resource));
				defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.USEFULNES_UNUSED_RESOURCE_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			}
		}
		//@Episódio 3: Check that every Subject mentioned in episodes is an Actor, a Resource  [9] or the System [18][65]  (WARNING)
		//@Episódio 4: Check that every Direct-Object mentioned in episodes is a Resource or Actor [9];  (WARNING)
		//NLP
		int numEpisode = 1;
		for(StructuredEpisode episode: structuredScenario.getEpisodes()) {
			if(episode.getSentence() != null && !episode.getSentence().isEmpty()) {
				String episodeId = "Episode number " + numEpisode;
				numEpisode++;	
				if(episode.getId() != null && !episode.getId().isEmpty())
					episodeId = episode.getId(); 
				CustomSentenceNlpInfo sentenceComponents = episode.getSentenceNlp();
				if(sentenceComponents != null) {
					//Check that sentence reference to another scenario (sub-scenario)
					boolean referenceSubscenario = false;
					String actionVerb = null;
					if(sentenceComponents.getMainActionVerbs() != null && !sentenceComponents.getMainActionVerbs().isEmpty() && sentenceComponents.getMainActionVerbs().size() > 0)
						actionVerb = sentenceComponents.getMainActionVerbsAsStringList().get(0);
					if(actionVerb != null && episode.getSentence().contains(actionVerb.toUpperCase())) //FIX
						referenceSubscenario = true;
					
					//Indicator: The episode sentence contains undeclared Actor 
					boolean declaredSubject = false;//declared as Actor or System
					if(sentenceComponents.getSubjects() != null && !sentenceComponents.getSubjects().isEmpty() && sentenceComponents.getSubjects().size() > 0) {
						for (Map.Entry<Integer, CustomToken> subject : sentenceComponents.getSubjects().entrySet()) {
				 		    for (String actor : structuredScenario.getActors()) {
				 		    	if(actor.toLowerCase().contains(subject.getValue().getWord().toLowerCase())) {
				 		    		declaredSubject = true;
				 		    		break;
				 		    	}
				 		    }
				 		    if(!declaredSubject) {//System?
				 		    	if(subject.getValue().getWord().toLowerCase().matches(SYSTEM_DEVELOPED)) {
				 		    		declaredSubject = true;
				 		    		break;
				 		    	}
				 		    }
				 		    if(!declaredSubject) {
				 		    	for (String resource : structuredScenario.getResources()) {
					 		    	if(resource.toLowerCase().contains(subject.getValue().getWord().toLowerCase())) {
					 		    		declaredSubject = true;
					 		    		break;
					 		    	}
					 		    }
				 		    }
				 		    if(!declaredSubject) { 
				 		    	Defect defect = new Defect(); 
								defect.setQualityProperty(QualityPropertyEnum.USEFULNESS.getQualityProperty());
								defect.setScenarioId(structuredScenario.getId());
								defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
								defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
								defect.setIndicator(DefectIndicatorEnum.USEFULNES_EPISODE_UNDECLARED_ACTOR_INDICATOR.getDefectIndicator());
								defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
								defect.setIndicator(defect.getIndicator().replace("<indicator>", subject.getValue().getWord()));
								if(!referenceSubscenario)
									defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
								else
									defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
								
								defect.setFixRecomendation(DefectIndicatorEnum.USEFULNES_EPISODE_UNDECLARED_ACTOR_INDICATOR.getFixRecomendation());
								defects.add(defect)	;
				 		    }
				        }
						
					} 
					//Indicator: The episode sentence contains undeclared Resource or Actor (Indirect-Object)
					//FIX: VALIDATE
					/*
					boolean declaredObject = false;//declared as resource
					if(sentenceComponents.getDirectObjects() != null && !sentenceComponents.getDirectObjects().isEmpty() && sentenceComponents.getDirectObjects().size() > 0) {
						for (Map.Entry<Integer, CustomToken> object : sentenceComponents.getDirectObjects().entrySet()) {
				 		    for (String resource : structuredScenario.getResources()) {
				 		    	if(resource.toLowerCase().contains(object.getValue().getWord().toLowerCase())) {
				 		    		declaredObject = true;
				 		    		break;
				 		    	}
				 		    }
				 		    //FIX: Actor - to - actor is allowed????
							if(!declaredObject) {
				 		    	for (String actor : structuredScenario.getActors()) {
					 		    	if(actor.toLowerCase().contains(object.getValue().getWord().toLowerCase())) {
					 		    		declaredObject = true;
					 		    		break;
					 		    	}
					 		    }
				 		    }
				 		    if(!declaredObject) {
				 		    	Defect defect = new Defect(); 
								defect.setQualityProperty(QualityPropertyEnum.USEFULNESS.getQualityProperty());
								defect.setScenarioId(structuredScenario.getId());
								defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
								defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
								defect.setIndicator(DefectIndicatorEnum.USEFULNES_EPISODE_UNDECLARED_RESOURCE_INDICATOR.getDefectIndicator());
								defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
								defect.setIndicator(defect.getIndicator().replace("<indicator>", object.getValue().getWord()));
								defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
								defect.setFixRecomendation(DefectIndicatorEnum.USEFULNES_EPISODE_UNDECLARED_RESOURCE_INDICATOR.getFixRecomendation());
								defects.add(defect)	;
				 		    }
				        }
						
					} 
					*/
				}
			}
		}
		
		//@Episódio 5: Check that every Subject mentioned in Alternatives is an Actor, a Resource  [9] or the System [18][65]  (WARNING)
		if(!structuredScenario.getAlternative().isEmpty()) {
			int numAlternative = 1;
			for (StructuredAlternative alternative : structuredScenario.getAlternative()) {
				String alternativeId = "Alternate/Exception number " + numAlternative;
				numAlternative++;	
				if(alternative.getId() != null && !alternative.getId().isEmpty())
					alternativeId = alternative.getId(); 

				if(alternative.getSolution() != null && !alternative.getSolution().isEmpty()) {
					for (int i = 0; i < alternative.getSolution().size(); i++) {
						String solution = alternative.getSolution().get(i);
						if(solution != null && !solution.isEmpty()) {
							CustomSentenceNlpInfo sentenceComponents = alternative.getSolutionNlp().get(i);
							if(sentenceComponents != null) {
								//Check that sentence reference to another scenario (sub-scenario)
								boolean referenceSubscenario = false;
								String actionVerb = null;
								if(sentenceComponents.getMainActionVerbs() != null && !sentenceComponents.getMainActionVerbs().isEmpty() && sentenceComponents.getMainActionVerbs().size() > 0)
									actionVerb = sentenceComponents.getMainActionVerbsAsStringList().get(0);
								if(actionVerb != null && solution.contains(actionVerb.toUpperCase())) //FIX
									referenceSubscenario = true;
								//Indicator: The alternative solution step contains undeclared Actor
								boolean declaredSubject = false;//declared as Actor or System
								if(sentenceComponents.getSubjects() != null && !sentenceComponents.getSubjects().isEmpty() && sentenceComponents.getSubjects().size() > 0) {
									for (Map.Entry<Integer, CustomToken> subject : sentenceComponents.getSubjects().entrySet()) {
										for (String actor : structuredScenario.getActors()) {
											if(actor.toLowerCase().contains(subject.getValue().getWord().toLowerCase())) {
												declaredSubject = true;
												break;
											}
										}
										if(!declaredSubject) {//System?
											if(subject.getValue().getWord().toLowerCase().matches(SYSTEM_DEVELOPED)) {
												declaredSubject = true;
												break;
											}
										}
										if(!declaredSubject) {
											for (String resource : structuredScenario.getResources()) {
												if(resource.toLowerCase().contains(subject.getValue().getWord().toLowerCase())) {
													declaredSubject = true;
													break;
												}
											}
										}
										if(!declaredSubject && !referenceSubscenario) {
											Defect defect = new Defect(); 
											defect.setQualityProperty(QualityPropertyEnum.USEFULNESS.getQualityProperty());
											defect.setScenarioId(structuredScenario.getId());
											defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
											defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
											defect.setIndicator(DefectIndicatorEnum.USEFULNES_ALTERNATIVE_UNDECLARED_ACTOR_INDICATOR.getDefectIndicator());
											defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
											defect.setIndicator(defect.getIndicator().replace("<indicator>", subject.getValue().getWord()));
											defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
											defect.setFixRecomendation(DefectIndicatorEnum.USEFULNES_ALTERNATIVE_UNDECLARED_ACTOR_INDICATOR.getFixRecomendation());
											defects.add(defect)	;
										}
									}
									
								} 
							}
						}
					}
				}
			}
		}
		
		
		
		
		
		
		//@Episódio 6: Ensure that step numbering between the main flow and alternate/exception flow are consistent [41] (WARNING)
		//Indicator: Branching Episode of Alternative is missing
		int numAlternative = 1;
		for(StructuredAlternative alternative: structuredScenario.getAlternative()) {
			String alternativeId = "Alternate/Exception number " + numAlternative;
			if(alternative.getId() != null && !alternative.getId().isEmpty())
				alternativeId = alternative.getId(); 
			if(alternative.getBranchingEpisode() == null || alternative.getBranchingEpisode().getId() == null || alternative.getBranchingEpisode().getId().isEmpty()) {
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.USEFULNESS.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.ALTERNATIVE_ID.getScenarioElement());
				defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
				defect.setIndicator(DefectIndicatorEnum.USEFULNES_ALTERNATIVE_WITHOUT_BRANCHING_EPISODE_INDICATOR.getDefectIndicator());
				defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.USEFULNES_ALTERNATIVE_WITHOUT_BRANCHING_EPISODE_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			}
			
		}
		//@Episódio 7: Check the existence of more than two and less to 10 episodes per scenario [9] [60]  (WARNING)
		//Indicator: Number of episodes in current scenario is less than 3 or more than 9
		if(structuredScenario.getEpisodes() != null && (structuredScenario.getEpisodes().size() < 3 || structuredScenario.getEpisodes().size() > 9)) {
			Defect defect = new Defect(); 
			defect.setQualityProperty(QualityPropertyEnum.USEFULNESS.getQualityProperty());
			defect.setScenarioId(structuredScenario.getId());
			defect.setScenarioElement(ScenarioElement.EPISODES.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.USEFULNES_EPISODES_NOT_BETWEEN_3_AND_9_INDICATOR.getDefectIndicator());
			defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>",  structuredScenario.getEpisodes().size() + " steps"));
			defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.USEFULNES_EPISODES_NOT_BETWEEN_3_AND_9_INDICATOR.getFixRecomendation());
			defects.add(defect)	;
		}
		
		return defects;

	}
	
	/**
	 * Internal scenario elements are semantically coherent, i.e., scenario elements satisfy the scenario goal 
	 * @param structuredScenario
	 * @return
	 */
	public List<Defect> checkConceptuallySoundness(StructuredScenario structuredScenario) {
		List<Defect> defects = new ArrayList<Defect>();
		//@Episódio 1: Check that the Title describes the Goal (WARNING)
		//NLP
		//Indicator: The corresponding verbs and objects in Title and Goal are not the same
		if (isSintacticallySimilar(structuredScenario.getTitleNlp(), structuredScenario.getGoalNlp()) == false) {
			Defect defect = new Defect(); 
			defect.setQualityProperty(QualityPropertyEnum.CONCEPTUALLY_SOUNDNESS.getQualityProperty());
			defect.setScenarioId(structuredScenario.getId());
			defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
			defect.setIndicator(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_TITLE_DO_NOT_DESCRIBE_GOAL_INDICATOR.getDefectIndicator());
			defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getTitle()));
			defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getGoal()));
			defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
			defect.setFixRecomendation(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_TITLE_DO_NOT_DESCRIBE_GOAL_INDICATOR.getFixRecomendation());
			defects.add(defect)	;
		}
		
		//@Episódio 2: Ensure that the set of Episodes satisfies the Goal and is within the Context  (WARNING) (NOT AUTOMATED)
		
	
		//@Episódio 3: Ensure that Episodes contain only actions to be performed  (ERROR)
		//NLP
		//Simplicity indicator:  Missing Action-Verb in the episode sentence
		
		//@Episódio 4: Ensure that Episode condition contains Linking-Verbs (INFORMATION)
		/*
		int numEpisode = 1;
		for(StructuredEpisode episode: structuredScenario.getEpisodes()) {
			String episodeId = "Episode number " + numEpisode;
			if(episode.getId() != null && !episode.getId().isEmpty())
				episodeId = episode.getId(); 
			if(!episode.isSimple() && (episode.getConditions() != null && !episode.getConditions().isEmpty())) {
				boolean containsLinkingVerb = false;
				for(String condition : episode.getConditions()) {
					String upperCondition = PreProcessText.cleanSentence(condition).toUpperCase();
					for(String linkingVerb : SpecialVerb.LINKING_VERBS) {
						String regExpWord = RegularExpression.REGEX_PUNCTUATION_AT_BEGIN_SENTENCE + linkingVerb.toUpperCase() + RegularExpression.REGEX_PUNCTUATION_AT_END_SENTENCE; 
						String newCondition = upperCondition.replaceAll(regExpWord, EMPTY_CHAR);
						if(!upperCondition.equals(newCondition)) {
							containsLinkingVerb = true;
							break;
						}
					}
					if(containsLinkingVerb) 
						break;
					
				}
				if(!containsLinkingVerb) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.CONCEPTUALLY_SOUNDNESS.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.EPISODE_CONDITION.getScenarioElement());
					defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
					defect.setIndicator(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_EPISODE_MISSING_LINKING_VERB_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<indicator>", episode.getConditions().toString()));
					defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_EPISODE_MISSING_LINKING_VERB_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
				}
			}
			//Ensure that Pre-conditions contain State-Verbs (INFORMATION)
			if(episode.getPreConditions() != null && !episode.getPreConditions().isEmpty()) {
				boolean containsStateVerb = false;
				for(String condition : episode.getPreConditions()) {
					String upperCondition = PreProcessText.cleanSentence(condition).toUpperCase();
					for(String stateVerb : SpecialVerb.STATE_VERBS) {
						String regExpWord = RegularExpression.REGEX_PUNCTUATION_AT_BEGIN_SENTENCE + stateVerb.toUpperCase() + RegularExpression.REGEX_PUNCTUATION_AT_END_SENTENCE; 
						String newCondition = upperCondition.replaceAll(regExpWord, EMPTY_CHAR);
						if(!upperCondition.equals(newCondition)) {
							containsStateVerb = true;
							break;
						}
					}
					if(containsStateVerb)
						break;
					
				}
				if(!containsStateVerb) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.CONCEPTUALLY_SOUNDNESS.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.EPISODE_PRE_CONDITION.getScenarioElement());
					defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
					defect.setIndicator(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_PRE_CONDITION_MISSING_STATE_VERB_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<indicator>", episode.getPreConditions().toString()));
					defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_PRE_CONDITION_MISSING_STATE_VERB_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
				}
			}
			//Ensure that Post-conditions contain State-Verbs (INFORMATION)
			if(episode.getPostConditions() != null && !episode.getPostConditions().isEmpty()) {
				boolean containsStateVerb = false;
				for(String condition : episode.getPostConditions()) {
					String upperCondition = PreProcessText.cleanSentence(condition).toUpperCase();
					for(String stateVerb : SpecialVerb.STATE_VERBS) {
						String regExpWord = RegularExpression.REGEX_PUNCTUATION_AT_BEGIN_SENTENCE + stateVerb.toUpperCase() + RegularExpression.REGEX_PUNCTUATION_AT_END_SENTENCE; 
						String newCondition = upperCondition.replaceAll(regExpWord, EMPTY_CHAR);
						if(!upperCondition.equals(newCondition)) { 
							containsStateVerb = true;
							break;
						}
					}
					if(containsStateVerb)
						break;
				
				}
				if(!containsStateVerb) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.CONCEPTUALLY_SOUNDNESS.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.EPISODE_POST_CONDITION.getScenarioElement());
					defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
					defect.setIndicator(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_POST_CONDITION_MISSING_STATE_VERB_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<indicator>", episode.getPostConditions().toString()));
					defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_POST_CONDITION_MISSING_STATE_VERB_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
				}
			}
			
		}
		*/
		//@Episódio 5: Ensure that Pre-conditions contain State-Verbs (INFORMATION)
		//For episode in previous episode
		/*
		if(structuredScenario.getContext().getPreConditions() != null && !structuredScenario.getContext().getPreConditions().isEmpty()) {
			boolean containsStateVerb = false;
			for(String condition : structuredScenario.getContext().getPreConditions()) {
				String upperCondition = PreProcessText.cleanSentence(condition).toUpperCase();
				for(String stateVerb : SpecialVerb.STATE_VERBS) {
					String regExpWord = RegularExpression.REGEX_PUNCTUATION_AT_BEGIN_SENTENCE + stateVerb.toUpperCase() + RegularExpression.REGEX_PUNCTUATION_AT_END_SENTENCE; 
					String newCondition = upperCondition.replaceAll(regExpWord, EMPTY_CHAR);
					if(!upperCondition.equals(newCondition)) {
						containsStateVerb = true;
						break;
					}
				}
				if(containsStateVerb)
					break;
				
			}
			if(!containsStateVerb) {
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.CONCEPTUALLY_SOUNDNESS.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.CONTEXT_PRE_CONDITION.getScenarioElement());
				defect.setIndicator(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_PRE_CONDITION_MISSING_STATE_VERB_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<indicator>", structuredScenario.getContext().getPreConditions().toString()));
				defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_PRE_CONDITION_MISSING_STATE_VERB_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			}
		}
		*/
		//@Episódio 6: Ensure that Post-conditions contain State-Verbs (INFORMATION)
		//For episode in previous episode
		/*
		if(structuredScenario.getContext().getPostConditions() != null && !structuredScenario.getContext().getPostConditions().isEmpty()) {
			boolean containsStateVerb = false;
			for(String condition : structuredScenario.getContext().getPostConditions()) {
				String upperCondition = PreProcessText.cleanSentence(condition).toUpperCase();
				for(String stateVerb : SpecialVerb.STATE_VERBS) {
					String regExpWord = RegularExpression.REGEX_PUNCTUATION_AT_BEGIN_SENTENCE + stateVerb.toUpperCase() + RegularExpression.REGEX_PUNCTUATION_AT_END_SENTENCE; 
					String newCondition = upperCondition.replaceAll(regExpWord, EMPTY_CHAR);
					if(!upperCondition.equals(newCondition)) {
						containsStateVerb = true;
						break;
					}
				}
				if(containsStateVerb) 
					break;
				
			}
			if(!containsStateVerb) {
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.CONCEPTUALLY_SOUNDNESS.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.CONTEXT_POST_CONDITION.getScenarioElement());
				defect.setIndicator(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_POST_CONDITION_MISSING_STATE_VERB_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<indicator>", structuredScenario.getContext().getPostConditions().toString()));
				defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_POST_CONDITION_MISSING_STATE_VERB_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			}
		}
		*/
		
		//@Episódio 7: Ensure that Alternatives contain only actions to be performed (WARNING)
		//NLP
		//Simplicity indicator: Missing Action-Verb in the alternative solution step
		
		//@Episódio 8: Ensure that Alternative cause contains Linking-Verbs or State-Verbs (INFORMATION)
		/*
		int numAlternative = 1;
		for(StructuredAlternative alternative: structuredScenario.getAlternative()) {
			String alternativeId = "Alternate/Exception number " + numAlternative;
			if(alternative.getId() != null && !alternative.getId().isEmpty())
				alternativeId = alternative.getId(); 
					
			if(alternative.getCauses() != null && !alternative.getCauses().isEmpty()) {
				boolean containsStateVerb = false;
				boolean containsLinkingVerb = false;
				for(String cause : alternative.getCauses()) {
					if(cause != null && !cause.isEmpty()) {
						String upperCause = PreProcessText.cleanSentence(cause).toUpperCase();
						
						for(String stateVerb : SpecialVerb.STATE_VERBS) {
							String regExpWord = RegularExpression.REGEX_PUNCTUATION_AT_BEGIN_SENTENCE + stateVerb.toUpperCase() + RegularExpression.REGEX_PUNCTUATION_AT_END_SENTENCE;
							String newCause =  upperCause.replaceAll(regExpWord, EMPTY_CHAR);		
							if(!upperCause.equals(newCause)) {
								containsStateVerb = true;
								break;
							}
						}
						
						for(String linkingVerb : SpecialVerb.LINKING_VERBS) {
							String regExpWord = RegularExpression.REGEX_PUNCTUATION_AT_BEGIN_SENTENCE + linkingVerb.toUpperCase() + RegularExpression.REGEX_PUNCTUATION_AT_END_SENTENCE; 
							String newCause =  upperCause.replaceAll(regExpWord, EMPTY_CHAR);		
							if(!upperCause.equals(newCause)) {
								containsLinkingVerb = true;
								break;
							}
						}
						if(containsStateVerb || containsLinkingVerb) {
							break;
						}
						
					}
				}
				if(!containsStateVerb && !containsLinkingVerb) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.CONCEPTUALLY_SOUNDNESS.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.ALTERNATIVE_CAUSE.getScenarioElement());
					defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
					defect.setIndicator(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_ALTERNATIVE_MISSING_STATE_OR_LINKING_VERB_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<indicator>", alternative.getCauses().toString()));
					defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.CONCEPTUALLY_SOUNDNESS_ALTERNATIVE_MISSING_STATE_OR_LINKING_VERB_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
				}
			}
			numAlternative++;				
		}
		*/
		return defects;
	}

	
	/**
	 * Whenever a scenario includes an explicit relationship on another scenario, the related scenario should exist as another scenario within the set of scenarios. 
	 * @param structuredScenario
	 * @param sequentiallyRelatedScenarios sequentially related scenarios
	 * @param sequentiallyRelatedScenariosByPrePost Pre-condition (not described as another scenario) of a scenario (Context) should be satisfied by a Post-condition of other scenario
	 * @return
	 */
	public List<Defect> checkIntegrity(StructuredScenario structuredScenario, List<StructuredScenario> sequentiallyRelatedScenarios, List<StructuredScenario> sequentiallyRelatedScenariosByPrePost) {
		List<Defect> defects = new ArrayList<Defect>();
		//@Episódio 1: Check that every included scenario (Pre-condition, Post-condition, Episode sentence, Alternative solution, Constraint) exists within the set of scenarios [9]; (ERROR)
		//Indicator: Pre-condition references to a scenario that does not exist within the set of scenarios
		if(structuredScenario.getContext().getPreConditions() != null && !structuredScenario.getContext().getPreConditions().isEmpty()) {
			for(String condition : structuredScenario.getContext().getPreConditions()) {
				//REFERENCED SCENARIOS MUST BE UPPERCASE PHRASES
				List<String> includedScenarios = StringManipulation.getUpperCasePhrases(condition);
				if(includedScenarios != null && !includedScenarios.isEmpty()) {
					for(String includedScenario : includedScenarios) {
						boolean existRelatedScenario = false;
						if(sequentiallyRelatedScenarios != null && !sequentiallyRelatedScenarios.isEmpty()) {
							for(StructuredScenario scenario : sequentiallyRelatedScenarios) {
								if(includedScenario.equals(scenario.getTitle().toUpperCase())) {
									existRelatedScenario = true;
									break;
								}
							}
						}
						if(!existRelatedScenario) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.INTEGRITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.CONTEXT_PRE_CONDITION.getScenarioElement());
							defect.setIndicator(DefectIndicatorEnum.INTEGRITY_SCENARIO_PRE_CONDITION_NOT_EXIST_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<indicator>", includedScenario));
							defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.INTEGRITY_SCENARIO_PRE_CONDITION_NOT_EXIST_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					}
				}
			}
		}
		
		//Indicator: Post-condition references to a scenario that does not exist within the set of scenarios
		if(structuredScenario.getContext().getPostConditions() != null && !structuredScenario.getContext().getPostConditions().isEmpty()) {
			for(String condition : structuredScenario.getContext().getPostConditions()) {
				//REFERENCED SCENARIOS MUST BE UPPERCASE PHRASES
				List<String> includedScenarios = StringManipulation.getUpperCasePhrases(condition);
				if(includedScenarios != null && !includedScenarios.isEmpty()) {
					for(String includedScenario : includedScenarios) {
						boolean existRelatedScenario = false;
						if(sequentiallyRelatedScenarios != null && !sequentiallyRelatedScenarios.isEmpty()) {
							for(StructuredScenario scenario : sequentiallyRelatedScenarios) {
								if(includedScenario.equals(scenario.getTitle().toUpperCase())) {
									existRelatedScenario = true;
									break;
								}
							}
						}
						if(!existRelatedScenario) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.INTEGRITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.CONTEXT_POST_CONDITION.getScenarioElement());
							defect.setIndicator(DefectIndicatorEnum.INTEGRITY_SCENARIO_POST_CONDITION_NOT_EXIST_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<indicator>", includedScenario));
							defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.INTEGRITY_SCENARIO_POST_CONDITION_NOT_EXIST_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					}
				}
			}
		}
		
		//CONSTRAINT ?
		
		//Indicator: Episode sentence references to a scenario that does not exist within the set of scenarios
		int numEpisode = 1;
		for(StructuredEpisode episode: structuredScenario.getEpisodes()) {
			String episodeId = "Episode number " + numEpisode;
			if(episode.getId() != null && !episode.getId().isEmpty())
				episodeId = episode.getId(); 
			
			if(episode.getSentence() != null && !episode.getSentence().isEmpty()) {
				String sentence = episode.getSentence();
				//REFERENCED SCENARIOS MUST BE UPPERCASE PHRASES
				List<String> includedScenarios = StringManipulation.getUpperCasePhrases(sentence);
				if(includedScenarios != null && !includedScenarios.isEmpty()) {
					for(String includedScenario : includedScenarios) {
						boolean existRelatedScenario = false;
						if(sequentiallyRelatedScenarios != null && !sequentiallyRelatedScenarios.isEmpty()) {
							for(StructuredScenario scenario : sequentiallyRelatedScenarios) {
								if(includedScenario.equals(scenario.getTitle().toUpperCase())) {
									existRelatedScenario = true;
									break;
								}
							}
						}
						if(!existRelatedScenario) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.INTEGRITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
							defect.setIndicator(DefectIndicatorEnum.INTEGRITY_SCENARIO_EPISODE_NOT_EXIST_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<indicator>", includedScenario));
							defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.INTEGRITY_SCENARIO_EPISODE_NOT_EXIST_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					}
				}
				
			} 
			numEpisode++;				
		}
		
		//Indicator: Alternative solution step references to a scenario that does not exist within the set of scenarios
		int numAlternative = 1;
		for(StructuredAlternative alternative: structuredScenario.getAlternative()) {
			String alternativeId = "Alternate/Exception number " + numAlternative;
			if(alternative.getId() != null && !alternative.getId().isEmpty())
				alternativeId = alternative.getId(); 
					
			if(alternative.getSolution() != null && !alternative.getSolution().isEmpty()) {
				for(String solution : alternative.getSolution()) {
					if(solution != null && !solution.isEmpty()) {
						//REFERENCED SCENARIOS MUST BE UPPERCASE PHRASES
						List<String> includedScenarios = StringManipulation.getUpperCasePhrases(solution);
						if(includedScenarios != null && !includedScenarios.isEmpty()) {
							for(String includedScenario : includedScenarios) {
								boolean existRelatedScenario = false;
								if(sequentiallyRelatedScenarios != null && !sequentiallyRelatedScenarios.isEmpty()) {
									for(StructuredScenario scenario : sequentiallyRelatedScenarios) {
										if(includedScenario.equals(scenario.getTitle().toUpperCase())) {
											existRelatedScenario = true;
											break;
										}
									}
								}
								if(!existRelatedScenario) {
									Defect defect = new Defect(); 
									defect.setQualityProperty(QualityPropertyEnum.INTEGRITY.getQualityProperty());
									defect.setScenarioId(structuredScenario.getId());
									defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
									defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
									defect.setIndicator(DefectIndicatorEnum.INTEGRITY_SCENARIO_ALTERNATIVE_NOT_EXIST_INDICATOR.getDefectIndicator());
									defect.setIndicator(defect.getIndicator().replace("<indicator>", includedScenario));
									defect.setDefectCategory(DefectCategoryEnum.ERROR.getDefectCategory());
									defect.setFixRecomendation(DefectIndicatorEnum.INTEGRITY_SCENARIO_ALTERNATIVE_NOT_EXIST_INDICATOR.getFixRecomendation());
									defects.add(defect)	;
								}
							}
						}
					}
				}
			}
			numAlternative++;				
		}
		

		//@Episódio 2: Check that a Pre-condition (not described as another scenario) of a scenario is satisfied by a Post-condition of other scenario (pre-condition/post-condition relationship [9]);  (INFORMATION)
		//Indicator: Missing scenario Post-condition that satisfies the current Pre-condition
		if(structuredScenario.getContext().getPreConditions() != null && !structuredScenario.getContext().getPreConditions().isEmpty()) {
			for(String preCondition : structuredScenario.getContext().getPreConditions()) {
				//BUSCAR REFERENCIA NA LISTA DE CENARIOS RELACIONADOS
				boolean existRelatedScenario  = false;
				boolean existPostConditionForPreCondition  = false;
				if(sequentiallyRelatedScenarios != null && !sequentiallyRelatedScenarios.isEmpty()) {
					for(StructuredScenario scenario : sequentiallyRelatedScenarios) {
						if(preCondition.toUpperCase().contains(scenario.getTitle().toUpperCase())) {
							existRelatedScenario = true;
							break;
						}
					}
				}
				//SE REFERENCIA NAO EXISTE, BUSCAR NOS RELACIONAMENTOS POS-CONDICAO -> PRE-CONDICAO
				if(!existRelatedScenario) {
					if(sequentiallyRelatedScenariosByPrePost != null && !sequentiallyRelatedScenariosByPrePost.isEmpty()) {
						for(StructuredScenario relatedScenario : sequentiallyRelatedScenariosByPrePost) {
							if(relatedScenario.getContext().getPostConditions() != null  && !relatedScenario.getContext().getPostConditions().isEmpty()) {
								for(String postCondition : relatedScenario.getContext().getPostConditions()) {
									if(preCondition.toUpperCase().equals(postCondition.toUpperCase())) {
										existPostConditionForPreCondition = true;
										break;
									}
								}
							}

						}
					}
				}
				//SE PRE-CONDICAO NAO E SATISFEITA POR UMA POST-CONDICAO
				if(!existPostConditionForPreCondition) {

					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.INTEGRITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.CONTEXT_PRE_CONDITION.getScenarioElement());
					defect.setIndicator(DefectIndicatorEnum.INTEGRITY_SCENARIO_PRE_CONDITIONS_DONOT_PERFORMED_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<indicator>", preCondition));
					defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.INTEGRITY_SCENARIO_PRE_CONDITIONS_DONOT_PERFORMED_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
				}

			}
		}
		return defects;
	}
	
	/**
	 * Internal elements of explicitly related scenarios should be precise and use a common terminology, e.g. pre-conditions of sub-scenarios are coherent with main scenario pre-conditions.  
	 * @param structuredScenario
	 * @param sequentiallyRelatedScenarios sequentially related scenarios
	 * @return
	 */
	public List<Defect> checkCoherency(StructuredScenario structuredScenario, List<StructuredScenario> sequentiallyRelatedScenarios) {
		List<Defect> defects = new ArrayList<Defect>();
		//@Episódio 1: Check coherence between the related scenario Pre-conditions and the main scenario Pre-conditions [9]; (WARNING)
		//NOT AUTOMATED

		//@Episódio 2: Check that Geographical and Temporal location of the related scenarios are equal or more restricted than those of the main scenario [9]; (WARNING)
		//Indicator: Related scenario Geographical location is not in the set of Geographical locations of the main scenario
		if(sequentiallyRelatedScenarios != null && !sequentiallyRelatedScenarios.isEmpty()) {
			for(StructuredScenario subScenario : sequentiallyRelatedScenarios) {
				//FIND Geographical location IN MAIN SCENARIO
				boolean existGeogLocation  = false;
				if(subScenario.getContext().getGeographicalLocation() != null && !subScenario.getContext().getGeographicalLocation().isEmpty()) {
					for(String subLocation : subScenario.getContext().getGeographicalLocation()) {
						if(structuredScenario.getContext().getGeographicalLocation() != null && !structuredScenario.getContext().getGeographicalLocation().isEmpty()) {
							for(String mainLocation : structuredScenario.getContext().getGeographicalLocation()) {
								if(subLocation.toUpperCase().contains(mainLocation.toUpperCase())) {
									existGeogLocation = true;
									break;
								}
							}
						}
						if(!existGeogLocation) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.COHERENCY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.CONTEXT_GEOGRAPHICAL_LOCATION.getScenarioElement());
							defect.setIndicator(DefectIndicatorEnum.COHERENCY_GEOGRAPHICAL_LOCATIONS_RELATED_SCENARIO_NOT_IN_MAIN_SCENARIO_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getTitle()));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subLocation));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getTitle()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.COHERENCY_GEOGRAPHICAL_LOCATIONS_RELATED_SCENARIO_NOT_IN_MAIN_SCENARIO_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					}
				}
				
			}
		}
		
		//Indicator: Related scenario Temporal location is not in the set of Geographical locations of the main scenario
		if(sequentiallyRelatedScenarios != null && !sequentiallyRelatedScenarios.isEmpty()) {
			for(StructuredScenario subScenario : sequentiallyRelatedScenarios) {
				//FIND Temporal location IN MAIN SCENARIO
				boolean existTemporalLocation  = false;
				if(subScenario.getContext().getTemporalLocation() != null && !subScenario.getContext().getTemporalLocation().isEmpty()) {
					for(String subLocation : subScenario.getContext().getTemporalLocation()) {
						if(structuredScenario.getContext().getTemporalLocation() != null && !structuredScenario.getContext().getTemporalLocation().isEmpty()) {
							for(String mainLocation : structuredScenario.getContext().getTemporalLocation()) {
								if(subLocation.toUpperCase().contains(mainLocation.toUpperCase())) {
									existTemporalLocation = true;
									break;
								}
							}
						}
						if(!existTemporalLocation) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.COHERENCY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.CONTEXT_TEMPORAL_LOCATION.getScenarioElement());
							defect.setIndicator(DefectIndicatorEnum.COHERENCY_TEMPORAL_LOCATIONS_RELATED_SCENARIO_NOT_IN_MAIN_SCENARIO_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getTitle()));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subLocation));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getTitle()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.COHERENCY_TEMPORAL_LOCATIONS_RELATED_SCENARIO_NOT_IN_MAIN_SCENARIO_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					}
				}
				
			}
		}
		
		//@Episódio 3: Check that every referenced scenario does not reference the main scenario [37]; (WARNING)
		//FIND REFERENCES TO THE MAIN SCENARIO IN SUB-SCENARIOS
		String mainScenarioTitle = structuredScenario.getTitle().toUpperCase();
		if(sequentiallyRelatedScenarios != null && !sequentiallyRelatedScenarios.isEmpty()) {
			for(StructuredScenario subScenario : sequentiallyRelatedScenarios) {	
				if(!structuredScenario.getId().equals(subScenario.getId())) {//FIX: is it Ok?
					//Indicator: Circular inclusion: The related scenario reference in its description {Context pre-condition} to the main scenario
					if(subScenario.getContext().getPreConditions() != null && !subScenario.getContext().getPreConditions().isEmpty()) {
						boolean existReferenceToMainScenario = false;
						for(String preCondition : subScenario.getContext().getPreConditions()) {
							if(preCondition.toUpperCase().contains(mainScenarioTitle)) {
								existReferenceToMainScenario = true;
								break;
							}
						}
						if(existReferenceToMainScenario) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.COHERENCY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.CONTEXT_PRE_CONDITION.getScenarioElement());
							defect.setIndicator(DefectIndicatorEnum.COHERENCY_SCENARIOS_CIRCULAR_INCLUSION_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getTitle()));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", ScenarioElement.CONTEXT_PRE_CONDITION.getScenarioElement()));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getTitle()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.COHERENCY_SCENARIOS_CIRCULAR_INCLUSION_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					}
					//Indicator: Circular inclusion: The related scenario reference in its description {Context post-condition} to the main scenario
					if(subScenario.getContext().getPostConditions() != null && !subScenario.getContext().getPostConditions().isEmpty()) {
						boolean existReferenceToMainScenario = false;
						for(String postCondition : subScenario.getContext().getPostConditions()) {
							if(postCondition.toUpperCase().contains(mainScenarioTitle)) {
								existReferenceToMainScenario = true;
								break;
							}
						}
						if(existReferenceToMainScenario) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.COHERENCY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.CONTEXT_POST_CONDITION.getScenarioElement());
							defect.setIndicator(DefectIndicatorEnum.COHERENCY_SCENARIOS_CIRCULAR_INCLUSION_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getTitle()));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", ScenarioElement.CONTEXT_POST_CONDITION.getScenarioElement()));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getTitle()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.COHERENCY_SCENARIOS_CIRCULAR_INCLUSION_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					}
					//Indicator: Circular inclusion: The related scenario reference in its description {Resource} to the main scenario
					if(subScenario.getResources() != null && !subScenario.getResources().isEmpty()) {
						boolean existReferenceToMainScenario = false;
						for(String resource : subScenario.getResources()) {
							if(resource.toUpperCase().contains(mainScenarioTitle)) {
								existReferenceToMainScenario = true;
								break;
							}
						}
						if(existReferenceToMainScenario) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.COHERENCY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.RESOURCES.getScenarioElement());
							defect.setIndicator(DefectIndicatorEnum.COHERENCY_SCENARIOS_CIRCULAR_INCLUSION_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getTitle()));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", ScenarioElement.RESOURCES.getScenarioElement()));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getTitle()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.COHERENCY_SCENARIOS_CIRCULAR_INCLUSION_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					}
					
					//Indicator: Circular inclusion: The related scenario reference in its description {Episode} to the main scenario
					if(subScenario.getEpisodes() != null && !subScenario.getEpisodes().isEmpty()) {
						boolean existReferenceToMainScenario = false;
						for(StructuredEpisode episode : subScenario.getEpisodes()) {
							if(episode.getSentence().toUpperCase().contains(mainScenarioTitle)) {
								existReferenceToMainScenario = true;
								break;
							}
						}
						if(existReferenceToMainScenario) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.COHERENCY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.EPISODES.getScenarioElement());
							defect.setIndicator(DefectIndicatorEnum.COHERENCY_SCENARIOS_CIRCULAR_INCLUSION_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getTitle()));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", ScenarioElement.EPISODES.getScenarioElement()));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getTitle()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.COHERENCY_SCENARIOS_CIRCULAR_INCLUSION_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					}
	
					//Indicator: Circular inclusion: The related scenario reference in its description {Alternative} to the main scenario
					if(subScenario.getAlternative() != null && !subScenario.getAlternative().isEmpty()) {
						boolean existReferenceToMainScenario = false;
						for(StructuredAlternative alternative : subScenario.getAlternative()) {
							if(alternative.getSolution() != null && !alternative.getSolution().isEmpty())
								for(String solution : alternative.getSolution()) {
									if(solution != null && !solution.isEmpty()) {
										if(solution.toUpperCase().contains(mainScenarioTitle)) {
											existReferenceToMainScenario = true;
											break;
										}
									}
								}
							if(existReferenceToMainScenario)
								break;
						}
						if(existReferenceToMainScenario) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.COHERENCY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.ALTERNATIVE.getScenarioElement());
							defect.setIndicator(DefectIndicatorEnum.COHERENCY_SCENARIOS_CIRCULAR_INCLUSION_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getTitle()));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", ScenarioElement.ALTERNATIVE.getScenarioElement()));
							defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getTitle()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.COHERENCY_SCENARIOS_CIRCULAR_INCLUSION_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}
					}
				}
			}
		}
		return defects;
	}
	
	/**
	 * A scenario is unique when no other scenario is the same or too similar, i.e., duplicates are avoided because they are source of inconsistencies  
	 * @param structuredScenario
	 * @param scenarios all project scenarios 
	 * @return
	 */
	public List<Defect> checkUniqueness(StructuredScenario structuredScenario, List<StructuredScenario> scenarios) {
		List<Defect> defects = new ArrayList<Defect>();
		
		DamerauLevenshteinAlgorithm distanceAlgorithm = new DamerauLevenshteinAlgorithm(1, 1, 1, 1);
		String mainScenarioTitle = structuredScenario.getTitle().toUpperCase();
		String mainScenarioGoal = structuredScenario.getGoal().toUpperCase();
		List<String> mainScenarioPreconditions = new ArrayList<>();
		List<String> mainScenarioEpisodeSentences = new ArrayList<>();
		if(structuredScenario.getContext().getPreConditions() != null && !structuredScenario.getContext().getPreConditions().isEmpty()) {
			mainScenarioPreconditions = structuredScenario.getContext().getPreConditions();
		}
		if(structuredScenario.getEpisodes() != null && !structuredScenario.getEpisodes().isEmpty()) {
			for(StructuredEpisode episode : structuredScenario.getEpisodes())
				mainScenarioEpisodeSentences.add(episode.getSentence());
		}
		
		if(scenarios != null && !scenarios.isEmpty()) {
			for(StructuredScenario subScenario : scenarios) {	
				subScenario = ScenarioAnnotation.annotateScenario(subScenario);
				if(!structuredScenario.getId().equals(subScenario.getId())) {
					//@Episódio 1: Check that the Title of a scenario is not already included in another scenario; (WARNING)
					//Indicator: The main scenario and another scenario have similar Titles
					int distance = distanceAlgorithm.execute(mainScenarioTitle, subScenario.getTitle().toUpperCase(), 2);
					if (distance < 2) {
						//Create Defect
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.UNIQUENESS.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
						defect.setIndicator(DefectIndicatorEnum.UNIQUENESS_SCENARIOS_TITLE_COINCIDENCE_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getTitle()));
						defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getTitle()));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.UNIQUENESS_SCENARIOS_TITLE_COINCIDENCE_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
					}
					//@Episódio 2: Check that the Goal of a scenario is not already included in another scenario; (WARNING)
					//Indicator: The main scenario and another scenario have similar Goals
					distance = distanceAlgorithm.execute(mainScenarioGoal, subScenario.getGoal().toUpperCase(), 2);
					if (distance < 2) {
						//Create Defect
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.UNIQUENESS.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.GOAL.getScenarioElement());
						defect.setIndicator(DefectIndicatorEnum.UNIQUENESS_SCENARIOS_GOAL_COINCIDENCE_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getTitle()));
						defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getTitle()));
						defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getGoal()));
						defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getGoal()));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.UNIQUENESS_SCENARIOS_GOAL_COINCIDENCE_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
					}
					//@Episódio 3: Check that the Context Pre-condition of a scenario is not already included in another scenario; (WARNING)
					//Indicator: The main scenario and another scenario have similar Pre-conditions
					if(!mainScenarioPreconditions.isEmpty()) {
						if(subScenario.getContext().getPreConditions() != null && !subScenario.getContext().getPreConditions().isEmpty()) {
							if(ListManipulation.isSubset(mainScenarioPreconditions, subScenario.getContext().getPreConditions() )) {
								Defect defect = new Defect(); 
								defect.setQualityProperty(QualityPropertyEnum.UNIQUENESS.getQualityProperty());
								defect.setScenarioId(structuredScenario.getId());
								defect.setScenarioElement(ScenarioElement.CONTEXT_PRE_CONDITION.getScenarioElement());
								defect.setIndicator(DefectIndicatorEnum.UNIQUENESS_SCENARIOS_CONTEXT_PRE_CONDITIONS_COINCIDENCE_INDICATOR.getDefectIndicator());
								defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getTitle()));
								defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getTitle()));
								defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getContext().getPreConditions().toString()));
								defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getContext().getPreConditions().toString()));
								defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
								defect.setFixRecomendation(DefectIndicatorEnum.UNIQUENESS_SCENARIOS_CONTEXT_PRE_CONDITIONS_COINCIDENCE_INDICATOR.getFixRecomendation());
								defects.add(defect)	;
							}
						}
					}
					
					//@Episódio 4: Check that the set of Episodes of a scenario is not included in another scenario; (WARNING)
					//Indicator: The main scenario and another scenario have similar Episodes
					if(!mainScenarioEpisodeSentences.isEmpty()) {
						if(subScenario.getEpisodes() != null && !subScenario.getEpisodes().isEmpty()) {
							List<String> subScenarioEpisodeSentences = new ArrayList<>();
							for(StructuredEpisode episode : subScenario.getEpisodes()) {
								subScenarioEpisodeSentences.add(episode.getSentence());
							}
							if(ListManipulation.isSubset(mainScenarioEpisodeSentences, subScenarioEpisodeSentences )) {
								Defect defect = new Defect(); 
								defect.setQualityProperty(QualityPropertyEnum.UNIQUENESS.getQualityProperty());
								defect.setScenarioId(structuredScenario.getId());
								defect.setScenarioElement(ScenarioElement.EPISODES.getScenarioElement());
								defect.setIndicator(DefectIndicatorEnum.UNIQUENESS_SCENARIOS_EPISODES_COINCIDENCE_INDICATOR.getDefectIndicator());
								defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", structuredScenario.getTitle()));
								defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getTitle()));
								defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", mainScenarioEpisodeSentences.toString()));
								defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenarioEpisodeSentences.toString()));
								defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
								defect.setFixRecomendation(DefectIndicatorEnum.UNIQUENESS_SCENARIOS_EPISODES_COINCIDENCE_INDICATOR.getFixRecomendation());
								defects.add(defect)	;
							}
						}
					}	
					
					//@Episódio 5: Check the syntactic similarity of a scenario (Title) with other scenarios; (WARNING)
					//NLP
					//Indicator: Syntactic Similarity: The main scenario and another scenario share the same Action-Verbs and the Direct-Objects
					if (isSintacticallySimilar(structuredScenario.getTitleNlp(), subScenario.getTitleNlp()) == true) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.UNIQUENESS.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
						defect.setIndicator(DefectIndicatorEnum.UNIQUENESS_SCENARIOS_TITLE_SYNTACTIC_SAME_VERB_OBJECT_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", mainScenarioTitle));
						defect.setIndicator(defect.getIndicator().replaceFirst("<indicator>", subScenario.getTitle().toUpperCase()));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.UNIQUENESS_SCENARIOS_TITLE_SYNTACTIC_SAME_VERB_OBJECT_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
					}
					//@Episódio 6: Check the semantic similarity of a scenario (Title) with other scenarios; (WARNING)
					//NLP
					//WordNet
					//Indicator: Semantic Similarity: The main scenario and another scenario share Action-Verbs and the Direct-Objects in synonymous forms
				}						
							
			}
		}
		
		
		
		
		return defects;
	}
	
	/**
	 * It is possible to perform each operation described in a scenario and each internal/external condition is not violated 
	 * @param structuredScenario
	 * @return
	 */
	public List<Defect> checkFeasibility(StructuredScenario structuredScenario) {
		List<Defect> defects = new ArrayList<Defect>();
		//@Episódio 1: Check that is possible to derive an initial system design from the current scenario and its relationships [30]; (ERROR)

		//@Episódio 2: Check that initial system design does not contain isolated sub-systems; (ERROR)
		
		return defects;
	}
	
	
	/**
	 * 
	 * @param structuredScenario
	 * @param sequentiallyRelatedScenarios sequentially related scenarios
	 * @param sequentiallyRelatedScenariosByPrePost Pre-condition (not described as another scenario) of a scenario (Context) should be satisfied by a Post-condition of other scenario
	 * @param scenarios all project scenarios 
	 **/
	public List<Defect> analyze(StructuredScenario structuredScenario, List<StructuredScenario> sequentiallyRelatedScenarios, List<StructuredScenario> sequentiallyRelatedScenariosByPrePost, List<StructuredScenario> scenarios) {
		List<Defect> defects = new ArrayList<Defect>();

		defects.addAll(checkAtomicity(structuredScenario));
		defects.addAll(checkSimplicity(structuredScenario));
		defects.addAll(checkUniformity(structuredScenario));
		defects.addAll(checkUsefulness(structuredScenario));
		defects.addAll(checkConceptuallySoundness(structuredScenario));
		
		if(sequentiallyRelatedScenarios != null) {
			if(sequentiallyRelatedScenariosByPrePost != null)
				defects.addAll(checkIntegrity(structuredScenario, sequentiallyRelatedScenarios, sequentiallyRelatedScenariosByPrePost));
			defects.addAll(checkCoherency(structuredScenario, sequentiallyRelatedScenarios));
		}
		if(scenarios != null) 
			defects.addAll(checkUniqueness(structuredScenario, scenarios));
		
		defects.addAll(checkFeasibility(structuredScenario));

		return defects;
	}
	
	/**
	 * Measure Syntactic Similarity between two sentences (used to evaluate duplicity and coherency) by comparing action-verbs and direct objects
	 * <br/>
	 * IF two sentences (tokens, subjects, verbs, objects) have the same Action-Verb AND Total-Matching-Objects / Total-Distinct-Objects > 0 THEN They are potentially duplicated 
	 * @param sentenceNlp
	 * @param otherSentenceNlp
	 * @return
	 */
	private boolean isSintacticallySimilar(CustomSentenceNlpInfo sentenceNlp, CustomSentenceNlpInfo otherSentenceNlp) {
		//Get sentences components from texts 
		
		//Get action-verbs in the sentences components
		HashMap<Integer, CustomToken> verbsSentence = null;
		HashMap<Integer, CustomToken> verbsOtherSentence = null;
		//Get Objects in the sentences components
		HashMap<Integer, CustomToken> objectsSentence = null;
		HashMap<Integer, CustomToken> objectsOtherSentence = null;
		if(sentenceNlp != null && otherSentenceNlp != null) {
			objectsSentence = sentenceNlp.getDirectObjects();
			objectsSentence.putAll(sentenceNlp.getIndirectObjects());
			objectsOtherSentence = otherSentenceNlp.getDirectObjects();
			objectsOtherSentence.putAll(otherSentenceNlp.getIndirectObjects());
			
			verbsSentence = sentenceNlp.getMainActionVerbs();
			verbsSentence.putAll(sentenceNlp.getComplementActionVerbs());
			verbsSentence.putAll(sentenceNlp.getModifierActionVerbs());
			verbsOtherSentence = otherSentenceNlp.getMainActionVerbs();
			verbsOtherSentence.putAll(otherSentenceNlp.getComplementActionVerbs());
			verbsOtherSentence.putAll(otherSentenceNlp.getModifierActionVerbs());

		} else {
			return false;
		}

		//Similarity metric m/p > 0
		double totalDistinctObjects = 0;//Union
		double totalMatchingObjects = 0;//Intersection
		double totalObjects = 0;//with no duplicate Objects in two sentences
		//Remove duplicate objects in two sentences: 
		HashMap<String, CustomToken> unionObjectsWithoutDupl = new HashMap<String, CustomToken>(); //two sentences
		HashMap<String, CustomToken> objectsSentenceWithoutDupl = new HashMap<String, CustomToken>(); //sentence
		HashMap<String, CustomToken> objectsOtherSentenceWithoutDupl = new HashMap<String, CustomToken>(); //other_sentence
		if (objectsSentence != null && !objectsSentence.isEmpty() && objectsOtherSentence != null && !objectsOtherSentence.isEmpty()){
			for (Map.Entry<Integer, CustomToken> object : objectsSentence.entrySet()) {
				//singulars
				String singularNoun = object.getValue().getStem().toLowerCase();
				objectsSentenceWithoutDupl.put(singularNoun, object.getValue());				
			}
			for (Map.Entry<Integer, CustomToken> object : objectsOtherSentence.entrySet()) {
				//singulars
				String singularNoun = object.getValue().getStem().toLowerCase();
				objectsOtherSentenceWithoutDupl.put(singularNoun, object.getValue());				
			}
			//Union objects of two sentences
			unionObjectsWithoutDupl.putAll(objectsSentenceWithoutDupl);
			if (objectsOtherSentenceWithoutDupl != null && !objectsOtherSentenceWithoutDupl.isEmpty()){
				for (Map.Entry<String, CustomToken> otherObject : objectsOtherSentenceWithoutDupl.entrySet()) {
					boolean sameObject = false;
					for (Map.Entry<String, CustomToken> object : unionObjectsWithoutDupl.entrySet()) {
						if (object.getKey().endsWith(otherObject.getKey()) || otherObject.getKey().endsWith(object.getKey())){
							sameObject = true;
							break;
						}	
					}	
					if(!sameObject)
						unionObjectsWithoutDupl.put(otherObject.getKey(), otherObject.getValue());

				}
			}			
			//unionObjectsWithoutDupl.putAll(objectsOtherSentenceWithoutDupl);
			
		}

		//Get total objects between the two sentences
		totalObjects = objectsSentenceWithoutDupl.size() + objectsOtherSentenceWithoutDupl.size();
		//Get total distinct objects between the two sentences ('p')
		totalDistinctObjects = unionObjectsWithoutDupl.size();

		//Get total matching objects between the two sentences ('m')
		totalMatchingObjects = totalObjects - totalDistinctObjects;


		//Check that they have the same action-verb
		boolean sameActionVerb = false;
		if (verbsSentence != null && !verbsSentence.isEmpty() && verbsOtherSentence != null && !verbsOtherSentence.isEmpty()){
			for (Map.Entry<Integer, CustomToken> verb : verbsSentence.entrySet()) {
				for (Map.Entry<Integer, CustomToken> otherVerb : verbsOtherSentence.entrySet()) {
					if (verb.getValue().getStem().toLowerCase().equals(otherVerb.getValue().getStem().toLowerCase())){
						sameActionVerb = true;
						break;
					}	
				}	

				if (sameActionVerb){
					break;
				}	
			}
		}	

		//IF they have the same action-verb and common objects THEN they are potentially duplicated
		if (sameActionVerb && ((totalDistinctObjects > 0 && (totalMatchingObjects/totalDistinctObjects) > 0 ) || totalDistinctObjects == 0))
			return true;
		else 
			return false;

	}

	/**
	 * Measure  Semantic between two sentences (used to evaluate duplicity and coherency) by comparing action-verbs (or their synonyms) and direct objects (or their synonyms) 
	 * <br/>
	 * IF two sentences (tokens, subjetcs, verbs, objects) have the same Action-Verb AND Total-Matching-Objects / Total-Distinct-Objects > 0 THEN They are pontentially duplicated 
	 * @resource WordNet
	 * @param sentence
	 * @param otherSentence
	 * @return
	 */
	private boolean isSemanticallySimilar(CustomSentenceNlpInfo sentence, CustomSentenceNlpInfo otherSentence) {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
