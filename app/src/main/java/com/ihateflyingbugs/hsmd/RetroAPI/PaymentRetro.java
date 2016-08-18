package com.ihateflyingbugs.hsmd.RetroAPI;

import com.ihateflyingbugs.hsmd.model.CouponData;
import com.ihateflyingbugs.hsmd.model.PaymentData;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;


/**
 * Created by 영철 on 2016-05-18.
 */
public interface PaymentRetro {


    public String URL_PAYMENT = "http://52.79.87.3/rest/index.php/Payment/";

    @FormUrlEncoded
    @POST(URL_PAYMENT+"PaymentInfo")      //here is the other url part.best way is to start using /
    public Call<PaymentData> retroInsertPaymentInfo(@Field("payment_request_id") String payment_request_id,
                                                  @Field("data_signature")String data_signature,
                                                  @Field("purchase_data")String purchase_data);


    @FormUrlEncoded
    @POST(URL_PAYMENT+"insertPaymentRequest")      //here is the other url part.best way is to start using /
    public Call<PaymentData> retroInsertPaymentRequest(@Field("student_id") String student_id,
                                                       @Field("payment_type_id")String payment_type_id);

    @FormUrlEncoded
    @POST(URL_PAYMENT+"updatePaymentRequest")      //here is the other url part.best way is to start using /
    public Call<PaymentData> retroUpdatePaymentRequest(@Field("student_id") String student_id,
                                                       @Field("payment_request_id")String payment_request_id,
                                                       @Field("product_type_id")String product_type_id);

   @GET(URL_PAYMENT+"ProductInfoList")      //here is the other url part.best way is to start using /
    public Call<PaymentData> retroGetProductInfoList();


    @FormUrlEncoded
    @POST(URL_PAYMENT+"GoogleItemConsume")      //here is the other url part.best way is to start using /
    public Call<PaymentData> retroInsertGoogleItemConsume(@Field("student_id") String student_id,
                                             @Field("payment_request_id")String payment_request_id);


    @GET(URL_PAYMENT + "UsableCoupon")      //here is the other url part.best way is to start using /
    public Call<CouponData> retroGetUsableCoupon(@Query("student_id") String StudentID);


    @FormUrlEncoded
    @POST(URL_PAYMENT + "Coupon")      //here is the other url part.best way is to start using /
    public Call<CouponData> retroInsertCoupon(@Field("student_id") String StudentID,
                                              @Field("coupon_type_id") String coupon_type_id);

    @GET(URL_PAYMENT + "SetInviteFreePeriod")      //here is the other url part.best way is to start using /
    public Call<PaymentData> retrogetInviteFreePeriod();

    @FormUrlEncoded
    @POST(URL_PAYMENT + "FinishDateAtLeft")      //here is the other url part.best way is to start using /
    public Call<PaymentData> retroInsertFinishDateAtLeft(@Field("student_id") String StudentID);


    @FormUrlEncoded
    @POST(URL_PAYMENT + "FinishDateWithInvite")      //here is the other url part.best way is to start using /
    public Call<PaymentData> retroInsertFinishDateAtInvite(@Field("student_id") String StudentID,
                                                         @Field("payment_type_id") String payment_type_id);



    @FormUrlEncoded
    @POST(URL_PAYMENT + "ClickPayRequest")      //here is the other url part.best way is to start using /
    public void retroUpdateClickPayStep(@Field("payment_request_id") String payment_request_id);

    @GET(URL_PAYMENT + "CheckGotFreeDay")      //here is the other url part.best way is to start using /
    public Call<CouponData> retroCheckGotFreeDay(@Query("student_id") String StudentID);

    @FormUrlEncoded
    @POST(URL_PAYMENT + "GiftCard")      //here is the other url part.best way is to start using /
    public Call<PaymentData> retroGiftCard(@Field("student_id") String student_id,
                              @Field("payment_request_id") String payment_request_id,
                              @Field("gift_card_num") String gift_card_num);


}
