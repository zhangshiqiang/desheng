package com.hanyu.desheng.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.hanyu.desheng.R;
import com.hanyu.desheng.ui.wheelView.AbstractWheelTextAdapter;
import com.hanyu.desheng.ui.wheelView.OnWheelChangedListener;
import com.hanyu.desheng.ui.wheelView.WheelView;
/**
 * 选择目前年纪dialog
 * @author wangbin
 *
 */
public class ShowAgeDialog implements OnWheelChangedListener{
	@SuppressWarnings("unused")
	private Display display;
    private Context context;
    private Dialog dialog;
    private RelativeLayout tvSure;
    private WheelView wvYear,wvMonth,wvDay;
    
    private String[] years;
	private String[] months = new String[12];
	private String[] tinyDays = new String[28];
	private String[] smallDays = new String[29];
	private String[] normalDays = new String[30];
	private String[] bigDays = new String[31];
	
	private String year,month,day;
	private int wDay;
	public ShowAgeDialog(Context context){
		this.context=context;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}
	
	
	public ShowAgeDialog builder(){
		View view=View.inflate(context, R.layout.age_dialog_view,null);
		tvSure=(RelativeLayout) view.findViewById(R.id.tvSure);
		wvYear=(WheelView) view.findViewById(R.id.wvYear);
		wvMonth=(WheelView) view.findViewById(R.id.wvMonth);
		wvDay=(WheelView) view.findViewById(R.id.wvDay);
		tvSure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int age=getTd()-Integer.valueOf(year.substring(0,4));
				if(onSaveDay!=null)
				onSaveDay.setDay(year.substring(0,4)+"-"+month.substring(0,2)+"-"+day.substring(0,2),age);
				dialog.cancel();
			}
			
		});
		wvYear.addChangingListener(this);
		wvMonth.addChangingListener(this);
		wvDay.addChangingListener(this);
		setData();
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		dialog.setContentView(view);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
		dialogWindow.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.x = 0;
		lp.y = 0;
		dialogWindow.setAttributes(lp);
		return this;
	}
	
	public void show(){
		dialog.show();
	}
	
	private OnsaveDayListener onSaveDay;
	public interface OnsaveDayListener{
		public void setDay(String saveDay,int age);
	}

	public OnsaveDayListener getOnSaveDay() {
		return onSaveDay;
	}

	public void setOnSaveDay(OnsaveDayListener onSaveDay) {
		this.onSaveDay = onSaveDay;
	}

	int currentMonth = 1;
	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		String trim = null;
		switch (wheel.getId()) {
		case R.id.wvYear:
			trim = splitDateString(years[wvYear.getCurrentItem()]);
			year=years[wvYear.getCurrentItem()];
//			tvYear.setText(trim);
			if (isLeapYear(trim)) {
				if (currentMonth == 2) {
					wvDay.setViewAdapter(smallDaysAdapter);
				} else if (isBigMonth(currentMonth)) {
					wvDay.setViewAdapter(bigDaysAdapter);
				} else {
					wvDay.setViewAdapter(normalDaysAdapter);
				}
			} else if (currentMonth == 2) {
				wvDay.setViewAdapter(tinyDaysAdapter);
			} else if (isBigMonth(currentMonth)) {
				wvDay.setViewAdapter(bigDaysAdapter);
			} else {
				wvDay.setViewAdapter(smallDaysAdapter);
			}
			break;
		case R.id.wvMonth:
			trim =splitDateString(months[wvMonth.getCurrentItem()])
					.trim();
			month=months[wvMonth.getCurrentItem()];
			currentMonth = Integer.parseInt(trim);
//			tvMonth.setText(trim);
			switch (currentMonth) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				wvDay.setViewAdapter(bigDaysAdapter);
				wDay=0;
				break;
			case 2:
				String yearString = splitDateString(
						years[wvYear.getCurrentItem()]).trim();
				
				if (isLeapYear(yearString)) {
					wvDay.setViewAdapter(smallDaysAdapter);
					wDay=1;
				} else {
					wvDay.setViewAdapter(tinyDaysAdapter);
					wDay=2;
				}
				break;
			default:
				wvDay.setViewAdapter(normalDaysAdapter);
				wDay=3;
				break;
			}
			break;
		case R.id.wvDay:
			switch(wDay){
			case 0:
				day=bigDays[wvDay.getCurrentItem()];
				break;
			case 1:
				day=smallDays[wvDay.getCurrentItem()];
				break;
			case 2:
				day=tinyDays[wvDay.getCurrentItem()];
				break;
			case 3:
				day=normalDays[wvDay.getCurrentItem()];
				break;
			}
//			tvDay.setText(splitDateString(wvDay.getCurrentIt))
//					.trim());
			break;
		}
	
	}
	
	private class AgeAdapter extends AbstractWheelTextAdapter{
		private String ages[];
		protected AgeAdapter(Context context,String ages[]) {
			super(context, R.layout.wheelcity_age_layout, NO_RESOURCE);
			setItemTextResource(R.id.tvAge);
			this.ages=ages;
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return ages.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
			return ages[index];
		}
		
	}
	
	private AgeAdapter yearsAdapter;
	private AgeAdapter monthsAdapter;
	private AgeAdapter tinyDaysAdapter;
	private AgeAdapter smallDaysAdapter;
	private AgeAdapter normalDaysAdapter;
	private AgeAdapter bigDaysAdapter;
	
	private void setData() {
		years=new String[getTd()-1940];
		for (int i = 0; i < years.length; i++) {
			years[i] = 2000 + i + " 年";
		}
		for (int i = 0; i < months.length; i++) {
			if (i < 9) {
				months[i] = "0" + (1 + i) + " 月";
			} else {
				months[i] = (1 + i) + " 月";
			}
		}
		for (int i = 0; i < tinyDays.length; i++) {
			if (i < 9) {
				tinyDays[i] = "0" + (1 + i) + " 日";
			} else {
				tinyDays[i] = (1 + i) + " 日";
			}
		}
		for (int i = 0; i < smallDays.length; i++) {
			if (i < 9) {
				smallDays[i] = "0" + (1 + i) + " 日";
			} else {
				smallDays[i] = (1 + i) + " 日";
			}
		}
		for (int i = 0; i < normalDays.length; i++) {
			if (i < 9) {
				normalDays[i] = "0" + (1 + i) + " 日";
			} else {
				normalDays[i] = (1 + i) + " 日";
			}
		}
		for (int i = 0; i < bigDays.length; i++) {
			if (i < 9) {
				bigDays[i] = "0" + (1 + i) + " 日";
			} else {
				bigDays[i] = (1 + i) + " 日";
			}
		}
		yearsAdapter = new AgeAdapter(context,years);
		monthsAdapter = new AgeAdapter(context,months);
		tinyDaysAdapter = new AgeAdapter(context,tinyDays);
		smallDaysAdapter = new AgeAdapter(context,smallDays);
		normalDaysAdapter = new AgeAdapter(context,normalDays);
		bigDaysAdapter = new AgeAdapter(context,bigDays);
		wvYear.setViewAdapter(yearsAdapter);
		wvYear.setCurrentItem(getTodayYear());
		day=years[getTodayYear()];
		wvYear.setCyclic(false);
		wvMonth.setViewAdapter(monthsAdapter);
		wvMonth.setCurrentItem(getTodayMonth());
		month=months[getTodayMonth()];
		wvMonth.setCyclic(true);
		if (isBigMonth(getTodayMonth() + 1)) {
			wvDay.setViewAdapter(bigDaysAdapter);
		} else if (getTodayMonth() == 1
				&& isLeapYear(years[wvYear.getCurrentItem()].subSequence(0, 4)
						.toString().trim())) {
			wvDay.setViewAdapter(smallDaysAdapter);
		} else if (getTodayMonth() == 1) {
			wvDay.setViewAdapter(tinyDaysAdapter);
		} else {
			wvDay.setViewAdapter(normalDaysAdapter);
		}
		wvDay.setCurrentItem(getTodayDay());
		
		if (isBigMonth(getTodayMonth() + 1)) {
			day=bigDays[wvDay.getCurrentItem()];
		} else if (getTodayMonth() == 1
				&& isLeapYear(years[wvYear.getCurrentItem()].subSequence(0, 4)
						.toString().trim())) {
			day=smallDays[wvDay.getCurrentItem()];
		} else if (getTodayMonth() == 1) {
			day=tinyDays[wvDay.getCurrentItem()];
		} else {
			day=normalDays[wvDay.getCurrentItem()];
		}
		wvDay.setCyclic(false);
	}
	
	
	/**
	 * 获取当前日期的天数的日子
	 * @return
	 */
	private int getTodayDay() {
		// 2015年12月01日
		int position = 0;
		String today = getToday();
		String day = today.substring(8, 10);
		day = day + " 日";
		for (int i = 0; i < bigDays.length; i++) {
			if (day.equals(bigDays[i])) {
				position = i;
				break;
			}
		}
		return position;
	}
	/**
	 * 获取当前日期的月数的位置
	 * @return
	 */
	private int getTodayMonth() {
		// 2015年12月01日
		int position = 0;
		String today = getToday();
		String month = today.substring(5, 7);
		month = month + " 月";
		for (int i = 0; i < months.length; i++) {
			if (month.equals(months[i])) {
				position = i;
				break;
			}
		}
		return position;
	}

	/**
	 * 获取当天的年份
	 * 
	 * @return
	 */
	private int getTodayYear() {
		int position = 0;
		String today = getToday();
		String year = today.substring(0, 4);
		year = year + " 年";
		for (int i = 0; i < years.length; i++) {
			if (year.equals(years[i])) {
				position = i;
				break;
			}
		}
		return position;
	}

	/**
	 * 设置当前显示的年份
	 * 
	 * @param year
	 */
	public void setCurrentYear(String year) {
		boolean overYear = true;
		year = year + " 年";
		for (int i = 0; i < years.length; i++) {
			if (year.equals(years[i])) {
				wvYear.setCurrentItem(i);
				overYear = false;
				break;
			}
		}
		if (overYear) {
		}
	}

	/**
	 * 设置当前显示的月份
	 * 
	 * @param month
	 */
	public void setCurrentMonth(String month) {
		month = month + " 月";
		for (int i = 0; i < months.length; i++) {
			if (month.equals(months[i])) {
				wvMonth.setCurrentItem(i);
				break;
			}
		}
	}

	/**
	 * 设置当前显示的日期号
	 * 
	 * @param day
	 *            14
	 */
	public void setCurrentDay(String day) {
		day = day + " 日";
		for (int i = 0; i < smallDays.length; i++) {
			if (day.equals(smallDays[i])) {
				wvDay.setCurrentItem(i);
				break;
			}
		}
	}

	/**
	 * 获取选择的日期的值
	 * 
	 * @return
	 */
//	public String getSelectedDate() {
//		return tvYear.getText().toString().trim() + "-"
//				+ tvMonth.getText().toString().trim() + "-"
//				+ tvDay.getText().toString().trim();
//	}


//	/**
//	 * 设置日期选择器的日期转轮是否可见
//	 * 
//	 * @param visibility
//	 */
//	public void setDateSelectorVisiblility(int visibility) {
//		llWheelViews.setVisibility(visibility);
//	}
//
//	public int getDateSelectorVisibility() {
//		return llWheelViews.getVisibility();
//	}

	/**
	 * 判断是否是闰年
	 * 
	 * @param year
	 * @return
	 */
	private boolean isLeapYear(String year) {
		int temp = Integer.parseInt(year);
		return temp % 4 == 0 && (temp % 100 != 0 || temp % 400 == 0) ? true
				: false;
	}

	/**
	 * 判断是否是大月
	 * 
	 * @param month
	 * @return
	 */
	private boolean isBigMonth(int month) {
		boolean isBigMonth = false;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			isBigMonth = true;
			break;

		default:
			isBigMonth = false;
			break;
		}
		return isBigMonth;
	}

	


	/**
	 * 获取今天的日期
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	private String getToday() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;
	}
	
	private int getTd(){
		return Integer.valueOf(getToday().substring(0,4));
	}
	
	private  String splitDateString(String date){
		//1942年
		return date.split(" ")[0];
	}
}
