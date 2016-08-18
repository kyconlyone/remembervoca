package com.ihateflyingbugs.hsmd.manager;

import android.content.Context;

public class ManagerInfo {

	private static ManagerInfo instance = null;


	public static ManagerInfo getInstance(Context context) {
		if(instance == null) {
			instance = new ManagerInfo();
		}
		return instance;
	}

	void ManagerInfo() {
	}

	String AsyncMethodName_String;
	int AsyncReturnCode;
	String AsyncReturnMessage;
	String Teacher_id;


	String StudentID_String;
	String StudentCode_String;

	int ExamID;
	int ExamType;
	String ExamTypeContent_String;
	int ExamQuestionCount;

	String LastExamDate_String;


	int ApplyBetaService;


	public String getAsyncMethodName_String() {
		return AsyncMethodName_String;
	}

	public void setAsyncMethodName_String(String asyncMethodName_String) {
		AsyncMethodName_String = asyncMethodName_String;
	}

	public int getAsyncReturnCode() {
		return AsyncReturnCode;
	}

	public void setAsyncReturnCode(int asyncReturnCode) {
		AsyncReturnCode = asyncReturnCode;
	}

	public String getAsyncReturnMessage() {
		return AsyncReturnMessage;
	}

	public void setAsyncReturnMessage(String asyncReturnMessage) {
		AsyncReturnMessage = asyncReturnMessage;
	}

	public String getStudentID_String() {
		return StudentID_String;
	}

	public void setStudentID_String(String studentID_String) {
		StudentID_String = studentID_String;
	}

	public String getStudentCode_String() {
		return StudentCode_String;
	}

	public void setStudentCode_String(String studentCode_String) {
		StudentCode_String = studentCode_String;
	}

	public int getExamID() {
		return ExamID;
	}

	public void setExamID(int examID) {
		ExamID = examID;
	}

	public int getExamType() {
		return ExamType;
	}

	public void setExamType(int examType) {
		ExamType = examType;
	}

	public String getExamTypeContent_String() {
		return ExamTypeContent_String;
	}

	public void setExamTypeContent_String(String examTypeContent_String) {
		ExamTypeContent_String = examTypeContent_String;
	}

	public int getExamQuestionCount() {
		return ExamQuestionCount;
	}

	public void setExamQuestionCount(int examQuestionCount) {
		ExamQuestionCount = examQuestionCount;
	}

	public String getLastExamDate_String() {
		return LastExamDate_String;
	}

	public void setLastExamDate_String(String lastExamDate_String) {
		LastExamDate_String = lastExamDate_String;
	}

	public int getApplyBetaService() {
		return ApplyBetaService;
	}

	public void setApplyBetaService(int applyBetaService) {
		ApplyBetaService = applyBetaService;
	}


	public String getTeacher_id(){
		return Teacher_id;
	}
	public void setTeacher_id(String teacher_id){
		this.Teacher_id = teacher_id;
	}

}
