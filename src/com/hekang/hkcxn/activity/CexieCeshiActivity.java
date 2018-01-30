package com.hekang.hkcxn.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hekang.R;
import com.hekang.hkcxn.BLE.BleService;
import com.hekang.hkcxn.BLE.Modbus;
import com.hekang.hkcxn.model.IsBLE;
import com.hekang.hkcxn.service.BluetoothService;
import com.hekang.hkcxn.tanguan.FileHead;
import com.hekang.hkcxn.tanguan.TanGuan;
import com.hekang.hkcxn.tanguan.TanguanBuChang;
import com.hekang.hkcxn.util.DialogUtil;
import com.hekang.hkcxn.util.MyLogger;
import com.hekang.hkcxn.util.PublicValues;
import com.hekang.hkcxn.util.SharedPreferencesHelper;
import com.hekang.hkcxn.util.SysExitUtils;
import com.hekang.hkcxn.util.TGhelper;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Set;

/**
 * 仪器自检
 * Created by ACHAO on 2015/5/6.
 */
public class CexieCeshiActivity extends MyBaseActivity {
    public final int TXSB = 9;
    public final int STOP = 10;                //停止检测
    public final int TG_TYPE = 11;             //探管类型
    public final int GBXZ = 12;                //高边修正
    public final int GZZZ = 13;                //工作状态
    public final int XZXS = 14;                    //修正系数
    public final int TGJC = 15;                    //探管检测
    public final int BCXS = 16;                    //补偿系数
    public final int GZFS = 17;                    //工作方式
    public final int ALLSHUJU = 18;                // 采集数据
    public final int DJCG = 102;                // 队号、井号、测深、高边修正值
    public final int SDZ = 105;                    // 时间、点数、状态

    public final int MESSAGE_STATE_CHANGE = 1;
    public final int MESSAGE_READ = 2;
    public final int MESSAGE_WRITE = 3;
    public final int MESSAGE_DEVICE_NAME = 4;
    public final int MESSAGE_TOAST = 5;
    TextView timeview, tanguanbianhao, tanguanwendu, dianchidianya, jiaoyanhe, jingxie;
    ImageView isok1, isok2, isok3, isok4;
    String s = "";
    double d;
    public final String TOAST = "toast";
    private final static byte[] hex = "0123456789ABCDEF".getBytes();
    DecimalFormat df1 = new DecimalFormat("000");
    DecimalFormat df2 = new DecimalFormat("#.000V");
    private TGhelper thelper = new TGhelper();
    int tg_type = 0;
    short dizhi = 0;
    short c_xs[] = new short[301];
    short c_bcxs[] = new short[301];
    int bcbiaoji = 1;
    float c_d[] = new float[44];
    TanguanBuChang t_bc[] = new TanguanBuChang[6];
    TanGuan tanguan = new TanGuan();
    float[] info = new float[10];
    int bc1 = 0, bc2 = 0, bc3 = 0, bc4 = 0;
    send send1 = new send();
    DecimalFormat df5 = new DecimalFormat("#0.00°");
    DecimalFormat df6 = new DecimalFormat("#0.00μT");
    DecimalFormat df3 = new DecimalFormat("#0.00℃");
    DecimalFormat df4 = new DecimalFormat("#0.000");
    String tanguanleixing;
    boolean wenduisok = true, dianyaisok = true, jiaoyanheisok = true, jingxieisok = true;
    Dialog dialog;
    int chonglianbiaoji = 1;
    SharedPreferencesHelper sp_cexiestep, sp2;
    int cxtype = 0;
    int count = 0;
    int dianyabiaoji = 0;
    int dianyabaioji_ = 0;
    int dianyabaioji_s = 1;
    //	int wendubiaoji = 0;''
    int jiaoyanhebiaoji = 0;
    int jiaoyanhebiaoji_ = 0;
    int jiaoyanhebiaoji_s = 1;
    String txtTanguanBianhao = "";
    boolean isnormalexit = true;
    int gongzuofangshi = 0;//单点为0，定点为1，多点为2；
    private Button tanguanjiance;

    private Button Back;
    Builder builder;
    ProgressBar mProgressBar;
    /***
     * 2.0蓝牙     时间：2016-3-16 08:49:31
     **/
    private BluetoothAdapter mBtAdapter;
    private BluetoothService mService = null;
    Set<BluetoothDevice> pairedDevices;
    /**
     * 4.0蓝牙      时间：2016-4-5 16:41:16
     */
    private static final Object obj = new Object();
    int DataLength = 0, DataNum = 0, BLE_TYPE = 0;
    byte[] DATA;
    DialogUtil dialogUtil;
    FileHead filehead = new FileHead();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SysExitUtils.getAppManager().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cexie_ceshi);

        sp_cexiestep = new SharedPreferencesHelper(this, "cexiestep");
        sp2 = new SharedPreferencesHelper(this, "config");
        cxtype = sp_cexiestep.getInt("cexietype");

        findViewById();

        setOnClickListener();

        init();
    }

    private void init() {

        dialogUtil = new DialogUtil(CexieCeshiActivity.this, "正在连接探管,请稍等!");
        dialogUtil.showDialog();

        /**获取手机配对过的探管数据**/

        MyLogger.jLog().e(sp2.getString("tanguanaddress"));
        if (sp2.getString("tanguanaddress") != null) {
            if (!MainActivity.mBleService.connect(sp2.getString("tanguanaddress"))) {
                MyLogger.jLog().e("txsb");
                mHandler.sendEmptyMessage(TXSB);
            }
        }
        Blecallback();
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
                    if (!IsFinish) mHandler.sendEmptyMessage(TXSB);
                } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                    MyLogger.jLog().e("Ble正在连接");
                } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                    MyLogger.jLog().e("Ble已连接");
                    mHandler.sendEmptyMessage(0x11112);
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
                mHandler.sendEmptyMessage(0x11111);
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
             * change:1.0:           1.考虑误码率
             *                       2.考虑一整包数据中是否含有包头
             *                       3.Message复位，如超过30ms未执行回调，已处理完一整包数据并进行发送。
             */
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
                synchronized (obj) {
                    MyLogger.jLog().e(Arrays.toString(characteristic.getValue()));
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
                        MyLogger.jLog().e(DataNum);
                        for (int i = 0; i < characteristic.getValue().length; i++)
                            DATA[DataNum * 20 + i] = characteristic.getValue()[i];
                        MyLogger.jLog().e(DataNum);
                    }
                    MyLogger.jLog().e("data:"+Arrays.toString(DATA));
                    DataNum++;
                    mHandler.removeMessages(BLE_TYPE);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(BLE_TYPE, DATA), 300);
                }
            }
        });
    }

    private boolean IsFinish = false;

    private void setOnClickListener() {
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send1.isok = false;
                stopTimer();
                IsFinish = true;
                MainActivity.mBleService.disconnect();
                SysExitUtils.getAppManager().finishActivity();
            }
        });

        tanguanjiance.setClickable(true);
        tanguanjiance.setEnabled(false);
        tanguanjiance.setTextColor(getResources().getColor(R.color.gray));
        tanguanjiance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//				 TODO Auto-generated method stub
                if (!dianyaisok && !jiaoyanheisok && !jingxieisok) {
                    send1.isok = false;

                    MyLogger.jLog().e(tanguanleixing);
                    if ("51F".equals(tanguanleixing) || "51S".equals(tanguanleixing) || "51E".equals(tanguanleixing) || "22E".equals(tanguanleixing) || "68A".equals(tanguanleixing) || "68S".equals(tanguanleixing)) {
                        Intent intent = new Intent(CexieCeshiActivity.this, TanGuanShezhiActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private TextView isCon;

    private void findViewById() {
        //		探管编号 电池电压 tiotext 校验和 井斜 对错 1234
        isCon = findViewById(R.id.team);
        tanguanbianhao = findViewById(R.id.probe_number);
        tanguanwendu = findViewById(R.id.probe_temperature);
        dianchidianya = findViewById(R.id.cell_voltage);
        jiaoyanhe = findViewById(R.id.efficacy);
        jingxie = findViewById(R.id.well_deflection);
        isok1 = findViewById(R.id.isok1);
        isok2 = findViewById(R.id.isok2);
        isok3 = findViewById(R.id.isok3);
        isok4 = findViewById(R.id.isok4);
        tanguanjiance = findViewById(R.id.zijianhege);
        Back = findViewById(R.id.back);
        mProgressBar = findViewById(R.id.pb_progressbar);
        cifagnweijiao = findViewById(R.id.time_cexie);
        dialog = new Dialog(CexieCeshiActivity.this, R.style.MyDialog);
        TextView selector_title = findViewById(R.id.selector_title);
        selector_title.setText("仪器自检");
    }

    TextView cifagnweijiao;

    private void startTimer() {
        mHandler.postDelayed(runnable, 1000);
    }

    private void stopTimer() {
        mHandler.removeCallbacks(runnable);
    }

    int mProgress = 100;
    /*	int mI=1;*/
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            mProgressBar.setProgress(mProgress);
            if (mProgress == 0) {
                send1.isok = false;
                if (!dianyaisok && !jiaoyanheisok && !jingxieisok) {
                } else {
                    mHandler.sendEmptyMessage(110);
                }
            }
            mProgress -= 5;
            mHandler.postDelayed(this, 1000);
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    int connect = 0;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            try {
                byte[] writeBuf = (byte[]) msg.obj;
                MyLogger.jLog().e("writeBuf:" + Arrays.toString(writeBuf) + "msg.arg1:" + msg.arg1 + "msg.what:" + msg.what);
                short[] temp = null;
                if (msg.what != 0x11111 || msg.what != 0x11112 || msg.what != TXSB) {
//                    MyLogger.jLog().e(Arrays.toString(writeBuf));
                    if (writeBuf != null) {
                        temp = getbytes(writeBuf);
                    }
                }
                if (IsBLE.isBle) DataNum = 0;
                switch (msg.what) {
                    case MESSAGE_STATE_CHANGE:
                        if (msg.arg1 == BluetoothService.STATE_CONNECTED) {
                            send1 = null;
                            send1 = new send();
                            send1.start();
                        }
                        break;
                    case MESSAGE_WRITE:
                        break;
                    case MESSAGE_TOAST:
                        if (chonglianbiaoji <= 3) {
                            mService.connect(PublicValues.device);
                            MyLogger.jLog().e("第" + chonglianbiaoji + "次链接");
                            chonglianbiaoji++;
                        } else {
                            dialogUtil.closeDialog();
                            MyLogger.jLog().e("MESSAGE_TOAST");
                            Dialogtishi();
                        }
                        break;
                    case TXSB:
                        dialogUtil.closeDialog();
                        Dialogtishi();
                        break;
                    case TG_TYPE:
                        /**
                         * 01E探管类型11/                         * 51F探管类型12/                         * 51S探管类型13/                         * 22E探管类型22
                         */
                        if (writeBuf[3] == (int) '6' && writeBuf[4] == (int) '8') {
                            tg_type = thelper.bg_tg_type(writeBuf[3], writeBuf[4], writeBuf[5]);
                        }
                        MyLogger.jLog().e("----->" + tg_type + "===" + Arrays.toString(writeBuf));

                        ble_send(GZFS, 0);
                        break;
                    case GZFS:
                        MyLogger.jLog().d("GZFS");
                            if (writeBuf[4] != -103 && writeBuf[3] != 15) {
                                dialogUtil.closeDialog();
                                new AlertDialog.Builder(CexieCeshiActivity.this).setTitle("提示").setMessage("        多点选点器程序不可用于启动定点探管。").setPositiveButton("返回主页", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        stopTimer();
                                        finish();
                                    }
                                    //                                    }).setNegativeButton("重新自检", new DialogInterface.OnClickListener() {
                                    //                                        public void onClick(DialogInterface dialog, int which) {
                                    //                                        }
                                }).setCancelable(false).show();
                            } else {

                                ble_send(XZXS, 110);
                            }
                        break;
                    case XZXS:
                        MyLogger.jLog().d("XZXS");
                            /**4.0蓝牙误码率 判断应答数据是否完成*/
                            MyLogger.jLog().e(Arrays.toString(writeBuf));

                            if (!Modbus.CRCIsTrue(writeBuf)) {
                                ble_send(XZXS, 110);
                                MyLogger.jLog().e("this is error!");
                                break;
                            }
                            /**4.0蓝牙模块存储修正系数包头+包尾5个字节从第三个字节开始*/
                            System.arraycopy(temp, 3, c_xs, 0, temp.length - 3);
                            txtTanguanBianhao = "" + (char) (c_xs[140]) + (char) (c_xs[141]) + (char) (c_xs[142]) + "-" + df1.format(c_xs[144] * 256 + c_xs[143]);
                            tanguanbianhao.setText(thelper.getTGName(gongzuofangshi, txtTanguanBianhao));
                            tanguanleixing = "" + (char) (c_xs[140]) + (char) (c_xs[141]) + (char) (c_xs[142]);
                            MyLogger.jLog().e("探管类型:" + tanguanleixing + " tg_type:" + tg_type);

                            mProgressBar.setVisibility(View.VISIBLE);
                            startTimer();
                            dialogUtil.closeDialog();
                            c_d = thelper.getXiuZhengXiShu(c_xs);
                            ble_send(BCXS, 47);
                        sp_cexiestep.putValue("tanguanleixing", tanguanleixing);
                        break;
                    case BCXS:
                        MyLogger.jLog().e("BCXS");
                            /**4.0蓝牙误码率，判断应答数据是否完成*/
                            if (!Modbus.CRCIsTrue(writeBuf)) {
                                MyLogger.jLog().e("BCXS重新发送");
                                ble_send(BCXS, 47);
                                break;
                            }
                            System.arraycopy(temp, 3, c_bcxs, 0, temp.length - 3);
                            t_bc = thelper.getBuChangXiShu(c_bcxs);
                            for (int i = 0; i < t_bc.length; i++) {
                                MyLogger.jLog().e("补偿系数" + t_bc[i].A1 + "     " + t_bc[i].A2 + "     " + t_bc[i].A3 + "     " + t_bc[i].A4 + "----");
                            }
                            ble_send900(TGJC, 0);
                        bcbiaoji++;
                        break;
                    case TGJC:
                        short getinfo[] = new short[37];
                            if (!Modbus.CRCIsTrue(writeBuf)) {
                                ble_send(TGJC, 0);
                                break;
                            }
                            System.arraycopy(temp, 3, getinfo, 0, temp.length - 3);
                            dialogUtil.closeDialog();
                            tanguanjiance.setEnabled(true);
                            tanguan = thelper.js_dy(14, getinfo, c_d);
                            info = thelper.bg_show_f(tanguan, 14, c_d, t_bc, 1234);
                        count -= 2;
                        bc1++;
                        dianyabaioji_++;
                        if (dianyaisok) {
                            dianchidianya.setText(df2.format(tanguan.dianya));
                            if (tg_type == 12 || tg_type == 13 || tg_type == 14) {
                                if (tanguan.dianya >= 5.0 && tanguan.dianya <= 6.0) {

                                    if ((dianyabaioji_ - dianyabaioji_s) == 1) {
                                        isok2.setImageResource(R.drawable.apply);
                                        dianyaisok = false;
                                    }
                                    dianyabaioji_s = dianyabaioji_;
                                } else {
                                    isok2.setImageResource(R.drawable.delete);
                                    dianyabiaoji++;
                                }
                            } else if (tg_type == 22) {
                                if (tanguan.dianya >= 6.3 && tanguan.dianya <= 8) {
                                    isok2.setImageResource(R.drawable.apply);
                                    dianyaisok = false;
                                } else {
                                    isok2.setImageResource(R.drawable.delete);
                                    dianyabiaoji++;
                                }
                            } else {
                                if (tanguan.dianya >= 7.5 && tanguan.dianya <= 9.6) {
                                    isok2.setImageResource(R.drawable.apply);
                                    dianyaisok = false;
                                } else {
                                    isok2.setImageResource(R.drawable.delete);
                                    dianyabiaoji++;
                                }
                            }
                        }
                        if (wenduisok) {
                            tanguanwendu.setText(df3.format(info[1]));
                            if (info[1] >= -20 && info[1] <= 50) {
                                if (bc1 >= 3) {
                                    wenduisok = false;
                                }
                            }
                        }
                        cifagnweijiao.setText(df5.format(info[3]));

                        jiaoyanhebiaoji_++;
                        if (jiaoyanheisok) {
                            jiaoyanhe.setText(df4.format(info[8]));
                            s = String.valueOf(info[8]);
                            d = Double.parseDouble(s);
                            if (d >= 0.99 && d <= 1.01) {
                                if ((jiaoyanhebiaoji_ - jiaoyanhebiaoji_s) == 1) {
                                    isok3.setImageResource(R.drawable.apply);
                                    jiaoyanheisok = false;
                                }
                                jiaoyanhebiaoji_s = jiaoyanhebiaoji_;
                            } else {
                                isok3.setImageResource(R.drawable.delete);
                                jiaoyanhebiaoji++;
                            }

                        }
                        if (jingxieisok) {
                            jingxie.setText(df5.format(info[2]));

                            if (info[2] <= 5) {
                                isok4.setImageResource(R.drawable.apply);
                                jingxieisok = false;
                            } else {
                                isok4.setImageResource(R.drawable.delete);
                            }
                        }
                        if (!dianyaisok && !jiaoyanheisok && !jingxieisok) {
                            tanguanjiance.setEnabled(true);
                            tanguanjiance.setTextColor(getResources().getColor(R.color.black));
                            send1.isok = false;
                            mProgressBar.setVisibility(View.INVISIBLE);
                            stopTimer();
                        } else {
                                ble_send900(TGJC, 0);
                        }
                        break;
                    case 10085:
                        if (isnormalexit) {
                            dialogUtil.closeDialog();
                            isnormalexit = false;
                            MyLogger.jLog().e("10085");
                            Dialogtishi();
                        }
                        break;

                    case 110:
                        stopTimer();
                        mProgressBar.setVisibility(View.INVISIBLE);
                        new AlertDialog.Builder(CexieCeshiActivity.this).setTitle("提示").setMessage("自检不合格,请重新自检。").setPositiveButton("返回主页", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                stopTimer();
                                finish();
                            }
                        }).setNegativeButton("重新自检", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                count = 0;
                                mProgress = 100;
                                jiaoyanhebiaoji_ = 0;
                                jiaoyanhebiaoji_s = 1;
                                mProgressBar.setProgress(mProgress);
                                mProgressBar.setVisibility(View.VISIBLE);
                                    if (MainActivity.mBleService.isConnect()) {
                                        mHandler.removeMessages(TGJC);
                                        ble_send900(TGJC, 0);
                                        MyLogger.jLog().e("未断开继续自检");
                                    } else {
                                        MyLogger.jLog().e("已断开");
                                    }
                                    startTimer();
                                isok2.setImageResource(R.drawable.bg);
                                isok3.setImageResource(R.drawable.bg);
                                isok4.setImageResource(R.drawable.bg);
                                tanguanwendu.setText("");
                                jingxie.setText("");
                                cifagnweijiao.setText("");
                                jiaoyanhe.setText("");
                                dianchidianya.setText("");
                                wenduisok = true;
                                dianyaisok = true;
                                jiaoyanheisok = true;
                                jingxieisok = true;
                                dialogUtil.showDialog();
                            }
                        }).show();

                        break;
                    case 0x11111:
                        MainActivity.mBleService.setCharacteristicNotification(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID, true);//设置通知
                        MainActivity.mBleService.readCharacteristic(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID);//读取数据
                        Thread.sleep(100);
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

    private class send extends Thread {
        boolean isok = true;
        Message msg = Message.obtain();

        @Override
        public void run() {
            try {
                MyLogger.jLog().e("send start!");
                mService.write(thelper.bg_send(STOP, 0), STOP);
                Thread.sleep(500);
                mService.write(thelper.bg_send(TG_TYPE, 0), TG_TYPE);
                Thread.sleep(500);
                mService.write(thelper.bg_send(16, 0x01a2), GZFS);
                Thread.sleep(300);
                for (dizhi = 0x0120; dizhi <= 0x01e0; dizhi += 0x20) {
                    mService.write(thelper.bg_send(14, dizhi), 14);
                    Thread.sleep(300);
                }
                if (tg_type == 12 || tg_type == 13) {
                    for (dizhi = 0xa0; dizhi <= 0xe0; dizhi += 0x20) {
                        mService.write(thelper.bg_send(16, dizhi), 16);
                        Thread.sleep(300);
                    }
                }
                /**
                 * 延时300毫秒为handler逻辑计算预留时间
                 */
                Thread.sleep(300);
                while (isok) {
                    mService.write(thelper.bg_send(15, 0), TGJC);
                    count++;
                    Thread.sleep(900);
                    if (count > 10) {
                        msg = Message.obtain();
                        msg.what = 10085;
                        mHandler.sendMessage(msg);
                        send1.isok = false;
                        MyLogger.jLog().e("是不是你的问题");
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
            MyLogger.jLog().e(_type + "<-----------------------");
            MyLogger.jLog().e(Arrays.toString(getbytes(Modbus.Read0304H(_type, num))));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ble_send900(int _type, int num) {
        try {
            Thread.sleep(800);
            MainActivity.mBleService.writeCharacteristic(MainActivity.SERVICE_UUID, MainActivity.CHARACTERISTIC_UUID, Modbus.Read0304H(_type, num));
            BLE_TYPE = _type;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    boolean Isbuilder = true;

    private void Dialogtishi() {
        if (!Isbuilder) {
            return;
        }
        stopTimer();
        chonglianbiaoji = 1;
        count = 0;
        mProgress = 100;
        jiaoyanhebiaoji_ = 0;
        jiaoyanhebiaoji_s = 1;
        mProgressBar.setProgress(mProgress);
        mProgressBar.setVisibility(View.INVISIBLE);
        isok2.setImageResource(R.drawable.bg);
        isok3.setImageResource(R.drawable.bg);
        isok4.setImageResource(R.drawable.bg);
        tanguanwendu.setText("");
        jingxie.setText("");
        cifagnweijiao.setText("");
        jiaoyanhe.setText("");
        dianchidianya.setText("");
        wenduisok = true;
        dianyaisok = true;
        jiaoyanheisok = true;
        jingxieisok = true;
        String strtxsb = "";
            strtxsb = getString(R.string.tongxunshibai68A);
        newAlertDialog(strtxsb);
        try {
            builder.show();
        } catch (Exception e) {
            newAlertDialog(strtxsb);
            builder.show();
        }
        Isbuilder = false;
    }

    private void newAlertDialog(String str) {
        builder = new AlertDialog.Builder(CexieCeshiActivity.this);
        builder.setTitle("提示").setMessage(str).setPositiveButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Isbuilder = true;
                send1.isok = false;
                CexieCeshiActivity.this.finish();
            }
        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialogUtil.showDialog();
                    MainActivity.mBleService.connect(sp2.getString("tanguanaddress"));//连接Ble
                Isbuilder = true;
            }
        });
    }

    protected void onSaveInstanceState(Bundle outState) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}
