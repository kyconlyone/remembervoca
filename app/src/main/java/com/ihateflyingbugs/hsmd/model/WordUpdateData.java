package com.ihateflyingbugs.hsmd.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Created by 영철 on 2016-06-13.
 */
public class WordUpdateData {

    int result;
    int current_word_version;
    String status;
    List<JsonNode> words;
    List<JsonNode> means;
    List<WordID> word_list;

    public List<WordID> getWord_list() {
        return word_list;
    }

    public void setWord_list(List<WordID> word_list) {
        this.word_list = word_list;
    }

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

    public int getCurrent_word_version() {
        return current_word_version;
    }

    public void setCurrent_word_version(int current_word_version) {
        this.current_word_version = current_word_version;
    }

    public List<JsonNode> getWords() {
        return words;
    }

    public void setWords(List<JsonNode> words) {
        this.words = words;
    }

    public List<JsonNode> getMeans() {
        return means;
    }

    public void setMeans(List<JsonNode> means) {
        this.means = means;
    }

    public WordUpdateData(){

    }


    public static class WordID {
        int  word_id;

        public WordID(){

        }

        public int getWord_id() {
            return word_id;
        }

        public void setWord_id(int word_id) {
            this.word_id = word_id;
        }
    }
}
