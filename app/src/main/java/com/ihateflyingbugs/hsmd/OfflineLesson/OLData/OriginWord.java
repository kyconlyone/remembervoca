package com.ihateflyingbugs.hsmd.OfflineLesson.OLData;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by 영철 on 2016-07-11.
 */
public class OriginWord implements Serializable{
    int origin_word_id;
    String origin_word;
    String origin_word_mean;
    String origin_word_img1;
    String origin_word_img2;

    List<DetailOriginWord> detail_origin_word;



    public OriginWord(){

    }

    public int getOrigin_word_id() {
        return origin_word_id;
    }

    public void setOrigin_word_id(int origin_word_id) {
        this.origin_word_id = origin_word_id;
    }

    public String getOrigin_word() {
        return origin_word;
    }

    public void setOrigin_word(String origin_word) {
        this.origin_word = origin_word;
    }

    public String getOrigin_word_mean() {

        try {
            return URLDecoder.decode(origin_word_mean, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "의미를 가져올수 없습니다.";
        }
    }

    public void setOrigin_word_mean(String origin_word_mean) {
        this.origin_word_mean = origin_word_mean;
    }

    public String getOrigin_word_img1() {
        return origin_word_img1;
    }

    public void setOrigin_word_img1(String origin_word_img1) {
        this.origin_word_img1 = origin_word_img1;
    }

    public String getOrigin_word_img2() {
        return origin_word_img2;
    }

    public void setOrigin_word_img2(String origin_word_img2) {
        this.origin_word_img2 = origin_word_img2;
    }

    public List<DetailOriginWord> getDetail_origin_word() {
        return detail_origin_word;
    }

    public void setDetail_origin_word(List<DetailOriginWord> detail_origin_word) {
        this.detail_origin_word = detail_origin_word;
    }



}
