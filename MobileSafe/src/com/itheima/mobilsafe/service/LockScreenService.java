package com.itheima.mobilsafe.service;

import com.itheima.mobilesafe.engine.ProcessInfoProvider;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockScreenService extends Service {

	private IntentFilter intentFilter;
	private InnerReceiver innerReceiver;
	@Override
	public void onCreate() {
		intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		
		innerReceiver = new InnerReceiver();
		//注册广播
		registerReceiver(innerReceiver, intentFilter);
		
		super.onCreate();
	}
	
	private class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//清理手机正在运行的进程
			ProcessInfoProvider.killAll(context);
		}
		
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(innerReceiver!=null){
			unregisterReceiver(innerReceiver);
		}
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
