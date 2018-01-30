package com.hekang.hkcxn.model;

import java.io.Serializable;

public class InfoModel implements Serializable{
	/**
	 * 序列化，存储采集点数据
	 */
	private static final long serialVersionUID = 1L;
	private int num;//数据在全部数据里的序号
	private String xuhao;//有笑点序号
	private String shendu;
	private String time;
	private String type;
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setXuhao(String xuhao) {
		this.xuhao = xuhao;
	}
	public String getXuhao() {
		return xuhao;
	}
	public void setShendu(String shendu) {
		this.shendu = shendu;
	}
	public String getShendu() {
		return shendu;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTime() {
		return time;
	}
}
