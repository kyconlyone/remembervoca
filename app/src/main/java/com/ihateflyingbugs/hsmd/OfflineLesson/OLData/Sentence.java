package com.ihateflyingbugs.hsmd.OfflineLesson.OLData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 영철 on 2016-07-05.
 */
public class Sentence implements Serializable {

    List<Grammar> grammar_class_id;
    int is_input_grammar;
    int is_valid;
    String reg_time;
    String sentence;
    int sentence_id;
    int text_id ;
    String sentence_translation;


    List<WordTextView> sentence_split;

    public  Sentence(){

    }

    public int getText_id() {
        return text_id;
    }

    public void setText_id(int text_id) {
        this.text_id = text_id;
    }

    public String getSentence_translation() {
        return sentence_translation;
    }

    public void setSentence_translation(String sentence_translation) {
        this.sentence_translation = sentence_translation;
    }

    public List<Grammar> getGrammar_class_id() {
        return grammar_class_id;
    }

    public void setGrammar_class_id(List<Grammar> grammar_class_id) {
        this.grammar_class_id = grammar_class_id;
    }

    public int getIs_input_grammar() {
        return is_input_grammar;
    }

    public void setIs_input_grammar(int is_input_grammar) {
        this.is_input_grammar = is_input_grammar;
    }

    public int getIs_valid() {
        return is_valid;
    }

    public void setIs_valid(int is_valid) {
        this.is_valid = is_valid;
    }

    public String getReg_time() {
        return reg_time;
    }

    public void setReg_time(String reg_time) {
        this.reg_time = reg_time;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public int getSentence_id() {
        return sentence_id;
    }

    public void setSentence_id(int sentence_id) {
        this.sentence_id = sentence_id;
    }

    public List<WordTextView> getSentence_split() {
        return sentence_split;
    }

    public void setSentence_split(List<WordTextView> sentence_split) {
        this.sentence_split = sentence_split;
    }


}
