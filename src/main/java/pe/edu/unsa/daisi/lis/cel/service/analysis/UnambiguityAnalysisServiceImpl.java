package pe.edu.unsa.daisi.lis.cel.service.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.Defect;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.DefectCategoryEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.DefectIndicatorEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.QualityPropertyEnum;
import pe.edu.unsa.daisi.lis.cel.domain.model.analysis.ScenarioElement;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredEpisode;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredAlternative;
import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured.StructuredScenario;
import pe.edu.unsa.daisi.lis.cel.util.RegularExpression;
import pe.edu.unsa.daisi.lis.cel.util.StringManipulation;
import pe.edu.unsa.daisi.lis.cel.util.nlp.CustomSentenceNlpInfo;
import pe.edu.unsa.daisi.lis.cel.util.nlp.CustomToken;
import pe.edu.unsa.daisi.lis.cel.util.nlp.PosTagEnum;
import pe.edu.unsa.daisi.lis.cel.util.nlp.dictionary.english.Unambiguity;
import pe.edu.unsa.daisi.lis.cel.util.nlp.readability.Readability;
import pe.edu.unsa.daisi.lis.cel.util.nlp.readability.enums.MetricType;
import pe.edu.unsa.daisi.lis.cel.util.scenario.preprocess.ScenarioCleaner;

/**
 * 
 * @author Edgar
 *
 */
@Service("unambiguityAnalysisService")
public class UnambiguityAnalysisServiceImpl implements IUnambiguityAnalysisService{
	public static final String WHITE_SPACE = " ";
	@Override
	public List<Defect> analyze(StructuredScenario structuredScenario) {
		List<Defect> defects = new ArrayList<Defect>();
		//@Episode 1: Check Readability index (Coleman-Liau,  Flesch-Kincaid, Automated Readability Index - ARI) (WARNING) - Title;
		//Indicator: The sentence is difficult-to-read
		/*
		if (structuredScenario.getTitle() != null && !structuredScenario.getTitle().isEmpty()) {
			Readability readability = new Readability(structuredScenario.getTitle());
			if (readability.getMetric(MetricType.COLEMAN_LIAU) > 14 || readability.getMetric(MetricType.FLESCH_KINCAID) > 14 || readability.getMetric(MetricType.ARI) > 14 ) {
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.READABILITY.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
				defect.setIndicator(DefectIndicatorEnum.READABILITY_DIFFICULT_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<sentence>", ScenarioElement.TITLE.getScenarioElement() +": "+ structuredScenario.getTitle()));
				String readabilityIndex = "<br>" +
										  "Coleman Liau Index = " + readability.getMetric(MetricType.COLEMAN_LIAU)+
										  "<br>" +
										  "Flesch-Kincaid Index = " + readability.getMetric(MetricType.FLESCH_KINCAID)+
										  "<br>" +
										  "ARI Index = " + readability.getMetric(MetricType.ARI);
				
				defect.setIndicator(defect.getIndicator().replace("<indicator>", readabilityIndex));
				defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.READABILITY_DIFFICULT_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
			}
		}
		*/
		//@Episode 2: Check the Vagueness of scenario Title, Goal (WARNING);
		for (String indicator : Unambiguity.VAGUE_WORDS_PHRASES) {
			String regExpWord = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + indicator + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT;
			String replacement = "<mark>" + indicator + "</mark>"; 
			if (structuredScenario.getTitle() != null && !structuredScenario.getTitle().isEmpty()) {
				//Indicator: The Title contains vague words or phrases
				String title = ScenarioCleaner.cleanSentence(structuredScenario.getTitle().toLowerCase());
				String newTitle = title.replaceAll(regExpWord, replacement);
				if (!newTitle.equals(title)) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.VAGUENESS.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
					defect.setIndicator(DefectIndicatorEnum.VAGUENESS_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.VAGUENESS_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
					break;
				}	
			}	
			if (structuredScenario.getGoal() != null && !structuredScenario.getGoal().isEmpty()) {
				//Indicator: The Goal contains vague words or phrases		
				String goal = ScenarioCleaner.cleanSentence(structuredScenario.getGoal().toLowerCase());
				String newGoal = goal.replaceAll(regExpWord, replacement);
				if (!newGoal.equals(goal)) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.VAGUENESS.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.GOAL.getScenarioElement());
					defect.setIndicator(DefectIndicatorEnum.VAGUENESS_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getGoal()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.VAGUENESS_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
					break;
				}	
			}
		}

		//@Episode 3: Check the Subjectiveness of scenario Title, Goal (WARNING);
		//NLP POS tagging
		CustomSentenceNlpInfo titleNlp = structuredScenario.getTitleNlp();
		for(CustomToken token : titleNlp.getTokens()) {
			//Indicator: The Title contains words like comparative/superlative adverbs/adjectives				
			if (token.getPosTag().equals(PosTagEnum.JJR.name()) || token.getPosTag().equals(PosTagEnum.JJS.name())
					|| token.getPosTag().equals(PosTagEnum.RBR.name()) || token.getPosTag().equals(PosTagEnum.RBS.name())) {
				
				String indicator = token.getWord();
				
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.SUBJECTIVENESS.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
				defect.setIndicator(DefectIndicatorEnum.SUBJECTIVENESS_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
				defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
				defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.SUBJECTIVENESS_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
				break;
			}	
		}
		CustomSentenceNlpInfo goalNlp = structuredScenario.getGoalNlp();
		for(CustomToken token : goalNlp.getTokens()) {
			////Indicator: The Goal contains words like comparative/superlative adverbs/adjectives		
			if (token.getPosTag().equals(PosTagEnum.JJR.name()) || token.getPosTag().equals(PosTagEnum.JJS.name())
					|| token.getPosTag().equals(PosTagEnum.RBR.name()) || token.getPosTag().equals(PosTagEnum.RBS.name())) {
				
				String indicator = token.getWord();
					
				Defect defect = new Defect(); 
				defect.setQualityProperty(QualityPropertyEnum.SUBJECTIVENESS.getQualityProperty());
				defect.setScenarioId(structuredScenario.getId());
				defect.setScenarioElement(ScenarioElement.GOAL.getScenarioElement());
				defect.setIndicator(DefectIndicatorEnum.SUBJECTIVENESS_INDICATOR.getDefectIndicator());
				defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getGoal()));
				defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
				defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
				defect.setFixRecomendation(DefectIndicatorEnum.SUBJECTIVENESS_INDICATOR.getFixRecomendation());
				defects.add(defect)	;
				break;
			}	
		}
		
		
		//@Episode 4: Check the Optionality of scenario Title, Goal (WARNING);
		for (String indicator : Unambiguity.OPTION_WORDS_PHRASES) {
			String regExpWord = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + indicator + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT; 
			String replacement = "<mark>" + indicator + "</mark>"; 
			if (structuredScenario.getTitle() != null && !structuredScenario.getTitle().isEmpty()) {
				//Indicator: The Title contains words that express optionality	
				String title = ScenarioCleaner.cleanSentence(structuredScenario.getTitle().toLowerCase());
				String newTitle = title.replaceAll(regExpWord, replacement);
				if (!newTitle.equals(title)) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.OPTIONALITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
					defect.setIndicator(DefectIndicatorEnum.OPTIONALITY_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.OPTIONALITY_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
					break;
				}	
			}	
			if (structuredScenario.getGoal() != null && !structuredScenario.getGoal().isEmpty()) {
				//Indicator: The Goal contains words that express optionality	
				String goal = ScenarioCleaner.cleanSentence(structuredScenario.getGoal().toLowerCase());
				String newGoal = goal.replaceAll(regExpWord, replacement);
				if (!newGoal.equals(goal)) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.OPTIONALITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.GOAL.getScenarioElement());
					defect.setIndicator(DefectIndicatorEnum.OPTIONALITY_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getGoal()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.OPTIONALITY_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
					break;
				}	
			}
		}
		//@Episode 5: Check the Weakness of scenario Title, Goal (WARNING);
		for (String indicator : Unambiguity.WEAK_WORDS_PHRASES) {
			if(titleNlp != null) {
				for (CustomToken token : titleNlp.getTokens()) {
					if(token.getWord().equals(indicator)) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.WEAKNESS.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
						defect.setIndicator(DefectIndicatorEnum.WEAKNESS_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.WEAKNESS_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						break;
					}
				}
			}
			if(goalNlp != null) {
				for (CustomToken token : goalNlp.getTokens()) {
					if(token.getWord().equals(indicator)) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.WEAKNESS.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.GOAL.getScenarioElement());
						defect.setIndicator(DefectIndicatorEnum.WEAKNESS_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getGoal()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.WEAKNESS_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						break;
					}
				}							
			}
		}
		
		//@Episode 6: Check the Implicit of scenario Title, Goal (WARNING);
		for (String indicator : Unambiguity.IMPLICIT_WORDS_PHRASES) {
			String regExpWord = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + indicator + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT; 
			String replacement = "<mark>" + indicator + "</mark>";
			if (structuredScenario.getTitle() != null && !structuredScenario.getTitle().isEmpty()) {
				//Indicator: The Title does not specify the subject or object by means of its specific name but uses pronoun or indirect reference			
				String title = ScenarioCleaner.cleanSentence(structuredScenario.getTitle().toLowerCase());
				String newTitle = title.replaceAll(regExpWord, replacement);
				if (!newTitle.equals(title)) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.IMPLICITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
					defect.setIndicator(DefectIndicatorEnum.IMPLICITY_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.IMPLICITY_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
					break;
				}	
			}			
		}
		
		
		if(goalNlp != null) {
			//Indicator: The Goal contains implicit subjects 
			if(goalNlp.getSubjects() != null && !goalNlp.getSubjects().isEmpty()) {
				List<String> strSubjects = goalNlp.getSubjectsAsStringList();
				if(goalNlp.getModifierSubjects() != null && !goalNlp.getModifierSubjects().isEmpty())
					strSubjects.addAll(goalNlp.getModifierSubjectsAsStringList());
				
				List<String> strIndicators = new ArrayList<String>();
				for(String subject : strSubjects) {
					for (String indicator : Unambiguity.IMPLICIT_WORDS_PHRASES) {
						if (subject.equals(indicator)) {
							strIndicators.add(subject);
						}
					}					
				}
										
				if (!strIndicators.isEmpty()) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.IMPLICITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.GOAL.getScenarioElement());
					defect.setIndicator(DefectIndicatorEnum.IMPLICITY_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getGoal()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", strIndicators.toString()));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.IMPLICITY_INDICATOR.getFixRecomendation());
					defects.add(defect)	;						
				}	
			}
			
			//Indicator: The Goal contains implicit objects 
			if(goalNlp.getDirectObjects() != null && !goalNlp.getDirectObjects().isEmpty()) {
				List<String> strObjects = goalNlp.getDirectObjectsAsStringList();
				if(goalNlp.getIndirectObjects() != null && !goalNlp.getIndirectObjects().isEmpty())
					strObjects.addAll(goalNlp.getIndirectObjectsAsStringList());
				
				List<String> strIndicators = new ArrayList<String>();
				for(String object : strObjects) {
					for (String indicator : Unambiguity.IMPLICIT_WORDS_PHRASES) {
						if (object.equals(indicator)) {
							strIndicators.add(object);
						}
					}					
				}
										
				if (!strIndicators.isEmpty()) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.IMPLICITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.GOAL.getScenarioElement());
					defect.setIndicator(DefectIndicatorEnum.IMPLICITY_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getGoal()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", strIndicators.toString()));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.IMPLICITY_INDICATOR.getFixRecomendation());
					defects.add(defect)	;		
				}	
			}
				
		}
		
		//@Episode 7: Check the Quantifiability of scenario Title, Goal (INFO);
		for (String indicator : Unambiguity.QUANTITY_WORDS_PHRASES) {
			if (titleNlp != null) {
				for (CustomToken token : titleNlp.getTokens()) {
					if(token.getWord().equals(indicator)) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.QUANTIFIABILITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
						defect.setIndicator(DefectIndicatorEnum.QUANTIFIABILITY_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.QUANTIFIABILITY_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						break;
					}
				}
			}
			if (goalNlp != null) {
				for (CustomToken token : goalNlp.getTokens()) {
					if(token.getWord().equals(indicator)) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.QUANTIFIABILITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.GOAL.getScenarioElement());
						defect.setIndicator(DefectIndicatorEnum.QUANTIFIABILITY_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getGoal()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.QUANTIFIABILITY_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						break;
					}
				}
			}
		}

		//@Episode 8: Check the Multiplicity of scenario Title, Goal (WARNING);
		for (String indicator : Unambiguity.MULTIPLE_WORDS_PHRASES) {
			//Indicator: The Title has more than one main verb or subject 
			if (titleNlp != null) {
				for (CustomToken token : titleNlp.getTokens()) {
					if(token.getWord().equals(indicator)) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.MULTIPLICITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
						defect.setIndicator(DefectIndicatorEnum.MULTIPLICITY_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.MULTIPLICITY_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						break;
					}
				}
			}
			if (goalNlp != null) {
				//Indicator: The Goal has more than one main verb or subject 		

			}
		}
		//@Episode 9: Check the Minimality of scenario Title, Goal (WARNING);
		for (String indicator : Unambiguity.NON_MINIMAL_WORDS_PHRASES) {
			if (titleNlp != null) {
				for (CustomToken token : titleNlp.getTokens()) {
					if(token.getWord().equals(indicator)) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.MINIMALITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
						defect.setIndicator(DefectIndicatorEnum.NON_MINIMALITY_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.NON_MINIMALITY_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						break;
					}
				}
			}
			if (goalNlp != null) {
				for (CustomToken token : goalNlp.getTokens()) {
					if(token.getWord().equals(indicator)) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.MINIMALITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.GOAL.getScenarioElement());
						defect.setIndicator(DefectIndicatorEnum.NON_MINIMALITY_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getGoal()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.NON_MINIMALITY_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						break;
					}
				}
			}
			
			/*
			String regExpWord = RegularExpression.REGEX_ANY_CHARACTER + indicator  + RegularExpression.REGEX_WHITE_SPACE + RegularExpression.REGEX_ANY_CHARACTER; 
			String replacement = "<mark>" + indicator + "</mark>";
			if (structuredScenario.getTitle() != null && !structuredScenario.getTitle().isEmpty()) {
				//Indicator: The Title contains a Text after a dot, hyphen, semicolon or other punctuation mark			
				String title = ScenarioCleaner.cleanSentence(structuredScenario.getTitle().toLowerCase());
				String newTitle = title.replaceAll(regExpWord, replacement);
				if (!newTitle.equals(title)) {
					newTitle = structuredScenario.getTitle().replaceAll(indicator, replacement);
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.MINIMALITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.TITLE.getScenarioElement());
					defect.setIndicator(DefectIndicatorEnum.NON_MINIMALITY_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getTitle()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.NON_MINIMALITY_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
					break;
				}	
			}	
			if (structuredScenario.getGoal() != null && !structuredScenario.getGoal().isEmpty()) {
				//Indicator: The Goal contains a Text after a dot, hyphen, semicolon or other punctuation mark	
				String goal = ScenarioCleaner.cleanSentence(structuredScenario.getGoal().toLowerCase());
				String newGoal = goal.replaceAll(regExpWord, replacement);
				if (!newGoal.equals(goal)) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.MINIMALITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.GOAL.getScenarioElement());
					defect.setIndicator(DefectIndicatorEnum.NON_MINIMALITY_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", structuredScenario.getGoal()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.NON_MINIMALITY_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
					break;
				}	
			}
			*/
		}

		//@Episode 10: Check Episodes (WARNING);
		int numEpisode = 1;
		for(StructuredEpisode episode: structuredScenario.getEpisodes()) {
			
			CustomSentenceNlpInfo episodeNlpInfo = episode.getSentenceNlp();
			
			//@Episode 10.0: Create ID - Episode;
			numEpisode++;
			String episodeId = "Episode number " + numEpisode;
			if(episode.getId() != null && !episode.getId().isEmpty())
				episodeId = episode.getId(); 
			
			
			//@Episode 10.1: Check Readability index (Coleman-Liau,  Flesch-Kincaid, Automated Readability Index - ARI) (WARNING) - Episode;
			//Indicator: The sentence is difficult-to-read
			
			//@Episode 10.2: Check the Vagueness of scenario Episode (WARNING);
			for (String indicator : Unambiguity.VAGUE_WORDS_PHRASES) {
				String regExpWord = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + indicator + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT; 
				String replacement = "<mark>" + indicator + "</mark>";
				if (episode.getSentence() != null && !episode.getSentence().isEmpty()) {
					//Indicator: The Episode Sentence contains vague words or phrases
					String sentence  = ScenarioCleaner.cleanSentence(episode.getSentence().toLowerCase());
					String newSentence = sentence.replaceAll(regExpWord, replacement);
					if (!newSentence.equals(sentence)) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.VAGUENESS.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
						defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
						defect.setIndicator(DefectIndicatorEnum.VAGUENESS_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.VAGUENESS_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						break;
					}	
				}	
				
			}

			//@Episode 10.3: Check the Subjectiveness of scenario Episode (WARNING);
			//NLP POS tagging
			if (episodeNlpInfo != null) {
				for(CustomToken token : episodeNlpInfo.getTokens()) {
					//Indicator: The Episode sentence contains words like comparative/superlative adverbs/adjectives		
					if (token.getPosTag().equals(PosTagEnum.JJR.name()) || token.getPosTag().equals(PosTagEnum.JJS.name())
							|| token.getPosTag().equals(PosTagEnum.RBR.name()) || token.getPosTag().equals(PosTagEnum.RBS.name())) {

						String indicator = token.getWord();
						
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.SUBJECTIVENESS.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
						defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
						defect.setIndicator(DefectIndicatorEnum.SUBJECTIVENESS_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.SUBJECTIVENESS_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						break;
					}	
				}
			}


			//@Episode 10.4: Check the Optionality of scenario Episode (WARNING);
			for (String indicator : Unambiguity.OPTION_WORDS_PHRASES) {
				String regExpWord = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + indicator + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT; 
				String replacement = "<mark>" + indicator + "</mark>";
				if (episode.getSentence() != null && !episode.getSentence().isEmpty()) {
					//Indicator: The Episode sentence contains words that express optionality	
					String sentence  = ScenarioCleaner.cleanSentence(episode.getSentence().toLowerCase());
					String newSentence = sentence.replaceAll(regExpWord, replacement);
					if (!newSentence.equals(sentence)) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.OPTIONALITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
						defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
						defect.setIndicator(DefectIndicatorEnum.OPTIONALITY_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.OPTIONALITY_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						break;
					}	
				}	
				
			}
			//@Episode 10.5: Check the Weakness of scenario Episode (WARNING);
			for (String indicator : Unambiguity.WEAK_WORDS_PHRASES) {
				if(episodeNlpInfo != null) {
					for (CustomToken token : episodeNlpInfo.getTokens()) {
						if(token.getWord().equals(indicator)) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.WEAKNESS.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
							defect.setIndicator(DefectIndicatorEnum.WEAKNESS_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
							defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.WEAKNESS_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
							break;
						}
					}							
				}
				/*
				String regExpWord = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + indicator + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT; 
				String replacement = "<mark>" + indicator + "</mark>";
				if (episode.getSentence() != null && !episode.getSentence().isEmpty()) {
					//Indicator: The Episode sentence contains clauses that are apt to cause uncertainty
					String sentence  = ScenarioCleaner.cleanSentence(episode.getSentence().toLowerCase());
					String newSentence = sentence.replaceAll(regExpWord, replacement);
					if (!newSentence.equals(sentence)) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.WEAKNESS.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
						defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
						defect.setIndicator(DefectIndicatorEnum.WEAKNESS_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.WEAKNESS_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						break;
					}	
				}	
				*/
			}
			//@Episode 10.6: Check the Implicit of scenario Episode (WARNING);
			//NLP Dependency parsing and Dictionary
			if(episodeNlpInfo != null) {
				//Indicator: The Episode sentence contains implicit subjects 
				if(episodeNlpInfo.getSubjects() != null && !episodeNlpInfo.getSubjects().isEmpty()) {
					List<String> strSubjects = episodeNlpInfo.getSubjectsAsStringList();
					if(episodeNlpInfo.getModifierSubjects() != null && !episodeNlpInfo.getModifierSubjects().isEmpty())
						strSubjects.addAll(episodeNlpInfo.getModifierSubjectsAsStringList());
					if(episodeNlpInfo.getComplementSubjects() != null && !episodeNlpInfo.getComplementSubjects().isEmpty())
						strSubjects.addAll(episodeNlpInfo.getComplementSubjectsAsStringList());
					
					List<String> strIndicators = new ArrayList<String>();
					for(String subject : strSubjects) {
						for (String indicator : Unambiguity.IMPLICIT_WORDS_PHRASES) {
							if (subject.equals(indicator)) {
								strIndicators.add(subject);
							}
						}						
					}
											
					if (!strIndicators.isEmpty()) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.IMPLICITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
						defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
						defect.setIndicator(DefectIndicatorEnum.IMPLICITY_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", strIndicators.toString()));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.IMPLICITY_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						
					}	
				}
				
				//Indicator: The Episode sentence contains implicit objects 
				if(episodeNlpInfo.getDirectObjects() != null && !episodeNlpInfo.getDirectObjects().isEmpty()) {
					List<String> strObjects = episodeNlpInfo.getDirectObjectsAsStringList();
					if(episodeNlpInfo.getIndirectObjects() != null && !episodeNlpInfo.getIndirectObjects().isEmpty())
						strObjects.addAll(episodeNlpInfo.getIndirectObjectsAsStringList());
					
					List<String> strIndicators = new ArrayList<String>();
					for(String object : strObjects) {
						for (String indicator : Unambiguity.IMPLICIT_WORDS_PHRASES) {
							if (object.equals(indicator)) {
								strIndicators.add(object);
							}
						}						
					}
											
					if (!strIndicators.isEmpty()) {
						Defect defect = new Defect(); 
						defect.setQualityProperty(QualityPropertyEnum.IMPLICITY.getQualityProperty());
						defect.setScenarioId(structuredScenario.getId());
						defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
						defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
						defect.setIndicator(DefectIndicatorEnum.IMPLICITY_INDICATOR.getDefectIndicator());
						defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
						defect.setIndicator(defect.getIndicator().replace("<indicator>", strIndicators.toString()));
						defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
						defect.setFixRecomendation(DefectIndicatorEnum.IMPLICITY_INDICATOR.getFixRecomendation());
						defects.add(defect)	;
						
					}	
				}
					
			}
					
			
			//@Episode 10.7: Check the Quantifiability of scenario Episode (INFO);
			//CustomSentenceNlpInfo episodeNlpInfo = episode.getSentenceNlp();
			if(episodeNlpInfo != null) {
				//Indicator: The Episode sentence contains words that express quantification + adjective (vague) + (noun)*
				int indexQuantityToken = -1;
				int indexAdjectiveToken = -1;
				String quantityIndicator = "";
				String adjectiveIndicator = "";
				for (String indicator : Unambiguity.QUANTITY_WORDS_PHRASES) {
					for (CustomToken token : episodeNlpInfo.getTokens()) {
						if(token.getWord().equals(indicator)) {
							indexQuantityToken = token.getIndex();
							quantityIndicator = indicator;
							break;
						}
					}
					if(indexQuantityToken >= 0)
						break;					
				}
				//get adjective (vague) + (noun)*
				if(indexQuantityToken >= 0) {
					if(indexQuantityToken < episodeNlpInfo.getTokens().size() - 1) {
						CustomToken nextToken = episodeNlpInfo.getTokens().get(indexQuantityToken + 1);
						for (String indicator : Unambiguity.VAGUE_WORDS_PHRASES) {
							if(nextToken.getWord().equals(indicator)) {
								indexAdjectiveToken = nextToken.getIndex();						
								adjectiveIndicator = nextToken.getWord();
								break;
							}
						}
						/*
						if(nextToken.getPosTag().equals(PosTagEnum.JJ.name()) | nextToken.getPosTag().equals(PosTagEnum.VBN.name()) | nextToken.getPosTag().equals(PosTagEnum.VBD.name())) {
							indexAdjectiveToken = nextToken.getIndex();						
							adjectiveIndicator = nextToken.getWord();
						}*/
					}
				}
				if(indexQuantityToken >= 0 && indexAdjectiveToken > 0) {
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.QUANTIFIABILITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
					defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
					defect.setIndicator(DefectIndicatorEnum.QUANTIFIABILITY_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", quantityIndicator + WHITE_SPACE + adjectiveIndicator));
					defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.QUANTIFIABILITY_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
					
				}
			}
			

			//@Episode 10.8: Check the Multiplicity of scenario Episode (WARNING);
					
			if(episodeNlpInfo != null) {

				//Indicator: The Episode sentence has more than one subject 
				if(episodeNlpInfo.getSubjects() != null && !episodeNlpInfo.getSubjects().isEmpty() && episodeNlpInfo.getSubjects().size() > 1) {
					
					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.MULTIPLICITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
					defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
					defect.setIndicator(DefectIndicatorEnum.MULTIPLICITY_SUBJECT_INDICATOR.getDefectIndicator());
					defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", episodeNlpInfo.getSubjectsAsString()));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.MULTIPLICITY_SUBJECT_INDICATOR.getFixRecomendation());
					defects.add(defect)	;						
				}

				//Indicator: The Episode sentence has more than one main action-verb
				if(episodeNlpInfo.getMainActionVerbs() != null && !episodeNlpInfo.getMainActionVerbs().isEmpty() && episodeNlpInfo.getMainActionVerbs().entrySet().size() > 1) {
					String newSentence = episode.getSentence().toLowerCase();
					for(String indicator : episodeNlpInfo.getMainActionVerbsAsStringList()) {
						String regExpWord = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + indicator + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT; 
						String replacement = "<mark>" + indicator + "</mark>";
						newSentence = newSentence.replaceAll(regExpWord, replacement);
					}

					Defect defect = new Defect(); 
					defect.setQualityProperty(QualityPropertyEnum.MULTIPLICITY.getQualityProperty());
					defect.setScenarioId(structuredScenario.getId());
					defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
					defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
					defect.setIndicator(DefectIndicatorEnum.MULTIPLICITY_ACTION_VERB_INDICATOR.getDefectIndicator());
					//defect.setIndicator(defect.getIndicator().replace("<sentence>", newSentence));
					defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
					defect.setIndicator(defect.getIndicator().replace("<indicator>", episodeNlpInfo.getMainActionVerbsAsString()));
					defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
					defect.setFixRecomendation(DefectIndicatorEnum.MULTIPLICITY_ACTION_VERB_INDICATOR.getFixRecomendation());
					defects.add(defect)	;
				}					
			}
			
			
			//@Episode 10.9: Check the Minimality of scenario Episode (WARNING);
			for (String indicator : Unambiguity.NON_MINIMAL_WORDS_PHRASES) {
				if (episodeNlpInfo != null) {
					for (CustomToken token : episodeNlpInfo.getTokens()) {
						if(token.getWord().equals(indicator)) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.MINIMALITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.EPISODE_SENTENCE.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", episodeId));
							defect.setIndicator(DefectIndicatorEnum.NON_MINIMALITY_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<sentence>", episode.getSentence()));
							defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.NON_MINIMALITY_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
							break;
						}
					}
				}
				
			}
		}

		//@Episode 11: Check Alternatives (WARNING);
		int numAlternative = 1;
		for(StructuredAlternative alternative: structuredScenario.getAlternative()) {
			//@Episode 10.0: Create ID - Alternative;
			numAlternative++;
			String alternativeId = "Alternative number " + numAlternative;
			if(alternative.getId() != null && !alternative.getId().isEmpty())
				alternativeId = alternative.getId(); 
			
			
			//@Episode 10.1: Check Readability index (Coleman-Liau,  Flesch-Kincaid, Automated Readability Index - ARI) (WARNING) - Alternative;
			//Indicator: The sentence is difficult-to-read
			
			//@Episode 10.2: Check the Vagueness of scenario Alternative (WARNING);
			for (String indicator : Unambiguity.VAGUE_WORDS_PHRASES) {
				String regExpWord = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + indicator + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT; 
				String replacement = "<mark>" + indicator + "</mark>";
				if (alternative.getSolution() != null && !alternative.getSolution().isEmpty()) {
					for(String solution : alternative.getSolution()) {
						if(solution != null && !solution.isEmpty()) {
							//Indicator: The Alternative solution step contains vague words or phrases
							String lowSolution  = ScenarioCleaner.cleanSentence(solution.toLowerCase());
							String newSentence = lowSolution.replaceAll(regExpWord, replacement);
							if (!newSentence.equals(lowSolution)) {
								Defect defect = new Defect(); 
								defect.setQualityProperty(QualityPropertyEnum.VAGUENESS.getQualityProperty());
								defect.setScenarioId(structuredScenario.getId());
								defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
								defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
								defect.setIndicator(DefectIndicatorEnum.VAGUENESS_INDICATOR.getDefectIndicator());
								//defect.setIndicator(defect.getIndicator().replace("<sentence>", newSentence));
								defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
								defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
								defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
								defect.setFixRecomendation(DefectIndicatorEnum.VAGUENESS_INDICATOR.getFixRecomendation());
								defects.add(defect)	;
								break;
							}
						}
					}
					
				}	
				
			}

			//@Episode 10.3: Check the Subjectiveness of scenario Alternative (WARNING);
			//NLP POS tagging
			if (alternative.getSolution() != null && !alternative.getSolution().isEmpty()) {
				for (int i = 0; i < alternative.getSolution().size(); i++) {
					String solution = alternative.getSolution().get(i);
					if(solution != null && !solution.isEmpty()) {
						CustomSentenceNlpInfo solutionNlpInfo = alternative.getSolutionNlp().get(i);
						for(CustomToken token : solutionNlpInfo.getTokens()) {
							//Indicator: The Alternative solution step contains words like comparative/superlative adverbs/adjectives		
							if (token.getPosTag().equals(PosTagEnum.JJR.name()) || token.getPosTag().equals(PosTagEnum.JJS.name())
									|| token.getPosTag().equals(PosTagEnum.RBR.name()) || token.getPosTag().equals(PosTagEnum.RBS.name())) {
		
								String indicator = token.getWord();
							
								Defect defect = new Defect(); 
								defect.setQualityProperty(QualityPropertyEnum.SUBJECTIVENESS.getQualityProperty());
								defect.setScenarioId(structuredScenario.getId());
								defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
								defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
								defect.setIndicator(DefectIndicatorEnum.SUBJECTIVENESS_INDICATOR.getDefectIndicator());
								defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
								defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
								defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
								defect.setFixRecomendation(DefectIndicatorEnum.SUBJECTIVENESS_INDICATOR.getFixRecomendation());
								defects.add(defect)	;
								break;
							}	
						}
					}
				}
			}

			//@Episode 10.4: Check the Optionality of scenario Alternative (WARNING);
			for (String indicator : Unambiguity.OPTION_WORDS_PHRASES) {
				String regExpWord = RegularExpression.REGEX_PUNCTUATION_MARK_AT_BEGIN_TEXT + indicator + RegularExpression.REGEX_PUNCTUATION_MARK_AT_END_TEXT; 
				String replacement = "<mark>" + indicator + "</mark>";
				if (alternative.getSolution() != null && !alternative.getSolution().isEmpty()) {
					for(String solution : alternative.getSolution()) {
						if(solution != null && !solution.isEmpty()) {
							//Indicator: The Alternative solution step contains words that express optionality	
							String lowSolution  = ScenarioCleaner.cleanSentence(solution.toLowerCase());
							String newSentence = lowSolution.replaceAll(regExpWord, replacement);
							if (!newSentence.equals(lowSolution)) {
								Defect defect = new Defect(); 
								defect.setQualityProperty(QualityPropertyEnum.OPTIONALITY.getQualityProperty());
								defect.setScenarioId(structuredScenario.getId());
								defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
								defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
								defect.setIndicator(DefectIndicatorEnum.OPTIONALITY_INDICATOR.getDefectIndicator());
								defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
								defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
								defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
								defect.setFixRecomendation(DefectIndicatorEnum.OPTIONALITY_INDICATOR.getFixRecomendation());
								defects.add(defect)	;
								break;
							}	
						}
					}
				}	
				
			}
			//@Episode 10.5: Check the Weakness of scenario Alternative (WARNING);
			for (String indicator : Unambiguity.WEAK_WORDS_PHRASES) {
				if (alternative.getSolution() != null && !alternative.getSolution().isEmpty()) {
					for (int i = 0; i < alternative.getSolution().size(); i++) {
						String solution = alternative.getSolution().get(i);
						CustomSentenceNlpInfo solutionNlpInfo = alternative.getSolutionNlp().get(i);
						if(solutionNlpInfo != null) {
							for (CustomToken token : solutionNlpInfo.getTokens()) {
								if(token.getWord().equals(indicator)) {
									Defect defect = new Defect(); 
									defect.setQualityProperty(QualityPropertyEnum.WEAKNESS.getQualityProperty());
									defect.setScenarioId(structuredScenario.getId());
									defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
									defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
									defect.setIndicator(DefectIndicatorEnum.WEAKNESS_INDICATOR.getDefectIndicator());
									defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
									defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
									defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
									defect.setFixRecomendation(DefectIndicatorEnum.WEAKNESS_INDICATOR.getFixRecomendation());
									defects.add(defect)	;
									break;
								}
							}							
						}						
					}
				}	
				
			}
			//@Episode 10.6: Check the Implicit of scenario Alternative (WARNING);
			for (int i = 0; i < alternative.getSolution().size(); i++) {
				String solution = alternative.getSolution().get(i);
				if(solution != null && !solution.isEmpty()) {
					CustomSentenceNlpInfo solutionNlpInfo = alternative.getSolutionNlp().get(i);
					if(solutionNlpInfo != null) {
						//Indicator: The Episode sentence contains implicit subjects 
						if(solutionNlpInfo.getSubjects() != null && !solutionNlpInfo.getSubjects().isEmpty()) {
							List<String> strSubjects = solutionNlpInfo.getSubjectsAsStringList();
							if(solutionNlpInfo.getModifierSubjects() != null && !solutionNlpInfo.getModifierSubjects().isEmpty())
								strSubjects.addAll(solutionNlpInfo.getModifierSubjectsAsStringList());
														
							List<String> strIndicators = new ArrayList<String>();
							for(String subject : strSubjects) {
								for (String indicator : Unambiguity.IMPLICIT_WORDS_PHRASES) {
									if (subject.equals(indicator)) {
										
										strIndicators.add(subject);
									}
								}								
							}
													
							if (!strIndicators.isEmpty()) {
								Defect defect = new Defect(); 
								defect.setQualityProperty(QualityPropertyEnum.IMPLICITY.getQualityProperty());
								defect.setScenarioId(structuredScenario.getId());
								defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
								defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
								defect.setIndicator(DefectIndicatorEnum.IMPLICITY_INDICATOR.getDefectIndicator());
								defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
								defect.setIndicator(defect.getIndicator().replace("<indicator>", strIndicators.toString()));
								defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
								defect.setFixRecomendation(DefectIndicatorEnum.IMPLICITY_INDICATOR.getFixRecomendation());
								defects.add(defect)	;
								
							}	
						}
						
						//Indicator: The Episode sentence contains implicit objects 
						if(solutionNlpInfo.getDirectObjects() != null && !solutionNlpInfo.getDirectObjects().isEmpty()) {
							List<String> strObjects = solutionNlpInfo.getDirectObjectsAsStringList();
							if(solutionNlpInfo.getIndirectObjects() != null && !solutionNlpInfo.getIndirectObjects().isEmpty())
								strObjects.addAll(solutionNlpInfo.getIndirectObjectsAsStringList());
							
							List<String> strIndicators = new ArrayList<String>();
							for(String object : strObjects) {
								for (String indicator : Unambiguity.IMPLICIT_WORDS_PHRASES) {
									if (object.equals(indicator)) {
										strIndicators.add(object);
									}
								}								
							}
													
							if (!strIndicators.isEmpty()) {
								Defect defect = new Defect(); 
								defect.setQualityProperty(QualityPropertyEnum.IMPLICITY.getQualityProperty());
								defect.setScenarioId(structuredScenario.getId());
								defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
								defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
								defect.setIndicator(DefectIndicatorEnum.IMPLICITY_INDICATOR.getDefectIndicator());
								defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
								defect.setIndicator(defect.getIndicator().replace("<indicator>", strIndicators.toString()));
								defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
								defect.setFixRecomendation(DefectIndicatorEnum.IMPLICITY_INDICATOR.getFixRecomendation());
								defects.add(defect)	;
								
							}	
						}						
					}	
				}
			}
		
			
			
			//@Episode 10.7: Check the Quantifiability of scenario Alternative (INFO);
			for (int i = 0; i < alternative.getSolution().size(); i++) {
				String solution = alternative.getSolution().get(i);
				if(solution != null && !solution.isEmpty()) {
					CustomSentenceNlpInfo solutionNlpInfo = alternative.getSolutionNlp().get(i);
					if(solutionNlpInfo != null) {
						//Indicator: The Alternative solution step contains words that express quantification + adjective (vague) + (noun)*
						int indexQuantityToken = -1;
						int indexAdjectiveToken = -1;
						String quantityIndicator = "";
						String adjectiveIndicator = "";
						for (String indicator : Unambiguity.QUANTITY_WORDS_PHRASES) {
							for (CustomToken token : solutionNlpInfo.getTokens()) {
								if(token.getWord().equals(indicator)) {
									indexQuantityToken = token.getIndex();
									quantityIndicator = indicator;
									break;
								}
							}
							if(indexQuantityToken >= 0)
								break;					
						}
						//get adjective (vague) + (noun)*
						if(indexQuantityToken >= 0) {
							if(indexQuantityToken < solutionNlpInfo.getTokens().size() - 1) {
								CustomToken nextToken = solutionNlpInfo.getTokens().get(indexQuantityToken + 1);
								for (String indicator : Unambiguity.VAGUE_WORDS_PHRASES) {
									if(nextToken.getWord().equals(indicator)) {
										indexAdjectiveToken = nextToken.getIndex();						
										adjectiveIndicator = nextToken.getWord();
										break;
									}
								}
								/*
								if(nextToken.getPosTag().equals(PosTagEnum.JJ.name()) | nextToken.getPosTag().equals(PosTagEnum.VBN.name()) | nextToken.getPosTag().equals(PosTagEnum.VBD.name())) {
									indexAdjectiveToken = nextToken.getIndex();						
									adjectiveIndicator = nextToken.getWord();
								}*/
							}
						}
						
						if(indexQuantityToken >= 0 && indexAdjectiveToken > 0) {
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.QUANTIFIABILITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
							defect.setIndicator(DefectIndicatorEnum.QUANTIFIABILITY_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
							defect.setIndicator(defect.getIndicator().replace("<indicator>", quantityIndicator + WHITE_SPACE + adjectiveIndicator));
							defect.setDefectCategory(DefectCategoryEnum.INFO.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.QUANTIFIABILITY_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
													
						}
					}
					
				}
			}
			
			
			//@Episode 10.8: Check the Multiplicity of scenario Alternative (WARNING);
						
			//NLP Dependency parsing
			for (int i = 0; i < alternative.getSolution().size(); i++) {
				String solution = alternative.getSolution().get(i);
				if(solution != null && !solution.isEmpty()) {
					CustomSentenceNlpInfo solutionNlpInfo = alternative.getSolutionNlp().get(i);
					if(solutionNlpInfo != null) {

						//More that one sentences

						//Indicator: The Alternative solution step has more than one subject
						if(solutionNlpInfo.getSubjects() != null && !solutionNlpInfo.getSubjects().isEmpty() && solutionNlpInfo.getSubjects().size() > 1) {
							
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.MULTIPLICITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
							defect.setIndicator(DefectIndicatorEnum.MULTIPLICITY_SUBJECT_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
							defect.setIndicator(defect.getIndicator().replace("<indicator>", solutionNlpInfo.getSubjectsAsString()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.MULTIPLICITY_SUBJECT_INDICATOR.getFixRecomendation());
							defects.add(defect)	;						
						}

						//Indicator: The Alternative solution step has more than one main action-verb
						if(solutionNlpInfo.getMainActionVerbs() != null && !solutionNlpInfo.getMainActionVerbs().isEmpty() && solutionNlpInfo.getMainActionVerbs().entrySet().size() > 1) {
							
							Defect defect = new Defect(); 
							defect.setQualityProperty(QualityPropertyEnum.MULTIPLICITY.getQualityProperty());
							defect.setScenarioId(structuredScenario.getId());
							defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
							defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
							defect.setIndicator(DefectIndicatorEnum.MULTIPLICITY_ACTION_VERB_INDICATOR.getDefectIndicator());
							defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
							defect.setIndicator(defect.getIndicator().replace("<indicator>", solutionNlpInfo.getMainActionVerbsAsString()));
							defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
							defect.setFixRecomendation(DefectIndicatorEnum.MULTIPLICITY_ACTION_VERB_INDICATOR.getFixRecomendation());
							defects.add(defect)	;
						}					
					}
				}
			}
			
			
			
			//@Episode 10.9: Check the Minimality of scenario Alternative (WARNING);
			for (String indicator : Unambiguity.NON_MINIMAL_WORDS_PHRASES) {
				if (alternative.getSolution() != null && !alternative.getSolution().isEmpty()) {
					for (int i = 0; i < alternative.getSolution().size(); i++) {
						String solution = alternative.getSolution().get(i);
						CustomSentenceNlpInfo solutionNlpInfo = alternative.getSolutionNlp().get(i);
						//Indicator: The Alternative solution step contains a Text after a dot, hyphen, semicolon or other punctuation mark
						if(solutionNlpInfo != null) {
							for (CustomToken token : solutionNlpInfo.getTokens()) {
								if(token.getWord().equals(indicator)) {
									Defect defect = new Defect(); 
									defect.setQualityProperty(QualityPropertyEnum.MINIMALITY.getQualityProperty());
									defect.setScenarioId(structuredScenario.getId());
									defect.setScenarioElement(ScenarioElement.ALTERNATIVE_SOLUTION.getScenarioElement());
									defect.setScenarioElement(defect.getScenarioElement().replace("<id>", alternativeId));
									defect.setIndicator(DefectIndicatorEnum.NON_MINIMALITY_INDICATOR.getDefectIndicator());
									defect.setIndicator(defect.getIndicator().replace("<sentence>", solution));
									defect.setIndicator(defect.getIndicator().replace("<indicator>", indicator));
									defect.setDefectCategory(DefectCategoryEnum.WARNING.getDefectCategory());
									defect.setFixRecomendation(DefectIndicatorEnum.NON_MINIMALITY_INDICATOR.getFixRecomendation());
									defects.add(defect)	;
									break;
								}
							}							
						}						
					}					
				}	
				
			}
		}
		return defects;
	}

}
