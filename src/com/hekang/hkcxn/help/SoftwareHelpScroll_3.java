package com.hekang.hkcxn.help;

import com.hekang.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;

public class SoftwareHelpScroll_3 extends Activity{
	
    ScrollView scroll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_3);
		   
		scroll = (ScrollView) findViewById(R.id.scroll);
		
//    	scroll.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View arg0, MotionEvent arg1) {
//				// TODO Auto-generated method stub
//				Log.e("scroll.getScaleY();", ""+scroll.getHeight());
//				return false;
//			}
//		});
	}

}
