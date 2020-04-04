package com.kits.asli.webService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient_kowsar {

    private static Retrofit t = null;

    private static final String BASE_URL_log = "http://87.107.78.234:60005/login/";

    public static Retrofit getCleint_log() {
        if (t == null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            t = new Retrofit.Builder()
                    .baseUrl(BASE_URL_log)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return t;
    }
}
