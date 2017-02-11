package com.itheima.mobilsafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.engine.ProcessInfoProvider;
import com.itheima.mobilsafe.receiver.MyAppWidgetProviderReceiver;

public class UpdateWidgetService extends Service {

	protected static final String tag = "UpdateWidgetService";
	private Timer mTimer;
	private InnerReceiver mInnerReceiver;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		//管理进程总数和可用内存更新(定时器)
		startTime();
		//注册开锁，解锁广播接受者
		IntentFilter intentFilter = new IntentFilter();
		//开锁action
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		//解锁action
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		mInnerReceiver = new InnerReceiver();
		
		registerReceiver(mInnerReceiver, intentFilter);
		
		super.onCreate();
	}
	
	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
				//开启定时更新任务
				startTime();
				
			}else{
				//关闭定时更新任务
				cancelTimerTask();
			}
		}
		
	}
	
	private void startTime() {
		mTimer = new Timer();
		
		mTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				//UI定时刷新
				updateAppWidget();
//				Log.i(tag, "五秒一次的定时任务现在正在运行..........");
			}
			//延时多少秒    间隔5秒执行一次
		}, 0, 5000);
	}
	public void cancelTimerTask() {
		//mTimer中cancel方法取消定时任务方法
		if(mTimer!=null){
			mTimer.cancel();
			mTimer = null;
		}
		
	}
	protected void updateAppWidget() {
		//获取AppWidget对象
		AppWidgetManager aWM = AppWidgetManager.getInstance(this);
		//获取窗体小部件布局转换成的view对象
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
		//给窗体小部件内部的控件赋值
		remoteViews.setTextViewText(R.id.tv_process_count, "进程总数:"+ProcessInfoProvider.getProcessCount(getApplicationContext()));
		//显示可用内存大小
		String strAvailSpace = Formatter.formatFileSize(getApplicationContext(), ProcessInfoProvider.getAvailSpace(getApplicationContext()));
		remoteViews.setTextViewText(R.id.tv_process_memory, "可用内存:"+strAvailSpace);
		
		//点击窗体小部件，进入应用
		Intent intent = new Intent("android.intent.action.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);
		
		//通过延期意图发送广播，在广播接受者中杀死进程
		
		Intent broadCastIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
		
		PendingIntent broadCastPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, broadCastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		remoteViews.setOnClickPendingIntent(R.id.btn_clear, broadCastPendingIntent);
		
		ComponentName componentName = new ComponentName(getApplicationContext(), MyAppWidgetProviderReceiver.class);
		aWM.updateAppWidget(componentName, remoteViews);
		
	}
	
	@Override
	public void onDestroy() {
		if(mInnerReceiver!=null){
			unregisterReceiver(mInnerReceiver);
		}
		//调用onDestroy即关闭服务,关闭服务的方法在移除最后一个窗体小部件的时候调用,定时任务也必要维护
		cancelTimerTask();
		super.onDestroy();
	}
	
}
