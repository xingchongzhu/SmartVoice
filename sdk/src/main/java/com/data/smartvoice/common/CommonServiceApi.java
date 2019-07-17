package com.data.smartvoice.common;

import com.data.smartvoice.entry.DataResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

interface CommonServiceApi {

    @GET("appClient/validate/{appId}/{pkgName}/{appName}/{signature}")
    Call<DataResult<String>> isAppValid(@Path("appId") String appId, @Path("pkgName") String pkgName, @Path("appName") String appName, @Path("signature") String sign);

}
