package com.hekang.hkcxn.BLE;

import com.hekang.hkcxn.util.MyLogger;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/3/22.
 * 说明
 * 用 >>                          不占用CPU进行运算
 * 不用  /  % 符号运算运算         占用CPU进行运算
 * 例子：
 * Buffer[2] = (byte) (c_num >> 8);
 * Buffer[3] = (byte) c_num;
 * Buffer[2] = (byte) (c_num/256);
 * Buffer[3] = (byte) (c_num%256);
 * */
public class Modbus {

    private static final int STOP = 10;   				//停止检测
    private static final int TG_TYPE = 11;				//探管类型
    private static final int GBXZ = 12; 				//高边修正
    private static final int GZZZ = 13; 				//工作状态
    private static final int XZXS = 14;					//修正系数
    private static final int TGJC = 15;					//探管检测
    private static final int BCXS = 16;					//补偿系数
    private static final int GZFS = 17;					//工作方式
    private static final int ALLSHUJU = 18;				// 采集数据
    private static final int DJCG = 102; 				// 队号、井号、测深、高边修正值
    private static final int SDZ = 105;					// 时间、点数、状态
    /**
     * 处理返回的数据
     */
    public static byte MB_RTU(byte[] Buffer, int FistAddr, int DataLen)  //CSV读函数
    {
        byte FunctionCode = 0x00;
        byte RXOKFlg = 0;
        int tempA = 0x00, tempB = 0x00;
        int i, bytes = 0x00;
        int[] My_DATA = new int[300];
        tempA = CRC16(Buffer, DataLen - 2);
        tempB = Buffer[DataLen - 1];
        tempB |= Buffer[DataLen - 2] << 8;            // 解析校验码
        if (tempA == tempB)                    // 校验码正确
        {
            FunctionCode = Buffer[1];
            bytes = Buffer[2] / 2;
            switch (FunctionCode) {
                case 0x03:
                    for (i = 0; i < bytes; i++) {
                        My_DATA[FistAddr + i] = (Buffer[3 + 2 * i] << 8) + Buffer[4 + 2 * i];
                        RXOKFlg = 0x03;
                    }
                    break;
                case 0x04:
                    for (i = 0; i < bytes; i++) {
                        My_DATA[FistAddr + i] = (Buffer[3 + 2 * i] << 8) + Buffer[4 + 2 * i];
                        RXOKFlg = 0x04;
                    }
                    break;
                default:
                    break;
            }
        }

        return RXOKFlg;
    }

    /**
     * 数据打包（4000是保持寄存器（0x03））（3000是输入寄存器（0x04））
     */
    public static byte[] Read0304H(int type, int m_num)  //CSV读函数
    {
        boolean is0X30 = true;
        int m_start = 0;
        switch (type) {
            case TG_TYPE:           //探管编号
                is0X30 = true;
                m_start = 169;
                m_num = 5;
                break;
            case XZXS:              //修正系数
                is0X30 = true;
                m_start = 99;
//                m_num = 146;
                break;
            case TGJC:              //探管检测
                is0X30 = false;
                m_start = 2;
                m_num = 10;
                break;
            case BCXS:              //BCXS
                is0X30 = true;
                m_start = 44;
                break;
            case GZFS:              //工作方式
                is0X30 = true;
                m_start = 38;
                m_num = 1;
                break;
            case SDZ:               //时间（延时，间隔）点数（采集点数）状态（工作状态）
                is0X30 = true;
                m_start = 1;
                m_num = 4;
                break;
            case DJCG:
                is0X30 = true;
                m_start = 14;
                m_num = 10;
                break;
            case ALLSHUJU:
                is0X30 = true;
                m_start = 250;
                m_num = 120;
                break;
            default:

                break;
        }
        byte[] Buffer = new byte[8];
        Buffer[0] = (byte) (0xff);
        Buffer[1] = (byte) (is0X30 ? 0x03 : 0x04);
        Buffer[2] = (byte) (m_start >> 8);   //首地址
        Buffer[3] = (byte) m_start;
        Buffer[4] = (byte) (m_num >> 8);
        Buffer[5] = (byte) m_num;
        Pocket(Buffer, (byte) 6);
        return Buffer;
    }

    /**
     * 数据打包（4000是保持寄存器（0x03））（3000是输入寄存器（0x04））
     */
    public static byte[] Read0304H(int type, int m_start, int m_num)  //CSV读函数
    {
        byte[] Buffer = new byte[8];
        Buffer[0] = (byte) (0xff);
        Buffer[1] = (byte) (0x03);
        Buffer[2] = (byte) (m_start >> 8);   //首地址
        Buffer[3] = (byte) m_start;
        Buffer[4] = (byte) (m_num >> 8);
        Buffer[5] = (byte) m_num;
        Pocket(Buffer, (byte) 6);
        return Buffer;
    }

    public static void Pocket(byte[] Data, byte D_Long) {
        int temp0 = 0x00;
        temp0 = CRC16(Data, D_Long);
        Data[D_Long + 1] = (byte) temp0;
        Data[D_Long] = (byte) (temp0 >> 8);
    }

    /**
     * 计算校验和
     **/
    public static int CRC16(byte[] buf, int cnt) {
        int i, j;
        int temp = 0, flag = 0, temp1 = 0;
        temp = 0xFFFF;
        for (i = 0; i < cnt; i++) {
            temp = temp ^ byteToInteger(buf[i]);
            for (j = 1; j <= 8; j++) {
                flag = temp & 0x0001;
                temp = temp >> 1;
                if (flag == 1)
                    temp = temp ^ 0xA001;
            }
        }
        temp1 = temp >> 8;
        temp = (temp << 8) | temp1;
        temp &= 0xFFFF;
        return (temp);
    }

    private static int byteToInteger(byte b) {
        int value;
        value = b & 0xff;
        return value;
    }
    /***************************************向保持寄存器写入命令-开始*********************************/
    /**
     * 停止指令
     * @return
     */
    public static byte[] sendStop(){
        byte[] Buffer = new byte[11];
        Buffer[0] = (byte) (0xff);
        Buffer[1] = (byte) (0x10);
        Buffer[2] = (byte) 0x00;
        Buffer[3] = (byte) 0x00;
        Buffer[4] = (byte) 0x00;
        Buffer[5] = (byte) 0x01;
        Buffer[6] = (byte) 0x02;
        Buffer[7] = (byte) 0xAA>>8;
        Buffer[8] = (byte) 0xAA;
        Pocket(Buffer, (byte) 9);
        return Buffer;
    }
    /**
     * 启动指令
     * @return
     */
    public static byte[] sendstart(){
        byte[] Buffer = new byte[11];
        Buffer[0] = (byte) 0xff;
        Buffer[1] = (byte) 0x10;
        Buffer[2] = (byte) 0x00;
        Buffer[3] = (byte) 0x00;
        Buffer[4] = (byte) 0x00;
        Buffer[5] = (byte) 0x01;
        Buffer[6] = (byte) 0x02;
        Buffer[7] = (byte) 0x00;
        Buffer[8] = (byte) 0x55;
        Pocket(Buffer, (byte) 9);
        return Buffer;
    }
    /**
     * 设置队号指令
     * @return
     */
    public static byte[] setTeam(String s){
        byte[] Buffer = new byte[19];
        byte k=18;
        int p =7 ;
        byte[] temp = null ;
        Buffer[0] = (byte) 0xff;
        Buffer[1] = 0x10;
        Buffer[2] = (byte) (0x00);
        Buffer[3] = (byte) (14);
        Buffer[4] = (byte) (0x00);
        Buffer[5] = (byte) (0x05);
        Buffer[6] = (byte) (0x10);
        try {
            temp = s.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            MyLogger.jLog().e("e:"+e);
        }
        for(int i=0;i<temp.length;i++){
            Buffer[p] = temp[i];
            p+=1;
        }
        for(int i = p ;i<k;i++){
            Buffer[i] = 32;
        }
        Pocket(Buffer, (byte) 17);
        return Buffer;
    }
    /***
     * 设置井号指令
     * @return
     */
    public static byte[] setWell(String s){
        byte[] Buffer = new byte[19];
        byte k=18;
        int p =7 ;
        byte[] temp = null ;
        Buffer[0] = (byte) 0xff;
        Buffer[1] = 0x10;
        Buffer[2] = (byte) (0x00);
        Buffer[3] = (byte) (19);
        Buffer[4] = (byte) (0x00);
        Buffer[5] = (byte) (0x05);
        Buffer[6] = (byte) (0x10);
        try {
            temp = s.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            MyLogger.jLog().e("e:"+e);
        }
        for(int i=0;i<temp.length;i++){
            Buffer[p] = temp[i];
            p+=1;
        }
        for(int i = p ;i<k;i++){
            Buffer[i] = 32;
        }
        Pocket(Buffer, (byte) 17);
        return Buffer;
    }
    /***
     * 批量写入  设置延时、间隔指令
     * @return
     */
    public static byte[] setTime(int j,int interval){
        byte[] Buffer = new byte[13];
//        int s = j-1;          HK68-01A探管 延时时间设置 不用减1分钟
        Buffer[0] = (byte) 0xff;
        Buffer[1] = 0x10;
        Buffer[2] = (byte) (0x02>>8);
        Buffer[3] = (byte) (0x02);
        Buffer[4] = 0;
        Buffer[5] = 0x02;
        Buffer[6] = (byte) 0x04;
        Buffer[7] = (byte) ((j*60)>>8);
        Buffer[8] = (byte) (j*60);
        Buffer[9] = (byte) (interval>>8);
        Buffer[10] = (byte) interval;
        Pocket(Buffer, (byte) 11);
        return Buffer;
    }
    /**************************************向保持寄存器写入命令-结束**********************************/
    /**
     * 计算返回指令是否正确
     *
     */
    public static boolean CRCIsTrue(byte[] Buffer)
    {
        int tempA = 0x00, tempB = 0x00, tempC = 0x00;
        int DataLen = Buffer.length;
        tempA = CRC16(Buffer, DataLen - 2);
        tempB = Buffer[DataLen - 1];
        if (tempB <0)tempB+=256;
        tempC = Buffer[DataLen - 2];
        if (tempC <0)tempC+=256;
        if (Buffer[0]!=-1)return false;
        if (Buffer[1]!=0x03&&Buffer[1]!=0x04&&Buffer[1]!=0x10)return false;
        if (tempA !=tempC*256+tempB)return false;
        return true;
    }
    /**

       JH=：[-1, 16, 0, 32, 0, 5, 16, 49, 32, 32, 32, 32, 32, 32, 32, 32, 32, 10, -60]
     --JH=：[-1, 16, 0, 25, 0, 5, 16, 49, 32, 32, 32, 32, 32, 32, 32, 32, 32, 51, -3]
       DH=：[-1, 16, 0, 21, 0, 5, 16, 49, 32, 32, 32, 32, 32, 32, 32, 32, 32, 63, -15]
     --DH=：[-1, 16, 0, 20, 0, 5, 16, 49, 32, 32, 32, 32, 32, 32, 32, 32, 32, 63, 48]
       间隔时间=：[-1, 16, 0, 3, 0, 2, 4, 60, 60, 31, 83]
     --间隔时间=：[-1, 16, 0, 2, 0, 2, 4, 60, 60, 30, -126]
       启动=：[-1, 16, 0, 1, 0, 1, 2, 0, 85, 47, -38]
     --启动=：[-1, 16, 0, 0, 0, 1, 2, 0, 85, 46, 11]*/
}
