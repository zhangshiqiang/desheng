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
package com.hanyu.desheng.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.hanyu.desheng.R;
import com.hanyu.desheng.base.BaseActivity;
import com.hanyu.desheng.bean.TopUserBean;
import com.hanyu.desheng.engine.EngineManager;
import com.hanyu.desheng.engine.HttpTask;
import com.hanyu.desheng.utils.GsonUtils;
import com.hanyu.desheng.utils.SharedPreferencesUtil;

@SuppressLint("ShowToast")
public class NewGroupActivity extends BaseActivity {
	private EditText groupNameEditText;
	private ProgressDialog progressDialog;
	private EditText introductionEditText;
	private CheckBox checkBox;
	private CheckBox memberCheckbox;
	private LinearLayout openInviteContainer;

	

	@Override
	public int setLayout() {
		return R.layout.activity_new_group;
	}

	@Override
	public void init(Bundle savedInstanceState) {
		groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
		introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
		checkBox = (CheckBox) findViewById(R.id.cb_public);
		memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
		openInviteContainer = (LinearLayout) findViewById(R.id.ll_open_invite);
		
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					openInviteContainer.setVisibility(View.INVISIBLE);
				}else{
					openInviteContainer.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@Override
	public void setListener() {
		
	}
	@Override
	public void onClick(View arg0) {
		
	}

	/**
	 * @param v
	 */
	public void save(View v) {
		String str6 = getResources().getString(R.string.Group_name_cannot_be_empty);
		String name = groupNameEditText.getText().toString();
		if (TextUtils.isEmpty(name)) {
			Intent intent = new Intent(this, AlertDialog.class);
			intent.putExtra("msg", str6);
			startActivity(intent);
		} else {
			// 进通讯录选人
			startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), 0);
		}
	}
	
	private EMGroup eMGroup=null;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
		final String st2 = getResources().getString(R.string.Failed_to_create_groups);
		if (resultCode == RESULT_OK) {
			//新建群组
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(st1);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();

			new Thread(new Runnable() {
				@Override
				public void run() {
					// 调用sdk创建群组方法
					String groupName = groupNameEditText.getText().toString().trim();
					String desc = introductionEditText.getText().toString();
					String[] members = data.getStringArrayExtra("newmembers");
					try {
						if(checkBox.isChecked()){
							//创建公开群，此种方式创建的群，可以自由加入
//							EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, false);
							//创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
							 eMGroup= EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true);
						}else{
							//创建不公开群
							eMGroup=EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked());
						}
						runOnUiThread(new Runnable() {
							public void run() {
								if(!"".equals(SharedPreferencesUtil.getStringData(NewGroupActivity.this,"miphone",""))){
									getTopHxUser(SharedPreferencesUtil.getStringData(NewGroupActivity.this,"miphone",""),eMGroup.getGroupId());
								}else{
									progressDialog.dismiss();
								}
							}
						});
					} catch (final EaseMobException e) {
						runOnUiThread(new Runnable() {
							public void run() {
								progressDialog.dismiss();
								Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), 1).show();
							}
						});
					}
					
				}
			}).start();
		}
	}

	public void back(View view) {
		finish();
	}
	
//	/**
//	 * 检查上级店铺id是否可用
//	 * 
//	 * @param vspid
//	 */
//	protected void checkUpShopid(final String vspid,final String groupid) {
//		new HttpTask<Void, Void, InputStream>(this) {
//
//			@Override
//			protected InputStream doInBackground(Void... params) {
//				InputStream result = EngineManager.getShopEngine()
//						.toGetStoreinfo(vspid);
//				return result;
//			}
//
//			@Override
//			protected void onPostExecute(InputStream result) {
//				try {
//					if (result != null) {
//						ShopInfo shopinfo = new ShopInfo();
//						XmlComonUtil.streamText2Model(result, shopinfo);
//						if(!TextUtils.isEmpty(shopinfo.vsphone)){
//							LogUtil.i("tag", "上级电话号码："+shopinfo.vsphone);
//							LogUtil.i("tag", "群号码："+groupid);
//							getTopHxUser(shopinfo.vsphone, groupid);
//						}
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//
//			@Override
//			protected void onPreExecute() {
//			}
//		}.executeProxy();
//	}
	
	/**
	 * 获取环信用户名把用户拉近群
	 * @param mobile
	 * @param hx_group_id
	 */
	private void getTopHxUser(final String mobile,final String hx_group_id){
		new HttpTask<Void, Void, String>(this) {

			@Override
			protected String doInBackground(Void... params) {
				return EngineManager.getUserEngine().getTopHxUser(mobile, hx_group_id);
			}

			@Override
			protected void onPostExecute(String result) {
				if(result!=null){
					try {
						JSONObject jObj=new JSONObject(result);
						if("0".equals(jObj.getString("code"))){
							TopUserBean tb=GsonUtils.json2Bean(result,TopUserBean.class);
							EMGroupManager.getInstance().addUsersToGroup(hx_group_id, new String[]{tb.data.hx_name});
						}
						progressDialog.dismiss();
						Intent intent = new Intent();
						intent.putExtra("groupId", hx_group_id);
						setResult(RESULT_OK, intent);
						finish();
					} catch (JSONException e1) {
						e1.printStackTrace();
					}catch (EaseMobException e) {
						e.printStackTrace();
					}
					
				}else{
					progressDialog.dismiss();
				}
			}

			@Override
			protected void onPreExecute() {
			}
			
			
		}.executeProxy();
	}

	
}
