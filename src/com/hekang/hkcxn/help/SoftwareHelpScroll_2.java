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

public class SoftwareHelpScroll_2 extends Activity{
	
    ScrollView scroll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_2);
		   
		scroll = (ScrollView) findViewById(R.id.scroll);
		
    	
	}

}
