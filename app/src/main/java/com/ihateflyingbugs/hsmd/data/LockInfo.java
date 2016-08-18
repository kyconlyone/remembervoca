package com.ihateflyingbugs.hsmd.data;

public class LockInfo {
	
	String user_id;
	int during;
	int word_count;
	int info_type;
	int finish_type;
	String time;
	
	public int getDuring() {
		return during;
	}

	public void setDuring(int during) {
		this.during = during;
	}

	public int getWord_count() {
		return word_count;
	}

	public void setWord_count(int word_count) {
		this.word_count = word_count;
	}

	public int getInfo_type() {
		return info_type;
	}

	public void setInfo_type(int info_type) {
		this.info_type = info_type;
	}

	public int getFinish_type() {
		return finish_type;
	}

	public void setFinish_type(int finish_type) {
		this.finish_type = finish_type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public LockInfo(String user_id, int during, int word_count, int info_type, int finish_type, String time){
		this.during = during;
		this.word_count = word_count;
		this.info_type = info_type;
		this.finish_type = finish_type;
		this.time = time;
	}
	
}
