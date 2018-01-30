package com.hekang.hkcxn.model;

import com.hekang.hkcxn.util.Utils;

public class SaveData {

	/**
	 * 单个有效点数据结构，文件名：孔号.YX
	 */
	public static class Efficient_Point {

		/**
		 * 确定有效点结构,用于界面显示
		 */
		public static class Struct_Ensure_Point {
			public int id;
			public String deep;
			public String date;
			public String time;
//			public String type;
		}

		/**
		 * @param id
		 *            ----对应采集点编号0~65535，无符号整形 高位高地址
		 * @param deep
		 *            --对应测深
		 * @param date
		 *            --日期exp:131216
		 * @param time
		 *            --时间exp:231106
		 */
		public static byte[] getFileData(int id, float deep, String date,
				String time) {

			byte[] pointData = new byte[12];

			pointData[0] = (byte) (id & 0xFF);
			pointData[1] = (byte) ((id >> 8) & 0xFF);

			for (int i = 0; i < 4; i++) {
				pointData[i + 2] = (byte) (Float.floatToIntBits(deep) >> (24 - i * 8));
			}

			byte[] dat = Utils.hexStringToBytes(date);
			for (int i = 0; i < 3; i++) {
				pointData[i + 6] = dat[i];
			}
			// 
			byte[] tim = Utils.hexStringToBytes(time);
			for (int i = 0; i < 3; i++) {
				pointData[i + 9] = tim[i];
			}
			// // 增加TYPE
			// byte[] tpe = Utils.getBytes(type);
			// // System.out.println(tpe);
			// for (int i = 0; i < tpe.length; i++) {
			// pointData[i + 12] = tpe[i];
			// }
			// for(int i=0;i<15;i++){
			// Log.e("wwwwwwwwwwwwwwwwwwwwww", "wwwwwwwwwwwwwwwwwwwwww");
			// System.out.println(pointData[i]);
			// }
			return pointData;
		}

		/**
		 * 解析确定有效点单点结构,获取显示结构
		 * 
		 * @param bytes
		 * @return
		 */
		public static Struct_Ensure_Point getEnsurePointStruct(byte[] bytes) {

			Struct_Ensure_Point point = new Struct_Ensure_Point();

			point.id = ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
			point.deep = Float.toString(Utils.getFloat(bytes, 2));
			byte[] data = new byte[4];
			for (int i = 0; i < 3; i++) {
				data[i] = bytes[i + 6];
			}
			point.date = Utils.bytesToHexString(data);
			for (int i = 0; i < 3; i++) {
				data[i] = bytes[i + 9];
			}
			point.time = Utils.bytesToHexString(data);
//			for (int i = 0; i < 2; i++) {
//				data[i] = bytes[i + 12];
//			}
//			data = new byte[4];
//			for (int i = 0; i < 4; i++) {
//				data[i] = bytes[i + 12];
//			}
//			point.type = Utils.getString(data);
			return point;
		}
	}
}
