package com.ihateflyingbugs.hsmd.RetroAPI;

import com.ihateflyingbugs.hsmd.model.NotificationData;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by 영철 on 2016-05-18.
 */
public interface NotificationRetro {


    public String URL_NOTIFICATION = "http://52.79.87.3/rest/index.php/Notification/";

    @FormUrlEncoded
    @POST(URL_NOTIFICATION )      //here is the other url part.best way is to start using /
    public Call<NotificationData> retroMayKnowFriendPush(@Field("student_id") String StudentID,
                                                          @Field("school") String school,
                                                          @Field("name") String name,
                                                          @Field("sort") String sort);

    @FormUrlEncoded
    @POST(URL_NOTIFICATION )      //here is the other url part.best way is to start using /
    public Call<NotificationData> retroFirstReviewPush(@Field("student_id") String StudentID,
                                                       @Field("rate") String rate,
                                                       @Field("word_count") String word_count,
                                                       @Field("sort") String sort);

    @FormUrlEncoded
    @POST(URL_NOTIFICATION )      //here is the other url part.best way is to start using /
    public Call<NotificationData> retroSecondReviewPush(@Field("student_id") String StudentID,
                                                      @Field("word_count") String word_count,
                                                      @Field("sort") String sort);

    @FormUrlEncoded
    @POST(URL_NOTIFICATION )      //here is the other url part.best way is to start using /
    public Call<NotificationData> retroThirdReviewPush(@Field("student_id") String StudentID,
                                                        @Field("rate") String rate,
                                                        @Field("word_count") String word_count,
                                                        @Field("sort") String sort);

    @FormUrlEncoded
    @POST(URL_NOTIFICATION )      //here is the other url part.best way is to start using /
    public Call<NotificationData> retroNotAccessFirstTime(@Field("student_id") String StudentID,
                                                       @Field("sort") String sort);

    @FormUrlEncoded
    @POST(URL_NOTIFICATION )      //here is the other url part.best way is to start using /
    public Call<NotificationData> retroNotAccess(@Field("student_id") String StudentID,
                                                          @Field("sort") String sort);

    @FormUrlEncoded
    @POST(URL_NOTIFICATION )      //here is the other url part.best way is to start using /
    public Call<NotificationData> retroFailAcheiveGoal(@Field("student_id") String StudentID,
                                                         @Field("sort") String sort);





}
