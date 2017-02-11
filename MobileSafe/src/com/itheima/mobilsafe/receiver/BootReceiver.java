package com.itheima.mobilsafe.receiver;

import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.SpUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private static final String tag = "BootReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.i(tag, "重新启动成功");
		//获取开机后手机的sim卡的序列号
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		
		String resetnumber = tm.getSimSerialNumber()+"xxx";
		//sp中存储的序列卡号
		String sim_number = SpUtil.getString(context, ConstantValue.SIM_NUMBER, "");
		if(!resetnumber.equals(sim_number)){
			//发送短信给报警号码
			SmsManager sms = SmsManager.getDefault();
			
			sms.sendTextMessage("5558", null, "sim change !!!", null, null);
		}
		
		
	}

}
