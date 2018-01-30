package com.hekang.hkcxn.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5 {
	    /** 
	     * @param args 
	     */  
//	    public static void main(String[] args) {  
//	        // TODO Auto-generated method stub  
//	        try {  
//	          
//	            System.out.println("result-->"+result);  
//	        } catch (NoSuchAlgorithmException e) {  
//	            // TODO Auto-generated catch block  
//	            e.printStackTrace();  
//	        }  
//	    }  
	    public static String MD5(String strSrc,MessageDigest  md) {  
	    	strSrc = Migration(strSrc,15);
	        byte[] bt = strSrc.getBytes();  
	        md.update(bt);  
	        String strDes = bytes2Hex(md.digest()); // to HexString  
	        return strDes;  
	    }  
	    private static String bytes2Hex(byte[] bts) {  
	        StringBuffer des = new StringBuffer();  
	        String tmp = null;  
	        for (int i = 0; i < bts.length; i++) {  
	            tmp = (Integer.toHexString(bts[i] & 0xFF));  
	            if (tmp.length() == 1) {  
	                des.append("0");  
	            }  
	            des.append(tmp);  
	        }  
	        return des.toString().substring(8,18).toUpperCase();  
	    }
	    /**
	     * 移位
	     * @param str
	     * @param Data_len
	     * @return
	     */
	    private static String Migration(String str,int Data_len){
	    	
	    	int Encry_Code[] = { 6, 11, 4, 0, 2, 12, 7, 10, 14, 13, 9, 5, 1, 8, 3 };//加密码  
	    	
	    	char User_Code[] = new char [15]; 
	    	
		   	char IMEI_Code [] = str.toCharArray();
		   	
		   	
		   	for(int i=0;i<Data_len;i++)
		   		
		   		User_Code[i]=IMEI_Code[Encry_Code[i]];
		   	
	   	return String.valueOf(User_Code);
	   }
	    /**
	     * 移位加密
	     * @param str
	     * @param Data_len
	     * @return
	     */
	    public static String HEX_ASCLL(String str,int Data_len){
	    	
	    	int Encry_Code[] = { 6, 11, 4, 0, 2, 12, 7, 10, 14, 13, 9, 5, 1, 8, 3 };//加密码  
	    	
	    	char User_Code[] = new char [15]; 
	    	
		   	char IMEI_Code [] = str.toCharArray();
		   	
		   	
		   	for(int i=0;i<Data_len;i++)
		   		
		   		User_Code[i]=IMEI_Code[Encry_Code[i]];
		   	
		   	  
		   	for(int i=0;i<(Data_len-5);i++)
		   		
		   		User_Code[i]=(char) (User_Code[i]+i+17);
		   	
	//	   	for(int i=0;i<Data_len;i++)
	//	   	 User_Code2[Encry_CODE[i]]=(char) (User_CODE[i]-17);
	//	   	  MyLogger.jLog().e(String.valueOf(User_Code2));
	   	return String.valueOf(User_Code);
	   }
}
