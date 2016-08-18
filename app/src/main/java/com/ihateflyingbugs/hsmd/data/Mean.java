package com.ihateflyingbugs.hsmd.data;

public class Mean {

	private int _id;
	private String meaning;
	private int w_class;
	private int w_code;
	private int w_priority;
	private int ExSentence_Code;
	
	public Mean(){
		
	}
	
	public Mean(int _id, String meaning, int w_class, int w_priority, int ExSentence_Code){
		
		this._id = _id;
		this.meaning = meaning;
		this.w_class = w_class;
		this.w_priority = w_priority;
		this.ExSentence_Code = ExSentence_Code;
	}
	
	public Mean(int _id,int w_code,  int w_class, String meaning){
		
		this._id = _id;
		this.meaning = meaning;
		this.w_class = w_class;
		this.w_code = w_code;
	}

	public int get_id(){
		return _id;
	}
	
	public String getMeaning(){
		return meaning;
	}
	
	public int getMClass(){
		return w_class;
	}
	
	public int getMpriority(){
		return w_priority;
	}
	
	public int getMwCode(){
		return w_code;
	}
	
	public int getExSentence_Code(){
		return ExSentence_Code;
	}
}
