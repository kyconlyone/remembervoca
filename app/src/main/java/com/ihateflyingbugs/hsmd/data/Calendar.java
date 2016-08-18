package com.ihateflyingbugs.hsmd.data;

public class Calendar{



	int date;
	int study_time;
	int goal_time;
	int new_count;

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public int getStudy_time() {
		return study_time;
	}

	public void setStudy_time(int study_time) {
		this.study_time = study_time;
	}

	public int getGoal_time() {
		return goal_time;
	}

	public void setGoal_time(int goal_time) {
		this.goal_time = goal_time;
	}

	public int getNew_count() {
		return new_count;
	}

	public void setNew_count(int new_count) {
		this.new_count = new_count;
	}

	public int getReview_count() {
		return review_count;
	}

	public void setReview_count(int review_count) {
		this.review_count = review_count;
	}

	public int getWill_review_count() {
		return will_review_count;
	}

	public void setWill_review_count(int will_review_count) {
		this.will_review_count = will_review_count;
	}

	int review_count;
	int will_review_count;

	public Calendar(){

	}

	public Calendar(int date,int  study_time, int goal_time,int new_count,int review_count,int will_review_count){
		this.date = date;
		this.study_time= study_time;
		this.goal_time = goal_time;
		this.new_count = new_count;
		this.review_count = review_count;
		this.will_review_count =  will_review_count;
	}


	int student_id;
	int calendar_date;
	int goal_word_count;

	public int getStudent_id() {
		return student_id;
	}

	public void setStudent_id(int student_id) {
		this.student_id = student_id;
	}

	public int getCalendar_date() {
		return calendar_date;
	}

	public void setCalendar_date(int calendar_date) {
		this.calendar_date = calendar_date;
	}

	public int getGoal_word_count() {
		return goal_word_count;
	}

	public void setGoal_word_count(int goal_word_count) {
		this.goal_word_count = goal_word_count;
	}
}
