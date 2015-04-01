package com.se.users;

import com.se.users.action.Action;
import com.se.users.action.pdfupdate.development.Eng;


public  class ActionFactory
{
	public Action getUserType(String user) {

		 if(user.equals("ParametricEngNewGUI")){
		 return new Eng();//new Eng();
		// }else if(user.equals("ParametricEngNew")){
		// return new ParametricEngNew();
		// }else if(user.equals("ParametricTLNew")){
		// return new ParametricTLNew();
		// }else if(user.equals("QualityEngNew")){
		// return new QualityEngNew();
		//
		// }else{
		// return null;
		 }
		return null;
	}
}
