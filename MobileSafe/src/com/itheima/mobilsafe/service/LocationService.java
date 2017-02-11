package com.itheima.mobilsafe.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
    @Override
    public void onCreate() {
    	super.onCreate();
    	//获取手机的经纬度坐标
    	//获取位置管理者对象
    	LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
    	//以最优的方式获取经纬度坐标
    	Criteria criteria = new Criteria();
    	//允许花费流量
    	criteria.setCostAllowed(true);
    	//以较好的方式获取经纬度坐标
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);//指定获取经纬度的精确度
    	String bestProvider = lm.getBestProvider(criteria, true);
    	
    	MyLocationListener myLocationListener = new MyLocationListener();
    	
    	//在一定时间间隔，移动一定距离后获取经纬度坐标
    	lm.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
    }
    
    private class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			//经度
			double longitude = location.getLongitude();
			//纬度
			double latitude = location.getLatitude();
			//发送短信
			//发送短信给报警号码
			SmsManager sms = SmsManager.getDefault();
			//发送短信添加权限
			sms.sendTextMessage("5558", null, "longitude = "+longitude+"   latitude = "+latitude+"  lockscreen password = pwd123 ", null, null);

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}}
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	return super.onStartCommand(intent, flags, startId);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
	
	
	
}
