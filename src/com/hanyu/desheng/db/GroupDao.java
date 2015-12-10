package com.hanyu.desheng.db;


import java.util.List;

import com.hanyu.desheng.bean.GroupBean;
import com.hanyu.desheng.util.ShUtils;
import com.leaf.library.db.TemplateDAO;

public class GroupDao extends TemplateDAO<GroupBean,String>{

	public GroupDao() {
		super(ShUtils.getDbhelper());
	}
	
	private static GroupDao dao;
	
	private static GroupDao getDao(){
		if(dao==null){
			dao=new GroupDao();
		}
		return dao;
	}
	
	public static void saveGroup(GroupBean gb){
		getDao().insert(gb);
	}

	/**
	 * 是否已存在
	 * @param groupid
	 * @param hxusername
	 * @return
	 */
	public static boolean getGroup(String groupid,String hxusername){
		String selection="groupid=? and hxusername=?";
		String selectionArgs[]=new String[]{groupid,hxusername};
		List<GroupBean> list=getDao().find(null, selection, selectionArgs,null,null,null,null);
		if(list.size()>0){
			return true;
		}else{
			return false;
		}
	} 
}
