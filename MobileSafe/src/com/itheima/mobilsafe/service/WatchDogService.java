package com.itheima.mobilsafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.itheima.mobilesafe.activity.EnterPsdActivity;
import com.itheima.mobilesafe.db.dao.AppLockDao;

public class WatchDogService extends Service {
	
	
	private boolean isWatch;
	private AppLockDao mDao;
	private List<String> mPackageList;
	private InnerReceiver mInnerReceiver;
	private String mSkipPackageName;
	private MyContentObserver mContentObserver;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mDao = AppLockDao.getInstance(getApplicationContext());
		
		//维护一个看门狗的死循环,让其时刻检测现在开启的应用,是否为程序锁中要去拦截的应用
		isWatch = true;
		watch();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.SKIP");
		
		mInnerReceiver = new InnerReceiver();
		
		registerReceiver(mInnerReceiver, intentFilter);
		
		mContentObserver = new MyContentObserver(new Handler());
		
		//注册一个内容观察者，观察数据库的变化，一旦数据有删除或者添加，则需要让mPackageList重新获取一次数据
		getContentResolver().registerContentObserver(Uri.parse("content://applock/change"),
				true, mContentObserver);
	}
	
	private class MyContentObserver extends ContentObserver{

		public MyContentObserver(Handler handler) {
			super(handler);
		}
		//一旦数据库发生改变的时候调用的方法,重新获取包名所在集合的数据
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			new Thread(){
				public void run() {
					
					mPackageList = mDao.findAll();
					
				};
				
			}.start();
			
			
		}
		
	}
	
	private class InnerReceiver extends BroadcastReceiver{

		

		@Override
		public void onReceive(Context context, Intent intent) {
			
			mSkipPackageName = intent.getStringExtra("packagename");
		}
		
		
	}
	
	private void watch() {
		//子线程中,开启一个可控的死循环
		new Thread(){
			@SuppressWarnings("deprecation")
			public void run() {
				mPackageList = mDao.findAll();
				while(isWatch){
					//监测正在开启的应用,任务栈
					//获取activity管理者对象
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					//获取正在运行任务站方法
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					//获取栈顶的activity,然后获取此activity所在应用的包名
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					
					String packageName = runningTaskInfo.topActivity.getPackageName();
					//拿次包名在已加锁的包名集合中去做比对，如果包含次包名，则需要弹出拦截界面
					if(mPackageList.contains(packageName)){
						//如果已解锁则进入到应用程序
						if(!packageName.equals(mSkipPackageName)){
							//弹出拦截界面
							Intent intent = new Intent(getApplicationContext(),EnterPsdActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packagename", packageName);
							startActivity(intent);
							
						}
					
					}
					//睡眠一下，时间片轮转
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
			
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//停止看门狗循环
		isWatch = false;
		//注销广播接受者
		if(mInnerReceiver!=null){
			
			unregisterReceiver(mInnerReceiver);
		}
		//注销内容观察者
		if(mContentObserver!=null){
			
			getContentResolver().unregisterContentObserver(mContentObserver);
		}
		
		
	}
}
