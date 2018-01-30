package com.hekang.hkcxn.activity;

/**
 * 探管设置界面
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hekang.R;
import com.hekang.hkcxn.BLE.BleService;
import com.hekang.hkcxn.BLE.Modbus;
import com.hekang.hkcxn.fragmentActivity.EfficientPoint;
import com.hekang.hkcxn.model.IsBLE;
import com.hekang.hkcxn.model.SaveListModel;
import com.hekang.hkcxn.service.BluetoothService;
import com.hekang.hkcxn.tanguan.FileHead;
import com.hekang.hkcxn.util.DBHelper;
import com.hekang.hkcxn.util.DialogUtil;
import com.hekang.hkcxn.util.MyLogger;
import com.hekang.hkcxn.util.PublicValues;
import com.hekang.hkcxn.util.SharedPreferencesHelper;
import com.hekang.hkcxn.util.SysExitUtils;
import com.hekang.hkcxn.util.TGhelper;
import com.hekang.hkcxn.util.Utils;
import com.hekang.hkcxn.util.ZfSend;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TanGuanShezhiActivity extends MyBaseActivity {
private final int TXSB = 9;
    private final int STOP = 10;        // 停止检测
    private final int TG_TYPE = 11;        // 探管类型
    private final int JH = 17;            // 井号 队号 测深 垂深 延时 工作方式 设置times message state change
    private final int DH = 18;
    private final int YANSHI = 21;      //延时间隔时间
    private final int START = 111000;
    private final int CESHEN = 19;
    private final int CHUISHEN = 20;
    private final int GONGZUOFANGSHI = 22;
    private final int SZTIMES = 23;
    public final int MESSAGE_STATE_CHANGE = 1;
    public final int MESSAGE_READ = 2;
    public final int MESSAGE_WRITE = 3;
    public final int MESSAGE_DEVICE_NAME = 4;
    public final int MESSAGE_TOAST = 5;
    public final String TOAST = "toast";
    private final static byte[] hex = "0123456789ABCDEF".getBytes();
    private TGhelper thelper = new TGhelper();
    EditText duihao, jinghao, yanshishijian, jiange,quhao;
    SharedPreferencesHelper sp = null, sp2 = null, sp3 = null, share;
    ZfSend zfsend = new ZfSend();
    double d, l, b;
    double t = 0;
    int tg_type = 0;
    boolean isok = false;
    DecimalFormat formater = new DecimalFormat();
    /*
     * 0 一个数字　 # 一个数字，不包括 0　　 . 小数的分隔符的占位符　　 , 分组分隔符的占位符 　　 ; 分隔格式。 　　 -
     * 缺省负数前缀。　　 % 乘以 100 和作为百分比显示 　　 ? 乘以 1000 和作为千进制货币符显示
     */ Context context;
    Date sztime;
    SimpleDateFormat sztimestype = new SimpleDateFormat("yyyyMMddHHmmss");
    String sztimestr;
    String tanguanleixing = "";
    Button queding;
    // 蓝牙操作的一些量
    private BluetoothService mService = null;
    DBHelper helper;
    Dialog dialog;
    int count = 1;
    boolean isnormalexit = false;
    FileHead filehead = new FileHead();
    /**4.0蓝牙      时间：2016-4-5 16:41:16*/
    private static final Object obj = new Object();
    int DataLength = 0;
    int DataNum = 0;
    byte[] DATA;
    int BLE_TYPE = 0;
    DialogUtil dialogUtil;

    private Button Back;
    private TextView SelectorTitle;// title
    private TextView youxiaodiancaijishijian;//间隔时间算出来的有效点采集时间
    private TextView kecaijishujushijian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SysExitUtils.getAppManager().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_probe_test);
        if (!IsBLE.isBle) mService = new BluetoothService(this, mHandler);

        findView();

        init();

        setOnClickListenner();

        if (IsBLE.isBle) Blecallback();
    }

    private void setOnClickListenner() {

        Back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isnormalexit = true;
                if (IsBLE.isBle){
                    IsFinish =true;
                    MainActivity.mBleService.disconnect();
                }else {

                    if (mService != null) {
                        mService.stop();
                        mService = null;
                    }
                }
                Intent intent = new Intent(TanGuanShezhiActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        queding.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//
                if (!editBuilderS(duihao,"请填写队号！","请重新输入队号，只能输入5个汉字或者10个字母。",10))return;
                if (!editBuilderS(quhao,"请填写区号！","请重新输入区号，只能输入20个汉字或者40个字母。",40))return;
                if (!editBuilderS(jinghao,"请填写井号！","请重新输入井号，只能输入5个汉字或者10个字母。",10))return;
                if (yanshishijian.getText() == null || yanshishijian.getText().toString().length() == 0) {
                    editBuilder("请填写延时时间！");
                    return;
                } else {
                    int yanshitemp = Integer.parseInt(yanshishijian.getText().toString());
                    if (yanshitemp < 1 || yanshitemp > 480) {
                        editBuilder("延时时间应在1-480分钟范围内！");
                        return;
                    }
                }
                sp2.putValue("T2", Integer.parseInt(yanshishijian.getText().toString()));
                MyLogger.jLog().e("T2:" + yanshishijian.getText().toString());
                if (jiange.getText() == null || jiange.getText().toString().length() == 0) {
                    editBuilder("请填写间隔时间！");
                    return;
                } else {
                    int jiangetemp = Integer.parseInt(jiange.getText().toString());
                    switch (tanguanleixing){
                        case "68A":
                            if (jiangetemp < 2 || jiangetemp > 3600) {
                                editBuilder("间隔时间应在2-3600秒范围内！");
                                return;
                            }
                            break;
                        case "68S":
                            if (jiangetemp < 2 || jiangetemp > 3600) {
                                editBuilder("间隔时间应在2-3600秒范围内！");
                                return;
                            }
                            break;
                        default:
                            if (jiangetemp < 6 || jiangetemp > 3600) {
                                editBuilder("间隔时间应在6-3600秒范围内！");
                                return;
                            }
                            break;
                    }
                }
                sp2.putValue("T3", Integer.parseInt(jiange.getText().toString()));
                MyLogger.jLog().e("T3" + jiange.getText().toString());
                if (IsBLE.isBle) {
                    quedingAlertDialog(getString(R.string.tgsz_68A));
                } else {
                    if ("51S".equals(tanguanleixing)) {
                        quedingAlertDialog(getString(R.string.tgsz_51S));
                    } else {
                        quedingAlertDialog(getString(R.string.tgsz_51F));
                    }
                }
            }
        });
    }

    public void quedingAlertDialog(String str) {
        new AlertDialog.Builder(TanGuanShezhiActivity.this).setTitle("提示").setMessage(str).setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PublicValues.yachishijian = Integer.parseInt(yanshishijian.getText().toString());
                dialog.dismiss();
                if (!IsBLE.isBle) {
                    jingshiinfo("正在写入数据...", true);
                } else {
                    dialogUtil = new DialogUtil(TanGuanShezhiActivity.this, "正在写入数据...");
                    dialogUtil.showDialog();
                    ble_send(STOP);
                }
                writeSetInfo();
            }
        }).show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private boolean IsFinish = false;
                private final Handler mHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case MESSAGE_STATE_CHANGE:
                                if (msg.arg1 == BluetoothService.STATE_CONNECTED) {
                                    new send().start();
                                }
                                break;
                            case MESSAGE_WRITE:
                                break;
                case MESSAGE_TOAST:
                    MyLogger.jLog().e(MESSAGE_TOAST+"=====================");
                     if (count <= 3) {
                         mService.connect(PublicValues.device);
                         count++;
                     	Log.e("MESSAGE_TOAST", "MESSAGE_TOAST  	"+count);
                     } else {
                     //					tongxunshibai(R.drawable.dialogtongxunshibai);
                         jingshiinfo("", false);
                         if (Dialog_istrue) {
                         Dialogtishi();
                         Dialog_istrue=false;
                         }
                     }
                    break;
                case TXSB:
                    if(!IsFinish){
                        MyLogger.jLog().e("dialogUtil.closeDialog();");
                        dialogUtil.closeDialog();
                        MyLogger.jLog().e("BleDialogtishi();");
                        BleDialogtishi();
                    }

                    break;
                case 6:
                    MyLogger.jLog().e(6+"=====================");
                    if (!isnormalexit) {
                        jingshiinfo("", false);
                        if (Dialog_istrue) {
                            Dialogtishi();
                            Dialog_istrue = false;
                        }
                    }
                    break;
                case TG_TYPE:
                    byte[] writeBuf1 = (byte[]) msg.obj;
                    tg_type = thelper.bg_tg_type(writeBuf1[1], writeBuf1[2], writeBuf1[3]);
                    break;
                case STOP:
                    byte[] writeBufstop = (byte[]) msg.obj;
                    MyLogger.jLog().e(Arrays.toString(writeBufstop));
                    //				Log.d("zifushezhi", "stopisok");
                    //4.0也适用 isok 标志位 线程最后执行 isok=true
                    if (isok) {
                        isnormalexit = true;
                        isok = false;
                        sp2.putValue("step", 1);
                        jingshiinfo("", false);
                        if (mService != null) {
                            mService.stop();
                            mService = null;
                        }
                        Intent intent = new Intent(TanGuanShezhiActivity.this, SynchronousStartActivity.class);
                        startActivity(intent);
                    }
                    if (IsBLE.isBle) {

                        ble_send(JH);
                    }
                    break;

                case START:
                    isnormalexit = true;
                    isok = false;
                    sp2.putValue("step", 1);
                    jingshiinfo("", false);
                    if (!IsBLE.isBle) {
                        if (mService != null) {
                            mService.stop();
                            mService = null;
                        }
                    }
                    try{

                        dialogUtil.closeDialog();
                    }catch (Exception e){
                        MyLogger.jLog().e("closeDialog is null"+e);
                    }
                    Intent intent = new Intent(TanGuanShezhiActivity.this, EfficientPoint.class);
                    startActivity(intent);
                    Date time = new Date();
                    PublicValues.start = time;
                    sp2.putValue("starttime", new Date().getTime());
                    sp2.putValue("T1", new Date().getTime());
                    sp2.putValue("unusual", 1);
                    SaveListModel.listinfo = null;
                    share.clear();
                    MyLogger.jLog().e("T1" + new Date().getTime());
                    break;

                case YANSHI:
                    /**设置探管延时及间隔*/
                    if (IsBLE.isBle) {
                        ble_send(START);
                    }
                    break;
                case JH:
                    if (IsBLE.isBle) {
                        ble_send(DH);
                    }
                    break;
                case DH:
                    if (IsBLE.isBle) {
                        ble_send(YANSHI);
                    }
                    break;

                case 0x11111:
                    MainActivity.mBleService.setCharacteristicNotification(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID, true);//设置通知
                    MainActivity.mBleService.readCharacteristic(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID);//读取数据
                    ble_send(STOP);
                    break;
            }
        }
    };

    public static String Bytes2HexString(byte[] b) {
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    public static byte[] Bytes2HexBytes(byte[] b) {
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return buff;
    }

    private class send extends Thread {
        @Override
        public void run() {
            try {
                /**停止指令**/
                mService.write(zfsend.sendtingzhi(), 10);
                Thread.sleep(400);
                /**类型指令**/
                mService.write(zfsend.sendtgtype(), 11);
                Thread.sleep(400);
                /**延时间隔指令**/
                mService.write(zfsend.setTime(0, Integer.parseInt(yanshishijian.getText().toString()), Integer.parseInt(jiange.getText().toString()), TG_TYPE), YANSHI);
                Thread.sleep(300);
                /**队号指令**/
                mService.write(zfsend.setDuihao(0x10, duihao.getText().toString()), DH);
                Thread.sleep(300);
                /**井号指令**/
                mService.write(zfsend.setJinghao(0x1a, jinghao.getText().toString()), JH);
                Thread.sleep(300);
                /*******手机负者存到下位机其余不用处理设置参数时间 上位机能用到-开始，******/
                sztime = new Date();
                sp2.putValue("firsttime", sztime.getTime());
                sztimestr = sztimestype.format(sztime);
                sp2.putValue("firsttimestr", sztimestr);
                /**存储设置时间指令**/
                mService.write(zfsend.setSZtime(0x57, sztimestr), SZTIMES);
                Thread.sleep(300);
                /*******手机负者存到下位机其余不用处理设置参数时间 上位机能用到-结束，*****/
                if ("51S".equals(tanguanleixing)) {
                    mService.write(zfsend.sendstart(), START);
                    Thread.sleep(300);
                } else {
                    mService.write(zfsend.sendtingzhi(), 10);
                }
                isok = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    private void writeSetInfo() {
        sp3.putValue("quhao",quhao.getText().toString());
        sp3.putValue("duihao", duihao.getText().toString());
        sp3.putValue("jinghao", jinghao.getText().toString());
        sp3.putValue("yanshishijian", yanshishijian.getText().toString());
        sp3.putValue("jiange", jiange.getText().toString());
    }

    private int getASCnum(String str) {
        int k = 0;
        try {
            k = str.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return k;
    }

    public void findView() {
        SelectorTitle = (TextView) findViewById(R.id.selector_title);
        SelectorTitle.setText("探管设置");
        youxiaodiancaijishijian = (TextView) findViewById(R.id.text);
        kecaijishujushijian = (TextView) findViewById(R.id.text11111111111);
        queding = (Button) findViewById(R.id.queding);
        duihao = (EditText) findViewById(R.id.duihao);
        jinghao = (EditText) findViewById(R.id.jinghao);
        jiange = (EditText) findViewById(R.id.jiange);
        quhao = (EditText) findViewById(R.id.quhao);
        yanshishijian = (EditText) findViewById(R.id.yanshi);
        Back = (Button) findViewById(R.id.back);
    }

    private void init() {
        formater.setMaximumFractionDigits(1);
        formater.setGroupingSize(0);
        formater.setRoundingMode(RoundingMode.FLOOR);
        sp = new SharedPreferencesHelper(this, "zfset");
        share = new SharedPreferencesHelper(this, "mapxml");
        helper = new DBHelper(this);
        sp2 = new SharedPreferencesHelper(this, "cexiestep");
        sp3 = new SharedPreferencesHelper(this, "tdset");
        tanguanleixing = sp2.getString("tanguanleixing");

        readSetInfo();

        if ("51S".equals(tanguanleixing)||"68A".equals(tanguanleixing)||"68S".equals(tanguanleixing)) {
            queding.setText("确定并启动");
            SelectorTitle.setText("设置并启动");
        }
        if (!jiange.getText().toString().equals("")) {
            int second;
            switch (tanguanleixing) {
                case "68A":
                    second = 8000;
                    break;
                case "68S":
                    second = 8000;
                    break;
                default:
                    second = 3600;
                    break;
            }
            int sec = Integer.parseInt(jiange.getText().toString()) * (second - 1);
            kecaijishujushijian.setText("可采集数据时间:");
            youxiaodiancaijishijian.setText(Utils.ZifushezhigetTime(sec));
        }
        dialog = new Dialog(TanGuanShezhiActivity.this, R.style.MyDialog);

        jiange.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (!jiange.getText().toString().equals("")) {
                    int second;
                    switch (tanguanleixing) {
                        case "68A":
                            second = 8000;
                            break;
                        case "68S":
                            second = 8000;
                            break;
                        default:
                            second = 3600;
                            break;
                    }
                    int sec = Integer.parseInt(jiange.getText().toString()) * (second - 1);
                    kecaijishujushijian.setText("可采集数据时间:");
                    youxiaodiancaijishijian.setText(Utils.ZifushezhigetTime(sec));
                }
            }
        });
        settextChangedListener(quhao,40);
        settextChangedListener(duihao,10);
        settextChangedListener(jinghao,10);
    }
    /**获取默认值**/
    private void readSetInfo() {
        if (sp3.getString("quhao") != null) quhao.setText(sp3.getString("quhao"));
        if (sp3.getString("duihao") != null) duihao.setText(sp3.getString("duihao"));
        if (sp3.getString("jinghao") != null) jinghao.setText(sp3.getString("jinghao"));
        if (sp3.getString("yanshishijian") != null)  yanshishijian.setText(sp3.getString("yanshishijian"));
        if (sp3.getString("jiange") != null) jiange.setText(sp3.getString("jiange"));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!IsBLE.isBle) {
            if (mService == null) {
                mService = new BluetoothService(this, mHandler);
                mService.connect(PublicValues.device);
            }
        }
    }

    private void jingshiinfo(String s, boolean b) {
        TextView message;
        // 设置它的ContentView
        dialog.setContentView(R.layout.blank_dialog_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        message = (TextView) dialog.findViewById(R.id.message);
        message.setText(s);
        if (b) {
            dialog.show();
            if (!IsBLE.isBle) {
                if (mService != null) mService.stop();
                {
                    if(mService.getState()==BluetoothService.STATE_CONNECTED){
                        new send().start();
                    }else{
                        mService.connect(PublicValues.device);
                    }
                }
            }
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    }

    boolean Dialog_istrue = true;

    private void Dialogtishi() {

        new AlertDialog.Builder(TanGuanShezhiActivity.this).setTitle("提示").setMessage(getString(R.string.tongxunshibai)).setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                count = 1;
                if (mService != null) mService.stop();
                Dialog_istrue = true;
            }
        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                jingshiinfo("正在连接探管,请稍等!", true);
                count = 1;
                Dialog_istrue = true;
                if (mService != null) mService.stop();
                mService.connect(PublicValues.device);
            }
        }).show();

    }
    private AlertDialog dialog1;
    private void BleDialogtishi(){

        dialog1 = new AlertDialog.Builder(TanGuanShezhiActivity.this).setTitle("提示").setMessage(getString(R.string.tongxunshibai68A)).setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialogUtil.closeDialog();
                count = 1;
                Dialog_istrue = true;
            }
        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialogUtil.closeDialog();
                dialogUtil.showDialog();
                count = 1;
                Dialog_istrue = true;
                if (!MainActivity.mBleService.isConnect()) {
                    if (sp2.getString("tanguanaddress") != null)
                        if (!MainActivity.mBleService.connect(sp2.getString("tanguanaddress"))) {
                            mHandler.sendEmptyMessage(TXSB);
                        }
                } else {
                    MainActivity.mBleService.disconnect();
                    if (sp2.getString("tanguanaddress") != null)
                        if (!MainActivity.mBleService.connect(sp2.getString("tanguanaddress"))) {
                            mHandler.sendEmptyMessage(TXSB);
                        }
                }
            }
        }).show();

    }


    private boolean editBuilderS(EditText edit,String str,String str1,int i){
        if (edit.getText() == null || edit.getText().toString().length() == 0) {
            editBuilder(str);
            return false;
        } else if (getASCnum(edit.getText().toString()) > i) {
            editBuilder(str1);
            return false;
        }
        return  true;
    }
    private void editBuilder(String str) {
        new AlertDialog.Builder(TanGuanShezhiActivity.this).setTitle("提示").setMessage(str).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setCancelable(false).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        setBotton();
    }

    /**
     * BLE 回调
     */
    private void Blecallback() {
        MainActivity.mBleService.setOnConnectListener(new BleService.OnConnectListener() {
            @Override
            public void onConnect(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    MyLogger.jLog().e("Ble连接已断开");
                    mHandler.sendEmptyMessage(TXSB);
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
        //Ble服务发现回调
        MainActivity.mBleService.setOnServicesDiscoveredListener(new BleService.OnServicesDiscoveredListener() {
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                MyLogger.jLog().e("onServicesDiscovered");
                mHandler.sendEmptyMessage(0x11111);
            }
        });

        MainActivity.mBleService.setOnDataAvailableListener(new BleService.OnDataAvailableListener() {
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                //处理特性读取返回的数据
                MyLogger.jLog().e("onCharacteristicRead" + Arrays.toString(characteristic.getValue()));
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                synchronized (obj) {
                    if (characteristic.getValue()[0] == -1 && (characteristic.getValue()[1] == 3 || characteristic.getValue()[1] == 4 || characteristic.getValue()[1] == 0x10)) {
                        DataLength = 9;
                        DATA = new byte[DataLength];
                        for (int i = 0; i < DataLength; i++) DATA[i] = characteristic.getValue()[i];
                    } else {
                        for (int i = 0; i < characteristic.getValue().length; i++)
                            DATA[DataNum * 20 + i] = characteristic.getValue()[i];
                    }
                    DataNum++;
                    mHandler.removeMessages(BLE_TYPE);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(BLE_TYPE, DATA), 300);
                }
            }
        });
    }

    public void ble_send(int _type) {
        BLE_TYPE = _type;
        switch (_type) {
            case STOP:
                if (MainActivity.mBleService.isConnect()) {
                    MainActivity.mBleService.writeCharacteristic(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID, Modbus.sendStop());
                    MyLogger.jLog().e("连接状态,发送一个停止指令");
                } else {
                    MyLogger.jLog().e("未连接状态");
                    mHandler.sendEmptyMessage(TXSB);
                }
                break;
            case YANSHI:
                /**设置探管延时及间隔*/
                MainActivity.mBleService.writeCharacteristic(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID, Modbus.setTime(Integer.parseInt(yanshishijian.getText().toString()), Integer.parseInt(jiange.getText().toString())));
                MyLogger.jLog().e("间隔时间=：" + Arrays.toString(Modbus.setTime(Integer.parseInt(yanshishijian.getText().toString()), Integer.parseInt(jiange.getText().toString()))));
                break;
            case JH:
                MainActivity.mBleService.writeCharacteristic(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID, Modbus.setWell(jinghao.getText().toString()));
                MyLogger.jLog().e("JH=：" + Arrays.toString(Modbus.setWell(jinghao.getText().toString())));
                break;
            case DH:
                MainActivity.mBleService.writeCharacteristic(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID, Modbus.setTeam(duihao.getText().toString()));
                MyLogger.jLog().e("DH=：" + Arrays.toString(Modbus.setTeam(duihao.getText().toString())));
                break;
            case START:
                MainActivity.mBleService.writeCharacteristic(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID, Modbus.sendstart());
                MyLogger.jLog().e("启动=：" + Arrays.toString(Modbus.sendstart()));
                break;
        }
    }

    private String stringFilter(String str)throws PatternSyntaxException {
        String regEx = "[/\\:*?<>|\"\n\t()]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
    private void settextChangedListener(final EditText mEditText, final int mMaxLenth) {

//    int mMaxLenth = 200;//设置允许输入的字符长度

        mEditText.addTextChangedListener(new TextWatcher() {
            private int cou = 0;
            int selectionEnd = 0;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                cou = before + count;
                String editable = mEditText.getText().toString();
                String str = stringFilter(editable.toString()); //过滤特殊字符
                if (!editable.equals(str)) {


                    mEditText.setText(str);
                }
                if(str.contains("\\")){
                    str = str.substring(0,str.length()-1);
                    mEditText.setText(str);
                }
                mEditText.setSelection(mEditText.length());
                cou = mEditText.length();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (cou > mMaxLenth) {
//                    selectionEnd = mEditText.getSelectionEnd();
//                    s.delete(mMaxLenth, selectionEnd);
//                }
            }
        });
    }

    /**
     * 此方法必须重写，以决绝退出activity时 dialog未dismiss而报错的bug
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        try{
            dialog1.dismiss();
        }catch (Exception e) {
            System.out.println("myDialog取消，失败！");
            // TODO: handle exception
        }
        super.onDestroy();
    }
}
