package com.itheima.mobilesafe.global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

public class MyApplication extends Application {
	protected static final String tag = "MyApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		//捕获全局异常
		
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				//在获取到了未捕获的异常后，处理的方法
				ex.printStackTrace();
				Log.i(tag, "捕获到了一个异常");
				//将捕获的异常存储到sd卡中
				
				String path = Environment.getExternalStorageDirectory().getAbsoluteFile()+File.separator+"error.log";
				File file = new File(path);
				try {
					PrintWriter writer = new PrintWriter(file);
					
					ex.printStackTrace(writer);
					writer.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				//把错误日志上传到公司服务器
				System.exit(0);
				
			}
		});
	}
}
