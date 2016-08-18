package com.ihateflyingbugs.hsmd.data;

public class FAQ {
	int faq_id;
	String p_number;
	String title;
	String reg_time ;
	String faq_question;
	String a_flag;
	String faq_answer ;
	String a_date;


	public int getFaq_id() {
		return faq_id;
	}

	public void setFaq_id(int faq_id) {
		this.faq_id = faq_id;
	}

	public String getP_number() {
		return p_number;
	}

	public void setP_number(String p_number) {
		this.p_number = p_number;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReg_time() {
		return reg_time;
	}

	public void setReg_time(String reg_time) {
		this.reg_time = reg_time;
	}

	public String getFaq_question() {
		return faq_question;
	}

	public void setFaq_question(String faq_question) {
		this.faq_question = faq_question;
	}

	public String getA_flag() {
		return a_flag;
	}

	public String getFaq_answer() {
		return faq_answer;
	}

	public void setFaq_answer(String faq_answer) {
		this.faq_answer = faq_answer;
	}

	public String getA_date() {
		return a_date;
	}

	public void setA_date(String a_date) {
		this.a_date = a_date;
	}

	public FAQ(){

	}
	

	public FAQ(int faq_id, String reg_time, String faq_question,String a_date,String faq_answer){
		this.faq_id = faq_id;
		this.reg_time = reg_time;
		this.faq_question = faq_question;
		this.a_date = a_date;
		this.faq_answer = faq_answer;
	}

	public void setA_flag(String a_flag){
		this.a_flag = a_flag;
	}
	public void setAnswer(String faq_answer, String Adate){
		this.faq_answer = faq_answer;
		this.a_date = Adate;
	}

	public String getTitle(){
		return title;

	}
	public String getQdate(){
		return reg_time;

	}
	public String getQuestion(){
		return faq_question;

	}
	public String getAflag(){
		return a_flag;

	}
	public String getAnswer(){
		return faq_answer;

	}
	public String getAdate(){
		return a_date;

	}

}
