package com.ihateflyingbugs.hsmd.model;

import java.util.List;

/**
 * Created by 영철 on 2016-05-23.
 */
public class ManagerData {

    int result;
    int count;
    int state;
    String status;
    int test_id;
    boolean isExist_manager;

    String message;


    List<TestExam> test_paper;

    int type;
    String teacher_id;



    String type_content;
    String last_time;



    public void setExist_manager(boolean exist_manager) {
        isExist_manager = exist_manager;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public boolean isExist_manager() {
        return isExist_manager;
    }

    public void setisExist_manager(boolean isExist_manager) {
        this.isExist_manager = isExist_manager;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getType_content() {
        return type_content;
    }

    public void setType_content(String type_content) {
        this.type_content = type_content;
    }

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    public int getTest_id() {
        return test_id;
    }

    public void setTest_id(int test_id) {
        this.test_id = test_id;
    }

    public List<TestExam> getTest_paper() {
        return test_paper;
    }

    public void setTest_paper(List<TestExam> test_paper) {
        this.test_paper = test_paper;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class TestExam{
        int word_test_paper_id;
        int test_id;
        int word_id;
        String word ;
        int correct_num;
        int count_ebs ;
        int count_sat ;
        int count_sat2 ;
        int sentence_id;
        int text_id;
        double forgetting_percent ;
        String exam1;
        String exam2;
        String exam3;
        String exam4;
        String sentence;
        String sentence_translation ;
        String sentence_info ;
        String word1;
        String word2;
        String word3;
        String word4;
        int choice_num ;

        public int getTest_id() {
            return test_id;
        }

        public void setTest_id(int test_id) {
            this.test_id = test_id;
        }

        public int getSentence_id() {
            return sentence_id;
        }

        public void setSentence_id(int sentence_id) {
            this.sentence_id = sentence_id;
        }

        public int getText_id() {
            return text_id;
        }

        public void setText_id(int text_id) {
            this.text_id = text_id;
        }

        public String getWord1() {
            return word1;
        }

        public void setWord1(String word1) {
            this.word1 = word1;
        }

        public String getWord2() {
            return word2;
        }

        public void setWord2(String word2) {
            this.word2 = word2;
        }

        public String getWord3() {
            return word3;
        }

        public void setWord3(String word3) {
            this.word3 = word3;
        }

        public String getWord4() {
            return word4;
        }

        public void setWord4(String word4) {
            this.word4 = word4;
        }

        public int getCorrect_num() {
            return correct_num;
        }

        public void setCorrect_num(int correct_num) {
            this.correct_num = correct_num;
        }

        public int getCount_ebs() {
            return count_ebs;
        }

        public void setCount_ebs(int count_ebs) {
            this.count_ebs = count_ebs;
        }

        public int getCount_sat() {
            return count_sat;
        }

        public void setCount_sat(int count_sat) {
            this.count_sat = count_sat;
        }

        public int getCount_sat2() {
            return count_sat2;
        }

        public void setCount_sat2(int count_sat2) {
            this.count_sat2 = count_sat2;
        }

        public double getForgetting_percent() {
            return forgetting_percent;
        }

        public void setForgetting_percent(double forgetting_percent) {
            this.forgetting_percent = forgetting_percent;
        }

        public String getSentence_translation() {
            return sentence_translation;
        }

        public void setSentence_translation(String sentence_translation) {
            this.sentence_translation = sentence_translation;
        }

        public String getSentence_info() {
            return sentence_info;
        }

        public void setSentence_info(String sentence_info) {
            this.sentence_info = sentence_info;
        }

        public int getChoice_num() {
            return choice_num;
        }

        public void setChoice_num(int choice_num) {
            this.choice_num = choice_num;
        }

        public String getSentence() {
            return sentence;
        }

        public void setSentence(String sentence) {
            this.sentence = sentence;
        }

        public int getWord_test_paper_id() {
            return word_test_paper_id;
        }

        public void setWord_test_paper_id(int word_test_paper_id) {
            this.word_test_paper_id = word_test_paper_id;
        }

        public int getWord_id() {
            return word_id;
        }

        public void setWord_id(int word_id) {
            this.word_id = word_id;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getExam1() {
            return exam1;
        }

        public void setExam1(String exam1) {
            this.exam1 = exam1;
        }

        public String getExam2() {
            return exam2;
        }

        public void setExam2(String exam2) {
            this.exam2 = exam2;
        }

        public String getExam3() {
            return exam3;
        }

        public void setExam3(String exam3) {
            this.exam3 = exam3;
        }

        public String getExam4() {
            return exam4;
        }

        public void setExam4(String exam4) {
            this.exam4 = exam4;
        }
    }
}
