package com.hekang.hkcxn.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "hk.db";
	private static final String TBL_NAME = "hkdata";
	private static final String CREATE_TBL = "create table IF NOT EXISTS hkdata (_id integer,duihao text,jinghao text,ceshen float ,celiangdate date,bianhao text,celiangtime time,jingxie text , fangwei text,qingjiao text,gaobian text,gongjumian text,xiuzheng text,wendu text,dianya text,cichang text,jiaoyanhe text, panfufangshi text,youxiaoshijian text,primary key (jinghao,ceshen,bianhao)) ";

	private SQLiteDatabase db;

	public DBHelper(Context c) {
		super(c, DB_NAME, null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		db.execSQL(CREATE_TBL);
	}

	public void insert(ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		db.insert(TBL_NAME, null, values);
		db.close();
	}

	public Cursor query() {
		SQLiteDatabase db = getWritableDatabase();
		Cursor c = db.query(TBL_NAME, null, null, null, null, null, null);
		return c;
	}

	public void insertstr(String[] strs) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "REPLACE INTO hkdata (duihao ,jinghao ,ceshen ,celiangdate ,bianhao ,celiangtime ,jingxie,  fangwei ,qingjiao ,gaobian ,gongjumian ,xiuzheng ,wendu ,dianya ,cichang ,jiaoyanhe , panfufangshi ,youxiaoshijian) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		String sql2 = "REPLACE  INTO hkdata (duihao, jinghao, ceshen, celiangdate, bianhao) VALUES (?,?,?,?,?)";
		try {
			db.execSQL(sql, strs);
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("dbhelp","插入异常");
		}

	}

	public void delete(String[] strs) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = "delete from hkdata where jinghao = ? and ceshen = ? and bianhao = ?";
		try {
			db.execSQL(sql, strs);
			Log.d("dbhelp","删除成功");
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("dbhelp","删除异常");
		}

	}

	public Cursor select(String sql,String values[]){
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, values);
		return cursor;
	}

	public void del(int id) {
		if (db == null)
			db = getWritableDatabase();
		db.delete(TBL_NAME, "_id=?", new String[] { String.valueOf(id) });
	}

	public void close() {
		if (db != null)
			db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}