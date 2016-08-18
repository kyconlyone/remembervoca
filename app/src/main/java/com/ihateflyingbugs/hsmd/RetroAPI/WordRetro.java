package com.ihateflyingbugs.hsmd.RetroAPI;

import com.ihateflyingbugs.hsmd.model.WordData;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by 영철 on 2016-05-18.
 */
public interface WordRetro {


    public String URL_BOARD = "http://52.79.87.3/rest/index.php/Word/";

    @GET(URL_BOARD + "getWorkbookListByWord")      //here is the other url part.best way is to start using /
    public Call<WordData> retroGetWorkbookListByWord(@Query("word_id")String word_id);

    @GET(URL_BOARD + "WordExamRate")      //here is the other url part.best way is to start using /
    public Call<WordData> retroGetWordExamRate(@Query("words_json") String words_json);

}
