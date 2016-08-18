package com.ihateflyingbugs.hsmd.model;

import java.util.List;

/**
 * Created by 영철 on 2016-05-23.
 */
public class StudyInfoData {

    int result;
    int count;
    String status;
    String averageUseTimeOfUser;
    String averageUseTimeAsAge;
    String theNumberOfPeople;
    String userRankAsUseTime;
    String goneUpRankAsUseTime;
    String averageUseTimeAsAgeMonth;
    String averageUseTimeAsAgeAndGrade;
    String averageUseTimeAsAgeAndGradeMonth;
    String notice_title;
    String notice_body;
    List<WorkBook> workbook_list;


    public StudyInfoData(){

    }

    public List<WorkBook> getWorkbook_list() {
        return workbook_list;
    }

    public void setWorkbook_list(List<WorkBook> workbook_list) {
        this.workbook_list = workbook_list;
    }

    public String getNotice_body() {
        return notice_body;
    }

    public void setNotice_body(String notice_body) {
        this.notice_body = notice_body;
    }

    public String getNotice_title() {
        return notice_title;
    }

    public void setNotice_title(String notice_title) {
        this.notice_title = notice_title;
    }

    public double getAverageUseTimeOfUser() {
        return Double.valueOf(averageUseTimeOfUser);
    }

    public void setAverageUseTimeOfUser(double averageUseTimeOfUser) {
        this.averageUseTimeOfUser = String.valueOf(averageUseTimeOfUser);
    }

    public double getAverageUseTimeAsAge() {
        return Double.valueOf(averageUseTimeAsAge);
    }

    public void setAverageUseTimeAsAge(double averageUseTimeAsAge) {
        this.averageUseTimeAsAge = String.valueOf(averageUseTimeAsAge);
    }

    public int getTheNumberOfPeople() {
        return Integer.valueOf(theNumberOfPeople);
    }

    public void setTheNumberOfPeople(int theNumberOfPeople) {
        this.theNumberOfPeople = String.valueOf(theNumberOfPeople);
    }

    public int getUserRankAsUseTime() {
        return Integer.valueOf(userRankAsUseTime);
    }

    public void setUserRankAsUseTime(int userRankAsUseTime) {
        this.userRankAsUseTime = String.valueOf(userRankAsUseTime);
    }

    public int getGoneUpRankAsUseTime() {
        return Integer.valueOf(goneUpRankAsUseTime);
    }

    public void setGoneUpRankAsUseTime(int goneUpRankAsUseTime) {
        this.goneUpRankAsUseTime = String.valueOf(goneUpRankAsUseTime);
    }

    public double getAverageUseTimeAsAgeMonth() {
        return Double.valueOf(averageUseTimeAsAgeMonth);
    }

    public void setAverageUseTimeAsAgeMonth(double averageUseTimeAsAgeMonth) {
        this.averageUseTimeAsAgeMonth = String.valueOf(averageUseTimeAsAgeMonth);
    }

    public double getAverageUseTimeAsAgeAndGrade() {
        return Double.valueOf(averageUseTimeAsAgeAndGrade);
    }

    public void setAverageUseTimeAsAgeAndGrade(double averageUseTimeAsAgeAndGrade) {
        this.averageUseTimeAsAgeAndGrade = String.valueOf(averageUseTimeAsAgeAndGrade);
    }

    public double getAverageUseTimeAsAgeAndGradeMonth() {
        return Double.valueOf(averageUseTimeAsAgeAndGradeMonth);
    }

    public void setAverageUseTimeAsAgeAndGradeMonth(double averageUseTimeAsAgeAndGradeMonth) {
        this.averageUseTimeAsAgeAndGradeMonth = String.valueOf(averageUseTimeAsAgeAndGradeMonth);
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class WorkBook{
        int num;
        String cat1;
        String cat2;
        String cat2_sub;
        String word_count;
        String published_date;
        int category;
        int sum;
        String word;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }
        public WorkBook(){

        }
        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public String getCat1() {
            return cat1;
        }

        public void setCat1(String cat1) {
            this.cat1 = cat1;
        }

        public String getCat2() {
            return cat2;
        }

        public void setCat2(String cat2) {
            this.cat2 = cat2;
        }

        public String getCat2_sub() {
            return cat2_sub;
        }

        public void setCat2_sub(String cat2_sub) {
            this.cat2_sub = cat2_sub;
        }

        public String getWord_count() {
            return word_count;
        }

        public void setWord_count(String word_count) {
            this.word_count = word_count;
        }

        public String getPublished_date() {
            return published_date;
        }

        public void setPublished_date(String published_date) {
            this.published_date = published_date;
        }
    }
}
