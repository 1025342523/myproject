package com.itheima.mobilesafe.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsBackUp {
	private static int index = 0;
	private static Cursor cursor;
	private static FileOutputStream fos;

	// 备份短信方法
	@SuppressWarnings("resource")
	public static void backup(Context context, String path, CallBack callback) {
		try {
			// 需要用到的对象上下文环境，备份文件夹路径，进度条所在的对话框对象用于备份过程中进度的更新
			// 1.获取备份短信写入的文件
			File file = new File(path);
			
			cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
					new String[] { "address", "date", "type", "body" }, null,
					null, null);

			fos = new FileOutputStream(file);
			// 4.序列化数据库中读取的数据，放置到xml中
			XmlSerializer newSerializer = Xml.newSerializer();
			// 5.给xml做相应的设置
			newSerializer.setOutput(fos, "utf-8");

			newSerializer.startDocument("utf-8", true);
			
			newSerializer.startTag(null, "smss");
			//备份短信总数指定
			if(callback!=null){
				callback.setMax(cursor.getCount());
			}
			//6.读取数据库中的每一行的数据写入到xml中
			while(cursor.moveToNext()){
				newSerializer.startTag(null, "sms");
				
				newSerializer.startTag(null, "address");
				newSerializer.text(cursor.getString(0));
				newSerializer.endTag(null, "address");

				newSerializer.startTag(null, "date");
				newSerializer.text(cursor.getString(1));
				newSerializer.endTag(null, "date");
				
				newSerializer.startTag(null, "type");
				newSerializer.text(cursor.getString(2));
				newSerializer.endTag(null, "type");
				
				newSerializer.startTag(null, "body");
				newSerializer.text(cursor.getString(3));
				newSerializer.endTag(null, "body");
				
				newSerializer.endTag(null, "sms");
				//每一次循环就让进度条叠加
				index ++;
				
				Thread.sleep(500);
				//ProgressDialog可以在子线程中更新相应的进度条的改变
				
				if(callback!=null){
				     callback.setProgress(index);
				}
			}
			
			
			newSerializer.endTag(null, "smss");
			
			newSerializer.endDocument();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fos!=null && cursor!=null){
				try {
					cursor.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	//回调
	//1.定义一个接口
	//2.定义接口中未实现的业务逻辑方法(短信总数设置,备份过程中短信百分比更新)
	//3.传递一个实现了此接口的类的对象(至备份短信的工具类中),接口的实现类，一定实现了上述两个未实现的方法(自己决定使用对话框,还是进度条)
	//4.获取传递进来的对象，在合适的地方(设置总数,设置百分比的地方)作方法的调用
	public interface CallBack{
		//设置短信总数未实现的方法
		public void setMax(int max);
		//备份过程中短信百分比更新
		public void setProgress(int index);
		
	}
}
