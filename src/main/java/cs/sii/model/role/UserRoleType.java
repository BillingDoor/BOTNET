package cs.sii.model.role;

import java.io.Serializable;

public enum UserRoleType implements Serializable{
	USER("USER"),
	DBA("DBA"),
	ADMIN("ADMIN"),
	BOT("BOT");
	
	String userProfileType;
	
	private UserRoleType(String userProfileType){
		this.userProfileType = userProfileType;
	}
	
	public String getUserProfileType(){
		return userProfileType;
	}
	
}
