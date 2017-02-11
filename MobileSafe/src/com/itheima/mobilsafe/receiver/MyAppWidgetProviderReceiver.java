package com.itheima.mobilsafe.receiver;

import com.itheima.mobilsafe.service.UpdateWidgetService;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MyAppWidgetProviderReceiver extends AppWidgetProvider {
	
	private static final String tag = "MyAppWidgetProvider";
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		Log.i(tag, "onReceive.........");
	}
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		//创建第一个窗体小部件调用的方法
		Log.i(tag, "onEnabled 创建第一个窗体小部件调用的方法");
		//开启服务
		context.startService(new Intent(context,UpdateWidgetService.class));
	}
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		
		Log.i(tag, "onUpdate 创建多一个窗体小部件调用的方法");
		//开启服务
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	@SuppressLint("NewApi") 
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		//当窗体小部件宽高发生改变的时候调用的方法,创建小部件时也调用此方法
		Log.i(tag, "onAppWidgetOptionsChanged 创建多一个窗体小部件调用的方法");
		//开启服务
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
	}
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.i(tag, "onDeleted 删除一个窗体小部件调用的方法");
	}
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.i(tag, "onDisabled 删除最后一个窗体小部件调用的方法");
	}
	
	
}
