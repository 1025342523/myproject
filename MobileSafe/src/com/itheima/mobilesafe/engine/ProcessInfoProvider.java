package com.itheima.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.bean.ProcessInfo;

public class ProcessInfoProvider {
	private static FileReader reader;
	private static BufferedReader bufferedReader;

	// 获取进程总数的方法
	public static int getProcessCount(Context context) {
		// 获取activityManager对象
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取正在运行进程的集合
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		// 返回集合的总数
		return runningAppProcesses.size();
	}

	/**
	 * @param context
	 * @return 返回可用内存数 bytes
	 */
	public static long getAvailSpace(Context context) {
		// 获取activityManager对象
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 构造存储可用内存对象
		MemoryInfo memoryInfo = new MemoryInfo();
		// 给memoryInfo对象赋值
		am.getMemoryInfo(memoryInfo);
		// 获取memoryInfo中相应可用内存大小
		return memoryInfo.availMem;
	}

	@SuppressWarnings("resource")
	public static long getTotalSpace(Context context) {
		/*
		 * // 获取activityManager对象 ActivityManager am = (ActivityManager) context
		 * .getSystemService(Context.ACTIVITY_SERVICE); //构造存储可用内存对象 MemoryInfo
		 * memoryInfo = new MemoryInfo(); //给memoryInfo对象赋值
		 * am.getMemoryInfo(memoryInfo); //获取memoryInfo中相应可用内存大小 return
		 * memoryInfo.availMem;
		 */

		try {
			reader = new FileReader("proc/meminfo");

			bufferedReader = new BufferedReader(reader);

			String line = bufferedReader.readLine();
			// 将字符串转换成字符数组
			char[] charArray = line.toCharArray();

			StringBuffer sb = new StringBuffer();
			// 循环遍历每一个字符,如果此字符的ASCII码在0到9的范围内，说明此字符有效
			for (char c : charArray) {
				if (c >= '0' && c <= '9') {

					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString()) * 1024;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null && bufferedReader != null) {
					reader.close();
					bufferedReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static List<ProcessInfo> getProcessInfo(Context context) {

		List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
		// 获取进程相关信息
		// 1.获取activityManager管理者对象
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();

		// 2.获取正在运行的进程的集合
		List<RunningAppProcessInfo> runningAppProcesses = am
				.getRunningAppProcesses();
		// 遍历上诉集合，获取进程相关信息(名称，包名，图标，使用内存大小)
		for (RunningAppProcessInfo info : runningAppProcesses) {
			ProcessInfo processInfo = new ProcessInfo();
			// 获取进程的名称 == 应用的包名
			processInfo.packageName = info.processName;
			// 获取进程占用内存的大小(传递一个进程对应的pid数组)
			android.os.Debug.MemoryInfo[] processMemoryInfo = am
					.getProcessMemoryInfo(new int[] { info.pid });
			// 返回数组中索引位置为0的对象，为当前进程的内存信息对象
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			// 获取已使用的大小
			processInfo.memSize = memoryInfo.getTotalPrivateDirty() * 1024;
			// 获取应用的名称
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						processInfo.packageName, 0);
				// 获取应用的名称
				processInfo.name = applicationInfo.loadLabel(pm).toString();
				// 获取应用的图标
				processInfo.icon = applicationInfo.loadIcon(pm);
				// 判断是否为系统进程
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {

					processInfo.isSystem = true;
				} else {
					processInfo.isSystem = false;

				}
			} catch (NameNotFoundException e) {
				// 需要处理
				processInfo.name = info.processName;
				processInfo.icon = context.getResources().getDrawable(
						R.drawable.ic_launcher);
				processInfo.isSystem = true;
				e.printStackTrace();
			}

			processInfoList.add(processInfo);
		}

		return processInfoList;
	}

	/**
	 * 杀死进程方法
	 * 
	 * @param processInfo
	 *            进程所对应的javabean对象
	 * @param context
	 *            上下文环境
	 */
	public static void killProcess(ProcessInfo processInfo, Context context) {
		// 1.获取activityManager
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 2.杀死指定包名进程
		am.killBackgroundProcesses(processInfo.packageName);

	}

	public static void killAll(Context context) {
		// 1.获取activityManager
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 2.获取正在运行的进程集合
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//循环遍历所有的进程并且杀死
		for (RunningAppProcessInfo info : runningAppProcesses) {
			//除了手机卫士以外，其他进程都需要杀死
			if(info.processName.equals(context.getPackageName())){
				//如果匹配上了手机卫士，则需要跳出本次循环，进行下一次循环，继续杀死进程
				continue;
			}
			am.killBackgroundProcesses(info.processName);
		}
		
	}
}
