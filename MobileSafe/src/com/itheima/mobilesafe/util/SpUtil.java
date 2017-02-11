package com.itheima.mobilesafe.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtil {

	private static SharedPreferences sp;

	/**
	 * @param context   上下文环境
	 * @param key       存储节点名称
	 * @param value     存储节点的值    boolean
	 */
	public static void putBoolean(Context context,String key,boolean value){
		if(sp == null){
			//存储节点文件名称，读写模式
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		
		sp.edit().putBoolean(key, value).commit();
	}
	/**
	 * @param context      上下文环境
	 * @param key          存储节点名称
	 * @param defValue     没有此节点的默认值
	 * @return             默认值或者此节点的值
	 */
	public static boolean getBoolean(Context context,String key,boolean defValue){
		if(sp == null){
			//存储节点文件名称，读写模式
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		
		return sp.getBoolean(key, defValue);
	}
	
	/**
	 * @param context   上下文环境
	 * @param key       存储节点名称
	 * @param value     存储节点的值    String
	 */
	public static void putString(Context context,String key,String value){
		if(sp == null){
			//存储节点文件名称，读写模式
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		
		sp.edit().putString(key, value).commit();
	}
	/**
	 * @param context      上下文环境
	 * @param key          存储节点名称
	 * @param defValue     没有此节点的默认值
	 * @return             默认值或者此节点的值
	 */
	public static String getString(Context context,String key,String defValue){
		if(sp == null){
			//存储节点文件名称，读写模式
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		
		return sp.getString(key, defValue);
	}
	/**
	/**
	 * @param context   上下文环境
	 * @param key       存储节点名称
	 * @param which     存储节点的值    String
	 */
	public static void putInt(Context context,String key,int which){
		if(sp == null){
			//存储节点文件名称，读写模式
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		
		sp.edit().putInt(key, which).commit();
	}
	/**
	 * @param context      上下文环境
	 * @param key          存储节点名称
	 * @param defValue     没有此节点的默认值
	 * @return             默认值或者此节点的值
	 */
	public static int getInt(Context context,String key,int defValue){
		if(sp == null){
			//存储节点文件名称，读写模式
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		
		return sp.getInt(key, defValue);
	}
	/**
	 * 从sp中移除指定节点
	 * @param context      上下文环境
	 * @param key           需要移除的节点名称
	 */
	public static void remover(Context context, String key) {
		if(sp == null){
			//存储节点文件名称，读写模式
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		
		sp.edit().remove(key).commit();
		
	}
	
	
}
