package com.ihateflyingbugs.hsmd.model;

import org.json.JSONArray;

/**
 * Created by 영철 on 2016-05-18.
 */
public class RetroData {


    public int result;

    public String student_id;

    public String school_name;
    public String school_id;
    public String student_birth;
    public String student_sat_grade;
    public String student_word_grade;
    public int availability;
    public int is_invite_friend;
    public int student_use_state;
    public String finish_date;
    public String payment_request_code;
    public JSONArray notice;


    public String payment_type;

    public JSONArray getNoticeArray(){
        return this.notice;
    }

    public void setNoticeArray(JSONArray notice){
        this.notice = notice;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public int getIs_invite_friend() {
        return is_invite_friend;
    }

    public void setIs_invite_friend(int is_invite_friend) {
        this.is_invite_friend = is_invite_friend;
    }


    public int getStudent_use_state() {
        return student_use_state;
    }

    public void setStudent_use_state(int student_use_state) {
        this.student_use_state = student_use_state;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

}
