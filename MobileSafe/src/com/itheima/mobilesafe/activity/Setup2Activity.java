package com.itheima.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.SpUtil;
import com.itheima.mobilesafe.util.ToastUtil;
import com.itheima.mobilesafe.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

	private SettingItemView siv_sim_bound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup2);

		initUI();
	}

	private void initUI() {

		siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
		// 1.回显(读取已有的绑定状态，用作显示，sp中是否存储了sim的序列号)
		String sim_number = SpUtil.getString(getApplicationContext(),
				ConstantValue.SIM_NUMBER, "");
		// 判断序列卡号是否为空
		if (TextUtils.isEmpty(sim_number)) {
			siv_sim_bound.setCheck(false);

		} else {

			siv_sim_bound.setCheck(true);
		}
		siv_sim_bound.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 获取原有的状态
				boolean isCheck = siv_sim_bound.isCheck();
				// 将原有状态取反
				siv_sim_bound.setCheck(!isCheck);
				// 设置给当前条目
				if (!isCheck) {
					// 存储序列号 获取sim卡的序列号 TelephoneManager
					TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					// 获取SIM卡的序列号
					String number = manager.getSimSerialNumber();
                    //存储SIM的序列号
					SpUtil.putString(getApplicationContext(),
							ConstantValue.SIM_NUMBER, number);
				} else {
					// 将存储序列卡号的节点，从sp中删除
                     SpUtil.remover(getApplicationContext(),
 							ConstantValue.SIM_NUMBER);
				}

			}
		});

	}

	@Override
	protected void showPrePage() {
		Intent intent = new Intent(getApplicationContext(),
				Setup1Activity.class);

		startActivity(intent);
		// 开启一个activity 关闭上一个activity
		finish();

		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	
	}

	@Override
	protected void showNextPage() {

		String sim_number = SpUtil.getString(getApplicationContext(), ConstantValue.SIM_NUMBER, "");
		
		if(!TextUtils.isEmpty(sim_number)){
			
			Intent intent = new Intent(getApplicationContext(),
					Setup3Activity.class);

			startActivity(intent);
			// 开启一个activity 关闭上一个activity
			finish();
			
			overridePendingTransition(R.anim.net_in_anim, R.anim.net_out_anim);
		}else{
			ToastUtil.show(getApplicationContext(), "请绑定sim卡");
			
		}
		
	}

}
