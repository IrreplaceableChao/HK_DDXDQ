package com.hekang.hkcxn.util;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.hekang.R;

/**
 * Created by Administrator on 2016/3/31.
 */
public class DialogUtil{
    private Context mContext;
    private Dialog dialog;
    private TextView message;

    public DialogUtil(Context mContext,String msg){
        this.mContext=mContext;
        if (dialog==null){
            dialog = new Dialog(mContext, R.style.MyDialog);
        }
        jingshiinfo(msg);
    }

    private void jingshiinfo(String msg) {
        //设置它的ContentView
        dialog.setContentView(R.layout.blank_dialog_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        message = (TextView) dialog.findViewById(R.id.message);
        message.setText(msg);
    }

    public void showDialog(){
        if (dialog!=null&&!dialog.isShowing()){
            dialog.show();
        }
    }

    public void closeDialog(){
        if (dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
        }
    }

}
