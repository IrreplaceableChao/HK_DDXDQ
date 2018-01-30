package com.hekang.hkcxn.fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hekang.R;
import com.hekang.hkcxn.activity.MainActivity;
import com.hekang.hkcxn.adapter.ListviewAdapter;
import com.hekang.hkcxn.fragmentActivity.EfficientPoint;
import com.hekang.hkcxn.model.AdapterModel;
import com.hekang.hkcxn.model.InfoModel;
import com.hekang.hkcxn.model.SaveListModel;
import com.hekang.hkcxn.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * 有效点预览  
 * @author Administrator
 *
 */
public class F3  extends Fragment{
	View view;
	Button jiesu,delete;
	ListView listview;
	//	ListviewAdapter adapter;
	List<InfoModel> listinfo;
	boolean flag = true;
	SharedPreferencesHelper sp_cexiestep;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
				view = inflater.inflate(R.layout.f3, container,false);
				findview();
				init();
				return view;
	}

	private void findview() {
		jiesu = (Button) view.findViewById(R.id.jiesu);
		delete = (Button) view.findViewById(R.id.delete);
		listview = (ListView) view.findViewById(R.id.f3_listview);
	}
	@Override
	public void onStart() {
		super.onStart();
	}
	private void init() {
		sp_cexiestep = new SharedPreferencesHelper(getActivity(), "cexiestep");
		jiesu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				new AlertDialog.Builder(getActivity())  
                .setTitle("提示")
                .setMessage("　　结束选点，后续测量将不能继续自动选点，您确定【结束选点】？")
//                点击“退出”，增加两级退出提示，例如：“您确认已完成测量，“确定”后将无法再继续进行测量！”；“您确定要退出并返回主页？”

                .setPositiveButton("取消", new DialogInterface.OnClickListener(){
                   public void onClick(DialogInterface dialog, int which) {
                	   
                   }    
               })
               .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) {
            				new AlertDialog.Builder(getActivity())  
                            .setTitle("提示")
                            .setMessage("　　请再次确定是否结束选点并返回主页。")
                            .setPositiveButton("取消", new DialogInterface.OnClickListener(){
                               public void onClick(DialogInterface dialog, int which) {
                            	   
                               }    
                           })
                           .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                            	   BluetoothAdapter ble = BluetoothAdapter.getDefaultAdapter();
                                   if (!ble.isEnabled()) {
                                	   ble.enable();
                           		}
                            	   Intent intent = new Intent(getActivity(),MainActivity.class);
								   //如果activity在task存在，拿到最顶端,不会启动新的Activity
//								   intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
									//如果activity在task存在，将Activity之上的所有Activity结束掉
//								   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								   //默认的跳转类型,将Activity放到一个新的Task中
//								   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									//如果Activity已经运行到了Task，再次跳转不会在运行这个Activity
//								   intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
								   startActivity(intent);
								   sp_cexiestep.putValue("unusual", 1);
								   sp_cexiestep.putValue("T4", new Date().getTime());
                            	   getActivity().finish();
                               }
                           })
                           .show();	
                   }
               })
               .show();	
			}
		});
		
		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (F2.xuhao == 1) {
					Toast.makeText(getActivity(), "没有可删除数据", Toast.LENGTH_SHORT).show();
					return;
				}
				if (flag) {
					new AlertDialog.Builder(getActivity())  
	                .setTitle("提示")
	                .setMessage("数据删除将不可恢复。")
	                .setPositiveButton("取消", new DialogInterface.OnClickListener(){
	                   public void onClick(DialogInterface dialog, int which) {
	                	flag = true;
	                   }    
	               })
	               .setNegativeButton("确定", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int which) {
	       				int info_size = SaveListModel.listinfo.size();
	    				F2.xuhao--;
	    				if (F2.xuhao != 1 && F2.xuhao != 0) {
	    					EfficientPoint.ceshen_ = Float.parseFloat(SaveListModel.listinfo.get(F2.xuhao-2).getShendu()) - F2.lizhuchangdu;
						}else if (F2.xuhao == 1) {
							EfficientPoint.ceshen_ = EfficientPoint.ceshen;
						}
	    				if (info_size != 0) {
	    					SaveListModel.listinfo.remove(info_size-1);
	    					// 更改文件数据,自动模式下重新写文件
							/**2016-6-5,注释掉创建文件代码.*/
//							 FileUtils.FileNewCreate(EfficientPoint.filename);
//							 FileUtils.RandomWriteFile(EfficientPoint.filename, 0, Utils.getLittleBytes((char)(SaveListModel.listinfo.size())));
//							 for (int i = 0; i < SaveListModel.listinfo.size(); i++) {
//								 String str = SaveListModel.listinfo.get(i).getTime();
//								 String timestr = str.substring(0, 2)+str.substring(3, 5)+str.substring(6, 8);
//								 byte[] point =
//										 SaveData.Efficient_Point.getFileData(Integer.parseInt(SaveListModel.listinfo.get(i).getXuhao()),
//												 Float.parseFloat(SaveListModel.listinfo.get(i).getShendu()), Utils.getCurDateString(), timestr);
//								 // 写入有效点
////							 	getViewByPosition(mPointList.size(), mLvPoint).findViewById(R.id.itme_main).setBackgroundColor(Color.WHITE);
//								 FileUtils.appendWriteFile(EfficientPoint.filename, point);
//							 }
	    				}
	    				AdapterModel.adapter.notifyDataSetChanged();
	    				F2.shuaxinzidongdangqianceshen();
	    				flag = true;
	                   }
	               }).setCancelable(false).show();	
				}
				flag = false;				
				
			}
		});
		
		if (SaveListModel.listinfo==null) {
			SaveListModel.listinfo = new ArrayList<InfoModel>();
		}
		
		if (AdapterModel.adapter ==null) {
			AdapterModel.adapter= new ListviewAdapter(getActivity(),SaveListModel.listinfo);
		}
		listview.setAdapter(AdapterModel.adapter);
		
	}
	public void starnotifyDataSetChanged(){
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		F2.isYQDD = false;// 仪器到底
	}
}
