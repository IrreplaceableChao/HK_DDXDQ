package com.hekang.hkcxn.activity;
/**
 * @author Administrator
 *
 */

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hekang.R;
import com.hekang.hkcxn.model.IsBLE;
import com.hekang.hkcxn.util.CTelephoneInfo;
import com.hekang.hkcxn.util.Md5;
import com.hekang.hkcxn.util.MyLogger;
import com.hekang.hkcxn.util.SharedPreferencesHelper;
import com.hekang.hkcxn.util.SysExitUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class TimeData extends MyBaseActivity {
	String UPDATE = "updateTime";
	Date date;
	Boolean isrun = true;
	ImageView main;
	Bitmap bmp;
	Button button1,button2;
	private BluetoothAdapter bluetoothAdapter;
	String User_Code; 
	String imeiSIM1;
	String imeiSIM2;
	String Imei = "000000000000000";

	public static String ImeiMd5;
	public static String SdImeiMd5="";
	AlertDialog dialog ;
	Field field;
	
	public static int LsNum = 3;
	public static String Lsjihuoma = "0123456789";
	SharedPreferencesHelper sp_cexiestep,sp_config;
	File dir ;
	TextView usertv;
	EditText useret;
	View view ;
	
	public void onCreate(Bundle savedInstanceState) {
		SysExitUtils.getAppManager().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		findViewById();

		setOnClickLisnener();

		init();

	}
	private void init() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		sp_cexiestep = new SharedPreferencesHelper(getApplicationContext(), "cexiestep");
		sp_config = new SharedPreferencesHelper(getApplicationContext(), "config");
		if(sp_config.getString("isBLE").equals("true")){
			IsBLE.isBle = true;
		}else{
			IsBLE.isBle = false;
		}
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}

		CTelephoneInfo telephonyInfo = CTelephoneInfo.getInstance(this);
		telephonyInfo.setCTelephoneInfo();
		imeiSIM1 = telephonyInfo.getImeiSIM1();
		imeiSIM2 = telephonyInfo.getImeiSIM2();
		if (imeiSIM1.length() == 15){
			Imei = imeiSIM1;
		}else if(imeiSIM2.length() == 15){
			Imei = imeiSIM2;
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		MyLogger.jLog().e(Imei);

		User_Code = Md5.HEX_ASCLL(Imei, 15);
		usertv.setText(User_Code);
		ImeiMd5 = Md5.MD5(Imei, md);
		MyLogger.jLog().e(ImeiMd5);
		Log.e("激活码","AA"+ImeiMd5+"DD");
		 /**
		/** 1.首先我查看sd路径下是否有注册码文件
		 * 2.有进行对比是否一致
		 * 3.不一致弹出提示框
***/
		dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +File.separator + "hklog");
		try{
			if (dir.listFiles().length > 0) {
				for (File file : dir.listFiles()) {
//				  MyLogger.jLog().e(file);
					if (file.toString().contains("android")) {
//						MyLogger.jLog().e(file.toString());
						LsNum = sp_cexiestep.getInt("LsNum");
						try {
							FileInputStream fis = new FileInputStream(new File(dir, "android.zcm"));
							byte[] b=new byte[fis.available()];//新建一个字节数组
							fis.read(b);//将文件中的内容读取到字节数组中
							fis.close();
							SdImeiMd5 = new String(b).toUpperCase();//再将字节数组中的内容转化成字符串形式输出
							MyLogger.jLog().e(SdImeiMd5+" is equals ture or false");
						} catch (IOException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					}else{
						if (sp_cexiestep.getInt("LsNum")!=0) {
							LsNum = sp_cexiestep.getInt("LsNum");
						}

					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		if (!SdImeiMd5.equals(ImeiMd5)) {
			if(SdImeiMd5.equals(Lsjihuoma)){
//				MyLogger.jLog().e("11"+LsNum);
				if (sp_cexiestep.getInt("LsNum")>0) {
//					MyLogger.jLog().e(LsNum);
					Toast.makeText(getApplicationContext(), "正在使用临时激活码，剩余"+LsNum+"次！", Toast.LENGTH_LONG).show();
				}else{
					dialog();
				}
			}else{
				dialog();
			}
		}

//		Start();
	}


	private void setOnClickLisnener() {
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TimeData.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});

		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(TimeData.this)
						.setTitle("提示")
						.setMessage("确定退出吗？")
						.setNegativeButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int which) {
										// SysExitUtil.getAppManager().finishAllActivity();
										SysExitUtils.getAppManager().AppExit(
												TimeData.this);
										// Const.exit();
										// System.exit(0);
										// ActivityManager activityMgr=
										// (ActivityManager)
										// MainActivity.this.getSystemService(ACTIVITY_SERVICE
										// );
										// activityMgr.restartPackage(getPackageName());
									}
								})
						.setPositiveButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
														int which) {

									}
								}).setCancelable(false).show();
			}
		});
	}

	private void findViewById() {
		view = getLayoutInflater().inflate(R.layout.user_code,null);
		usertv = (TextView) view.findViewById(R.id.textView1);
		useret = (EditText) view.findViewById(R.id.editText1);
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		isrun = false;
		super.onDestroy();
		// android.os.Process.killProcess(android.os.Process.myPid());
		// ActivityManager activityMgr= (ActivityManager)
		// getSystemService(ACTIVITY_SERVICE );
		// activityMgr.killBackgroundProcesses(getPackageName());
		super.onDestroy();
		System.gc();
	}

	public void Start() {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}.start();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void dialog(){
		dialog = new AlertDialog.Builder(this)
		.setTitle("请输入激活码").setIcon(android.R.drawable.ic_dialog_info)
		.setView(view)		
		.setPositiveButton("注册", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				try {
					
					field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
					field.setAccessible(true); 	//设置mShowing值，欺骗android系统 
                     field.set(dialog, false); //需要关闭的时候将这个参数设置为true 他就会自动关闭了
				}catch(Exception e) { 
					e.printStackTrace(); 
				}
				if (useret.getText().toString().toUpperCase().equals(ImeiMd5)) {
					   if (!dir.exists())  
		                    dir.mkdir();  
		                try {
							FileOutputStream fos = new FileOutputStream(new File(dir, "android.zcm"));
							fos.write(useret.getText().toString().toUpperCase().getBytes());  
				            fos.close();  
						} catch (Exception e1) {
							e1.printStackTrace();
						}  
						Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_LONG).show();
						try {
							field.set(dialog, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
				}else if(useret.getText().toString().toUpperCase().equals(Lsjihuoma)){
					
					if (LsNum < 1) {
						Toast.makeText(getApplicationContext(), "临时注册失败", Toast.LENGTH_LONG).show();
						return;
					}
					  if (!dir.exists())  
		                    dir.mkdir();  
		                try {
							FileOutputStream fos = new FileOutputStream(new File(dir, "android.zcm"));
							fos.write(useret.getText().toString().toUpperCase().getBytes());  
				            fos.close();  
						} catch (Exception e1) {
							e1.printStackTrace();
						}  
		                sp_cexiestep.putValue("LsNum", LsNum);
						Toast.makeText(getApplicationContext(), "临时注册成功，剩余"+LsNum+"次！", Toast.LENGTH_LONG).show();
						try {
							field.set(dialog, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
				}else{
					
					Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_LONG).show();
				}
			}
		}).setCancelable(false).show();
	}

	private void imporDatabase() {
		// 存放数据库的目录
		String dirPath = "/data/data/com.hekang.hkcxn/databases";
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdir();
		}
		// 数据库文件
		File file = new File(dir, "hk.db");
		try {
			if (!file.exists()) {
				file.createNewFile();
				// 加载需要导入的数据库
				InputStream is = this.getApplicationContext().getResources()
						.openRawResource(R.raw.hk);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffere = new byte[is.available()];
				is.read(buffere);
				fos.write(buffere);
				is.close();
				fos.close();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
