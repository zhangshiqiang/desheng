package com.hanyu.desheng.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.hanyu.desheng.R;
import com.hanyu.desheng.activity.ChatActivity;
import com.hanyu.desheng.base.BaseFragment;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase;
import com.hanyu.desheng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.hanyu.desheng.pulltorefresh.PullToRefreshListView;
import com.hanyu.desheng.ui.ClearEditText;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

@SuppressLint("SimpleDateFormat")
public class MyGroupFragment extends BaseFragment {
	@ViewInject(R.id.list)
	private PullToRefreshListView groupListView;
	protected List<EMGroup> grouplist;
	private GroupAdapter groupAdapter;
	
	@ViewInject(R.id.contacts_fm_et)
	private ClearEditText contacts_fm_et;// 搜索框
	

	@Override
	public View initView(LayoutInflater inflater) {
		view = View.inflate(context, R.layout.mygroup_fm, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData(Bundle savedInstanceState) {
		grouplist =	EMGroupManager.getInstance().getAllGroups();
		groupAdapter = new GroupAdapter(getActivity(), 1, grouplist);
		groupListView.getRefreshableView().setAdapter(groupAdapter);
		groupListView.setPullLoadEnabled(false);
		groupListView.setPullRefreshEnabled(true);
		groupListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					//进入群聊
					Intent intent = new Intent(getActivity(), ChatActivity.class);
					// it is group chat
					intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
					intent.putExtra("groupId", groupAdapter.getItem(position).getGroupId());
					startActivityForResult(intent, 0);
			}

		});
		groupListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
				groupListView.setLastUpdatedLabel(format.format(new Date()));
				grouplist = EMGroupManager.getInstance().getAllGroups();
				groupAdapter = new GroupAdapter(getActivity(), 1, grouplist);
				groupListView.getRefreshableView().setAdapter(groupAdapter);
				groupAdapter.notifyDataSetChanged();
				groupListView.onPullDownRefreshComplete();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				
			}
		});
		contacts_fm_et.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	groupAdapter.getFilter().filter(s);
//                if (s.length() > 0) {
//                    clearSearch.setVisibility(View.VISIBLE);
//                } else {
//                    clearSearch.setVisibility(View.INVISIBLE);
//                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
	}

	@Override
	public void onResume() {
		super.onResume();
		grouplist = EMGroupManager.getInstance().getAllGroups();
		groupAdapter = new GroupAdapter(getActivity(), 1, grouplist);
		groupListView.getRefreshableView().setAdapter(groupAdapter);
		groupAdapter.notifyDataSetChanged();
	}
	@Override
	public void onClick(View v) {

	}
	@Override
	public void setListener() {

	}

	
	class GroupAdapter extends ArrayAdapter<EMGroup> {


		private LayoutInflater inflater;
		@SuppressWarnings("unused")
		private String str;

		public GroupAdapter(Context context, int res, List<EMGroup> groups) {
			super(context, res, groups);
			this.inflater = LayoutInflater.from(context);
			str = context.getResources().getString(R.string.The_new_group_chat);
		}
		
//		@Override
//		public int getViewTypeCount() {
//			return 2;
//		}
//		
//		@Override
//		public int getItemViewType(int position) {
//		    if(position == 0){
//		        return 0;
//		    }else { 
//		        return 1;
//		    }
//		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
//			if(getItemViewType(position) == 0){
//		        if (convertView == null) {
//	                convertView = inflater.inflate(R.layout.search_bar_with_padding, null);
//	            }
//	    	    final EditText query = (EditText) convertView.findViewById(R.id.query);
//	            final ImageButton clearSearch = (ImageButton) convertView.findViewById(R.id.search_clear);
//	            query.addTextChangedListener(new TextWatcher() {
//	                public void onTextChanged(CharSequence s, int start, int before, int count) {
//	                    getFilter().filter(s);
//	                    if (s.length() > 0) {
//	                        clearSearch.setVisibility(View.VISIBLE);
//	                    } else {
//	                        clearSearch.setVisibility(View.INVISIBLE);
//	                    }
//	                }
//	    
//	                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//	                }
//	    
//	                public void afterTextChanged(Editable s) {
//	                }
//	            });
//	            clearSearch.setOnClickListener(new OnClickListener() {
//	                @Override
//	                public void onClick(View v) {
//	                    query.getText().clear();
//	                }
//	            });
//		    }
			
//			else{
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.row_group, null);
				}
				
				((TextView)convertView.findViewById(R.id.name)).setText(getItem(position).getGroupName());
				
//		    }
			return convertView;
		}

		@Override
		public int getCount() {
			return super.getCount();
		}


	}
}
