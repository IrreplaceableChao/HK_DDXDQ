package com.hekang.hkcxn.model;

public class SET_START {
	private int DELAY_TIME = 1;
	private int INTERVAL_TIME = 2;
	private String HOLE_ID = "test";
	private long START_TIME = 0L;
	private String Ben = "B";
	private String Face = "1";
	private String Drilling = "1";
	private String ChongM ="0"; 
	private String Time = "140101";
	private int JiaoZhun = 10;
	
	
	public int getJiaoZhun() {
		return JiaoZhun;
	}
	public void setJiaoZhun(int jiaoZhun) {
		JiaoZhun = jiaoZhun;
	}
	public String getTime() {
		return Time;
	}
	public void setTime(String time) {
		Time = time;
	}
	public String getChongM() {
		return ChongM;
	}
	public void setChongM(String chongM) {
		ChongM = chongM;
	}
	public String getBen() {
		return Ben;
	}
	public void setBen(String ben) {
		Ben = ben;
	}
	public String getFace() {
		return Face;
	}
	public void setFace(String face) {
		Face = face;
	}
	public String getDrilling() {
		return Drilling;
	}
	public void setDrilling(String drilling) {
		Drilling = drilling;
	}

	
	public int getDELAY_TIME() {
		return DELAY_TIME;
	}
	public void setDELAY_TIME(int dELAY_TIME) {
		DELAY_TIME = dELAY_TIME;
	}
	public int getINTERVAL_TIME() {
		return INTERVAL_TIME;
	}
	public void setINTERVAL_TIME(int iNTERVAL_TIME) {
		INTERVAL_TIME = iNTERVAL_TIME;
	}
	public String getHOLE_ID() {
		return HOLE_ID;
	}
	public void setHOLE_ID(String hOLE_ID) {
		HOLE_ID = hOLE_ID;
	}
	public long getSTART_TIME() {
		return START_TIME;
	}
	public void setSTART_TIME(long sTART_TIME) {
		START_TIME = sTART_TIME;
	}
	
}
