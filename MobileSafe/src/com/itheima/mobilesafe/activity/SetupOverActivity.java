package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.SpUtil;

public class SetupOverActivity extends Activity {

	private TextView tv_phone;
	private TextView tv_reset_setup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean setup_over = SpUtil.getBoolean(getApplicationContext(), ConstantValue.SETUP_OVER, false);
        if(setup_over){
        	//密码输入成功,并且四个导航界面设置完成---------->停留在设置完成功能列表界面
             setContentView(R.layout.activity_setup_over);
             
             initUI();
        	
        }else{
        	//密码输入成功,但是四个导航界面没有设置完成---------->跳转到导航界面第一个
        	Intent intent = new Intent(this,Setup1Activity.class);
        	startActivity(intent);
        	
        	finish();
        }
		
	    
	}

	private void initUI() {
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        String phone = SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        
        tv_phone.setText(phone);
        
		tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
		tv_reset_setup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//重新设置进入Setup1   
				Intent intent = new Intent(getApplicationContext(),Setup1Activity.class);
				
				startActivity(intent);
				
				finish();
			}
		});
		
		
	}
}
