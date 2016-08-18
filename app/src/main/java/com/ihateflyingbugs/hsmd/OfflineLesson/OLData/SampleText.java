package com.ihateflyingbugs.hsmd.OfflineLesson.OLData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 영철 on 2016-06-22.
 */
public class SampleText implements Serializable{

    String text;
    int text_id;
    List<Sentence> sentences;

    public int getText_id() {
        return text_id;
    }

    public void setText_id(int text_id) {
        this.text_id = text_id;
    }
    public List<Sentence> getSentences() {
        return sentences;
    }

    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public SampleText(String sampleText, int text_id) {
        this.text = sampleText;
        this.text_id = text_id;

    }

    public SampleText() {

    }




}