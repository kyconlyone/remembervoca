package com.ihateflyingbugs.hsmd.RetroAPI;

import com.ihateflyingbugs.hsmd.data.FAQ;
import com.ihateflyingbugs.hsmd.data.MyQnA;
import com.ihateflyingbugs.hsmd.model.BoardData;
import com.ihateflyingbugs.hsmd.tutorial.Feed;

import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by 영철 on 2016-05-18.
 */
public interface BoardRetro {


    public String URL_BOARD = "http://52.79.87.3/rest/index.php/Board/";

    @GET(URL_BOARD + "NoticeBoard")      //here is the other url part.best way is to start using /
    public Call<List<Feed>> retroGetNotice();

    @GET(URL_BOARD + "FAQBoard")      //here is the other url part.best way is to start using /
    public Call<List<FAQ>> retroGetFAQBoard();

    @GET(URL_BOARD + "QnABoard")      //here is the other url part.best way is to start using /
    public Call<List<MyQnA>> retroGetQnABoard(@Query("student_id") String StudentID);


    @FormUrlEncoded
    @POST(URL_BOARD + "QnABoard")      //here is the other url part.best way is to start using /
    public Call<BoardData> retroSendQnABoard(@Field("student_id") String StudentID,
                                             @Field("phone_number")String phone_number,
                                             @Field("qna_question")String qna_question);

}
