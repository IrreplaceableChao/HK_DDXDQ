package com.hekang.hkcxn.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hekang.R;
import com.hekang.hkcxn.activity.MainActivity;
import com.hekang.hkcxn.adapter.ListviewAdapter;
import com.hekang.hkcxn.fragmentActivity.EfficientPoint;
import com.hekang.hkcxn.model.AdapterModel;
import com.hekang.hkcxn.model.InfoModel;
import com.hekang.hkcxn.model.IsBLE;
import com.hekang.hkcxn.model.SaveListModel;
import com.hekang.hkcxn.util.FileUtils;
import com.hekang.hkcxn.util.MyLogger;
import com.hekang.hkcxn.util.SharedPreferencesHelper;
import com.hekang.hkcxn.util.Utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 
 * 
 * 确定采集点页面
 *  对于项目BUG管理的网络工具
 * 	https://tower.im/users/sign_in
 */
public class F2 extends Fragment implements OnClickListener {
	protected static final String STATUS_DELAY = null;
	private Button quedingyouxiaodian;				// 确定有效点
	private Button bt_qryqdd;						// 确认仪器到底
	private TextView quedingyouxiaodian_tv, tv_dqcs;// 确定有效点
	private ListView listview;
	private static EditText dangqianceshen;			// 手动输出测深
	public  static TextView zidongDangqianceshen;	// 自动显示测深
	public 	static Boolean isYQDD = false;			// 仪器到底
	public 	static int xuhao = 1;
	static  View view;
	private SharedPreferencesHelper sp_cexiestep, sp_tdset, sp_mapxml,sp_config;
	private TextView uptime, f2_caijishijian,tv_isyanshitime,gongzuozhuangtai;
	public  static float lizhuchangdu = 0;
	public  TextView CountDown_;  					// 显示的倒计时
	long    T;										// 当前时间
	long    T1;										// 启动时间
	long    T2;										// 延时时间
	long    T3;										// 间隔时间
	long    time;
	String  timestr;
	int 	icaijiman = 0;							// 标记计算采集满之后
	int 	caiji_second;							// 延时变采集状态时候 的 时间 单位（s）
	int 	caiji_6 = 1;
	int 	count = 1;
	int 	BCDJS = 0;
	int 	Down_T = 0;
	int 	down_ = 0;
	int 	daojishi = -1;
	boolean isCreateFilewwww = true;
	boolean isRunnable = true;
	boolean flag1 = false;                          // 暂时没有用
	static	boolean isCreateFile = false;
	static  DecimalFormat DF_=new DecimalFormat("#0.00");
	SimpleDateFormat filedateformat = new SimpleDateFormat("yyyy-MM-dd");
	private int m_caijidian;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.f2, container, false);
		if (IsBLE.isBle){
			m_caijidian=8000;
		}else{
			m_caijidian=3600;
		}
		findView();
		setOnClick();
		startshengming();
		init();
		Unusual();
		startTimer();
		acquireWakeLock();
		return view;
	}

	private void setOnClick() {
		quedingyouxiaodian.setOnClickListener(this);
		bt_qryqdd.setOnClickListener(this);
	}

	private void findView() {

		bt_qryqdd = (Button) view.findViewById(R.id.bt_qryqdd);
		uptime = (TextView) view.findViewById(R.id.uptime);
		tv_dqcs = (TextView) view.findViewById(R.id.tv_dqcs);
		listview = (ListView) view.findViewById(R.id.f2_listview);
		CountDown_  = (TextView) view.findViewById(R.id.caijitime);
		dangqianceshen = (EditText) view.findViewById(R.id.dangqianceshen);
		f2_caijishijian = (TextView) view.findViewById(R.id.f2_caijishijian);
		tv_isyanshitime = (TextView) view.findViewById(R.id.tv_isyanshitime);
		gongzuozhuangtai = (TextView) view.findViewById(R.id.gongzuozhuangtai);
		quedingyouxiaodian = (Button) view.findViewById(R.id.quedingyouxiaodian);
		zidongDangqianceshen = (TextView) view.findViewById(R.id.zidong_dangqianceshen);
		quedingyouxiaodian_tv = (TextView) view.findViewById(R.id.quedingyouxiaodian_tv);
	}

	public static void startshengming() {
		// 自动还是手动
		if (isYQDD) {
			if (EfficientPoint.IsAutomatic) {
				// 自动
				zidongDangqianceshen.setVisibility(View.VISIBLE);
				dangqianceshen.setVisibility(View.GONE);
				if (!dangqianceshen.getText().toString().equals("")) {
					zidongDangqianceshen.setText(DF_.format(Float.parseFloat(dangqianceshen.getText().toString())));
				}
			} else {
				zidongDangqianceshen.setVisibility(View.GONE);
				dangqianceshen.setVisibility(View.VISIBLE);
			}
		}else{
			zidongDangqianceshen.setVisibility(View.VISIBLE);
			dangqianceshen.setVisibility(View.GONE);
			zidongDangqianceshen.setText(DF_.format(EfficientPoint.ceshen));
		}
	}
	
	private void init() {

		sp_tdset = new SharedPreferencesHelper(getActivity(), "tdset");
		sp_mapxml = new SharedPreferencesHelper(getActivity(), "mapxml");
		sp_cexiestep = new SharedPreferencesHelper(getActivity(), "cexiestep");
		sp_config = new SharedPreferencesHelper(getActivity(), "config");
		// caiji_6 = Integer.parseInt(sp_tdset.getString("jiange"));
		T = new Date().getTime();
		T1 = sp_cexiestep.getLong("T1");
		T2 = (sp_cexiestep.getInt("T2")) * 60 * 1000;
		T3 = (sp_cexiestep.getInt("T3")) * 1000;
		xuhao = 1;
		SaveListModel.listinfo = new ArrayList<InfoModel>();
		MyLogger.jLog().e("unusual"+":" + sp_cexiestep.getInt("unusual") + "");
		if (sp_cexiestep.getInt("unusual") == 3) {
			huifulist();													// 现场恢复
		} else {
			sp_mapxml.putValue("isYQDD", "false");
		}
		AdapterModel.adapter = new ListviewAdapter(getActivity(),SaveListModel.listinfo);
		listview.setAdapter(AdapterModel.adapter);
		bt_qryqdd.setVisibility(View.GONE);
	}

	private void Unusual() {
		//先判断延时还是采集 然后计算到计时时间。
		int dangqianmiaoshu = Utils.getDatatimesecond();
		int ss = longToS(T1);
		if (T < (T1 + T2)) {
			/*
			 *  延时时间
			 */
			tv_isyanshitime.setText("剩余延时时间");

			gongzuozhuangtai.setText("延时状态");

			MyLogger.jLog().e("dagnqianmiaoshu"+":" + (dangqianmiaoshu - ss));

			int yanshi_ = Integer.parseInt(sp_tdset.getString("yanshishijian"));

			if (dangqianmiaoshu == ss) {
				time = (yanshi_ * 60 + dangqianmiaoshu) - ss;  				// 启动时候的延时倒计时
				MyLogger.jLog().e("dagnqianmiaoshu"+":time=" + time);
				
			} else if (dangqianmiaoshu > ss) {
				time = (yanshi_ * 60);
				MyLogger.jLog().e("dagnqianmiaoshu"+ ":time=" + time);  	// 启动时候的延时倒计时
				
			} else if (dangqianmiaoshu < ss) {
				Toast.makeText(getActivity(), "当前秒数小于启动时间 不可能出现",Toast.LENGTH_SHORT).show();
			}
		} else {
			/*
			 *  采集时间
			 */
			tv_isyanshitime.setText("剩余采集时间");

			gongzuozhuangtai.setText("采集状态");

			time = Integer.parseInt(sp_tdset.getString("jiange")) * (m_caijidian - 1);

			zhizouyici = false;
		}
	}

	public static void shuaxinzidongdangqianceshen() {
		zidongDangqianceshen.setText(DF_.format(EfficientPoint.ceshen_));
	}

	public static void lizhuchangdu(float a) {
		if (SaveListModel.listinfo.size() != 0) {
			zidongDangqianceshen = (TextView) view.findViewById(R.id.zidong_dangqianceshen);
			zidongDangqianceshen.setText(DF_.format(EfficientPoint.ceshen_ + lizhuchangdu- a));
			EfficientPoint.ceshen_ += lizhuchangdu - a;
		}
		lizhuchangdu = a;
	}

	public static void fileName() {
		isCreateFile = true;
	}

	Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what){
				case 110:
					if (xuhao == 1) {
						bt_qryqdd.setVisibility(View.VISIBLE);
					} else {
						quedingyouxiaodian.setVisibility(View.VISIBLE);
					}
					tv_isyanshitime.setText("剩余采集时间");
					gongzuozhuangtai.setText("采集状态");
					if (sp_cexiestep.getInt("unusual") == 3) {
						zidongDangqianceshen.setText(DF_.format(EfficientPoint.ceshen_ ));
					} else {
						zidongDangqianceshen.setText(DF_.format(EfficientPoint.ceshen ));
					}
					sp_cexiestep.putValue("unusual", 2);
					break;
				case 111:
					f2_caijishijian.setText(timestr);
					break;
				case 112:
					String datadangqianshijian = Utils.getDatetime(Down_T);
					f2_caijishijian.setText(timestr);
					uptime.setText(datadangqianshijian);
					if (flag1) {
						if (daojishi > 0) {
							CountDown_.setVisibility(View.VISIBLE);
							CountDown_.setText((daojishi-1)+"");
							/**
							 * android.content.res.Resources$NotFoundException: String resource ID #0x3
							 * 如果不小心将一个int值传给了它，那它不会显示该int值，
							 * 而是跑到工程下去找一个对应的resource的id,当然是找不到的，于是就报错啦
							 * */
							daojishi--;
						}
						if (isYQDD) {
							if (daojishi == 0) {
								CountDown_.setVisibility(View.GONE);
								quedingyouxiaodian.setVisibility(View.VISIBLE);
								quedingyouxiaodian_tv.setVisibility(View.GONE);
							}else if(daojishi>0){
								quedingyouxiaodian.setVisibility(View.GONE);
								quedingyouxiaodian_tv.setVisibility(View.VISIBLE);
							}
						}
					}
					break;
				case 113:
					isRunnable = false;
					new AlertDialog.Builder(getActivity())
							.setTitle("提示")
							.setMessage("　　仪器采集点已经采满，请点击结束选点。")
							.setPositiveButton("结束选点",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,	int which) {
									Intent intent = new Intent(getActivity(),MainActivity.class);
									startActivity(intent);
									sp_cexiestep.putValue("unusual", 1);
									getActivity().finish();
								}
							}).setCancelable(false).show();
					break;
				default:
					break;
			}
		}
	};

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			/**
			 *  可见时执行的操作
			 */
			try {
				if (!isYQDD) {
					if (EfficientPoint.ceshen != 0) zidongDangqianceshen.setText(DF_.format(EfficientPoint.ceshen));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} /**else {}不可见时执行的操作*/
	}

	Boolean zhizouyici = true;
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			while (isRunnable) {
				Down_T = Utils.getDatatimesecond();					// 当前是时间秒数
				T = new Date().getTime();
				long times = 0;
				if (T < (T1 + T2)) {
					/**延时时间*/
					times = time - (T - T1) / 1000;					//延时倒计时   120 ~ 1
					if (sp_cexiestep.getInt("unusual") == 3) 	sp_cexiestep.putValue("unusual", 2);
				} else {
					/**采集时间*/
					if (times == time - (T - T1) / 1000 + T2 / 1000) continue;
					times = time - (T - T1) / 1000 + T2 / 1000;        //采集时间倒计时   6*3600-6 ~ 1
					if (times == T2 / 1000) {
						if (zhizouyici) {
							times = 0;
							zhizouyici = false;
						}
					}
					if (sp_cexiestep.getInt("unusual") == 3) times = 0;
				}
				StringBuffer strbuf = new StringBuffer();
				if (times == 0) {
					if (sp_cexiestep.getInt("unusual") != 3) {					//判断是否为现场恢复
						caiji_second = Utils.getDatatimesecond();				//获取当前时间并转化为秒数
						sp_cexiestep.putValue("caiji_second", caiji_second);	//存储当前时间作为测量时间
						BCDJS = (int) (caiji_second % (T3 / 1000));
						sp_cexiestep.putValue("BCDJS", BCDJS);
					} else {
						BCDJS = sp_cexiestep.getInt("BCDJS");
						caiji_6 = (int) ((T3 / 1000) - (((int) (Utils.getDatatimesecond() % (T3 / 1000))) - BCDJS))+1;
					}
					icaijiman++;
					flag1 = true;
					handler2.sendEmptyMessage(110);
					if (T >= (T1 + T2)) {
						time = Integer.parseInt(sp_tdset.getString("jiange"))* (m_caijidian - 1);
					} else {
						time = Integer.parseInt(sp_tdset.getString("jiange"))* (m_caijidian - 1);
					}
				}
				if (Down_T > (longToS(T1)+T2/1000+T3/1000*(m_caijidian-1))) handler2.sendEmptyMessage(113);
				if (times<0) {
					isRunnable = false;
					handler2.sendEmptyMessage(113);
					times = 0;
				}
				timestr = secoundToTimeStr(times,strbuf);
				if (flag1) {
					caiji_6--;
					if (caiji_6 < 1) {
						caiji_6 = Integer.parseInt(sp_tdset.getString("jiange"));
						count++;
						MyLogger.jLog().e( "count : "+count);
					}
				}
				handler2.sendEmptyMessage(112);
				try {
					Thread.sleep(994);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	private void startTimer() {
		new Thread(runnable).start();
	}

	private void stopTimer() {
		isRunnable = false;
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.quedingyouxiaodian) {
			if (xuhao == 1) {
				EfficientPoint.ceshen_ = EfficientPoint.ceshen;
			}
			if (F1.et_ceshen.getText().toString().equals("")) {
				AlertDialog("请输入测深！");
				return;
			}
			if (EfficientPoint.IsAutomatic) {
				// 自动
				if (F1.et_lizhuchangdu.getText().toString().equals("")) {
					AlertDialog("请输入立柱长度！");
					return;
				}
			}
			MyLogger.jLog().e("按下采集键"+ lizhuchangdu + "<==立柱长度赋值之前");
			lizhuchangdu = Float.parseFloat(F1.et_lizhuchangdu.getText().toString());
			MyLogger.jLog().e("按下采集键"+ lizhuchangdu + "<==立柱长度赋值之后");
			if (!EfficientPoint.IsAutomatic) {
				if (xuhao != 1) {
					if (dangqianceshen.getText().toString().equals("")) {
						AlertDialog("请输入当前测深");
						return;
					} else {
						float dqce = Float.parseFloat(dangqianceshen.getText().toString());
						MyLogger.jLog().e("dayin"+ ""+SaveListModel.listinfo.get(xuhao-2).getShendu());
						if (dqce >= Float.parseFloat(SaveListModel.listinfo.get(xuhao-2).getShendu())) {
							AlertDialog("输入的当前测深应小于上一测点深度");
							return;
						}
					}
				}
			}
			if (EfficientPoint.ceshen_ - lizhuchangdu < 0) {
				AlertDialog("下一次测深点已为负数");
			}
			tv_dqcs.setText("当前测深(米)");
			quedingyouxiaodian.setVisibility(View.GONE);
			quedingyouxiaodian_tv.setVisibility(View.VISIBLE);
			InfoModel info = new InfoModel();
			info.setXuhao(xuhao + "");
			MyLogger.jLog().e("手动"+ " ："+EfficientPoint.IsAutomatic);
			if (EfficientPoint.IsAutomatic) {
				info.setShendu( DF_.format(EfficientPoint.ceshen_ ));
				MyLogger.jLog().e("手动==>"+"xuhao:"+xuhao);
				info.setType("自动");
			} else {
				MyLogger.jLog().e("手动==>"+"xuhao:"+xuhao);
				if (xuhao !=1) {
					info.setShendu( DF_.format(Float.parseFloat(dangqianceshen.getText().toString())));
					EfficientPoint.ceshen_ = Float.parseFloat(dangqianceshen.getText().toString());
				}else{
					MyLogger.jLog().e("手动==>"+ "else    xuhao:"+xuhao);
					info.setShendu( DF_.format(EfficientPoint.ceshen_ ));
					zidongDangqianceshen.setVisibility(View.GONE);
					dangqianceshen.setVisibility(View.VISIBLE);
				}
				info.setType("手动");
			}
			if (caiji_6>6) {									//判断延时时间是否大于10秒         大于10秒+10
				down_ = 0;
			}else{
				 if ((caiji_6+ (T3 / 1000))>=6) {
					 down_ = 1;
					}else {
					 if ((caiji_6+ (T3 / 1000)*2)>=6) {
						 down_ = 2;
					 }else {
						 if ((caiji_6+ (T3 / 1000)*3)>=6) {
							 down_ = 3;
						 }else {
							 MyLogger.jLog().e(caiji_6+"down_=2");
						 }
					 }
				 }
			}
			String str = "";
			String save_time = "";
			boolean abcdn=true;
			while (abcdn) {
				str = Utils.getDatetime(caiji_second + ((count + down_)-1) * Integer.parseInt(sp_tdset.getString("jiange")));
				save_time = str.substring(0, 2) + str.substring(3, 5)+ str.substring(6, 8);
				int hour = Integer.parseInt(str.substring(0, 2));
				int minute = Integer.parseInt(str.substring(3, 5));
				int sec =  Integer.parseInt(str.substring(6, 8));
				int ss = hour*3600+minute*60+sec;
				daojishi=ss-Down_T+2;
				MyLogger.jLog().e(daojishi);
				if (daojishi-10 > Integer.parseInt(sp_tdset.getString("jiange"))) {
					count--;
				}else{
					abcdn=false;
				}
			}
			info.setNum(count + down_);
			info.setTime(str);
			SaveListModel.listinfo.add(info);
			/**2016-6-5,注释掉创建文件代码.*/
//			byte[] point = SaveData.Efficient_Point.getFileData(Integer.parseInt(info.getXuhao()), Float.parseFloat(info
//					.getShendu()), Utils.getCurDateString().substring(2, 8),save_time);
//			if (isCreateFilewwww) {
//				if (isCreateFile) {
			if (sp_tdset.getString("quhao").equals("")){
				String riqi = sp_tdset.getString("jinghao") + "("+ DF_.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime")));

			}else{
				String riqi = sp_tdset.getString("quhao")+"："+sp_tdset.getString("jinghao") + "("+ DF_.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime")));

			}

			String riqi = sp_tdset.getString("quhao")+"："+sp_tdset.getString("jinghao") + "("+ DF_.format(EfficientPoint.ceshen) + "米)" + filedateformat.format(new Date(sp_cexiestep.getLong("starttime")));
					FileUtils.sdfilepath = Environment.getExternalStorageDirectory().getPath() + "/HKCX-SJ/"+ riqi + "/";
//					EfficientPoint.filename = riqi	+ ".YX";
//					FileUtils.FileNewCreate(EfficientPoint.filename);
//					FileUtils.RandomWriteFile(EfficientPoint.filename, 0,Utils.getLittleBytes((char) xuhao));
//					// 写入此次有效点
//					FileUtils.appendWriteFile(EfficientPoint.filename, point);
					sp_mapxml.putValue("FileUtils.sdfilepath",FileUtils.sdfilepath);
					sp_mapxml.putValue("EfficientPoint.filename",EfficientPoint.filename);
//					isCreateFile = false;
//				}
//				isCreateFilewwww = false;
//			} else {
//				// 存储更新有效点点数
//				FileUtils.RandomWriteFile(EfficientPoint.filename, 0,Utils.getLittleBytes((char) xuhao));
//				// 写入此次有效点
//				FileUtils.appendWriteFile(EfficientPoint.filename, point);
//			}
			AdapterModel.adapter.notifyDataSetChanged();
	    	listview.setSelection(listview.getCount() - 1);
			xuhao++;
			EfficientPoint.ceshen_ -= lizhuchangdu; // 按一下可以减一个立柱长度。
			zidongDangqianceshen.setText(DF_.format(EfficientPoint.ceshen_ ));
			sp_mapxml.putValue("i", Integer.parseInt(info.getXuhao()));
			sp_mapxml.putValue("xuhao" + info.getXuhao(), info.getXuhao());
			sp_mapxml.putValue("shendu" + info.getXuhao(), info.getShendu());
			sp_mapxml.putValue("type" + info.getXuhao(), info.getType());
			sp_mapxml.putValue("time" + info.getXuhao(), info.getTime());
			sp_mapxml.putValue("count" + info.getXuhao(), count + down_);
			sp_mapxml.putValue("EfficientPoint.ceshen_", EfficientPoint.ceshen_);
			// xuhaoceshen++;
		} else if (v.getId() == R.id.bt_qryqdd) {
			if (F1.et_ceshen.getText().toString().equals("")) {
				AlertDialog("请输入测深！");
				return;
			}
			new AlertDialog.Builder(getActivity())
					.setTitle("提示")
					.setMessage(getString(R.string.qryqdd))
					.setPositiveButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							})
					.setNegativeButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									bt_qryqdd.setVisibility(View.GONE);
									quedingyouxiaodian.setVisibility(View.VISIBLE);
									isYQDD = true;
								}
							}).show();

		}
	}

	public void huifulist() {
		FileUtils.sdfilepath = sp_mapxml.getString("FileUtils.sdfilepath");
		EfficientPoint.filename = sp_mapxml.getString("EfficientPoint.filename");
		int conunt_i = sp_mapxml.getInt("i");
		caiji_second = sp_cexiestep.getInt("caiji_second");
		EfficientPoint.ceshen_ = sp_mapxml.getFloat("EfficientPoint.ceshen_");
		
		MyLogger.jLog().e("huifulist"+"conunt_i:" + conunt_i + "ceshen_:"
				+ EfficientPoint.ceshen_);
		
		if (EfficientPoint.ceshen_ != 0) {
			zidongDangqianceshen.setText(DF_.format(EfficientPoint.ceshen_));
			if (sp_mapxml.getString("IsAutomatic").equals("true")) {
				zidongDangqianceshen.setVisibility(View.VISIBLE);
				dangqianceshen.setVisibility(View.GONE);
				EfficientPoint.IsAutomatic = true;
				MyLogger.jLog().e("huifulist自动"+ "true");
			} else {
				zidongDangqianceshen.setVisibility(View.GONE);
				dangqianceshen.setVisibility(View.VISIBLE);
				
				EfficientPoint.IsAutomatic = false;
				MyLogger.jLog().e("huifulist手动"+ " ："+EfficientPoint.IsAutomatic);
			}
			// MyLogger.jLog().e("IsAutomatic222222", "" + shared.getString("IsAutomatic"));
		}
		for (int i = 1; i <= conunt_i; i++) {
			InfoModel info = new InfoModel();
			info.setXuhao(sp_mapxml.getString("xuhao" + i));
			info.setShendu(sp_mapxml.getString("shendu" + i));
			info.setType(sp_mapxml.getString("type" + i));
			info.setTime(sp_mapxml.getString("time" + i));
			int countxml = sp_mapxml.getInt("count" + i);
			info.setNum(countxml);
			MyLogger.jLog().e("shared :" + sp_mapxml.getString("xuhao" + i) + 
					"shendu:" + (sp_mapxml.getString("shendu" + i)) + 
					"time:"	+ sp_mapxml.getString("time" + i) + "countxml:"+ countxml);
			SaveListModel.listinfo.add(info);
			if (sp_mapxml.getString("xuhao" + i) != null) {
				xuhao = Integer.parseInt((sp_mapxml.getString("xuhao" + i)));
				xuhao++;
			}
		}
		if (conunt_i != 0) {
			isCreateFile = false;
			isCreateFilewwww = false;
			isYQDD = true;
			MyLogger.jLog().e("huifulist"+ "isYQDD = true");
			count = (int) ((T - T1 - T2) / 1000 / (T3 / 1000))+3;
			listview.setSelection(listview.getCount() - 1);
			bt_qryqdd.setVisibility(View.GONE);
		}
		if (T >= (T1 + T2)) {
			count = (int) ((T - T1 - T2) / 1000 / (T3 / 1000))+3; 
		} 
	}

	public void AlertDialog(String str){
		new AlertDialog.Builder(getActivity())
		.setTitle("提示")
		.setMessage(str)
		.setNegativeButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				})
		.setCancelable(false).show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseWakeLock();
		stopTimer();
	}
	
	/**************************************工具代码******************************************/
	private WakeLock mWakeLock;
	/**
	 * 申请设备电源锁
	 */ 
	public void acquireWakeLock() {
		if (null == mWakeLock) {
			PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					"NotificationService");
			if (null != mWakeLock) {
				mWakeLock.acquire();
			}
		}
	}

	/**
	 *  释放设备电源锁
	 */
	public void releaseWakeLock() {
		if (null != mWakeLock) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}
	/**
	 * 把时间戳转换成秒数
	 * @param longs
	 * @return
	 */
	public int longToS(long longs) {
		String qidongshijian = Utils.millisToTimeStr(longs);
		int h = Integer.parseInt(qidongshijian.substring(0, 2));
		int m = Integer.parseInt(qidongshijian.substring(2, 4));
		int s = Integer.parseInt(qidongshijian.substring(4, 6));
		int ss = h * 3600 + m * 60 + s;
		return ss;
	}

	private String secoundToTimeStr(long times,StringBuffer buf){
		long hour = times / 3600;
		if (hour>=24) {
			hour-=24;
		}
		if (hour < 10) {
			buf.append(0);
		}
		buf.append(hour);
		buf.append(":");

		long minute = times % 3600 / 60;
		if (minute < 10) {
			buf.append(0);
		}
		buf.append(minute);
		buf.append(":");

		long second = times % 3600 % 60;
		if (second < 10) {
			buf.append(0);
		}
		buf.append(second);
		return buf.toString();
	}
}
