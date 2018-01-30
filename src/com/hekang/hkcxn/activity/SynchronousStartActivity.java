package com.hekang.hkcxn.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hekang.R;
import com.hekang.hkcxn.fragmentActivity.EfficientPoint;
import com.hekang.hkcxn.model.SaveListModel;
import com.hekang.hkcxn.util.MyLogger;
import com.hekang.hkcxn.util.PublicValues;
import com.hekang.hkcxn.util.SharedPreferencesHelper;
import com.hekang.hkcxn.util.SysExitUtils;

import java.util.Date;

/**
 * 同步启动
 * Created by AChao on 2015/5/6.   
 */
public class SynchronousStartActivity extends Activity{
    private TextView SelectorTitle;//title
    private Button Start;
    private Button Back;
	Context context;
	SharedPreferencesHelper sp_cexiestep,sp_mapxml;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	SysExitUtils.getAppManager().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.synchronous_start);
        findView();
        init();
    }


    private void findView() {
        SelectorTitle = (TextView) findViewById(R.id.selector_title);
        Start = (Button) findViewById(R.id.start);
        Back = (Button) findViewById(R.id.button3);
    }
    private void init(){
		try {
			context =  createPackageContext("com.hekang.hkcxn",Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sp_cexiestep = new SharedPreferencesHelper(this,"cexiestep");
		sp_mapxml = new SharedPreferencesHelper(this,"mapxml");
        SelectorTitle.setText("同步启动");
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SynchronousStartActivity.this,EfficientPoint.class);
                startActivity(intent);
				Date time = new Date();
//				PublicValues.step = 2;
				PublicValues.start = time;
				sp_cexiestep.putValue("starttime", new Date().getTime());
				sp_cexiestep.putValue("T1", new Date().getTime());
				sp_cexiestep.putValue("unusual", 1);
				MyLogger.jLog().e("T1:"+new Date().getTime());
				
				SaveListModel.listinfo=null;
				sp_mapxml.clear();
            }
        });		
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(SynchronousStartActivity.this,MainActivity.class);
				startActivity(intent);
            }
        });

    }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

