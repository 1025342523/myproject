package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.engine.AddressDao;

public class QueryAddressActivity extends Activity {

	private EditText et_phone;
	private Button btn_query;
	private TextView tv_result;
	private String mAddress;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tv_result.setText(mAddress);
		};

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);

		initUI();
	}

	private void initUI() {
		et_phone = (EditText) findViewById(R.id.et_phone);
		btn_query = (Button) findViewById(R.id.btn_query);
		tv_result = (TextView) findViewById(R.id.tv_result);

		btn_query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String phone = et_phone.getText().toString();
				// 查询是耗时操作，放到子线程中
				if (!TextUtils.isEmpty(phone)) {

					query(phone);
				} else {
					Animation shake = AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.shake);
					findViewById(R.id.et_phone).startAnimation(shake);
					// 手机震动效果
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    //震动毫秒值
					vibrator.vibrate(2000);
					//规律震动(震动规则(不震动时间，震动时间，不震动时间，震动时间))
					vibrator.vibrate(new long[]{2000,5000,2000,5000}, -1);
				}

			}
		});

		et_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String phone = et_phone.getText().toString();

				query(phone);
			}
		});

	}

	/**
	 * 耗时操作 获取电话号码的归属地
	 * 
	 * @param phone
	 *            查询电话号码
	 */
	protected void query(final String phone) {
		new Thread() {
			public void run() {
				mAddress = AddressDao.getAddress(phone);
				// 消息机制，告知主线程查询结束，可以去使用查询结果
				mHandler.sendEmptyMessage(0);
			};

		}.start();

	}
}
