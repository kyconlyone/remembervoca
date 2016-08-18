package com.ihateflyingbugs.hsmd.model;

import java.util.List;

/**
 * Created by 영철 on 2016-05-24.
 */
public class CouponData {

    int result;
    String status;
    int count;
    int is_exist_coupon;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    List<Coupon> coupon;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<Coupon> getCoupon() {
        return coupon;
    }

    public void setCoupon(List<Coupon> coupon) {
        this.coupon = coupon;
    }

    public int getIs_exist_coupon() {
        return is_exist_coupon;
    }

    public void setIs_exist_coupon(int is_exist_coupon) {
        this.is_exist_coupon = is_exist_coupon;
    }

    public static class Coupon{
        String student_id;
        int student_coupon_id;
        int is_used;
        String reg_time;
        int coupon_type_id;
        String coupon_type_name;
        String coupon_type_info;
        int is_duplicated;
        String student_coupon_valid_date;



        public String getStudent_id() {
            return student_id;
        }

        public void setStudent_id(String student_id) {
            this.student_id = student_id;
        }

        public int getStudent_coupon_id() {
            return student_coupon_id;
        }

        public void setStudent_coupon_id(int student_coupon_id) {
            this.student_coupon_id = student_coupon_id;
        }

        public int getIs_used() {
            return is_used;
        }

        public void setIs_used(int is_used) {
            this.is_used = is_used;
        }

        public String getReg_time() {
            return reg_time;
        }

        public void setReg_time(String reg_time) {
            this.reg_time = reg_time;
        }

        public int getCoupon_type_id() {
            return coupon_type_id;
        }

        public void setCoupon_type_id(int coupon_type_id) {
            this.coupon_type_id = coupon_type_id;
        }

        public String getCoupon_type_name() {
            return coupon_type_name;
        }

        public void setCoupon_type_name(String coupon_type_name) {
            this.coupon_type_name = coupon_type_name;
        }

        public String getCoupon_type_info() {
            return coupon_type_info;
        }

        public void setCoupon_type_info(String coupon_type_info) {
            this.coupon_type_info = coupon_type_info;
        }

        public int getIs_duplicated() {
            return is_duplicated;
        }

        public void setIs_duplicated(int is_duplicated) {
            this.is_duplicated = is_duplicated;
        }

        public String getStudent_coupon_valid_date() {
            return student_coupon_valid_date;
        }

        public void setStudent_coupon_valid_date(String student_coupon_valid_date) {
            this.student_coupon_valid_date = student_coupon_valid_date;
        }
    }

}
