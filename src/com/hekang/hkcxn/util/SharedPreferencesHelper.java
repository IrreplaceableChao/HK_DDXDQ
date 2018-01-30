package com.hekang.hkcxn.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

public class SharedPreferencesHelper {
	
	SharedPreferences sp;
	SharedPreferences.Editor editor;

	Context context;
	//创建
	public SharedPreferencesHelper(Context c,String name){
		Log.d("chuangjian",name);
	context = c;
	sp = context.getSharedPreferences(name, 0);
	editor = sp.edit();
	
	}
	//写入数据
	public void putValue(String key, String value){
		//Log.d("xieshuju","ok");
		editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
		} 
	//写入数据
		public void putValue(String key, float value){
			//Log.d("xieshuju","ok");
			editor = sp.edit();
			editor.putFloat(key, value);		
			editor.commit();
			} 
	public void putValue(String key, long value){
		//Log.d("xieshuju","ok");
		editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
		} 
	public void putValue(String key, int value){
		//Log.d("xieshuju","ok");
		editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
		} 
//	读取数据
	public String getString(String key){
		//Log.d("dustring","ok");
		return sp.getString(key, null);
		}
	public Long getLong(String key){
		return sp.getLong(key, 0);
		}
	public int getInt(String key){
		return sp.getInt(key, 0);
		}
	
	public float getFloat(String key){
		return sp.getFloat(key, 0);
		}
	public void clear(){
		editor.clear();
		editor.commit();
	}
	
//	public void saveOAuth(OAuthV1 oAuth_1) {
//		SharedPreferences preferences = getSharedPreferences("base64",
//				MODE_PRIVATE);
//		// 创建字节输出流
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		try {
//			// 创建对象输出流，并封装字节流
//			ObjectOutputStream oos = new ObjectOutputStream(baos);
//			// 将对象写入字节流
//			oos.writeObject(oAuth_1);
//			// 将字节流编码成base64的字符窜
//			String oAuth_Base64 = new String(Base64.encodeBase64(baos
//					.toByteArray()));
//			Editor editor = preferences.edit();
//			editor.putString("oAuth_1", oAuth_Base64);
//
//			editor.commit();
//		} catch (IOException e) {
//			// TODO Auto-generated
//		}
//		Log.i("ok", "存储成功");
//	}
}
