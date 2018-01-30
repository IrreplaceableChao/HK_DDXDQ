package com.hekang.hkcxn.util;

import android.util.Log;

public class BaseNumber {
	
	public static long basejisuan(String arg0 , String arg1){
		long a ;
		int a0 = Integer.parseInt(arg0, 16);
		int a1 = Integer.parseInt(arg1, 16);
		a = a0*256+a1;
		return a;
	}
	
	public static double dianya(String arg0 , String arg1){
		long a = basejisuan(arg0, arg1);
		double dianya;
		dianya = a/8*0.000806*4.9;
		Log.d("mylog",arg0+"--"+arg1);
		return dianya;
	}
	
	public static double wendu(String arg0 , String arg1){
		long a = basejisuan(arg0, arg1);
		double wendu;
		if(a<32768){
			wendu = a * 0.0625;
		}else{
			wendu = (a-65536)* 0.0625;
		}
		Log.d("mylog",arg0+"--"+arg1);
		return wendu;
	}
	
	public static String[] str2strs(String a){

		String[] b = new String[39];
		int j =1;
		for(int i = 0;i<76;i+=2){			
			b[j] = a.substring(i, i+2);
			j++;
		}
		return b;
	}
	
	public static String tanguanbianhao (String arg0,String arg1,String arg2,String arg3,String arg4){
		String name = null ;
		char a0 = (char) Integer.parseInt(arg0, 16);
		char a1 = (char) Integer.parseInt(arg1, 16);
		char a2 = (char) Integer.parseInt(arg2, 16);
		long weihao = basejisuan(arg4,arg3);
		if(weihao/100==0){
			name = ""+a0+a1+a2+"0"+weihao;
		}else{
			name = ""+a0+a1+a2+weihao;
		}
		Log.d("mylog", name);
		return name;
	}

}
