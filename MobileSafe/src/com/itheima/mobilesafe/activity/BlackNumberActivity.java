package com.itheima.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.bean.BlackNumberInfo;
import com.itheima.mobilesafe.db.dao.BlackNumberDao;
import com.itheima.mobilesafe.util.ToastUtil;


/**
 *   ListView 的优化
 *   1.复用converView
 *   2.对findViewById()的次数优化，使用viewHolder
 *   3.将viewHolder定义成静态的，不会创建多个对象
 *   4.对sql语句的优化，使用分页查询
 *   
 * @author Administrator
 *
 */
public class BlackNumberActivity extends Activity {
	
	private ListView lv_blacknumber;
	private Button btn_insert;
	private BlackNumberDao mDao;
	private List<BlackNumberInfo> mList;
	protected int mode = 1;
	private MyAdapter mAdapter;
	private boolean mIsLoad = false;
	int mCount;

	private Handler mHnadler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if(mAdapter == null){
				
				mAdapter = new MyAdapter();
				lv_blacknumber.setAdapter(mAdapter);
			}else{
				
				mAdapter.notifyDataSetChanged();
			}
		

		};
	};
	

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
		/*	View view;
			if (convertView == null) {

			} else {
				view = convertView;

			}*/
			
			//复用viewHolder步骤一
			ViewHolder holder = null;
			
			if(convertView == null){
				
				convertView = View.inflate(getApplicationContext(),
								R.layout.listview_blacknumber_item, null);
				//步骤三 
				holder = new ViewHolder();
				//步骤四
				holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
				holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
				//步骤五
				convertView.setTag(holder);
			}else{
				
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//数据库中删除
					mDao.delete(mList.get(position).getPhone());
					//集合中删除,通知数据适配器刷新
					mList.remove(position);
					//通知数据适配器刷新
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
						
					}
					
				}
			});
			
			
			holder.tv_phone.setText(mList.get(position).getPhone());

			int mode = Integer.parseInt(mList.get(position).getMode());
			switch (mode) {
			case 1:
				holder.tv_mode.setText("拦截短信");

				break;
			case 2:
				holder.tv_mode.setText("拦截电话");

				break;
			case 3:
				holder.tv_mode.setText("拦截所有");

				break;

			}

			return convertView;
		}

	}
	//步骤二
	static class ViewHolder{
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);

		initUI();
		initData();
	}

	private void initData() {
		// 获取数据库中所有的电话号码
		new Thread() {

			public void run() {
				// 获取操作黑名单数据库的对象
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				//查询部分数据
				mList = mDao.find(0);
				
				mCount = mDao.getCount();
				
				mHnadler.sendEmptyMessage(0);
			};

		}.start();
	}

	private void initUI() {
		btn_insert = (Button) findViewById(R.id.btn_insert);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		
		btn_insert.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		//监听listView的滚动状态
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {
			//滚动过程中状态发生改变调用的方法
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				OnScrollListener.SCROLL_STATE_FLING   飞速滚动
//				OnScrollListener.SCROLL_STATE_IDLE     空闲状态
//				OnScrollListener.SCROLL_STATE_TOUCH_SCROLL   拿手去触摸着的滚动状态
				
				if(mList!=null){
				//条件一:滚动到停止状态
				//条件二:最后一个条目可见 (最后一个条目的索引值 >= 数据适配器中集合的总个数 - 1)
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE 
						&& lv_blacknumber.getLastVisiblePosition() >= mList.size() - 1 
						&& !mIsLoad){
					/*mIsLoad防止重复加载的变量    因为查询是一个耗时操作
					 *如果当前正在加载, mIsLoad就会为true,本次加载完毕，再将mIsLoad改为false
					 *如果下一次加载需要去做执行的时候，会判断上述mIsLoad变量，是否为false
					 *如果为true就需要等待上一次加载完成，将其值改为false后再去加载
					 */
					//如果条目总数大于集合大小时，才可以去加载数据
					if(mCount > mList.size()){
						
						new Thread() {
							//加载下一页数据
							public void run() {
								// 获取操作黑名单数据库的对象
								mDao = BlackNumberDao.getInstance(getApplicationContext());
								//查询部分数据
								List<BlackNumberInfo> moreData = mDao.find(mList.size());
								//添加下一页数据的过程
								mList.addAll(moreData);
								//通过消息机制告知主线程可以去使用包含数据的集合
								mHnadler.sendEmptyMessage(0);
							};

						}.start();
						
					}
					
				}
			  }
			}
			//滚动过程中调用的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		
	}

	protected void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog alertDialog = builder.create();
		View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
		
		alertDialog.setView(view, 0, 0, 0, 0);
		
		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		
		Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
		
		Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
		//监听选择条目的切换过程
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:
					//拦截短信
					mode = 1;
					break;
				case R.id.rb_phone:
					//拦截电话
					mode = 2;
					break;
				case R.id.rb_all:
					//拦截所有
					mode = 3;
					break;
				}
			}
		});
		
		
		btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//获取输入框中的电话号码
				String phone = et_phone.getText().toString();
			    if(!TextUtils.isEmpty(phone)){
			    	//数据库插入当前输入的拦截号码
			    	mDao.insert(phone, mode+"");
			    	//让数据库和集合保持同步两种方式(1.重新查询一遍数据库   2.手动向集合添加一个对象(插入数据库的对象))
			    	BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			    	blackNumberInfo.setPhone(phone);
			    	blackNumberInfo.setMode(mode+"");
			    	//将对象插入到集合的最顶部
			    	mList.add(0, blackNumberInfo);
			    	//通知数据适配器刷新(数据适配器中的数据有改变了)
			    	if(mAdapter!=null){
			    		mAdapter.notifyDataSetChanged();
			    	}
			    	alertDialog.dismiss();
			    }else{
			    	ToastUtil.show(getApplicationContext(), "请输入电话号码！！");
			    	
			    }
			
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		
		alertDialog.show();
	}

}
