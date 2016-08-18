package com.ihateflyingbugs.hsmd.RetroAPI;

import com.ihateflyingbugs.hsmd.OfflineLesson.OLData.SampleText;
import com.ihateflyingbugs.hsmd.model.ManagerData;

import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Query;

/**
 * Created by 영철 on 2016-07-05.
 */
public interface OfflineLessonRretro {

    public String URL_OFFLINELESSON = "http://52.79.87.3/manager/index.php/Teaching/";

    @GET(URL_OFFLINELESSON + "grammarFromSentence")      //here is the other url part.best way is to start using /
    public Call<List<SampleText>> retroGetSampleTextList(@Query("student_id") String StudentID,
                                                         @Query("teacher_id") String Teacher_id);

    @FormUrlEncoded
    @PUT(URL_OFFLINELESSON + "studentSubmitText")      //here is the other url part.best way is to start using /
    public Call<ManagerData> retroSubmitStudentText(@Field("student_id") String student_id,
                                                    @Field("teacher_id") String teacher_id,
                                                    @Field("text_id") String text_id);



}
