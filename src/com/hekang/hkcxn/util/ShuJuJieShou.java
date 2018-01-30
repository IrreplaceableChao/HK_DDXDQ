package com.hekang.hkcxn.util;

import android.util.Log;

import com.hekang.hkcxn.tanguan.ReceiveTanguan;
import com.hekang.hkcxn.tanguan.T_Sg;
import com.hekang.hkcxn.tanguan.TanGuan;
import com.hekang.hkcxn.tanguan.TanguanBuChang;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShuJuJieShou extends TGhelper{
	@Override
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
	   	
	   	if(kk > 11){
	   		send[1] = 0;
		    send[2] = (byte) (c_num3/256);
		    send[3] = (byte) (c_num3 % 256);
	   		for(int j=4; j<k; j++){
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
	public TanGuan js_dy (int tg_type,short c_ls[],float c_d[],int kk){
		TanGuan c_js = new TanGuan();
		float ls[] = new float[17];
		float k;
		if(tg_type == 10){
			for(int i=0 ; i<=7; i++){
					ls[i] = (float) ((c_ls[2*i + kk * 18]*256+c_ls[2 * i + 1+ kk * 18]) * 0.00244/16);
			}
			c_js.gx = ls[0] ;
		    c_js.gy = ls[1] ;
		    if(c_d[14]!=0){
		    	c_js.gz = ls[2];
		    }else{
		    	c_js.dianya = ls[2];
		    }
		    c_js.mx = ls[3] - ls[6];
		    c_js.my = ls[4] - ls[6];
		    c_js.mz = ls[5] - ls[6];
		    c_js.ut = ls[7] ;
		       
		}
		if(tg_type==11 || tg_type==12 || tg_type==13){
		    c_js.gx = (float) ((c_ls[kk * 18 + 0] * 256 + c_ls[kk * 18 + 1]) * 0.000806 / 8);
		    c_js.gy = (float) ((c_ls[kk * 18 + 2] * 256 + c_ls[kk * 18 + 3]) * 0.000806 / 8);
		    c_js.gz = (float) ((c_ls[kk * 18 + 4] * 256 + c_ls[kk * 18 + 5]) * 0.000806 / 8);
		    c_js.mx = c_ls[kk * 18 + 6] * 256 + c_ls[kk * 18 + 7];
		    if(c_js.mx >= 32768){c_js.mx = c_js.mx - 65536;}
		    c_js.mx = (float) (c_js.mx * 0.0526 / 8);
		    c_js.my = c_ls[kk * 18 + 8] * 256 + c_ls[kk * 18 + 9];
		    if(c_js.my>=32768){c_js.my = c_js.my - 65536;}
		    c_js.my = (float) (c_js.my * 0.0526 / 8);
		    c_js.mz = c_ls[kk * 18 + 10] * 256 + c_ls[kk * 18 + 11];
		    if(c_js.mz>= 32768){c_js.mz = c_js.mz - 65536;}
		    c_js.mz = (float) (c_js.mz * 0.0526 / 8);
			c_js.dianya =  (float) ((c_ls[kk * 18 + 14] * 256 + c_ls[kk * 18 + 15]) * 4.9 * 0.000806 / 8);
		    k = c_ls[kk * 18 + 12] * 256 + c_ls[kk * 18 + 13];
		    c_js.wendu = ah_wendu(k);
		}
		if(tg_type==22){
//			接收数据
		    c_js.gx = (float) ((c_ls[kk * 18 +0] * 256 + c_ls[kk * 18 +1]) * 0.00244/16);
		    c_js.gy = (float) ((c_ls[kk * 18 +2] * 256 + c_ls[kk * 18 +3]) * 0.00244/16);
		    c_js.gz = (float) ((c_ls[kk * 18 +4] * 256 + c_ls[kk * 18 +5]) * 0.00244/16);
		    c_js.fd = (float) ((c_ls[kk * 18 +12] * 256 + c_ls[kk * 18 +13]) * 0.00244/16);
		    c_js.mx = (float) ((c_ls[kk * 18 +6] * 256 + c_ls[kk * 18 +7])* 0.00244/16 - c_js.fd);
		    c_js.my = (float) ((c_ls[kk * 18 +8] * 256 + c_ls[kk * 18 +9])* 0.00244/16  - c_js.fd);
		    c_js.mz = (float) ((c_ls[kk * 18 +10] * 256 + c_ls[kk * 18 +11])* 0.00244/16 - c_js.fd);
		    c_js.dianya = (float) ((c_ls[kk * 18 +14] * 256 + c_ls[kk * 18 +15]) * 0.00244/16);
		    k = c_ls[16] * 256 + c_ls[kk * 18 +17];
		    c_js.wendu = ah_wendu(k);
		}
		if(tg_type ==14){
			c_js.gx = (float) ((c_ls[kk * 16 + 0] * 256 + c_ls[kk * 16 + 1]) * 0.000806 / 16);
			c_js.gy = (float) ((c_ls[kk * 16 + 2] * 256 + c_ls[kk * 16 + 3]) * 0.000806 / 16);
			c_js.gz = (float) ((c_ls[kk * 16 + 4] * 256 + c_ls[kk * 16 + 5]) * 0.000806 / 16);
			c_js.mx = c_ls[kk * 16 + 6] * 256 + c_ls[kk * 16 + 7];
			if(c_js.mx >= 32768){c_js.mx = c_js.mx - 65536;}
			c_js.mx = (float) (c_js.mx * 0.0526 / 6);
			c_js.my = c_ls[kk * 16 + 8] * 256 + c_ls[kk * 16 + 9];
			if(c_js.my>=32768){c_js.my = c_js.my - 65536;}
			c_js.my = (float) (c_js.my * 0.0526 / 6);
			c_js.mz = c_ls[kk * 16 + 10] * 256 + c_ls[kk * 16 + 11];
			if(c_js.mz>= 32768){c_js.mz = c_js.mz - 65536;}
			c_js.mz = (float) (c_js.mz * 0.0526 / 6);
			c_js.dianya =  (float) ((c_ls[kk * 16 + 14] * 256 + c_ls[kk * 16 + 15]) * 0.000806 / 16)*c_d[24]+c_d[25];
			k = c_ls[kk * 16 + 12] * 256 + c_ls[kk * 16 + 13];
			c_js.wendu = ah_wendu(k);
		}
		return c_js;
	}
	
	
//	计算井斜

	public float jiSuanJingXie(String wd1,float wd,float gx, float gy , float gz,int tg_type,TanguanBuChang[] c_bc,float c_d[]){
		float k1 = 0,n1,k;
		int i;
		float gx1 = 0,gy1 = 0,gz1 = 0,mx1,my1,mz1;
		float c_dy[] = new float[8];
		//计算温度
		if(tg_type == 10){
			if (c_d[25] == 0) c_d[25] = (float) 0.000001;
			k1 = (wd - c_d[24])/c_d[25];
		}else{
			try {
				k1 = Float.parseFloat(wd1);
			} catch (Exception e) {
				// TODO: handle exception
				Log.d("shujujieshouutil","wendu=0");
			}
			
		}
		//修正
		if((c_d[19] *k1 + c_d[13]) == 0 ){c_d[13] = (float) 0.000001;}
		c_dy[4] = (c_d[7] * k1 + c_d[1] + gy) / (c_d[19] * k1 + c_d[13]);
		gy = c_dy[4];
		if(gy == 0){gy = (float) 0.000001;}
		
		if((c_d[18] *k1 + c_d[12]) == 0 ){c_d[12] = (float) 0.000001;}
		c_dy[5] = (c_d[6] * k1 + c_d[0] + gx) / (c_d[18] * k1 + c_d[12]);
		gx = c_dy[5];
		if(gx == 0){gx = (float) 0.000001;}
		if(c_d[14] != 0){
			if(c_d[20] * k1 + c_d[14] == 0){c_d[14] = (float) 0.000001;}
			c_dy[6] = (c_d[8] * k1 + c_d[2] + gz) / (c_d[20] * k1 + c_d[14]);
			gz = c_dy[6];
			if(gz == 0){gz = (float) 0.000001;}
			if(tg_type == 9){gz = -gz;}
		}
		
		if(tg_type == 12 || tg_type == 13){
			gx1 = ah_js(gx, gy, gz, c_bc[0].A1, c_bc[0].A2, c_bc[0].A3, c_bc[0].A4);
			gy1 = ah_js(gx, gy, gz, c_bc[1].A1, c_bc[1].A2, c_bc[1].A3, c_bc[1].A4);
			gz1 = ah_js(gx, gy, gz, c_bc[2].A1, c_bc[2].A2, c_bc[2].A3, c_bc[2].A4);
			gx = gx1;
			gy = gy1;
			gz = gz1;
			if(gz>0){
				n1 = (float) ((Math.atan(Math.sqrt(gx * gx + gy * gy) / (gz))) * 57.3);
			}else{
				n1 = (float) (180 - (Math.atan(Math.sqrt(gx * gx + gy * gy) / (-gz))) * 57.3);
			}
			return n1;
		}
		
		i = 0;
		k = Math.abs(1-gy * gy - gx * gx);
		if(k == 0) k = (float) 0.000001;
		if(c_d[14] == 0) i=1;
		if(i == 1){
			n1 = (float) ((Math.atan(Math.sqrt(gx * gx + gy * gy) / Math.sqrt(k))) * 57.3);
			gz = 0;
		}else{
			if(gz == 0) gz = (float) 0.000001;
			n1 = (float) ((Math.atan(Math.sqrt(gx * gx + gy * gy) / Math.sqrt(k))) * 57.3);
			if(n1 > 66){
				if(gz < 0){
					n1 = (float) ((Math.atan(Math.sqrt(gx * gx + gy * gy) / (-gz))) * 57.3);
				}else{
					n1 = (float) (180-(Math.atan(Math.sqrt(gx * gx + gy * gy) / (gz))) * 57.3);
				}
			}else{
				if(gz > 0){
					n1 = (float) (180-(Math.atan(Math.sqrt(gx * gx + gy * gy) / (gz))) * 57.3);
				}
			}
		}
		return n1;
	}
	
	public long[] startpAndendp(long time1 , int max_point , int ys , int qd){
		long t1,k1,bh_dingdian,c_end_point;
		long[] startandend = new long [2];
		t1 = time1/1000;
		k1 = (t1 -ys - 60)/ qd +1;
		if(max_point < (k1 + 14)){
			bh_dingdian = -1;
			startandend[0] = bh_dingdian;
			startandend[1] = 0;
			return startandend;
		}
		if(t1 <(ys + 60)){
			bh_dingdian = -2;
			startandend[0] = bh_dingdian;
			startandend[1] = 0;
			return startandend;
		}
		
		k1 = k1 -1;
		bh_dingdian = 0x200 + k1 * 18;
		c_end_point = bh_dingdian + 14 * 18;
		startandend[0] = bh_dingdian;
		startandend[1] = c_end_point;
		return startandend;
	}
	
	
	/***
	 * 
	 * @param tg_type 探管类型
	 * @param c_bc  24个补偿系数
	 * @param c_d	float c_d[] = new float[44];   //存 44个修正系数
	 * @param bt_sj   T_Sg q_bt_sj [] = new T_Sg[16];
	 * @returnfloat 
	 */
	public ReceiveTanguan bg_cl3(int tg_type,TanguanBuChang[] c_bc,float c_d[],T_Sg bt_sj []){
		int j = 0;
		ReceiveTanguan receive = new ReceiveTanguan();
		TanGuan c_js = new TanGuan();
		float zl_start,zl,zl_max,k1,max1,min1,n1,c_jx;
		float cz[] = new float[13];
		String info [] = new String[20];
		String s1;
		boolean s = false;
		boolean c_b ;
		T_Sg q_bt_sj [] = new T_Sg[bt_sj.length];
		for(int i =0; i<bt_sj.length;i++){
			q_bt_sj[i] = bt_sj [i];
		}
		byte b ;
		
		for (int i=0; i<=14;i++){
			q_bt_sj[i].jx = jiSuanJingXie(q_bt_sj[i].wendu, q_bt_sj[i].ut, q_bt_sj[i].gx, q_bt_sj[i].gy, q_bt_sj[i].gz, tg_type, c_bc, c_d);	
			Log.d("mylog","canshu"+q_bt_sj[i].wendu+q_bt_sj[i].ut+q_bt_sj[i].gx+q_bt_sj[i].gy+q_bt_sj[i].gz);
			//Log.d("mylog","井斜"+i+"="+q_bt_sj[i].jx);
		}
		for(int i = 0 ; i<=12 ; i++){
			max1 = q_bt_sj[i].jx;
			min1 = q_bt_sj[i].jx;
			for(j = i+1;j<=i+2; j++){
				if(max1 < q_bt_sj[j].jx) max1 = q_bt_sj[j].jx;
				if(min1 >q_bt_sj[j].jx) min1 = q_bt_sj[j].jx;
			}
			cz[i] = max1 - min1;
		}
		
		zl_start = (float) 0.1;
		zl = (float) 0.05;
		zl_max = (float) 0.3;
		b = 0 ;
		
		while(b == 0){
			for (j = 0; j<=12 ; j++){
				if(cz[j] <= zl_start){
					b = 1 ;
					s = true;
					break;
				}
			}
			if(s) break;
			zl_start = zl_start + zl;
			if(zl_start > zl_max){
				b = 2;
				break;
			}
		}
		
		if (b == 2){
			c_b = false;
			//未找到有效点
			Log.d("mylog", "未找到有效点");
			return null;
		}
		
		//    te_1(22).Text = ah_second_zh_time((c_start_point - &H200) \ 18 + j + q_ys_time)
	    //    te_1(25).Text = "智能判读"
		receive.pandufangshi="智能判读";
		n1 = 0;
		for(int i=j; i<= j+2; i++){
			n1 = n1 + q_bt_sj[i].jx;
		}
		n1 = n1/3;
		c_jx = n1;//井斜
		receive.jingxie = n1;
		//te_1(0).Text = Format(N1, "0.00") + "°"
		
		c_js.gx = 0;
		for(int i=j; i<=j+2; i++){
			c_js.gx = c_js.gx + q_bt_sj[i].gx;
		}
		c_js.gx = c_js.gx / 3;
		
		c_js.gy = 0;
		for(int i=j; i<=j+2; i++){
			c_js.gy = c_js.gy + q_bt_sj[i].gy;
		}
		c_js.gy = c_js.gy / 3;
		
		c_js.gz = 0;
		for(int i=j; i<=j+2; i++){
			c_js.gz = c_js.gz + q_bt_sj[i].gz;
		}
		c_js.gz = c_js.gz / 3;
		
		c_js.mx = 0;
		for(int i=j; i<=j+2; i++){
			c_js.mx = c_js.mx + q_bt_sj[i].mx;
		}
		c_js.mx = c_js.mx / 3;
		
		c_js.my = 0;
		for(int i=j; i<=j+2; i++){
			c_js.my = c_js.my + q_bt_sj[i].my;
		}
		c_js.my = c_js.my / 3;
		
		c_js.mz = 0;
		for(int i=j; i<=j+2; i++){
			c_js.mz = c_js.mz + q_bt_sj[i].mz;
		}
		c_js.mz = c_js.mz / 3;
		
		c_js.ut = 0;
		for(int i=j; i<=j+2; i++){
			c_js.ut = c_js.ut + q_bt_sj[i].ut;
		}
		c_js.ut = c_js.ut / 3;
		
		c_js.wendu = 0;
		for(int i=j; i<=j+2; i++){
			try {
				c_js.wendu = c_js.wendu + Float.parseFloat(q_bt_sj[i].wendu);
			} catch (Exception e) {
				// TODO: handle exception
				Log.d("shujujieshouutil","wendu=0");
			}
			
		}
		c_js.wendu = c_js.wendu / 3;
		
		c_js.dianya = 0;
		for(int i=j; i<=j+2; i++){
				c_js.dianya = c_js.dianya + q_bt_sj[i].ub;
			
		}
		c_js.dianya = c_js.dianya / 3;
		Log.d("shujujieshoumylogfirst","gx="+c_js.gx+"&gy="+c_js.gy+"&gz="+c_js.gz+"&mx="+c_js.mx+"&my="+c_js.my+"&mz="+c_js.mz+"&ut="+c_js.ut+"&wendu="+c_js.wendu+"&dianya="+c_js.dianya);
		
		if(tg_type == 12 || tg_type == 13){
			//调用showf方法显示信息
			float tempstr[] = bg_show_f(c_js, tg_type, c_d, c_bc,n1);
			receive.dianya = c_js.dianya;
			receive.wendu = tempstr[1];
        	receive.cifangwei = tempstr[3];
        	receive.zhongligaobian = tempstr[4];
        	receive.diciqingjiao = tempstr[6];
        	receive.cichangqiangdu = tempstr[7];
        	//receive.jingxie = tempstr[2];
        	receive.cigongjumian = tempstr[5];
        	receive.jiaoyanhe = tempstr[8];
        	receive.time = j*1000;
		}else{
			//调用show方法显示信息
			float tempstr[] = bg_show(c_js, tg_type, c_d,1234);
			receive.dianya = c_js.dianya;
			receive.wendu = tempstr[1];
        	receive.cifangwei = tempstr[3];
        	receive.zhongligaobian = tempstr[4];
        	receive.diciqingjiao = tempstr[6];
        	receive.cichangqiangdu = tempstr[7];
        	//receive.jingxie = tempstr[2];
        	receive.cigongjumian = tempstr[5];
        	receive.jiaoyanhe = tempstr[8];
        	receive.time = j*1000;
		}
		return receive;
	}
	
	
	public List<Map<String, String>> rengongduqu(int tg_type,TanguanBuChang[] c_bc,float c_d[],TanGuan bt_sj [],int startpoint , int endpoint,int starttime,float xiuzhengvalue){
		DecimalFormat df2=new DecimalFormat("#0.00");
		DecimalFormat df3=new DecimalFormat("#0.000");
		DecimalFormat df1 = new DecimalFormat("00");
		ReceiveTanguan receive = new ReceiveTanguan();
		List<Map<String, String>> datas = new ArrayList<Map<String,String>>();
		Map<String, String> data = null;
		TanGuan c_js = new TanGuan();
		float tempqjbh = 0,zl,tempxiuzhengvalue,tempgongjumian,max1,min1,n1,c_jx,jx;
		float cz[] = new float[13];
		String info [] = new String[20];
		String s1;
		boolean s = false;
		boolean c_b ;
		TanGuan q_bt_sj [] = new TanGuan[bt_sj.length];
		for(int i =0; i<bt_sj.length;i++){
			q_bt_sj[i] = bt_sj [i];
		}
		
		for (int i = startpoint; i <= endpoint - 2; i++) {
			
			c_jx = 0;
			c_js.gx = 0;
			c_js.gy = 0;
			c_js.gz = 0;
			c_js.mx = 0;
			c_js.my = 0;
			c_js.mz = 0;
			c_js.ut = 0;
			c_js.dianya = 0;
			c_js.wendu = 0;
			min1 = 200;
			max1 = -200;
			for(int j=i; j<=i+2;j++){
				jx = jiSuanJingXie(q_bt_sj[i].wendu, q_bt_sj[j].ut, q_bt_sj[j].gx, q_bt_sj[j].gy, q_bt_sj[j].gz, tg_type, c_bc, c_d);	
				if( jx > max1 ){
					max1 = jx;
				}
				if(jx < min1){
					min1 = jx;
				}
				c_jx = c_jx + jx;
				c_js.gx = c_js.gx + q_bt_sj[j].gx;
				c_js.gy = c_js.gy + q_bt_sj[j].gy;
				c_js.gz = c_js.gz + q_bt_sj[j].gz;
				c_js.mx = c_js.mx + q_bt_sj[j].mx;
				c_js.my = c_js.my + q_bt_sj[j].my;
				c_js.mz = c_js.mz + q_bt_sj[j].mz;
				c_js.ut = c_js.ut + q_bt_sj[j].ut;
				c_js.wendu = c_js.wendu + q_bt_sj[j].wendu;
				c_js.dianya = c_js.dianya + q_bt_sj[j].dianya;
			}
			c_jx = c_jx / 3;
			c_js.gx = c_js.gx / 3;
			c_js.gy = c_js.gy / 3;
			c_js.gz = c_js.gz / 3;
			c_js.mx = c_js.mx / 3;
			c_js.my = c_js.my / 3;
			c_js.mz = c_js.mz / 3;
			c_js.ut = c_js.ut / 3;
			c_js.dianya = c_js.dianya / 3;
			c_js.wendu = c_js.wendu / 3;
			
			Log.d("shujujieshoumylog","第几次"+i+"###gx="+c_js.gx+"&gy="+c_js.gy+"&gz="+c_js.gz+"&mx="+c_js.mx+"&my="+c_js.my+"&mz="+c_js.mz+"&ut="+c_js.ut+"&wendu="+c_js.wendu+"&dianya="+c_js.dianya);

			if(tg_type == 12 || tg_type == 13){
				//调用showf方法显示信息
				float tempstr[] = bg_show_f(c_js, tg_type, c_d, c_bc,c_jx);
				data = new HashMap<String, String>();
				data.put("id", String.valueOf(i+1));
				data.put("测量时间", df1.format((starttime+(i-startpoint))/60/60)+":"+df1.format((starttime+(i-startpoint))%3600/60)+":"+df1.format((starttime+(i-startpoint))%60) );
				data.put("井斜", df2.format(c_jx));
				data.put("井斜最大差值", df2.format(max1 - min1) );				
				data.put("磁倾角", df2.format(tempstr[6]) );
				data.put("磁方位", df2.format(tempstr[3]) );
				tempxiuzhengvalue = tempstr[4]- xiuzhengvalue;
				if(tempxiuzhengvalue < 0 )tempxiuzhengvalue = tempxiuzhengvalue+360;
				data.put("重力高边", df2.format(tempxiuzhengvalue) );
				tempgongjumian = tempstr[5]- xiuzhengvalue;
				if(tempgongjumian < 0 )tempgongjumian = tempgongjumian+360;
				data.put("磁工具面", df2.format(tempgongjumian));
				data.put("磁场强度", df2.format(tempstr[7]) );
				data.put("温度", df2.format(tempstr[1]));	
				data.put("校验和", df3.format(tempstr[8]));
				data.put("电压", df3.format(c_js.dianya));
				if(i==0){
					data.put("磁倾角变化", "0.00");
				}else{
					data.put("磁倾角变化", df2.format(tempstr[6]-tempqjbh));
				}
				datas.add(data);
				tempqjbh = tempstr[6];
			}else{
				//调用show方法显示信息
				float tempstr[] = bg_show(c_js, tg_type, c_d,1234);
				data = new HashMap<String, String>();
				data.put("id", String.valueOf(i+1));
				data.put("测量时间", df1.format((starttime+(i-startpoint))/60/60)+":"+df1.format((starttime+(i-startpoint))%3600/60)+":"+df1.format((starttime+(i-startpoint))%60) );
				data.put("井斜", df2.format(c_jx));
//				Log.d("shujujieshouunit","max1="+max1);
				Log.d("shujujieshouunit","jingxie1="+df2.format(c_jx)+"  jingxie2="+df2.format(tempstr[2]));
				data.put("井斜最大差值", df2.format(max1 - min1) );
				data.put("磁倾角", df2.format(tempstr[6]) );
				data.put("磁方位", df2.format(tempstr[3]) );
				tempxiuzhengvalue = tempstr[4]- xiuzhengvalue;
				if(tempxiuzhengvalue < 0 )tempxiuzhengvalue = tempxiuzhengvalue+360;
				data.put("重力高边", df2.format(tempxiuzhengvalue) );
				tempgongjumian = tempstr[5]- xiuzhengvalue;
				if(tempgongjumian < 0 )tempgongjumian = tempgongjumian+360;
				data.put("磁工具面", df2.format(tempgongjumian));
				data.put("磁场强度", df2.format(tempstr[7]) );
				data.put("温度", df2.format(tempstr[1]));
				data.put("校验和", df3.format(tempstr[8]));
				//Log.d("shujujieshouunit","size()=+++++");
				data.put("电压", df3.format(c_js.dianya));
				if(i==0){
					data.put("磁倾角变化", "0.00");
				}else{
					data.put("磁倾角变化", df2.format(tempstr[6]-tempqjbh));
				}
				datas.add(data);
				tempqjbh = tempstr[6];
			}
		}
		//Log.d("shujujieshouunit","size()="+datas.size());
		return datas;
	}
	/***
	 * 
	 * 计算井斜
	 * @param wd1
	 * @param wd
	 * @param gx
	 * @param gy
	 * @param gz
	 * @param tg_type
	 * @param c_bc
	 * @param c_d
	 * @return
	 */
	public float jiSuanJingXie(float wd1,float wd,float gx, float gy , float gz,int tg_type,TanguanBuChang[] c_bc,float c_d[]){
		float k1,n1,k;
		int i;
		float gx1 = 0,gy1 = 0,gz1 = 0,mx1,my1,mz1;
		float c_dy[] = new float[8];
		//计算温度
		if(tg_type == 10){
			if (c_d[25] == 0) c_d[25] = (float) 0.000001;
			k1 = (wd - c_d[24])/c_d[25];
		}else{
			k1 = wd1;
		}
		//修正
		if((c_d[19] *k1 + c_d[13]) == 0 ){c_d[13] = (float) 0.000001;}
		c_dy[4] = (c_d[7] * k1 + c_d[1] + gy) / (c_d[19] * k1 + c_d[13]);
		gy = c_dy[4];
		if(gy == 0){gy = (float) 0.000001;}
		
		if((c_d[18] *k1 + c_d[12]) == 0 ){c_d[12] = (float) 0.000001;}
		c_dy[5] = (c_d[6] * k1 + c_d[0] + gx) / (c_d[18] * k1 + c_d[12]);
		gx = c_dy[5];
		if(gx == 0){gx = (float) 0.000001;}
		if(c_d[14] != 0){
			if(c_d[20] * k1 + c_d[14] == 0){c_d[14] = (float) 0.000001;}
			c_dy[6] = (c_d[8] * k1 + c_d[2] + gz) / (c_d[20] * k1 + c_d[14]);
			gz = c_dy[6];
			if(gz == 0){gz = (float) 0.000001;}
			if(tg_type == 9){gz = -gz;}
		}
		
		if(tg_type == 12 || tg_type == 13 || tg_type ==14){
			gx1 = ah_js(gx, gy, gz, c_bc[0].A1, c_bc[0].A2, c_bc[0].A3, c_bc[0].A4);
			gy1 = ah_js(gx, gy, gz, c_bc[1].A1, c_bc[1].A2, c_bc[1].A3, c_bc[1].A4);
			gz1 = ah_js(gx, gy, gz, c_bc[2].A1, c_bc[2].A2, c_bc[2].A3, c_bc[2].A4);
			gx = gx1;
			gy = gy1;
			gz = gz1;
			if(gz>0){
				n1 = (float) ((Math.atan(Math.sqrt(gx * gx + gy * gy) / (gz))) * 57.3);
			}else{
				n1 = (float) (180 - (Math.atan(Math.sqrt(gx * gx + gy * gy) / (-gz))) * 57.3);
			}
			return n1;
		}
		
		i = 0;
		k = Math.abs(1-gy * gy - gx * gx);
		if(k == 0) k = (float) 0.000001;
		if(c_d[14] == 0) i=1;
		if(i == 1){
			n1 = (float) ((Math.atan(Math.sqrt(gx * gx + gy * gy) / Math.sqrt(k))) * 57.3);
			gz = 0;
		}else{
			if(gz == 0) gz = (float) 0.000001;
			n1 = (float) ((Math.atan(Math.sqrt(gx * gx + gy * gy) / Math.sqrt(k))) * 57.3);
			if(n1 > 66){
				if(gz < 0){
					n1 = (float) ((Math.atan(Math.sqrt(gx * gx + gy * gy) / (-gz))) * 57.3);
				}else{
					n1 = (float) (180-(Math.atan(Math.sqrt(gx * gx + gy * gy) / (gz))) * 57.3);
				}
			}else{
				if(gz > 0){
					n1 = (float) (180-(Math.atan(Math.sqrt(gx * gx + gy * gy) / (gz))) * 57.3);
				}
			}
		}
		return n1;
	}
}


































