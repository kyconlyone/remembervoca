package com.ihateflyingbugs.hsmd.manager;

import java.util.Date;

public class ExamResult_Questions {

	public ExamResult_Questions(int questionNumber, int rowid, int questionWordcode, String questionWord, int correctNumber, String exampleWord1,
			String exampleWord2, String exampleWord3, String exampleWord4,
			int frequencyCount_EBS, int frequencyCount_SN,
			int frequencyCount_PGW, double forgetRate, int studentCheckedNumber) {
		super();
		QuestionNumber = questionNumber;
		rowID = rowid;
		QuestionWordCode = questionWordcode;
		QuestionWord_String = questionWord;
		CorrectNumber = correctNumber;
		ExampleWord1 = exampleWord1;
		ExampleWord2 = exampleWord2;
		ExampleWord3 = exampleWord3;
		ExampleWord4 = exampleWord4;
		FrequencyCount_EBS = frequencyCount_EBS;
		FrequencyCount_SN = frequencyCount_SN;
		FrequencyCount_PGW = frequencyCount_PGW;
		ForgetRate = forgetRate;
		StudentCheckedNumber = studentCheckedNumber;
	}
	
	public ExamResult_Questions(int questionNumber, int rowid, int questionWordcode, String questionWord, int correctNumber, String exampleWord1,
			String exampleWord2, String exampleWord3, String exampleWord4,
			double forgetRate, int studentCheckedNumber) {
		super();
		QuestionNumber = questionNumber;
		rowID = rowid;
		QuestionWordCode = questionWordcode;
		QuestionWord_String = questionWord;
		CorrectNumber = correctNumber;
		ExampleWord1 = exampleWord1;
		ExampleWord2 = exampleWord2;
		ExampleWord3 = exampleWord3;
		ExampleWord4 = exampleWord4;
		ForgetRate = forgetRate;
		StudentCheckedNumber = studentCheckedNumber;
	}

	Date ExamDate;
	int QuestionNumber;
	
	int rowID;
	
	int QuestionWordCode;
	String QuestionWord_String;
	int CorrectNumber;		// 1����4���� ���� 
	
	String ExampleWord1;
	String ExampleWord2;
	String ExampleWord3;
	String ExampleWord4;
	
	int FrequencyCount_EBS;
	int FrequencyCount_SN;
	int FrequencyCount_PGW;
	double ForgetRate;
	
	int StudentCheckedNumber;
	
	String Sentence;
	String Sentence_Mean;
	String Sentence_Info;

	public int getQuestionNumber() {
		return QuestionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		QuestionNumber = questionNumber;
	}


	public int getRowID() {
		return rowID;
	}

	public void setRowID(int rowID) {
		this.rowID = rowID;
	}

	public int getQuestionWordCode() {
		return QuestionWordCode;
	}

	public void setQuestionWordCode(int questionWordCode) {
		QuestionWordCode = questionWordCode;
	}

	public String getQuestionWord_String() {
		return QuestionWord_String;
	}

	public void setQuestionWord_String(String questionWord_String) {
		QuestionWord_String = questionWord_String;
	}

	public int getCorrectNumber() {
		return CorrectNumber;
	}

	public void setCorrectNumber(int correctNumber) {
		CorrectNumber = correctNumber;
	}

	public String getExampleWord1() {
		return ExampleWord1;
	}

	public void setExampleWord1(String exampleWord1) {
		ExampleWord1 = exampleWord1;
	}

	public String getExampleWord2() {
		return ExampleWord2;
	}

	public void setExampleWord2(String exampleWord2) {
		ExampleWord2 = exampleWord2;
	}

	public String getExampleWord3() {
		return ExampleWord3;
	}

	public void setExampleWord3(String exampleWord3) {
		ExampleWord3 = exampleWord3;
	}

	public String getExampleWord4() {
		return ExampleWord4;
	}

	public void setExampleWord4(String exampleWord4) {
		ExampleWord4 = exampleWord4;
	}

	public int getFrequencyCount_EBS() {
		return FrequencyCount_EBS;
	}

	public void setFrequencyCount_EBS(int frequencyCount_EBS) {
		FrequencyCount_EBS = frequencyCount_EBS;
	}

	public int getFrequencyCount_SN() {
		return FrequencyCount_SN;
	}

	public void setFrequencyCount_SN(int frequencyCount_SN) {
		FrequencyCount_SN = frequencyCount_SN;
	}

	public int getFrequencyCount_PGW() {
		return FrequencyCount_PGW;
	}

	public void setFrequencyCount_PGW(int frequencyCount_PGW) {
		FrequencyCount_PGW = frequencyCount_PGW;
	}
	
	public double getForgetRate() {
		return ForgetRate;
	}

	public void setForgetRate(double forgetRate) {
		ForgetRate = forgetRate;
	}

	public int getStudentCheckedNumber() {
		return StudentCheckedNumber;
	}

	public void setStudentCheckedNumber(int studentCheckedNumber) {
		StudentCheckedNumber = studentCheckedNumber;
	}

	public String getSentence() {
		return Sentence;
	}

	public void setSentence(String sentence) {
		Sentence = sentence;
	}

	public String getSentence_Mean() {
		return Sentence_Mean;
	}

	public void setSentence_Mean(String sentence_Mean) {
		Sentence_Mean = sentence_Mean;
	}

	public String getSentence_Info() {
		return Sentence_Info;
	}

	public void setSentence_Info(String sentence_Info) {
		Sentence_Info = sentence_Info;
	}
	
	
}
