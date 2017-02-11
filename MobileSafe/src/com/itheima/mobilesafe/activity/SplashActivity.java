package com.itheima.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.SpUtil;
import com.itheima.mobilesafe.util.StreamUtils;
import com.itheima.mobilesafe.util.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

public class SplashActivity extends Activity {

	protected static final String tag = "SplashActivity";

	/**
	 * 提示用户更新状态码
	 */
	protected static final int UPDATE_VERSION = 1;
	/**
	 * 进入应用程序状态码
	 */
	protected static final int ENTER_HOME = 2;

	/**
	 * URL地址错误
	 */
	protected static final int URL_ERROR = 3;
	protected static final int IO_ERROR = 4;
	protected static final int JSON_ERROR = 5;
	private TextView tv_version_name;
	private int mLocalVersionCode;
	
	private String mVersionDes;
	private String mDownloadUrl;
	
	private Handler mHandler = new Handler() {
		//
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case UPDATE_VERSION:
				// 弹出对话框，提示用户更新
                showUpdateDialog();
				break;
			case ENTER_HOME:
				// 进入应用程序主界面
				enterHome();

				break;
			case URL_ERROR:
				ToastUtil.show(SplashActivity.this, "url异常");
				enterHome();
				break;
			case IO_ERROR:
				ToastUtil.show(SplashActivity.this, "读取流异常");
				enterHome();
				break;
			case JSON_ERROR:
				ToastUtil.show(SplashActivity.this, "json解析异常");
				enterHome();
				break;
			}

		};

	};

	private RelativeLayout rl_root;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除掉当前activity头title
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_splash);
		// 初始化UI
		initUi();
		// 初始化数据
		initData();
		//初始化动画
		initAnimation();
		//初始化数据库
		initDB();
		
		if(!SpUtil.getBoolean(getApplicationContext(), ConstantValue.HAS_SHORTCUT, false)){
			//生成快捷方式
			initShortCut();
			
		}
		
	}

	/**
	 * 生成快捷方式
	 * 
	 */
	private void initShortCut() {
		//给intent维护图标，名称
		Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		//维护图标
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		//名称
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
		//点击快捷方式跳转到的activity
		//维护开启的意图对象
		Intent shortCutIntent = new Intent("android.intent.action.HOME");
		
		shortCutIntent.addCategory("android.intent.category.DEFAULT");
		
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
		//发送广播
		sendBroadcast(intent);
		
		SpUtil.putBoolean(getApplicationContext(), ConstantValue.HAS_SHORTCUT, true);
	}

	private void initDB() {
		//归属地数据拷贝过程
		initAddressDB("address.db");
		//常见号码数据库的拷贝过程
		initAddressDB("commonnum.db");
		//病毒数据库的拷贝过程
		initAddressDB("antivirus.db");
	}

	/**
	 * 拷贝数据库至files文件夹下
	 * 
	 * @param dbName  数据库名字
	 */
	private void initAddressDB(String dbName) {
//		getCacheDir();
//		Environment.getExternalStorageDirectory().getAbsolutePath()
		
		InputStream stream = null;
		FileOutputStream fos = null;
		//在files文件夹下创建dbName数据库文件过程
		File files = getFilesDir();
		File file = new File(files, dbName);
		//是否已经存在
		if(file.exists()){
			
			return;
		}
		//  输入流 读取第三方资产目录下的文件
		try {
		     stream = getAssets().open(dbName);
			//将读取的内容写入到指定文件夹的文件中去
			 fos = new FileOutputStream(file);
			//每次的读取内容大小
			byte[] buffer = new byte[1024];
			int temp = -1;
			while((temp = stream.read(buffer))!=-1){
				
				fos.write(buffer, 0, temp);
				
			}
			
			
		} catch (IOException e) {

			e.printStackTrace();
		
		}finally{
		    if(stream!=null && fos!=null){
		    	try {
					stream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }	
			
		}
		
		
	}

	/**
	 * 加入淡入动画效果
	 * 
	 */
	private void initAnimation() {
		//从完全透明到不透明   0 完全透明    1不透明
     	AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
     	//执行动画的时间
     	alphaAnimation.setDuration(3000);
     	
     	rl_root.startAnimation(alphaAnimation);
     	
	}

	/**
	 * 弹出对话框，提示用户更新
	 */
	
	protected void showUpdateDialog() {
		//对话框，是依赖activity存在的
		Builder builder = new AlertDialog.Builder(this);
		//设置左上角图标
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("版本更新");
		//设置描述内容
		builder.setMessage(mVersionDes);
		//积极按钮，立即更新
		builder.setPositiveButton("立即更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//下载apk ,apk连接地址，dowmloadUrl
				downloadApk();
			}
		});
		builder.setNegativeButton("稍后再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			      //取消对话框，进入主界面
				enterHome();
			}
		});
		//点击取消时间监听
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				//即使用户点击取消，页跳转到Home界面
				enterHome();
				dialog.dismiss();
			}
		});
		
		builder.show();
	}

	/**
	 * 
	 */
	protected void downloadApk() {
          //apk下载连接地址，放置apk的是在路径
		  
		  //1.判断SD卡是否可用，是否挂载上
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//获取SD卡路径
			String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/mobilsafe.apk";
			//3.发送请求，获取apk并且放到指定的路径
			HttpUtils httpUtils = new HttpUtils();
			
			//发送请求，传递参数  (下载地址，下载应用放置的位置，)
			httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
				
				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					//下载成功  (下载过后放置在SD卡中的apk)
					Log.i(tag, "下载成功");
					File file = responseInfo.result;
					installApk(file);
				}
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					//下载失败
					Log.i(tag, "下载失败");

					
				}
				//刚开是开始下载的方法
				@Override
				public void onStart() {
					Log.i(tag, "刚开始下载");
					
					super.onStart();
				}
				//下载过程中的方法（apk的总大小，当前下载位置 ，是否正在下载）
				@Override
				public void onLoading(long total, long current,boolean isUploading) {
					Log.i(tag, "下载中....");
					Log.i(tag, "total = "+total);
					Log.i(tag, "current = "+current);
					
					super.onLoading(total, current, isUploading);
				}
			});
			
		}
	}

	/**
	 * 安装对应apk
	 * 
	 * @param file   安装文件
	 */
	protected void installApk(File file) {
		//系统界面，源码，安装apk入口
		Intent intent = new Intent("android.intent.action.VIEW");
		
		intent.addCategory("android.intent.category.DEFAULT");
	/*	//文件作为数据源
		intent.setData(Uri.fromFile(file));
		//设置安装的类型
		intent.setType("application.android.package-archive");*/
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		
//		startActivity(intent);
		startActivityForResult(intent, 0);
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		enterHome();
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 进入应用程序主界面
	 * 
	 */
	protected void enterHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// 在开启一个新的界面后，将导航界面关闭
		finish();

	}

	/**
	 * 初始化UI方法 alt+shift+j
	 */

	private void initUi() {

		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}

	/**
	 * 获取数据方法
	 */

	private void initData() {
		// 1. 应用版本名称
		tv_version_name.setText("版本名称：" + getVersionName());
		// 2 应用版本号
		mLocalVersionCode = getVersionCode();
		// 获取服务器版本号（客户端发请求， 服务端给响应，(json,xml)）
        if(SpUtil.getBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, false)){
        	
        	checkVersion();
        }else{
        	//直接进入应用程序主界面
        	//消息机制
        	//在发送消息4秒后去处理当前ENTER_HOME状态码指向的消息
        	mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }
		
	}

	/**
	 * 检测版本号
	 * 
	 */
	private void checkVersion() {
		// 开启线程的两种方式

		new Thread() {
			

			

			public void run() {
				// 发送请求获取数据

				Message msg = Message.obtain();
				long startTime = System.currentTimeMillis();
				try {
					// 封装 url 地址
					URL url = new URL("http://10.0.2.2:8080/update.json");
					// 开启一个连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					// 设置常见请求参数
					// 请求超时
					conn.setConnectTimeout(2000);
					// 读取超时
					conn.setReadTimeout(2000);
					// 请求方式get post 默认是get
					// conn.setRequestMethod("POST");
					// 获取响应码

					if (conn.getResponseCode() == 200) {
						// 服务器以流的形式返回
						InputStream is = conn.getInputStream();
						// 将流转换成字符串(工具类封装)
						String json = StreamUtils.streamToString(is);
						Log.i(tag, json);
						// 解析json
						JSONObject jsonObject = new JSONObject(json);
						String versionName = jsonObject
								.getString("versionName");
						mVersionDes = jsonObject.getString("versionDes");
						String versionCode = jsonObject
								.getString("versionCode");
						mDownloadUrl = jsonObject
								.getString("downloadUrl");

						Log.i(tag, versionName);
						Log.i(tag, mVersionDes);
						Log.i(tag, versionCode);
						Log.i(tag, mDownloadUrl);

						// 判断本地版本号与服务器版本号的大小
						if (Integer.parseInt(versionCode) > mLocalVersionCode) {
							// 服务器版本大于本地版本提示更新,弹出对话框UI，消息机制
							msg.what = UPDATE_VERSION;

						} else {
							// 进入主界面
							msg.what = ENTER_HOME;
						}

					}

				} catch (MalformedURLException e) {

					e.printStackTrace();
					msg.what = URL_ERROR;

				} catch (IOException e) {

					e.printStackTrace();
					msg.what = IO_ERROR;
				} catch (JSONException e) {

					e.printStackTrace();
					msg.what = JSON_ERROR;
				} finally {
					// 约定：
					// 指定睡眠时间，请求网络的时长超过4秒侧不做处理
					// 请求网络的时长小于4秒，强制让其睡满4秒
					long endTime = System.currentTimeMillis();
					if (endTime - startTime < 4000) {
						try {
							Thread.sleep(4000 - (endTime - startTime));
						} catch (InterruptedException e) {

							e.printStackTrace();
						}

					}

					mHandler.sendMessage(msg);
				}

			};

		}.start();

		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() {
		 * 
		 * 
		 * } }).start();
		 */

	}

	/**
	 * 返回版本号
	 * 
	 * @return 非0 表示获取成功
	 * 
	 */
	private int getVersionCode() {
		// 1,包管理者对象packageManager
		PackageManager pm = getPackageManager();
		// 2,从包的管理者对象中,获取指定包名的基本信息(版本名称,版本号),传0代表获取基本信息
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);

			// 3,获取版本号
			return packageInfo.versionCode;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获取版本名称:清单文件中
	 * 
	 * @return 应用版本名称 返回null代表异常
	 */

	private String getVersionName() {
		// 1,包管理者对象packageManager
		PackageManager pm = getPackageManager();
		// 2,从包的管理者对象中,获取指定包名的基本信息(版本名称,版本号),传0代表获取基本信息
		try {
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);

			// 3,获取版本名称
			return packageInfo.versionName;

		} catch (Exception e) {

			e.printStackTrace();
		}
		return null;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
