package com.hanyu.desheng.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class MyWebView extends WebView {

	private float x,y;


	public MyWebView(Context context) {
		super(context);
	}
	
	
	public MyWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent event) {
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			x = event.getX();
//			y = event.getY();
//			break;
//		case MotionEvent.ACTION_MOVE:
//			float dx = Math.abs(event.getX() - x);
//			float dy = Math.abs(event.getY() -y );
//			x = event.getX();
//			y = event.getY();
//			if (dx > dy) {
//				return true;
//			}
//			break;
//		case MotionEvent.ACTION_UP:
//			
//			break;
//
//		}
//		return super.dispatchTouchEvent(event);
//	}

}
