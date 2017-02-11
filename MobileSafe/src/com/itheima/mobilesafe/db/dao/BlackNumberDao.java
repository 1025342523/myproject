package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.db.BlackNumberOpenHelper;
import com.itheima.mobilesafe.db.bean.BlackNumberInfo;

public class BlackNumberDao {

	private BlackNumberOpenHelper blackNumberOpenHelper;

	// BlackNumberDao单例模式
	// 1.私有化构造方法
	public BlackNumberDao(Context context) {
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);

	}

	// 2.声明当前类的对象
	private static BlackNumberDao blackNumberDao = null;

	// 3.提供一个方法,如果当前类的对象为空，创建一个新
	public static BlackNumberDao getInstance(Context context) {
		if (blackNumberDao == null) {

			blackNumberDao = new BlackNumberDao(context);
		}
		return blackNumberDao;
	}

	/**
	 * 增加一个条目
	 * 
	 * @param phone
	 *            拦截的电话号码
	 * @param mode
	 *            拦截类型(1:短信 2:电话 3:拦截所有(短信 + 电话))
	 */
	public void insert(String phone, String mode) {
		// 开启数据库，准备做写入操作
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("phone", phone);
		values.put("mode", mode);

		db.insert("blacknumber", null, values);

		db.close();

	}

	/**
	 * 从数据库中删除一条电话号码
	 * 
	 * @param phone
	 */
	public void delete(String phone) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		db.delete("blacknumber", "phone = ?", new String[] { phone });

		db.close();

	}

	/**
	 * @param phone
	 *            更新拦截模式的电话号
	 * @param mode
	 *            要更新为的模式 (1:短信 2:电话 3:拦截所有)
	 */
	public void update(String phone, String mode) {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put("mode", mode);

		db.update("blacknumber", values, "phone = ?", new String[] { phone });

		db.close();

	}

	public List<BlackNumberInfo> findAll() {
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		Cursor cursor = db.query("blacknumber", new String[]{"phone","mode"}, null, null, null, null, "_id desc");
		
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			info.setPhone(cursor.getString(0)); 
			info.setMode(cursor.getString(1));
			list.add(info);
		}
		cursor.close();
		db.close();
		return list;
	}
	/**
	 * 每次查询20条数据
	 * @param index   查询的索引值
	 * @return     返回查询结果的集合
	 */
	public List<BlackNumberInfo> find(int index){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20", new String[]{index+""});
				
		List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			BlackNumberInfo info = new BlackNumberInfo();
			info.setPhone(cursor.getString(0)); 
			info.setMode(cursor.getString(1));
			list.add(info);
		}
		cursor.close();
		db.close();
		
		return list;
		
	}
	/**
	 * @return    数据库中数据的总个数，返回0代表没有数据
	 */
	public int getCount(){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
		int count = 0;
		
		if(cursor.moveToNext()){
			count = cursor.getInt(0);
			
		}
		
		cursor.close();
		db.close();
		
		return count;
	}
	/**
	 * @param phone    作为查询条件的电话号码
	 * @return     传入电话号码的拦截模式        1:短信      2:电话      3:所有
	 */
	public int getMode(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery("select mode from blacknumber where phone = ?", new String[]{phone});
		
		int mode = 0;
		
		if(cursor.moveToNext()){
			mode = cursor.getInt(0);
		}
		
		cursor.close();
		db.close();
		
		return mode;
		
	}
}
