package com.ihateflyingbugs.hsmd.RetroAPI;

import com.ihateflyingbugs.hsmd.model.UseAppInfoData;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by 영철 on 2016-05-18.
 */
public interface UseAppInfomationRetro {


    public String URL_USEAPP = "http://52.79.87.3/rest/index.php/UseAppInfomation/";

    @GET(URL_USEAPP + "getBetaServiceAdmin")      //here is the other url part.best way is to start using /
    public Call<UseAppInfoData> getBetaServiceAdmin();

    @FormUrlEncoded
    @POST(URL_USEAPP + "insertUseLockScreenInfo")      //here is the other url part.best way is to start using /
    public Call<UseAppInfoData> retroInsertUseLockScreenInfo(@Field("student_id") String StudentID,
                                                             @Field("list") String list);
    @GET(URL_USEAPP + "getPolicyDocument")      //here is the other url part.best way is to start using /
    public Call<UseAppInfoData> retroGetPolicy();

    @GET(URL_USEAPP + "getUserCount")      //here is the other url part.best way is to start using /
    public Call<UseAppInfoData> retroGetUseCount();

    @FormUrlEncoded
    @POST(URL_USEAPP + "updateUserPushLog")      //here is the other url part.best way is to start using /
    public Call<UseAppInfoData> retroUpdateUserPushLog(@Field("idx") String idx,
                                                         @Field("sort") String sort,
                                                         @Field("screen_state") String screen_state);

    @FormUrlEncoded
    @POST(URL_USEAPP + "updateTutorialFinished")      //here is the other url part.best way is to start using /
    public Call<UseAppInfoData> retroUpdateTutorialFinished(@Field("student_id") String StudentID);



    @FormUrlEncoded
    @POST(URL_USEAPP + "insertWordReport")      //here is the other url part.best way is to start using /
    public Call<UseAppInfoData> retroInsertWordReport(@Field("student_id") String student_id,
                                                       @Field("word_id") String word_id,
                                                       @Field("reason") String reason);


    @GET(URL_USEAPP + "isApplyBetaService")      //here is the other url part.best way is to start using /
    public Call<UseAppInfoData> retroIsApplyBetaService(@Query("student_id") String student_id);

    @GET(URL_USEAPP + "getUserRestoreDb")      //here is the other url part.best way is to start using /
    public Call<UseAppInfoData> retroGetUserRestoreDb(@Query("student_id")String student_id);

    @GET(URL_USEAPP + "getBetaServiceAddress")      //here is the other url part.best way is to start using /
    public Call<UseAppInfoData> retroGetBetaServiceAddress();

    @GET(URL_USEAPP + "getLockScreenComment")      //here is the other url part.best way is to start using /
    public Call<UseAppInfoData> retroGetLockScreenComment();





}
