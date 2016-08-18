package com.ihateflyingbugs.hsmd.OfflineLesson.OLData;

import java.io.Serializable;

/**
 * Created by 영철 on 2016-06-17.
 */
public class WordTextView implements Serializable{



    String word;
    int word_id;
    String type;
    Boolean isSelected = false;



    int tvResID;
    public WordTextView(){
    }

    public WordTextView(String word, String sentence_logic, int word_id, String type) {
        this.word = word;
        this.type = type;
        this.word_id= word_id;

    }


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Boolean getWordSelected() {
        return isSelected;
    }

    public void setWordSelected(Boolean selected) {
        isSelected = selected;
    }

    public int getWord_id() {
        return word_id;
    }

    public void setWord_id(int word_id) {
        this.word_id = word_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }
    public int getTvResID() {
        return tvResID;
    }

    public void setTvResID(int tvResID) {
        this.tvResID = tvResID;
    }


}
