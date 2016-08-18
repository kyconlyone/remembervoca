package com.ihateflyingbugs.hsmd.RetroAPI;

import com.ihateflyingbugs.hsmd.model.StudyInfoData;
import com.ihateflyingbugs.hsmd.model.WordUpdateData;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by 영철 on 2016-05-18.
 */
public interface StudyInfoRetro {


    public String URL_STUDYINFO = "http://52.79.87.3/rest/index.php/StudyInfo/";

    @FormUrlEncoded
    @POST(URL_STUDYINFO + "StudentGoalTime")      //here is the other url part.best way is to start using /
    public Call<StudyInfoData> retroInsertStudentGoalTime(@Field("student_id") String student_id,
                                                          @Field("goal_time") String goal_time);


    @FormUrlEncoded
    @POST(URL_STUDYINFO + "StudentCalendarData")      //here is the other url part.best way is to start using /
    public Call<StudyInfoData> retroInsertStudentCalendarData(@Field("student_id") String StudentID,
                                                           @Field("calendar_data") String calendar_data);


    @FormUrlEncoded
    @POST(URL_STUDYINFO + "WordLog")      //here is the other url part.best way is to start using /
    public Call<StudyInfoData> retroInsertWordLog(@Field("student_id") String student_id,
                                                              @Field("word_log") String word_log);

    @FormUrlEncoded
    @POST(URL_STUDYINFO + "StateZeroWord")      //here is the other url part.best way is to start using /
    public Call<StudyInfoData> retroInsertStateZeroWord(@Field("student_id") String student_id,
                                                  @Field("state_zero_word_list") String state_zero_word_list);


    @GET(URL_STUDYINFO + "WillCoachMent")      //here is the other url part.best way is to start using /
    public Call<StudyInfoData> retroGetWillCoachMent(@Query("student_id") String student_id,
                                                     @Query("student_birth") String student_birth,
                                                        @Query("student_sat_grade") String student_sat_grade);

    @GET(URL_STUDYINFO + "DailyAchievement")      //here is the other url part.best way is to start using /
    public Call<StudyInfoData> retroInsertDailyAchievement();


    @FormUrlEncoded
    @POST(URL_STUDYINFO + "ClickNextWordSet")      //here is the other url part.best way is to start using /
    public Call<StudyInfoData> retroInsertClickNextWordSet(@Field("student_id") String student_id);

    @FormUrlEncoded
    @POST(URL_STUDYINFO + "StudentForgettingCurves")      //here is the other url part.best way is to start using /
    public Call<StudyInfoData> retroInsertStudentForgettingCurves(@Field("student_id") String student_id,
                                                                    @Field("forgetting_curves") String forgetting_curves,
                                                                    @Field("is_error") String is_error);


    @GET(URL_STUDYINFO + "HitCoachMent")      //here is the other url part.bes way is to start using /
    public Call<StudyInfoData> retroGetHitCoachMent();

    @GET(URL_STUDYINFO + "WorkbookList")      //here is the other url part.best way is to start using /
    public Call<StudyInfoData> retroGetWorkbookList();

    @GET(URL_STUDYINFO + "WrongWordLog")      //here is the other url part.best way is to start using /
    public Call<WordUpdateData> retroUpdateWordState(@Query("student_id")String student_id);





}
