package com.hanyu.desheng.utils;


import com.google.gson.Gson;

public class GsonUtils {
	private static Gson gson;

	private static Gson getGson() {
		if (gson == null) {
			gson = new Gson();
		}
		return gson;
	}

	public static <T> T json2Bean(String json, Class<T> clazz) {
		getGson();
		return gson.fromJson(json, clazz);
	}

}
