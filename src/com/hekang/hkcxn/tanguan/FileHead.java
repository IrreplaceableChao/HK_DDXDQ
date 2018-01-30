package com.hekang.hkcxn.tanguan;

import java.io.Serializable;

public class FileHead implements Serializable{

	private static final long serialVersionUID = -8084080610590860463L;
	public int caijidianshu;
	public int yanshishijian;
	public int jiangeshijian;
	public String startdate;
	public String starttime;
	public String buhuoshijian;
	public String zhinengshijian;
	public String tanguanbianhao;
	public int tg_type;
	public String duihao;
	public String jinghao;
	public String ceshen;
	public String quhao;
	public float gaobianxiuzheng;
	/***
	 * 0为没有智能点 ， 1为有智能点且和捕获点相同  ， 2为智能点和捕获点不相同
	 */
	public int resulttype;
}
