package com.ihateflyingbugs.hsmd.model;

import com.kakao.usermgmt.response.model.User;

import java.util.ArrayList;

;

/**
 * Created by 영철 on 2016-05-26.
 */
public class ChannelInfo {
    //사실 isStarted가 더 적당함
    Boolean is_created;

    ArrayList<User> user_list;

    String code;

    public ArrayList<User> getUserList() {
        return user_list;
    }

    public Boolean getIsCreated() {
        return is_created;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setIsCreated(Boolean isCreated) {
        this.is_created = isCreated;
    }

    public void setUserList(ArrayList<User> userList) {
        this.user_list = userList;
    }
}
