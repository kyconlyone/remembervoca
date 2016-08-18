package com.ihateflyingbugs.hsmd.data;

public class MyQnA {

	int qna_id;
	String student_id;
	String phone_number;
	String title;
	String question_time ;
	String qna_question;
	String is_answer;
	String answer_admin;
	String qna_answer ;
	String answer_time;

	public MyQnA(){

	}

	public int getQna_id() {
		return qna_id;
	}

	public void setQna_id(int qna_id) {
		this.qna_id = qna_id;
	}

	public String getStudent_id() {
		return student_id;
	}

	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}

	public String getPhone_number() {
		return phone_number;
	}

	public void setPhone_number(String phone_number) {
		this.phone_number = phone_number;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getQuestion_time() {
		return question_time;
	}

	public void setQuestion_time(String question_time) {
		this.question_time = question_time;
	}

	public String getQna_question() {
		return qna_question;
	}

	public void setQna_question(String qna_question) {
		this.qna_question = qna_question;
	}

	public String getIs_answer() {
		return is_answer;
	}

	public void setIs_answer(String is_answer) {
		this.is_answer = is_answer;
	}

	public String getAnswer_admin() {
		return answer_admin;
	}

	public void setAnswer_admin(String answer_admin) {
		this.answer_admin = answer_admin;
	}

	public String getQna_answer() {
		return qna_answer;
	}

	public void setQna_answer(String qna_answer) {
		this.qna_answer = qna_answer;
	}

	public String getAnswer_time() {
		return answer_time;
	}

	public void setAnswer_time(String answer_time) {
		this.answer_time = answer_time;
	}

	public MyQnA(int qna_id, String id, String title, String question_time, String qna_question, String flag){
		this.qna_id = qna_id;
		this.student_id = student_id;
		this.title = title;
		this.question_time = question_time;
		this.qna_question = qna_question;
		this.is_answer = is_answer;

	}
	


	public void setA_flag(String is_answer){
		this.is_answer = is_answer;
	}
	public void setAnswer(String answer, String Adate){
		this.qna_answer = qna_answer;
		this.answer_time = Adate;
	}

	public String getTitle(){
		return title;

	}
	public String getQdate(){
		return question_time;

	}
	public String getQuestion(){
		return qna_question;

	}
	public String getAflag(){
		return is_answer;

	}
	public String getAnswer(){
		return qna_answer;

	}public String getdAnswer(){
		return answer_time;

	}

}
