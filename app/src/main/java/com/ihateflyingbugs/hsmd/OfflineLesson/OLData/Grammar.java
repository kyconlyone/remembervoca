package com.ihateflyingbugs.hsmd.OfflineLesson.OLData;

import java.io.Serializable;

/**
 * Created by 영철 on 2016-07-05.
 */
public class Grammar implements Serializable {

    String grammar_book;
    int grammar_book_id;
    int grammar_class_id;
    String grammar_class;
    int sentence_id;
    String sentence_split_indexes;



    int grammar_id;
    String grammar_main_category;
    int  grammar_main_category_id;
    String grammar_sub_category;
    int grammar_sub_category_id;
    int grammar_sub_unit;
    int grammar_unit;
    int grammar_unit_id;

    public Grammar(){

    }

    public String getGrammar_class() {
        return grammar_class;
    }

    public void setGrammar_class(String grammar_class) {
        this.grammar_class = grammar_class;
    }

    public int getSentence_id() {
        return sentence_id;
    }

    public void setSentence_id(int sentence_id) {
        this.sentence_id = sentence_id;
    }

    public String getSentence_split_indexes() {
        return sentence_split_indexes;
    }

    public void setSentence_split_indexes(String sentence_split_indexes) {
        this.sentence_split_indexes = sentence_split_indexes;
    }


    public String getGrammar_book() {
        return grammar_book;
    }

    public void setGrammar_book(String grammar_book) {
        this.grammar_book = grammar_book;
    }

    public int getGrammar_book_id() {
        return grammar_book_id;
    }

    public void setGrammar_book_id(int grammar_book_id) {
        this.grammar_book_id = grammar_book_id;
    }

    public int getGrammar_class_id() {
        return grammar_class_id;
    }

    public void setGrammar_class_id(int grammar_class_id) {
        this.grammar_class_id = grammar_class_id;
    }

    public int getGrammar_id() {
        return grammar_id;
    }

    public void setGrammar_id(int grammar_id) {
        this.grammar_id = grammar_id;
    }

    public String getGrammar_main_category() {
        return grammar_main_category;
    }

    public void setGrammar_main_category(String grammar_main_category) {
        this.grammar_main_category = grammar_main_category;
    }

    public int getGrammar_main_category_id() {
        return grammar_main_category_id;
    }

    public void setGrammar_main_category_id(int grammar_main_category_id) {
        this.grammar_main_category_id = grammar_main_category_id;
    }

    public String getGrammar_sub_category() {
        return grammar_sub_category;
    }

    public void setGrammar_sub_category(String grammar_sub_category) {
        this.grammar_sub_category = grammar_sub_category;
    }

    public int getGrammar_sub_category_id() {
        return grammar_sub_category_id;
    }

    public void setGrammar_sub_category_id(int grammar_sub_category_id) {
        this.grammar_sub_category_id = grammar_sub_category_id;
    }

    public int getGrammar_sub_unit() {
        return grammar_sub_unit;
    }

    public void setGrammar_sub_unit(int grammar_sub_unit) {
        this.grammar_sub_unit = grammar_sub_unit;
    }

    public int getGrammar_unit() {
        return grammar_unit;
    }

    public void setGrammar_unit(int grammar_unit) {
        this.grammar_unit = grammar_unit;
    }

    public int getGrammar_unit_id() {
        return grammar_unit_id;
    }

    public void setGrammar_unit_id(int grammar_unit_id) {
        this.grammar_unit_id = grammar_unit_id;
    }
}
