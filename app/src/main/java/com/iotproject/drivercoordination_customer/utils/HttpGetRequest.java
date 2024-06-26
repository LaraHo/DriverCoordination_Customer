package com.iotproject.drivercoordination_customer.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**网络请求工具
 * Get请求
 */
public class HttpGetRequest {
    public static void sendOkHttpGetRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}

