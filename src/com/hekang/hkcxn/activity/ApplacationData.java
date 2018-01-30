package com.hekang.hkcxn.activity;

import java.util.Calendar;

import android.app.Application;
import android.text.format.Time;

public class ApplacationData{
	Calendar c = Calendar.getInstance();
	private int year = c.get(Calendar.YEAR);
	private int month = c.get(Calendar.MONTH);
	private int day = c.get(Calendar.DAY_OF_MONTH);
	private int hour = c.get(Calendar.HOUR_OF_DAY);
	private int minute = c.get(Calendar.MINUTE);
    
	private String date = year+"/"+month+"/"+day;
	public String time = hour+":"+minute;
	public String getDate() {
		return date;
	}
	public String getTime() {
		return time;
	}
    
    
}
