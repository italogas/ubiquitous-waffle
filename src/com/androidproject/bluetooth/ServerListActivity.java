/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androidproject.bluetooth;


import com.androidproject.R;
import com.androidproject.R.id;
import com.androidproject.R.layout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * A simple list activity that displays Bluetooth devices that are in
 * discoverable mode. This can be used as a gamelobby where players can see
 * available servers and pick the one they wish to connect to.
 */

public class ServerListActivity extends Activity {
    public static String EXTRA_SELECTED_ADDRESS = "btaddress";

    private BluetoothAdapter myBt;

    private ServerListActivity self;

    private ArrayAdapter<String> arrayAdapter;
    
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Parcelable btParcel = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BluetoothDevice btDevice = (BluetoothDevice) btParcel;
            arrayAdapter.add(btDevice.getName() + " - " + btDevice.getAddress());
//            Toast.makeText(context, btDevice.getName() + " - " + btDevice.getAddress(), Toast.LENGTH_LONG).show();
//            Log.v("tag",btDevice.getName() + " - " + btDevice.getAddress());
        }
    };

    @TargetApi(23)
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_db);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check     
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {        
                final AlertDialog.Builder builder = new AlertDialog.Builder(this); 
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect other users.");
                builder.setPositiveButton(android.R.string.ok, null); 
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {  
                        @Override 
                        public void onDismiss(DialogInterface dialog) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_REQUEST_COARSE_LOCATION);             
                        }  
                }); 
                builder.show();    
            }else{
           	 Resume();
            }
         }else{
        	 Resume();
         }
   
       
    }
protected void Resume(){
	 self = this;
     arrayAdapter = new ArrayAdapter<String>(self, R.layout.text);
    // ListView lv = self.getListView();
     ListView lv = (ListView)findViewById(R.id.listView);
     lv.setAdapter(arrayAdapter);
     lv.setOnItemClickListener(new OnItemClickListener() {
         @Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
             myBt.cancelDiscovery(); // Cancel BT discovery explicitly so
             // that connections can go through
             String btDeviceInfo = ((TextView) arg1).getText().toString();
             String btHardwareAddress = btDeviceInfo.substring(btDeviceInfo.length() - 17);
             Intent i = new Intent();
             i.putExtra(EXTRA_SELECTED_ADDRESS, btHardwareAddress);
             self.setResult(Activity.RESULT_OK, i);
             finish();
         }
     });
     myBt = BluetoothAdapter.getDefaultAdapter();
     myBt.startDiscovery();
     self.setResult(Activity.RESULT_CANCELED);
}
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(myReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(myReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myBt != null) {
            myBt.cancelDiscovery(); // Ensure that we don't leave discovery
            // running by accident
        }
    }
    
    @SuppressLint("NewApi")
	@Override
    public void onRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "coarse location permission granted");
                    Resume();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
        
    }

}
