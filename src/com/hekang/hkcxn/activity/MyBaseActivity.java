package com.hekang.hkcxn.activity;

import android.app.Activity;
import android.os.Bundle;

import com.hekang.hkcxn.model.IsBLE;
import com.hekang.hkcxn.util.MyLogger;
import com.hekang.hkcxn.util.SharedPreferencesHelper;

public class MyBaseActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferencesHelper sp_config = new SharedPreferencesHelper(this, "config");
		if (sp_config.getString("isBLE") == null){
			sp_config.putValue("isBLE","false");
			IsBLE.isBle = false;
		}else{
			if (sp_config.getString("isBLE").equals("true")){
				IsBLE.isBle = true;
			}else {
				IsBLE.isBle = false;
			}
		}
	}
	public void baseInit(){
	}
	public void setBotton(){
	}
}
