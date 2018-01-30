package com.hekang.hkcxn.fragment;

import java.text.DecimalFormat;

import com.hekang.R;
import com.hekang.hkcxn.fragmentActivity.EfficientPoint;
import com.hekang.hkcxn.util.MyLogger;
import com.hekang.hkcxn.util.SharedPreferencesHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

public class F1  extends Fragment{
    private RadioGroup rg_sex ;
    private View view;
    SharedPreferencesHelper sp3,shared;
    public static TextView et_lizhuchangdu;
    public static EditText et_tuopan,et_jiachang;
    public static EditText et_dangen,et_lizhu;
    public static TextView et_ceshen;
    private LinearLayout linear_gone1;
    DecimalFormat DF_=new DecimalFormat("#0.00");
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.f1, container,false);
        
        sp3 = new SharedPreferencesHelper(getActivity(), "tdset");
        shared = new SharedPreferencesHelper(getActivity(), "mapxml");
        
        findview();
        init();
//        ceshen = Integer.parseInt(sp3.getString("ceshen"));
//        et_ceshen.setText(ceshen+"");
        
        rg_sex.setFocusable(true);
        rg_sex.setFocusableInTouchMode(true);
        readSetInfo();
		return view;
	}
	private void readSetInfo() {
//		Log.e("ccccccccccccccccccc11111111", ""+sp3.getFloat("et_ceshen")+":"+DF_.format(cd_));
		if (sp3.getFloat("et_ceshen") != 0){
			cd_ = sp3.getFloat("et_ceshen");
//			Log.e("ccccccccccccccccccc", ""+sp3.getFloat("et_ceshen")+":"+DF_.format(cd_));
			et_ceshen.setText(""+DF_.format(cd_));
		}
		if (sp3.getFloat("et_lizhuchangdu") != 0){
			ab_ = sp3.getFloat("et_lizhuchangdu");
//			Log.e("ccccccccccccccccccc222222222", ""+sp3.getFloat("et_lizhuchangdu")+":"+DF_.format(ab_));
			et_lizhuchangdu.setText(""+DF_.format(ab_));
		}
		if (sp3.getFloat("et_tuopan") != 0){
			c_ = sp3.getFloat("et_tuopan");
			et_tuopan.setText(""+DF_.format(c_));
		}
		if (sp3.getFloat("et_jiachang") != 0){
			d_ = sp3.getFloat("et_jiachang");
			et_jiachang.setText(""+DF_.format(d_));
		}
		if (sp3.getFloat("et_dangen") != 0){
			a_ = sp3.getFloat("et_dangen");
			et_dangen.setText(""+DF_.format(a_));
		}
		if (sp3.getInt("et_lizhu") != 0)
			et_lizhu.setText(""+sp3.getInt("et_lizhu"));
		
	       
        if (!et_tuopan.getText().toString().equals("") && !et_jiachang.getText().toString().equals("")) {
			et_ceshen.setText(""+DF_.format(Float.parseFloat(et_tuopan.getText().toString())-Float.parseFloat(et_jiachang.getText().toString())));
			c_ = Float.parseFloat(et_tuopan.getText().toString());
			sp3.putValue("et_tuopan", c_);
			d_ = Float.parseFloat(et_jiachang.getText().toString());
			sp3.putValue("et_jiachang", d_);
			et_ceshen();
		}
    	if (!et_dangen.getText().toString().equals("") && !et_lizhu.getText().toString().equals("")) {
			et_lizhuchangdu.setText(""+DF_.format(Float.parseFloat(et_dangen.getText().toString())*Float.parseFloat(et_lizhu.getText().toString())));
			a_ = Float.parseFloat(et_dangen.getText().toString());
			sp3.putValue("et_dangen", a_);
			b_ = Integer.parseInt(et_lizhu.getText().toString());
			sp3.putValue("et_lizhu", b_);
			et_lizhuchangdu();
		}	
		
		
	}
	private void findview(){
        et_tuopan = (EditText) view.findViewById(R.id.editText_tuopan);
        et_jiachang = (EditText) view.findViewById(R.id.editText_jiachang);
        et_ceshen = (TextView) view.findViewById(R.id.editText_diyi);
        et_dangen = (EditText) view.findViewById(R.id.dangen);
        et_lizhu = (EditText) view.findViewById(R.id.lizhu);
        et_lizhuchangdu = (TextView) view.findViewById(R.id.lizhuchangdu);
//        linear_gone1隐藏3个
        linear_gone1 = (LinearLayout) view.findViewById(R.id.linear_gone1);
        
	}

    private void init() {
    	
        rg_sex = (RadioGroup) view.findViewById(R.id.rg_sex);
        if (shared.getString("IsAutomatic")!=null) {
        	if (shared.getString("IsAutomatic").equals("true")) {
//        		rg_sex.check(R.id.shoudong);
//        		linear_gone1.setVisibility(View.VISIBLE);
			}else{
				rg_sex.check(R.id.shoudong);
				linear_gone1.setVisibility(View.GONE);
			}
		}else{
			EfficientPoint.IsAutomatic = true;//默认是自动
			shared.putValue("IsAutomatic", "true");//默认是自动
		}
        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.zidong:
//                        System.out.println("自动Automatic and manual");
                        EfficientPoint.IsAutomatic = true;
                        shared.putValue("IsAutomatic", "true");
                        F2.startshengming();
                        linear_gone1.setVisibility(View.VISIBLE);
                        break;
                    case R.id.shoudong:
//                        System.out.println("手动");
                        EfficientPoint.IsAutomatic = false;
                        shared.putValue("IsAutomatic", "false");
                        F2.startshengming();
                        
                        linear_gone1.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        });
        et_tuopan.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void afterTextChanged(Editable arg0) {
				if (!et_tuopan.getText().toString().equals("")) {
					
					c_ = Float.parseFloat(et_tuopan.getText().toString());
					
					sp3.putValue("et_tuopan", c_);
				}
				if (!et_tuopan.getText().toString().equals("") && !et_jiachang.getText().toString().equals("")) {
					et_ceshen.setText(""+DF_.format(Float.parseFloat(et_tuopan.getText().toString())-Float.parseFloat(et_jiachang.getText().toString())));
					et_ceshen();
				}
			}
		});
        et_jiachang.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void afterTextChanged(Editable arg0) {
				if (!et_jiachang.getText().toString().equals("")) {
					d_ = Float.parseFloat(et_jiachang.getText().toString());
					sp3.putValue("et_jiachang", d_);
				}
				if (!et_tuopan.getText().toString().equals("") && !et_jiachang.getText().toString().equals("")) {
					et_ceshen.setText(""+DF_.format(Float.parseFloat(et_tuopan.getText().toString())-Float.parseFloat(et_jiachang.getText().toString())));
					et_ceshen();
				}
			}
		});
        
        
        
        
        et_dangen.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void afterTextChanged(Editable arg0) {
				if (!et_dangen.getText().toString().equals("")) {
					
					a_ = Float.parseFloat(et_dangen.getText().toString());
					
					sp3.putValue("et_dangen", a_);
				}
				
				if (!et_dangen.getText().toString().equals("") && !et_lizhu.getText().toString().equals("")) {
					et_lizhuchangdu.setText(""+DF_.format(Float.parseFloat(et_dangen.getText().toString())*Float.parseFloat(et_lizhu.getText().toString())));
					et_lizhuchangdu();
					MyLogger.jLog().e("输入单根立柱长度");
				}
			}
		});
        et_lizhu.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void afterTextChanged(Editable arg0) {
				if (!et_lizhu.getText().toString().equals("")) {
					
					b_ = Integer.parseInt(et_lizhu.getText().toString());
					
					sp3.putValue("et_lizhu", b_);
				}
				if (!et_dangen.getText().toString().equals("") && !et_lizhu.getText().toString().equals("")) {
					et_lizhuchangdu.setText(""+DF_.format(Float.parseFloat(et_dangen.getText().toString())*Float.parseFloat(et_lizhu.getText().toString())));
					et_lizhuchangdu();
					MyLogger.jLog().e("输入多少根");
				}		
			}
		});
    }
    private void et_lizhuchangdu(){
    	// TODO Auto-generated method stub
    	if (!et_lizhuchangdu.getText().toString().equals("")) {
			ab_ = Float.parseFloat(et_lizhuchangdu.getText().toString());
			sp3.putValue("et_lizhuchangdu", ab_);
		}	
    }
    private void et_ceshen(){
    	if (!et_ceshen.getText().toString().equals("")) {
			EfficientPoint.ceshen_dian = et_ceshen.getText().toString();
			cd_ = EfficientPoint.ceshen = Float.parseFloat(et_ceshen.getText().toString());
			F2.fileName();
			sp3.putValue("et_ceshen", EfficientPoint.ceshen);
		}
    }
    
    boolean isvisible = true;
    
    float a_ = 0;
    int b_ = 0;
    float c_ = 0;
    float d_ = 0;
    float cd_ = 0;
    float ab_ = 0;
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			//可见时执行的操作
			MyLogger.jLog().e("F1setUserHint可见时候操作");
				if (F2.isYQDD ==true) {
					MyLogger.jLog().e("F1setUserHint可见时候操作判断语句里的");
					et_tuopan.setEnabled(false);
					et_jiachang.setEnabled(false);
				}
				hind = true;
		} else {
			//不可见时执行的操作
				if (isvisible) {
					MyLogger.jLog().e("F1setUserHint");
					if (a_ != 0) {
							a_ = Float.parseFloat(DF_.format(a_));
							sp3.putValue("et_dangen", a_);
							et_dangen.setText(DF_.format(a_));
					}
					if (c_ != 0) {
							c_ = Float.parseFloat(DF_.format(c_));
							sp3.putValue("et_tuopan",c_);
							et_tuopan.setText(DF_.format(c_));
					}
					if (d_ != 0) {
							d_ = Float.parseFloat(DF_.format(d_));
							sp3.putValue("et_jiachang", d_);
							et_jiachang.setText(DF_.format(d_));
					}
					if (ab_ != 0) {
							ab_ = Float.parseFloat(DF_.format(ab_));
							sp3.putValue("et_lizhuchangdu", ab_);
							et_lizhuchangdu.setText(DF_.format(ab_));
					}
	//					
					if (cd_ != 0) {
							cd_ = Float.parseFloat(DF_.format(cd_));
							sp3.putValue("et_ceshen", cd_);
							 et_ceshen.setText(DF_.format(cd_));
					}
			}
				if (hind) {
					if (!et_lizhuchangdu.getText().toString().equals("")) {
						ab_ = Float.parseFloat(et_lizhuchangdu.getText().toString());
						MyLogger.jLog().e("F1=>F2.lizhuchangdu()方法："+ab_);
						F2.lizhuchangdu(ab_);
					}	
				}
				
		}
	};
	boolean  hind = false;
}
