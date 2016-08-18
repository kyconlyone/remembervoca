package com.ihateflyingbugs.hsmd.RetroAPI;

import com.ihateflyingbugs.hsmd.model.ChannelInfo;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;


/**
 * Created by 영철 on 2016-05-18.
 */
public interface TestRetro {


    public String URL_PAYMENT = "http://52.79.87.3/rest/index.php/Payment/";

    @GET(URL_PAYMENT + "UsableCoupon")      //here is the other url part.best way is to start using /
    public Call<ChannelInfo> retroGetUsableCoupon(@Query("student_id") String StudentID);

}
