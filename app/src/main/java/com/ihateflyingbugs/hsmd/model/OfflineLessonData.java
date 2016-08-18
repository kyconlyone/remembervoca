package com.ihateflyingbugs.hsmd.model;

import android.content.Context;

import com.ihateflyingbugs.hsmd.OfflineLesson.OLData.SampleText;

import java.util.List;

/**
 * Created by 영철 on 2016-07-05.
 */
public class OfflineLessonData {

    private static OfflineLessonData instance = null;


    public static OfflineLessonData getInstance(Context context) {
        if(instance == null) {
            instance = new OfflineLessonData();
        }
        return instance;
    }

    int result;
    String status;

    List<SampleText> texts;

    public static OfflineLessonData getInstance() {
        return instance;
    }

    public static void setInstance(OfflineLessonData instance) {
        OfflineLessonData.instance = instance;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SampleText> getTexts() {
        return texts;
    }

    public void setTexts(List<SampleText> texts) {
        this.texts = texts;
    }


}
