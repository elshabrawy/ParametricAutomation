package com.se.parametric.util;

import java.util.HashMap;
import java.util.Map;

import com.se.automation.db.client.mapping.Pl;
import com.se.parametric.dto.FeatureDTO;

public class PDDRow {

	private String PN;
	private String family;
	private String familycross;
	private String generic;
	private String mask;
	private String convirsionpart;
	private Pl pl ;

	private Map features;


	public Pl getPl() {
		return pl;
	}
	public void setPl(Pl pl) {
		this.pl = pl;
	}
	public String getPN() {
		return PN;
	}
	public void setPN(String pN) {
		PN = pN;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public String getFamilycross() {
		return familycross;
	}
	public void setFamilycross(String familycross) {
		this.familycross = familycross;
	}
	public String getGeneric() {
		return generic;
	}
	public void setGeneric(String generic) {
		this.generic = generic;
	}
	public String getMask() {
		return mask;
	}
	public void setMask(String mask) {
		this.mask = mask;
	}
	public String getConvirsionpart() {
		return convirsionpart;
	}
	public void setConvirsionpart(String convirsionpart) {
		this.convirsionpart = convirsionpart;
	}
	public Map getFeatures() {
		return features;
	}
	public FeatureDTO getFeature(String featurename){
		if( features.containsKey(featurename))
		return (FeatureDTO)features.get(featurename);
		else
			return null;
	}

	public void addFeature(FeatureDTO f ){
		if(features== null)
			features = new HashMap<String,FeatureDTO>();

		features.put(f.getFeatureName(), f);
	}


}
