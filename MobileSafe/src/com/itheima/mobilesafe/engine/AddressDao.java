package com.itheima.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressDao {

	private static final String tag = "AddressDao";
	// 指定访问数据库的路径
	public static String path = "data/data/com.itheima.mobilesafe/files/address.db";
	private static String mAddress;

	/**
	 * //开启数据库连接，进行访问,传递一个电话号码
	 * 
	 * @param phone
	 *            查询电话号
	 */
	public static String getAddress(String phone) {
		// 正则表达式，匹配手机号
		String reg = "^1[3-8]\\d{9}";
		boolean b = phone.matches(reg);

		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		if (b) {
			phone = phone.substring(0, 7);
			// 开启数据库连接(只读的形式打开)
			

			Cursor cursor = db.query("data1", new String[] { "outkey" },
					"id = ?", new String[] { phone }, null, null, null);
			if (cursor.moveToNext()) {

				String outkey = cursor.getString(0);

//				Log.i(tag, outkey);
				Cursor query = db.query("data2", new String[] { "location" },
						"id = ?", new String[] { outkey }, null, null, null);

				if (query.moveToNext()) {

					mAddress = query.getString(0);

//					Log.i(tag, address);

				}else{
					mAddress = "未知号码";
					
				}

			}

		}else{
			int length = phone.length();
			switch (length) {
			case 3://110 119 120 114
				mAddress = "报警电话";
				break;
			case 4://110 119 120 114
				mAddress = "模拟器";
				break;
			case 5://110 119 120 114
				mAddress = "服务电话";
				break;
			case 7://110 119 120 114
				mAddress = "本地电话";
				break;
			case 8://110 119 120 114
				mAddress = "本地电话";
				break;
			case 11://110 119 120 114
				String area = phone.substring(1, 3);
				
				Cursor cursor = db.query("data2", new String[]{"location"}, "area =?", new String[]{area}, null, null, null);
				
				if(cursor.moveToNext()){
					mAddress = cursor.getString(0);
					
				}else{
					
					mAddress = "未知号码";
				}
				break;
			case 12://110 119 120 114
				String area1 = phone.substring(1, 4);
				
				Cursor cursor1 = db.query("data2", new String[]{"location"}, "area =?", new String[]{area1}, null, null, null);
				
				if(cursor1.moveToNext()){
					mAddress = cursor1.getString(0);
					
				}else{
					
					mAddress = "未知号码";
				}
				break;

				
			default:
				mAddress = "未知号码";
				break;
			}
			
		}
        if(db!=null){
        	db.close();
        }
		return mAddress;
	}

}
