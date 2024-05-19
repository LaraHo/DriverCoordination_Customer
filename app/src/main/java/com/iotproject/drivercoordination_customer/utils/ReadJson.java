package com.iotproject.drivercoordination_customer.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ReadJson {
    private final JsonObject responseBodyJson;
    private static final String TAG = "连接后端测试：";

    public ReadJson(String responseBodyString){
        responseBodyJson = new Gson().fromJson(responseBodyString, JsonObject.class);
        Log.d(TAG, "信息: "+responseBodyString);
    }

    public int Code(){
        return responseBodyJson.get("code").getAsInt();
    }

    public String ReadString(String memberName){
        Log.d(TAG, "读到: "+responseBodyJson.get(memberName).getAsString());
        return responseBodyJson.get(memberName).getAsString();
    }

//    public Boolean has(String memberName){
//        Log.d(TAG, "有吗: "+responseBodyJson.has("memberName"));
//        return responseBodyJson.has("memberName");
//    }
}
