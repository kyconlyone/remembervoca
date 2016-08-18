package com.ihateflyingbugs.hsmd.RetroAPI;

import com.ihateflyingbugs.hsmd.model.ManagerData;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by 영철 on 2016-05-18.
 */
public interface ManagerRetro {


    public String URL_MANAGER = "http://52.79.87.3/rest/index.php/Manager/";

    @FormUrlEncoded
    @POST(URL_MANAGER + "StudentRelationCode")      //here is the other url part.best way is to start using /
    public Call<ManagerData> retroInsertRelationCode(@Field("student_id") String StudentID,
                                                     @Field("relation_code") String relation_code);

    @FormUrlEncoded
    @POST(URL_MANAGER + "WordBookEachCount")      //here is the other url part.best way is to start using /
    public Call<ManagerData> retroInsertWorkbookEachCount(@Field("student_id") String StudentID,
                                                         @Field("word_count") String word_count,
                                                         @Field("grade") String grade);


    @FormUrlEncoded
    @POST(URL_MANAGER + "TestExam")      //here is the other url part.best way is to start using /
    public Call<ManagerData> retroInsertTestExam(@Field("student_id") String StudentID,
                                                 @Field("exam_list") String exam_list);

    @FormUrlEncoded
    @POST(URL_MANAGER + "FailMakeExam")      //here is the other url part.best way is to start using /
    public Call<ManagerData> retroUpdateFailMakeExam(@Field("student_id") String StudentID);


    @FormUrlEncoded
    @POST(URL_MANAGER + "StopTest")      //here is the other url part.best way is to start using /
    public Call<ManagerData> retroUpdateStopTest(@Field("test_id") String test_id,
                                                 @Field("edit_type") String edit_type);


    @FormUrlEncoded
    @POST(URL_MANAGER + "FinishTestPaper")      //here is the other url part.best way is to start using /
    public Call<ManagerData> retroUpdateFinishTest(@Field("exam_result") String exam_result,
                                                    @Field("totalcount") String totalcount);


    @GET(URL_MANAGER + "WordTestResult")      //here is the other url part.best way is to start using /
    public Call<ManagerData> retroGetWordTestResult(@Query("test_id") String test_id);

    @GET(URL_MANAGER + "MyExistTest")      //here is the other url part.best way is to start using /
    public Call<ManagerData> retroIsExistTest(@Query("student_id") String student_id);


    @GET(URL_MANAGER + "MyExistTest2")      //here is the other url part.best way is to start using /
    public Call<ManagerData> retroIsExistTest2(@Query("student_id") String student_id);





    @FormUrlEncoded
    @POST(URL_MANAGER + "UpdateTestPaper")      //here is the other url part.best way is to start using /
    public Call<ManagerData> retroUpdateTestPaper(@Field("row_id") int test_id,
                                              @Field("choice_num") int choice_num);




}
