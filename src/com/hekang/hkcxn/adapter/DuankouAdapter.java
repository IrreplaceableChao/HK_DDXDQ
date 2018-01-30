package com.hekang.hkcxn.adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hekang.R;
import com.hekang.hkcxn.model.SaveListModel;

public class DuankouAdapter extends BaseAdapter {
	private Context context;
	private TextView BeforeOne;
	private Handler mHandler;

	public DuankouAdapter(Context context, TextView beforeone, Handler mHandler) {
		this.context = context;
		this.BeforeOne = beforeone;
		this.mHandler = mHandler;
	}

	@Override
	public int getCount() {
		
		if (SaveListModel.listDevices != null && SaveListModel.listDevices.size()!=0) {
			return SaveListModel.listDevices.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		// 观察convertView随ListView滚动情况
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.duankou_tablerow,null);
			holder = new ViewHolder();
			/* 得到各个控件的对象 */
			holder.text1 = (TextView) convertView.findViewById(R.id.text1);
//			holder.text2 = (TextView) convertView.findViewById(R.id.text2);
//			holder.text3 = (TextView) convertView.findViewById(R.id.text3);
			
			convertView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		
		holder.text1.setText(SaveListModel.listDevices.get(position));
		
		holder.text1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(context)  
                .setTitle("提示")
                .setMessage("更新当前选择适配器")
                .setPositiveButton("取消", new DialogInterface.OnClickListener(){
                   public void onClick(DialogInterface dialog, int which) {
                	   
                   }    
               })
               .setNegativeButton("确认", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
                	   mHandler.sendEmptyMessage(97);
                	   BeforeOne.setText(holder.text1.getText());
                   }
               })
               .show();	
			}
		});
		
//		holder.text1.setText(list.get(position).getXuhao());
//		holder.text2.setText(list.get(position).getShendu());
//		holder.text3.setText(list.get(position).getTime());
		
		return convertView;
	}

	/* 存放控件 */

	public final class ViewHolder {
		public TextView text1;
		public TextView text2;
		public TextView text3;
	}

}
