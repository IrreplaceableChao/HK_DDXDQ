package com.hekang.hkcxn.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hekang.R;
import com.hekang.hkcxn.BLE.BleService;
import com.hekang.hkcxn.model.SaveListModel;
import com.hekang.hkcxn.util.DialogUtil;
import com.hekang.hkcxn.util.MyLogger;
import com.hekang.hkcxn.util.PublicValues;
import com.hekang.hkcxn.util.SharedPreferencesHelper;
import com.hekang.hkcxn.util.SysExitUtils;

import java.util.ArrayList;
import java.util.List;

public class DuankouxuanzeActivity extends MyBaseActivity {

    Button fanhuizhuye;
    Button search;
    EditText tanguanpin;
    List<BluetoothDevice> Devices = new ArrayList<BluetoothDevice>();
    SharedPreferencesHelper sp_config;
    TextView selector_title;
    TextView BeforeOne,BeforeOneCw;
    ListView duankoulistview;
    DuankouAdapter adapter;
    DialogUtil dialogUtil_ble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SysExitUtils.getAppManager().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duankouxuanze);

        findViewById();

        setOnClick();

        init();

    }

    private void setOnClick() {
        fanhuizhuye.setOnClickListener(new monclicklistener());
        search.setOnClickListener(new monclicklistener());
    }

    private void findViewById() {
        tanguanpin = findViewById(R.id.number);
        fanhuizhuye = findViewById(R.id.back);
        duankoulistview = findViewById(R.id.duankou_listview);
        BeforeOne = findViewById(R.id.before_one);
        BeforeOneCw = findViewById(R.id.before_one_cw);
        search = findViewById(R.id.seach);
        selector_title = findViewById(R.id.selector_title);

    }

    private void init() {
        dialogUtil_ble = new DialogUtil(DuankouxuanzeActivity.this, "正在搜索探管...");
        sp_config = new SharedPreferencesHelper(this, "config");
        BeforeOne.setText(sp_config.getString("tanguanname"));
        BeforeOneCw.setText(sp_config.getString("tanguanname_cw"));
        selector_title.setText("探管适配");
        SaveListModel.listbluetoothdevice.clear();
        SaveListModel.listDevices = new ArrayList<>();
        adapter = new DuankouAdapter();
        duankoulistview.setAdapter(adapter);
        Blecallback();
    }

    /**
     * Ble扫描回调,连接回调
     */
    private void Blecallback() {

        MainActivity.mBleService.setOnLeScanListener(new BleService.OnLeScanListener() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                //每当扫描到一个Ble设备时就会返回，（扫描结果重复的库中已处理）
                MyLogger.jLog().e(device.getName() + ":" + device.getAddress());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (device.getName() != null) {
                            if (isBLEtanguan(device.getName())) {
                                SaveListModel.listDevices.add(0, device.getName());
                                SaveListModel.listbluetoothdevice.add(0, device);
                                Devices.add(device);
                                adapter.notifyDataSetChanged();
                                dialogUtil_ble.closeDialog();
                            }
                        }
                    }
                });
            }
        });
        //Ble连接回调
        MainActivity.mBleService.setOnConnectListener(new BleService.OnConnectListener() {
            @Override
            public void onConnect(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    MyLogger.jLog().e("Ble连接已断开");
                } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                    MyLogger.jLog().e("Ble正在连接");
                } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                    MyLogger.jLog().e("Ble已连接");
                    MainActivity.mBleService.getSupportedGattServices();//获取服务
                } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                    MyLogger.jLog().e("Ble正在断开连接");
                }
            }
        });
    }

    int count_30 = 0;
    /**
     * 对搜索时间进行控制
     */
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (count_30 > 5) {
                dialogUtil_ble.closeDialog();
                stopTimer();
            } else {
                mHandler.postDelayed(runnable, 1000);
            }
            count_30++;
        }
    };


    private void startTimer() {
        mHandler.postDelayed(runnable, 1000);
    }

    private void stopTimer() {
        mHandler.removeCallbacks(runnable);
    }

    private class monclicklistener implements OnClickListener {
        @Override
        public void onClick(View arg0) {
            int id = arg0.getId();
            switch (id) {
                case R.id.back:
                    DuankouxuanzeActivity.this.finish();
                    if (sp_config.getString("tanguanname") == null) break;
                    break;
                case R.id.seach:
                    MainActivity.mBleService.scanLeDevice(true, 5000);
                    SaveListModel.listDevices.clear();
                    SaveListModel.listbluetoothdevice.clear();
                    Devices.clear();
                    adapter.notifyDataSetChanged();
                    dialogUtil_ble.showDialog();
                    startTimer();

                    break;
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MyLogger.jLog().e("" + msg.what + ":" + msg.arg1);
//            switch (msg.what) {
//                case 1:
//                    switch (msg.arg1) {
//                        case BluetoothService.STATE_CONNECTED:
//                            sp_config.putValue("tanguanname", tanguanname);
//                            sp_config.putValue("tanguanaddress", tanguanaddress);
//                            BeforeOne.setText(sp_config.getString("tanguanname"));
//                            MyLogger.jLog().e("适配成功");
//                            Toast.makeText(getApplicationContext(), "适配器适配成功。", Toast.LENGTH_SHORT).show();
//                            dialogUtil.closeDialog();
//                            break;
//
//                    }
//                    break;
//                case 5:
//                        Toast.makeText(DuankouxuanzeActivity.this, "适配器适配不成功,请检查连接！", Toast.LENGTH_LONG).show();
//                        dialogUtil.closeDialog();
//                    break;
//            }
        }
    };


    private boolean isBLEtanguan(String name) {
        return name != null && (name.contains("68A-") || name.contains("68S-"));
    }

    String Message = "";

    public class DuankouAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (SaveListModel.listDevices != null && SaveListModel.listDevices.size() != 0) {
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
                convertView = LayoutInflater.from(DuankouxuanzeActivity.this).inflate(R.layout.duankou_tablerow, null);
                holder = new ViewHolder();
                /* 得到各个控件的对象 */
                holder.text1 = (TextView) convertView.findViewById(R.id.text1);
                convertView.setTag(holder);// 绑定ViewHolder对象
            } else {
                holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
            }
            holder.text1.setText(SaveListModel.listDevices.get(position));
//            MyLogger.jLog().e("position:" + position + SaveListModel.listbluetoothdevice.get(position).getName());
//            MyLogger.jLog().e("position:" + position + SaveListModel.listbluetoothdevice.get(position).getAddress());
            Message = "您确认选择当前探管？";
            holder.text1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    new AlertDialog.Builder(DuankouxuanzeActivity.this).setTitle("提示").setMessage(Message).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            PublicValues.device = SaveListModel.listbluetoothdevice.get(position);
                            name = SaveListModel.listbluetoothdevice.get(position).getName();
                            address = SaveListModel.listbluetoothdevice.get(position).getAddress();
                            if ( name.contains("68S-")){
                                sp_config.putValue("tanguanname", name);
                                sp_config.putValue("tanguanaddress", address);
                                BeforeOne.setText(sp_config.getString("name"));
                            }else{
                                sp_config.putValue("tanguanname_cw", name);
                                sp_config.putValue("tanguanaddress_cw", address);
                                BeforeOneCw.setText(sp_config.getString("tanguanname_cw"));
                            }
                            /**
                             * 4.0蓝牙连接
                             * 点击确定储存 address信息，在自检里进行连接
                             */
                            BeforeOne.setText(sp_config.getString("tanguanname"));
                            Toast.makeText(getApplicationContext(), "探管适配成功。", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
                }
            });
            return convertView;
        }
        /* 存放控件 */
        final class ViewHolder {
            TextView text1;
        }
    }
    private String name, address;
}
