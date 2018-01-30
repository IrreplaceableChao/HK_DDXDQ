package com.hekang.hkcxn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hekang.R;
import com.hekang.hkcxn.model.InfoModel;

import java.text.DecimalFormat;
import java.util.List;

public class ListviewAdapter extends BaseAdapter {
	private Context context;
	private List<InfoModel> list;
	DecimalFormat DF_=new DecimalFormat("#0.00");

	public ListviewAdapter(Context context, List<InfoModel> list) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		
		if (list != null && list.size()!=0) {
			return list.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		// 观察convertView随ListView滚动情况
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.f2_tablerow,null);
			holder = new ViewHolder();
			/* 得到各个控件的对象 */
			holder.text1 = (TextView) convertView.findViewById(R.id.text1);
			holder.text2 = (TextView) convertView.findViewById(R.id.text2);
			holder.text3 = (TextView) convertView.findViewById(R.id.text3);
			holder.text4 = (TextView) convertView.findViewById(R.id.text4);
			
			convertView.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		
		holder.text1.setText(list.get(position).getXuhao());
		holder.text2.setText(list.get(position).getShendu());
		holder.text3.setText(list.get(position).getType());
		holder.text4.setText(list.get(position).getTime());
		
		return convertView;
	}

	/* 存放控件 */

	public final class ViewHolder {
		public TextView text1;
		public TextView text2;
		public TextView text3;
		public TextView text4;
	}

}
