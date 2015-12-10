package com.hanyu.desheng.db;


import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.hanyu.desheng.bean.HxUserBean;
import com.hanyu.desheng.util.ShUtils;
import com.leaf.library.db.TemplateDAO;
/**
 * 用户数据库
 * @author wangbin
 *
 */
public class HxUserDao extends TemplateDAO<HxUserBean,String>{

	public HxUserDao() {
		super(ShUtils.getDbhelper());
	}

	private static HxUserDao dao;
	
	private static HxUserDao getDao(){
		if(dao==null){
			dao=new HxUserDao();
		}
		return dao;
	}
	
	/**
	 * 插入用户
	 * @param hxUser
	 */
	public static void insertHxUser(HxUserBean hxUser){
		HxUserDao dao=getDao();
		dao.insert(hxUser);
	}
	
	/**
	 * 通过环信用户名查找User
	 * @param hx_username
	 * @return
	 */
	public static HxUserBean findByHx(String hx_username){
		String selection="hx_username=?";
		String [] selectionArgs=new String[]{hx_username};
		List<HxUserBean> list=getDao().find(null, selection, selectionArgs, null, null, null, null);
		if(list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 
	 * @param hxUser
	 */
	public static void updateUser(HxUserBean hxUser){
		HxUserDao dao=getDao();
		SQLiteDatabase db=dao.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("username", hxUser.username);
		values.put("headpic", hxUser.headpic);
		db.update(dao.getTableName(),values,"hx_username=?",new String[]{hxUser.hx_username});
	}
}
