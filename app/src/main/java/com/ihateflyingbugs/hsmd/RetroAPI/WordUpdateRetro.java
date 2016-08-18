package com.ihateflyingbugs.hsmd.RetroAPI;

import com.ihateflyingbugs.hsmd.model.WordUpdateData;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by 영철 on 2016-05-18.
 */
public interface WordUpdateRetro {


    public String URL_WORDUPDATE = "http://52.79.87.3/rest/index.php/WorkBook/";

    @GET(URL_WORDUPDATE + "wordsDownload")      //here is the other url part.best way is to start using /
    public Call<WordUpdateData> retroUpdateWord(@Query("word_version")String word_version);

}
