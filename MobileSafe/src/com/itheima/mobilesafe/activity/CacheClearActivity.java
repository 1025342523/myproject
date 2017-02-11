package com.itheima.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

public class CacheClearActivity extends Activity {

	protected static final int UPDATE_CACHE_APP = 100;
	protected static final int CHECK_CACHE_APP = 101;
	protected static final int CHECK_FINISH = 102;
	protected static final int CLEAR_CACHE = 103;
	private Button btn_clear;
	private ProgressBar pb_bar;
	private TextView tv_name;
	private LinearLayout ll_add_text;
	private PackageManager mPm;
	private int mIndex = 0;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_CACHE_APP:
				// 在线性布局中添加有缓存应用条目
				View view = View.inflate(getApplicationContext(),
						R.layout.linearlayout_cache_item, null);
				ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				TextView tv_item_name = (TextView) view
						.findViewById(R.id.tv_name);
				TextView tv_memory_info = (TextView) view
						.findViewById(R.id.tv_memory_info);
				ImageView iv_delete = (ImageView) view
						.findViewById(R.id.iv_delete);

				final CacheInfo cacheInfo = (CacheInfo) msg.obj;
				iv_icon.setBackgroundDrawable(cacheInfo.icon);
				tv_item_name.setText(cacheInfo.name);
				tv_memory_info.setText(Formatter.formatFileSize(
						getApplicationContext(), cacheInfo.cacheSize));

				ll_add_text.addView(view, 0);
				
				iv_delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						
					/*	以下代码如果要执行成功则需要系统应用才可以去使用的权限
					 * 
					 * //清除单个选中应用的缓存内容(PackageManager)
						try {
							Class<?> clazz = Class.forName("android.content.pm.PackageManager");
							// 2.获取调用方法对象
							Method method = clazz.getMethod("deleteApplicationCacheFiles", String.class,
									IPackageDataObserver.class);
							// 3.获取对象调用方法 方法对应的参数
							method.invoke(mPm, cacheInfo.packagename, new IPackageDataObserver.Stub() {
								
								@Override
								public void onRemoveCompleted(String packageName, boolean succeeded)
										throws RemoteException {
									//删除此应用缓存后，调用的方法，子线程中
									
								}
							});

						} catch (Exception e) {
							e.printStackTrace();
						}*/
						//通过查看日志，获取清理缓存activity中action和data
						Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.setData(Uri.parse("package:"+cacheInfo.packagename));
						startActivity(intent);
					}
				});
				break;
			case CHECK_CACHE_APP:

				tv_name.setText((String) msg.obj);

				break;
			case CHECK_FINISH:

				tv_name.setText("扫描完成");

				break;
			case CLEAR_CACHE:
				//从线性布局中删除所有条目
				ll_add_text.removeAllViews();
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_cache_clear);
		initUI();

		initDate();
	}

	/**
	 * 遍历手机所有的应用，获取有缓存的应用，用作显示
	 */
	private void initDate() {

		new Thread() {

			public void run() {
				// 获取包管理者对象
				mPm = getPackageManager();
				// 获取安装在手机上的所有应用
				List<PackageInfo> installedPackages = mPm
						.getInstalledPackages(0);
				// 给进度条设置最大值
				pb_bar.setMax(installedPackages.size());
				// 循环遍历每一个应用，获取有缓存应用的信息(应用名称，图标，缓存大小，包名)
				for (PackageInfo packageInfo : installedPackages) {
					String packageName = packageInfo.packageName;

					getPackageCache(packageName);

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mIndex++;
					pb_bar.setProgress(mIndex);
					// 每循环一次就将应用的名称发送给主线程显示
					Message msg = Message.obtain();
					msg.what = CHECK_CACHE_APP;
					try {
						msg.obj = mPm.getApplicationInfo(packageName, 0)
								.loadLabel(mPm).toString();
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					mHandler.sendMessage(msg);
				}
				Message msg = Message.obtain();
				msg.what = CHECK_FINISH;
				mHandler.sendMessage(msg);
			};
		}.start();

	}

	private class CacheInfo {
		public String name;
		public Drawable icon;
		public String packagename;
		public long cacheSize;
	}

	/**
	 * 通过包名获取此包名指向应用的缓存信息
	 * 
	 * @param packageName
	 *            应用包名
	 * 
	 */
	protected void getPackageCache(String packageName) {
		// 创建了一个IPackageStatsObserver.Stub子类的对象,并且实现了onGetStatsCompleted方法
		IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

			public void onGetStatsCompleted(PackageStats stats,
					boolean succeeded) {
				// 获取缓存大小的过程
				long cacheSize = stats.cacheSize;
				// 判断缓存大小是否大于0
				if (cacheSize > 0) {
					// 告知主线程更新UI
					Message msg = Message.obtain();
					msg.what = UPDATE_CACHE_APP;
					CacheInfo cacheInfo = new CacheInfo();
					cacheInfo.cacheSize = cacheSize;
					cacheInfo.packagename = stats.packageName;
					try {
						cacheInfo.name = mPm
								.getApplicationInfo(stats.packageName, 0)
								.loadLabel(mPm).toString();
						cacheInfo.icon = mPm.getApplicationInfo(
								stats.packageName, 0).loadIcon(mPm);

					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					// System.out.println("********"+cacheInfo);
					msg.obj = cacheInfo;
					mHandler.sendMessage(msg);
				}
			}
		};
		try {
			Class<?> clazz = Class.forName("android.content.pm.PackageManager");
			// 2.获取调用方法对象
			Method method = clazz.getMethod("getPackageSizeInfo", String.class,
					IPackageStatsObserver.class);
			// 3.获取对象调用方法 方法对应的参数
			method.invoke(mPm, packageName, mStatsObserver);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initUI() {
		btn_clear = (Button) findViewById(R.id.btn_clear);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		tv_name = (TextView) findViewById(R.id.tv_name);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);

		btn_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Class<?> clazz = Class
							.forName("android.content.pm.PackageManager");
					// 2.获取调用方法对象
					Method method = clazz.getMethod("freeStorageAndNotify",
							long.class, IPackageDataObserver.class);
					// 3.获取对象调用方法 方法对应的参数
					method.invoke(mPm, Long.MAX_VALUE,
							new IPackageDataObserver.Stub() {

								@Override
								public void onRemoveCompleted(
										String packageName, boolean succeeded)
										throws RemoteException {
									// 清除缓存后调用的方法
									Message msg = Message.obtain();
									msg.what = CLEAR_CACHE;
									mHandler.sendMessage(msg);
								}
							});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
