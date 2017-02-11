package com.itheima.mobilesafe.db.bean;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	public String name;//应用名称
	public Drawable icon;//应用图标
	public long memSize;//
	public boolean isSystem;//是否是系统应用
	public boolean isCheck;//是否被选中
	public String packageName;//如果应用没有名称，则将其所在应用的包名作为名称
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public long getMemSize() {
		return memSize;
	}
	public void setMemSize(long memSize) {
		this.memSize = memSize;
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	
}
