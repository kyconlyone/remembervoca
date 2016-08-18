package com.ihateflyingbugs.hsmd.manager;

public class SelectedExamWord {
	
	public SelectedExamWord(int wordCode, String word_String, int forgetRate) {
		super();
		WordCode = wordCode;
		Word_String = word_String;
		ForgetRate = forgetRate;
	}
	
	
	int WordCode;
	String Word_String;
	int ForgetRate;
	
	
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
	public int getForgetRate() {
		return ForgetRate;
	}
	public void setForgetRate(int forgetRate) {
		ForgetRate = forgetRate;
	}
}
