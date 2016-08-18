package com.ihateflyingbugs.hsmd.model;

import com.ihateflyingbugs.hsmd.data.Calendar;
import com.ihateflyingbugs.hsmd.data.ForgettingCurve;
import com.ihateflyingbugs.hsmd.data.Word;

import java.util.List;

/**
 * Created by 영철 on 2016-05-23.
 */
public class UseAppInfoData {

     int result;
    String status;
    String blog_url;
    String lock_screen_comment;
    int comment_type;
    int info_type;
    String type1;
    String type2;
    int user_count;
    int count;
    String address;
    String phonenum;

    List<Word> word_tbl;
    List<ForgettingCurve> forgetting_curves_tbl;
    List<Calendar> calendar_data_tbl;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Word> getWord_tbl() {
        return word_tbl;
    }

    public void setWord_tbl(List<Word> word_tbl) {
        this.word_tbl = word_tbl;
    }

    public List<ForgettingCurve> getForgetting_curves_tbl() {
        return forgetting_curves_tbl;
    }

    public void setForgetting_curves_tbl(List<ForgettingCurve> forgetting_curves_tbl) {
        this.forgetting_curves_tbl = forgetting_curves_tbl;
    }

    public List<Calendar> getCalendar_data_tbl() {
        return calendar_data_tbl;
    }

    public void setCalendar_data_tbl(List<Calendar> calendar_data_tbl) {
        this.calendar_data_tbl = calendar_data_tbl;
    }

    public int getUser_count() {
        return user_count;
    }

    public void setUser_count(int user_count) {
        this.user_count = user_count;
    }

    public String getBlog_url() {
        return blog_url;
    }

    public void setBlog_url(String blog_url) {
        this.blog_url = blog_url;
    }

    public String getLock_screen_comment() {
        return lock_screen_comment;
    }

    public void setLock_screen_comment(String lock_screen_comment) {
        this.lock_screen_comment = lock_screen_comment;
    }

    public int getComment_type() {
        return comment_type;
    }

    public void setComment_type(int comment_type) {
        this.comment_type = comment_type;
    }

    public int getInfo_type() {
        return info_type;
    }

    public void setInfo_type(int info_type) {
        this.info_type = info_type;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
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
