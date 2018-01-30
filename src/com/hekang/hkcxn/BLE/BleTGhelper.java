package com.hekang.hkcxn.BLE;

import android.util.Log;

import com.hekang.hkcxn.util.MyLogger;

import java.util.Arrays;

/**
 * Created by Administrator on 2016/3/16.
 */
public class BleTGhelper {

/** private final int STOP = 10;   //停止检测
    private final int TG_TYPE = 11;//探管类型
    private final int GBXZ = 12; //高边修正
    private final int GZZZ = 13; //工作状态
    private  final int XZXS = 14;//修正系数
    private  final int TGJC = 15;//探管检测
    private  final int BCXS = 16;//补偿系数
    private  final int GZFS = 17;//工作方式*/
public byte[] ble_bg_send()
    {
        byte[] send = new byte[8];
        int k=6;
        send[0] = (byte) 0xff;
        send[1] = 0x04;
        send[2] = 0x00;
        send[3] = 0x02;
        send[4] = 0x00;
        send[5] = 0x05;
        int  temp0=0x00;
        temp0=  CRC16(send, 0, k);
        send[k+1]= (byte) temp0;
        send[k]  = (byte) (temp0>>8);
        MyLogger.jLog().e(Arrays.toString(send));
        return send;
    }
    public int CRC16(byte buf[],int start,int cnt)   //-----计算出2个字节CRC
    {

        int i,j;
        int temp=0,flag=0,temp1=0;
        temp =  0xFFFF;
        for (i=start; i<cnt; i++)
        {
            temp = temp ^ byteToInteger(buf[i]);
            for (j=1; j<=8; j++)
            {
                flag = temp & 0x0001;
                temp = temp >> 1;
                if (flag!=0)
                    temp = temp ^ 0xA001;
            }
        }
        temp1 = temp >> 8;
        temp  = ((temp << 8) | temp1);
        temp &= 0xFFFF;
        return(temp);
    }
    private int byteToInteger(byte b) {
        int value;
        value = b & 0xff;
        return value;
    }


    public byte[] bg_send(int kk , int c_num3){
        byte[] send = new byte[8];
        int k=7;
        int jyh;
        send[0] = (byte) 0xff;


        if(kk == 10){
            send[1] = (byte) 0x04;
            for (int j=2; j<k; j++){
                send[j] = 0x00;
            }
        }



//		发送停止指令
        if(kk == 10){
            send[1] = (byte) 0xb2;
            for (int j=2; j<k; j++){
                send[j] = 0x00;
            }
        }
/*	            读取除HK22S外的探管型号*/
        if(kk == 11){
            Log.d("mylog", "get探管型号");
            send[1]=0x00;
            send[2]=0x01;
            send[3]=(byte) 0xac;
            for(int j=4; j<k; j++){
                send[j] = 0x00;
            }
        }

/*	    Rem 读取高边修正值*/
        if(kk == 12){
            send[1] = 0x00;
            send[2] = 0x00;
            send[3] = 0x2a;
            for(int j=4; j<k; j++){
                send[j] = 0x00;
            }
        }

/*	    Rem 读取工作状态*/
        if(kk == 13){
            for(int j=1; j<k; j++){
                send[j] = 0x00;
            }
        }

/*	    Rem 读取补偿系数*/
        if(kk == 16){
            send[1] = 0;
            send[2] = (byte) (c_num3/256);
            send[3] = (byte) (c_num3 % 256);
            for(int j=4; j<k; j++){
                send[j] = 0x00;
            }
        }

/*	    Rem 读取修正系数*/
        if(kk == 14){
            send[1] = 0;
            send[2] = (byte) (c_num3/256);
            send[3] = (byte) (c_num3 % 256);
            for(int j=4; j<k; j++){
                send[j] = 0x00;
            }
        }

/*	    Rem 发送启动指令*/
        if(kk == 15){
            send[1] = (byte) 0xa2;
            for(int j=2; j<k; j++){
                send[j] = 0x00;
            }
        }

/*	    Rem 计算校验和*/
        jyh = 0;
        for(int j=0 ; j<k ; j++){
            jyh = jyh + send[j];
        }
        send[k] = (byte) (jyh%256);
        return send;
    }
}
