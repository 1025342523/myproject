package com.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

public class ContactListActivity extends Activity {

	protected static final String tag = "ContactListActivity";
	
	private ListView lv_contact;
	
    private List<Map<String,String>> contactList = new ArrayList<Map<String,String>>();
	
    private MyAdapter mAdapter;
    
    private Handler mHandler = new Handler(){
    	

		public void handleMessage(android.os.Message msg) {
    		mAdapter = new MyAdapter();
    		
    		lv_contact.setAdapter(mAdapter);
    	};
    	
    };
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    setContentView(R.layout.activity_contact_list);  
	    
	    initUI();
	    initData();
	}

    private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			
			return contactList.size();
		}

		@Override
		public Map<String,String> getItem(int position) {
			
			return contactList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view ;
			if(convertView == null){
				view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
			}else{
				
				view = convertView;
			}
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			
			tv_name.setText(getItem(position).get("name"));
			tv_phone.setText(getItem(position).get("phone"));
			
			return view;
		}
    	
    	
    }
    
	/**
	 * 获取联系人数据方法
	 */
	private void initData() {
		//读取系统联系人，可能是一个耗时操作，放到子线程中处理
        new Thread(){
        	public void run() {
        		//1.获取内容解析者对象
        		ContentResolver resolver = getContentResolver();
        		
        		Cursor cursor = resolver.query(Uri.parse("content://com.android.contacts/raw_contacts"), 
        				new String[]{"contact_id"}, 
        				null, null, null);
        		
        		contactList.clear();
        		
        		while(cursor.moveToNext()){
        			String id = cursor.getString(0);
//        			Log.i(tag, id);
        			//根据用户唯一性id值。查询data表和mimetype表生成的视图，获取data1以及mimetype字段
        			Cursor cursor2 = resolver.query(Uri.parse("content://com.android.contacts/data"),
        					new String[]{"data1","mimetype"}, 
        					"raw_contact_id = ?", new String[]{id}, null);
        			
        			//循环获取每一个联系人的电话号码，以及姓名，数据类型
        			Map<String,String> hashMap = new HashMap<String,String>();
        			while(cursor2.moveToNext()){
        				
        				String data = cursor2.getString(0);
        				String mimetype = cursor2.getString(1);
        				
//        				Log.i(id, data);
//        				Log.i(tag, mimetype);
        				//区分数据类型  去填充
        				if(mimetype.equals("vnd.android.cursor.item/phone_v2")){
        					if(!TextUtils.isEmpty(data)){
        						
            					hashMap.put("phone", data);
        					}
   
        				}else if(mimetype.equals("vnd.android.cursor.item/name")){
        					if(!TextUtils.isEmpty(data)){
        						
        						hashMap.put("name", data);
        					}
        				}
        			}
        			
        			cursor2.close();
        			
        			contactList.add(hashMap);
        		}
        		cursor.close();
        		//消息机制  发送消息     子线程中不能更新UI
        		mHandler.sendEmptyMessage(0);
        		
        	};
        	
        }.start();
		
		
	}

	private void initUI() {

		lv_contact = (ListView) findViewById(R.id.lv_contact);
		lv_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//获取点中条目的索引指向集合中的对象
				if(mAdapter!=null){
					Map<String, String> map = mAdapter.getItem(position);
					//获取当前条目指向集合对应的电话号码
					String phone = map.get("phone");
				    //此电话号码需要给第三个导航界面使用
				    Intent intent = new Intent();
					intent.putExtra("phone", phone);
				    //结束此界面回到前一个导航界面的时候，需要将数据返回过去
					setResult(1, intent);
					
					finish();
				}
				
			}
		});
	}
}
