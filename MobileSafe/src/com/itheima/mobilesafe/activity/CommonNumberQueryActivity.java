package com.itheima.mobilesafe.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.engine.CommonnumDao;
import com.itheima.mobilesafe.engine.CommonnumDao.Child;
import com.itheima.mobilesafe.engine.CommonnumDao.Group;

public class CommonNumberQueryActivity extends Activity {
	private ExpandableListView elv_common_number;
	private List<Group> mGroupList;
	private MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
			
		initUI();
		initData();
		
	}

	/**
	 * 给可扩展ListView准备数据，并且填充
	 * 
	 */
	private void initData() {
		CommonnumDao commonnumDao = new CommonnumDao();
		mGroupList = commonnumDao.getGroup();
		mAdapter = new MyAdapter();
		
		elv_common_number.setAdapter(mAdapter);
		
		//给扩展listView注册点击事件
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				startCall(mAdapter.getChild(groupPosition, childPosition).number);
				return false;
			}
		});
	}

	protected void startCall(String number) {
		//开启系统的打电话
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
		
	}

	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
	}
	
	private class MyAdapter extends BaseExpandableListAdapter{

		@Override
		public int getGroupCount() {
			return mGroupList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mGroupList.get(groupPosition).list.size();
		}

		@Override
		public Group getGroup(int groupPosition) {
			return mGroupList.get(groupPosition);
		}

		@Override
		public Child getChild(int groupPosition, int childPosition) {
			return mGroupList.get(groupPosition).list.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}
		//dip == dp
		//dpi ==ppi  像素密度 (每一英寸上分布的像素点的个数)  
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText("			"+getGroup(groupPosition).name);
			textView.setTextColor(Color.RED);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			
			return textView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
			
			tv_name.setText(getChild(groupPosition, childPosition).name);
			tv_number.setText(getChild(groupPosition, childPosition).number);
			
			
			return view;
		}
		//孩子节点是否响应事件
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}}
}
