package com.ihateflyingbugs.hsmd.RetroAPI;

import com.ihateflyingbugs.hsmd.model.AuthorizationData;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by 영철 on 2016-05-18.
 */
public interface AuthorizationRetro {


    public String URL_INDEX = "http://52.79.87.3/rest/index.php/Authorization/";


    @FormUrlEncoded
    @POST(URL_INDEX+"idRegister")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroRegistAccount(@Field("login_type") String login_type,
                                                      @Field("login_id") String login_id,
                                                      @Field("student_name") String student_name,
                                                      @Field("student_birth") String student_birth,
                                                      @Field("school_id") String school_id,
                                                      @Field("student_gender") String student_gender,
                                                      @Field("student_phone") String student_phone,
                                                      @Field("student_push_id") String student_push_id,
                                                      @Field("student_device_num") String student_device_num,
                                                      @Field("student_email") String student_email,
                                                      @Field("student_sat_grade") String student_sat_grade,
                                                      @Field("friend_phone") String friend_phone,
                                                      @Field("student_profile_img") String student_profile_img);

    @GET(URL_INDEX+"isExistStudentId")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroIsExistStudentID(@Query("login_id") String login_id,
                                                         @Query("login_type") String login_type,
                                                         @Query("version") String version,
                                                         @Query("Gcm") String Gcm);


    @GET(URL_INDEX+"StudentAvailability")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroCheckavailability(@Query("student_id") String StudentID,
                                                  @Query("option") String option);

    @GET(URL_INDEX+"SplashInfo")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroCheckID(@Query("student_id") String StudentID,
                                        @Query("version") String version,
                                        @Query("Time") String Time,
                                        @Query("Device") String Device,
                                        @Query("Gcm") String Gcm,
                                        @Query("option") String option);

    @GET(URL_INDEX+"CheckVersion")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroCheckVersion(@Query("option") String option);

    @FormUrlEncoded
    @POST(URL_INDEX+"FriendPhone")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroInsertFriendPhone(@Field("student_id") String StudentID,
                                       @Field("friend_phone") String friend_phone);

    @GET(URL_INDEX+"isLeaveStudentId")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroIsLeaveStudent(@Query("student_id") String StudentID);

    @FormUrlEncoded
    @POST(URL_INDEX+"UserInfo")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroUpdateUserInfo(@Field("student_id") String StudentID,
                                                        @Field("student_name") String student_name,
                                                        @Field("student_birth") String student_birth,
                                                        @Field("school_id") String school_id,
                                                        @Field("student_email") String student_email,
                                                        @Field("student_profile_url") String student_profile_url,
                                                        @Field("student_word_grade") String student_word_grade,
                                                        @Field("student_sat_grade") String student_sat_grade);


    @FormUrlEncoded
    @POST(URL_INDEX+"LeaveInfo")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroInsertLeaveInfo(@Field("student_id") String StudentID,
                                                        @Field("reason") String reason);


    @GET(URL_INDEX+"StudentCouponAndFinishDate")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroGettStudentCouponAndFinishDate(@Query("student_id") String StudentID);


    @GET(URL_INDEX+"FinishDate")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroGetFinishDate(@Query("student_id") String StudentID);

    @GET(URL_INDEX+"FriendInfo")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroGetFriendList(@Query("student_id") String StudentID);

    @GET(URL_INDEX+"NearUserInfo")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroGetNearFriendList(@Query("student_id") String StudentID);


    @GET(URL_INDEX+"SchoolList")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroGetSchoolList(@Query("school_name") String school_name);


    @FormUrlEncoded
    @POST(URL_INDEX+"SchoolData")      //here is the other url part.best way is to start using /
    public Call<AuthorizationData> retroinsertSchoolData(@Field("school_name") String school_name,
                                                        @Field("school_address") String school_address);

}
