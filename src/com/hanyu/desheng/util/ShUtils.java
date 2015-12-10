package com.hanyu.desheng.util;

import android.content.Context;

import com.hanyu.desheng.ExampleApplication;
import com.leaf.library.db.SmartDBHelper;

public class ShUtils {
   
	private static SmartDBHelper dbHelper;
	public static Context getApplicationContext(){
		return getApplication().getApplicationContext();
	}
	
	public static SmartDBHelper getDbhelper(){
		if(dbHelper==null){
			dbHelper=new SmartDBHelper(getApplicationContext(), 
					ExampleApplication.DB_NAME,null,ExampleApplication.VERSION,ExampleApplication.MODELS);
		}
		return dbHelper;
	}
	
	public static ExampleApplication getApplication(){
		return ExampleApplication.getInstance();
	}
}
