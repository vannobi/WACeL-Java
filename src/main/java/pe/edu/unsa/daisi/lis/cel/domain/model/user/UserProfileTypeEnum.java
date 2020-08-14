package pe.edu.unsa.daisi.lis.cel.domain.model.user;

import java.io.Serializable;

public enum UserProfileTypeEnum implements Serializable{
	USER("USER"),
	DBA("DBA"),
	ADMIN("ADMIN"),
	GUEST("GUEST");
	
	String userProfileType;
	
	private UserProfileTypeEnum(String userProfileType){
		this.userProfileType = userProfileType;
	}
	
	public String getUserProfileType(){
		return userProfileType;
	}
	
}
