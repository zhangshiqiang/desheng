package com.hanyu.desheng.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/** * 自定义listView 在出库中应用 * */
public class MyListView extends ListView {
	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 根据模式计算每个child的高度和宽度
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
