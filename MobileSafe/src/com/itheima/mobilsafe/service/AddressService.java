package com.itheima.mobilsafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.engine.AddressDao;
import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.SpUtil;

public class AddressService extends Service {

	public static final String tag = "AddressService";
	private TelephonyManager mPhoneManager;
	private MyPhoneStateListener mStateListener;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private View mView;
	private WindowManager mWM;
	private String mAddress;
	private TextView tv_toast;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			tv_toast.setText(mAddress);
		};
	};
	private int[] mDrawableIds;

	private int mScreenWidth;
	private int mScreenHeight;
	private InnerOutCallReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		// 第一次开启服务以后，就需要去管理土司的显示
		// 电话状态的监听
		// 电话管理者对象
		mPhoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		mStateListener = new MyPhoneStateListener();
		// 监听电话状态
		mPhoneManager.listen(mStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
		// 创建窗体对象
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);

		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		
		//监听拨出电话的广播接受者
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		
		mReceiver = new InnerOutCallReceiver();
		
		registerReceiver(mReceiver, filter);
		
		super.onCreate();
	}
	private class InnerOutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//接收到此广播后，需要显示自定义的吐司，显示播出归属地号码
			//获取拨出的电话号码
			String phone = getResultData();
			showToast(phone);
		
		}
		
	}
	
	
	private class MyPhoneStateListener extends PhoneStateListener {
		// 重写，电话状态发生改变会触发的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// 空闲状态
				Log.i(tag, "挂断，空闲状态");
				// 挂断电话的时候窗体需要移除吐司

				if (mView != null && mWM != null) {
					mWM.removeView(mView);

				}

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 摘机状态

				break;
			case TelephonyManager.CALL_STATE_RINGING:// 响铃状态
				Log.i(tag, "响铃状态.....");

				showToast(incomingNumber);

				break;

			default:
				break;
			}

			super.onCallStateChanged(state, incomingNumber);
		}

	}

	@Override
	public void onDestroy() {
		//
		if (mPhoneManager != null && mStateListener != null) {
			// 取消监听电话的状态
			mPhoneManager
					.listen(mStateListener, PhoneStateListener.LISTEN_NONE);
		}
		if(mReceiver!=null){
			
			unregisterReceiver(mReceiver);
		}
		
		super.onDestroy();
	}

	public void showToast(String incomingNumber) {

		final WindowManager.LayoutParams params = mParams;

		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;

		params.format = PixelFormat.TRANSLUCENT;

		// 在响铃的时候显示土司, 和电话类型一至
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.setTitle("Toast");

		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
		 | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;
			//	| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE; 默认能够被触摸

		// 指定土司的所在位置
		params.gravity = Gravity.LEFT + Gravity.TOP;
		// 土司显示效果(土司布局文件)，xml----->view(土司)，将吐司挂载到windowManager窗体上

		mView = View
				.inflate(getApplicationContext(), R.layout.toast_view, null);

		tv_toast = (TextView) mView.findViewById(R.id.tv_toast);


//		System.out.println("123456");

		mView.setOnTouchListener(new OnTouchListener() {
			protected int startX;
			protected int startY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					System.out.println("ACTION_DOWN");

					break;
				case MotionEvent.ACTION_MOVE:
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();

					int disX = moveX - startX;
					int disY = moveY - startY;

					params.x = params.x + disX;
					params.y = params.y + disY;
					
					// 容错处理
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.y < 0) {
						params.y = 0;
					}
					if (params.x > mScreenWidth - mView.getWidth()) {

						params.x = mScreenWidth - mView.getWidth();
					}
					if (params.y > mScreenHeight - mView.getHeight() - 22) {

						params.y = mScreenHeight - mView.getHeight() - 22;
					}

					// 告知窗体吐司需要按照手势的移动，去做位置的更新
					mWM.updateViewLayout(mView, params);

					// 重置一次起始坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					// 存储移动到的位置
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_X, params.x);
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_Y, params.y);

					break;

				}

				// 响应拖拽触发的事件
				// 返回false 既响应点击事件 又响应拖拽过程
				return true;
			}
		});
		
		
		// 读取sp中存储吐司位置的x，y坐标值
		// params.x 为土司x轴左上角的值 params.y 为土司左上角y轴的值
		params.x = SpUtil.getInt(getApplicationContext(),
				ConstantValue.LOCATION_X, 0);
		params.y = SpUtil.getInt(getApplicationContext(),
				ConstantValue.LOCATION_Y, 0);

		// 从sp中获取色值文字的索引，匹配图片，用作展示
		mDrawableIds = new int[] { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };

		int index = SpUtil.getInt(getApplicationContext(),
				ConstantValue.TOAST_STYLE, 0);

		tv_toast.setBackgroundResource(mDrawableIds[index]);

		// 在窗体上挂载一个view(权限)
		mWM.addView(mView, params);

		// 获取来电号码以后，需要做来电号码查询
		query(incomingNumber);

	}

	private void query(final String phone) {
		// 耗时操作放到子线程中
		new Thread() {
			public void run() {
				// 查询电话号码归属地
				mAddress = AddressDao.getAddress(phone);
				mHandler.sendEmptyMessage(0);
			};

		}.start();
	}

}
