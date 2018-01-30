

package com.hekang.hkcxn.BLE;

/**
 * Created by ACHAO on 2016/3/11 0009.
 */
public interface Constants {
    //Connection state
    int STATE_DISCONNECTED = 0;
    int STATE_CONNECTING = 1;
    int STATE_CONNECTED = 2;
    int STATE_DISCONNECTING = 3;

    //Action
    String ACTION_GATT_DISCONNECTED = "com.junkchen.blelib.ACTION_GATT_DISCONNECTED";
    String ACTION_GATT_CONNECTING = "com.junkchen.blelib.ACTION_GATT_CONNECTING";
    String ACTION_GATT_CONNECTED = "com.junkchen.blelib.ACTION_GATT_CONNECTED";
    String ACTION_GATT_DISCONNECTING = "com.junkchen.blelib.ACTION_GATT_DISCONNECTING";
    String ACTION_GATT_SERVICES_DISCOVERED = "com.junkchen.blelib.ACTION_GATT_SERVICES_DISCOVERED";
    String ACTION_BLUETOOTH_DEVICE = "com.junkchen.blelib.ACTION_BLUETOOTH_DEVICE";
    String ACTION_SCAN_FINISHED = "com.junkchen.blelib.ACTION_SCAN_FINISHED";
}
