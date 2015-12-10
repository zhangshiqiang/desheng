package com.hanyu.desheng.db;

import android.content.Context;

public class DbManager {
 
	public static UserDao userDao;
	
	public static UserDao getUserDao(Context context){
		if(userDao==null){
			userDao=new UserDao(context);
		}
		return userDao;
	}
}
