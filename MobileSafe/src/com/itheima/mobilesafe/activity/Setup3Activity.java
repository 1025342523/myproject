package com.itheima.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.SpUtil;
import com.itheima.mobilesafe.util.ToastUtil;

public class Setup3Activity extends BaseSetupActivity {

	private EditText et_phone_number;
	private Button btn_select_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);

		initUI();

	}

	private void initUI() {

		// 显示电话号码的输入框
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		// 获取联系人电话号码回显过程
		String phone = SpUtil.getString(getApplicationContext(),
				ConstantValue.CONTACT_PHONE, "");

		et_phone_number.setText(phone);

		btn_select_number = (Button) findViewById(R.id.btn_select_number);
		// 点击按钮选择联系人
		btn_select_number.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						ContactListActivity.class);

				startActivityForResult(intent, 0);

			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 返回当前界面的时候，接受结果的方法
		if (data != null) {

			String phone = data.getStringExtra("phone");
			// 将特殊字符过滤
			phone = phone.replace("-", "").replace(" ", "").trim();

			et_phone_number.setText(phone);
			// 存储联系人
			SpUtil.putString(getApplicationContext(),
					ConstantValue.CONTACT_PHONE, phone);

		}

		super.onActivityResult(requestCode, resultCode, data);

	}
	@Override
	protected void showPrePage() {
		Intent intent = new Intent(getApplicationContext(),
				Setup2Activity.class);

		startActivity(intent);
		// 开启一个activity 关闭上一个activity
		finish();

		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);

	}

	@Override
	protected void showNextPage() {

		// 点击按钮以后，需要获取输入框中的联系人，在做下一页操作
				String phone = et_phone_number.getText().toString();
				// String contact_phone = SpUtil.getString(getApplicationContext(),
				// ConstantValue.CONTACT_PHONE, "");
				// 在sp存储了相关联系人以后才可以跳转到下一个界面
				if (!TextUtils.isEmpty(phone)) {
					Intent intent = new Intent(getApplicationContext(),
							Setup4Activity.class);

					startActivity(intent);
					// 开启一个activity 关闭上一个activity
					finish();
					// 如果是手动输入需要去保存
					SpUtil.putString(getApplicationContext(),
							ConstantValue.CONTACT_PHONE, phone);

					overridePendingTransition(R.anim.net_in_anim, R.anim.net_out_anim);
				} else {

					ToastUtil.show(getApplicationContext(), "请输入电话号码");

				}

	}

}
