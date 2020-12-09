package pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pe.edu.unsa.daisi.lis.cel.util.nlp.CustomSentenceNlpInfo;

/**
 * Class to deal with Scenario and its structured content <br/>
 * Structured Scenario
 * @author Edgar
 *
 */
public class StructuredScenario implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7276376273536856945L;

	private Long id;
	private String title;
	private String goal;
	private StructuredContext context;
	private List<String> actors; 
	private List<String> resources;
	private List<StructuredEpisode> episodes; 
	private List<StructuredAlternative> alternative; //Alternate/Exception Steps
	private java.util.Calendar inclusionDate;
	private Boolean exclusionFlag;
	private Long projectId;
	private String projectName;
	
	//NLP info about title and goal: tokens, subjects, verbs, objects, ....
	private CustomSentenceNlpInfo titleNlp;
	private CustomSentenceNlpInfo goalNlp;


	public StructuredScenario() {

	}

	/**
	 * Initialize Scenario <br/>
	 * Set context using get/set
	 * @param id
	 * @param title
	 * @param goal
	 * @param inclusionDate
	 * @param exclusionFlag
	 * @param projectId
	 * @param projectName
	 * @return
	 */
	public void createScenario(Long id, String title, String goal, Calendar inclusionDate,
			Boolean exclusionFlag, Long projectId, String projectName) {

		this.id = id;
		this.title = title;
		this.goal = goal;
		//this.context = context; //Set context
		this.actors = new ArrayList<String>();
		this.resources = new ArrayList<String>();
		this.episodes  = new ArrayList<StructuredEpisode>();
		this.alternative = new ArrayList<StructuredAlternative>();
		this.inclusionDate = inclusionDate;
		this.exclusionFlag = exclusionFlag;
		this.projectId = projectId;
		this.projectName = projectName;

	}

	public void addActor(String actor) {
		getActors().add(actor);
	}

	public void addResource(String resource) {
		getResources().add(resource);
	}

	public void addEpisode(StructuredEpisode episode) {
		getEpisodes().add(episode);
	}

	public void addAlternative(StructuredAlternative alternative) {
		getAlternative().add(alternative);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public StructuredContext getContext() {
		return context;
	}

	public void setContext(StructuredContext context) {
		this.context = context;
	}

	public List<String> getActors() {
		return actors;
	}

	public void setActors(List<String> actors) {
		this.actors = actors;
	}

	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}

	public List<StructuredEpisode> getEpisodes() {
		return episodes;
	}

	public void setEpisodes(List<StructuredEpisode> episodes) {
		this.episodes = episodes;
	}

	public List<StructuredAlternative> getAlternative() {
		return alternative;
	}

	public void setAlternative(List<StructuredAlternative> alternative) {
		this.alternative = alternative;
	}

	public java.util.Calendar getInclusionDate() {
		return inclusionDate;
	}

	public void setInclusionDate(java.util.Calendar inclusionDate) {
		this.inclusionDate = inclusionDate;
	}

	public Boolean getExclusionFlag() {
		return exclusionFlag;
	}

	public void setExclusionFlag(Boolean exclusionFlag) {
		this.exclusionFlag = exclusionFlag;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public CustomSentenceNlpInfo getTitleNlp() {
		return titleNlp;
	}

	public void setTitleNlp(CustomSentenceNlpInfo titleNlp) {
		this.titleNlp = titleNlp;
	}

	public CustomSentenceNlpInfo getGoalNlp() {
		return goalNlp;
	}

	public void setGoalNlp(CustomSentenceNlpInfo goalNlp) {
		this.goalNlp = goalNlp;
	}

	
}


