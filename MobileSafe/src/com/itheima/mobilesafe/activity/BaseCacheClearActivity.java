package com.itheima.mobilesafe.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost.TabSpec;

import com.itheima.mobilesafe.R;

public class BaseCacheClearActivity extends TabActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_clear);
		
//		View view = View.inflate(getApplicationContext(), resource, root);
//		TabSpec tab1 = getTabHost().newTabSpec("clear_cache").setIndicator(view);
		//1.生成选项卡1
		TabSpec tab1 = getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
		//2.生成选项卡2
		TabSpec tab2 = getTabHost().newTabSpec("sd_cache_clear").setIndicator("sd卡清理");
		
		tab1.setContent(new Intent(this,CacheClearActivity.class));
		tab2.setContent(new Intent(this,SDCacheClearActivity.class));
		
		//将次两个选项卡维护到host(宿主)中去
		getTabHost().addTab(tab1);
		getTabHost().addTab(tab2);
	}
}
