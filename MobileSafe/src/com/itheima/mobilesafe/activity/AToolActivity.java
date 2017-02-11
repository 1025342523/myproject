package com.itheima.mobilesafe.activity;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.engine.SmsBackUp;
import com.itheima.mobilesafe.engine.SmsBackUp.CallBack;

public class AToolActivity extends Activity {

	private TextView tv_query_phone_address,tv_sms_backup;
	private TextView tv_commonNumber_query;
	private TextView tv_app_lock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		//电话归属地查询方法
		initPhoneAddress();
		//短信备份方法
		initSmsBackUp();
		//常用号码查询
		initCommonNumberQuery();
		//
		initAppLock();
	}	
		
	private void initAppLock() {
		tv_app_lock = (TextView) findViewById(R.id.tv_app_lock);  
		tv_app_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
			}
		});
	}

	private void initCommonNumberQuery() {
		tv_commonNumber_query = (TextView) findViewById(R.id.tv_commonNumber_query);  
		tv_commonNumber_query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),CommonNumberQueryActivity.class));
			}
		});
	}
	
	private void initSmsBackUp() {
		tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
		tv_sms_backup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSmsBackUp();
			}
		});
	}

	protected void showSmsBackUp() {
		//1.创建一个进度条对话框
		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("短信备份");
		//2.指定进度条的样式为水平
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//3.展示进度条
		progressDialog.show();
		//4.直接调用短信备份方法即可
		new Thread(){
			public void run() {
				String path = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"sms.xml";
				
				SmsBackUp.backup(getApplicationContext(),path,new CallBack(){

					@Override
					public void setMax(int max) {
						progressDialog.setMax(max);
					}

					@Override
					public void setProgress(int index) {
						progressDialog.setProgress(index);
					}});
				
				progressDialog.dismiss();
			};
		}.start();
		
	}

	private void initPhoneAddress() {

		tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
		tv_query_phone_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						QueryAddressActivity.class);

				startActivity(intent);
			}
		});

	}

}
