package com.ihateflyingbugs.hsmd.manager;

public class ExamQuestion {

	public ExamQuestion(int rowid, int questionNum, int wordCode, String word_String,
			String example1_String, String example2_String,
			String example3_String, String example4_String) {
		super();
		RowID = rowid;
		QuestionNum = questionNum;
		WordCode = wordCode;
		Word_String = word_String;
		Example1_String = example1_String;
		Example2_String = example2_String;
		Example3_String = example3_String;
		Example4_String = example4_String;
	}

	int RowID;
	
	int QuestionNum;
	int WordCode;
	String Word_String;
	
	String Example1_String;
	String Example2_String;
	String Example3_String;
	String Example4_String;
	
	int SelectedNum;
	
	String Sentence;

	
	
	public int getRowID() {
		return RowID;
	}

	public void setRowID(int rowID) {
		RowID = rowID;
	}

	public int getQuestionNum() {
		return QuestionNum;
	}

	public void setQuestionNum(int questionNum) {
		QuestionNum = questionNum;
	}

	public int getWordCode() {
		return WordCode;
	}

	public void setWordCode(int wordCode) {
		WordCode = wordCode;
	}

	public String getWord_String() {
		return Word_String;
	}

	public void setWord_String(String word_String) {
		Word_String = word_String;
	}

	public String getExample1_String() {
		return Example1_String;
	}

	public void setExample1_String(String example1_String) {
		Example1_String = example1_String;
	}

	public String getExample2_String() {
		return Example2_String;
	}

	public void setExample2_String(String example2_String) {
		Example2_String = example2_String;
	}

	public String getExample3_String() {
		return Example3_String;
	}

	public void setExample3_String(String example3_String) {
		Example3_String = example3_String;
	}

	public String getExample4_String() {
		return Example4_String;
	}

	public void setExample4_String(String example4_String) {
		Example4_String = example4_String;
	}

	public int getSelectedNum() {
		return SelectedNum;
	}

	public void setSelectedNum(int selectedNum) {
		SelectedNum = selectedNum;
	}

	public String getSentence() {
		return Sentence;
	}

	public void setSentence(String sentence) {
		Sentence = sentence;
	}
	
	
}
