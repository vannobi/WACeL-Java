package pe.edu.unsa.daisi.lis.cel.domain.model.analysis;

import java.io.Serializable;

public enum DefectCategoryEnum implements Serializable{
	ERROR("Error"),
	WARNING("Warning"),
	INFO("Information");
	
	String defectCategory;
	
	private DefectCategoryEnum(String defectCategory){
		this.defectCategory = defectCategory;
	}
	
	public String getDefectCategory(){
		return defectCategory;
	}
	
}
