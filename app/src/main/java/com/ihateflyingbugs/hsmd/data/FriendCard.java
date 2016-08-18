package com.ihateflyingbugs.hsmd.data;

public class FriendCard {
	String friend_name;
	String friend_school;
	String friend_grade;
	double friend_use_days;
	String friend_goaltime;
	String friend_profile;
	

	public FriendCard(String name, String school, String grade, double date, String goaltime, String profile_url){

		friend_name = name;
		friend_school = school;
		friend_grade = grade;
		friend_use_days = date;
		friend_goaltime = goaltime;
		friend_profile = profile_url;
	}
	
	public String getName(){
		return friend_name;
	}
	public String getFriendSchool(){
		return friend_school;
	}
	public String getYears(){
		return friend_grade;
	}
	public double getDate(){
		return friend_use_days;
	}
	public String getGoaltime(){
		return friend_goaltime;
	}
	public String getProfileUrl(){
		return friend_profile;
	}
}
