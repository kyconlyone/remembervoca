package com.ihateflyingbugs.hsmd.model;

import java.util.List;

/**
 * Created by 영철 on 2016-05-23.
 */
public class PaymentData {

    int result;
    int count;
    int payment_type;
    String request_id;
    String pay_url;
    String status;
    List<Product> usable_product_list;
    int free_period;

    public int getFree_period() {
        return free_period;
    }

    public void setFree_period(int free_period) {
        this.free_period = free_period;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(int payment_type) {
        this.payment_type = payment_type;
    }

    public String getPay_url() {
        return pay_url;
    }

    public void setPay_url(String pay_url) {
        this.pay_url = pay_url;
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

    public List<Product> getUsable_product_list() {
        return usable_product_list;
    }

    public void setUsable_product_list(List<Product> usable_product_list) {
        this.usable_product_list = usable_product_list;
    }



    public static class  Product{
        int product_type_id;
        int not_dc_price;
        int price;
        int payment_way;
        int price_code;
        int period_month;
        int is_subscribed;
        int is_sold;
        String google_product_id;
        String reg_time;

        public int getProduct_type_id() {
            return product_type_id;
        }

        public void setProduct_type_id(int product_type_id) {
            this.product_type_id = product_type_id;
        }

        public int getNot_dc_price() {
            return not_dc_price;
        }

        public void setNot_dc_price(int not_dc_price) {
            this.not_dc_price = not_dc_price;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getPayment_way() {
            return payment_way;
        }

        public void setPayment_way(int payment_way) {
            this.payment_way = payment_way;
        }

        public int getPrice_code() {
            return price_code;
        }

        public void setPrice_code(int price_code) {
            this.price_code = price_code;
        }

        public int getPeriod_month() {
            return period_month;
        }

        public void setPeriod_month(int period_month) {
            this.period_month = period_month;
        }

        public int getIs_subscribed() {
            return is_subscribed;
        }

        public void setIs_subscribed(int is_subscribed) {
            this.is_subscribed = is_subscribed;
        }

        public int getIs_sold() {
            return is_sold;
        }

        public void setIs_sold(int is_sold) {
            this.is_sold = is_sold;
        }

        public String getGoogle_product_id() {
            return google_product_id;
        }

        public void setGoogle_product_id(String google_product_id) {
            this.google_product_id = google_product_id;
        }

        public String getReg_time() {
            return reg_time;
        }

        public void setReg_time(String reg_time) {
            this.reg_time = reg_time;
        }

        public Product() {
        }
    }

}
