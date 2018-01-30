package com.hekang.hkcxn.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.hekang.R;
import com.hekang.hkcxn.util.MyLogger;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/5/6.
 * 数据查询
 */
public class QueryDataActivity extends Activity {
	private TextView SelectorTitle;// title
	private Button Back;

	private static final String TAG = "MainActivity";
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private Button chaxun;
	private Button fanhui;
	private RadioGroup radiops;
	private Button fanhuizhuye;
	private boolean isNum = false;  // 是否队号查询
	private Spinner spinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_data);
		findView();
		setonClick();
		init();

	}

	private void setonClick() {
		chaxun.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(QueryDataActivity.this,	DataQueryListActivity.class);
				intent.putExtra("isNum", isNum);
				if (listDevices.size() != 0) {
					intent.putExtra("duihao",listDevices.get(set1));
				}
				startActivity(intent);
			}
		});
		fanhui.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				QueryDataActivity.this.finish();
			}
		});

	}

	private void findView() {
		SelectorTitle = (TextView) findViewById(R.id.selector_title);
		chaxun = (Button) findViewById(R.id.chaxun);
		fanhui = (Button) findViewById(R.id.fanhuizhuye);
		radiops = (RadioGroup) findViewById(R.id.radiops);
		spinner = (Spinner) findViewById(R.id.Spinner);
	}

	private void init() {
		SelectorTitle.setText("数据查询");

		SDFile = Environment.getExternalStorageDirectory();
		File sdPath = new File(SDFile.getAbsolutePath() + "/HKCX-SJ");
		getAllFiles(sdPath);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name,listDevices);
		spinner.setAdapter(mNewDevicesArrayAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					set1 = arg2;
					MyLogger.jLog().e("arg0:"+arg0+"arg1:"+arg1+"arg2:"+arg2+"arg3:"+arg3);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		radiops.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if (checkedId == R.id.radioquan) {
					isNum = false;
				} else if (checkedId == R.id.radiojing) {
					isNum = true;
				}
				MyLogger.jLog().e(isNum+"");
			}

		});
		
	}
	
	
	int set1 = 0;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	
	 List<String> listDevices = new ArrayList<String>();
	private String path;
	private File SDFile;
	private void getAllFiles(File path) {
		this.path = path.getAbsolutePath();
		MyLogger.jLog().e("" + this.path);
		if (path.listFiles() == null ){
			return;
		}
			if (path.listFiles().length > 0) {
				for (File file : path.listFiles()) {
					boolean isxls = false;
					MyLogger.jLog().e("" + file);
					for (File a : file.listFiles()) {
						if ((a.toString().substring(a.toString().length()-3, a.toString().length())).equals("xls")) {
							isxls = true;
						}
					}
					if (isxls  == false) {
						continue;
					}
					if (this.path.equals(SDFile.getAbsolutePath() + "/HKCX-SJ")) {
						String allFileName = file.getName();
						allFileName = allFileName.substring(allFileName.indexOf("："),allFileName.length()); // jinghao-ceshen-20150618
						String jinghao = allFileName.substring(1,allFileName.indexOf("("));
							if (!listDevices.contains(jinghao)) {
								listDevices.add(jinghao);
							}
					}
				}
			}
	}

	// 隐藏手机键盘
	private void hideIM(View edt) {
		try {
			InputMethodManager im = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			IBinder windowToken = edt.getWindowToken();

			if (windowToken != null) {
				im.hideSoftInputFromWindow(windowToken, 0);
			}
		} catch (Exception e) {

		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private long timeString2Long(String timeStr) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}
}
