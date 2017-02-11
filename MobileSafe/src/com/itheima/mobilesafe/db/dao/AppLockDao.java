package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.itheima.mobilesafe.db.AppLockOpenHelper;

public class AppLockDao {
	
	private AppLockOpenHelper appLockOpenHelper;
	private Context context;

	// BlackNumberDao单例模式
	// 1.私有化构造方法
	public AppLockDao(Context context) {
		this.context = context;
		appLockOpenHelper = new AppLockOpenHelper(context);

	}

	// 2.声明当前类的对象
	private static AppLockDao appLockDao = null;

	// 3.提供一个方法,如果当前类的对象为空，创建一个新
	public static AppLockDao getInstance(Context context) {
		if (appLockDao == null) {

			appLockDao = new AppLockDao(context);
		}
		return appLockDao;
	}

	public void insert(String packagename) {
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("packagename", packagename);
		db.insert("applock", null, values);

		db.close();
		context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
	}

	public void delete(String packagename) {
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();

		db.delete("applock", "packagename = ?", new String[] { packagename });
		db.close();
		
		context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
	}

	public List<String> findAll() {
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();

		Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null);
		List<String> lockPackNameList = new ArrayList<String>();
		while(cursor.moveToNext()){
			String packagename = cursor.getString(0);
			lockPackNameList.add(packagename);
		}
		
		cursor.close();
		db.close();
		return lockPackNameList;
	}

}
