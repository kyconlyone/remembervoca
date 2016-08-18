package com.ihateflyingbugs.hsmd.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.transform.Source;

public class ExamContents {

	String title;
	int hitPercent;
	int grade_1;
	int grade_23;
	int grade_45;
	int grade_67;
	int grade_89;

	public ExamContents(String title, int hitPercent, int grade_1,
			int grade_23, int grade_45, int grade_67, int grade_89) {
		this.title = title;
		this.hitPercent = hitPercent;
		this.grade_1 = grade_1;
		this.grade_23 = grade_23;
		this.grade_45 = grade_45;
		this.grade_67 = grade_67;
		this.grade_89 = grade_89;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setHitPercent(int hitPercent) {
		this.hitPercent = hitPercent;
	}

	public void setGrade_1(int grade) {
		this.grade_1 = grade;
	}

	public void setGrade_23(int grade) {
		this.grade_23 = grade;
	}

	public void setGrade_45(int grade) {
		this.grade_45 = grade;
	}

	public void setGrade_67(int grade) {
		this.grade_67 = grade;
	}

	public void setGrade_89(int grade) {
		this.grade_89 = grade;
	}

	public String getTitle() {
		return this.title;
	}

	public int getHitPercent() {
		return this.hitPercent;
	}

	public int getGrade_1() {
		return this.grade_1;
	}

	public int getGrade_23() {
		return this.grade_23;
	}

	public int getGrade_45() {
		return this.grade_45;
	}

	public int getGrade_67() {
		return this.grade_67;
	}

	public int getGrade_89() {
		return this.grade_89;
	}
}
