package com.itheima.mobilsafe.receiver;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.SpUtil;
import com.itheima.mobilsafe.service.LocationService;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

	private ComponentName mComponentName;
	private DevicePolicyManager mDMP;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 判断是否开启了防盗保护
		boolean open_security = SpUtil.getBoolean(context,
				ConstantValue.OPEN_SECURITY, false);
		MyActivity myActivity = new MyActivity();
		
		if (open_security) {
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
				// 判断是否包含播放音乐的关键字
				if (body.contains("#*alarm*#")) {
					// 播放音乐 准备音乐 MediaPlay
					MediaPlayer player = MediaPlayer
							.create(context, R.raw.ylzs);
					player.setLooping(true);
					player.start();
				}

				if (body.contains("#*location*#")) {
					// 开启获取位置的服务
					Intent intent2 = new Intent(context, LocationService.class);

					context.startService(intent2);
				}

				if (body.contains("#*wipedata*#")) {

					myActivity.wipdata();
					
				}
				if (body.contains("#*lockscreen*#")) {
					myActivity.lockscreen();
				}

			}
		}
	}
	private class MyActivity extends Activity {
		//
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			mDMP = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

			mComponentName = new ComponentName(this, DeviceAdmin1.class);

			// 开启设备管理器的activity
			Intent intent1 = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);

			intent1.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
					mComponentName);

			intent1.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "设备管理器");

			startActivity(intent1);
		}

		public void wipdata() {

			if (mDMP.isAdminActive(mComponentName)) {
				// 清除数据
				mDMP.wipeData(0);// 清除手机数据

				mDMP.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);// 清除SD卡数据

			} else {

				Toast.makeText(this, "请先激活", 0).show();

			}

		}
		public void lockscreen(){
			// 是否激活的判断
			if (mDMP.isAdminActive(mComponentName)) {
				// 激活锁屏
				mDMP.lockNow();
				// 锁屏的同时去设置密码
//				mDMP.resetPassword("pwd123", 0);
				mDMP.resetPassword("", 0);
			} else {

				Toast.makeText(this, "请先激活", 0).show();

			}
		}
	}

}
