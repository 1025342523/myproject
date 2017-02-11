package com.itheima.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.itheima.mobilesafe.db.bean.AppInfo;

public class AppInfoProvider {
	/**
	 * 返回当前手机所有的应用的相关的信息(名称,包名,图标,(内存,sd卡),(系统,手机));
	 * @param context  获取报管理者的上下文 
	 *  @return     包含手机安装应用相关信息的对象
	 * 
	 */
	public static List<AppInfo> getAppInfoList(Context context){
		//包的管理者对象
		PackageManager pm = context.getPackageManager();
		//获取安装在手机上应用相关信息的集合
		List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
		
		List<AppInfo> appInfoList = new ArrayList<AppInfo>();
		//循环遍历应用信息的集合
		for (PackageInfo packageInfo : installedPackages) {
			AppInfo appInfo = new AppInfo();
			//获取应用的包名
			appInfo.name = packageInfo.packageName;
			//获取应用名称
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			appInfo.name = applicationInfo.loadLabel(pm).toString()+applicationInfo.uid;
			//获取图标
			appInfo.icon = applicationInfo.loadIcon(pm);
			//判断是否为系统应用
			if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
				//系统应用
				appInfo.isSystem = true;
			}else{
				//手机应用
				appInfo.isSystem = false;
			}
			//判断是否为sd卡中安装的应用
			if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE){
				//系统应用
				appInfo.isSdCard = true;
			}else{
				//手机应用
				appInfo.isSdCard = false;
			}
			appInfoList.add(appInfo);
		}
		
		return appInfoList;
		
	}
}
