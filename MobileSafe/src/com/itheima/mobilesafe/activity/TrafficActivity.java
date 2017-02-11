package com.itheima.mobilesafe.activity;

import com.itheima.mobilesafe.R;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;

public class TrafficActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_traffic);
		//获取流量(R  手机(2G,3G,4G)下载流量)
		long mobileRxBytes = TrafficStats.getMobileRxBytes();
		
		//(T  total(手机(2G,3G,4G)总流量(上传+下载)))
		long mobileTxBytes = TrafficStats.getMobileTxBytes();
		
		//total(下载流量总和(手机+wifi))
		long totalRxBytes = TrafficStats.getTotalRxBytes();
		
		//总流量(手机+wifi),(上传+下载)
		long totalTxBytes = TrafficStats.getTotalTxBytes();
		
		
	}
}
