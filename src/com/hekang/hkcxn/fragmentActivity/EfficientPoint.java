package com.hekang.hkcxn.fragmentActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.hekang.R;
import com.hekang.hkcxn.activity.MainActivity;
import com.hekang.hkcxn.model.AdapterModel;
import com.hekang.hkcxn.fragment.F1;
import com.hekang.hkcxn.fragment.F2;
import com.hekang.hkcxn.fragment.F3;
import com.hekang.hkcxn.model.IsBLE;
import com.hekang.hkcxn.util.SharedPreferencesHelper;
import com.hekang.hkcxn.util.SysExitUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 确定有效点3个fragment Created by Administrator on 2015/5/6.
 */
public class EfficientPoint extends FragmentActivity implements
		View.OnClickListener {

	private TextView tab1;
	private TextView tab2;
	private TextView tab3;

	private ViewPager viewPager;
	private List<Fragment> list = new ArrayList<Fragment>();

	private TextView SelectorTitle;						// title
	public F3 f3;

	public static boolean IsAutomatic = true;			// AndManual ;// 自动或者手动 1是自动 0是手动
	public static String filename;
	public static float ceshen = 0;
	public static float ceshen_ = 0;
	public static String ceshen_dian;
	
	private WifiManager wifiManager=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		SysExitUtils.getAppManager().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_efficient_point);
		
		/** 
         * 获取WIFI服务 
         */  
        wifiManager=(WifiManager)super.getSystemService(Context.WIFI_SERVICE);  
        if (wifiManager.isWifiEnabled()) {
        	wifiManager.setWifiEnabled(false);  
		}

		BluetoothAdapter ble = BluetoothAdapter.getDefaultAdapter();
		if (ble.isEnabled()) {
			ble.disable();
			/**蓝牙关闭，4.0蓝牙服务“可能”找不到对应的蓝牙。在接收数据后连接不上。需要卸载服务。在F3启动MainActivity重新绑定初始化服务*/
			if (IsBLE.isBle){
				MainActivity.mainActivity.doUnBindService();
			}
		}
		f3 = new F3();

		findView();

		init();
	}

	private void findView() {
		SelectorTitle = (TextView) findViewById(R.id.selector_title);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		tab1 = (TextView) findViewById(R.id.tab1);
		tab2 = (TextView) findViewById(R.id.tab2);
		tab3 = (TextView) findViewById(R.id.tab3);

	}

	SharedPreferencesHelper sp;

	private void init() {
		sp = new SharedPreferencesHelper(EfficientPoint.this, "cexiestep");
		// sp.putValue("unusual", 2);
		SelectorTitle.setText("确定采集点");
		tab3.setOnClickListener(this);
		tab2.setOnClickListener(this);
		tab1.setOnClickListener(this);
		list.add(new F1());
		list.add(new F2());
		list.add(f3);

		MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// 页面选中完毕
				System.out.println("onPageSelected:" + arg0);
				// 移动完毕
				// Toast.makeText(MainActivity.this, "当前是第" + (arg0 + 1) + "页",
				// 0)
				// .show();
				sp.putValue("arg0", arg0);

				if (arg0 == 0) {
					View view = getWindow().peekDecorView();
					if (view != null) {
						InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputmanger.hideSoftInputFromWindow(
								view.getWindowToken(), 0);
					}

					tab1.setTextColor(Color.WHITE);
					tab1.setBackgroundColor(getResources().getColor(
							R.color.background));
					// tab1.setBackgroundResource(R.color.background);
					tab2.setBackgroundColor(Color.WHITE);
					tab3.setBackgroundColor(Color.WHITE);
					tab2.setTextColor(Color.BLACK);
					tab3.setTextColor(Color.BLACK);
				} else if (arg0 == 1) {
					View view = getWindow().peekDecorView();
					if (view != null) {
						InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						inputmanger.hideSoftInputFromWindow(
								view.getWindowToken(), 0);
					}
					if (AdapterModel.adapter != null) {

						AdapterModel.adapter.notifyDataSetChanged();
					}
					tab2.setTextColor(Color.WHITE);
					tab2.setBackgroundColor(getResources().getColor(
							R.color.background));
					tab1.setTextColor(Color.BLACK);
					tab3.setTextColor(Color.BLACK);
					tab1.setBackgroundColor(Color.WHITE);
					tab3.setBackgroundColor(Color.WHITE);
				} else if (arg0 == 2) {
					tab3.setTextColor(Color.WHITE);
					tab3.setBackgroundColor(getResources().getColor(
							R.color.background));
					tab1.setTextColor(Color.BLACK);
					tab2.setTextColor(Color.BLACK);
					tab2.setBackgroundColor(Color.WHITE);
					tab1.setBackgroundColor(Color.WHITE);
					if (AdapterModel.adapter != null) {

						AdapterModel.adapter.notifyDataSetChanged();
					}
					// f3.starnotifyDataSetChanged();
				}
			}

			/*
			 * arg0=页面的索引 arg1= 方向值 0.9 0.2 arg2=移动的像素值 * @see
			 * android.support.v4
			 * .view.ViewPager.OnPageChangeListener#onPageScrolled(int, float,
			 * int)
			 */
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// 页面滑动
				// 移动中回调方法
				// System.out.println("A:" + arg0 + "--B:" + arg1 + "--C:" +
				// arg2);
				// // 通过该事件来完成 状态条的移动.
				// // 设置line的位置
				// // 首先取得宽高
				// if(arg1>0&&arg2>0){
				// MarginLayoutParams params = new MarginLayoutParams(
				// getLineWidth(), LinearLayout.LayoutParams.MATCH_PARENT);
				// //取得当前的起点
				// int w = arg0*t2;
				// params.setMargins(w+(arg2/4), 0, 0, 0);
				// line.setLayoutParams(new LinearLayout.LayoutParams(params));
				// }
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// 页面活动状态改变
				// 当移动时，arg0=state =1 ,移动完毕 arg0=0
				// if (arg0 == 0) {
				// // 停止
				// MarginLayoutParams params = new MarginLayoutParams(
				// getLineWidth(), LinearLayout.LayoutParams.MATCH_PARENT);
				// //停止时，取得当前的位置
				// int pos = viewPager.getCurrentItem();
				// if(pos==0){
				// params.setMargins(t1, 0, 0, 0);
				// }else if(pos==1){
				// params.setMargins(t2, 0, 0, 0);
				// }else if(pos==2){
				// params.setMargins(t3, 0, 0, 0);
				// }else if(pos==3){
				// params.setMargins(t4, 0, 0, 0);
				// }
				// line.setLayoutParams(new LinearLayout.LayoutParams(params));
				// } else if (arg0 == 1) {
				// // 移动中
				// }

				System.out.println("onPageScrollStateChanged:" + arg0);
			}

		});
		int unusual = sp.getInt("unusual");

		if (unusual == 3) {
			if (sp.getInt("arg0")==2) {
				viewPager.setCurrentItem(1);
			}else{
				viewPager.setCurrentItem(sp.getInt("arg0"));
			}
		}else{
			sp.putValue("unusual", 2);
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tab1) {
			viewPager.setCurrentItem(0);
		} else if (v.getId() == R.id.tab2) {
			viewPager.setCurrentItem(1);
		} else if (v.getId() == R.id.tab3) {
			viewPager.setCurrentItem(2);
		}

	}

	public class MyAdapter extends FragmentStatePagerAdapter {

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			return list.get(pos);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		// @Override
		// public CharSequence getPageTitle(int position) {
		// return titleList.get(position);
		// }
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		sp.putValue("unusual", 1);
	}
}
