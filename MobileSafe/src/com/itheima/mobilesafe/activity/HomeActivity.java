package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.MD5Utils;
import com.itheima.mobilesafe.util.SpUtil;
import com.itheima.mobilesafe.util.ToastUtil;

public class HomeActivity extends Activity {

	private GridView gv_home;
	private String[] mTitleStrs;
	private int[] mDrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		//初始化UI的方法
		initUI();
		//初始化数据的方法
		initData();
	}

	private void initData() {
		//准备数据(文字(九组))图片(9张)
		
	mTitleStrs = new String[]{
			"手机防盗","通信卫士","软件管理",
			"进程管理","流量统计","手机杀毒",
			"缓存清理","高级工具","设置中心"
	};
		
	 mDrawableIds = new int[]{
			 R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps,
			 R.drawable.home_taskmanager,R.drawable.home_netmanager,R.drawable.home_trojan,
			 R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings 
	 };
	
	 gv_home.setAdapter(new MyAdapter());
	 //注册九宫格单个条目点击事件
	 gv_home.setOnItemClickListener(new OnItemClickListener() {
		 //position  点击条目的索引
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				//开启对话框
				showDialog();
				
				break;
			case 1:
				startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
				break;
			case 2:
				startActivity(new Intent(getApplicationContext(),AppManagerActivity.class));
				
				break;
			case 3:
				startActivity(new Intent(getApplicationContext(),ProcessManagerActivity.class));
				break;
			case 4:
				
				startActivity(new Intent(getApplicationContext(),TrafficActivity.class));
				break;
			case 5:
				startActivity(new Intent(getApplicationContext(),AnitVirusActivity.class));
				break;
			case 6:
//				startActivity(new Intent(getApplicationContext(),CacheClearActivity.class));
				startActivity(new Intent(getApplicationContext(),BaseCacheClearActivity.class));
				break;
			case 7:
				Intent intent2 = new Intent(getApplicationContext(),AToolActivity.class);
				
				startActivity(intent2);
				
				break;
			case 8:
				Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
				
				startActivity(intent);
				break;

			
			}
			
		}
	});
	}
    public void showDialog(){
    	//判断本地是否有存储密码(sp)
    	String mobilsafe_pwd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBIL_SAFE_PWD, "");
    	if(TextUtils.isEmpty(mobilsafe_pwd)){
    		//初始设置密码对话框
    		showSetPwdDialog();
    	}else{
    		//确认密码对话框
    		showConfirmPwdDialog();
    		
    	}

    }
	
	
	/**
	 * 确认密码对话框
	 * 
	 */
	private void showConfirmPwdDialog() {
		//因为需要自己去定义对话框的样式，所有需要调用dialog.setView(view)
				//view 自己编写的xml转换成的view对象
				Builder builder = new AlertDialog.Builder(this);
				final AlertDialog dialog = builder.create();
				
				final View view = View.inflate(getApplicationContext(), R.layout.dialog_confirm_pwd, null);
				//让对话框显示自己定义的效果
//				dialog.setView(view);
				//向低版本兼容    把对话框的内边距设置为0
   				dialog.setView(view, 0, 0, 0, 0);
                 
				dialog.show();
				Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
				Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
				
				bt_submit.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						EditText et_confirm_pwd = (EditText) view.findViewById(R.id.et_confirm_pwd);
						
						String confirmPwd = et_confirm_pwd.getText().toString();
						
						if(!TextUtils.isEmpty(confirmPwd)){
							    //将存储在sp中32位的密码，获取出来，然后将输入的密码同样进行MD5加密，然后与sp中的密码进行比较
							    String pwd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBIL_SAFE_PWD, "");
							
							  if(pwd.equals(MD5Utils.encoder(confirmPwd))){
								  //进入手机防盗模块，开启一个新的activity
								  
								  Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
								  
								  startActivity(intent);
								  
								  //跳到新界面以后需要隐藏对话框
								  dialog.dismiss();
								  
								  
							  }else{
								  
								  ToastUtil.show(getApplicationContext(), "确认密码有误"); 
		                          return ;
							  }
						}else{
							
							ToastUtil.show(getApplicationContext(), "密码不能为空");
							
							return;
						}
						
					}
				});
				
				bt_cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
		
	}

	/**
	 * 设置密码对话框
	 * 
	 */
	private void showSetPwdDialog() {
		
		//因为需要自己去定义对话框的样式，所有需要调用dialog.setView(view)
		//view 自己编写的xml转换成的view对象
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		
		final View view = View.inflate(getApplicationContext(), R.layout.dialog_set_pwd, null);
		//让对话框显示自己定义的效果
//		dialog.setView(view);   
		//向低版本兼容   把内边距设置为0
		dialog.setView(view, 0, 0, 0, 0);

		dialog.show();
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		
		bt_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText et_set_pwd = (EditText) view.findViewById(R.id.et_set_pwd);
				EditText et_confirm_pwd = (EditText) view.findViewById(R.id.et_confirm_pwd);
				
				String pwd = et_set_pwd.getText().toString();
				String confirmPwd = et_confirm_pwd.getText().toString();
				
				
				
				if(!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(confirmPwd)){
					  if(pwd.equals(confirmPwd)){
						  //进入手机防盗模块，开启一个新的activity
						  
//						  Intent intent = new Intent(getApplicationContext(),TestActivity.class);
						  Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
						  
						  startActivity(intent);
						  
						  //跳到新界面以后需要隐藏对话框
						  dialog.dismiss();
						  
						  SpUtil.putString(getApplicationContext(), ConstantValue.MOBIL_SAFE_PWD,MD5Utils.encoder(pwd));
						  
					  }else{
						  
						  ToastUtil.show(getApplicationContext(), "确认密码有误"); 
                          return ;
					  }
				}else{
					
					ToastUtil.show(getApplicationContext(), "密码不能为空");
					
					return;
				}
				
			}
		});
		
		bt_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		
	}

	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);
		
	}
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mTitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			return mTitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = View.inflate(getApplicationContext(),R.layout.gridview_item , null);
			
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			
			tv_title.setText(mTitleStrs[position]);
			
			iv_icon.setBackgroundResource(mDrawableIds[position]);
			
			return view;
		}}
}
