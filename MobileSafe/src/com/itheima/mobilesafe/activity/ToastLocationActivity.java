package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.SpUtil;

public class ToastLocationActivity extends Activity {

	private ImageView iv_drag;
	private Button btn_top;
	private Button btn_bottom;
	private WindowManager mWM;
	private int mScreenWidth;
	private int mScreenHeight;
	private long[] mHits = new long[2];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);

		initUI();

	}

	@SuppressWarnings("deprecation")
	private void initUI() {
		// 可拖拽双击居中的图片
		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		btn_top = (Button) findViewById(R.id.btn_top);
		btn_bottom = (Button) findViewById(R.id.btn_bottom);

		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();

		int locationX = SpUtil.getInt(getApplicationContext(),
				ConstantValue.LOCATION_X, 0);
		int locationY = SpUtil.getInt(getApplicationContext(),
				ConstantValue.LOCATION_Y, 0);
		// 左上角坐标作用在iv_drag
		// <!-- iv_drag在相对布局中，所以其所在位置的规则需要由相对布局提供 -->
		// 宽高都为wrap_content

		LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		// 将左上角的坐标作用在iv_drag对应规则参数上
		params.leftMargin = locationX;
		params.topMargin = locationY;
		// 将以上规则作用在iv_drag上
		iv_drag.setLayoutParams(params);

		if (locationY > mScreenHeight / 2) {
			btn_bottom.setVisibility(View.INVISIBLE);
			btn_top.setVisibility(View.VISIBLE);

		} else {

			btn_bottom.setVisibility(View.VISIBLE);
			btn_top.setVisibility(View.INVISIBLE);
		}

		iv_drag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length-1] = SystemClock.uptimeMillis();
				if(mHits[mHits.length-1] - mHits[0] < 500){
					//满足双击事件后，调用代码
					int left = mScreenWidth/2 - iv_drag.getWidth()/2;
					int top = mScreenHeight/2 - iv_drag.getHeight()/2;
					int right = mScreenWidth/2 + iv_drag.getWidth()/2;
					int bottom = mScreenHeight/2 + iv_drag.getHeight()/2;
				    
					//按以上规则作用在控件上
					iv_drag.layout(left, top, right, bottom);
					//存储最终位置
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_Y, iv_drag.getTop());
					
				}
			}
		});
		
		// 监听某个控件的拖拽过程(按下(1)，移动(多次),抬起(1))
		iv_drag.setOnTouchListener(new OnTouchListener() {
			private int startX;
			private int startY;

			// 对不同的事件做不同的逻辑处理
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();

					int disX = moveX - startX;
					int disY = moveY - startY;
					// 当前控件左上角在屏幕的位置
					int left = iv_drag.getLeft() + disX;// 左侧坐标
					int top = iv_drag.getTop() + disY;// 顶端坐标
					int right = iv_drag.getRight() + disX;// 右侧坐标
					int bottom = iv_drag.getBottom() + disY;// 底部坐标

					// 容错处理 左边缘不能超出屏幕
					if (left < 0 || right > mScreenWidth || top < 0
							|| bottom > mScreenHeight - 22) {

						return true;
					}

					if (top > mScreenHeight / 2) {

						btn_bottom.setVisibility(View.INVISIBLE);
						btn_top.setVisibility(View.VISIBLE);

					} else {

						btn_bottom.setVisibility(View.VISIBLE);
						btn_top.setVisibility(View.INVISIBLE);
					}

					// 告知移动的控件，按计算出来的坐标去展示
					iv_drag.layout(left, top, right, bottom);

					// 重置一次起始坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					// 存储移动到的位置
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_X, iv_drag.getLeft());
					SpUtil.putInt(getApplicationContext(),
							ConstantValue.LOCATION_Y, iv_drag.getTop());
					
					break;

				}

				// 返回false 不响应 返回true 响应
				//既要响应点击事件，又要响应拖拽过程，则此返回值结果需要修改为false
				return false;
			}
		});

	}
}
