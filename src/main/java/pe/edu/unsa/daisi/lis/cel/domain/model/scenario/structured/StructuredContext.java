package pe.edu.unsa.daisi.lis.cel.domain.model.scenario.structured;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Structured Conext
 * @author Edgar
 *
 */
public class StructuredContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sentence;
	private List<String> temporalLocation; // TBD: A geographical Location has Constraints
	private List<String> geographicalLocation;// TBD: A temporal Location has Constraints
	private List<String> preConditions;// TBD: A pre-condition has Constraints
	private List<String> postConditions;


	public StructuredContext() {

	}

	/**
	 * Initialize Context
	 * @param context
	 * @return
	 */
	public void createContext(String sentence) {
		this.sentence = sentence;
		this.temporalLocation = new ArrayList<String>();
		this.geographicalLocation = new ArrayList<String>();
		this.preConditions = new ArrayList<String>();
		this.postConditions = new ArrayList<String>();
	}

	public void addTemporalLocation(String location ) {
		getTemporalLocation().add(location);
	}

	public void addGeographicalLocation(String location ) {
		getGeographicalLocation().add(location);
	}

	public void addPreCondition(String preCondition) {
		getPreConditions().add(preCondition);
	}

	public void addPostCondition(String postCondition) {
		getPostConditions().add(postCondition);
	}


	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public List<String> getTemporalLocation() {
		return temporalLocation;
	}
	public void setTemporalLocation(List<String> temporalLocation) {
		this.temporalLocation = temporalLocation;
	}
	public List<String> getGeographicalLocation() {
		return geographicalLocation;
	}
	public void setGeographicalLocation(List<String> geographicalLocation) {
		this.geographicalLocation = geographicalLocation;
	}
	public List<String> getPreConditions() {
		return preConditions;
	}
	public void setPreConditions(List<String> preConditions) {
		this.preConditions = preConditions;
	}
	public List<String> getPostConditions() {
		return postConditions;
	}
	public void setPostConditions(List<String> postConditions) {
		this.postConditions = postConditions;
	}

}