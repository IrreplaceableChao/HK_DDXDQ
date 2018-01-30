package com.hekang.hkcxn.help;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hekang.R;
import com.hekang.hkcxn.multi.helpcexieyi;
import com.hekang.hkcxn.multi.helpchangjianwenti;
import com.hekang.hkcxn.multi.helplianxiwomen;
import com.hekang.hkcxn.multi.helpxuandianqi;

/**
 * Created by Administrator on 2015/5/6.
 */
public class SoftwareHelp extends Activity {
    private Button Back;
//    private Button button1,button2,button3,button4;
    private ImageView imageRjbz1 ,imageRjbz2 ,imageRjbz3 ,imageRjbz4;
    TextView selector_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        findView();
        init();
    }

    private void findView() {
    
//        Back = (Button) findViewById(R.id.button5);
        imageRjbz1 = (ImageView) findViewById(R.id.image_rjbz1);
        imageRjbz2 = (ImageView) findViewById(R.id.image_rjbz2);
        imageRjbz3 = (ImageView) findViewById(R.id.image_rjbz3);
        imageRjbz4 = (ImageView) findViewById(R.id.image_rjbz4);
        
//        button2 = (Button) findViewById(R.id.button2);
//        button3 = (Button) findViewById(R.id.button3);
//        button4 = (Button) findViewById(R.id.button4);
//        selector_title = (TextView) findViewById(R.id.selector_title);
//        selector_title.setText("软件帮助");

    }

    private void init() {

//        Back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        imageRjbz1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SoftwareHelp.this,SoftwareHelpCXY.class);
                startActivity(intent);
            }
        });
        imageRjbz2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SoftwareHelp.this,SoftwareHelpScroll_2.class);
                startActivity(intent);
            }
        });
        imageRjbz3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SoftwareHelp.this,SoftwareHelpScroll_3.class);
                startActivity(intent);
            }
        });
        imageRjbz4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SoftwareHelp.this,SoftwareHelpScroll_4.class);
                startActivity(intent);
            }
        });
    }
}
