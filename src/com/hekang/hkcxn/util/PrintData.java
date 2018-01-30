package com.hekang.hkcxn.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.hekang.hkcxn.service.BluetoothService;

public class PrintData {
    private BluetoothReceiver bluetoothreceiver;
    private BluetoothAdapter bluetoothAdapter;
//    private final String lockName = "Brightek Thermal Printer";
//    private final String lockName = "TIII BT Printer";
    private String lockName = " ";
//    private final String lockName = "T10 BT Printer";
    private List<String> devices;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothService mService = null;
    Context c;
    Context spc = null;
    SharedPreferencesHelper sp;
	byte[] out ={ 0x1b, 0x6d, 0xd };
	String sendData;
	int times;
	boolean b  = false;
	boolean ispipei	 = false;
	BluetoothDevice device;
	int pipeideirce = 0;
	public boolean bIsOk = false ,bIsOk2 = false;
	boolean d = true;
	public void init(Context c){
    	this.c = c;
		try {
			spc =  c.createPackageContext("com.hekang.hkcxn",Context.CONTEXT_IGNORE_SECURITY);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sp = new SharedPreferencesHelper(c, "config");
		lockName = sp.getString("printername");
        devices = new ArrayList<String>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if(!bluetoothAdapter.isEnabled()){
        	bluetoothAdapter.enable();
        }
        mService = new BluetoothService(c,mHandler);
        if(pairedDevices.size()>0){
        	for(BluetoothDevice device : pairedDevices){
        		if(lockName.equals(device.getName())){
        			Log.d("mylog","�Ѿ����ֱ������");
//        			connectThread = new ConnectThread(device, mHandler);
//        			connectThread.start();
//        			ispipei = true;
//        			PublicValues.printdevice = device;
        			//mService.connect(device);
//        			mService = new BluetoothService(c,mHandler);
//        			mService.connect(PublicValues.printdevice);
        			content();
        			break;
        		}
        		pipeideirce ++;
        		Log.d("pd","pipeideirce="+pipeideirce+"----pairedDevices.size()="+pairedDevices.size());
        	}
        }

//        	if(pipeideirce >= pairedDevices.size()){
//        	    bluetoothAdapter.startDiscovery();//����
//        	    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        	//    IntentFilter filter2 = new IntentFilter("android.bluetooth.device.action.PAIRING_REQUEST");
//        	    bluetoothreceiver = new BluetoothReceiver();
//        	    c.registerReceiver(bluetoothreceiver, filter);
//        	//    c.registerReceiver(bluetoothreceiver, filter2);
//        	    ispipei = false;
//        	    Toast.makeText(c, "�������Ӵ�ӡ��1",Toast.LENGTH_SHORT).show();
//        	    Log.d("pd","��������豸������ƥ���豸");
//        	}
//
//        }else{
//	        bluetoothAdapter.startDiscovery();//����
//	        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);	        
//	        bluetoothreceiver = new BluetoothReceiver();
//	        c.registerReceiver(bluetoothreceiver, filter);
//	        ispipei = false;
//	        Log.d("pd","������豸");
//	        Toast.makeText(c, "�������Ӵ�ӡ��2",Toast.LENGTH_SHORT).show();
//		}
    }
    
    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (isLock(device)) {
                   // devices.add(device.getName());
                	bluetoothAdapter.cancelDiscovery();
                	Log.d("mylog",device.getAddress());
                	Log.d("mylog",device.getName());
//        			connectThread = new ConnectThread(device, mHandler);
//        			connectThread.start();
//        			PublicValues.socket = connectThread.getSocket();
//                	PublicValues.printdevice = device;
                //	mService.connect(device);
                	Log.d("mylog", "����ƥ��ɹ�");
                	
//                	ispipei = false;
//                    if (device.getBondState() != BluetoothDevice.BOND_BONDED){
////                    	BluetoothConnectActivityReceiver bc = new BluetoothConnectActivityReceiver();
////                    	bc.setStrPsw("1234");
//                    	Log.d("pd","û�а�");
//                    	PublicValues.pinkey=sp.getString("printerpin");
//                    }
                }else{
                	Log.d("printdata","û��ƥ�������");
                	
                }
               // deviceList.add(device);
            }
//       	 if (intent.getAction().equals(
//    				"android.bluetooth.device.action.PAIRING_REQUEST"))
//    		{
//    			BluetoothDevice btDevice = intent
//    					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//    			// byte[] pinBytes = BluetoothDevice.convertPinToBytes("1234");
//    			// device.setPin(pinBytes);
//    			Log.i("tag11111", "ddd");
//    			try
//    			{
//    				ClsUtils.setPin(device.getClass(), device, PublicValues.pinkey); // �ֻ�������ɼ������
//    				ClsUtils.createBond(device.getClass(), device);
//    				ClsUtils.cancelPairingUserInput(device.getClass(), device);
//    			}
//    			catch (Exception e)
//    			{
//    				// TODO Auto-generated catch block
//    				e.printStackTrace();
//    			}
//    		}
    		
            // showDevices();
//            Log.d("mylog", "lianjieyici");
//            if(pair(device.getAddress(),"0000")){
            	content();
//            }
            
        }
    }
    
    private boolean isLock(BluetoothDevice device) {
        boolean isLockName = lockName.equals(device.getName());
        boolean isSingleDevice = devices.indexOf(device.getName()) == -1;
        return isLockName && isSingleDevice;
    }
    
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 1:
                //if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
//            		mService.write(out, 2048);
            		for(int i=1 ; i<=times; i++){
            			Log.d("paringdata","pring2");
            			Log.d("paringdata","sendData="+sendData);
            			mService.printserver(sendData);
            		}
            		b = true;
            		bIsOk = true;
                    break;
                case BluetoothService.STATE_CONNECTING:

                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:

                    break;
                }
                break;
            case 159:
//                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
//                String writeMessage = new String(writeBuf);
//                Log.d("mylog------",writeMessage);
              //  mConversationArrayAdapter.add("Me:  " + writeMessage);
//                break;
//            case MESSAGE_READ:
//                byte[] readBuf = (byte[]) msg.obj;
//                // construct a string from the valid bytes in the buffer
//                String readMessage = new String(readBuf, 0, msg.arg1);
//               // mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
//                break;
//            case MESSAGE_DEVICE_NAME:
//                // save the connected device's name
//              //  mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
//              //  Toast.makeText(getApplicationContext(), "Connected to "
//                          //     + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                break;
            case 753:
//                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
//                               Toast.LENGTH_SHORT).show();
                break;
            case 5:
                    Toast.makeText(c, "��ȷ�ϴ�ӡ��ͨѶ�Ƿ���",
                            Toast.LENGTH_SHORT).show();	
                    b = false;
                    bIsOk = false;
//                if(ispipei){
//                	Toast.makeText(c, "�������ӵڶ�����ӡ��",Toast.LENGTH_SHORT).show();
//        	        bluetoothAdapter.startDiscovery();//����
//        	        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        	        bluetoothreceiver = new BluetoothReceiver();
//        	        c.registerReceiver(bluetoothreceiver, filter);
//        	        ispipei = false;
//                }

                break;
            case 6:
            	if(d){
                	Toast.makeText(c, "��ȷ�ϴ�ӡ��ͨѶ�Ƿ���",
                            Toast.LENGTH_SHORT).show();	
                	bIsOk2 = true;
            	}

            	break;
            }
        }
    };
    
    public void content(){
		
//		Log.d("pd","printdevice"+PublicValues.printdevice);
//		mService.connect(PublicValues.printdevice);
    }
    
    
    public void  send(String sendData,int times){
    	this.sendData = sendData;
    	this.times = times;
    	if(device!=null){
    		Log.d("pd","device="+device.getAddress());
    	}
    	
    	if(b){
    		for(int i=1 ; i<=times; i++){
    			Log.d("paringdata","pring");
    			mService.printserver(sendData);
    		}
    	}
    }
    
    public void finishprint(){
    	if (mService != null){
    		Log.d("printdata","finishprint");
    		mService.stop();
        	d = false;
    	}
    	if(bluetoothreceiver != null){
    		c.unregisterReceiver(bluetoothreceiver);
        	d = false;
    	}

    }
    
}
