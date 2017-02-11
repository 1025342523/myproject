package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.ServiceUtil;
import com.itheima.mobilesafe.util.SpUtil;
import com.itheima.mobilsafe.service.LockScreenService;

public class ProcessSettingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		
		initSystemShow();
		initLockScreenClear();
	}
	private void initLockScreenClear() {
		final CheckBox cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);
		//根据锁屏清理服务是否开启，决定单选框是否选中
		boolean isRunning = ServiceUtil.isRunning(getApplicationContext(), "com.itheima.mobilsafe.service.LockScreenService");
		if(isRunning){
			cb_lock_clear.setText("锁屏清理已开启");
		}else{
			cb_lock_clear.setText("锁屏清理已关闭");
		}
		cb_lock_clear.setChecked(isRunning);
		//对选中状态进行监听
		cb_lock_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//isCheck就作为是否选中的状态
				if(isChecked){
					cb_lock_clear.setText("锁屏清理已开启");
					//开启服务
					startService(new Intent(getApplicationContext(),LockScreenService.class));
				}else{
					cb_lock_clear.setText("锁屏清理已关闭");
					//关闭服务
					stopService(new Intent(getApplicationContext(),LockScreenService.class));
					
				}
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, isChecked);
			}
		});
	}
	private void initSystemShow() {
		final CheckBox cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		//对之前存储过的状态进行回显
		boolean showSystem = SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false);
		cb_show_system.setChecked(showSystem);
		if(showSystem){
			cb_show_system.setText("显示系统进程");
		}else{
			cb_show_system.setText("隐藏系统进程");
		}
		
		//队选中状态进行监听
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//isCheck就作为是否选中的状态
				if(isChecked){
					cb_show_system.setText("显示系统进程");
				}else{
					cb_show_system.setText("隐藏系统进程");
				}
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, isChecked);
			}
		});
	}
}
