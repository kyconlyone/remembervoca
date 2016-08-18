package com.ihateflyingbugs.hsmd.data;

public class userDB {

	String forgetting_curves_tbl;
	String calendar_tbl;
	String words_tbl;
	String time;

	public String getCalendar_tbl() {
		return calendar_tbl;
	}
	public void setCalendar_tbl(String calendar_tbl) {
		this.calendar_tbl = calendar_tbl;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public userDB(String curve, String word, String calendar,String time ){
		this.words_tbl = word;
		this.forgetting_curves_tbl = curve;
		this.time = time;
		this.calendar_tbl = calendar;
	}
	public String getForgetting_curves_tbl() {
		return forgetting_curves_tbl;
	}
	public void setForgetting_curves_tbl(String forgetting_curves_tbl) {
		this.forgetting_curves_tbl = forgetting_curves_tbl;
	}
	public String getWords_tbl() {
		return words_tbl;
	}
	public void setWords_tbl(String words_tbl) {
		this.words_tbl = words_tbl;
	}
}
