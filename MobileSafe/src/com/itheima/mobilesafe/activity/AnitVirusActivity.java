package com.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.engine.ViruDao;
import com.itheima.mobilesafe.util.MD5Utils;

public class AnitVirusActivity extends Activity {
	protected static final int SCANING = 100;
	protected static final int SCANING_FINISH = 101;
	private ImageView iv_scanning;
	private TextView tv_name;
	private ProgressBar pb_bar;
	private LinearLayout ll_add_text;
	private List<ScannInfo> mVirusScanInfoList;
	private int index = 0;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				//1.显示正在扫描应用的名称
				 ScannInfo info = (ScannInfo) msg.obj;
				tv_name.setText(info.name);
				//在线性布局中添加一个正在扫描应用的textView
				TextView textView = new TextView(getApplicationContext());
				if(info.isVirus){
					//是病毒
					textView.setTextColor(Color.RED);
					textView.setText("发现病毒:"+info.name);
				}else{
					//不是病毒
					textView.setTextColor(Color.BLACK);
					textView.setText("扫描安全:"+info.name);
				}
				ll_add_text.addView(textView, 0);
				break;
			case SCANING_FINISH:
				tv_name.setText("扫描完成");
				//停止动画
				iv_scanning.clearAnimation();
				//告知用户卸载包含了病毒的应用
				uninstallVirus();
				break;
			    
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anit_virus);

		initUI();
		initAnimation();
		checkVirus();
	}

	protected void uninstallVirus() {
		for (ScannInfo scannInfo : mVirusScanInfoList) {
			String packageName = scannInfo.packageName;
			//卸载病毒应用
			Intent intent = new Intent("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:" + packageName));
			startActivity(intent);
			
		}
	}

	private void checkVirus() {
		new Thread() {

			public void run() {
				// 获取数据库中所有的md5码
				List<String> virusList = ViruDao.getVirusList();
				// 获取手机上面所有应用程序
				// 获取包管理者
				PackageManager pm = getPackageManager();
				// 获取所有应用程序签名文件(PackageManager.GET_SIGNATURES 已安装应用的签名文件)
				// PackageManager.GET_UNINSTALLED_PACKAGES 卸载完了的应用，残余的文件
				List<PackageInfo> installedPackages = pm
						.getInstalledPackages(PackageManager.GET_SIGNATURES
								+ PackageManager.GET_UNINSTALLED_PACKAGES);
				// 记录所有应用的集合
				List<ScannInfo> scanInfoList = new ArrayList<ScannInfo>();

				mVirusScanInfoList = new ArrayList<ScannInfo>();
				// 设置进度条的最大值
				pb_bar.setMax(installedPackages.size());
				// 遍历应用集合
				for (PackageInfo packageInfo : installedPackages) {
					ScannInfo scannInfo = new ScannInfo();
					// 获取签名文件的数组
					Signature[] signatures = packageInfo.signatures;
					// 获取签名文件数组的第一位，然后进行md5,将次md5和数据库中的md5比对
					Signature signature = signatures[0];
					String string = signature.toCharsString();
					// 32位字符串，16进制字符
					String encoder = MD5Utils.encoder(string);
					if (virusList.contains(encoder)) {
						// 记录病毒

						scannInfo.isVirus = true;
						mVirusScanInfoList.add(scannInfo);
					} else {

						scannInfo.isVirus = false;

					}
					// 维护应用的包名，以及应用名称
					scannInfo.packageName = packageInfo.packageName;

					scannInfo.name = packageInfo.applicationInfo.loadLabel(pm)
							.toString();

					scanInfoList.add(scannInfo);
					// 在扫描的过程中，需要更新进度条
					index++;
					pb_bar.setProgress(index);

					try {
						Thread.sleep(50 + new Random().nextInt(100));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// 在子线程中发消息,告知主线程更新UI，(1.顶部扫描应用的名称 2:扫描过程中往线性布局中增加view)
					Message msg = Message.obtain();
					msg.what = SCANING;
					msg.obj = scannInfo;
					mHandler.sendMessage(msg);
				}
				Message msg = Message.obtain();
				msg.what = SCANING_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();

	}

	private class ScannInfo {
		public boolean isVirus;
		public String packageName;
		public String name;
	}

	private void initAnimation() {
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);

		rotateAnimation.setDuration(1000);
		// 指定动画一直旋转
		// rotateAnimation.setRepeatMode(RotateAnimation.INFINITE);
		rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);

		iv_scanning.startAnimation(rotateAnimation);
	}

	private void initUI() {
		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
		tv_name = (TextView) findViewById(R.id.tv_name);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);

	}
}
