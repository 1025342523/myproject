package com.itheima.mobilesafe.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.ServiceUtil;
import com.itheima.mobilesafe.util.SpUtil;
import com.itheima.mobilesafe.view.SettingClickView;
import com.itheima.mobilesafe.view.SettingItemView;
import com.itheima.mobilsafe.service.AddressService;
import com.itheima.mobilsafe.service.BlackNumberService;
import com.itheima.mobilsafe.service.WatchDogService;

public class SettingActivity extends Activity {
     private SettingClickView scv_toast_style;
	private String[] mToastStyleDes;
	private int mToastStyle;
	private SettingClickView scv_location;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        
        initUpdate();
        initAddress();
        initToastStyle();
        initLocation();
        initBlackNumber();
        initAppLock();
     }

	/**
	 * 初始化程序锁的方法
	 */
	private void initAppLock() {
		final SettingItemView siv_app_lock = (SettingItemView) findViewById(R.id.siv_app_lock);
		boolean isRunning = ServiceUtil.isRunning(getApplicationContext(), "com.itheima.mobilsafe.service.WatchDogService");
		siv_app_lock.setCheck(isRunning);
		
		siv_app_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isCheck = siv_app_lock.isCheck();
				siv_app_lock.setCheck(!isCheck);
				if(!isCheck){
					//开启服务
					startService(new Intent(getApplicationContext(),WatchDogService.class));
				}else{
					//关闭服务
					stopService(new Intent(getApplicationContext(),WatchDogService.class));
				}
			}
		});
	}

	/**
	 * 拦截黑名单短信电话
	 * 
	 */
	private void initBlackNumber() {
		final SettingItemView siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);
		boolean isRunning = ServiceUtil.isRunning(getApplicationContext(), "com.itheima.mobilsafe.service.BlackNumberService");
		siv_blacknumber.setCheck(isRunning);
		
		siv_blacknumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean isCheck = siv_blacknumber.isCheck();
				siv_blacknumber.setCheck(!isCheck);
				if(!isCheck){
					//开启服务
					startService(new Intent(getApplicationContext(),BlackNumberService.class));
				}else{
					//关闭服务
					stopService(new Intent(getApplicationContext(),BlackNumberService.class));
				}
				
			}
		});
	}

	/**
	 * 双击居中view所在屏幕位置的处理方法
	 */
	private void initLocation() {
		scv_location = (SettingClickView) findViewById(R.id.scv_location);
	    
		scv_location.setTitle("归属地提示框的位置");
		
		scv_location.setDes("设置归属地提示框的位置");
		scv_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),ToastLocationActivity.class));
			}
		});
		
		
	}

	private void initToastStyle() {
		scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
		
		scv_toast_style.setTitle("设置归属地显示风格");
		mToastStyleDes = new String[]{"透明","橙色","蓝色","灰色","绿色"};
		mToastStyle = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
	     
		scv_toast_style.setDes(mToastStyleDes[mToastStyle]);
		
		//监听点击事件，弹出对话框
		scv_toast_style.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//显示吐司样式的对话框
				showToastStyleDialog();
			}
		});
		
		
	}

	/**
	 * 创建选中显示样式的对话框
	 */
	protected void showToastStyleDialog() {
		Builder builder = new AlertDialog.Builder(this);
	    builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("请选择归属地样式");
		//选择单个条目的时间监听(1:string类型的数组描述颜色文字数组,2:弹出对话框的时候的选中条目索引值,3:点击某个条目后触发的事件(1,记录选中的索引值 2.关闭对话框 3.显示选中色值文字))
		builder.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {// 选中的索引值
				//1,记录选中的索引值 2.关闭对话框 3.显示选中色值文字
				SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
				
				dialog.dismiss();
				
				scv_toast_style.setDes(mToastStyleDes[which]);
			}
		});
		//消极按钮
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.show();
	}

	/**
	 * 是否显示电话号码归属地的方法
	 * 
	 * 
	 */
	private void initAddress() {
		final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);
		boolean running = ServiceUtil.isRunning(getApplicationContext(), "com.itheima.mobilsafe.service.AddressService");
		
		siv_address.setCheck(running);
		
		siv_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//返回点击前的状态
				boolean isCheck = siv_address.isCheck();
			    siv_address.setCheck(!isCheck);  
			    if(!isCheck){
			    	//开启服务管理土司
			    	Intent intent = new Intent(getApplicationContext(),AddressService.class);
			    	startService(intent);
			    }else{
			    	//关闭服务，不需要显示土司
			    	startService(new Intent(getApplicationContext(),AddressService.class));
			    }
			}
		});
		
		
	}

	private void initUpdate() {
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		
		//获取已有的状态用作显示
		boolean open_update = SpUtil.getBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, false);
		//是否选中根据上一次的结果去决定
		siv_update.setCheck(open_update);
		
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//如果之前是选中的，点击过后，变成未选中
				//如果之前是未选中的，点击过后，变成选中
				//获取之前的选中状态
				boolean isCheck = siv_update.isCheck();
				//将原有状态取反
				siv_update.setCheck(!isCheck);
				//将取反后的状态存储到相应的sp中
				SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !isCheck);
			}
		});
	}
}
