package com.ihateflyingbugs.hsmd.RetroAPI;

import com.ihateflyingbugs.hsmd.model.OriginWordData;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by 영철 on 2016-05-18.
 */
public interface OriginWordRetro {


    public String URL_ORIGINWORD = "http://52.79.87.3/rest/index.php/OriginWord/";

    @GET(URL_ORIGINWORD + "OriginWordList")      //here is the other url part.best way is to start using /
    public Call<OriginWordData> retroGetOriginWordList(@Query("student_id") String StudentID);

}
