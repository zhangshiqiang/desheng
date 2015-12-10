package com.hanyu.desheng.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.hanyu.desheng.activity.LoginActivity;

public class ShowDialogUtil {
	/**
	 * 是否登录
	 */
	public static void showIsLoginDialog(final Context context) {
		new AlertDialog.Builder(context).setTitle("提示")
				.setMessage("您还没有登录，是否现在登录")
				.setPositiveButton("确定", new Dialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(context, LoginActivity.class);
						context.startActivity(intent);
					}
				}).setNegativeButton("取消", null).show();

	}
}
