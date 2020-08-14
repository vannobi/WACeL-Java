package pe.edu.unsa.daisi.lis.cel.domain.model.analysis;

import java.io.Serializable;

import pe.edu.unsa.daisi.lis.cel.domain.model.scenario.Scenario;

/**
 * It is used to persist an indicator of violation of an specific quality property
 * @author Edgar
 *
 */
public class Defect  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -766565238241263736L;

	private Long id;
	
	private Long scenarioId;
	
	private String scenarioElement; // the scenario element/attribute with defect: trace
	
	private String qualityProperty; // the quality violated
	
	private String indicator;	// Description of the defect
	
	private String fixRecomendation; 	// Refactoring actions to fix the defect
	
	private String defectCategory; // Information, Warning or Error

	private String description; //toString();

	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(Long scenarioId) {
		this.scenarioId = scenarioId;
	}

	public String getScenarioElement() {
		return scenarioElement;
	}

	public void setScenarioElement(String scenarioElement) {
		this.scenarioElement = scenarioElement;
	}

	public String getQualityProperty() {
		return qualityProperty;
	}

	public void setQualityProperty(String qualityProperty) {
		this.qualityProperty = qualityProperty;
	}

	public String getIndicator() {
		return indicator;
	}

	public void setIndicator(String indicator) {
		this.indicator = indicator;
	}

	public String getFixRecomendation() {
		return fixRecomendation;
	}

	public void setFixRecomendation(String fixRecomendation) {
		this.fixRecomendation = fixRecomendation;
	}

	public String getDefectCategory() {
		return defectCategory;
	}

	public void setDefectCategory(String defectCategory) {
		this.defectCategory = defectCategory;
	}

	public String getDescription() {
		this.description = "";
				if(this.defectCategory.equals(DefectCategoryEnum.INFO.getDefectCategory()))
					this.description = this.description +	"<span class=\'text-info\'> <b>" + this.defectCategory + ": </b>"; 
				if(this.defectCategory.equals(DefectCategoryEnum.WARNING.getDefectCategory()))
					this.description = this.description +	"<span class=\'text-warning\'> <b>" + this.defectCategory + ": </b>";
				if(this.defectCategory.equals(DefectCategoryEnum.ERROR.getDefectCategory()))
					this.description = this.description +	"<span class=\'text-danger\'> <b>" + this.defectCategory + ": </b>";
				
				this.description = this.description + this.scenarioElement + "</span>" + 
									"<p> " + this.indicator + "</p>" + 
						"<span class=\"text-secondary\"><b>FIX: </b>" + this.fixRecomendation +" </span>";
		
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	
	
	
}
