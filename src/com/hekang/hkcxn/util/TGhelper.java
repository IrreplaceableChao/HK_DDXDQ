package com.hekang.hkcxn.util;


import android.util.Log;

import com.hekang.hkcxn.tanguan.TanGuan;
import com.hekang.hkcxn.tanguan.TanguanBuChang;

import java.text.DecimalFormat;

public class TGhelper {

	float c_hs,c_xz ;

	public byte[] bg_send(int kk , int c_num3){
		byte[] send = new byte[38];
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
			c_js.gx = c_js.gx + (ls[0] + ls[0 + 8])/2;
		    c_js.gy = c_js.gy + (ls[1] + ls[1 + 8])/2;
		    if(c_d[14]!=0){
		    	c_js.gz = c_js.gz + ls[2] + ls[2 + 8];
		    }else{
		    	c_js.dianya = c_js.dianya + (ls[2] + ls[2 + 8])/2;
		    }
		    c_js.mx = c_js.mx + (ls[3] + ls[3 + 8] - ls[6] - ls[6 + 8])/2;
		    c_js.my = c_js.my + (ls[4] + ls[4 + 8] - ls[6] - ls[6 + 8])/2;
		    c_js.mz = c_js.mz + (ls[5] + ls[5 + 8] - ls[6] - ls[6 + 8])/2;
		    c_js.ut = c_js.ut + (ls[7] + ls[7 + 8])/2;
		       
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
		if(tg_type==22){
		    c_js.gx = (float) ((c_ls[0] * 256 + c_ls[1]) * 0.00244);
		    c_js.gy = (float) ((c_ls[2] * 256 + c_ls[3]) * 0.00244);
		    c_js.gz = (float) ((c_ls[4] * 256 + c_ls[5]) * 0.00244);
		    
		    c_js.fd = (float) ((c_ls[12] * 256 + c_ls[13]) * 0.00244);
		    
		    c_js.mx = (float) ((c_ls[6] * 256 + c_ls[7]) * 0.00244 - c_js.fd);
//		       if(c_js.mx >= 32768)  {c_js.mx = c_js.mx - 65536;}
//		     c_js.mx = (float) (c_js.mx * 0.0526 / 8);
		    
		    c_js.my = (float) ((c_ls[8] * 256 + c_ls[9]) * 0.00244 - c_js.fd);
//		    if(c_js.my>=32768){c_js.my = c_js.my - 65536;}
//		    c_js.my = (float) (c_js.my * 0.0526 / 8);
		    
		    c_js.mz = (float) ((c_ls[10] * 256 + c_ls[11]) * 0.00244 - c_js.fd);
//		    if(c_js.mz>= 32768){c_js.mz = c_js.mz - 65536;}
//		    c_js.mz = (float) (c_js.mz * 0.0526 / 8);
		    
		    c_js.dianya = (float) ((c_ls[14] * 256 + c_ls[15]) * 0.00244);
		    
		    k = c_ls[16] * 256 + c_ls[17];
		    c_js.wendu = ah_wendu(k);
		    
		}

		if(tg_type==14){
			c_js.gx = (float) ((c_ls[0] * 256 + c_ls[1]) * 0.000806 / 16);
			c_js.gy = (float) ((c_ls[2] * 256 + c_ls[3]) * 0.000806 / 16);
			c_js.gz = (float) ((c_ls[4] * 256 + c_ls[5]) * 0.000806 / 16);
			c_js.mx = c_ls[6] * 256 + c_ls[7];
			if(c_js.mx >= 32768){c_js.mx = c_js.mx - 65536;}
			c_js.mx = (float) (c_js.mx * 0.0526 / 6);
			c_js.my = c_ls[8] * 256 + c_ls[9];
			if(c_js.my>=32768){c_js.my = c_js.my - 65536;}
			c_js.my = (float) (c_js.my * 0.0526 / 6);
			c_js.mz = c_ls[10] * 256 + c_ls[11];
			if(c_js.mz>= 32768){c_js.mz = c_js.mz - 65536;}
			c_js.mz = (float) (c_js.mz * 0.0526 / 6);
			c_js.dianya = (float) ((c_ls[14] * 256 + c_ls[15]) * 0.000806 / 16)*c_d[24]+c_d[25];
			k = c_ls[12] * 256 + c_ls[13];
			c_js.wendu = ah_wendu(k);

		}

		MyLogger.jLog().e("gx"+c_js.gx+"gy"+c_js.gy+"gz"+c_js.gz+"mx"+c_js.mx+"my"+c_js.my+"mz"+c_js.mz+"dianya"+c_js.dianya+"wendu"+c_js.wendu+"ut"+c_js.ut);
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
		if(zt == 0){s1 = s1 + "单点方式，上次定时时间为" +ys + "分钟。";}
		if(zt == 1){ s1 = s1 + "定点方式，上次延时时间为" + ys + "分钟，采集了" + point + "点。";}
		if(zt == 2){s1 = s1 + "多点方式，上次延时时间为" + ys + "分钟，间隔时间为" + jg + "秒，采集了" + point + "点。";}
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
			if(k3 == (int)'f' || k3 == (int)'F' || k3 == (int)'g' || k3 == (int)'G' ){
				tg_type = 12;
			}
			if(k3 == (int)'s' || k3 == (int)'S' ){
				tg_type = 13;
			}
		}else if (k1 == (int)'6' && k2 == (int)'8'){
			/**
			 * author: A Chao
			 * Time: 2016-4-19 08:44:03
			 */
			if( k3 == (int)'a' || k3 == (int)'A'||k3 == (int)'s' || k3 == (int)'S' ){
				tg_type = 14;
			}
		}
		Log.d("TGhelper", "tg_type="+tg_type);
		return tg_type;
	}
	
	
	public float[] bg_show(TanGuan c_js,int tg_type,float c_d[],float N ){

		float k = 0,k1 = 0, mz0 = 0,ff = 0 , A = 0;
		float mx,my,mz,gx,gy,gz = 0;
		float N1,ma = 0,hs = 0,ms = 0,b,m,mi;
		float g ; //校验和
		int i;
		float c_dy[] = new float[8];
		float info[] = new float[10];
		//计算温度
		if(tg_type<5 || tg_type == 10){
			if(c_d[25] == 0){c_d[25]=(float) 0.000001;}
			c_js.wendu = (c_js.ut - c_d[24]) / c_d[25];  
		}
		k1 = c_js.wendu;//获取到了温度可对其进行操作--------------------------------
		info[1] = k1;
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
		Log.d("xiuzhenghou","gx="+gx+"&gy="+gy+"&gz="+gz+"&mx="+mx+"&my="+my+"&mz="+mz+"&wendu="+k1+"&dianya="+c_js.dianya);
		
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
		if(N != 1234){
			N1 = N;
		}
		info[2] = N1;
		Log.d("mylog","n1="+N1);

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
		info[3] = ma;
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
		info[4] = hs;
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
		info[5] = ms;
		/*  te_1(20).Text = Format(ms, "0.00") + "°"*/
		
		//磁场强度
		 m = (float) Math.sqrt(mx * mx + my * my + mz * mz);
		 info[7] = m;
		 /*te_1(10).Text = Format(m, "0.00") + "μT"*/
		//磁倾角mi
		mi = (float) (Math.atan(mz0 / Math.sqrt(Math.abs(m * m - mz0 * mz0))) * 57.3);
		info[6] = mi;
		/*te_1(21).Text = Format(mi, "0.00") + "°"*/
		//校验和
		g = (float) Math.sqrt(gx*gx+gy*gy+gz*gz);
		info[8] = g;
		Log.d("mylog","show ok");
		return info;
	}
	
	
//	获取修正系数方法       
	
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
			Log.e("xiuzhengxishu","第"+i+"个   "+geshi.format(c_d[i]));
		}

		return c_d;
	}
	
	public static float byte2int_Float(short A1,short A2,short A3,short A4) {
		short b[] = {A1,A2,A3,A4};
        int bits = b[3] & 0xff | (b[2] & 0xff) << 8 | (b[1] & 0xff) << 16 | (b[0] & 0xff) << 24;

        int sign = ((bits & 0x80000000) == 0) ? 1 : -1;
        int exponent = ((bits & 0x7f800000) >> 23);
        int mantissa = (bits & 0x007fffff);

        mantissa |= 0x00800000;
        // Calculate the result:
        float f = (float) (sign * mantissa * Math.pow(2, exponent - 150));

        return f;
        } 
//	补偿系数
	public TanguanBuChang[] getBuChangXiShu(short c_xs[]){
		TanguanBuChang c_bc[] = {new TanguanBuChang(),new TanguanBuChang(),new TanguanBuChang(),new TanguanBuChang(),new TanguanBuChang(),new TanguanBuChang()};
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
			Log.d("buchang","a1="+c_bc[i].A1+"  a2="+c_bc[i].A2+"  a3="+c_bc[i].A3+"  a4="+c_bc[i].A4);
		}
		return c_bc;
	}
	/**
	 * 类型   12   13
	 * @param c_js
	 * @param tg_type
	 * @param c_d
	 * @param c_bc
	 * @param N    井斜
	 * @return
	 */
	public float[] bg_show_f(TanGuan c_js,int tg_type,float c_d[] , TanguanBuChang c_bc[],float N){
		float k1 = 0, mz0 = 0 , a = 0,ab;
		float mx,my,mz,gx,gy,gz = 0;
		float mx1,my1,mz1,gx1,gy1,gz1 = 0;
		float N1,ma = 0,zl = 0,hs = 0,m,mi;
		float g ; //校验和
		float c_dy[] = new float[8];
		float info[] = new float[10];
		//计算温度
		k1 = c_js.wendu;//获取到了温度可对其进行操作--------------------------------
		info[1] = k1;
		//修正
		if((c_d[19] *k1 + c_d[13]) == 0 ){c_d[13] = (float) 0.000001;}
		c_dy[4] = (c_d[7] * k1 + c_d[1] + c_js.gy) / (c_d[19] * k1 + c_d[13]);
		gy = c_dy[4];
		if(gy == 0){gy = (float) 0.000001;}
		
		if((c_d[18] *k1 + c_d[12]) == 0 ){c_d[12] = (float) 0.000001;}
		c_dy[5] = (c_d[6] * k1 + c_d[0] + c_js.gx) / (c_d[18] * k1 + c_d[12]);
		gx = c_dy[5];
		if(gx == 0){gx = (float) 0.000001;}
		
		if(c_d[20] * k1 + c_d[14] == 0){c_d[14] = (float) 0.000001;}
		c_dy[6] = (c_d[8] * k1 + c_d[2] + c_js.gz) / (c_d[20] * k1 + c_d[14]);
		gz = c_dy[6];
		if(gz == 0){gz = (float) 0.000001;}
		
		if((c_d[21] *k1 + c_d[15]) == 0 ){c_d[15] = (float) 0.000001;}
		c_dy[7] = (c_d[9] * k1 + c_d[3] + c_js.mx) / (c_d[21] * k1 + c_d[15]);
		mx = c_dy[7];
		
		if((c_d[22] *k1 + c_d[16]) == 0 ){c_d[16] = (float) 0.000001;}
		c_dy[2] = (c_d[10] * k1 + c_d[4] + c_js.my) / (c_d[22] * k1 + c_d[16]);
		my = c_dy[2];
		
		if((c_d[23] *k1 + c_d[17]) == 0 ){c_d[17] = (float) 0.000001;}
		c_dy[3] = (c_d[11] * k1 + c_d[5] + c_js.mz) / (c_d[23] * k1 + c_d[17]);
		mz = c_dy[3];
		

		
//		if(c_d[14] != 0){
//			if(c_d[20] * k1 + c_d[14] == 0){c_d[14] = (float) 0.000001;}
//			c_dy[6] = (c_d[8] * k1 + c_d[2] + c_js.gz) / (c_d[20] * k1 + c_d[14]);
//			gz = c_dy[6];
//			if(gz == 0){gz = (float) 0.000001;}
//			if(tg_type == 9){gz = -gz;}
//		}
		
		mx = mx * c_d[27];
		my = my * c_d[27];
		mz = mz * c_d[27];
//		Log.e("xiuzhenghou","gx="+gx+"&gy="+gy+"&gz="+gz+"&mx="+mx+"&my="+my+"&mz="+mz+"&wendu="+k1+"&dianya="+c_js.dianya);
//		Log.e("xiuzhengh o u","="+c_bc[0].A1+ "="+c_bc[0].A2+"="+c_bc[0].A3 +"="+ c_bc[0].A4);
		gx1 = ah_js(gx, gy, gz, c_bc[0].A1, c_bc[0].A2, c_bc[0].A3, c_bc[0].A4);
		gy1 = ah_js(gx, gy, gz, c_bc[1].A1, c_bc[1].A2, c_bc[1].A3, c_bc[1].A4);
		gz1 = ah_js(gx, gy, gz, c_bc[2].A1, c_bc[2].A2, c_bc[2].A3, c_bc[2].A4);
		mx1 = ah_js(mx, my, mz, c_bc[3].A1, c_bc[3].A2, c_bc[3].A3, c_bc[3].A4) * 100;
		my1 = ah_js(mx, my, mz, c_bc[4].A1, c_bc[4].A2, c_bc[4].A3, c_bc[4].A4) * 100;
		mz1 = ah_js(mx, my, mz, c_bc[5].A1, c_bc[5].A2, c_bc[5].A3, c_bc[5].A4) * 100;
		
		gx = gx1;
		gy = gy1;
		gz = gz1;
		mx = mx1;
		my = my1;
		mz = mz1;
//		Log.e("buchanghou","gx="+gx+"&gy="+gy+"&gz="+gz+"&mx="+mx+"&my="+my+"&mz="+mz+"&wendu="+k1+"&dianya="+c_js.dianya);
		//井斜
		if(gz>0){
			N1 = (float) ((Math.atan(Math.sqrt(gx * gx + gy * gy) / (gz))) * 57.3);
		}else{
			N1 = (float) (180 - (Math.atan(Math.sqrt(gx * gx + gy * gy) / (-gz))) * 57.3);
		}
		if(N != 1234){
			N1 = N;
		}
		info[2] = N1;
		
		//校验和
		g = (float) Math.sqrt(gx*gx+gy*gy+gz*gz);
		info[8] = g;
		
		//显示方位
		a = (gx*my-gy*mx)*g;
		ab  = mz * (gx * gx + gy * gy) - gz * (gx * mx + gy * my);
		if(a==0){
			if(ab>0){
				ma = 0;
			}else{
				ma = 180;
			}
		}else if(ab == 0){
			if(a>0){
				ma = 90;
			}else{
				ma = 270;
			}
		}else if(a>0 && ab>0){
			ma = (float) (Math.atan(a/ab)*57.3);
		}else if(a>0 && ab<0){
			ma = (float) (Math.atan(a/ab)*57.3+180);
		}else if(a<0 && ab<0){
			ma = (float) (Math.atan(a/ab)*57.3+180);
		}else{
			ma = (float) (Math.atan(a/ab)*57.3+360);
		}
		info[3] = ma;
		//重力高边
		if(gy == 0){
			if(gx < 0){
				zl = 0;
			}else{
				zl = 180;
			}
		}else if(gx == 0){
			if(gy > 0){
				zl = 90;
			}else{
				zl = 270;
			}
		}else if(gx<0 && gy>0){
			zl = (float) (- Math.atan(gy / gx) * 57.3);
		}else if(gx>0 && gy>0){
			zl = (float) (180-Math.atan(gy / gx) * 57.3);
		}else if(gx>0 && gy<0){
			zl = (float) (180-Math.atan(gy / gx) * 57.3);
		}else{
			zl = (float) (360-Math.atan(gy / gx) * 57.3);
		}
//		c_hs = hs;
//		hs = hs - c_xz;
		zl = zl - c_xz;
//		hs = Float.parseFloat(df1.format(hs));
		if(zl<0){zl = zl +360;}
//		if(hs>=360){hs = hs - 360;}
		//s1 = df1.format(hs);
		info[4] = zl;

		
		//磁性高边
		if(my == 0){
			if(mx > 0){
				hs = 0;
			}else{
				hs = 180;
			}
		}else if(mx == 0){
			if(my < 0){
				hs = 90;
			}else{
				hs = 270;
			}
		}else if(mx > 0 && my<0){
			hs = (float) (-Math.atan(my/mx)*57.3);
		}else if(mx < 0 && my<0){
			hs = (float) (180-Math.atan(my/mx)*57.3);
		}else if(mx<0 && my>0){
			hs = (float) (180-Math.atan(my/mx)*57.3);
		}else{
			hs = (float) (360-Math.atan(my/mx)*57.3);
		}
		hs = hs - c_xz;
		if(hs < 0){hs = hs+360;}
		info[5] = hs;
		
		//磁场强度
		 m = (float) Math.sqrt(mx * mx + my * my + mz * mz);
		 info[7] = m;
		
		//磁倾角mi
		 mz0 = (float) (gx * mx + gy * my + mz * Math.cos(N1 / 57.3));
		mi = (float) (Math.atan(mz0 / Math.sqrt(Math.abs(m * m - mz0 * mz0))) * 57.3);
		info[6] = mi;
		

		return info;
	}
	
	public float ah_js(float x,float Y,float z,float A1,float A2,float A3,float A4){
		return ( x * A1 + Y * A2 + z * A3 + A4);
	}
	
	public int bg_show_dord(short k1,short k2,short k3, short k4, short k5,short k6, short k7,int tg_type){
//		Log.d("mylog","传入值为:"+k1+" "+k2+" "+k3+" "+k4+" "+k5+" "+k6+" "+k7+" "+tg_type+" ");
		byte zt = 0;
		long ys = 0,jg = 0,point = 0;
//		String s1;
//		s1 = "此探管工作在";
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
/*		if(zt == 0){s1 = s1 + "单点方式，上次定时时间为" +ys + "分钟。";}
		if(zt == 1){ s1 = s1 + "定点方式，上次延时时间为" + ys + "分钟，采集了" + point + "点。";}
		if(zt == 2){s1 = s1 + "多点方式，上次延时时间为" + ys + "分钟，间隔时间为" + jg + "秒，采集了" + point + "点。";}*/
		return zt;
	}
	
	public String getTGName(int gongzuofangshi,String tanguanname) {
		String name = "";
		String subname[] = tanguanname.split("-");
		if("51F".equals(subname[0])){
			switch (gongzuofangshi) {
			case 0:
				
				break;

			case 221:
				name = subname[0]+"S-"+subname[1];
				break;
			case 238:
				name = subname[0]+"M-"+subname[1];
				break;
			}
		}else{
			name = tanguanname;
		}
		return name;
	}
	
}





































