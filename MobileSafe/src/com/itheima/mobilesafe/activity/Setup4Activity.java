package com.itheima.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.SpUtil;
import com.itheima.mobilesafe.util.ToastUtil;

public class Setup4Activity extends BaseSetupActivity {

	private CheckBox cb_box;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setup4);

		initUI();

	}

	private void initUI() {
		cb_box = (CheckBox) findViewById(R.id.cb_box);
		// 是否选中状态的回显
		boolean open_security = SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_SECURITY, false);
		// 根据选中状态，修改CheckBox后续的文字显示
		cb_box.setChecked(open_security);

		if (open_security) {

			cb_box.setText("安全设置已开启");
		} else {
			cb_box.setText("安全设置已关闭");

		}
		// 点击过程中，监听选中状态发生改变过程
		cb_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// isCheck点击后的状态,存储点击后的状态

				SpUtil.putBoolean(getApplicationContext(),
						ConstantValue.OPEN_SECURITY, isChecked);
				// 根据开启关闭状态，去修改显示的文字
				if (isChecked) {
					cb_box.setText("安全设置已开启");

				} else {
					cb_box.setText("安全设置已关闭");

				}
			}
		});

	}

	@Override
	protected void showPrePage() {
		Intent intent = new Intent(getApplicationContext(),
				Setup3Activity.class);

		startActivity(intent);
		// 开启一个activity 关闭上一个activity
		finish();

		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);

		
	}

	@Override
	protected void showNextPage() {
		
		boolean open_security = SpUtil.getBoolean(getApplicationContext(),
				ConstantValue.OPEN_SECURITY, false);

		if (open_security) {

			Intent intent = new Intent(getApplicationContext(),
					SetupOverActivity.class);

			startActivity(intent);
			// 开启一个activity 关闭上一个activity
			finish();

			SpUtil.putBoolean(getApplicationContext(),
					ConstantValue.SETUP_OVER, true);

			overridePendingTransition(R.anim.net_in_anim, R.anim.net_out_anim);

		} else {

			ToastUtil.show(getApplicationContext(), "请开启防盗保护");
		}


	}

}
