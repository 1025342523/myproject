package com.itheima.mobilsafe.service;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;

public class BlackNumberService extends Service {

	private InnerSmsReceiver mInnerSmsReceiver;
	private BlackNumberDao mDao;
	private TelephonyManager mPhoneManager;
	private MyPhoneStateListener mStateListener;
	private MyContentObserver mObserver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mDao = BlackNumberDao.getInstance(getApplicationContext());
		
		IntentFilter filter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		// 设置级别
		filter.setPriority(1000);

		mInnerSmsReceiver = new InnerSmsReceiver();
		// 注册广播接受者
		registerReceiver(mInnerSmsReceiver, filter);

		// 电话状态的监听
		// 电话管理者对象
		mPhoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		mStateListener = new MyPhoneStateListener();
		// 监听电话状态
		mPhoneManager.listen(mStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

	}
	private class MyPhoneStateListener extends PhoneStateListener {
		// 重写，电话状态发生改变会触发的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// 空闲状态

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 摘机状态

				break;
			case TelephonyManager.CALL_STATE_RINGING:// 响铃状态
				
				endCall(incomingNumber);
				
				break;

			}

			super.onCallStateChanged(state, incomingNumber);
		}

	}
	
	
	private class InnerSmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 获取短信内容，获取发送短信地址,如果此号码在黑名单中，并且拦截模式为1或者3，拦截短信
			// 获取短信内容
			Object[] object = (Object[]) intent.getExtras().get("pdus");
			// 循环遍历短信过程
			for (Object obj : object) {
				// 获取短信对象
				SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
				// 获取短信对象的基本信息 电话号码
				String address = message.getOriginatingAddress();
				// 短信内容
				String body = message.getMessageBody();
				
				int mode = mDao.getMode(address);
				
				System.out.println(mode);
				
				if (mode == 1 || mode == 3) {
					// 拦截短信
					abortBroadcast();
				}

			}

		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//注销广播
		if (mInnerSmsReceiver != null) {
			unregisterReceiver(mInnerSmsReceiver);

		}
		//注销内容观察者
		if(mObserver!=null){
			getContentResolver().unregisterContentObserver(mObserver);
			
		}
		//取消对电话的监听
		if(mStateListener!=null){
			mPhoneManager.listen(mStateListener, PhoneStateListener.LISTEN_NONE);
		}

	}

	public void endCall(String phone) {
		int mode = mDao.getMode(phone);
		
		System.out.println(mode);
		
		if(mode == 2||mode == 3){
//		ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE))
			//1.获取ServiceManager字节码文件   
			try {
				Class<?> clazz = Class.forName("android.os.ServiceManager");
				//2.获取方法
				Method method = clazz.getMethod("getService", String.class);
				//3.反射调用此方法
				IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
				//4.调用获取aidl文件对象方法
				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				//5.调用aidl中隐藏的endCall方法
				iTelephony.endCall();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mObserver = new MyContentObserver(new Handler(),phone);
			//在内容解析者上，去注册内容观察者，通过内容观察者，观察数据库的变化
			getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, mObserver);
			
			
			
		}
	}
	private class MyContentObserver extends ContentObserver{
		private String phone;
		public MyContentObserver(Handler handler,String phone) {
			super(handler);
			this.phone = phone;
		}
		//数据库中指定calls表发生改变的时候回去调用的方法
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			//插入一条数据之后在进行删除
			getContentResolver().delete(Uri.parse("content://call_log/calls"), "number = ?", new String[]{phone});

		}
	}
	
}
