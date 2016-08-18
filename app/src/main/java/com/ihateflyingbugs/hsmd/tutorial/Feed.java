package com.ihateflyingbugs.hsmd.tutorial;

public class Feed {

	public final static int NOTICE = 1;
	public final static int LEVEL_UP = 2;
	public final static int STUDY_FEEDBACK = 3;

	int feedType;
	int notice_id;
	String reg_time;
	String notice_title;
	String notice_contents;



	String notice_context;

//	String notice_date;
//	String notice_title;
//	String notice_contents;
//	String reg_time;

	int total_review_count;
	int do_review_count;
	int new_study_count;

	public Feed(){

	}

	public Feed(int type, String notice_date, String notice_title,String contents){
		feedType = type;
		this.reg_time = notice_date;
		this.notice_title = notice_title;
		this.notice_contents = notice_contents;


	}

	public Feed(int type, String notice_date, String notice_title, int total_count, int do_count, int new_count){
		feedType = type;
		this.reg_time = notice_date;
		this.notice_title = notice_title;
		total_review_count = total_count;
		do_review_count= do_count;
		new_study_count = new_count;
	}

	public String getNotice_context() {
		return notice_contents;
	}

	public void setNotice_context(String notice_context) {
		this.notice_contents = notice_context;
	}

	public static int getNOTICE() {
		return NOTICE;
	}

	public static int getLevelUp() {
		return LEVEL_UP;
	}

	public static int getStudyFeedback() {
		return STUDY_FEEDBACK;
	}

	public int getFeedType() {
		return feedType;
	}

	public void setFeedType(int feedType) {
		this.feedType = feedType;
	}

	public int getNotice_id() {
		return notice_id;
	}

	public void setNotice_id(int notice_id) {
		this.notice_id = notice_id;
	}

	public String getReg_time() {
		return reg_time;
	}

	public void setReg_time(String reg_time) {
		this.reg_time = reg_time;
	}

	public String getNotice_title() {
		return notice_title;
	}

	public void setNotice_title(String notice_title) {
		this.notice_title = notice_title;
	}

	public String getNotice_contents() {
		return notice_contents;
	}

	public void setNotice_contents(String notice_contents) {
		this.notice_contents = notice_contents;
	}

	public void setTotal_review_count(int total_review_count) {
		this.total_review_count = total_review_count;
	}

	public void setDo_review_count(int do_review_count) {
		this.do_review_count = do_review_count;
	}

	public void setNew_study_count(int new_study_count) {
		this.new_study_count = new_study_count;
	}

	public int getType(){
		return feedType;
	}
	public String getDate(){
		return reg_time;
	}
	public String getTitle(){
		return notice_title;
	}
	public String getContents(){
		return notice_contents;
	}

	public int getTotal_review_count(){
		return total_review_count;
	}
	public int getDo_review_count(){
		return do_review_count;
	}
	public int getNew_study_count(){
		return new_study_count;
	}


}
