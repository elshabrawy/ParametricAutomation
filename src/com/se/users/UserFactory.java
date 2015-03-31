package com.se.users;

public class UserFactory
{
public UserType getUser(String user){
	
	if(user.equals("ParametricEngUpdate")){
		return new ParametricEngUpdate();
		}else if(user.equals("ParametricEngNew")){
			return new ParametricEngNew();
		}else if(user.equals("ParametricTLNew")){
			return new ParametricTLNew();
		}else if(user.equals("QualityEngNew")){
			return new QualityEngNew();
			
	}else{
		return null;
	}
}
}
