package com.hekang.hkcxn.util;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/*
已建立连接后启动的线程，需要传进来两个参数
socket用来获取输入流，读取远程蓝牙发送过来的消息
handler用来在收到数据时发送消息
*/
public class ConnectedThread extends Thread {

	private static final int RECEIVE_MSG = 7;
	private static final int SEND_MSG=8;
	private BluetoothSocket socket;
	private Handler handler;
	private InputStream is;
	private OutputStream os;
	private final static byte[] hex = "0123456789ABCDEF".getBytes();

	public ConnectedThread(BluetoothSocket s){
		socket=s;
	}

	public ConnectedThread(BluetoothSocket s,Handler h){
		socket=s;
		handler=h;
	}
	public void run(){
		byte[] buf;
		int size;
		int readCount = 0;
		while(true){
			size=0;
			buf=new byte[1];
			//byte b[] = new byte[1024];
			try {
				if(socket.getInputStream()!=null){
					is=socket.getInputStream();
					//System.out.println("等待数据");
					//size=is.read(buf);

					//  readCount = 0; // 已经成功读取的字节的个数
					//  while (readCount < 38) {
					readCount = is.read(buf);
					//  }
					//  Log.d("mylog", "执行他说明读取了数据");
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("mylog", size+"++");
				break;
			}
			try {
				sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(readCount>0){
				//把读取到的数据放进Bundle再放进Message，然后发送出去
				sendMessageToHandler(buf, RECEIVE_MSG);
				Log.d("mylog1","读取了一次数据3");
			}
		}

	}

	public void write(byte[] buf){
		try {
			os=socket.getOutputStream();
			os.write(buf);

			Log.d("mylog", "发送过程");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Log.d("malog", buf.length+"---");
		//sendMessageToHandler(buf, SEND_MSG);
	}

	private void sendMessageToHandler(byte[] buf,int mode){
		String msgStr;
		msgStr = Bytes2HexString(buf);
		//Log.d("mylog", "格式化接收的数据");
		Log.d("mylog","新界面接收数据"+msgStr);
		sendMessage(msgStr);
//		Bundle bundle=new Bundle();
//		bundle.putString("str", msgStr);
//		Message msg=new Message();
//		msg.setData(bundle);
//		msg.what=mode;
//		handler.sendMessage(msg);
//		Log.d("mylog","执行一次");
	}

	public static String Bytes2HexString(byte[] b) {
		byte[] buff = new byte[2 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[2 * i + 1] = hex[b[i] & 0x0f];
		}
		return new String(buff);
	}
	String m = "";
	private void sendMessage(String a){
		m = m+a ;
		Log.d("mylog", "msg");
		if(m.length()>=76){
			Bundle bundle=new Bundle();
			bundle.putString("str", m);
			Message msg=new Message();
			msg.setData(bundle);
			msg.what=RECEIVE_MSG;
			handler.sendMessage(msg);
			Log.d("mylog",m);
			m = "";
			Log.d("mylog","执行一次");
		}
	}
}
