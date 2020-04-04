package com.kits.asli.webService;//package com.kits.test.webService;

import com.kits.asli.model.Good;
import com.kits.asli.model.GoodResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface {


    @POST("index.php")
    @FormUrlEncoded
    Call<String> XUserCreate(@Field("tag") String tag, @Field("UName") String user, @Field("UPass") String pass, @Field("FName") String fname, @Field("LName") String lname, @Field("mobile") String mobile, @Field("address") String address, @Field("email") String email);


    @POST("index.php")
    @FormUrlEncoded
    Call<String> info(@Field("tag") String tag, @Field("Where") Integer Where);

    @POST("index.php")
    @FormUrlEncoded
    Call<String> GetImage(@Field("tag") String tag, @Field("GoodCode") String GoodCode, @Field("IX") Integer IX);


    @POST("index.php")
    @FormUrlEncoded
    Call<String> Kowsar_log(@Field("tag") String tag, @Field("Device_Id") String Device_Id, @Field("Address_Ip") String Address_Ip, @Field("Server_Name") String Server_Name, @Field("Factor_Code") String Factor_Code, @Field("StrDate") String StrDate, @Field("Broker") String Broker, @Field("Explain") String Explain);


    @POST("index.php")
    @FormUrlEncoded
    Call<GoodResponse> check(@Field("tag") String tag);

    @POST("index.php")
    @FormUrlEncoded
    Call<GoodResponse> check_page(@Field("tag") String tag, @Field("page") Integer page, @Field("recorde") Integer recorde);

    @POST("index.php")
    @FormUrlEncoded
    Call<Good> GetGood(@Field("tag") String tag, @Field("Where") String Where);


}

