package pe.edu.unsa.daisi.lis.cel.domain.model.analysis;

import java.io.Serializable;

/**
 * Scenario element/attribute with defect: trace
 * @author Edgar
 *
 */
public enum ScenarioElement implements Serializable{
	TITLE("Title"),
	GOAL("Goal"),
	CONTEXT("Context"),
	CONTEXT_TEMPORAL_LOCATION("Context Temporal Location"),
	CONTEXT_GEOGRAPHICAL_LOCATION("Context Geographical Location"),
	CONTEXT_PRE_CONDITION("Context Pre-condition"),
	CONTEXT_POST_CONDITION("Context Post-condition"),
	ACTORS("Actors"),
	RESOURCES("Resources"),
	EPISODES("Episodes"),
	EPISODE_ID("Episode <b><id></b>"),
	EPISODE_SENTENCE("Episode <b><id></b> Sentence"),
	EPISODE_SENTENCE_NON_SEQUENTIAL("Episode <b><id></b> Sentence in Non-sequential group"), //Used to mark episodes between a #<episode series># structure
	EPISODE_CONDITION("Episode <b><id></b> Condition"),
	EPISODE_PRE_CONDITION("Episode <b><id></b> Pre-condition"),
	EPISODE_POST_CONDITION("Episode <b><id></b> Post-condition"),
	EPISODE_CONSTRAINT("Episode <b><id></b> Constraint"),
	ALTERNATIVE("Alternatives"),
	ALTERNATIVE_ID("Alternative <b><id></b>"),
	ALTERNATIVE_SOLUTION("Alternative <b><id></b> Solution"),
	ALTERNATIVE_CAUSE("Alternative <b><id></b> Cause"),
	ALTERNATIVE_POST_CONDITION("Alternative <b><id></b> Post-condition");
	
	
	String scenarioElement;
	
	private ScenarioElement(String scenarioElement){
		this.scenarioElement = scenarioElement;
	}
	
	public String getScenarioElement(){
		return scenarioElement;
	}
	
}
