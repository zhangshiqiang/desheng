/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hanyu.desheng.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easemob.util.HanziToPinyin;
import com.hanyu.desheng.domain.User;
import com.hanyu.desheng.easemob.chatuidemo.Constant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class UserDao {
	public static final String TABLE_NAME = "uers";
	public static final String COLUMN_NAME_ID = "username";
	public static final String COLUMN_NAME_NICK = "nick";
	public static final String COLUMN_NAME_AVATAR = "avatar";
	public static final String COLUMN_NAME_MOBILE = "mobile";

	private DbOpenHelper dbHelper;

	public UserDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	public void saveContactList(List<User> contactList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
//			db.delete(TABLE_NAME, null, null);
			for (User user : contactList) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, user.getUsername());
				if (user.getNick() != null)
					values.put(COLUMN_NAME_NICK, user.getNick());
				if (user.getAvatar() != null)
					values.put(COLUMN_NAME_AVATAR, user.getAvatar());
				if (user.getMobile() != null)
					values.put(COLUMN_NAME_MOBILE, user.getMobile());
				db.replace(TABLE_NAME, null, values);
			}
		}
	}
	
	/**
	 * 分页加载好友数据
	 * @return
	 */
	private int pageSize = 10;
	public Map<String, User> getContactListByPage(int page){
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, User> users = new HashMap<String, User>();
		if (db.isOpen()) {
//			Cursor cursor = db.rawQuery(
//					"select * from " + TABLE_NAME /* + " desc" */ + " limit "+ page * 10 +"," + (page+1)*10, null);
//			String sql = "select * from "+TABLE_NAME+" limit ?,?";
//			Cursor cursor = db.1rawQuery(sql, new String[]{(page*10)+"",((page+1)*10)+""});
			Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, (page*pageSize)+","+pageSize+"");
			
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_ID));
				String mobile = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_MOBILE));
				String nick = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_AVATAR));
				User user = new User();
				user.setUsername(username);
				user.setNick(nick);
				user.setAvatar(avatar);
				user.setMobile(mobile);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}

				
				if (username.equals(Constant.NEW_FRIENDS_USERNAME)
						|| username.equals(Constant.GROUP_USERNAME)) {
					user.setHeader("");
				} else if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance()
							.get(headerName.substring(0, 1)).get(0).target
							.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				users.put(username, user);
			}
			cursor.close();
		}
		return users;
	}
	

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, User> users = new HashMap<String, User>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select * from " + TABLE_NAME /* + " desc" */, null);
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_ID));
				String mobile = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_MOBILE));
				String nick = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_NICK));
				String avatar = cursor.getString(cursor
						.getColumnIndex(COLUMN_NAME_AVATAR));
				User user = new User();
				user.setUsername(username);
				user.setNick(nick);
				user.setAvatar(avatar);
				user.setMobile(mobile);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}
				if (username==null) {
					continue;
				}

				if (username.equals(Constant.NEW_FRIENDS_USERNAME)
						|| username.equals(Constant.GROUP_USERNAME)) {
					user.setHeader("");
				} else if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance()
							.get(headerName.substring(0, 1)).get(0).target
							.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				users.put(username, user);
			}
			cursor.close();
		}
		return users;
	}

	/**
	 * 删除一个联系人
	 * 
	 * @param username
	 */
	public void deleteContact(String username) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?",
					new String[] { username });
		}
	}

	/**
	 * 保存一个联系人
	 * 
	 * @param user
	 */
	public void saveContact(User user) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME_ID, user.getUsername());
		if (user.getNick() != null)
			values.put(COLUMN_NAME_NICK, user.getNick());
		if (user.getAvatar() != null)
			values.put(COLUMN_NAME_AVATAR, user.getAvatar());
		if (user.getMobile() != null)
			values.put(COLUMN_NAME_MOBILE, user.getMobile());
		if (db.isOpen()) {
			db.replace(TABLE_NAME, null, values);
		}
	}

	/**
	 * 改变联系人的信息
	 * 
	 * @param user
	 */
	public void updateUser(User user) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		if (!TextUtils.isEmpty(user.getAvatar())) {
			values.put(COLUMN_NAME_AVATAR, user.getAvatar());
		}
		if (!TextUtils.isEmpty(user.getNick())) {
			values.put(COLUMN_NAME_NICK, user.getNick());
		}
		if (!TextUtils.isEmpty(user.getMobile())) {
			values.put(COLUMN_NAME_MOBILE, user.getMobile());
		}
		if (db.isOpen()) {
			db.update(TABLE_NAME, values, COLUMN_NAME_ID + "=?",
					new String[] { user.getUsername() });
		}
	}
	
	public void deleteAll(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
		}
	}
}
