package com.se.parametric.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.se.grm.client.mapping.GrmApplication;
import com.se.grm.client.mapping.GrmGroup;
import com.se.grm.client.mapping.GrmGroupMessege;
import com.se.grm.client.mapping.GrmRequest;
import com.se.grm.client.mapping.GrmRequestAttributeValue;
import com.se.grm.client.mapping.GrmRequestCategory;
import com.se.grm.client.mapping.GrmRequestMessege;
import com.se.grm.client.mapping.GrmRequestStatusHistory;
import com.se.grm.client.mapping.GrmRole;
import com.se.grm.client.mapping.GrmUser;
import com.se.grm.client.mapping.GrmUserRequestStar;

public class GrmUserDTO {
	private static final long serialVersionUID = -8830100909986109948L;
	private long id;
	private GrmRole grmRole;
	private String groupName ;
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public GrmRole getGrmRole() {
		return grmRole;
	}
	public void setGrmRole(GrmRole grmRole) {
		this.grmRole = grmRole;
	}
	public GrmGroup getGrmGroup() {
		return grmGroup;
	}
	public void setGrmGroup(GrmGroup grmGroup) {
		this.grmGroup = grmGroup;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getStoreDate() {
		return storeDate;
	}
	public void setStoreDate(Date storeDate) {
		this.storeDate = storeDate;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public long getEnable() {
		return enable;
	}
	public void setEnable(long enable) {
		this.enable = enable;
	}
	public Date getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
	public Date getLastLogout() {
		return lastLogout;
	}
	public void setLastLogout(Date lastLogout) {
		this.lastLogout = lastLogout;
	}
	public GrmUser getLeader() {
		return leader;
	}
	public void setLeader(GrmUser leader) {
		this.leader = leader;
	}
	public Set<GrmUserRequestStar> getGrmUserRequestStars() {
		return grmUserRequestStars;
	}
	public void setGrmUserRequestStars(Set<GrmUserRequestStar> grmUserRequestStars) {
		this.grmUserRequestStars = grmUserRequestStars;
	}
	public Set<GrmRequestMessege> getGrmRequestMesseges() {
		return grmRequestMesseges;
	}
	public void setGrmRequestMesseges(Set<GrmRequestMessege> grmRequestMesseges) {
		this.grmRequestMesseges = grmRequestMesseges;
	}
	public Set<GrmRequestAttributeValue> getGrmRequestAttributeValues() {
		return grmRequestAttributeValues;
	}
	public void setGrmRequestAttributeValues(Set<GrmRequestAttributeValue> grmRequestAttributeValues) {
		this.grmRequestAttributeValues = grmRequestAttributeValues;
	}
	public Set<GrmRequest> getGrmRequests() {
		return grmRequests;
	}
	public void setGrmRequests(Set<GrmRequest> grmRequests) {
		this.grmRequests = grmRequests;
	}
	public Set<GrmRequestCategory> getGrmRequestCategories() {
		return grmRequestCategories;
	}
	public void setGrmRequestCategories(Set<GrmRequestCategory> grmRequestCategories) {
		this.grmRequestCategories = grmRequestCategories;
	}
	public Set<GrmGroupMessege> getGrmGroupMesseges() {
		return grmGroupMesseges;
	}
	public void setGrmGroupMesseges(Set<GrmGroupMessege> grmGroupMesseges) {
		this.grmGroupMesseges = grmGroupMesseges;
	}
	public Set<GrmRequestStatusHistory> getGrmRequestStatusHistories() {
		return grmRequestStatusHistories;
	}
	public void setGrmRequestStatusHistories(Set<GrmRequestStatusHistory> grmRequestStatusHistories) {
		this.grmRequestStatusHistories = grmRequestStatusHistories;
	}
	public Set<GrmApplication> getGrmApplications() {
		return grmApplications;
	}
	public void setGrmApplications(Set<GrmApplication> grmApplications) {
		this.grmApplications = grmApplications;
	}
	public Set<GrmUser> getDevelopers() {
		return developers;
	}
	public void setDevelopers(Set<GrmUser> developers) {
		this.developers = developers;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private GrmGroup grmGroup;
	private String fullName;
	private String email;
	private Date storeDate;
	private String password;
	private long enable;
	private Date lastLogin;
	private Date lastLogout;
	private GrmUser leader;
	private Set<GrmUserRequestStar> grmUserRequestStars = new HashSet<GrmUserRequestStar>(0);
	private Set<GrmRequestMessege> grmRequestMesseges = new HashSet<GrmRequestMessege>(0);
	private Set<GrmRequestAttributeValue> grmRequestAttributeValues = new HashSet<GrmRequestAttributeValue>(0);
	private Set<GrmRequest> grmRequests = new HashSet<GrmRequest>(0);
	private Set<GrmRequestCategory> grmRequestCategories = new HashSet<GrmRequestCategory>(0);
	private Set<GrmGroupMessege> grmGroupMesseges = new HashSet<GrmGroupMessege>(0);
	private Set<GrmRequestStatusHistory> grmRequestStatusHistories = new HashSet<GrmRequestStatusHistory>(0);
	private Set<GrmApplication> grmApplications = new HashSet<GrmApplication>(0);
	private Set<GrmUser> developers = new HashSet<GrmUser>(0);
	

}
