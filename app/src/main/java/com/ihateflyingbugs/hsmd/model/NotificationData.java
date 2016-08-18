package com.ihateflyingbugs.hsmd.model;

/**
 * Created by 영철 on 2016-05-23.
 */
public class NotificationData {
    final public static int PUSH_EVERYBODY = 7;
    final public static int PUSH_EVERYBODY_NOTIFICATION = 8;
    final public static int PUSH_HIT = 11;
    final public static int PUSH_HIT_BLOG = 19;
    final public static int PUSH_WILL_NOT_ACCESS_FIRSTTIME = 21;
    final public static int PUSH_WILL_NOT_ACCESS = 22;
    final public static int PUSH_WILL_FAIL_ACHIVE_GOAL = 23;
    final public static int PUSH_WILL_BLOG = 29;
    final public static int PUSH_MEMORY_REVIEW_FIRST_TIME = 31;
    final public static int PUSH_MEMORY_REVIEW_SECOND_TIME = 32;
    final public static int PUSH_MEMORY_REVIEW_THIRD_TIME  = 33;
    final public static int PUSH_MEMORY_BLOG  = 39;

    final public static int PUSH_NEW_MEMBER  = 50;




    int result;
    int count;
    int state;



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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
