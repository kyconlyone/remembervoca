package com.ihateflyingbugs.hsmd.model;

import com.ihateflyingbugs.hsmd.data.Friend;

import java.util.List;

/**
 * Created by 영철 on 2016-05-23.
 */
public class AuthorizationData {


    int count;
    int is_friend_phone;
    int res_user_info;
    int res_user_grade;
    int result;
    int availability;
    int is_invite_friend;
    int student_use_state;
    int school_id;

    String status;
    String message;


    String student_id;
    String school_name;
    String student_birth;
    String student_sat_grade;
    String student_word_grade;

    public List<School> school_list;
    public List<Friend> friend_list;



    int version_id;
    String version;
    String is_update;
    String contents;
    int is_inspect;
    String start_time;
    String end_time;
    String update_time;
    String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public int getVersion_id() {
        return version_id;
    }

    public void setVersion_id(int version_id) {
        this.version_id = version_id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String is_update() {
        return is_update;
    }

    public void setIs_update(String is_update) {
        this.is_update = is_update;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getIs_inspect() {
        return is_inspect;
    }

    public void setIs_inspect(int is_inspect) {
        this.is_inspect = is_inspect;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public int getRes_user_info() {
        return res_user_info;
    }

    public void setRes_user_info(int res_user_info) {
        this.res_user_info = res_user_info;
    }

    public int getRes_user_grade() {
        return res_user_grade;
    }

    public void setRes_user_grade(int res_user_grade) {
        this.res_user_grade = res_user_grade;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }



    public int getIs_friend_phone() {
        return is_friend_phone;
    }

    public void setIs_friend_phone(int is_friend_phone) {
        this.is_friend_phone = is_friend_phone;
    }
    public List<Friend> getFriend_list() {
        return friend_list;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setFriend_list(List<Friend> friend_list) {
        this.friend_list = friend_list;
    }


    public List<School> getSchool_list() {
        return school_list;
    }

    public void setSchool_list(List<School> school_list) {
        this.school_list = school_list;
    }

    public static class School {
        int school_origin_id;
        String school_origin_name;
        String school_address;

        String latitude;
        String longitude;
        String special_highschool;
        String reg_time;

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getSpecial_highschool() {
            return special_highschool;
        }

        public void setSpecial_highschool(String special_highschool) {
            this.special_highschool = special_highschool;
        }

        public String getReg_time() {
            return reg_time;
        }

        public void setReg_time(String reg_time) {
            this.reg_time = reg_time;
        }

        public int getSchool_origin_id() {
            return school_origin_id;
        }

        public void setSchool_origin_id(int school_origin_id) {
            this.school_origin_id = school_origin_id;
        }

        public String getSchool_origin_name() {
            return school_origin_name;
        }

        public void setSchool_origin_name(String school_origin_name) {
            this.school_origin_name = school_origin_name;
        }

        public String getSchool_address() {
            return school_address;
        }

        public void setSchool_address(String school_address) {
            this.school_address = school_address;
        }

        public School(){

        }

        public School(int school_origin_id, String school_origin_name, String school_address){
            this.school_origin_id = school_origin_id;
            this.school_origin_name = school_origin_name;
            this.school_address = school_address;

        }
    }




    public String getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(String finish_date) {
        this.finish_date = finish_date;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public int getSchool_id() {
        return school_id;
    }

    public void setSchool_id(int school_id) {
        this.school_id = school_id;
    }

    public String getStudent_birth() {
        return student_birth;
    }

    public void setStudent_birth(String student_birth) {
        this.student_birth = student_birth;
    }

    public String getStudent_sat_grade() {
        return student_sat_grade;
    }

    public void setStudent_sat_grade(String student_sat_grade) {
        this.student_sat_grade = student_sat_grade;
    }

    public String getStudent_word_grade() {
        return student_word_grade;
    }

    public void setStudent_word_grade(String student_word_grade) {
        this.student_word_grade = student_word_grade;
    }

    public String finish_date;


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
