package com.ihateflyingbugs.hsmd.model;

import java.util.List;

/**
 * Created by 영철 on 2016-05-23.
 */
public class WordData {
    int result;
    String status;
    int current_word_version;
    double setting_rate;
    double grade1=0;
    double grade2=0;
    double grade3=0;
    double grade4=0;
    double grade5=0;

    public double getGrade1() {
        return grade1;
    }

    public void setGrade1(double grade1) {
        this.grade1 = grade1;
    }

    public double getGrade2() {
        return grade2;
    }

    public void setGrade2(double grade2) {
        this.grade2 = grade2;
    }

    public double getGrade3() {
        return grade3;
    }

    public void setGrade3(double grade3) {
        this.grade3 = grade3;
    }

    public double getGrade4() {
        return grade4;
    }

    public void setGrade4(double grade4) {
        this.grade4 = grade4;
    }

    public double getGrade5() {
        return grade5;
    }

    public void setGrade5(double grade5) {
        this.grade5 = grade5;
    }


    List<StudyInfoData.WorkBook> workbook_list;

    public double getSetting_rate() {
        return setting_rate;
    }

    public void setSetting_rate(double setting_rate) {
        this.setting_rate = setting_rate;
    }

    public int getCurrent_word_version() {
        return current_word_version;
    }

    public void setCurrent_word_version(int current_word_version) {
        this.current_word_version = current_word_version;
    }

    public List<StudyInfoData.WorkBook> getWorkbook_list() {
        return workbook_list;
    }

    public void setWorkbook_list(List<StudyInfoData.WorkBook> workbook_list) {
        this.workbook_list = workbook_list;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
