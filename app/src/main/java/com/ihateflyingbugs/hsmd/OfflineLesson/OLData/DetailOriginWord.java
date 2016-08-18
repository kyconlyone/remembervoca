package com.ihateflyingbugs.hsmd.OfflineLesson.OLData;

import java.io.Serializable;

/**
 * Created by 영철 on 2016-07-11.
 */
public class DetailOriginWord implements Serializable{

    String word_id;
    String word;
    String description_1;
    String description_2;
    String description_3;

    public DetailOriginWord(){

    }


    public String getWord_id() {
        return word_id;
    }

    public void setWord_id(String word_id) {
        this.word_id = word_id;
    }

    public String getDescription_1() {
        return description_1;
    }

    public void setDescription_1(String description_1) {
        this.description_1 = description_1;
    }

    public String getDescription_2() {
        return description_2;
    }

    public void setDescription_2(String description_2) {
        this.description_2 = description_2;
    }

    public String getDescription_3() {
        return description_3;
    }

    public void setDescription_3(String description_3) {
        this.description_3 = description_3;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
