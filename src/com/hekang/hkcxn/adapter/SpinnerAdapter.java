package com.hekang.hkcxn.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpinnerAdapter extends BaseAdapter{
	private Context context;
	private String tempstr[];
	private int num = 8;

	public SpinnerAdapter(Context context) {
		this.context = context;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return num;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public void setdata(String str[]) {
		tempstr = str;
		Log.d("sa","fuzhi"+tempstr.length);
	}

	public void setnum(int number){
		this.num = number;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		TextView result = new TextView(context);
		result.setText(tempstr[arg0]);
		result.setTextColor(Color.BLACK);
		result.setTextSize(12);
		result.setWidth(30);
		result.setLayoutParams(new AbsListView.LayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT)));
		result.setGravity(Gravity.CENTER);
		result.setBackgroundColor(Color.WHITE); //设置背景颜色
		return result;
	}

}
