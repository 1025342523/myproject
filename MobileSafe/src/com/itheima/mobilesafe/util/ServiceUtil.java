package com.itheima.mobilesafe.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {
	
	private static ActivityManager mActivityManager;

	/**
	 * @param context    上下文环境
	 * @param serviceName     服务的名称
	 * @return     true 代表服务开启         false  代表服务关闭
	 */
	public static boolean isRunning(Context context,String serviceName){
		//获取activityManager管理者，可以去获取当前手机正在运行的所有服务
		mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取手机中正在运行的服务集合(多少个服务)
		List<RunningServiceInfo> runningServices = mActivityManager.getRunningServices(1000);
		//遍历获取所有服务的集合，拿到每一个
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			if(serviceName.equals(runningServiceInfo.service.getClassName())){
				
				return true;
			}
		}
		
		return false;
	}
}
