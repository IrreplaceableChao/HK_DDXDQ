package com.hekang.hkcxn.util;

import android.util.Log;

import com.hekang.hkcxn.tanguan.TanGuan;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class CopyOfTGhelper {
	DecimalFormat df1=new DecimalFormat("#.00");
	long c_num;
	int c_num1,c_num2,c_num4,c_num5;
	int c_num3;
	int tg_type;
	byte c_xs[] =  new byte[300];
	float[] c_ls = new float[37];
	t_duo c_js = new t_duo();
	public float c_d[] = new float[44];
	t_bc c_bc[] = new t_bc[6];
	float c_dy[] = new float[8];
	float c_hs,c_xz ;
	int send_num = 0;

	public byte[] bg_send(int kk , int c_num3){
		byte[] send = new byte[38];
		byte i;
		int k=37;
		int jyh;
		send[0] = 0x2a;
//		发送停止指令
		if(kk == 10){
			send[1] = (byte) 0xb2;
			for (int j=2; j<k; j++){
				send[j] = 0x00;
			}
		}
/*	            读取除HK22S外的探管型号*/
		if(kk == 11){
			Log.d("mylog","get探管型号");
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
			if(tg_type == 6){
				send[1] = (byte) 0xa1;
			}else{
				send[1] = (byte) 0xa2;
			}
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
	/*  传感器电压*/
	public TanGuan js_dy (int tg_type,short c_ls[],float c_d[]){
		TanGuan c_js = new TanGuan();
		float ls[] = new float[17];
		float k;
		if(tg_type == 10){
			for(int i=0 ; i<=15; i++){
				if(i<8){
					ls[i] = (float) ((c_ls[2*i]*256+c_ls[2 * i + 1]) * 0.00244);
				}else{
					ls[i] = (float) ((c_ls[2*i + 2]*256+c_ls[2 * i + 1 + 2]) * 0.00244);
				}
			}
			c_js.gx = c_js.gx + ls[0] + ls[0 + 8];
			c_js.gy = c_js.gy + ls[1] + ls[1 + 8];
			if(c_d[14]!=0){
				c_js.gz = c_js.gz + ls[2] + ls[2 + 8];
			}else{
				c_js.dianya = c_js.dianya + ls[2] + ls[2 + 8];
			}
			c_js.mx = c_js.mx + ls[3] + ls[3 + 8] - ls[6] - ls[6 + 8];
			c_js.my = c_js.my + ls[4] + ls[4 + 8] - ls[6] - ls[6 + 8];
			c_js.mz = c_js.mz + ls[5] + ls[5 + 8] - ls[6] - ls[6 + 8];
			c_js.ut = c_js.ut + ls[7] + ls[7 + 8];

		}
		if(tg_type==11 || tg_type==12 || tg_type==13){
			c_js.gx = (float) ((c_ls[0] * 256 + c_ls[1]) * 0.000806 / 8);
			c_js.gy = (float) ((c_ls[2] * 256 + c_ls[3]) * 0.000806 / 8);
			c_js.gz = (float) ((c_ls[4] * 256 + c_ls[5]) * 0.000806 / 8);
			c_js.mx = c_ls[6] * 256 + c_ls[7];
			if(c_js.mx >= 32768){c_js.mx = c_js.mx - 65536;}
			c_js.mx = (float) (c_js.mx * 0.0526 / 8);
			c_js.my = c_ls[8] * 256 + c_ls[9];
			if(c_js.my>=32768){c_js.my = c_js.my - 65536;}
			c_js.my = (float) (c_js.my * 0.0526 / 8);
			c_js.mz = c_ls[10] * 256 + c_ls[11];
			if(c_js.mz>= 32768){c_js.mz = c_js.mz - 65536;}
			c_js.mz = (float) (c_js.mz * 0.0526 / 8);
			c_js.dianya = (float) ((c_ls[14] * 256 + c_ls[15]) * 4.9 * 0.000806 / 8);
			k = c_ls[12] * 256 + c_ls[13];
			c_js.wendu = ah_wendu(k);

		}
		return c_js;
	}
	public float ah_wendu(float k){
		if(k - 32768>=0){
			k = k-65536;
		}
		return (125*k)/2000;
	}

	public String bg_show_zt(short k1,short k2,short k3, short k4, short k5,short k6, short k7,int tg_type){
		Log.d("mylog","传入值为:"+k1+" "+k2+" "+k3+" "+k4+" "+k5+" "+k6+" "+k7+" "+tg_type+" ");
		byte zt = 0;
		long ys = 0,jg = 0,point = 0;
		String s1;
		s1 = "此探管工作在";
		if(tg_type == 1||tg_type == 3||tg_type == 4||tg_type == 5||tg_type == 6||tg_type == 9||tg_type == 10||tg_type == 11||tg_type == 12||tg_type == 13){
			ys = k4;
			ys = (ys * 256 + k5) / 60;
			if(tg_type != 6){ys = ys+1;}
			if(k3 != 0xa1){
				zt = 0;
			}else{
				jg = k6;
				jg = jg * 256 + k7;
				point = k1;
				point = point * 256 + k2;
				if(jg ==1){
					zt = 1;
				}else{
					zt = 2;
				}
			}
		}
		DateFormat format = new SimpleDateFormat("HH");
		DecimalFormat df2=new DecimalFormat("000");
		if(zt == 0){s1 = s1 + "单点方式，上次定时时间为" + format.format(ys) + "分钟。";}
		if(zt == 1){ s1 = s1 + "定点方式，上次延时时间为" + ys + "分钟，采集了" + point + "点。";}
		if(zt == 2){s1 = s1 + "多点方式，上次延时时间为" + format.format(ys) + "分钟，间隔时间为" + format.format(jg) + "秒，采集了" + format.format(point) + "点。";}
		return s1;
	}

	public float bh_xz(byte k1, byte k2, byte k3, byte k4, byte k5) {
		float bh_xz = k1;
		bh_xz = bh_xz * 10 + k2;
		bh_xz = bh_xz * 10 + k3;
		bh_xz = bh_xz * 10 + k4;
		bh_xz = bh_xz * 10 + k5;
		bh_xz = bh_xz / 100;
		Log.d("mylog","c_xz = "+c_xz);
		return bh_xz;

	}

	public int bg_tg_type (byte k1, byte k2, byte k3){
		int tg_type = 0;
		if(k1 == (int)'5' && k2 == (int)'1'){
			if(k3 == (int)'d' || k3 == (int)'D' ){
				tg_type = 10;
			}
			if(k3 == (int)'e' || k3 == (int)'E' ){
				tg_type = 11;
			}
			if(k3 == (int)'f' || k3 == (int)'F' ||k3 == (int)'g' || k3 == (int)'G' ){
				tg_type = 12;
			}
			if(k3 == (int)'s' || k3 == (int)'S' ){
				tg_type = 13;
			}
		}
		Log.d("mylog", "tg_type="+tg_type);
		return tg_type;
	}


	public String[] bg_show(TanGuan c_js,int tg_type,float c_d[] ){
		DecimalFormat df1=new DecimalFormat("#0.00°");
		DecimalFormat df2=new DecimalFormat("#0.00μT");
		DecimalFormat df3=new DecimalFormat("#0.00℃");
		DecimalFormat df4=new DecimalFormat("#0.000");
		float k = 0,k1 = 0, mz0 = 0,ff = 0 , A = 0,gk;
		float mx,my,mz,gx,gy,gz = 0;
		float N1,ma = 0,hs = 0,ms = 0,b,m,mi;
		float g ; //校验和
		int i ,Y;
		String s1;
		float c_dy[] = new float[8];
		String info[] = new String[10];
		//计算温度
		if(tg_type<5 || tg_type == 10){
			if(c_d[25] == 0){c_d[25]=(float) 0.000001;}
			c_js.wendu = (c_js.ut - c_d[24]) / c_d[25];
		}
		k1 = c_js.wendu;//获取到了温度可对其进行操作--------------------------------
		info[1] = df3.format(k1);
		//修正
		if((c_d[19] *k1 + c_d[13]) == 0 ){c_d[13] = (float) 0.000001;}
		c_dy[4] = (c_d[7] * k1 + c_d[1] + c_js.gy) / (c_d[19] * k1 + c_d[13]);
		gy = c_dy[4];
		if(gy == 0){gy = (float) 0.000001;}

		if((c_d[18] *k1 + c_d[12]) == 0 ){c_d[12] = (float) 0.000001;}
		c_dy[5] = (c_d[6] * k1 + c_d[0] + c_js.gx) / (c_d[18] * k1 + c_d[12]);
		gx = c_dy[5];
		if(gx == 0){gx = (float) 0.000001;}

		if((c_d[22] *k1 + c_d[16]) == 0 ){c_d[16] = (float) 0.000001;}
		c_dy[2] = (c_d[10] * k1 + c_d[4] + c_js.my) / (c_d[22] * k1 + c_d[16]);
		my = c_dy[2];

		if((c_d[23] *k1 + c_d[17]) == 0 ){c_d[17] = (float) 0.000001;}
		c_dy[3] = (c_d[11] * k1 + c_d[5] + c_js.mz) / (c_d[23] * k1 + c_d[17]);
		mz = c_dy[3];

		if((c_d[21] *k1 + c_d[15]) == 0 ){c_d[15] = (float) 0.000001;}
		c_dy[7] = (c_d[9] * k1 + c_d[3] + c_js.mx) / (c_d[21] * k1 + c_d[15]);
		mx = c_dy[7];

		if(c_d[14] != 0){
			if(c_d[20] * k1 + c_d[14] == 0){c_d[14] = (float) 0.000001;}
			c_dy[6] = (c_d[8] * k1 + c_d[2] + c_js.gz) / (c_d[20] * k1 + c_d[14]);
			gz = c_dy[6];
			if(gz == 0){gz = (float) 0.000001;}
			if(tg_type == 9){gz = -gz;}
		}

		if(tg_type == 11){
			mx = mx * c_d[27];
			my = my * c_d[27];
			mz = mz * c_d[27];
		}
		
/*	    Rem 显示加速度值和磁感应强度值
	     te_1(8).Text = Format(gx, "0.000")
	     te_1(7).Text = Format(gy, "0.000")
	     If c_d(14) <> 0 Then
	        te_1(6).Text = Format(gz, "0.000")
	     Else
	        te_1(6).Text = "-"
	     End If
	     te_1(11).Text = Format(mx, "0.00") & "μT"
	     te_1(12).Text = Format(my, "0.00") & "μT"
	     te_1(13).Text = Format(mz, "0.00") & "μT"*/

		i = 0;
		k = Math.abs(1 - gy * gy - gx * gx);
		if(k ==0 ){k = (float) 0.000001;}
		if(c_d[14]==0) {i = 1;}

		if(i == 1){
			N1 = (float) ((Math.atan(Math.sqrt(gy * gy + gx * gx)/Math.sqrt(k)))*57.3);
			gz = 0 ;
		}else{
			if(gz == 0){gz = (float) 0.000001;}
			N1 = (float) ((Math.atan(Math.sqrt(gy * gy + gx * gx)/Math.sqrt(k)))*57.3);
			if(N1 > 66){
				if(gz<0){
					N1 = (float) ((Math.atan(Math.sqrt(gx * gx + gy * gy) / (-gz))) * 57.3);
				}else{
					N1 = (float) (180 - (Math.atan(Math.sqrt(gx * gx + gy * gy) / (gz))) * 57.3);
				}
			}else{
				if(gz > 0){
					N1 = (float) (180 - (Math.atan(Math.sqrt(gx * gx + gy * gy) / (gz))) * 57.3);
				}
			}
		}
		info[2] = df1.format(N1);
/*		   te_1(0).Text = Format(N1, "0.00") + "°"
				   If i = 1 Then
				    te_1(9).Text = Format(Sqr(gx * gx + gy * gy), "0.000")
				   Else
				    te_1(9).Text = Format(Sqr(gx * gx + gy * gy + gz * gz), "0.000")
				   End If*/
		//显示方位
		mz0 = (float) (gx * mx + gy * my + mz * Math.cos(N1 / 57.3));
		ff = gx * my - gy * mx;
		A = (float) (((-ff) * Math.cos(N1 / 57.3)) / (gx * mx + gy * my - mz0 * Math.sin(N1 / 57.3) * Math.sin(N1 / 57.3)));
		if(A<0 && ff<=0){
			ma = (float) (360 - Math.atan(Math.abs(A)) * 57.3);
		}
		if(A<0 && ff>0){
			ma = (float) (180 - Math.atan(Math.abs(A)) * 57.3);
		}
		if(A>=0 && ff<=0){
			ma = (float) (Math.atan(Math.abs(A)) * 57.3+180);
		}
		if(A>=0 && ff>0){
			ma = (float) (Math.atan(Math.abs(A)) * 57.3);
		}
		info[3] = df1.format(ma);
		/*te_1(1).Text = Format(ma, "0.00") + "°"*/
		//重力高边
		if(gx<0 && gy<0){
			hs = (float) (360 - Math.atan(Math.abs(gy / gx)) * 57.3);
		}
		if(gx<0 && gy>=0){
			hs = (float) (Math.atan(Math.abs(gy / gx)) * 57.3);
		}
		if(gx>=0 && gy<0){
			hs = (float) (Math.atan(Math.abs(gy / gx)) * 57.3+180);
		}
		if(gx>0 && gy>=0){
			hs = (float) (180-Math.atan(Math.abs(gy / gx)) * 57.3);
		}
		c_hs = hs;
		hs = hs - c_xz;
//		hs = Float.parseFloat(df1.format(hs));
		if(hs<0){hs = hs +360;}
//		if(hs>=360){hs = hs - 360;}
		//s1 = df1.format(hs);
		hs = hs%360;
		info[4] = df1.format(hs);
		/*te_1(2).Text = s1*/

		//磁性高边
		b = (float) (((gy * mz0 - my) * Math.sqrt(Math.abs(1 - gx * gx))) / ((mx - gx * mz0) * Math.sqrt(Math.abs(1 - gy * gy))));
		if(b<=0){
			if((mx-gx*mz0)<0){
				ms = (float) (180 - Math.atan(Math.abs(b)) * 57.3);
			}else{
				ms = (float) (360 - Math.atan(Math.abs(b)) * 57.3);
			}
		}
		if(b>0){
			if((mx-gx*mz0)<0){
				ms = (float) (Math.atan(Math.abs(b)) * 57.3+180);
			}else{
				ms = (float) (Math.atan(Math.abs(b)) * 57.3);
			}
		}
		ms = ms -c_xz;
		if(ms < 0){ms = ms+360;}
		//if(ms >= 360){ms = ms-360;}
		ms = ms%360;
		info[5] = df1.format(ms);
		/*  te_1(20).Text = Format(ms, "0.00") + "°"*/

		//磁场强度
		m = (float) Math.sqrt(mx * mx + my * my + mz * mz);
		info[7] = df2.format(m);
		 /*te_1(10).Text = Format(m, "0.00") + "μT"*/
		//磁倾角mi
		mi = (float) (Math.atan(mz0 / Math.sqrt(Math.abs(m * m - mz0 * mz0))) * 57.3);
		info[6] = df1.format(mi);
		/*te_1(21).Text = Format(mi, "0.00") + "°"*/
		//校验和
		g = (float) Math.sqrt(gx*gx+gy*gy+gz*gz);
		info[8] = df4.format(g);
		return info;
	}

	public float[] getXiuZhengXiShu(short c_xs[]){
		float c_d[] = new float[44];
		float k=0,l=0,m=0,n=0,p=0;
		DecimalFormat geshi=new DecimalFormat("#0.0000000");
		for (int i=0 ; i<=43; i++){
			c_d[i] = 0;
			k = c_xs[5 * i];
			l = c_xs[5 * i + 1];
			m = c_xs[5 * i + 2];
			n = c_xs[5 * i + 3];
			p = c_xs[5 * i + 4];
			if(p == 1){
				c_d[i] = k * 0x1000000 + l * 0x10000 + m * 0x100 + n;
				c_d[i] = c_d[i] / 1000;
				c_d[i] = c_d[i] / 100;
				c_d[i] = -c_d[i] / 100;
			}
			if(p == 0){
				c_d[i] = k * 0x1000000 + l * 0x10000 + m * 0x100 + n;
				c_d[i] = c_d[i] / 100;
				c_d[i] = c_d[i] / 1000;
				c_d[i] = c_d[i] / 100;
			}
			if(p == 2){
				c_d[i] = k * 0x1000000 + l * 0x10000 + m * 0x100 + n;
				c_d[i] = c_d[i] / 100;
				c_d[i] = c_d[i] / 100;
				c_d[i] = c_d[i] / 100;
			}
			c_d[i] = Float.parseFloat(geshi.format(c_d[i]));
		}
		return c_d;
	}

	public static float byte2int_Float(byte A1,byte A2,byte A3,byte A4) {
		byte b[] = {A1,A2,A3,A4};
		int bits = b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16 | (b[0] & 0xff) << 24;

		int sign = ((bits & 0x80000000) == 0) ? 1 : -1;
		int exponent = ((bits & 0x7f800000) >> 23);
		int mantissa = (bits & 0x007fffff);

		mantissa |= 0x00800000;
		// Calculate the result:
		float f = (float) (sign * mantissa * Math.pow(2, exponent - 150));

		return f;
	}
	public void bg_bc_load(){
		float k = 0;
		for (int i = 0 ; i<=5; i++){
			for(int j=0; j<=3; j++){
				k = byte2int_Float(c_xs[16 * i + j * 4], c_xs[16 * i + j * 4 + 1], c_xs[16 * i + j * 4 + 2], c_xs[16 * i + j * 4 + 3]);
				switch (j) {
					case 0:
						c_bc[i].A1 = k;
						break;
					case 1:
						c_bc[i].A2 = k;
						break;
					case 2:
						c_bc[i].A3 = k;
						break;
					case 3:
						c_bc[i].A4 = k;
						break;
				}
			}
		}
	}

	public void ms_1_OnComm(byte [] arr1){
		byte arr[] = new byte[38];
		arr = arr1;
		float ls[] = new float[35];
		float ls1 = 0;
		int Y = 0;
		String s1;
		long k1;
		byte len1;
		long i1;
		byte c_hk71 = 0;
		if(arr[0]!=0x2a){
			//数据头不为2a
			return;
		}
		k1 = 0;
		for(int i=0; i<37; i++){
			k1 = arr[i] + k1;
		}
		k1 = k1%256;
		if(k1 != arr[37]){
			//校验和不对 
			return;
		}

		if(send_num == 0){
			if(arr[1]!=0xb2){
				c_num2 = 0;
			}
		}

		if(send_num == 1){
			Log.d("dlog","send_num == 1");
			bg_tg_type(arr[1], arr[2], arr[3]);
			if(tg_type<10){
				//该软件只支持HK51系列电子测斜仪，探管编号的前两个字符必须为'51'
			}
			send_num = 2;
			//	bg_send(2);
			c_num2 = 0;
		}

		if(send_num == 2){
			Log.d("dlog","send_num == 2");
			c_xz = bh_xz(arr[1], arr[2], arr[3],arr[4], arr[5]);
			c_xz = 0;
			send_num = 3;
			//	bg_send(3);
			c_num2 = 0;
		}

		if(send_num == 3){
			Log.d("dlog","send_num == 3");
			//	bg_show_zt(arr[1], arr[2], arr[4], arr[6], arr[7], arr[8], arr[9], c_hk71);
			send_num = 4;
			c_num3 = 0;
			//		bg_send(4);
			c_num2 = 0;
		}

		if(send_num == 6){
			Log.d("dlog","send_num == 6");
			for(int i = 0 ;i<=31; i++){
				c_xs[c_num3 - 0xa0 + i] = arr[i + 1];
			}
			if(c_num3 != 0xe0){
				c_num3 = c_num3 + 0x20;
				//		bg_send(send_num);
				return;
			}
			bg_bc_load();
			send_num = 5;
			c_num1 = 0;
			//	bg_send(5);
			c_num2 = 0;
		}

		if(send_num == 4){
			Log.d("dlog","send_num == 4");
			for(int i=0 ; i<=31 ; i++){
				c_xs[c_num3 - 0x120 + i] = arr[i + 1];
			}
			if(c_num3 != 0x01e0){
				c_num3 = c_num3 + 0x20;
				//		bg_send(send_num);
				return;
			}
/*		       te_1(5).Text = Chr(c_xs(140)) + Chr(c_xs(141)) + Chr(c_xs(142)) + "-" + Format((c_xs(144) * 256 + c_xs(143)) Mod 1000, "000")
		    	       q_tg_num = te_1(5).Text*/
			//	bg_xszh();

			if(tg_type == 12 || tg_type == 13){
				send_num = 6;
				c_num1 = 0;
				c_num3 = 0xA0;
				//			bg_send(6);
				c_num2 = 0;
			}else{
				send_num = 5;
				c_num1 = 0;
				//			bg_send(5);
				c_num2 = 0;
			}
		}

		if(send_num == 5){
			Log.d("dlog","send_num == 5");
			if(c_num4 == 1||c_num4 == 2){
				//接收到停止或退出指令
				Log.d("dlog", "tinghzituichu");
			}
			//vb这里有个延时
			if(c_num1 == 0){
				Log.d("dlog", "c_num1 == 0");
				c_js.gx = 0;
				c_js.gy = 0;
				c_js.gz = 0;
				c_js.mx = 0;
				c_js.my = 0;
				c_js.mz = 0;
				c_js.ut = 0;
				c_js.dianya = 0;
			}
			for(int i=0; i<=36 ; i++){
				c_ls[i] = arr[i + 1];
			}
			//	js_dy();
			if((tg_type>=6 && tg_type<=9)||tg_type==11 || tg_type==12 || tg_type==13){
				Log.d("dlog", "tg_type>=6 && tg_type<=9)||tg_t");
				if(tg_type!=6 && tg_type!= 9 && tg_type!=11 && tg_type!=12 && tg_type!=13){
					Log.d("dlog", "tinghzituichu");
					c_js.gx = c_js.gx / 2;
					c_js.gy = c_js.gy / 2;
					c_js.gz = c_js.gz / 2;
					c_js.mx = c_js.mx / 2;
					c_js.my = c_js.my / 2;
					c_js.mz = c_js.mz / 2;
					c_js.dianya = c_js.dianya / 2;
				}
			}else{
				c_num1 = c_num1 + 1;
				Log.d("dlog", "_num1 + 1;");
				if(c_num1 < 15){
					Log.d("dlog", "tinghzituichu");
					send_num = 5;
					//  	bg_send(5);
					c_num2 = 0;
				}
				Log.d("dlog", "c_num2 = 0;");
				c_js.gx = c_js.gx / 30;
				c_js.gy = c_js.gy / 30;
				c_js.gz = c_js.gz / 30;
				c_js.my = c_js.my / 30;
				c_js.mz = c_js.mz / 30;
				c_js.dianya = c_js.dianya / 30;
				c_js.ut = c_js.ut / 30;
			}

			c_num = c_num + 1;
			Log.d("dlog","dianya="+c_js.dianya);
			Log.d("dlog","dianya="+c_js.qingjiao);
			Log.d("dlog","dianya="+c_js.mx);
			Log.d("dlog","dianya="+c_js.ceshen);
			Log.d("dlog","dianya="+c_js.cixing);
			Log.d("dlog","dianya="+c_js.gx);
			Log.d("dlog","dianya="+c_js.gaobian);
			Log.d("dlog","dianya="+c_js.wendu);

			if(c_js.dianya != 0){
				Log.d("dlog", "c_js.dianya != 0");
				//te_1(3).Text = Format(c_js.dianya, "0.000") + "V"电压的值
				c_num1 = 0 ;
				if(tg_type == 12 || tg_type ==13){
					//bg_show_f
				}else{
					//bg_show();
					Log.d("dlog", "show");
				}
				//		bg_send(5);
				c_num2 = 0;
			}
		}
	}



	private class t_duo{
		boolean xz;
		float gx;
		float gy;
		float gz;
		float fangwei;
		float mx;
		float my;
		float mz;
		float qingjiao;
		float gaobian;
		float cixing;
		float wendu;
		float dianya;
		float cichang;
		float ut;
		float GT;
		float bt;
		float jxbh;
		float cqjbh;
		float fwbh;
		float zlbh;
		float cxbh;
		float dingshi;
		float ceshen;
		byte xg;
		String sj;
	}

	private class t_bc{
		float A1 ;
		float A2 ;
		float A3 ;
		float A4 ;
	}
}





































