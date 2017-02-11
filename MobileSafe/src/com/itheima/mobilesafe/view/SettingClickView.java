package com.itheima.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

public class SettingClickView extends RelativeLayout {

	private TextView tv_click_des;
	private TextView tv_click_title;

	public SettingClickView(Context context) {
		this(context, null);
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// xml ---> view 将设置界面的一个条目转换成view对象
		View.inflate(context, R.layout.setting_click_view, this);

		tv_click_title = (TextView) findViewById(R.id.tv_click_title);
		tv_click_des = (TextView) findViewById(R.id.tv_click_des);
		
	}

	/**
	 * @param title   设置标题内容
	 */
	public void setTitle(String title){
		
		tv_click_title.setText(title);
	}
	/**
	 * @param des     设置描述内容
	 */
	public void setDes(String des){
		
		tv_click_des.setText(des);
	}

}
