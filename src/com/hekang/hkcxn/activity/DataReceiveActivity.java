package com.hekang.hkcxn.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.hekang.R;
import com.hekang.hkcxn.BLE.BleService;
import com.hekang.hkcxn.BLE.Modbus;
import com.hekang.hkcxn.fragmentActivity.EfficientPoint;
import com.hekang.hkcxn.model.InfoModel;
import com.hekang.hkcxn.model.IsBLE;
import com.hekang.hkcxn.model.SaveListModel;
import com.hekang.hkcxn.service.BluetoothService;
import com.hekang.hkcxn.tanguan.FileHead;
import com.hekang.hkcxn.tanguan.ReceiveTanguan;
import com.hekang.hkcxn.tanguan.T_Sg;
import com.hekang.hkcxn.tanguan.TanGuan;
import com.hekang.hkcxn.tanguan.TanguanBuChang;
import com.hekang.hkcxn.util.CreateExcel;
import com.hekang.hkcxn.util.DialogUtil;
import com.hekang.hkcxn.util.FileUtils;
import com.hekang.hkcxn.util.MyLogger;
import com.hekang.hkcxn.util.Objiectinout;
import com.hekang.hkcxn.util.PublicValues;
import com.hekang.hkcxn.util.SharedPreferencesHelper;
import com.hekang.hkcxn.util.ShuJuJieShou;
import com.hekang.hkcxn.util.SysExitUtils;
import com.hekang.hkcxn.util.TGhelper;
import com.hekang.hkcxn.util.Utils;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 接收数据
 * Created by ACHAO on 2015/5/6.
 */
public class DataReceiveActivity extends MyBaseActivity {
    public final int TXSB = 9;                    //通讯失败
    public final int STOP = 10;                   //停止检测
    public final int TG_TYPE = 11;                //探管类型
    public final int GBXZ = 12;                   //高边修正
    public final int GZZZ = 13;                   //工作状态
    public final int XZXS = 14;                   //修正系数
    public final int TGJC = 15;                   //探管检测
    public final int BCXS = 16;                   //补偿系数
    public final int GZFS = 17;                   //工作方式
    public final int ALLSHUJU = 18;               // 采集数据
    public final int DJCG = 102;                  // 队号、井号、测深、高边修正值
    public final int SDZ = 105;                   // 时间、点数、状态
    public final int MESSAGE_STATE_CHANGE = 1;
    public final int MESSAGE_READ = 2;
    public final int MESSAGE_WRITE = 3;
    public final int MESSAGE_DEVICE_NAME = 4;
    public final int MESSAGE_TOAST = 5;
    private TextView SelectorTitle;            // title
    private Button Back;
    private HorizontalScrollView hs;
    private final int MESSAGESTART = 1096;      // 显示
    private final int MESSAGEEND = 1097;        // 消失
    public final String TOAST = "toast";
    private final static byte[] hex = "0123456789ABCDEF".getBytes();
    ProgressBar pb_data_receive;
    private BluetoothService mService = null;
    int tg_type = 0;
    short dizhi = 0;
    int xsbiaoji = 1;
    int bcbiaoji = 1;
    int start, end;                                // 开始地址和结尾地址
    int bleStart = 249, bleEnd = 0;                // 开始地址和结尾地址
    SharedPreferencesHelper sp_cexiestep, shared, sp_teset, sp_config;
    TanGuan all_info_bt_sj = null;
    int q_max_point, q_ys_time;                    // 采集点数   延时时间
    String binaryfilename_CJ, filename_excel;//文件名称
    long mmm;
    String time;
    boolean b = true, isSave = true;
    float xiuzhengvalue = 0, tempxiuzhengvalue, tempgongjumian;
    int count = 0;
    int kk;                                        //采集点数
    int cishu = 1;                                //进度条刻度
    boolean isexit = true;
    private boolean isyouxiao = false;
    Dialog dialog;
    int gongzuofangshi = 0;                        //单点为0，定点为1，多点为2；
    int list_ys = 0;
    String buchangxishu_tg_type = "";
    int caiji_second;
    int T3;
    private ShuJuJieShou thelper = new ShuJuJieShou();
    ReceiveTanguan receive = new ReceiveTanguan();    //接收类  float 井斜   磁强度。。。
    short c_xs[] = new short[301];
    short c_bcxs[] = new short[301];                    //补偿系数
    float c_d[] = new float[44];                        //存 44个修正系数
    /**
     * 存了补偿系数   每个类 4个变量 a1a2a3a4    new  6个       24个补偿系数  都是算好的   补偿系数   出来了。
     */
    TanguanBuChang t_bc[] = {new TanguanBuChang(), new TanguanBuChang(), new TanguanBuChang(), new TanguanBuChang(), new TanguanBuChang(), new TanguanBuChang()};
    TanGuan tanguan = new TanGuan();
    TanGuan tanguan1 = new TanGuan();
    long[] startAndendtime = new long[2];
    DecimalFormat df = new DecimalFormat("00");
    DecimalFormat df1 = new DecimalFormat("000");
    DecimalFormat df2 = new DecimalFormat("#.000");
    DecimalFormat df5 = new DecimalFormat("#0.00");
    DecimalFormat df6 = new DecimalFormat("#0.00");
    DecimalFormat df3 = new DecimalFormat("#0.00");
    DecimalFormat df4 = new DecimalFormat("#0.000");
    DecimalFormat df7 = new DecimalFormat("#0.00");
    SimpleDateFormat filedateformat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat filedateformat_SJ = new SimpleDateFormat("yyMMdd");
    T_Sg q_bt_sj[] = new T_Sg[16];
    send send1 = new send();
    List<Object> list = new ArrayList<Object>();   //
    FileHead filehead = new FileHead();
    public Objiectinout objectInOut = new Objiectinout();
    private List<Map<String, String>> excledatas = new ArrayList<Map<String, String>>();
    private List<Map<String, String>> excledatas_all = new ArrayList<Map<String, String>>();
    private File SDFile;
    /******************************************
     * BLE
     ****************************************/
    DialogUtil dialogUtil;
    private static final Object obj = new Object();
    int DataLength = 0, DataNum = 0, BLE_TYPE = 0;
    byte[] DATA;
boolean IsContains;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SysExitUtils.getAppManager().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_receive);
        findView();
        setOnClickListener();
        Blecallback();
        init();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void findView() {
        SelectorTitle = (TextView) findViewById(R.id.selector_title);
        Back = (Button) findViewById(R.id.back);
        pb_data_receive = (ProgressBar) findViewById(R.id.pb_data_receive);
        hs = (HorizontalScrollView) findViewById(R.id.hs);
        hs.scrollTo(0, 200);
    }

    private void setOnClickListener() {
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SDFile = Environment.getExternalStorageDirectory();
                File sdpath = new File(SDFile.getAbsolutePath() + "/HKCX-SJ");
                getAllFiles(sdpath);
                if (isSave) {
                    isexit = false;
                    if (mService != null) mService.stop();
                    DataReceiveActivity.this.finish();
                }
            }
        });
    }

    private void init() {
        for (int i = 0; i < 16; i++) {
            q_bt_sj[i] = new T_Sg();
        }
        SelectorTitle.setText("数据接收");
        sp_cexiestep = new SharedPreferencesHelper(DataReceiveActivity.this, "cexiestep");
        sp_teset = new SharedPreferencesHelper(DataReceiveActivity.this, "tdset");
        sp_config = new SharedPreferencesHelper(this, "config");
        shared = new SharedPreferencesHelper(this, "mapxml");
        buchangxishu_tg_type = sp_cexiestep.getString("tanguanleixing");
        caiji_second = sp_cexiestep.getInt("caiji_second");
        T3 = sp_cexiestep.getInt("T3");
        dialog = new Dialog(DataReceiveActivity.this, R.style.MyDialog);

        if (EfficientPoint.ceshen == 0) {
            int conunt_i = shared.getInt("i");
            EfficientPoint.ceshen = sp_teset.getFloat("et_ceshen");
            SaveListModel.listinfo = new ArrayList<InfoModel>();
            for (int i = 1; i <= conunt_i; i++) {
                InfoModel info = new InfoModel();
                info.setXuhao(shared.getString("xuhao" + i));
                info.setShendu(shared.getString("shendu" + i));
                info.setTime(shared.getString("time" + i));
                info.setType(shared.getString("type" + i));
                int countxml = shared.getInt("count" + i);
                info.setNum(countxml);
                MyLogger.jLog().e(shared.getString("xuhao" + i) + "shendu:" + (shared.getString("shendu" + i)) +
                        "time:" + shared.getString("time" + i) + "countxml:" + countxml);
                SaveListModel.listinfo.add(info);
            }
        }
        init_();
    }
    private void init_(){
        dialogUtil = new DialogUtil(DataReceiveActivity.this, "正在连接探管,请稍等!");
        dialogUtil.showDialog();


        if (!IsBLE.isBle) {
            mService = new BluetoothService(this, mHandler);
            if (PublicValues.device != null && PublicValues.device.getBondState() != BluetoothDevice.BOND_BONDED) {
                PublicValues.pinkey = PublicValues.tanguanpinkey;
            }
            mService.connect(PublicValues.device);
        } else {
            /*************BLE开始**********/
            if (!MainActivity.mBleService.isConnect()) {
                MyLogger.jLog().e(sp_config.getString("tanguanaddress"));
                if (sp_config.getString("tanguanaddress") != null) {
                    if (MainActivity.mBleService.connect(sp_config.getString("tanguanaddress"))) {

                    } else {
                        MainActivity.mBleService.connect(sp_config.getString("tanguanaddress"));
                    }

                }
            }
            /*************BLE结束**********/
        }
    }

    /**
     * send结束   ---->     10 停止指令  11 读取探管类型
     * Normal     ---->     SDZ  => if 探管（12   13）  for循环获取有补偿系数 =>循环采集 数据（应该是采集固定几个点数据）
     * WriteAll   ---->     读取所有数据
     */
    private class send extends Thread {
        int i;
        boolean isok = false;

        @Override
        public void run() {
            // 停止 读取探管型号
            try {
                Thread.sleep(300);
                mService.write(thelper.bg_send(10, 0), 10);
                Thread.sleep(400);
                mService.write(thelper.bg_send(11, 0), 11);
                MyLogger.jLog().e("发送获取探管类型指令");
                //13未知暂时没有用
//                mService.write(thelper.bg_send(13, 0), 13);
                Thread.sleep(300);
                //工作方式
                mService.write(thelper.bg_send(16, 0x01a2), GZFS);
                Thread.sleep(300);
                //修正系数
                for (dizhi = 0x120; dizhi <= 0x1e0; dizhi += 0x20) {
                    mService.write(thelper.bg_send(XZXS, dizhi), XZXS);
                    Thread.sleep(300);
                }
                //队号、井号、测深、高边修正值
                mService.write(thelper.bg_send(DJCG, 0x10), DJCG);//
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
                MyLogger.jLog().e(e);
            }
//            new Normal().start();
        }
    }

    public class Normal extends Thread {
        public void run() {
            try {
                mService.write(thelper.bg_send(SDZ, 0x0), SDZ);
                Thread.sleep(300);
                //补偿系数
                if (buchangxishu_tg_type.equals("51S")) {
                    tg_type = 13;
                } else if (buchangxishu_tg_type.equals("51F")) {
                    tg_type = 12;
                }
                if (tg_type == 12 || tg_type == 13) {
                    for (dizhi = 0xa0; dizhi <= 0xe0; dizhi += 0x20) {
                        mService.write(thelper.bg_send(16, dizhi), 16);
                        Thread.sleep(300);
                    }
                }
                Thread.sleep(300);
                start = 0x200;
                WriteAll(start);
            } catch (InterruptedException e) {
                e.printStackTrace();
                MyLogger.jLog().e(e);
            }
        }
    }

    public void WriteAll(int start) {
        try {
            Thread.sleep(50);
            mService.write(thelper.bg_send(ALLSHUJU, start), ALLSHUJU);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    int connect = 0;
    /*********************************************************** ble   start ********************************************************/
    private Handler mBleHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                DataNum = 0;
                byte[] writeBuf = (byte[]) msg.obj;
                MyLogger.jLog().e(Arrays.toString(writeBuf));
                short[] temp = null;
                if (msg.what != 0x11111  || msg.what != 0x11112 || msg.what != TXSB) {
                    if (writeBuf != null) {
                        temp = getbytes(writeBuf);
                    }
                }
                switch (msg.what) {
                    case TXSB:

                        dialogUtil.closeDialog();
                        String strtx = getString(R.string.tongxunshibai68A);
                        new AlertDialog.Builder(DataReceiveActivity.this).setTitle("提示").setMessage(strtx).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.mBleService.disconnect();
                                DataReceiveActivity.this.finish();
                            }
                        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                MainActivity.mBleService.disconnect();
                                DataReceiveActivity.this.finish();
                            }
                        }).show();
                        Back.setVisibility(View.VISIBLE);

                        break;
                    case TG_TYPE:
                        if (writeBuf[1] == (int) '6' && writeBuf[2] == (int) '8') {
                            tg_type = thelper.bg_tg_type(writeBuf[1], writeBuf[2], writeBuf[3]);
                            MyLogger.jLog().e("探管类型tg_type：68A=" + tg_type);
                        }
                        ble_send(GZFS, 0);
                        break;
                    case GZFS:
                        ble_send(XZXS, 110);
                        break;
                    case XZXS:
                        if (!Modbus.CRCIsTrue(writeBuf)) {
                            ble_send(XZXS, 110);
                            break;
                        }
                        System.arraycopy(temp, 3, c_xs, 0, temp.length - 3);
                        filehead.tanguanbianhao = thelper.getTGName(gongzuofangshi, "" + (char) (c_xs[140]) + (char) (c_xs[141]) + (char) (c_xs[142]) + "-" + df1.format(c_xs[144] * 256 + c_xs[143]));
                        MyLogger.jLog().e(filehead.tanguanbianhao);
                        c_d = thelper.getXiuZhengXiShu(c_xs);
                        list.add(c_d);
                        ble_send(BCXS, 47);
                        break;
                    case BCXS:
                        if (!Modbus.CRCIsTrue(writeBuf)) {
                            ble_send(BCXS, 47);
                            break;
                        }
                        System.arraycopy(temp, 3, c_bcxs, 0, temp.length - 3);
                        t_bc = thelper.getBuChangXiShu(c_bcxs);
                        ble_send(DJCG, 0);
                        break;
                    case DJCG:
                        if (!Modbus.CRCIsTrue(writeBuf)) {
                            ble_send(DJCG, 0);
                            break;
                        }
                        MyLogger.jLog().e(Arrays.toString(writeBuf));
                        byte[] k1 = new byte[10];
                        float p;
                        System.arraycopy(writeBuf, 3, k1, 0, 10);
                        filehead.duihao =  new String(k1, "GBK").trim();
                        if (filehead.duihao.contains("*")){
                            filehead.duihao = filehead.duihao.substring(1,filehead.duihao.length());
                        }
                        System.arraycopy(writeBuf, 13, k1, 0, 10);
                        filehead.jinghao = new String(k1, "GBK").trim();
                        filehead.quhao = sp_teset.getString("quhao");
                        FileUtils.sdfilepath = shared.getString("FileUtils.sdfilepath");
//                        FileUtils.sdfilepath = Environment.getExternalStorageDirectory().getPath() + "/HKCX-SJ/"+ riqi + "/";
//
                        MyLogger.jLog().e(shared.getString("FileUtils.sdfilepath"));
                        EfficientPoint.ceshen_ = shared.getFloat("EfficientPoint.ceshen_");

                        SDFile = Environment.getExternalStorageDirectory();

                        if (filehead.quhao.equals("")){
                            filename_excel =  filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime")));
                            binaryfilename_CJ = filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime"))) + ".CJ";

                            FileUtils.sdfilepath=SDFile.getPath()+  "/HKCX-SJ/："+ filename_excel + "/";
                        }else{
                            filename_excel = filehead.quhao + "：" + filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime")));
                            binaryfilename_CJ = filehead.quhao + "：" + filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime"))) + ".CJ";

                            FileUtils.sdfilepath=SDFile.getPath()+  "/HKCX-SJ/"+ filename_excel + "/";

                        }


                                File sdpath = new File(SDFile.getAbsolutePath() + "/HKCX-SJ");
                        if (sdpath.listFiles() != null ){
                            if (sdpath.listFiles().length > 0) {
                                for (File file : sdpath.listFiles()) {
                                    if (file.getCanonicalPath().contains(filename_excel)){
                                        new AlertDialog.Builder(DataReceiveActivity.this)
                                                .setTitle("提示")
                                                .setMessage("       有相同文件名的数据已存在  1.关于[覆盖],则覆盖原有数据。2.关于[另存],则在原有测深增加0.01米后另存")
                                                .setPositiveButton("另存", new DialogInterface.OnClickListener(){
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        EfficientPoint.ceshen += 0.01;
                                                        shared.putValue("EfficientPoint.ceshen_",EfficientPoint.ceshen);
                                                        if (filehead.quhao.equals("")){
                                                            filename_excel =  filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime")));
                                                            binaryfilename_CJ = filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime"))) + ".CJ";
                                                            FileUtils.sdfilepath = SDFile.getPath() + "/HKCX-SJ/："+ filename_excel + "/";

                                                        }else {
                                                            filename_excel = filehead.quhao + "：" + filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime")));
                                                            binaryfilename_CJ = filehead.quhao + "：" + filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime"))) + ".CJ";
                                                            FileUtils.sdfilepath = SDFile.getPath() + "/HKCX-SJ/"+ filename_excel + "/";
                                                        }
                                                        shared.putValue("FileUtils.sdfilepath", FileUtils.sdfilepath);
                                                        ble_send(SDZ, 0);
                                                    }
                                                })
                                                .setNegativeButton("覆盖", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        ble_send(SDZ, 0);
                                                    }
                                                }).setCancelable(false).show();
                                        return;
                                    }
                                }
                            }
                        }
                        ble_send(SDZ, 0);
                        break;
                    case SDZ:
                        if (!Modbus.CRCIsTrue(writeBuf)) {
                            ble_send(SDZ, 0);
                            break;
                        }
                        //读取延时时间
                        //采集点数
                        kk = temp[3];
                        kk = kk * 256 + temp[4];
                        q_max_point = kk;
                        int k = temp[5];
                        k = k * 256 + temp[6];
                        q_ys_time = k;
                        //读取间隔时间
                        int ii = temp[7];
                        ii = ii * 256 + temp[8];
                        int q_jg_time = ii;
                        pb_data_receive.setMax(kk);
                        MyLogger.jLog().d("有效点" + time + "采集点数" + kk + "读取延时时间" + k + "读取间隔时间" + ii + "开始" + startAndendtime[0] + "结束" + startAndendtime[1]);
                        filehead.caijidianshu = kk;
                        filehead.yanshishijian = q_ys_time;
                        filehead.jiangeshijian = q_jg_time;
                        if (binaryfilename_CJ.length() == 0) {
                            break;
                        }
                        if (kk != 0) {

                            FileUtils.FileNewCreate(binaryfilename_CJ);
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, filehead.duihao);
                            if (filehead.quhao.equals("")){

                                FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, filehead.jinghao);
                            }else{
                                FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, filehead.quhao+"-"+filehead.jinghao);
                            }
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, filehead.tanguanbianhao);
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime"))));
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(sp_cexiestep.getLong("T1")));
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "" + q_ys_time/60);
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "" + q_jg_time);
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "" + q_max_point);
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, String.valueOf(SaveListModel.listinfo.size()));

                        } else {
                            new AlertDialog.Builder(DataReceiveActivity.this).setTitle("提示").setMessage("探管内无数据！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                            Back.setVisibility(View.VISIBLE);
                        }
                        list.add(filehead);
                        pb_data_receive.setMax(q_max_point);
                        bleEnd = bleStart + q_max_point * 8;
                        MyLogger.jLog().e("bleEnd:" + bleEnd + "point :" + q_max_point);
                        if (q_max_point > 15) {
                            ble_send(ALLSHUJU, bleStart, 120);
                            bleStart += 120;
                        } else {
                            ble_send(ALLSHUJU, bleStart, q_max_point * 8);
                        }
                        dialogUtil.closeDialog();
                        break;
                    case ALLSHUJU:

                        if (!Modbus.CRCIsTrue(writeBuf)) {
                            if (q_max_point > 15) {
                                ble_send(ALLSHUJU, bleStart, 120);
//                                bleStart += 120;
                            } else {
                                ble_send(ALLSHUJU, bleStart, q_max_point * 8);
                            }
                            break;
                        }
                        tg_type = 14;
                        short getinfo[] = new short[temp.length - 5];
                        System.arraycopy(temp, 3, getinfo, 0, temp.length - 5);
                        for (int i = 0; i < (getinfo.length / 16); i++) {
//						循环解析数据
                            tanguan = thelper.js_dy(tg_type, getinfo, c_d, i);
                            float jcjingxie = thelper.jiSuanJingXie(tanguan.wendu, tanguan.ut, tanguan.gx, tanguan.gy, tanguan.gz, tg_type, t_bc, c_d);
                            TGhelper tghelper__ = new TGhelper();
                            float tempstr[];
                            tempstr = tghelper__.bg_show_f(tanguan, tg_type, c_d, t_bc, jcjingxie);
                            receive.jingxie = jcjingxie;
                            receive.dianya = tanguan.dianya;
                            receive.wendu = tempstr[1];
                            receive.cifangwei = tempstr[3];
                            receive.zhongligaobian = tempstr[4];
                            receive.diciqingjiao = tempstr[6];
                            receive.cichangqiangdu = tempstr[7];
                            receive.cigongjumian = tempstr[5];
                            receive.jiaoyanhe = tempstr[8];
                            tempgongjumian = receive.cigongjumian - xiuzhengvalue;
                            if (tempgongjumian < 0) tempgongjumian = tempgongjumian + 360;
                            tempxiuzhengvalue = receive.zhongligaobian - xiuzhengvalue;
                            if (tempxiuzhengvalue < 0) tempxiuzhengvalue = tempxiuzhengvalue + 360;
                            String biaoji = "";
                            ++list_ys;
                            Utils.getDatetime(caiji_second + list_ys * T3);
                            for (int j = 0; j < SaveListModel.listinfo.size(); j++) {
                                if (list_ys == SaveListModel.listinfo.get(j).getNum()) {
                                    if (receive.jiaoyanhe > 1.01 || receive.jiaoyanhe < 0.99) {
                                        queryPoint = "疑问点";
                                    } else {
                                        queryPoint = "";
                                    }
                                    String[] items = {SaveListModel.listinfo.get(j).getXuhao(), queryPoint, SaveListModel.listinfo.get(j).getShendu(), df5.format(receive.jingxie), df5.format(receive.cifangwei), df4.format(receive.jiaoyanhe), df6.format(receive.cichangqiangdu), df5.format(receive.diciqingjiao), df3.format(receive.wendu), df2.format(receive.dianya), df5.format(tempxiuzhengvalue), df5.format(tempgongjumian), SaveListModel.listinfo.get(j).getType(), SaveListModel.listinfo.get(j).getTime()};
                                    excledatas.add(array2map(items));
                                    biaoji = "1";
                                    addReceiveData(items);

                                    FileUtils.appendWriteFileStringToLine(binaryfilename_CJ,biaoji+","+SaveListModel.listinfo.get(j).getShendu()+","+df5.format(receive.jingxie)+","+ df5.format(receive.cifangwei)+","+df5.format(tempxiuzhengvalue)+","+ df5.format(tempgongjumian)+","+ df4.format(receive.jiaoyanhe)+","+"1,"+df5.format(receive.diciqingjiao)+","+df3.format(receive.wendu)+","+df6.format(receive.cichangqiangdu)+","+
                                            tanguan.gx+","+tanguan.gy+","+tanguan.gz+","+tanguan.mx+","+tanguan.my+","+tanguan.mz+","+ df2.format(receive.dianya)+",0");
                                }
                            }
                            supportingPaper(list_ys);
                            String[] item_all = {list_ys + "", biaoji, df5.format(receive.jingxie), df5.format(receive.cifangwei), df4.format(receive.jiaoyanhe), df6.format(receive.cichangqiangdu), df5.format(receive.diciqingjiao), df3.format(receive.wendu), df2.format(receive.dianya), df5.format(tempxiuzhengvalue), df5.format(tempgongjumian), Utils.getDatetime(caiji_second + (list_ys - 1) * T3), explain, paper};
                            excledatas_all.add(array2map1(item_all));
                            if (kk != 0) {
                                if (biaoji.equals("")){
                                    biaoji = "0";
//                              有效点标记（1-表示有效点，0-表示无效点）、测深（保留2位小数）、 井斜（保留2位小数）、磁方位（保留2位小数）、            高边工具面（保留2位小数）、磁性工具面（保留2位小数）、校验和（保留3位小数）、BT（保留3位小数）、磁倾角（保留2位小数）、温度（保留2位小数）、磁场强度（保留2位小数）
                                    FileUtils.appendWriteFileStringToLine(binaryfilename_CJ,biaoji+","+0+","+df5.format(receive.jingxie)+","+ df5.format(receive.cifangwei)+","+df5.format(tempxiuzhengvalue)+","+ df5.format(tempgongjumian)+","+ df4.format(receive.jiaoyanhe)+","+"1,"+df5.format(receive.diciqingjiao)+","+df3.format(receive.wendu)+","+df6.format(receive.cichangqiangdu)+","+
//                                      、GX（保留3位小数）、GY（保留3位小数）、GZ（保留3位小数）、MX（保留3位小数）、MY（保留3位小数）、MZ（保留3位小数）、电池电压（保留3位小数）、修改标记（1-修改过，0-未修改）。
                                            tanguan.gx+","+tanguan.gy+","+tanguan.gz+","+tanguan.mx+","+tanguan.my+","+tanguan.mz+","+ df2.format(receive.dianya)+",0");
                                }
                            }
                            biaoji = "";
                            pb_data_receive.setProgress(cishu);
                            cishu = cishu + 1;
                        }
                        if (list_ys == q_max_point) {
                            CreateExcel ce = new CreateExcel();
                            ce.pullArray(filename_excel, header1, excledatas);
                            ce.pullArray(filename_excel + "-YS", header2, excledatas_all);
                            dialogUtil.closeDialog();
                            new AlertDialog.Builder(DataReceiveActivity.this).setTitle("提示").setMessage("数据导出路径为:" +
                                    "手机存储/HKCX-SJ/"+filename_excel + "/").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mService != null) mService.stop();
                                }
                            }).show();
                            LsNum();
                            Back.setVisibility(View.VISIBLE);
                        }
                        if (bleStart < bleEnd) {
                            if ((bleEnd - bleStart) > 120) {
                                ble_send(ALLSHUJU, bleStart, 120);
                            } else {
                                ble_send(ALLSHUJU, bleStart, bleEnd - bleStart);
                            }
                            bleStart += 120;
                        }
                        break;
                    case 0x11111:

                        dialogUtil.closeDialog();
                        MainActivity.mBleService.setCharacteristicNotification(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID, true);//设置通知
                        MainActivity.mBleService.readCharacteristic(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID);//读取数据
                        Thread.sleep(200);
                        MainActivity.mBleService.writeCharacteristic(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID, Modbus.sendStop());
                        Thread.sleep(100);
                        ble_send(TG_TYPE, 0);
                        break;
                    case 0x11112:
                        MainActivity.mBleService.getSupportedGattServices();//获取服务
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                MyLogger.jLog().e(e);
            }
        }
    };

    /*********************************************************** ble   end ********************************************************/
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                byte[] writeBuf = (byte[]) msg.obj;
                short[] temp = null;
                String strtx = "";
                if (writeBuf != null)
                    temp = getbytes(writeBuf);
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:
                        if (msg.arg1 == BluetoothService.STATE_CONNECTED) {
                            send1 = null;
                            send1 = new send();
                            send1.start();
                            dialogUtil.closeDialog();
                        }
                        break;
                    case MESSAGE_TOAST:
                        dialogUtil.closeDialog();
                        /**判断探管类型,提示通讯失败*/

                        if (buchangxishu_tg_type.equals("51S")) {
                            strtx = getString(R.string.tongxunshibai51S);
                        } else {
                            strtx = getString(R.string.tongxunshibai51F);

                        }
                        new AlertDialog.Builder(DataReceiveActivity.this).setTitle("提示").setMessage(strtx).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (mService != null) mService.stop();
                                DataReceiveActivity.this.finish();
                            }
                        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialogUtil.showDialog();
                                if (mService != null) mService.stop();
                                mService.connect(PublicValues.device);
                            }
                        }).show();
                        Back.setVisibility(View.VISIBLE);
                        MyLogger.jLog().e("MESSAGE_TOAST的通讯失败");
                        break;
                    case TG_TYPE:
                        MyLogger.jLog().e("这里都没走么.");
                        if (writeBuf[1] == (int) '5' && writeBuf[2] == (int) '1') {
                            //        			此方法确定探管类型   10  11  F 12  S 13
                            tg_type = thelper.bg_tg_type(writeBuf[1], writeBuf[2], writeBuf[3]);
                        } else if (writeBuf[1] == (int) '2' && writeBuf[2] == (int) '2') {
                            tg_type = 22;
                        } else {
                            if (mService != null) mService.stop();
                            mService.connect(PublicValues.device);
                            connect++;
                            if (connect >= 3) {
                                send1.isok = false;
                                if (mService != null) mService.stop();
                            }
                        }
                        MyLogger.jLog().e("tg_type" + "550:-------------------------->" + tg_type);
                        filehead.tg_type = tg_type;
                        break;
                    case GZZZ:
                        MyLogger.jLog().d("TG_TYPE");
                        break;
                    case GZFS:
                        MyLogger.jLog().d("GZFS");
                        gongzuofangshi = temp[1];
                        break;
                    //            	修正系数
                    case XZXS:
                        MyLogger.jLog().d("XZXS");
                        for (int i = 0; i <= 31; i++) {
                            c_xs[dizhi - 0x0120 + i] = temp[i + 1];
                        }
                        if (xsbiaoji >= 7) {
                            c_d = thelper.getXiuZhengXiShu(c_xs);
                            filehead.tanguanbianhao = thelper.getTGName(gongzuofangshi, "" + (char) (c_xs[140]) + (char) (c_xs[141]) + (char) (c_xs[142]) + "-" + df1.format(c_xs[144] * 256 + c_xs[143]));
                            list.add(c_d);
                            MyLogger.jLog().e("=======>"+filehead.tanguanbianhao);
                            if(filehead.tanguanbianhao.contains("51E")){
                                filehead.tg_type = 11;
                                tg_type = 11;
                            }
                        }
                        xsbiaoji++;
                        break;
                    //            	接收到补偿系数
                    case BCXS:
                        MyLogger.jLog().d("BCXS");
                        for (int i = 0; i <= 31; i++) {
                            c_bcxs[dizhi - 0xa0 + i] = temp[i + 1];
                        }
                        if (bcbiaoji >= 3) {
                            t_bc = thelper.getBuChangXiShu(c_bcxs);
                            list.add(t_bc);
                        }
                        bcbiaoji++;
                        break;
                    case DJCG:
                        MyLogger.jLog().d("DJCG");
                        String s1 = "";
                        byte[] k1 = new byte[11];
                        float p;
                        MyLogger.jLog().e(Arrays.toString(writeBuf));
                        System.arraycopy(writeBuf, 1, k1, 0, 10);
                        /**
                         public static void arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
                         参数
                         src -- 这是源数组.
                         srcPos -- 这是源数组中的起始位置。
                         dest -- 这是目标数
                         destPos -- 这是目标数据中的起始位置。
                         length -- 这是一个要复制的数组元素的数目
                         */
                        s1 = new String(k1, "GBK");
                        filehead.duihao = s1.trim();
                        s1 = "";
                        System.arraycopy(writeBuf, 11, k1, 0, 10);
                        s1 = new String(k1, "GBK");
                        filehead.jinghao = s1.trim();
                        p = 0;
                        for (int i = 1; i <= 6; i++) {
                            p = p * 10 + temp[i + 20];
                        }
                        p = p / 100;
                        filehead.ceshen = df7.format(p);
                        p = 0;
                        for (int i = 1; i <= 5; i++) {
                            p = p * 10 + temp[i + 26];
                        }
                        p = p / 100;
                        filehead.gaobianxiuzheng = p;
                        xiuzhengvalue = p;
                        filehead.quhao = sp_teset.getString("quhao");
                        MyLogger.jLog().e(shared.getString("FileUtils.sdfilepath")+"<====================");
                        FileUtils.sdfilepath = shared.getString("FileUtils.sdfilepath");
                        EfficientPoint.ceshen_ = shared.getFloat("EfficientPoint.ceshen_");
                        if (filehead.quhao.equals("")){

                            String fileName = filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime")));
                            binaryfilename_CJ = fileName + ".CJ";
//                        filename_SJ = fileName + ".SJ";
                            filename_excel = fileName;
                        }else {
                            String fileName = filehead.quhao + "：" + filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime")));
                            binaryfilename_CJ = fileName + ".CJ";
//                        filename_SJ = fileName + ".SJ";
                            filename_excel = fileName;
                        }
                       SDFile = Environment.getExternalStorageDirectory();
                        File sdpath = new File(SDFile.getAbsolutePath() + "/HKCX-SJ");

                        if (sdpath.listFiles() != null ){
                            if (sdpath.listFiles().length > 0) {
                                for (File file : sdpath.listFiles()) {
                                    if (file.getCanonicalPath().contains(filename_excel)){
                                        new AlertDialog.Builder(DataReceiveActivity.this)
                                                .setTitle("提示")
                                                .setMessage("       有相同文件名的数据已存在  1.关于[覆盖],则覆盖原有数据。2.关于[另存],则在原有测深增加0.01米后另存")
                                                .setPositiveButton("另存", new DialogInterface.OnClickListener(){
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        EfficientPoint.ceshen += 0.01;
                                                        shared.putValue("EfficientPoint.ceshen_",EfficientPoint.ceshen);
                                                        if(filehead.quhao.equals("")) {

                                                            filename_excel = filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime")));
                                                            binaryfilename_CJ =  filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime"))) + ".CJ";
                                                            FileUtils.sdfilepath = SDFile.getPath() + "/HKCX-SJ/："+ filename_excel + "/";
                                                        }else{
                                                            filename_excel = filehead.quhao + "：" + filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime")));
                                                            binaryfilename_CJ = filehead.quhao + "：" + filehead.jinghao + "(" + df5.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime"))) + ".CJ";
                                                            FileUtils.sdfilepath = SDFile.getPath() + "/HKCX-SJ/"+ filename_excel + "/";
                                                        }

                                                        shared.putValue("FileUtils.sdfilepath", FileUtils.sdfilepath);
                                                        new Normal().start();
                                                    }
                                                })
                                                .setNegativeButton("覆盖", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        new Normal().start();
                                                    }
                                                }).setCancelable(false).show();
                                        return;
                                    }
                                }
                            }
                        }
                        new Normal().start();
                        break;
                    case SDZ:
                        MyLogger.jLog().d("SDZ");
                        int k2;
                        //读取延时时间
                        int k = temp[6];
                        k = k * 256 + temp[7];
                        q_ys_time = k + 60;
                        //读取间隔时间
                        int ii = temp[8];
                        ii = ii * 256 + temp[9];
                        int q_jg_time = ii;
                        //采集点数
                        kk = temp[1];
                        kk = kk * 256 + temp[2];
                        q_max_point = kk;
                        startAndendtime = thelper.startpAndendp(mmm, kk, k, ii);
                        if (startAndendtime[0] == -1 && startAndendtime[1] == 0 && kk != 0) {
                            isyouxiao = true;
                        }
                        MyLogger.jLog().d("有效点" + time + "采集点数" + kk + "读取延时时间" + k + "读取间隔时间" + ii + "开始" + startAndendtime[0] + "结束" + startAndendtime[1]);
                        filehead.caijidianshu = kk;
                        filehead.yanshishijian = q_ys_time;
                        filehead.jiangeshijian = ii;
                        if (binaryfilename_CJ.length() == 0) {
                            break;
                        }
                        SDFile = Environment.getExternalStorageDirectory();
                        if (kk != 0) {
                                FileUtils.FileNewCreate(binaryfilename_CJ);
                                FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, filehead.duihao);
                            if (filehead.quhao.equals("")){

                                FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, filehead.jinghao);
                            }else{
                                FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, filehead.quhao+"-"+filehead.jinghao);
                            }
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, filehead.tanguanbianhao);
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime"))));
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(sp_cexiestep.getLong("T1")));
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "" + q_ys_time/60);
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "" + q_jg_time);
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "0");
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, "" + q_max_point);
                            FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, String.valueOf(SaveListModel.listinfo.size()));

                        } else {
                            new AlertDialog.Builder(DataReceiveActivity.this).setTitle("提示").setMessage("探管内无数据！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();

                            if (mService != null) mService.stop();
                            Back.setVisibility(View.VISIBLE);
                        }
                        end = (q_max_point - 1) * 18 + 0x200;

                        list.add(filehead);

                        pb_data_receive.setMax(q_max_point);
                        break;
                    case ALLSHUJU:
                        MyLogger.jLog().e("ALLSHUJU---------------->" + tg_type);
                        short getinfo1[] = new short[37];
                        /**********计算校验和开始******************/
                        int jyh = 0;
                        int jyhk = 37;
                        for (int j = 0; j < jyhk; j++) {
                            jyh = jyh + writeBuf[j];
                        }
                        if (writeBuf[jyhk] != (byte) (jyh % 256)) {
                            MyLogger.jLog().e("校验和相等" + start);
                            WriteAll(start);
                            break;
                        }
                        /**********计算校验和结束******************/
                        System.arraycopy(temp, 1, getinfo1, 0, 36);
                        tanguan = thelper.js_dy(tg_type, getinfo1, c_d, 0);
                        float jcjingxie = thelper.jiSuanJingXie(tanguan.wendu, tanguan.ut, tanguan.gx, tanguan.gy, tanguan.gz, tg_type, t_bc, c_d);
                        TGhelper tghelper__ = new TGhelper();
                        float tempstr[];
                        if (tg_type == 12 || tg_type == 13) {
                            tempstr = tghelper__.bg_show_f(tanguan, tg_type, c_d, t_bc, jcjingxie);
                        } else {
                            tempstr = tghelper__.bg_show(tanguan, tg_type, c_d, jcjingxie);
                        }
                        /**
                         * 判断校验和大于5的情况下
                         * 可能是某个参数错误导致数据错误
                         */

                        if (tempstr[8] > 5) {
                            if (mService != null) mService.stop();
                            if (buchangxishu_tg_type.equals("51S")) {
                                strtx = getString(R.string.tongxunshibai51S);
                            } else {
                                strtx = getString(R.string.tongxunshibai51F);

                            }
                            new AlertDialog.Builder(DataReceiveActivity.this).setTitle("提示").setMessage(strtx).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    DataReceiveActivity.this.finish();
                                }
                            }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialogUtil.showDialog();
                                    if (mService != null) mService.stop();
                                    mService.connect(PublicValues.device);
                                }
                            }).show();
                            Back.setVisibility(View.VISIBLE);
                        }
                        receive.jingxie = jcjingxie;
                        receive.dianya = tanguan.dianya;
                        receive.wendu = tempstr[1];
                        receive.cifangwei = tempstr[3];
                        receive.zhongligaobian = tempstr[4];
                        receive.diciqingjiao = tempstr[6];
                        receive.cichangqiangdu = tempstr[7];
                        receive.cigongjumian = tempstr[5];
                        receive.jiaoyanhe = tempstr[8];
                        tempgongjumian = receive.cigongjumian - xiuzhengvalue;
                        if (tempgongjumian < 0) tempgongjumian = tempgongjumian + 360;
                        tempxiuzhengvalue = receive.zhongligaobian - xiuzhengvalue;
                        if (tempxiuzhengvalue < 0) tempxiuzhengvalue = tempxiuzhengvalue + 360;
                        String biaoji = "";
                        ++list_ys;
                        Utils.getDatetime(caiji_second + list_ys * T3);
                        for (int j = 0; j < SaveListModel.listinfo.size(); j++) {
                            if (list_ys == SaveListModel.listinfo.get(j).getNum()) {
                                if (receive.jiaoyanhe > 1.01 || receive.jiaoyanhe < 0.99) {
                                    queryPoint = "疑问点";
                                } else {
                                    queryPoint = "";
                                }
                                String[] items = {SaveListModel.listinfo.get(j).getXuhao(), queryPoint, SaveListModel.listinfo.get(j).getShendu(), df5.format(receive.jingxie), df5.format(receive.cifangwei), df4.format(receive.jiaoyanhe), df6.format(receive.cichangqiangdu), df5.format(receive.diciqingjiao), df3.format(receive.wendu), df2.format(receive.dianya), df5.format(tempxiuzhengvalue), df5.format(tempgongjumian), SaveListModel.listinfo.get(j).getType(), SaveListModel.listinfo.get(j).getTime()};
                                excledatas.add(array2map(items));
                                biaoji = "1";
                                addReceiveData(items);
                                FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, biaoji + "," + SaveListModel.listinfo.get(j).getShendu() + "," + df5.format(receive.jingxie) + "," + df5.format(receive.cifangwei) + "," + df5.format(tempxiuzhengvalue) + "," + df5.format(tempgongjumian) + "," + df4.format(receive.jiaoyanhe) + "," + "1," + df5.format(receive.diciqingjiao) + "," + df3.format(receive.wendu) + "," + df6.format(receive.cichangqiangdu) + "," +
//                                      、GX（保留3位小数）、GY（保留3位小数）、GZ（保留3位小数）、MX（保留3位小数）、MY（保留3位小数）、MZ（保留3位小数）、电池电压（保留3位小数）、修改标记（1-修改过，0-未修改）。
                                        tanguan.gx + "," + tanguan.gy + "," + tanguan.gz + "," + tanguan.mx + "," + tanguan.my + "," + tanguan.mz + "," + df2.format(receive.dianya) + ",0");


                            }
                        }
                        supportingPaper(list_ys);
                        String[] item_all = {list_ys + "", biaoji, df5.format(receive.jingxie), df5.format(receive.cifangwei), df4.format(receive.jiaoyanhe), df6.format(receive.cichangqiangdu), df5.format(receive.diciqingjiao), df3.format(receive.wendu), df2.format(receive.dianya), df5.format(tempxiuzhengvalue), df5.format(tempgongjumian), Utils.getDatetime(caiji_second + (list_ys - 1) * T3), explain, paper};
                        excledatas_all.add(array2map1(item_all));

                        if (kk != 0) {
//                                		                有效点标记（1-表示有效点，0-表示无效点）、测深（保留2位小数）、 井斜（保留2位小数）、磁方位（保留2位小数）、            高边工具面（保留2位小数）、磁性工具面（保留2位小数）、校验和（保留3位小数）、BT（保留3位小数）、磁倾角（保留2位小数）、温度（保留2位小数）、磁场强度（保留2位小数）
                            if (biaoji.equals("")) {
                                biaoji = "0";
                                FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, biaoji + "," + 0 + "," + df5.format(receive.jingxie) + "," + df5.format(receive.cifangwei) + "," + df5.format(tempxiuzhengvalue) + "," + df5.format(tempgongjumian) + "," + df4.format(receive.jiaoyanhe) + "," + "1," + df5.format(receive.diciqingjiao) + "," + df3.format(receive.wendu) + "," + df6.format(receive.cichangqiangdu) + "," +
//                                      、GX（保留3位小数）、GY（保留3位小数）、GZ（保留3位小数）、MX（保留3位小数）、MY（保留3位小数）、MZ（保留3位小数）、电池电压（保留3位小数）、修改标记（1-修改过，0-未修改）。
                                        tanguan.gx + "," + tanguan.gy + "," + tanguan.gz + "," + tanguan.mx + "," + tanguan.my + "," + tanguan.mz + "," + df2.format(receive.dianya) + ",0");
                                biaoji = "";
                            }

                        }
                        biaoji = "";
                        /********************解析前半条数据结束*********************/
                        tanguan1 = thelper.js_dy(tg_type, getinfo1, c_d, 1);
                        jcjingxie = thelper.jiSuanJingXie(tanguan1.wendu, tanguan1.ut, tanguan1.gx, tanguan1.gy, tanguan1.gz, tg_type, t_bc, c_d);
                        float tempstr1[];
                        if (tg_type == 12 || tg_type == 13) {
                            tempstr1 = tghelper__.bg_show_f(tanguan1, tg_type, c_d, t_bc, jcjingxie);
                        } else {
                            tempstr1 = tghelper__.bg_show(tanguan1, tg_type, c_d, jcjingxie);
                        }
                        receive.jingxie = jcjingxie;
                        receive.dianya = tanguan1.dianya;
                        receive.wendu = tempstr1[1];
                        receive.cifangwei = tempstr1[3];
                        receive.zhongligaobian = tempstr1[4];
                        receive.diciqingjiao = tempstr1[6];
                        receive.cichangqiangdu = tempstr1[7];
                        receive.cigongjumian = tempstr1[5];
                        receive.jiaoyanhe = tempstr1[8];
                        tempgongjumian = receive.cigongjumian - xiuzhengvalue;
                        if (tempgongjumian < 0) tempgongjumian = tempgongjumian + 360;
                        tempxiuzhengvalue = receive.zhongligaobian - xiuzhengvalue;
                        if (tempxiuzhengvalue < 0) tempxiuzhengvalue = tempxiuzhengvalue + 360;
                        ++list_ys;
                        pb_data_receive.setProgress(list_ys);
                        for (int j = 0; j < SaveListModel.listinfo.size(); j++) {
                            if (list_ys == SaveListModel.listinfo.get(j).getNum()) {
                                if (list_ys <= q_max_point) {
                                    //						"序号", "测量时间" , "测量深度", "井斜","磁方位","校验和","磁场强度","磁倾角","温度","电压", "重力高边","磁工具面","测深输入",
                                    if (receive.jiaoyanhe > 1.01 || receive.jiaoyanhe < 0.99) {
                                        queryPoint = "疑问点";
                                    } else {
                                        queryPoint = "";
                                    }
                                    String[] items = {SaveListModel.listinfo.get(j).getXuhao(), queryPoint, SaveListModel.listinfo.get(j).getShendu(), df5.format(receive.jingxie), df5.format(receive.cifangwei), df4.format(receive.jiaoyanhe), df6.format(receive.cichangqiangdu), df5.format(receive.diciqingjiao), df3.format(receive.wendu), df2.format(receive.dianya), df5.format(tempxiuzhengvalue), df5.format(tempgongjumian), SaveListModel.listinfo.get(j).getType(), SaveListModel.listinfo.get(j).getTime()};
                                    addReceiveData(items);
                                    excledatas.add(array2map(items));

                                    biaoji = "1";


                                    FileUtils.appendWriteFileStringToLine(binaryfilename_CJ, biaoji + "," + SaveListModel.listinfo.get(j).getShendu() + "," + df5.format(receive.jingxie) + "," + df5.format(receive.cifangwei) + "," + df5.format(tempxiuzhengvalue) + "," + df5.format(tempgongjumian) + "," + df4.format(receive.jiaoyanhe) + "," + "1," + df5.format(receive.diciqingjiao) + "," + df3.format(receive.wendu) + "," + df6.format(receive.cichangqiangdu) + "," +
//                                      、GX（保留3位小数）、GY（保留3位小数）、GZ（保留3位小数）、MX（保留3位小数）、MY（保留3位小数）、MZ（保留3位小数）、电池电压（保留3位小数）、修改标记（1-修改过，0-未修改）。
                                            tanguan.gx + "," + tanguan.gy + "," + tanguan.gz + "," + tanguan.mx + "," + tanguan.my + "," + tanguan.mz + "," + df2.format(receive.dianya) + ",0");

                                }
                            }
                        }
                        supportingPaper(list_ys);
                        String[] item_all1 = {list_ys + "", biaoji, df5.format(receive.jingxie), df5.format(receive.cifangwei), df4.format(receive.jiaoyanhe), df6.format(receive.cichangqiangdu), df5.format(receive.diciqingjiao), df3.format(receive.wendu), df2.format(receive.dianya), df5.format(tempxiuzhengvalue), df5.format(tempgongjumian), Utils.getDatetime(caiji_second + (list_ys - 1) * T3), explain, paper};
                        if (list_ys <= q_max_point) {
                            excledatas_all.add(array2map1(item_all1));
                            if (kk != 0) {
                                // 写入此次有效点
                                if (biaoji.equals(""))biaoji = "0";
                                FileUtils.appendWriteFileStringToLine(binaryfilename_CJ,biaoji+","+0+","+df5.format(receive.jingxie)+","+ df5.format(receive.cifangwei)+","+df5.format(tempxiuzhengvalue)+","+ df5.format(tempgongjumian)+","+ df4.format(receive.jiaoyanhe)+","+"1,"+df5.format(receive.diciqingjiao)+","+df3.format(receive.wendu)+","+df6.format(receive.cichangqiangdu)+","+
//                                      、GX（保留3位小数）、GY（保留3位小数）、GZ（保留3位小数）、MX（保留3位小数）、MY（保留3位小数）、MZ（保留3位小数）、电池电压（保留3位小数）、修改标记（1-修改过，0-未修改）。
                                        tanguan.gx+","+tanguan.gy+","+tanguan.gz+","+tanguan.mx+","+tanguan.my+","+tanguan.mz+","+ df2.format(receive.dianya)+",0");
                                biaoji = "";

                            }
                        }
                        /********************解析后半条数据结束*********************/
                        if (list_ys == q_max_point || list_ys - 1 == q_max_point) {
                            CreateExcel ce = new CreateExcel();
                            ce.pullArray(filename_excel, header1, excledatas);
                            ce.pullArray(filename_excel + "-YS", header2, excledatas_all);
                            new AlertDialog.Builder(DataReceiveActivity.this).setTitle("提示").setMessage("数据导出路径为:" + "手机存储/HKCX-SJ/"+filename_excel + "/").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mService != null) mService.stop();
                                }
                            }).show();
                            LsNum();
                            Back.setVisibility(View.VISIBLE);
                        }
                        if (start <= end) {
                            start += 36;
                            WriteAll(start);
                        }
                        pb_data_receive.setProgress(cishu);
                        cishu = cishu + 2;
                        count -= 2;
                        break;
                    case 100:
                        MyLogger.jLog().e("1088:" + tg_type);
                        List<Object> infolist = (List<Object>) objectInOut.read(FileUtils.sdfilepath + binaryfilename_CJ);
                        if (tg_type == 12 || tg_type == 13) {
                            all_info_bt_sj = (TanGuan) infolist.get(3);
                        } else {
                            all_info_bt_sj = (TanGuan) infolist.get(2);
                        }
                        short getinfo2[] = new short[37];
                        System.arraycopy(temp, 1, getinfo2, 0, 36);
                        tanguan = thelper.js_dy(tg_type, getinfo2, c_d, 0);
                        break;
                    case MESSAGESTART:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 写文件有效点
     */
    private void writeyxpoint(String filename) {
        //		井斜
        FileUtils.appendWriteFile(filename, receive.jingxie);
        //		方位
        FileUtils.appendWriteFile(filename, receive.cifangwei);
        //		重力高边
        FileUtils.appendWriteFile(filename, receive.diciqingjiao);
        //		磁工具面
        FileUtils.appendWriteFile(filename, receive.cichangqiangdu);
        //		磁倾角
        FileUtils.appendWriteFile(filename, tempgongjumian);
        //		磁场强度
        FileUtils.appendWriteFile(filename, tempxiuzhengvalue);
        //		温度
        FileUtils.appendWriteFile(filename, receive.wendu);
        //		电池电压
        FileUtils.appendWriteFile(filename, receive.dianya);
        //		校验和
        FileUtils.appendWriteFile(filename, receive.jiaoyanhe);
    }

    private void addReceiveData(String[] item) {
        //1 序号 	2测量时间 	3测量深度	4井斜		5磁方位	6磁倾角    	7磁场强度		8磁工具面  	9重力高边	 10温度	11电压 	12s效验和
        TableLayout table = (TableLayout) findViewById(R.id.tl_receive_data);
        TableRow tablerow = new TableRow(this);
        TableRow.LayoutParams lparams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        tablerow.setLayoutParams(lparams);
        //		序号
        TextView tv_num = new TextView(this);
        tv_num.setGravity(Gravity.CENTER);
        tv_num.setTextSize(20);
        tv_num.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_num.setBackgroundResource(R.drawable.bg_item_third);
        tv_num.setText(item[0]);
        tablerow.addView(tv_num);
        //		疑问点
        TextView tv_yiwen = new TextView(this);
        tv_yiwen.setGravity(Gravity.CENTER);
        tv_yiwen.setTextSize(20);
        tv_yiwen.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_yiwen.setBackgroundResource(R.drawable.bg_item_third);
        tv_yiwen.setText(item[1]);
        tablerow.addView(tv_yiwen);
        //		测量时间
        TextView tv_time = new TextView(this);
        tv_time.setGravity(Gravity.RIGHT);
        tv_time.setTextSize(20);
        tv_time.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_time.setBackgroundResource(R.drawable.bg_item_third);
        tv_time.setText(item[2]);
        tablerow.addView(tv_time);
        //		类型
        TextView tv_leixing = new TextView(this);
        tv_leixing.setGravity(Gravity.RIGHT);
        tv_leixing.setTextSize(20);
        tv_leixing.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_leixing.setBackgroundResource(R.drawable.bg_item_third);
        tv_leixing.setText(item[3]);
        tablerow.addView(tv_leixing);
        //		测深
        TextView tv_deep = new TextView(this);
        tv_deep.setGravity(Gravity.RIGHT);
        tv_deep.setTextSize(20);
        tv_deep.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_deep.setBackgroundResource(R.drawable.bg_item_third);
        tv_deep.setText(item[4]);
        tablerow.addView(tv_deep);
        //		井斜
        TextView tv_id = new TextView(this);
        tv_id.setGravity(Gravity.RIGHT);
        tv_id.setTextSize(20);
        tv_id.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_id.setBackgroundResource(R.drawable.bg_item_third);
        tv_id.setText(item[5]);
        tablerow.addView(tv_id);

        //		5磁方位
        TextView tv_cifangwei = new TextView(this);
        tv_cifangwei.setGravity(Gravity.RIGHT);
        tv_cifangwei.setTextSize(20);
        tv_cifangwei.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_cifangwei.setBackgroundResource(R.drawable.bg_item_third);
        tv_cifangwei.setText(item[6]);
        tablerow.addView(tv_cifangwei);
        //		磁倾角
        TextView tv_ciqinjiao = new TextView(this);
        tv_ciqinjiao.setGravity(Gravity.RIGHT);
        tv_ciqinjiao.setTextSize(20);
        tv_ciqinjiao.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_ciqinjiao.setBackgroundResource(R.drawable.bg_item_third);
        tv_ciqinjiao.setText(item[7]);
        tablerow.addView(tv_ciqinjiao);
        //		磁场强度
        TextView tv_magnetic = new TextView(this);
        tv_magnetic.setGravity(Gravity.RIGHT);
        tv_magnetic.setTextSize(20);
        tv_magnetic.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_magnetic.setBackgroundResource(R.drawable.bg_item_third);
        tv_magnetic.setText(item[8]);
        tablerow.addView(tv_magnetic);
        //		磁工具面
        TextView tv_cigongjumian = new TextView(this);
        tv_cigongjumian.setGravity(Gravity.RIGHT);
        tv_cigongjumian.setTextSize(20);
        tv_cigongjumian.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_cigongjumian.setBackgroundResource(R.drawable.bg_item_third);
        tv_cigongjumian.setText(item[9]);
        tablerow.addView(tv_cigongjumian);
        //		重力
        TextView tv_gravity = new TextView(this);
        tv_gravity.setGravity(Gravity.RIGHT);
        tv_gravity.setTextSize(20);
        tv_gravity.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_gravity.setBackgroundResource(R.drawable.bg_item_third);
        tv_gravity.setText(item[10]);
        tablerow.addView(tv_gravity);
        //		温度
        TextView tv_temperature = new TextView(this);
        tv_temperature.setGravity(Gravity.RIGHT);
        tv_temperature.setTextSize(20);
        tv_temperature.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_temperature.setBackgroundResource(R.drawable.bg_item_third);
        tv_temperature.setText(item[11]);
        tablerow.addView(tv_temperature);
        //		电压
        TextView tv_voltage = new TextView(this);
        tv_voltage.setGravity(Gravity.RIGHT);
        tv_voltage.setTextSize(20);
        tv_voltage.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_voltage.setBackgroundResource(R.drawable.bg_item_third);
        tv_voltage.setText(item[12]);
        tablerow.addView(tv_voltage);
        //		 校验和 ;
        TextView tv_checksum = new TextView(this);
        tv_checksum.setGravity(Gravity.CENTER);
        tv_checksum.setTextSize(20);
        tv_checksum.setTextColor(Color.rgb(0x40, 0x40, 0x40));
        tv_checksum.setBackgroundResource(R.drawable.bg_item_third);
        tv_checksum.setText(item[13]);
        tablerow.addView(tv_checksum);
        table.addView(tablerow);
    }

    /**
     * 生成Excel表的表头
     */
    String header1[] = {"序号", "注意", "测量深度,m", "井斜,°", "磁方位,°", "校验和", "磁场强度,μT", "磁倾角,°", "温度,°C", "电压,V", "重力高边,°", "磁工具面,°", "测深输入", "选点时间"};
    String header2[] = {"序号", "采集点标记", "井斜,°", "磁方位,°", "校验和", "磁场强度,μT", "磁倾角,°", "温度,°C", "电压,V", "重力高边,°", "磁工具面,°", "测量时间", "  ", ""};
    String explain = "";
    String paper = "";
    String queryPoint = "";

    private Map<String, String> array2map(String[] str) {
        Map<String, String> data = new HashMap<String, String>();
        for (int i = 0; i < str.length; i++) {
            data.put(header1[i], str[i]);
        }
        return data;
    }

    private Map<String, String> array2map1(String[] str) {
        Map<String, String> data = new HashMap<String, String>();
        for (int i = 0; i < str.length; i++) {
            data.put(header2[i], str[i]);
        }
        return data;
    }

    private void supportingPaper(int i) {
        switch (i) {
            case 1:
                explain = "探管编号:";
                paper = filehead.tanguanbianhao;
//              paper = filehead.tanguanbianhao;
                break;
            case 3:
                explain = "区号:";
                paper = sp_teset.getString("quhao");
                break;
            case 2:
                explain = "队号:";
                paper = sp_teset.getString("duihao");
                break;
            case 4:
                explain = "井号:";
                paper = sp_teset.getString("jinghao");
                break;
            case 5:
                explain = "延时时间:";
                paper = sp_teset.getString("yanshishijian") + "分钟";
                break;
            case 6:
                explain = "间隔时间:";
                paper = sp_teset.getString("jiange") + "秒";
                break;
            case 7:
                explain = "选择采集点点数:";
                paper = String.valueOf(SaveListModel.listinfo.size()) + "个点";
                break;
            case 8:
                explain = "托盘位置深度:";
                paper = String.valueOf(sp_teset.getFloat("et_tuopan")) + "米";
                break;
            case 9:
                explain = "下铜头至减震器:";
                paper = String.valueOf(sp_teset.getFloat("et_jiachang")) + "米";
                break;
            case 10:
                explain = "第一测点深度:";
                paper = String.valueOf(sp_teset.getFloat("et_ceshen")) + "米";
                break;
            case 11:
                explain = "同步启动时间:";
                paper = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(sp_cexiestep.getLong("T1"));
                break;
            case 12:
                explain = "结束选点时间:";
                paper = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(sp_cexiestep.getLong("T4"));
                break;
            default:
                explain = "";
                paper = "";
                break;
        }
    }
    /*****************************************工具代码*********************************************/
    /**
     * 从字节数组到十六进制字符串转换
     */
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

    /**
     * 从字节数组到10进制短整型转换
     */
    private static short[] getbytes(byte info[]) {
        short byteValue[] = new short[info.length];
        int temp;
        for (int i = 0; i < info.length; i++) {
            temp = info[i] % 256;
            if (info[i] < 0) {
                byteValue[i] = (short) (temp >= -128 ? 256 + temp : temp);
            } else {
                byteValue[i] = (short) (temp > 127 ? temp - 256 : temp);
            }
        }
        return byteValue;
    }

    private String str2float(String str) {
        String str2 = "";
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57 || str.charAt(i) == 46) {
                    str2 += str.charAt(i);
                }
            }
        }
        System.out.println(str2);
        return str2;
    }

    private void getAllFiles(File path) {
        if (path.listFiles().length > 0) {
            for (File file : path.listFiles()) {
                for (File a : file.listFiles()) {
                    MyLogger.jLog().e(a.toString());
                    String mnull = a.toString();
                    if (mnull.substring(mnull.length() - 4, mnull.length()).equals("null"))
                        a.delete();
                }
            }
        }
    }
/******************************************************BLE开始******************************************************************/

    /**
     * BLE 回调
     */
    private void Blecallback() {
        /***       Ble扫描回调*/
        MainActivity.mBleService.setOnLeScanListener(new BleService.OnLeScanListener() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                //每当扫描到一个Ble设备时就会返回，（扫描结果重复的库中已处理）
            }
        });
        MainActivity.mBleService.setOnConnectListener(new BleService.OnConnectListener() {
            @Override
            public void onConnect(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    MyLogger.jLog().e("Ble连接已断开");
                    mBleHandler.sendEmptyMessage(TXSB);
                } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                    MyLogger.jLog().e("Ble正在连接");
                } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                    MyLogger.jLog().e("Ble已连接");
//                    MainActivity.mBleService.getBluetoothGatt().discoverServices();
                    mBleHandler.sendEmptyMessage(0x11112);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                    MyLogger.jLog().e("Ble正在断开连接");
                }
            }
        });
        //Ble服务发现回调
        MainActivity.mBleService.setOnServicesDiscoveredListener(new BleService.OnServicesDiscoveredListener() {
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                MyLogger.jLog().e("发现服务回调");
                mBleHandler.sendEmptyMessage(0x11111);
                /**
                 * 工作者线程（Thread  Runnable AsyncTask ）
                 * UI线程（主线程）
                 * 在回调里面调用一些控件 报错只能在UI线程中操作控件
                 */
            }
        });
        //Ble数据回调
        MainActivity.mBleService.setOnDataAvailableListener(new BleService.OnDataAvailableListener() {
            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                //处理特性读取返回的数据
                MyLogger.jLog().e("onCharacteristicRead" + Arrays.toString(characteristic.getValue()));
            }

            /**
             * auter:AChao
             * time:2016-4-1 09:10:07
             * version:1.0
             * change:1.0:              1.考虑误码率
             *                          2.考虑一整包数据中是否含有包头
             *                          3.Message复位，如超过70ms未执行回调，已处理完一整包数据并进行发送。
             */
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                synchronized (obj) {
                    if (characteristic.getValue()[0] == -1 && (characteristic.getValue()[1] == 3 || characteristic.getValue()[1] == 4)) {
                        if (characteristic.getValue()[2] < 0) {
                            /**如果出现负数 则255+负数+1*/
                            DataLength = 255 + characteristic.getValue()[2] + 1 + 5;
                        } else {
                            DataLength = characteristic.getValue()[2] + 5;
                        }
                        DATA = new byte[DataLength];
                        MyLogger.jLog().e(DataLength);
                        if (DataLength > 20) {
                            for (int i = 0; i < 20; i++) DATA[i] = characteristic.getValue()[i];
                        } else {
                            for (int i = 0; i < DataLength; i++)
                                DATA[i] = characteristic.getValue()[i];
                        }
                    } else {
                        for (int i = 0; i < characteristic.getValue().length; i++)
                            DATA[DataNum * 20 + i] = characteristic.getValue()[i];
                    }
                    DataNum++;
                    mBleHandler.removeMessages(BLE_TYPE);
                    mBleHandler.sendMessageDelayed(mBleHandler.obtainMessage(BLE_TYPE, DATA), 300);
                }
            }
        });
    }

    /**
     * @param _type 发送指令类型
     * @param num   获取数据长度，已在Modbus赋值  XZXS单独填写
     */
    public void ble_send(int _type, int num) {
        try {
            Thread.sleep(100);
            MainActivity.mBleService.writeCharacteristic(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID, Modbus.Read0304H(_type, num));
            BLE_TYPE = _type;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param _type 发送指令类型
     * @param start 起始地址位置
     * @param num   获取数据长度
     *              XZXS单独使用方法
     */
    public void ble_send(int _type, int start, int num) {
        try {
            Thread.sleep(100);
            MainActivity.mBleService.writeCharacteristic(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID, Modbus.Read0304H(_type, start, num));
            BLE_TYPE = _type;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
/******************************************************BLE结束******************************************************************/

public void LsNum(){
    if (TimeData.LsNum != 0){
        TimeData.LsNum -= 1;
        sp_cexiestep.putValue("LsNum", TimeData.LsNum);
    }
}
}
