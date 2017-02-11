package com.itheima.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.util.ToastUtil;

/**
 * @author Administrator 第一个设置界面的activity
 * 
 * 
 */
public class Setup1Activity extends BaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup1);

        ToastUtil.show(getApplicationContext(), "请开启设备管理器");
	}

	@Override
	protected void showPrePage() {

	}

	@Override
	protected void showNextPage() {
		Intent intent = new Intent(getApplicationContext(),
				Setup2Activity.class);

		startActivity(intent);
		// 开启一个activity 关闭上一个activity
		finish();
		// 开启平移动画
		overridePendingTransition(R.anim.net_in_anim, R.anim.net_out_anim);

	}

}
