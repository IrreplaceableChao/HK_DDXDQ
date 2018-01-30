package com.hekang.hkcxn.model;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;

public class SaveListModel {
//	public static List<Integer> list_yx = new ArrayList<Integer>(); // 全部数据的点号。http://blog.csdn.net/ztp800201/article/details/16315857
	public static List<InfoModel> listinfo;//预览采集点数据
	public static List<String> listDevices;
	public static List<BluetoothDevice> listbluetoothdevice = new ArrayList<BluetoothDevice>();
}
