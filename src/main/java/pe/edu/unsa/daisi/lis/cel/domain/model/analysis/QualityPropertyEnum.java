package pe.edu.unsa.daisi.lis.cel.domain.model.analysis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public enum QualityPropertyEnum implements Serializable{
	VAGUENESS("Vagueness"),
	SUBJECTIVENESS("Subjectiveness"),
	OPTIONALITY("Optionality"),
	WEAKNESS("Weakness"),
	MULTIPLICITY("Multiplicity"),
	IMPLICITY("Implicity"),
	QUANTIFIABILITY("Quantifiability"),
	MINIMALITY("Minimality"),
	READABILITY("Readability"),
	ATOMICITY("Atomicity"),
	SIMPLICITY("Simplicity"),
	UNIFORMITY("Uniformity"),
	USEFULNESS("Usefulness"),
	CONCEPTUALLY_SOUNDNESS("Conceptually Soundness"),
	INTEGRITY("Integrity"),
	COHERENCY("Coherency"),
	UNIQUENESS("Uniqueness"),
	FEASIBILITY("Feasibility"),
	//CONSISTENCY("Consistency"),
	NON_INTERFERENTIAL("Non-interferential"),
	BOUNDEDNESS("Boundedness"),
	SAFENESS("Safeness"),
	REVERSIBILITY("Reversibility"),
	LIVENESS("Liveness")	;
	
	String qualityProperty;
	
	private QualityPropertyEnum(String qualityProperty){
		this.qualityProperty = qualityProperty;
	}
	
	public String getQualityProperty(){
		return qualityProperty;
	}
	
	public static List<String> getUnambiguityProperties(){
		List<String> unAmbiguityProperties = new ArrayList<String>();
		unAmbiguityProperties.add(QualityPropertyEnum.READABILITY.getQualityProperty());
		unAmbiguityProperties.add(QualityPropertyEnum.VAGUENESS.getQualityProperty());
		unAmbiguityProperties.add(QualityPropertyEnum.SUBJECTIVENESS.getQualityProperty());
		unAmbiguityProperties.add(QualityPropertyEnum.OPTIONALITY.getQualityProperty());
		unAmbiguityProperties.add(QualityPropertyEnum.WEAKNESS.getQualityProperty());
		unAmbiguityProperties.add(QualityPropertyEnum.MULTIPLICITY.getQualityProperty());
		unAmbiguityProperties.add(QualityPropertyEnum.IMPLICITY.getQualityProperty());
		unAmbiguityProperties.add(QualityPropertyEnum.QUANTIFIABILITY.getQualityProperty());
		unAmbiguityProperties.add(QualityPropertyEnum.MINIMALITY.getQualityProperty());
				
		return unAmbiguityProperties;
	}
	
	public static List<String> getCompletenessProperties(){
		List<String> completenessProperties = new ArrayList<String>();
		completenessProperties.add(QualityPropertyEnum.ATOMICITY.getQualityProperty());
		completenessProperties.add(QualityPropertyEnum.SIMPLICITY.getQualityProperty());
		completenessProperties.add(QualityPropertyEnum.UNIFORMITY.getQualityProperty());
		completenessProperties.add(QualityPropertyEnum.USEFULNESS.getQualityProperty());
		completenessProperties.add(QualityPropertyEnum.CONCEPTUALLY_SOUNDNESS.getQualityProperty());
		completenessProperties.add(QualityPropertyEnum.INTEGRITY.getQualityProperty());
		completenessProperties.add(QualityPropertyEnum.COHERENCY.getQualityProperty());
		completenessProperties.add(QualityPropertyEnum.UNIQUENESS.getQualityProperty());
		completenessProperties.add(QualityPropertyEnum.FEASIBILITY.getQualityProperty());
				
		return completenessProperties;
	}
	
	public static List<String> getConsistencyProperties(){
		List<String> consistencyProperties = new ArrayList<String>();
		//consistencyProperties.add(QualityProperty.CONSISTENCY.getQualityProperty());
		
		consistencyProperties.add(QualityPropertyEnum.NON_INTERFERENTIAL.getQualityProperty());
		consistencyProperties.add(QualityPropertyEnum.BOUNDEDNESS.getQualityProperty());
		consistencyProperties.add(QualityPropertyEnum.LIVENESS.getQualityProperty());
		consistencyProperties.add(QualityPropertyEnum.REVERSIBILITY.getQualityProperty());
		
		return consistencyProperties;
	}
	
}
