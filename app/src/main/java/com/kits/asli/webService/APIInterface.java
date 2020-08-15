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
    Call<String> GetImage(@Field("tag") String tag
            , @Field("GoodCode") String GoodCode
            , @Field("IX") Integer IX);


    @POST("index.php")
    @FormUrlEncoded
    Call<String> Kowsar_log(@Field("tag") String tag
            , @Field("Device_Id") String Device_Id
            , @Field("Address_Ip") String Address_Ip
            , @Field("Server_Name") String Server_Name
            , @Field("Factor_Code") String Factor_Code
            , @Field("StrDate") String StrDate
            , @Field("Broker") String Broker
            , @Field("Explain") String Explain);


    @POST("index.php")
    @FormUrlEncoded
    Call<GoodResponse> customer_insert(@Field("tag") String tag
            , @Field("BrokerRef") String BrokerRef
            , @Field("CityCode") String CityCode
            , @Field("KodeMelli") String KodeMelli
            , @Field("FName") String FName
            , @Field("LName") String LName
            , @Field("Address") String Address
            , @Field("Phone") String Phone
            , @Field("Mobile") String Mobile
            , @Field("EMail") String EMail
            , @Field("PostCode") String PostCode
            , @Field("ZipCode") String ZipCode);


}

