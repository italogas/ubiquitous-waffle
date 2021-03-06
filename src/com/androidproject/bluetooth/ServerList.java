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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple list that displays Bluetooth devices that are in discoverable mode. This can be used as a gamelobby where players can see available servers and pick the one they wish to connect to.
 */

public class ServerList {
	public ServerList(Context context, ListView lv) {
		super();
		this.context = context;
		this.lv = lv;
	}

	ListView lv;
	public static String EXTRA_SELECTED_ADDRESS = "btaddress";
	public Thread waitForChoose;
	private BluetoothAdapter myBt;
	private Context context;

	public ArrayAdapter<String> arrayAdapter;

	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Parcelable btParcel = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			BluetoothDevice btDevice = (BluetoothDevice) btParcel;
			arrayAdapter.add(btDevice.getName() + " - " + btDevice.getAddress());
			// Toast.makeText(context, btDevice.getName() + " - " + btDevice.getAddress(), Toast.LENGTH_LONG).show();
			Log.v("Found", btDevice.getName() + " - " + btDevice.getAddress());
		}
	};

	@TargetApi(23)
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		// setContentView(R.layout.activity_db);

	}

	public int Result = Activity.RESULT_CANCELED;
	public Intent ResultIntent = null;

	public void Resume() {
		arrayAdapter = new ArrayAdapter<String>(context, R.layout.text);
		lv.setAdapter(arrayAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				myBt.cancelDiscovery(); // Cancel BT discovery explicitly so that connections can go through
				String btDeviceInfo = ((TextView) arg1).getText().toString();
				String btHardwareAddress = btDeviceInfo.substring(btDeviceInfo.length() - 17);
				Intent i = new Intent();
				i.putExtra(EXTRA_SELECTED_ADDRESS, btHardwareAddress);
				ResultIntent = i;
				device = btHardwareAddress;

				Result = Activity.RESULT_OK;
				arg1.setSelected(!arg1.isSelected());
				finish();
			}
		});
		onResume();
		myBt = BluetoothAdapter.getDefaultAdapter();
		myBt.startDiscovery();
	}

	public String device;

	public void finish() {

		onPause();
		onDestroy();
	}

	protected void onResume() {
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver(myReceiver, filter);
	}

	protected void onPause() {
		try {

			context.unregisterReceiver(myReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onDestroy() {
		if (myBt != null) {
			myBt.cancelDiscovery(); // Ensure that we don't leave discovery running by accident
		}
	}

}
