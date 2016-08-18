package com.ihateflyingbugs.hsmd.manager;

import java.util.Date;

public class StudentInfo_ExamCard {
	
	public StudentInfo_ExamCard(String examcode, Date examDate, int examType, String examTypeContent, int count_Question,
			int count_Correct) {
		super();
		ExamCode_String = examcode;
		ExamDate = examDate;
		ExamType = examType;
		ExamtypeContent = examTypeContent;
		Count_Question = count_Question;
		Count_Correct = count_Correct;
	}
	
	String ExamCode_String;
	Date ExamDate;
	
	int ExamType;	 // 1:A  2:B  3:C
	String ExamtypeContent;
	
	int Count_Question;
	int Count_Correct;

	String ExamDate_String;
	int Count_incorrect;
	double CorrectRate;
	
	
	
	public String getExamCode_String() {
		return ExamCode_String;
	}
	public void setExamCode_String(String examCode_String) {
		ExamCode_String = examCode_String;
	}
	public Date getExamDate() {
		return ExamDate;
	}
	public void setExamDate(Date examDate) {
		ExamDate = examDate;
	}
	public int getExamType() {
		return ExamType;
	}
	public void setExamType(int examType) {
		ExamType = examType;
	}
	
	public String getExamtypeContent() {
		return ExamtypeContent;
	}
	public void setExamtypeContent(String examtypeContent) {
		ExamtypeContent = examtypeContent;
	}
	public int getCount_Question() {
		return Count_Question;
	}
	public void setCount_Question(int count_Question) {
		Count_Question = count_Question;
	}
	public int getCount_Correct() {
		return Count_Correct;
	}
	public void setCount_Correct(int count_Correct) {
		Count_Correct = count_Correct;
	}
	public String getExamDate_String() {
		return ExamDate_String;
	}
	public void setExamDate_String(String examDate_String) {
		ExamDate_String = examDate_String;
	}
	public int getCount_incorrect() {
		return Count_incorrect;
	}
	public void setCount_incorrect(int count_incorrect) {
		Count_incorrect = count_incorrect;
	}
	public double getCorrectRate() {
		return CorrectRate;
	}
	public void setCorrectRate(double correctRate) {
		CorrectRate = correctRate;
	}
}
