/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
        写字楼里写字间，写字间中程序员；
		程序人员写程序，又将程序换酒钱；
		酒醒只在屏前坐，酒醉还来屏下眠；
		酒醉酒醒日复日，屏前屏下年复年；
		但愿老死电脑间，不愿鞠躬老板前；
		奔驰宝马贵者趣，公交自行程序员；
		别人笑我太疯癫，我笑自己命太贱；
		但见满街漂亮妹，哪个归得程序员.
 */
package com.hekang.hkcxn.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.hekang.R;
import com.hekang.hkcxn.BLE.BleService;
import com.hekang.hkcxn.fragmentActivity.EfficientPoint;
import com.hekang.hkcxn.help.SoftwareHelp;
import com.hekang.hkcxn.model.IsBLE;
import com.hekang.hkcxn.util.MyLogger;
import com.hekang.hkcxn.util.PublicValues;
import com.hekang.hkcxn.util.SharedPreferencesHelper;
import com.hekang.hkcxn.util.SysExitUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends MyBaseActivity {

	String TAG = "time";
	Button dingdianceliang, shujujieshou, shujuchaxun, duankouxuanze,
			tuichuruanjian, help;
	String UPDATE = "updateTime";
	private static final int CONNECT_FAIL = 4;
	private static final int CONNECT_SUCCEED_P = 5;
	private static final int MAIN = 1;
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final String TOAST = "toast";
	private BluetoothReceiver bluetoothreceiver;
	private BluetoothAdapter bluetoothAdapter;
	private String lockName = "5103D";
	private List<String> devices;
	private Set<BluetoothDevice> pairedDevices;
	SharedPreferencesHelper sp, sp2, sp3;
	public static BleService mBleService;
	public static String SERVICE_UUID ="0000FFE0-0000-1000-8000-00805F9B34FB";
	public static String CHARACTERISTIC_UUID = "0000FFE1-0000-1000-8000-00805F9B34FB";
	private static boolean mIsBind;
/***单例模式与oncreate 冲突
//	private volatile static MainActivity singleton;
//	private MainActivity(){}
//	public static MainActivity getInstance(){
//		if(singleton==null){
//			synchronized(MainActivity.class){
//				if(singleton==null){
//					singleton=new MainActivity();
//				}
//			}
//		}
//		return singleton;
//	}**/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SysExitUtils.getAppManager().addActivity(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// mService = new BluetoothService(this,mHandler);
		MyLogger.jLog().e("onCreate");
		mainActivity=this;

		findViewById();

		init();

		setOnClickListener();
	}

	private void findViewById() {

		help = (Button) findViewById(R.id.help);
		tuichuruanjian = (Button) findViewById(R.id.exit);
		shujujieshou = (Button) findViewById(R.id.receive_data);
		dingdianceliang = (Button) findViewById(R.id.multi_point_measure_a);
		shujuchaxun = (Button) findViewById(R.id.query_data);
		duankouxuanze = (Button) findViewById(R.id.adapter_equipment);
	}

	private void setOnClickListener() {
		help.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,SoftwareHelp.class);
				startActivity(intent);
			}
		});

		tuichuruanjian.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage("确定退出吗？")
						.setPositiveButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
							}
						})
						.setNegativeButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
//								SysExitUtils.getAppManager().AppExit(MainActivity.this);
								finish();
							}
						}).show();
			}
		});

		shujujieshou.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ("51S".equals(sp.getString("tanguanleixing"))) {
					newAlertDialog("请确定探管已连接适配器,并处于待机状态。");

				} else if("68A".equals(sp.getString("tanguanleixing"))){
					Intent intent = new Intent(MainActivity.this, DataReceiveActivity.class);
					startActivity(intent);
				}else{
					newAlertDialog("请确定探管已连接适配器,并处于待机状态(红灯常亮)。");
				}
			}
		});

		IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelReceiver, batteryLevelFilter);

		dingdianceliang.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TimeData.SdImeiMd5.equals(TimeData.ImeiMd5)) {
					if (TimeData.SdImeiMd5.equals(TimeData.Lsjihuoma)) {
						if (sp.getInt("LsNum") == 0) {
							Toast.makeText(getApplicationContext(), "临时激活码已失效,请重新启动软件输入激活码", Toast.LENGTH_LONG).show();
							return;
						}
					}
				}
				IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
				Intent batteryStatus = registerReceiver(null, ifilter);
				// 你可以读到充电状态,如果在充电，可以读到是usb还是交流电

				// 是否在充电
				int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
						|| status == BatteryManager.BATTERY_STATUS_FULL;
				if (getSDAvailableSize() < LIMIT_AVAILABLESIZE) {
					new AlertDialog.Builder(MainActivity.this)
							.setTitle("提示")
							.setMessage("　　储存器容量不足，请进入数据查询删除数据。")
							.setPositiveButton("取消",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									})
							.setNegativeButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									}).show();
				} else if (batteryLevel < 30 && batteryLevel >= 0) {
					new AlertDialog.Builder(MainActivity.this)
							.setTitle("提示")
							.setMessage("　　控制器电量不足，请及时进行充电。")
							.setPositiveButton("取消",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int which) {
										}
									})
							.setNegativeButton("确定",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = new Intent(
													MainActivity.this,
													CexieCeshiActivity.class);
											startActivity(intent);
											dingdianceliang.setClickable(false);
										}
									}).show();
				} else if (sp2.getString("tanguanname") == null) {
					new AlertDialog.Builder(MainActivity.this).setTitle("提示")
							.setMessage("　　即将进入仪器自检，请将探管蓝牙适配器与探管连接。")
							.setPositiveButton("取消",
									new DialogInterface.OnClickListener() {public void onClick(
												DialogInterface dialog,int which) {
										}
									})
							.setNegativeButton("确定",new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int which) {
											Intent intent = new Intent(MainActivity.this,CexieCeshiActivity.class);
											startActivity(intent);
											dingdianceliang.setClickable(false);
										}
									}).show();
				} else {
					Intent intent = new Intent(MainActivity.this, CexieCeshiActivity.class);
					startActivity(intent);
					dingdianceliang.setClickable(false);
				}
			}
		});

		shujuchaxun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, QueryDataActivity.class);
				startActivity(intent);

			}
		});

		duankouxuanze.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,DuankouxuanzeActivity.class);
				startActivity(intent);
			}
		});
		int unusual = sp.getInt("unusual");
		if (unusual == 2) {
			new AlertDialog.Builder(MainActivity.this)
					.setTitle("提示")
					.setMessage("　　是否恢复异常退出前状态？")
					.setPositiveButton("否",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int which) {
									sp.putValue("unusual", 1);
								}
							})
					.setNegativeButton("是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int which) {
									sp.putValue("unusual", 3);
									Intent intent = new Intent(MainActivity.this,EfficientPoint.class);
									startActivity(intent);
								}
							}).setCancelable(false).show();
		} else if (unusual == 1) {
		}

	}

	private void init(){
		devices = new ArrayList<String>();
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		pairedDevices = bluetoothAdapter.getBondedDevices();
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}
		sp = new SharedPreferencesHelper(getApplicationContext(), "cexiestep");
		sp2 = new SharedPreferencesHelper(getApplicationContext(), "config");
		sp3 = new SharedPreferencesHelper(this, "tdset");
		sp3.putValue("ss", "ss");
		if (sp2.getString("tanguanname") != null) {
			lockName = sp2.getString("tanguanname");
		}
		PublicValues.tanguanpinkey = sp.getString("tanguanpin");
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (lockName.equals(device.getName())) {
					Log.d("mylog", "已经配对直接连接");
					PublicValues.device = device;
					break;
				}
			}
		}

		if (!IsBLE.isBle){
			bluetoothAdapter.startDiscovery();// 搜索
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			bluetoothreceiver = new BluetoothReceiver();
			registerReceiver(bluetoothreceiver, filter);
			doBindService();
		}else {
			doBindService();
		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		MyLogger.jLog().e("onPostResume");
		if (IsBLE.isBle){
			doBindService();
		}
	}

	/**设置启动模式之后，需要重写该方法来进行intent接收参数**/
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		MyLogger.jLog().e("onNewIntent");
	}

	public void newAlertDialog(String str) {
		new AlertDialog.Builder(MainActivity.this).setTitle("提示").setMessage(str).setPositiveButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		}).setNegativeButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(MainActivity.this, DataReceiveActivity.class);
				startActivity(intent);
			}
		}).show();
	}
	public static ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBleService = ((BleService.LocalBinder) service).getService();
			if (mBleService.initialize()) {
				/**初始化成功*/
				if (mBleService.enableBluetooth(true)) {
//					mBleService.scanLeDevice(true);
//					BleListener();
				}
			} else {
				/**处理初始化失败*/
			}
			mIsBind=true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBleService = null;
			mIsBind = false;
		}
	};

	//绑定服务
	public void doBindService() {
		Intent serviceIntent = new Intent(this, BleService.class);
		bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

	}
	//解除绑定
	public  void doUnBindService() {
		if (mIsBind) {
			unbindService(serviceConnection);
			mBleService = null;
			mIsBind = false;
		}
	}

	protected void onDestroy() {
		// TODO Auto-generated method stub
		// if (mService != null) mService.stop();
		if (bluetoothreceiver != null) {
			this.unregisterReceiver(bluetoothreceiver);
		}
		if (batteryLevelReceiver != null) {
			this.unregisterReceiver(batteryLevelReceiver);
		}
		System.exit(0);
		super.onDestroy();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class BluetoothReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device == null)
					Log.d("mainactivity", "device==null");
				if (isLock(device)) {
					// devices.add(device.getName());
					bluetoothAdapter.cancelDiscovery();
					Log.d("mylog", device.getAddress());
					// connectThread = new ConnectThread(device, mHandler);
					// connectThread.start();
					// PublicValues.socket = connectThread.getSocket();
					PublicValues.device = device;
					// mService.connect(device);
					Log.d("mylog", "名字匹配成功");
				}
				// deviceList.add(device);
			}
			// showDevices();
		}
	}

	private boolean isLock(BluetoothDevice device) {
		boolean isLockName = (lockName).equals(device.getName());
		boolean isSingleDevice = devices.indexOf(device.getName()) == -1;
		return isLockName && isSingleDevice;
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				Log.d("mylog------", writeMessage);
				// mConversationArrayAdapter.add("Me:  " + writeMessage);
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		dingdianceliang.setClickable(true);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (bluetoothreceiver != null) {
			this.unregisterReceiver(bluetoothreceiver);
			bluetoothreceiver = null;
		}
		// mainview.setBackgroundResource(R.drawable.tihuan);
	}

	public Bitmap readBitMap(Context context, int resId) {

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// setBotton();
		super.onResume();
	}

	// 最小可用容量10M
	private final long LIMIT_AVAILABLESIZE = 10 * 1024 * 1024;

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return
	 */
	private long getSDAvailableSize() {

		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	int batteryLevel = 0;
	// 获得总电量
	BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			int level = -1;
			if (rawlevel >= 0 && scale > 0) {
				level = (rawlevel * 100) / scale;
				batteryLevel = level;
			}
		}
	};

	public static MainActivity mainActivity;


}
