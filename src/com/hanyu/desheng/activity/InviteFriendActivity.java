package com.hanyu.desheng.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.hanyu.desheng.R;
import com.hanyu.desheng.adapter.PhoneContactsAdapter;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.MobileData;
import com.hanyu.desheng.bean.PhoneBean;
import com.hanyu.desheng.bean.PhoneModel;
import com.hanyu.desheng.db.PhoneDao;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.hanyu.desheng.pulltorefresh.PullToRefreshListView;
import com.hanyu.desheng.ui.ClearEditText;
import com.hanyu.desheng.ui.silent.handle.CharacterParser;
import com.hanyu.desheng.ui.silent.handle.PinyinComparator;
import com.hanyu.desheng.utils.MyToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Toast;

public class InviteFriendActivity extends BaseActivity {
	@ViewInject(R.id.invitefriend_lv)
	private PullToRefreshListView invitefriend_lv;
	@ViewInject(R.id.invitefriend_cet)
	private ClearEditText invitefriend_cet;
	@ViewInject(R.id.T_selectAll)
	private CheckBox T_SelectAll;
	@SuppressWarnings("unused")
	private List<String> mobilelist = new ArrayList<String>();

	/** 获取库Phon表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,
			Phone.CONTACT_ID };

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;
	protected static final String tag = "InviteFriendActivity";
	/** 联系人名称 **/
	private ArrayList<String> mContactsName = new ArrayList<String>();

	/** 联系人电话号 **/
	private ArrayList<String> mContactsNumber = new ArrayList<String>();

	/** 联系人头像 **/
	private ArrayList<Bitmap> mContactsPhonto = new ArrayList<Bitmap>();
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<PhoneModel> SourceDateList;

	private List<PhoneModel> allDateList;
	private boolean isAllSelector = false;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private PhoneContactsAdapter adapter;

	private Gson gson = new Gson();

	ProgressDialog dialog;

	@Override
	public void onClick(View v) {

	}

	@Override
	public int setLayout() {
		return R.layout.invitefriend;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		dialog = new ProgressDialog(this);
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		setBack();
		setTopTitle("邀请好友");
		context = InviteFriendActivity.this;
		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setMessage("获取通讯录中。。。");
		dialog.show();

		getPhoneContacts(0);
		// getAllContacts();

		dialog.dismiss();
		invitefriend_cet.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// filterData(s.toString());
				if (TextUtils.isEmpty(s + "")) {
					page = 0;
					getPhoneContacts(0);
				} else {
					searchFriendByName(s + "");
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		invitefriend_lv.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				// Toast.makeText(InviteFriendActivity.this,
				// ((PhoneModel) adapter.getItem(position)).getName(),
				// Toast.LENGTH_SHORT).show();
			}
		});
		T_SelectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked){
							isAllSelector = true;
						}else{
							isAllSelector = false;				
						}
						selectorAll();
					}
				});
		
		selectorAll();

	}
	private void selectorAll(){
		if(isAllSelector){
			selectAllContacts();
			
		}else{
			invertSelection();
			
		}
		
		
	}
	public void selectAllContacts() {  
        for (int i = 0; i < SourceDateList.size(); i++) {  
            PhoneModel contact = SourceDateList.get(i);  
            adapter.map_NumberSelected.put(contact, true);  
        }  
        isAllSelector = true;
        adapter.notifyDataSetChanged();
       // refreshList();  
    }  
    public void invertSelection() {  
        for (int i = 0; i < SourceDateList.size(); i++) {  
            PhoneModel contact = SourceDateList.get(i);  
            adapter.map_NumberSelected.put(contact, false);  
        }  
        isAllSelector = false;
        adapter.notifyDataSetChanged();
       // refreshList();  
    }  
	protected void searchFriendByName(String str) {
		ContentResolver resolver = context.getContentResolver();

		Cursor cursor = null;
		// 获取手机联系人
		cursor = resolver.query(
				Phone.CONTENT_URI, PHONES_PROJECTION, ContactsContract.PhoneLookup.DISPLAY_NAME + " like " + "'%" + str
						+ "%'" + " or " + ContactsContract.CommonDataKinds.Phone.NUMBER + " like " + "'%" + str + "%'",
				null, null);
		if (cursor != null) {
			mContactsName.clear();
			mContactsPhonto.clear();
			mContactsNumber.clear();
			while (cursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = cursor.getString(PHONES_NUMBER_INDEX).toString().trim();
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber)) {
					continue;
				}

				// 得到联系人名称
				String contactName = cursor.getString(PHONES_DISPLAY_NAME_INDEX);

				// 得到联系人ID
				Long contactid = cursor.getLong(PHONES_CONTACT_ID_INDEX);

				// 得到联系人头像ID
				Long photoid = cursor.getLong(PHONES_PHOTO_ID_INDEX);

				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;

				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				}

				mContactsName.add(contactName);

				mContactsPhonto.add(contactPhoto);
				mContactsNumber.add(filterUnNumber(phoneNumber).replace(" ", ""));
			}
			cursor.close();
		}
		List<PhoneModel> list = filledData(mContactsName, mContactsPhonto, mContactsNumber);
		if (SourceDateList == null) {
			SourceDateList = new ArrayList<PhoneModel>();
		}
		SourceDateList.clear();
		SourceDateList.addAll(list);

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		setOrNotifyAdapter();
	}

	@Override
	public void setListener() {
		invitefriend_lv.setPullRefreshEnabled(true);
		invitefriend_lv.setPullLoadEnabled(true);
		invitefriend_lv.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				// page = 0;
				// getPhoneContacts(0);
				invitefriend_lv.onPullDownRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				page += 1;
				getPhoneContacts(1);
				invitefriend_lv.onPullUpRefreshComplete();
			}
		});

	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param mContactsName2
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private List<PhoneModel> filledData(ArrayList<String> mContactsName2, ArrayList<Bitmap> mContactsPhonto2,
			ArrayList<String> mobiles) {
		List<PhoneModel> mSortList = new ArrayList<PhoneModel>();
		// mContactsName2.size()
		for (int i = 0; i < mobiles.size(); i++) {
			PhoneModel sortModel = new PhoneModel();
			// sortModel.setImgSrc(mContactsPhonto2.get(i));
			sortModel.setName(mContactsName2.get(i));
			sortModel.setMobile(mobiles.get(i));
			Log.e("mobile", mobiles.get(i));
			// 汉字转换成拼音
			try {
				String pinyin = characterParser.getSelling(mContactsName2.get(i));
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}

				mSortList.add(sortModel);
			} catch (Exception e) {

			}

		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<PhoneModel> filterDateList = new ArrayList<PhoneModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (PhoneModel sortModel : allDateList) {
				String name = sortModel.getName();
				String sortName = characterParser.getSelling(sortModel.getName()).toUpperCase();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(filterStr.toString())
						|| sortModel.getMobile().startsWith(filterStr.toString())
						|| sortName.startsWith(filterStr.toString().toUpperCase())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	private int page = 0;
	private int pageSize = 32;

	/** 得到手机通讯录联系人信息 **/
	private void getPhoneContacts(int type) {
		Log.e("1111", "page:" + page);
		if (type == 0 && SourceDateList != null) {
			SourceDateList.clear(); 
		}
		ContentResolver resolver = context.getContentResolver();

		Cursor phoneCursor = null;
		// 获取手机联系人
		phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null,
				ContactsContract.PhoneLookup.DISPLAY_NAME + " COLLATE NOCASE  limit " + (page * pageSize) + ","
						+ pageSize);

		@SuppressWarnings("unused")
		String newphoneNumber = null;

		if (phoneCursor != null) {
			mContactsName.clear();
			mContactsPhonto.clear();
			mContactsNumber.clear();

			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX).toString().trim();
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber)) {
					continue;
				}

				// 得到联系人名称
				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;

				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				}
				if (!mContactsNumber.contains(filterUnNumber(phoneNumber).replace(" ", ""))) {
					mContactsName.add(contactName);
					mContactsPhonto.add(contactPhoto);
					mContactsNumber.add(filterUnNumber(phoneNumber).replace(" ", ""));
				}
			}
			if (mContactsNumber.isEmpty()) {
				Toast.makeText(InviteFriendActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
			}
			phoneCursor.close();

		}
		checkPhoneNumber(gson.toJson(mContactsNumber));
//		List<PhoneModel> list = filledData(mContactsName, mContactsPhonto, mContactsNumber);
		if (SourceDateList == null) {
			SourceDateList = new ArrayList<PhoneModel>();
		}
		
		for (int i = 0; i < mContactsNumber.size(); i++) {
			PhoneModel model = new PhoneModel();
			model.setName(mContactsName.get(i));
			model.setMobile(mContactsNumber.get(i));
			model.setImgSrc(mContactsPhonto.get(i)+"");
			SourceDateList.add(model);
			
		}
		
//		SourceDateList.addAll(list);
		// 根据a-z进行排序源数据
//		 Collections.sort(SourceDateList, pinyinComparator);

		setOrNotifyAdapter();

	}

	/** 得到手机通讯录联系人信息 **/
	private void getAllContacts() {
		// if (type == 0 && SourceDateList != null) {
		// SourceDateList.clear();
		// }
		ContentResolver resolver = context.getContentResolver();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
		@SuppressWarnings("unused")
		String newphoneNumber = null;

		if (phoneCursor != null) {
			mContactsName.clear();
			mContactsPhonto.clear();
			mContactsNumber.clear();

			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX).toString().trim();
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber)) {
					continue;
				}

				// 得到联系人名称
				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);

				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);

				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);

				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;

				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				}

				mContactsName.add(contactName);

				mContactsPhonto.add(contactPhoto);
				mContactsNumber.add(filterUnNumber(phoneNumber).replace(" ", ""));
			}
			if (mContactsNumber.isEmpty()) {
				Toast.makeText(InviteFriendActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
			}

			phoneCursor.close();
		}
		List<PhoneModel> list = filledData(mContactsName, mContactsPhonto, mContactsNumber);
		if (allDateList == null) {
			allDateList = new ArrayList<PhoneModel>();
		}
		allDateList.addAll(list);

		// 根据a-z进行排序源数据
		// Collections.sort(allDateList, pinyinComparator);

	}

	private void setOrNotifyAdapter() {
		if (adapter == null) {
			adapter = new PhoneContactsAdapter(InviteFriendActivity.this, SourceDateList);
			invitefriend_lv.getRefreshableView().setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 过滤通讯录里没有注册环信的手机号
	 * 
	 * @param mobiles
	 *            手机号数组
	 */
	private void checkPhoneNumber(final String mobiles) {
		new HttpTask<Void, Void, String>(context) {

			@Override
			protected String doInBackground(Void... params) {
				String result = EngineManager.getUserEngine().toFilterHXMobile(mobiles);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {
				if (result != null) {
					try {
						MobileData md = gson.fromJson(result, MobileData.class);
						if ("0".equals(md.code)) {
							List<PhoneBean> lpb = md.data.hx_mobile;
							if (lpb != null && lpb.size() > 0) {
								for (int i = 0; i < lpb.size(); i++) {
									if (PhoneDao.findByMobole(lpb.get(i).mobile) == null) {
										PhoneDao.insertPhoneBean(lpb.get(i));
									}
								}
								adapter.notifyDataSetChanged();
							}
						} else {
							String msg = md.msg;
							MyToastUtils.showShortToast(context, msg);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				super.onPostExecute(result);
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
		}.executeProxy();
	}

	public static String filterUnNumber(String str) {
		// 只允数字
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		// 替换与模式匹配的所有字符（即非数字的字符将被""替换）
		return m.replaceAll("").trim();
	}

}
