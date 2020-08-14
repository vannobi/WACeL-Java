package pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pe.edu.unsa.daisi.lis.cel.util.nlp.CustomSentenceNlpInfo;

/**
 * Structured Alternative
 * @author Edgar
 *
 */
public class StructuredAlternative implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;//<Step><Reference>
	private List<String> causes;
	private List<String> solution; //FIX: In Use Case, we have a set of sub-steps
	private List<String> postConditions;
	private StructuredEpisode branchingEpisode; //Alternate/Exception has a branching Episode
	
	private StructuredEpisode goToEpisode; //Every alternate flow must return to some specific episode of the main flow 
	private boolean scenarioFinish = false;		   //or finish the scenario
	private Integer solutionStepWithGoToEpisode = -1; //Must be the last step of the solution
	
	
	private boolean ifThenFormat; //Format: IF-THEN | multi-step lines
	
	private String rawAlternative; //not parsed
	
	//NLP info about solution: tokens, subjetcs, verbs, objects, ....
	private List<CustomSentenceNlpInfo> solutionNlp;
	
	public StructuredAlternative() {

	}

	/**
	 * Initialize Alternative 
	 * @param id
	 * @return
	 */
	public void createAlternative(String id) {

		this.id = id;
		this.solution = new ArrayList<String>();;
		this.causes  = new ArrayList<String>();
		this.postConditions  = new ArrayList<String>();
		this.solutionNlp = new ArrayList<CustomSentenceNlpInfo>();
		this.ifThenFormat = false;
		
	}

	public void addSolution(String solution) {
		getSolution().add(solution);
	}

	public void addCause(String cause) {
		getCauses().add(cause);
	}

	public void addPostCondition(String postCondition) {
		getPostConditions().add(postCondition);
	}
	
	public void addSolutionNlp(CustomSentenceNlpInfo solutionNlpInfo) {
		getSolutionNlp().add(solutionNlpInfo);
	}

	public String getId() {
		return id;
	}
	/**
	 * <Step><Reference>
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getSolution() {
		return solution;
	}
	public void setSolution(List<String> solution) {
		this.solution = solution;
	}
	public List<String> getCauses() {
		return causes;
	}
	public void setCauses(List<String> causes) {
		this.causes = causes;
	}
	public List<String> getPostConditions() {
		return postConditions;
	}
	public void setPostConditions(List<String> postConditions) {
		this.postConditions = postConditions;
	}

	public StructuredEpisode getBranchingEpisode() {
		return branchingEpisode;
	}

	public void setBranchingEpisode(StructuredEpisode branchingEpisode) {
		this.branchingEpisode = branchingEpisode;
	}
	
	public StructuredEpisode getGoToEpisode() {
		return goToEpisode;
	}

	public void setGoToEpisode(StructuredEpisode goToEpisode) {
		this.goToEpisode = goToEpisode;
	}
		
	
	public boolean isScenarioFinish() {
		return scenarioFinish;
	}

	public void setScenarioFinish(boolean scenarioFinish) {
		this.scenarioFinish = scenarioFinish;
	}

	public Integer getSolutionStepWithGoToEpisode() {
		return solutionStepWithGoToEpisode;
	}

	public void setSolutionStepWithGoToEpisode(Integer solutionStepWithGoToEpisode) {
		this.solutionStepWithGoToEpisode = solutionStepWithGoToEpisode;
	}


	public boolean isIfThenFormat() {
		return ifThenFormat;
	}

	public void setIfThenFormat(boolean ifThenFormat) {
		this.ifThenFormat = ifThenFormat;
	}

	public String getRawAlternative() {
		return rawAlternative;
	}

	public void setRawAlternative(String rawAlternative) {
		this.rawAlternative = rawAlternative;
	}

	public List<CustomSentenceNlpInfo> getSolutionNlp() {
		return solutionNlp;
	}

	public void setSolutionNlp(List<CustomSentenceNlpInfo> solutionNlp) {
		this.solutionNlp = solutionNlp;
	}

	
}
