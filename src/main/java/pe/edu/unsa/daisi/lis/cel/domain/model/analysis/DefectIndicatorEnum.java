package pe.edu.unsa.daisi.lis.cel.domain.model.analysis;

import java.io.Serializable;

public enum DefectIndicatorEnum implements Serializable{
	//Unambiguity
	VAGUENESS_INDICATOR("The sentence <mark><sentence></mark> contains vague words or phrases {<b><i><indicator></i></b>}", "<i>Re-describe the sentence by removing vague terms</i>"),
	SUBJECTIVENESS_INDICATOR("The sentence <mark><sentence></mark> contains words like comparative/superlative adverbs/adjectives  {<b><i><indicator></i></b>}", "<i>Re-describe the sentence by removing subjective terms</i>"),
	OPTIONALITY_INDICATOR("The sentence <mark><sentence></mark> contains words that express optionality {<b><i><indicator></i></b>}", "<i>Re-describe the sentence by removing optional terms</i>"),
	WEAKNESS_INDICATOR("The sentence <mark><sentence></mark> contains clauses that are apt to cause uncertainty   {<b><i><indicator></i></b>}", "<i>Re-describe the sentence by removing weak terms</i>"),
	MULTIPLICITY_INDICATOR("The sentence <mark><sentence></mark> has more than one subject or main action-verb {<b><i><indicator></i></b>}", "<i>Split the sentence into multiple sentences</i>"),
	MULTIPLICITY_SUBJECT_INDICATOR("The sentence <mark><sentence></mark> has more than one subject  {<b><i><indicator></i></b>}", "<i>Split the sentence into multiple sentences</i>"),
	MULTIPLICITY_ACTION_VERB_INDICATOR("The sentence <mark><sentence></mark> has more than one main action-verb {<b><i><indicator></i></b>}", "<i>Split the sentence into multiple sentences</i>"),
	IMPLICITY_INDICATOR("The sentence <mark><sentence></mark> does not specify the subject or object by means of its specific name but uses pronoun or indirect reference {<b><i><indicator></i></b>}", "<i>Re-describe the sentence by specifying subjects/objects by means of its specific name</i>"),
	QUANTIFIABILITY_INDICATOR("The sentence <mark><sentence></mark> contains words that express quantification {<b><i><indicator></i></b>}", "Terms used for quantification can lead to ambiguity if not used properly. <i>Re-describe the sentence by removing quantifiable terms</i>"),
	NON_MINIMALITY_INDICATOR("The sentence <mark><sentence></mark> contains a Text after a dot, hyphen, semicolon or other punctuation mark {<b><i><indicator></i></b>}", "<i>Split the sentence into multiple sentences</i>"),
	READABILITY_DIFFICULT_INDICATOR("The sentence <mark><sentence></mark> is difficult-to-read {<b><i><indicator></i></b>}.", "<i>Check that sentence contains only significant information like the main verb, direct object and optionally the subject</i>"),
	//Completeness
	ATOMICITY_TITLE_MULTIPLE_SITUATION_INDICATOR("The title <mark><sentence></mark> contains more than one action-verb, subject or object  {<b><i><indicator></i></b>}", "<i>Split the scenario into multiple scenarios or remove one action-verbs or objects</i>"),
	ATOMICITY_GOAL_MULTIPLE_PURPOSE_INDICATOR("The goal <mark><sentence></mark> contains more than one main verb, subject or object {<b><i><indicator></i></b>}", "<i>Split the scenario into multiple scenarios or remove one action-verbs or objects</i>"),
	ATOMICITY_TITLE_UNNECESSARY_SUBJECT_INDICATOR("Unnecessary <i>Subjects</i> {<b><i><indicator></i></b>} in the title <mark><sentence></mark>", "<i>It is not necessary subjects in Title</i>"),
	ATOMICITY_TITLE_ACTION_VERB_NOT_IN_INFINITIVE_FORM_INDICATOR("<i>Action-Verb</i> {<b><i><indicator></i></b>} in the title <mark><sentence></mark> is not in INFINITIVE (base) FORM", "<i>Inform an Action-Verb in infinitive form</i>"),
	ATOMICITY_TITLE_MISSING_ACTION_VERB_INDICATOR("Missing <i>Action-Verb</i> in the title <mark><sentence></mark>", "<i>Inform an Action-Verb in infinitive form</i>"),
	ATOMICITY_TITLE_MISSING_OBJECT_INDICATOR("Missing <i>Object</i> in the title <mark><sentence></mark>", "<i>Inform an Object after the Action-Verb</i>"),
	ATOMICITY_TITLE_MORE_THAN_ONE_ACTION_VERB_INDICATOR("The title <mark><sentence></mark> contains more than one <i>Action-Verb</i> {<b><i><indicator></i></b>}", "<i>Split the scenario into multiple scenarios or remove one action-verb</i>"),
	
	SIMPLICITY_EPISODE_MORE_THAN_ONE_ACTION_VERB_INDICATOR("The episode sentence <mark><sentence></mark> contains more than one <i>Action-Verb</i> {<b><i><indicator></i></b>}", "<i>Split the sentence into multiple sentences</i>"),
	SIMPLICITY_EPISODE_MORE_THAN_ONE_SUBJECT_INDICATOR("The episode sentence <mark><sentence></mark> contains more than one <i>Subject</i> {<b><i><indicator></i></b>}", "<i>Split the sentence into multiple sentences</i>"),
	SIMPLICITY_EPISODE_MISSING_SUBJECT_INDICATOR("Missing <i>Subject</i> in the episode sentence <mark><sentence></mark>", "<i>IF sentence does not reference another scenario THEN Inform who (Subject) performs the Action-Verb</i>"),
	SIMPLICITY_EPISODE_MISSING_OBJECT_INDICATOR("Missing <i>Object</i> in the episode sentence <mark><sentence></mark>", "<i>Inform who (Object) is impacted by the Action-Verb</i>"),
	SIMPLICITY_EPISODE_MISSING_ACTION_VERB_INDICATOR("Missing <i>Action-Verb</i> in the episode sentence <mark><sentence></mark>", "<i>Inform an Action-Verb an action-verb in the present simple tense and active form</i>"),
	
	SIMPLICITY_ALTERNATIVE_MORE_THAN_ONE_ACTION_VERB_INDICATOR("The alternative solution step <mark><sentence></mark> contains more than one <i>Action-Verb</i> {<b><i><indicator></i></b>}", "<i>Split the solution into multiple sentences</i>"),
	SIMPLICITY_ALTERNATIVE_MORE_THAN_ONE_SUBJECT_INDICATOR("Alternative solution step <mark><sentence></mark> contains more than one <i>Subject</i> {<b><i><indicator></i></b>}", "<i>Split the solution into multiple sentences</i>"),
	SIMPLICITY_ALTERNATIVE_MISSING_OBJECT_INDICATOR("Missing <i>Object</i> in the alternative solution step <mark><sentence></mark>", "<i>Inform an Object after the Action-Verb</i>"),
	SIMPLICITY_ALTERNATIVE_MISSING_ACTION_VERB_INDICATOR("Missing <i>Action-Verb</i> in the alternative solution step <mark><sentence></mark>", "<i>Inform an Action-Verb an action-verb in the present simple tense and active form</i>"),
	
	SIMPLICITY_EPISODE_ACTION_VERB_NOT_IN_THIRD_FORM_INDICATOR("The pisode sentence<mark><sentence></mark> contains an Action-verb {<b><i><indicator></i></b>} not in the <i>third form </i>", "<i>Use an action-verb in the present simple tense and active form</i>"),
	SIMPLICITY_EPISODE_ACTION_VERB_NOT_IN_INFINITIVE_FORM_INDICATOR("The pisode sentence<mark><sentence></mark> references to another scenario and contains an Action-verb {<b><i><indicator></i></b>} not in the <i>infinitive form </i>", "<i>Use an action-verb in the infinitive form, when reference to another scenario</i>"),
	SIMPLICITY_ALTERNATIVE_ACTION_VERB_NOT_IN_THIRD_OR_BASE_FORM_INDICATOR("The alternative solution step <mark><sentence></mark> contains an Action-verb {<b><i><indicator></i></b>} not in the <i>third or infinitive form</i>", "<i>Use an action-verb in the present simple tense and active form (or infinitive form)</i>"),
	
	
	SIMPLICITY_TITLE_UNNECESSARY_INFORMATION_INDICATOR("The Title <mark><sentence></mark> contains <i>unnecessary information</i> {<b><i><indicator></i></b>}", "<i>Remove unnecessary information</i>"),
	SIMPLICITY_EPISODE_DUPLICATED_ID_INDICATOR("<i>Duplicated Episode Id/Step</i> {<b><i><indicator></i></b>}", "<i>Remove or re-write one episode</i>"),
	SIMPLICITY_EPISODE_DUPLICATED_SENTENCE_INDICATOR("<i>Duplicated Episode Sentence</i> {<b><i><indicator></i></b>}", "<i>Remove or re-write one episode</i>"),
	
	SIMPLICITY_EPISODE_COMPLICATED_VALIDATION_SENTENCE_INDICATOR("The Episode <mark><i><sentence></i></mark> involves a validation action and it is hard to understand and follow (contain structures like <mark>checks if / see whether</mark>)", "<i>Instead, re-write using the optimistic scenario, use one of the other validation verbs (verify / validate / ensure / establish followed by that) or relocate conditions and their actions to alternate/exception flow section</i>"),
	SIMPLICITY_EPISODE_NESTED_SENTENCE_INDICATOR("<i>More than one Episode-Sentence inside a nested IF structure</i> {<b><i><indicator></i></b>}", "<i>Create a new scenario and extract the sequence to it, or It should be in a separate Alternate/Exception flow section</i>"),
	SIMPLICITY_ALTERNATIVE_COMPLICATED_SOLUTION_INDICATOR("<i>The Alternate/Exception <mark><sentence></mark> has too many steps (more than 2) {<b> <i><i><indicator></i></b>}", "<i>Extract the sequence to a separated scenario</i>"),
	
	SIMPLICITY_ALTERNATIVE_GOTO_NOT_IN_LAST_ALTERNATE_SOLUTION_STEP_INDICATOR("<i>The Alternate <mark><sentence></mark> does not return to the main flow in the last solution step {solution step <b> <i><i><indicator></i></b>}", "<i>Move the solution step with <b>GO TO</b> to the last</i>"),
	SIMPLICITY_ALTERNATIVE_GOTO_WITHOUT_EPISODE_IN_ALTERNATE_SOLUTION_STEP_INDICATOR("<i>The Alternate <mark><sentence></mark> returns to the main flow using an invalid episode Id/Step {solution step <b> <i><i><indicator></i></b>}", "<i>Inform a valid episode Id/Step</i>"),
	
	UNIFORMITY_MISSING_TITLE_INDICATOR("Missing <i>Title</i>", "<i>Inform the Title</i>"),
	UNIFORMITY_MISSING_GOAL_INDICATOR("Missing <i>Goal</i>", "<i>Inform the Goal</i>"),
	UNIFORMITY_MISSING_ACTOR_INDICATOR("Missing <i>Actors</i>", "<i>Inform at least one Actor</i>"),
	UNIFORMITY_MISSING_RESOURCES_INDICATOR("Missing <i>Resources</i>", "<i>Inform at least one Resources</i>"),
	UNIFORMITY_MISSING_CONTEXT_SUBCOMPONENTS_INDICATOR("<i>Context</i> does not contain its relevant subcomponents", "<i>Inform at least one Pre-condition, Postcondition, Temporal Location or Geographical Location</i>"),
	UNIFORMITY_MISSING_EPISODES_INDICATOR("Missing <i>Episodes</i>", "<i>Inform at least one Episode</i>"),
	
	UNIFORMITY_MISSING_EPISODE_SUBCOMPONENTS_INDICATOR("The episode <mark><sentence></mark> does not contain its relevant parts: {<mark><b><i><indicator></i></b></mark>}", "<i>1. IF episode is Conditional or Loop THEN inform at least: Id/Step, Condition and Sentence; <br/>2. IF episode is Simple THEN inform at least: Id/Step and Sentence;</i>"),
	UNIFORMITY_MISSING_ALTERNATIVE_SUBCOMPONENTS_INDICATOR("The alternate/exception <mark><sentence></mark> does not contain its relevant parts: {<mark><b><i><indicator></i></b></mark>}", "<i>Inform: Id/Step, Cause or Solution</i>"),
	
	UNIFORMITY_MISSING_EPISODES_GROUP_INDICATOR("Incomplete <i> non-sequential construct</i> in non-sequential episodes delimited by <i># ... #</i>", "<i>Complete the non-sequential construct <i># ... #</i> </i>"),
	
	USEFULNES_UNUSED_ACTOR_INDICATOR("Actor does not participate in the situation - Episodes {<b><i><indicator></i></b>}", "<i>Mention the actor in at least one episode</i>"),
	USEFULNES_EPISODE_UNDECLARED_ACTOR_INDICATOR("The episode sentence <mark><sentence></mark> contains undeclared Actor {<b><i><indicator></i></b>}", "<i>Include the Subject in Actors or use the System word</i>"),
	USEFULNES_ALTERNATIVE_UNDECLARED_ACTOR_INDICATOR("The alternative solution step <mark><sentence></mark> contains undeclared Actor {<b><i><indicator></i></b>}", "<i>Include the Subject in Actors or use the System word</i>"),
	USEFULNES_UNUSED_RESOURCE_INDICATOR("Resource does not participate in the situation - Episodes {<b><i><indicator></i></b>}", "<i>Mention the resource in at least one episode</i>"),
	USEFULNES_EPISODE_UNDECLARED_RESOURCE_INDICATOR("The episode sentence <mark><sentence></mark> contains undeclared Resource or Actor {<b><i><indicator></i></b>}", "<i>Include Direct-Objects in Resources or Actors</i>"),

	USEFULNES_ALTERNATIVE_WITHOUT_BRANCHING_EPISODE_INDICATOR("Branching Episode of <i>Alternative</i> is missing", "<i>Update the alternative Id/StepRef to appoint the correct episode</i>"),
	USEFULNES_EPISODES_NOT_BETWEEN_3_AND_9_INDICATOR("Number of episodes in current scenario is <i>less than 3 or more than 9</i> {<b><i><indicator></i></b>}", "<i>Re-write the scenario to keep between 3 and 9 episodes</i>"),
	
	CONCEPTUALLY_SOUNDNESS_TITLE_DO_NOT_DESCRIBE_GOAL_INDICATOR("The corresponding verbs and objects in <i> Title </i> {<b><i><indicator></i></b>} and  <i>Goal</i> {<b><i><indicator></i></b>} are not the same", "<i>Re-write the Title to satisfy the Goal</i>"),
	CONCEPTUALLY_SOUNDNESS_EPISODES_DO_NOT_SATISFY_GOAL_INDICATOR("The set of episodes does not satisfy the <i>Goal</i>", "<i>Re-write the set of episodes to satisfy the Goal, or vice-versa</i>"),	//difficult to implement
	
	CONCEPTUALLY_SOUNDNESS_EPISODE_MISSING_ACTION_VERB_INDICATOR("Missing <i>Action-Verb</i> in the sentence {<b><i><indicator></i></b>}</b>", "<i>Inform an Action-Verb in the present simple tense and active form</i>"),
	
	CONCEPTUALLY_SOUNDNESS_EPISODE_MISSING_LINKING_VERB_INDICATOR("Missing <i>Linking-Verb</i> in conditions {<b><i><indicator></i></b>}", "<i>Inform a Linking-Verb in conditions</i>"),
	CONCEPTUALLY_SOUNDNESS_PRE_CONDITION_MISSING_STATE_VERB_INDICATOR("Missing <i>State-Verb</i> in Pre-conditions {<b><i><indicator></i></b>}", "<i>Inform a State-Verb in Pre-conditions</i>"),
	CONCEPTUALLY_SOUNDNESS_POST_CONDITION_MISSING_STATE_VERB_INDICATOR("Missing <i>State-Verb</i> in Post-condition {<b><i><indicator></i></b>}", "<i>Inform a State-Verb in Post-conditions</i>"),
	
	CONCEPTUALLY_SOUNDNESS_ALTERNATIVE_MISSING_ACTION_VERB_INDICATOR("Missing <i>Action-Verb</i> in solution {<b><i><indicator></i></b>}", "<i>IF solution is performed by an <i>actor or resource </i>, inform an Action-Verb in the present simple tense and active form, otherwise, inform in infinitive form</i>"),
	
	CONCEPTUALLY_SOUNDNESS_ALTERNATIVE_MISSING_STATE_OR_LINKING_VERB_INDICATOR("Missing <i>Linking-Verb or State-Verb</i> in causes {<b><i><indicator></i></b>}", "<i>Inform a Linking-Verb or State-Verb in causes</i>"),
	
	INTEGRITY_SCENARIO_PRE_CONDITION_NOT_EXIST_INDICATOR("Pre-condition {<b><i><indicator></i></b>} references to a <i>related scenario</i> that does not exist within the set of scenarios", "<i>Include the related scenario to the set of scenarios</i>"),
	INTEGRITY_SCENARIO_POST_CONDITION_NOT_EXIST_INDICATOR("Post-condition {<b><i><indicator></i></b>} references to a <i>related scenario</i> that does not exist within the set of scenarios", "<i>Include the related scenario to the set of scenarios</i>"),
	INTEGRITY_SCENARIO_EPISODE_NOT_EXIST_INDICATOR("Episode sentence {<b><i><indicator></i></b>} references to a <i>related scenario</i> that does not exist within the set of scenarios", "<i>Include the related scenario to the set of scenarios</i>"),
	INTEGRITY_SCENARIO_ALTERNATIVE_NOT_EXIST_INDICATOR("Alternative solution step {<b><i><indicator></i></b>} references to a <i>related scenario</i> that does not exist within the set of scenarios", "<i>Include the related scenario to the set of scenarios</i>"),
	INTEGRITY_SCENARIO_CONSTRAINT_NOT_EXIST_INDICATOR("Constraint {<b><i><indicator></i></b>} references to a <i>related scenario</i> that does not exist within the set of scenarios", "<i>Include the related scenario to the set of scenarios</i>"),
	
	INTEGRITY_ALTERNATIVE_COMPLEX_SOLUTION_INDICATOR("Complex Alternative Solution {<b><i><indicator></i></b>} must be treated by another <i>scenario</i>", "<i>Create a new scenario for the alternative solution</i>"),
	INTEGRITY_SCENARIO_PRE_CONDITIONS_DONOT_PERFORMED_INDICATOR("Missing scenario Post-condition (of another scenario) that satisfies the current Pre-condition {<b><i><indicator></i></b>}", "<i>IF the pre-condition is not an uncontrollable fact THEN describe it as post-condition of another scenario</i>"),
	
		
	COHERENCY_PRE_CONDITIONS_RELATED_SCENARIO_NOT_COHERENT_WITH_MAIN_SCENARIO_INDICATOR("Related scenario <b><related-scenario></b> Pre-conditions are not coherent with the Pre-conditions of the main scenario <b><main-scenario></b>", "<i>Re-write the pre-conditions of related or main scenario</i>"),
	COHERENCY_GEOGRAPHICAL_LOCATIONS_RELATED_SCENARIO_NOT_IN_MAIN_SCENARIO_INDICATOR("Related scenario {<b><i><indicator></i></b>} Geographical location {<b><i><indicator></i></b>} is not in the set of Geographical locations of the main scenario {<b><i><indicator></i></b>}", "<i>Re-write the Geographical locations of related scenario to be more restrict to the main scenario</i>"),
	COHERENCY_TEMPORAL_LOCATIONS_RELATED_SCENARIO_NOT_IN_MAIN_SCENARIO_INDICATOR("Related scenario {<b><i><indicator></i></b>} Temporal location {<b><i><indicator></i></b>} is not in the set of Temporal locations of the main scenario {<b><i><indicator></i></b>}", "<i>Re-write the Temporal locations of related scenario to be more restrict to the main scenario</i>"),
	COHERENCY_SCENARIOS_CIRCULAR_INCLUSION_INDICATOR("Circular inclusion: The related scenario {<b><i><indicator></i></b>} reference in its description {<b><i><indicator></i></b>} to the main scenario {<b><i><indicator></i></b>}. ", "<i>Remove the reference to the main scenario (in referenced scenario)</i></b>"),
	
	UNIQUENESS_SCENARIOS_TITLE_COINCIDENCE_INDICATOR("The main scenario {<b><i><indicator></i></b>} and another scenario {<b><i><indicator></i></b>} have similar Titles", "<i>1. IF the sets of episodes are the same THEN remove one scenario <br/> 2. IF the sets of episodes are not the same THEN rename the Title of one scenario</i></b>"),
	UNIQUENESS_SCENARIOS_GOAL_COINCIDENCE_INDICATOR("The main scenario {<b><i><indicator></i></b>} and another scenario {<b><i><indicator></i></b>} have similar Goals {<b><i><indicator></i> | <i><indicator></i></b>}", "<i>1. IF the sets of episodes are the same THEN remove one scenario <br/> 2. IF the sets of episodes are not the same THEN rename the Goal of one scenario</i></b>"),
	UNIQUENESS_SCENARIOS_CONTEXT_PRE_CONDITIONS_COINCIDENCE_INDICATOR("The main scenario {<b><i><indicator></i></b>} and another scenario {<b><i><indicator></i></b>} have similar Pre-conditions {<b><i><indicator></i> | <i><indicator></i></b>}", "<i>IF the sets of episodes are the same THEN remove one scenario</i></b>"),
	UNIQUENESS_SCENARIOS_EPISODES_COINCIDENCE_INDICATOR("The main scenario {<b><i><indicator></i></b>} and another scenario {<b><i><indicator></i></b>} have similar Episode Sentences {<b><i><indicator></i> | <i><indicator></i></b>}", "<i>1. IF the set of episodes of scenario_2 is a subset of scenario_1 THEN remove the duplicated episodes in scenario_1 and reference to scenario_2<br/> 2. IF the sets of episodes are the same THEN remove one scenario</i></b>"),
	UNIQUENESS_SCENARIOS_TITLE_SYNTACTIC_SAME_VERB_OBJECT_INDICATOR("Syntactic Similarity: The main scenario {<b><i><indicator></i></b>} and another scenario {<b><i><indicator></i></b>} share the same Action-Verbs and the Direct-Objects", "<i>1. IF the sets of episodes are the same THEN remove one scenario <br/> 2. IF the sets of episodes are not the same THEN rename the Title of one scenario</i></b>"),
	UNIQUENESS_SCENARIOS_TITLE_SEMANTIC_SYNONYM_VERB_OBJECT_INDICATOR("Semantic Similarity: The main scenario {<b><i><indicator></i></b>} and another scenario {<b><i><indicator></i></b>} share Action-Verbs and the Direct-Objects in synonymous forms", "<i>1. IF the sets of episodes are the same THEN remove one scenario <br/> 2. IF the sets of episodes are not the same THEN rename the Title of one scenario</i></b>"),
	
	FEASIBILITY_SCENARIOS_ISOLATED_INDICATOR("It is not possible to derive a Petri-Net", "<i>Inform the relevant parts of Episodes and Alternatives</i>"),
	FEASIBILITY_SCENARIO_UNREACHABLE_OPERATION_INDICATOR("The Petri-Net contains isolated sub nets - <i>unreachable transitions - operations</i> {<b><i><indicator></i></b>}", "<i>Inform the relevant parts of Episodes and Alternatives</i>"),
	
	//consistency
	CONSISTENCY_DEFECT_INDICATOR("The presence of defects is a strong indication, although not conclusive, of incorrectness that must be fixed.", "<i>Notify to the next software development activities</i>"),
	
	NON_INTERFERENTIAL_SIMULTANEOUS_ENABLED_OPERATIONS_INDICATOR("Petri-Net with simultaneously enabled transitions - operations {<b><i><indicator></i></b>}", "<i>1. Check that all pre-conditions or constraints associated to the episode/alternative corresponding to the transition are fulfilled <br/>2. Notify to the next software development activities</i>"),
	
	BOUNDEDNESS_RESOURCE_OVERFLOW_INDICATOR("Petri-Net is not bounded, i.e, It contains overflowed places - resources: {<b><i><indicator></i></b>}", "<i>1. Check that the overflowed resources is a critical shared resource modified by several operations or scenarios <br/>2. Check that the overflowed resources capacity <br/>3. Notify to the next software development activities</i>"),
	
	BOUNDEDNESS_PETRI_NET_NOT_SAFE("Petri-Net is not safe, i.e, It is not 1-bounded: {<b><i><indicator></i></b>}", "<i>1. Check that the overflowed resources capacity <br/>2. Notify to the next software development activities</i>"),
	
	LIVENESS_DEADLOCK_INDICATOR("Petri-Net with Path to deadlock: {<b><i><indicator></i></b>}", "<i>1. Check whether there are shared resources modified by the scenarios and their relationships <br/>2. Check that every alternative flow returns to a specific episode of the main flow or finishes the scenario <br/>3.  Notify to the next software development activities</i>"),
	
	LIVENESS_NEVER_ENABLED_OPERATIONS_INDICATOR("Petri-Net with Never enabled transitions - operations: {<b><i><indicator></i></b>}", "<i>1. Check that all pre-conditions, constraints, conditions or causes of the episode/alternative corresponding to the transition are fulfilled <br/>2. Notify to the next software development activities</i>"),
	
	REVERSIBILITY_NO_PATH_TO_INITIAL_STATE_INDICATOR("There are no a path from a transition - operation to the initial state of the Petri-Net, i.e, Petri-Net is not bounded, not safe and not live", "<i>1. Check that the performed scenarios are releasing resources, pre-conditions and constraints after completion <br/>2. Check that every alternative flow returns to a specific episode of the main flow or finishes the scenario <br/>3. Check the absence of deadlocks or never enabled operations</i>"),
	;
	
	
	String defectIndicator;
	String fixRecomendation;
	
	private DefectIndicatorEnum(String defectIndicator, String fixRecomendation){
		this.defectIndicator = defectIndicator;
		this.fixRecomendation = fixRecomendation;
	}

	public String getDefectIndicator() {
		return defectIndicator;
	}

	public void setDefectIndicator(String defectIndicator) {
		this.defectIndicator = defectIndicator;
	}

	public String getFixRecomendation() {
		return fixRecomendation;
	}

	public void setFixRecomendation(String fixRecomendation) {
		this.fixRecomendation = fixRecomendation;
	}
	
	
	
}
