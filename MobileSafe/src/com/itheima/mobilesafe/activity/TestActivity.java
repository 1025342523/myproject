package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        TextView view = new TextView(getApplicationContext());
		
        view.setText("TestActivity");
        
		setContentView(view);
	
	}
	
}
