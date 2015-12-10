package com.hanyu.desheng.db;


import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.hanyu.desheng.bean.PhoneBean;
import com.hanyu.desheng.util.ShUtils;
import com.leaf.library.db.TemplateDAO;

public class PhoneDao extends TemplateDAO<PhoneBean, String>{

	public PhoneDao() {
		super(ShUtils.getDbhelper());
		// TODO Auto-generated constructor stub
	}

	private static PhoneDao dao;
	private static PhoneDao getDao(){
		if(dao==null){
			dao=new PhoneDao();
		}
		return dao;
	}
	
	public static void insertPhoneList(List<PhoneBean> list){
		getDao().deleteAll();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				getDao().insert(list.get(i));
			}
		}
	}
	public static void insertPhoneBean(PhoneBean pb){
		getDao().insert(pb);
	}
	
	public static String findByMobole(String mobile){
		String selection="mobile=?";
		String [] selectionArgs=new String[]{mobile};
		List<PhoneBean> list=getDao().find(null, selection, selectionArgs, null, null, null, null);
		if(list.size()>0){
			return list.get(0).hx_name;
		}else{
			return null;
		}
	}
	
	public void updatePhone(String mobile,int type){
		SQLiteDatabase db=getDao().getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("type", type);
		db.update(getDao().getTableName(), values,"mobile=?", new String[]{mobile});
	}

		
	
}
