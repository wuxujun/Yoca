package com.xujun.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
	
	public static Object ObjFromJson(String jsonData, Class z) {
		Gson gson = new Gson();
        return gson.fromJson(jsonData, z);
	}

	public static List listFromJson(JSONArray ja, Class z)
			throws JSONException {
		Gson gson=new Gson();
 		List list = new ArrayList();
		for (int i = 0; i < ja.length(); i++) {
			list.add(gson.fromJson(ja.get(i).toString(), z));
		}
		return list;
	}

    public static String toJson(Object obj) throws JSONException{
        Gson gson=new Gson();
        return gson.toJson(obj);
    }

    public static Object ObjFromJsonNew(String jsonData, Class z) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(NumRuntime.class,new NumRuntimeTypeAdapter());
        Gson gson=builder.create();
        return gson.fromJson(jsonData, z);
    }

}
