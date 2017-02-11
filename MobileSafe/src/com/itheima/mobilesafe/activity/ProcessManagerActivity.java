package com.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.bean.ProcessInfo;
import com.itheima.mobilesafe.engine.ProcessInfoProvider;
import com.itheima.mobilesafe.util.ConstantValue;
import com.itheima.mobilesafe.util.SpUtil;
import com.itheima.mobilesafe.util.ToastUtil;

public class ProcessManagerActivity extends Activity implements OnClickListener {
	private TextView tv_process_count;
	private TextView tv_memroy_info;
	private TextView tv_des;
	private ListView lv_process_list;
	private Button btn_select_all;
	private Button btn_select_reverse;
	private Button btn_setting;
	private Button btn_clear;
	private int mProcessCount;
	private List<ProcessInfo> mProcessInfoList;
	private ArrayList<ProcessInfo> mSystemList;
	private ArrayList<ProcessInfo> mCustomerList;
	private MyAdapter mAdapter;
	private ProcessInfo mProcessInfo;
	private long mAvailSpace;
	private String mStrTotalSpace;
	

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();

			lv_process_list.setAdapter(mAdapter);

			if (tv_des != null && mCustomerList != null) {
				tv_des.setText("用户进程(" + mCustomerList.size() + ")");
			}

		};
	};
	
	

	private class MyAdapter extends BaseAdapter {
		// 获取数据适配器中条目类型的总数,修改成两种(纯文本,图片+文字)
		@Override
		public int getViewTypeCount() {
			return super.getViewTypeCount() + 1;
		}

		// 指定索引指向的条目类型,条目类型状态码指定(0,1)
		@Override
		public int getItemViewType(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {
				// 返回0,代表纯文本的状态码
				return 0;
			} else {
				// 返回1,代表图片+文字
				return 1;
			}

		}

		// listView 中增加两个描述条目
		@Override
		public int getCount() {
			if(SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false)){
				return mSystemList.size() + mCustomerList.size() + 2;
			}else{
				return mCustomerList.size()+1;
				
			}
		}

		@Override
		public ProcessInfo getItem(int position) {
			if (position == 0 || position == mCustomerList.size() + 1) {

				return null;

			} else {
				if (position < mCustomerList.size() + 1) {

					return mCustomerList.get(position - 1);
				} else {
					// 返回系统应用对应条目的对象
					return mSystemList.get(position - mCustomerList.size() - 2);

				}
			}

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);

			if (type == 0) {
				// 展示灰色纯文本条目
				ViewTitleHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_app_item_title, null);

					holder = new ViewTitleHolder();
					holder.tv_title = (TextView) convertView
							.findViewById(R.id.tv_title);

					convertView.setTag(holder);
				} else {

					holder = (ViewTitleHolder) convertView.getTag();
				}
				if (position == 0) {
					holder.tv_title.setText("用户进程(" + mCustomerList.size()
							+ ")");

				} else {
					holder.tv_title.setText("系统进程(" + mSystemList.size() + ")");
				}
				return convertView;
			} else {
				// 展示图片+文字条目
				ViewHolder holder = null;
				if (convertView == null) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.listview_process_item, null);

					holder = new ViewHolder();

					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_icon);
					holder.tv_name = (TextView) convertView
							.findViewById(R.id.tv_name);
					holder.tv_memory_info = (TextView) convertView
							.findViewById(R.id.tv_memory_info);
					holder.cb_box = (CheckBox) convertView
							.findViewById(R.id.cb_box);

					convertView.setTag(holder);
				} else {

					holder = (ViewHolder) convertView.getTag();
				}
				holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
				holder.tv_name.setText(getItem(position).name);
				String strSize = Formatter.formatFileSize(
						getApplicationContext(), getItem(position).memSize);
				holder.tv_memory_info.setText(strSize);
				if (getItem(position).packageName.equals(getPackageName())) {

					holder.cb_box.setVisibility(View.GONE);
				} else {
					holder.cb_box.setVisibility(View.VISIBLE);
				}
				holder.cb_box.setChecked(getItem(position).isCheck);

				return convertView;
			}

		}

	}

	private static class ViewHolder {
		ImageView iv_icon;
		TextView tv_name;
		TextView tv_memory_info;
		CheckBox cb_box;
	}

	private static class ViewTitleHolder {
		TextView tv_title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);

		initUI();
		initData();
		initListData();
	}

	private void initListData() {
		new Thread() {

			public void run() {
				mProcessInfoList = ProcessInfoProvider
						.getProcessInfo(getApplicationContext());
				mSystemList = new ArrayList<ProcessInfo>();
				mCustomerList = new ArrayList<ProcessInfo>();

				for (ProcessInfo info : mProcessInfoList) {
					if (info.isSystem) {
						// 系统应用
						mSystemList.add(info);
					} else {
						// 用户应用
						mCustomerList.add(info);
					}
				}
				mHandler.sendEmptyMessage(0);
			};

		}.start();

	}

	private void initData() {
		mProcessCount = ProcessInfoProvider.getProcessCount(this);
		tv_process_count.setText("进程总数:" + mProcessCount);

		mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
		String strAvailSpace = Formatter.formatFileSize(
				getApplicationContext(), mAvailSpace);

		long totalSpace = ProcessInfoProvider.getTotalSpace(this);
		mStrTotalSpace = Formatter.formatFileSize(
				getApplicationContext(), totalSpace);

		tv_memroy_info.setText("剩余/总共:" + strAvailSpace + "/" + mStrTotalSpace);

	}

	private void initUI() {
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memroy_info = (TextView) findViewById(R.id.tv_memroy_info);
		lv_process_list = (ListView) findViewById(R.id.lv_process_list);

		tv_des = (TextView) findViewById(R.id.tv_des);

		btn_select_all = (Button) findViewById(R.id.btn_select_all);
		btn_select_reverse = (Button) findViewById(R.id.btn_select_reverse);
		btn_clear = (Button) findViewById(R.id.btn_clear);
		btn_setting = (Button) findViewById(R.id.btn_setting);

		btn_select_all.setOnClickListener(this);
		btn_select_reverse.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
		btn_setting.setOnClickListener(this);

		lv_process_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// 滚动过程中调用的方法
				// firstVisibleItem 第一个可见条目的索引
				// visibleItemCount 当前屏幕可见条目
				// totalItemCount 条目总数
				if (mCustomerList != null && mSystemList != null) {
					if (firstVisibleItem >= mCustomerList.size() + 1) {
						// 滚到了系统条目
						tv_des.setText("系统进程(" + mSystemList.size() + ")");
					} else {
						// 滚到了用户应用条目
						tv_des.setText("用户进程(" + mCustomerList.size() + ")");

					}
				}
			}
		});

		lv_process_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || position == mCustomerList.size() + 1) {

					return;

				} else {
					if (position < mCustomerList.size() + 1) {

						mProcessInfo = mCustomerList.get(position - 1);
					} else {
						// 返回系统应用对应条目的对象
						mProcessInfo = mSystemList.get(position
								- mCustomerList.size() - 2);
					}
					if (mProcessInfo != null) {
						if (!mProcessInfo.packageName.equals(getPackageName())) {
							// 选择条目指向的对象和本应用的报名不一致，才需要去状态取反和设置单选框状态
							// 状态取反
							mProcessInfo.isCheck = !mProcessInfo.isCheck;
							// checkBox显示状态切换
							// 通过选中条目的view对象，findViewById找到此条目指向的cb_box,然后切换其状态
							CheckBox cb_box = (CheckBox) view
									.findViewById(R.id.cb_box);

							cb_box.setChecked(mProcessInfo.isCheck);

						}

					}
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_select_all:
			selectAll();
			break;
		case R.id.btn_select_reverse:
			selectReverse();
			break;
		case R.id.btn_clear:
			Clear();
			break;
		case R.id.btn_setting:
			setting();
			break;

		}
	}
	private void setting() {
		Intent intent = new Intent(this,ProcessSettingActivity.class);
		
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//通知数据适配器刷新
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
		
		
	}
	private void Clear() {
		//获取选中进程
		// 将原有的集合中的对象上的isCheck字段取反,
		ArrayList<ProcessInfo> killProcessList = new ArrayList<ProcessInfo>();
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			if(processInfo.isCheck ){
				//被选中   
				//不能在循环中去移除集合中的对象
				//记录需要杀死的进程
				killProcessList.add(processInfo);
			}
		}
		for (ProcessInfo processInfo : mSystemList) {
			if(processInfo.isCheck ){
				//被选中   
				//不能在循环中去移除集合中的对象
				//记录需要杀死的进程
				killProcessList.add(processInfo);
			}
		}
		long totalReleaseSpace = 0;
		//循环遍历killProcessList,然后去移除mCustomerList和mSystemList中的对象
		for (ProcessInfo processInfo : killProcessList) {
			//判断当前进程在哪个集合中，从所在集合中移除
			if(mCustomerList.contains(processInfo)){
				mCustomerList.remove(processInfo);
			}
			if(mSystemList.contains(processInfo)){
				mSystemList.remove(processInfo); 
			}
			//杀死记录在killProcessList中的进程
			ProcessInfoProvider.killProcess(processInfo,getApplicationContext());
			//记录释放空间的总大小
			totalReleaseSpace += processInfo.memSize;
		}
		//7.在集合改变后，需要通知数据适配器刷新
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
		//进程总数的更新
		mProcessCount -= killProcessList.size();
		//更新可用剩余空间(释放空间，原有剩余空间 == 当前剩余空间)
		mAvailSpace += totalReleaseSpace;
		
		tv_process_count.setText("进程总数:"+mProcessCount);
		
		tv_memroy_info.setText("剩余/总共:"+Formatter.formatFileSize(getApplicationContext(), mAvailSpace)+"/"+mStrTotalSpace);
		//通过吐司告知用户，释放了多少空间，杀死了几个进程，占位符
		String strSize = Formatter.formatFileSize(getApplicationContext(), totalReleaseSpace);
		
		ToastUtil.show(getApplicationContext(), "杀死了"+killProcessList.size()+"个进程,释放了"+strSize+"的空间");
		
//		ToastUtil.show(getApplicationContext(), String.format("杀死了%d进程，释放了%s的空间", killProcessList.size(),strSize));
		
		
	}

	private void selectReverse() {
		// 将原有的集合中的对象上的isCheck字段取反,
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.getPackageName().equals(getPackageName())) {
				continue;
			}

			processInfo.isCheck = !processInfo.isCheck;
		}
		for (ProcessInfo processInfo : mSystemList) {

			processInfo.isCheck = !processInfo.isCheck;
		}
		if (mAdapter != null) {

			mAdapter.notifyDataSetChanged();
		}
	}

	private void selectAll() {
		// 将原有的集合中的对象上的isCheck字段设置为true，代表全选,排除当前应用
		for (ProcessInfo processInfo : mCustomerList) {
			if (processInfo.getPackageName().equals(getPackageName())) {
				continue;
			}

			processInfo.isCheck = true;
		}
		for (ProcessInfo processInfo : mSystemList) {

			processInfo.isCheck = true;
		}
		if (mAdapter != null) {

			mAdapter.notifyDataSetChanged();
		}

	}
}
