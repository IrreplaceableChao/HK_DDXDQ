package com.hekang.hkcxn.util;

import android.app.Dialog;
import android.content.Context;
import android.widget.ProgressBar;

import com.hekang.R;

public class MyProgressDialog extends Dialog{

	Context context;
	ProgressBar pb ;
	public MyProgressDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

    public MyProgressDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }
/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
       
        
    }*/
    
    public void init(){
    	 this.setContentView(R.layout.progress_dialog_layout);
    	pb = (ProgressBar) findViewById(R.id.databar);
    }
    
    public void setProgress(int p){
    	pb.setProgress(p);
    }

	public int getProgress(){
		return pb.getProgress();
	}

    public void setMax(int m){
    	if(pb == null){
    		System.out.println("pb == null");
    	}
    	try {
        	pb.setMax(m);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}

    }
}
