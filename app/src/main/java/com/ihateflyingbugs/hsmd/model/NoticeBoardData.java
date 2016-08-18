package com.ihateflyingbugs.hsmd.model;

/**
 * Created by 영철 on 2016-05-23.
 */
public class NoticeBoardData {


    public final static int NOTICE = 1;
    public final static int LEVEL_UP = 2;
    public final static int STUDY_FEEDBACK = 3;

    int feedType;
    String date;
    String title;
    String contents;

    int total_review_count;
    int do_review_count;
    int new_study_count;

    public NoticeBoardData(int type, String date, String title, String contents){
        feedType = type;
        this.date = date;
        this.title = title;
        this.contents = contents;
    }

    public NoticeBoardData(int type, String date, String title, int total_count, int do_count, int new_count){
        feedType = type;
        this.date = date;
        this.title = title;
        total_review_count = total_count;
        do_review_count= do_count;
        new_study_count = new_count;
    }

    public int getType(){
        return feedType;
    }
    public String getDate(){
        return date;
    }
    public String getTitle(){
        return title;
    }
    public String getContents(){
        return contents;
    }

    public int getTotal_review_count(){
        return total_review_count;
    }
    public int getDo_review_count(){
        return do_review_count;
    }
    public int getNew_study_count(){
        return new_study_count;
    }
}
