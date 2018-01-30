package com.hekang.hkcxn.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ZfSend {
	
	public byte[] test(){
		byte[] send = new byte[38];
		byte k=37;
		int jyh = 0;
		send[0] = 0x2a;
		
		
	
	   	for(int i=0 ; i<k ; i++){
	   		jyh = jyh + send[i];
	   	}
	   	send[k] = (byte) (jyh%256);
	   	return send;
	}
	
	public byte[] sendtingzhi(){
		byte[] send = new byte[38];
		byte k=37;
		int jyh;
		send[0] = 0x2a;
		send[1] = (byte) 0xb2;
   		for (int i=2; i<k; i++){
   			send[i] = 0x00;
   		}
   		
	   	jyh = 0;
	   	for(int j=0 ; j<k ; j++){
	   		jyh = jyh + send[j];
	   	}
	   	send[k] = (byte) (jyh%256);
	   	return send;
	}
	public byte[] sendstart(){
		byte[] send = new byte[38];
		byte k=37;
		int jyh;
		send[0] = 0x2a;
		send[1] = (byte) 0xa5;
   		for (int i=2; i<k; i++){
   			send[i] = 0x00;
   		}
   		
	   	jyh = 0;
	   	for(int j=0 ; j<k ; j++){
	   		jyh = jyh + send[j];
	   	}
	   	send[k] = (byte) (jyh%256);
	   	return send;
	}
	
	public byte[] sendtgtype(){
		byte[] send = new byte[38];
		byte k=37;
		int jyh;
		send[0] = 0x2a;
   		Log.d("mylog","get̽���ͺ�");
   		send[1]=0x00;
   		send[2]=0x01;
   		send[3]=(byte) 0xac;
   		for(int i=4; i<k; i++){
   			send[i] = 0x00;
   		}
   		
	   	jyh = 0;
	   	for(int j=0 ; j<k ; j++){
	   		jyh = jyh + send[j];
	   	}
	   	send[k] = (byte) (jyh%256);
	   	return send;
	}
	
	public byte[] setTime(int c_num3,int j,int interval,int tg_type){
		byte[] send = new byte[38];
		byte k=37;
		int jyh = 0;
		send[0] = 0x2a;
		int p ;
		int s = j-1;
		send[1] = 0x1b;
   		send[2] = (byte) (c_num3/256);
   		send[3] = (byte) (c_num3%256);
   		send[8] = 0;
   		send[9] = (byte) ((s*60)/256);
   		send[10] = (byte) ((s*60)%256);
   		send[4] = 0;
   		send[5] = 0;
   		send[6] = (byte) 0xaa;
   		send[13] = send[9];
   		send[14] = send[10];
   		p = 15;
   		if(tg_type == 11 || tg_type == 12 || tg_type == 13){
   			send[8] = (byte) 0xa5;
   		}
   		send[7] = (byte) 0xa1;	
//   		send[11] = 0;
//   		send[12] = 1;
   		send[11] = (byte) (interval/256);
   		send[12] = (byte) (interval%256);
   		Log.e("/ ==:", (interval/256)+"         % == :"+(interval%256));
   		for(int i = p; i<k;i++){
   			send[i] = 0;
   		}
	
   		for(int i=0 ; i<k ; i++){
	   		jyh = jyh + send[i];
	   	}
	   	send[k] = (byte) (jyh%256);
	   	return send;
	}
	
	public byte[] setDuihao(int c_num3,String s){
		byte[] send = new byte[38];
		byte k=37;
		int jyh = 0;
		int p =4 ;
		int L1;
		byte[] temp = null ;
		send[0] = 0x2a;
   		send[1] = 0x1a;
   		send[2] = (byte) (c_num3/256);
   		send[3] = (byte) (c_num3%256);
   		try {
			temp = s.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<temp.length;i++){
//			if(temp[i]<0){
//				L1 = 65536+temp[i];
//				send[p] = (byte) (L1/256);
//				send[p+1] = (byte) (L1%256);
//				p+=2;
//			}else{
				send[p]=(byte) temp[i];
				p+=1;
//			}
		}
		for(int i = p ;i<k;i++){
			send[i] = 32;
		}
   		
	   	for(int i=0 ; i<k ; i++){
	   		jyh = jyh + send[i];
	   	}
	   	send[k] = (byte) (jyh%256);
	   	return send;
	}
	
	public byte[] setSZtime(int c_num3,String s){
		byte[] send = new byte[38];
		byte k=37;
		int jyh = 0;
		int p =4 ;
		int L1;
		int temp[] ;
		send[0] = 0x2a;
   		send[1] = 0x1e;
   		send[2] = (byte) (c_num3/256);
   		send[3] = (byte) (c_num3%256);
   		temp = str2asc(s);
   		Log.e("temp=", temp+"   : s= "+s);
   		System.out.println(Arrays.toString(temp));
		for(int i=0;i<temp.length;i++){
			if(temp[i]<0){
				L1 = 65536+temp[i];
				send[p] = (byte) (L1/256);
				send[p+1] = (byte) (L1%256);
				p+=2;
			}else{
				send[p]=(byte) temp[i];
				p+=1;
			}
		}
		Log.e("p=", ""+p);
		for(int i = p ;i<k;i++){
			send[i] = 32;
		}
   		
	   	for(int i=0 ; i<k ; i++){
	   		jyh = jyh + send[i];
	   	}
	   	send[k] = (byte) (jyh%256);
	   	return send;
	}
	
	
//	0x1a  ����
	public byte[] setJinghao(int c_num3,String s){
		byte[] send = new byte[38];
		byte k=37;
		int jyh = 0;
		int p = 4;
		byte temp[] = null ;
		int L1;
		send[0] = 0x2a;
		send[1] = 0x1a;
		send[2] = (byte) (c_num3/256);
		send[3] = (byte) (c_num3%256);
   		try {
			temp = s.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<temp.length;i++){
//			if(temp[i]<0){
//				L1 = 65536+temp[i];
//				send[p] = (byte) (L1/256);
//				send[p+1] = (byte) (L1%256);
//				p+=2;
//			}else{
				send[p]=(byte) temp[i];
				p+=1;
//			}
		}
		for(int i = p ;i<k;i++){
			send[i] = 32;
		}
	
	   	for(int i=0 ; i<k ; i++){
	   		jyh = jyh + send[i];
	   	}
	   	send[k] = (byte) (jyh%256);
	   	return send;
	}
	
	public byte[] setCeshenAndGaobian(int c_num3 , float l ,float l0){
		byte[] send = new byte[38];
		byte k=37;
		int jyh = 0;
		float l2 = l0*100;
		float l1 = l *100;
		send[0] = 0x2a;
		send[1] = 0x1b;
		send[2] = (byte) (c_num3/256);
		send[3] = (byte) (c_num3 % 256);
		for(int i = 0;i<=5;i++){
			send[4+i] = (byte) (l1/(Math.pow(10, 5-i)));
			l1 = (float) (l1 %(Math.pow(10, 5-i)));
		}
		
		for(int i=0; i<=4; i++){
			send[10+i] = (byte) (l2 /(Math.pow(10, 4-i)));
			l2 = (float) (l2 %(Math.pow(10, 4-i)));
		}
		for(int i=15; i<k ; i++){
			send[i] = 0;
		}
	   	for(int i=0 ; i<k ; i++){
	   		jyh = jyh + send[i];
	   	}
	   	send[k] = (byte) (jyh%256);
	   	return send;
	}
	
	public byte[] setXuhaoAndJingshen(int c_num3 , float l1){
		byte[] send = new byte[38];
		byte k=37;
		int jyh = 0;
		float l = l1 *100;
		send[0] = 0x2a;
		send[1] = 0x17;
		send[2] = (byte) (c_num3/256);
		send[3] = (byte) (c_num3 % 256);
		for(int i =0; i<=5 ; i++){
			send[5+i] = (byte) (l/(Math.pow(10, 5-i)));
			l = (float) (l %(Math.pow(10, 5-i)));
		}
		for(int i =11 ;i<k ; i++){
			send[i] = 0;
		}
		
	   	for(int i=0 ; i<k ; i++){
	   		jyh = jyh + send[i];
	   	}
	   	send[k] = (byte) (jyh%256);
	   	return send;
	}
	
	public byte[] setGongzuofangshi(int tg_type){
		byte[] send = new byte[38];
		byte k=37;
		int jyh = 0;
		send[0] = 0x2a;
		if(tg_type == 11 || tg_type == 12 || tg_type == 13){
			send[1] = (byte) 0xa3;
		}else{
			send[1] = (byte) 0xa1;
		}
		for(int i=0; i<k ; i++){
			send[i] = 0;
		}
	
	   	for(int i=0 ; i<k ; i++){  
	   		jyh = jyh + send[i];
	   	}
	   	send[k] = (byte) (jyh%256); 
	   	return send;
	}
	
	private static int[] str2asc(String s){
		char[] chars = s.toCharArray();
		
		System.out.println(Arrays.toString(chars));
		
		int [] temp = new int [chars.length];
		for (int i = 0; i < chars.length; i++) {
			temp [i] = (int) chars[i]; 
        }   
		return temp;
	}
}
