/**************************************************************************************************
  Filename:       DeviceActivity.java
  Revised:        $Date: 2013-09-05 07:58:48 +0200 (to, 05 sep 2013) $
  Revision:       $Revision: 27616 $

  Copyright 2013 Texas Instruments Incorporated. All rights reserved.
 
  IMPORTANT: Your use of this Software is limited to those specific rights
  granted under the terms of a software license agreement between the user
  who downloaded the software, his/her employer (which must be your employer)
  and Texas Instruments Incorporated (the "License").  You may not use this
  Software unless you agree to abide by the terms of the License. 
  The License limits your use, and you acknowledge, that the Software may not be 
  modified, copied or distributed unless used solely and exclusively in conjunction 
  with a Texas Instruments Bluetooth device. Other than for the foregoing purpose, 
  you may not use, reproduce, copy, prepare derivative works of, modify, distribute, 
  perform, display or sell this Software and/or its documentation for any purpose.
 
  YOU FURTHER ACKNOWLEDGE AND AGREE THAT THE SOFTWARE AND DOCUMENTATION ARE
  PROVIDED “AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
  INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, TITLE,
  NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT SHALL
  TEXAS INSTRUMENTS OR ITS LICENSORS BE LIABLE OR OBLIGATED UNDER CONTRACT,
  NEGLIGENCE, STRICT LIABILITY, CONTRIBUTION, BREACH OF WARRANTY, OR OTHER
  LEGAL EQUITABLE THEORY ANY DIRECT OR INDIRECT DAMAGES OR EXPENSES
  INCLUDING BUT NOT LIMITED TO ANY INCIDENTAL, SPECIAL, INDIRECT, PUNITIVE
  OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA, COST OF PROCUREMENT
  OF SUBSTITUTE GOODS, TECHNOLOGY, SERVICES, OR ANY CLAIMS BY THIRD PARTIES
  (INCLUDING BUT NOT LIMITED TO ANY DEFENSE THEREOF), OR OTHER SIMILAR COSTS.
 
  Should you have any questions regarding your right to use this Software,
  contact Texas Instruments Incorporated at www.TI.com

 **************************************************************************************************/
package ti.android.ble.sensortag;

import static ti.android.ble.sensortag.SensorTag.UUID_EUL_DATA;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import ti.android.ble.common.BluetoothLeService;
import ti.android.ble.common.GattInfo;
import ti.android.ble.common.HelpView;
import ti.android.util.Point3D;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class DeviceActivity extends ViewPagerActivity {
	// Log
	private static String TAG = "DeviceActivity";

	// Activity
	public static final String EXTRA_DEVICE = "EXTRA_DEVICE";
	private static final int PREF_ACT_REQ = 0;
	private static final int FWUPDATE_ACT_REQ = 1;
	private static final double ANGULAR_ERROR = 85;

	// UI
	private DeviceView mDeviceView;
	private StepView mStepView;
	private Context context;

	// BLE
	private BluetoothLeService mBtLeService = null;
	private List<String> mBluetoothDeviceAddressList;
	private List<String> mBluetoothDeviceNameList;
	private List<Integer> mBluetoothDevicePositionList;
	private List<BluetoothGatt> mBluetoothGattList;
	private List<BluetoothGattService> mServiceList = null;
	private static final int GATT_TIMEOUT = 100; // milliseconds
	private boolean mServicesRdy = false;
	private boolean mIsReceiving = false;

	// Data
	private float upperLeftLegLength = 0;
	private float lowerLeftLegLength = 0;
	private float upperRightLegLength = 0;
	private float lowerRightLegLength = 0;
	private Point3D upperLeftLegData;
	private Point3D lowerLeftLegData;
	private Point3D upperRightLegData;
	private Point3D lowerRightLegData;
	private Point3D upperLeftLegError;
	private Point3D lowerLeftLegError;
	private Point3D upperRightLegError;
	private Point3D lowerRightLegError;
	private Point3D BodyData;
	private boolean isRecording = false;
	private boolean isCalibrating = false;
	private double totalDistance = 0; // excluding current step length
	private double stepLength = 0;
	private double lastStepLength = 0;
	private double stepError = 0; // The error of step length
	private int numberOfSteps = 0;
	private boolean leftStepping = false;
	private int walkingState = 0; // 0 = standing, 1 = walking
	private int standingCount = 0; // Count when standing

	// SensorTag
	private List<Sensor> mEnabledSensors = new ArrayList<Sensor>();
	private BluetoothGattService mOadService = null;
	private BluetoothGattService mConnControlService = null;
	private boolean mMagCalibrateRequest = true;

	public DeviceActivity() {
		mResourceFragmentPager = R.layout.fragment_pager;
		mResourceIdPager = R.id.pager;
	}

	public static DeviceActivity getInstance() {
		return (DeviceActivity) mThis;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

		// GUI
		mDeviceView = new DeviceView();
		mStepView = new StepView();
		context = this;
		mSectionsPagerAdapter.addSection(mDeviceView, "SensorModule");
		mSectionsPagerAdapter.addSection(mStepView, "Overview");
		mSectionsPagerAdapter.addSection(new HelpView("help_device.html",
				R.layout.fragment_help, R.id.webpage), "Help");
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		// BLE
		mBtLeService = BluetoothLeService.getInstance();
		mServiceList = new ArrayList<BluetoothGattService>();
		mBluetoothDeviceAddressList = new ArrayList<String>();
		mBluetoothDeviceNameList = new ArrayList<String>();
		mBluetoothDevicePositionList = new ArrayList<Integer>();
		mBluetoothGattList = new ArrayList<BluetoothGatt>();

		// Data Initialization
		upperLeftLegData = new Point3D(90, 0, 0);
		lowerLeftLegData = new Point3D(90, 0, 0);
		upperRightLegData = new Point3D(90, 0, 0);
		lowerRightLegData = new Point3D(90, 0, 0);
		upperLeftLegError = new Point3D(0, 0, 0);
		lowerLeftLegError = new Point3D(0, 0, 0);
		upperRightLegError = new Point3D(0, 0, 0);
		lowerRightLegError = new Point3D(0, 0, 0);

		// GATT database
		Resources res = getResources();
		XmlResourceParser xpp = res.getXml(R.xml.gatt_uuid);
		new GattInfo(xpp);

		// Initialize sensor list
		updateSensorList();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		finishActivity(PREF_ACT_REQ);
		finishActivity(FWUPDATE_ACT_REQ);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.device_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.opt_prefs:
			startPrefrenceActivity();
			break;
		case R.id.opt_fwupdate:
			// startOadActivity();
			break;
		case R.id.opt_about:
			openAboutDialog();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		if (!mIsReceiving) {
			registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
			mIsReceiving = true;
		}
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
		if (mIsReceiving) {
			unregisterReceiver(mGattUpdateReceiver);
			mIsReceiving = false;
		}
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter fi = new IntentFilter();
		fi.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		fi.addAction(BluetoothLeService.ACTION_DATA_NOTIFY);
		fi.addAction(BluetoothLeService.ACTION_DATA_WRITE);
		fi.addAction(BluetoothLeService.ACTION_DATA_READ);
		return fi;
	}

	void onViewInflated(View view) {
		Log.d(TAG, "Gatt view ready");

		// Get Device Address
		mBluetoothDeviceAddressList = BluetoothLeService
				.getBluetoothDeviceAddressList();
		// Get Device Name
		mBluetoothDeviceNameList = BluetoothLeService
				.getBluetoothDeviceNameList();
		mDeviceView.setNames(mBluetoothDeviceNameList);

		// Get Sensor Positions and Limb Length
		getSensorPositions();
		getLimbLengths();

		// Get Gatt List
		mBluetoothGattList = BluetoothLeService.getBtGattList();

		// Set title bar to device name
		setTitle("" + mBluetoothGattList.size() + " devices connected");

		// Start service discovery
		if (!mServicesRdy && !mBluetoothGattList.isEmpty()) {
			mDeviceView.updateVisibility(mBluetoothGattList.size());
			for (int i = 0; i < mBluetoothGattList.size(); i++) {
				if (mBtLeService.getNumServices(mBluetoothDeviceAddressList
						.get(i)) == 0)
					discoverServices(i);
				else
					displayServices(i);
			}
		}
	}

	//
	// Application implementation
	//
	private void updateSensorList() {
		mEnabledSensors.clear();

		for (int i = 0; i < Sensor.SENSOR_LIST.length; i++) {
			Sensor sensor = Sensor.SENSOR_LIST[i];
			if (isEnabledByPrefs(sensor)) {
				mEnabledSensors.add(sensor);
			}
		}
	}

	boolean isEnabledByPrefs(final Sensor sensor) {
		String preferenceKeyString = "pref_"
				+ sensor.name().toLowerCase(Locale.ENGLISH) + "_on";

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		Boolean defaultValue = true;
		return prefs.getBoolean(preferenceKeyString, defaultValue);
	}

	private void getLimbLengths() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String defaultValue = "0";

		String preferenceKeyString = "pref_edit_upper_left_leg";
		upperLeftLegLength = Float.parseFloat(prefs.getString(
				preferenceKeyString, defaultValue)) / 100;
		preferenceKeyString = "pref_edit_lower_left_leg";
		lowerLeftLegLength = Float.parseFloat(prefs.getString(
				preferenceKeyString, defaultValue)) / 100;
		preferenceKeyString = "pref_edit_upper_right_leg";
		upperRightLegLength = Float.parseFloat(prefs.getString(
				preferenceKeyString, defaultValue)) / 100;
		preferenceKeyString = "pref_edit_lower_right_leg";
		lowerRightLegLength = Float.parseFloat(prefs.getString(
				preferenceKeyString, defaultValue)) / 100;
	}

	public int getSensorPosition(String sensorName) {
		int position = 0;
		int index = mBluetoothDeviceNameList.indexOf(sensorName);
		if (index < mBluetoothDevicePositionList.size() && index >= 0) {
			position = mBluetoothDevicePositionList.get(index);
		}
		return position;
	}

	private void getSensorPositions() {
		for (int i = 0; i < mBluetoothDeviceNameList.size(); i++) {
			int pos = getSensorPositionFromPreferences(mBluetoothDeviceNameList
					.get(i));
			mBluetoothDevicePositionList.add(pos);
		}
	}

	public int getSensorPositionFromPreferences(String sensorName) {
		String preferenceKeyString = "pref_select_"
				+ sensorName.toLowerCase(Locale.ENGLISH);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		String defaultValue = "0";
		return Integer.valueOf(prefs.getString(preferenceKeyString,
				defaultValue));
	}

	BluetoothGattService getOadService() {
		return mOadService;
	}

	BluetoothGattService getConnControlService() {
		return mConnControlService;
	}

	// private void startOadActivity() {
	// if (mOadService != null && mConnControlService != null) {
	// // Launch OAD
	// enableSensors(false);
	// enableNotifications(false);
	// Toast.makeText(this, "OAD service available", Toast.LENGTH_LONG).show();
	// final Intent i = new Intent(this, FwUpdateActivity.class);
	// startActivityForResult(i, FWUPDATE_ACT_REQ);
	// } else {
	// Toast.makeText(this, "OAD service not available on this device",
	// Toast.LENGTH_LONG).show();
	// }
	// }

	private void startPrefrenceActivity() {
		// Disable sensors and notifications when settings dialog is open
		for (int i = 0; i < mBluetoothDeviceAddressList.size(); i++) {
			enableSensors(false, mBluetoothDeviceAddressList.get(i));
			enableNotifications(false, mBluetoothDeviceAddressList.get(i));
		}

		final Intent i = new Intent(this, PreferencesActivity.class);
		i.putExtra(PreferencesActivity.EXTRA_SHOW_FRAGMENT,
				PreferencesFragment.class.getName());
		i.putExtra(PreferencesActivity.EXTRA_NO_HEADERS, true);
		// i.putExtra(EXTRA_DEVICE, mBluetoothDevice);
		startActivityForResult(i, PREF_ACT_REQ);
	}

	private void checkOad() {
		// Check if OAD is supported (needs OAD and Connection Control service)
		mOadService = null;
		mConnControlService = null;

		for (int i = 0; i < mServiceList.size()
				&& (mOadService == null || mConnControlService == null); i++) {
			BluetoothGattService srv = mServiceList.get(i);
			if (srv.getUuid().equals(GattInfo.OAD_SERVICE_UUID)) {
				mOadService = srv;
			}
			if (srv.getUuid().equals(GattInfo.CC_SERVICE_UUID)) {
				mConnControlService = srv;
			}
		}
	}

	private void discoverServices(int index) {
		BluetoothGatt mBtGatt = mBluetoothGattList.get(index);

		if (mBtGatt.discoverServices()) {
			Log.i(TAG, "START SERVICE DISCOVERY");
			mServiceList.clear();
			if (index == 0) {
				setStatus("Service discovery started", index);
			}
		} else {
			setError("Service discovery start failed", index);
		}
	}

	private void displayServices(int index) {
		mServicesRdy = true;
		String address = mBluetoothDeviceAddressList.get(index);

		try {
			mServiceList = mBtLeService.getSupportedGattServices(address);
		} catch (Exception e) {
			e.printStackTrace();
			mServicesRdy = false;
		}

		// Characteristics descriptor readout done
		if (mServicesRdy) {
			if (index == 0)
				setStatus("Service discovery complete", index);
			enableSensors(true, address);
			enableNotifications(true, address);
		} else {
			setError("Failed to read services", index);
		}
	}

	private void setError(String txt, int index) {
		if (mDeviceView != null)
			mDeviceView.setError(txt);
	}

	private void setStatus(String txt, int index) {
		if (mDeviceView != null)
			mDeviceView.setStatus(txt);
	}

	private void enableSensors(boolean enable, String address) {
		BluetoothGatt mBtGatt = mBluetoothGattList
				.get(mBluetoothDeviceAddressList.indexOf(address));
		for (Sensor sensor : mEnabledSensors) {
			UUID servUuid = sensor.getService();
			UUID confUuid = sensor.getConfig();

			// Skip keys
			if (confUuid == null)
				break;

			BluetoothGattService serv = mBtGatt.getService(servUuid);
			BluetoothGattCharacteristic charac = serv
					.getCharacteristic(confUuid);
			byte value = enable ? sensor.getEnableSensorCode()
					: Sensor.DISABLE_SENSOR_CODE;
			mBtLeService.writeCharacteristic(charac, value, address);
			mBtLeService.waitIdle(GATT_TIMEOUT);
		}

	}

	private void enableNotifications(boolean enable, String address) {
		BluetoothGatt mBtGatt = mBluetoothGattList
				.get(mBluetoothDeviceAddressList.indexOf(address));
		for (Sensor sensor : mEnabledSensors) {
			UUID servUuid = sensor.getService();
			UUID dataUuid = sensor.getData();
			BluetoothGattService serv = mBtGatt.getService(servUuid);
			BluetoothGattCharacteristic charac = serv
					.getCharacteristic(dataUuid);

			mBtLeService.setCharacteristicNotification(charac, enable, address);
			mBtLeService.waitIdle(GATT_TIMEOUT);
		}
	}

	void calibrateMagnetometer() {
		Log.d(TAG, "calibrateMagnetometer");
		MagnetometerCalibrationCoefficients.INSTANCE.val.x = 0.0;
		MagnetometerCalibrationCoefficients.INSTANCE.val.y = 0.0;
		MagnetometerCalibrationCoefficients.INSTANCE.val.z = 0.0;

		mMagCalibrateRequest = true;
	}

	// Activity result handling
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case PREF_ACT_REQ:
			mDeviceView.updateVisibility(mBluetoothGattList.size());
			Toast.makeText(this, "Applying preferences", Toast.LENGTH_SHORT)
					.show();
			if (!mIsReceiving) {
				mIsReceiving = true;
				registerReceiver(mGattUpdateReceiver,
						makeGattUpdateIntentFilter());
			}
			updateSensorList();
			for (int i = 0; i < mBluetoothDeviceAddressList.size(); i++) {
				enableSensors(true, mBluetoothDeviceAddressList.get(i));
				enableNotifications(true, mBluetoothDeviceAddressList.get(i));
			}
			break;
		case FWUPDATE_ACT_REQ:
			// FW update cancelled so resume
			for (int i = 0; i < mBluetoothDeviceAddressList.size(); i++) {
				enableSensors(true, mBluetoothDeviceAddressList.get(i));
				enableNotifications(true, mBluetoothDeviceAddressList.get(i));
			}
			break;
		default:
			Log.e(TAG, "Unknown request code");
			break;
		}
	}

	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			int status = intent.getIntExtra(BluetoothLeService.EXTRA_STATUS,
					BluetoothGatt.GATT_SUCCESS);
			String address = intent
					.getStringExtra(BluetoothLeService.EXTRA_ADDRESS);
			int index = 0;
			if (mBluetoothDeviceAddressList.contains(address))
				index = mBluetoothDeviceAddressList.indexOf(address);
			else {
				Log.e(TAG, "Unknown device");
			}

			if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action)) {
				if (status == BluetoothGatt.GATT_SUCCESS) {
					displayServices(index);
					checkOad();
				} else {
					Toast.makeText(getApplication(),
							"Service discovery failed", Toast.LENGTH_LONG)
							.show();
					return;
				}
			} else if (BluetoothLeService.ACTION_DATA_NOTIFY.equals(action)) {
				// Notification
				byte[] value = intent
						.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				String uuidStr = intent
						.getStringExtra(BluetoothLeService.EXTRA_UUID);
				onCharacteristicChanged(uuidStr, value, index);
			} else if (BluetoothLeService.ACTION_DATA_WRITE.equals(action)) {
				// Data written
				String uuidStr = intent
						.getStringExtra(BluetoothLeService.EXTRA_UUID);
				onCharacteristicWrite(uuidStr, status, index);
			} else if (BluetoothLeService.ACTION_DATA_READ.equals(action)) {
				// Data read
				String uuidStr = intent
						.getStringExtra(BluetoothLeService.EXTRA_UUID);
				byte[] value = intent
						.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
				onCharacteristicsRead(uuidStr, value, status, index);
			}

			if (status != BluetoothGatt.GATT_SUCCESS) {
				setError("GATT error code: " + status, index);
			}
		}
	};

	private void onCharacteristicWrite(String uuidStr, int status, int index) {
		Log.d(TAG, "onCharacteristicWrite: " + uuidStr + "with device " + index);
	}

	private void onCharacteristicChanged(String uuidStr, byte[] value, int index) {
		if (mDeviceView != null) {
			if (mMagCalibrateRequest) {
				if (uuidStr.equals(SensorTag.UUID_MAG_DATA.toString())) {
					Point3D v = Sensor.MAGNETOMETER.convert(value);

					MagnetometerCalibrationCoefficients.INSTANCE.val = v;
					mMagCalibrateRequest = false;
					Toast.makeText(this, "Magnetometer calibrated",
							Toast.LENGTH_SHORT).show();
				}
			}
			// mDeviceView.onCharacteristicChanged(uuidStr, value, index);
			if (uuidStr.equals(UUID_EUL_DATA.toString())) {
				Point3D v = Sensor.EULER_ANGLE.convert(value);
				if (mBluetoothDevicePositionList.get(index) == Position.UPPER_LEFT_LEG) {
					upperLeftLegData = Point3D.subtract(v, upperLeftLegError);
					mDeviceView.UpdateAngles(upperLeftLegData, index);
				}
				if (mBluetoothDevicePositionList.get(index) == Position.LOWER_LEFT_LEG) {
					lowerLeftLegData = Point3D.subtract(v, lowerLeftLegError);
					mDeviceView.UpdateAngles(lowerLeftLegData, index);
				}
				if (mBluetoothDevicePositionList.get(index) == Position.UPPER_RIGHT_LEG) {
					upperRightLegData = Point3D.subtract(v, upperRightLegError);
					mDeviceView.UpdateAngles(upperRightLegData, index);
				}
				if (mBluetoothDevicePositionList.get(index) == Position.LOWER_RIGHT_LEG) {
					lowerRightLegData = Point3D.subtract(v, lowerRightLegError);
					mDeviceView.UpdateAngles(lowerRightLegData, index);
				}
				if (mBluetoothDevicePositionList.get(index) == Position.BODY) {
					BodyData = v;
					mDeviceView.UpdateAngles(BodyData, index);
				}
				if (isRecording) {
					UpdateSummary();
				}
				if (isCalibrating) {
					Calibrate();
				}
			}

		}
	}

	private void onCharacteristicsRead(String uuidStr, byte[] value,
			int status, int index) {
		Log.i(TAG, "onCharacteristicsRead: " + uuidStr + "with device " + index);
		// if (uuidStr.equals(SensorTag.UUID_BAR_CALI.toString())) {
		// Log.i(TAG, "CALIBRATED");
		// // Barometer calibration values are read.
		// List<Integer> cal = new ArrayList<Integer>();
		// for (int offset = 0; offset < 8; offset += 2) {
		// Integer lowerByte = (int) value[offset] & 0xFF;
		// Integer upperByte = (int) value[offset +1] & 0xFF;
		// cal.add((upperByte << 8) + lowerByte);
		// }
		//
		// for (int offset = 8; offset < 16; offset += 2) {
		// Integer lowerByte = (int) value[offset] & 0xFF;
		// Integer upperByte = (int) value[offset +1];
		// cal.add((upperByte << 8) + lowerByte);
		// }
		//
		// BarometerCalibrationCoefficients.INSTANCE.barometerCalibrationCoefficients
		// = cal;
		// }
	}

	// Start recording SensorModule data
	public void startRecording() {
		totalDistance = 0;
		stepLength = 0;
		numberOfSteps = 0;
		walkingState = 0;
		standingCount = 0;
		stepError = (upperLeftLegLength + lowerLeftLegLength)
				* Math.cos(Math.toRadians(ANGULAR_ERROR));
		leftStepping = false;
		isCalibrating = false;
		isRecording = true;
	}

	// Stop recording SensorModule data
	public void stopRecording() {
		isRecording = false;
	}

	public void startCalibration() {
		if (isCalibrating) {
			isCalibrating = false;
			Toast.makeText(context, "Calibration Finished!", Toast.LENGTH_SHORT)
					.show();
		} else {
			standingCount = 0;
			isCalibrating = true;
		}
	}

	// Calculate and update step summary
	private void UpdateSummary() {
		stepLength = 0;

		// x > 90 means the limb swinging backwards, < 90 means otherwise
		// Calculate Left Leg
		double left = (Math.cos(Math.toRadians(upperLeftLegData.y))
				* Math.cos(Math.toRadians(upperLeftLegData.x)) * upperLeftLegLength)
				+ (Math.cos(Math.toRadians(lowerLeftLegData.y))
						* Math.cos(Math.toRadians(lowerLeftLegData.x)) * lowerLeftLegLength);
		// Calculate Right Leg
		double right = (Math.cos(Math.toRadians(upperRightLegData.y))
				* Math.cos(Math.toRadians(upperRightLegData.x)) * upperRightLegLength)
				+ (Math.cos(Math.toRadians(lowerRightLegData.y))
						* Math.cos(Math.toRadians(lowerRightLegData.x)) * lowerRightLegLength);

		if (walkingState == 0) {
			standingCount = 0;
			if ((left - right) > stepError) {
				leftStepping = true;
				walkingState = 1;
			} else if ((right - left) > stepError) {
				leftStepping = false;
				walkingState = 1;
			}
		} else if (walkingState == 1) {
			if ((left - right) > stepError) {
				if (leftStepping == false) {
					numberOfSteps++;
					totalDistance = totalDistance + lastStepLength;
					Log.d("Result", "NumberOfSteps = " + numberOfSteps
							+ "\ntotalDistance = " + totalDistance
							+ "\nlastStepLength = " + lastStepLength);
					lastStepLength = 0;
				}
				stepLength = left - right;
				// true = the left leg surpasses right leg;
				leftStepping = true;
			} else if ((right - left) > stepError) {
				if (leftStepping == true) {
					numberOfSteps++;
					totalDistance = totalDistance + lastStepLength;
					Log.d("Result", "NumberOfSteps = " + numberOfSteps
							+ "\ntotalDistance = " + totalDistance
							+ "\nlastStepLength = " + lastStepLength);
					lastStepLength = 0;
				}
				stepLength = right - left;
				// false = the right leg surpasses left leg;
				leftStepping = false;
			} else {
				standingCount++;
				if (standingCount > 100) {
					numberOfSteps++;
					totalDistance = totalDistance + lastStepLength;
					Log.d("Result", "NumberOfSteps = " + numberOfSteps
							+ "\ntotalDistance = " + totalDistance
							+ "\nlastStepLength = " + lastStepLength);
					lastStepLength = 0;
					walkingState = 0;
				}
			}

			if (stepLength > lastStepLength) {
				lastStepLength = stepLength;
			}
		}

		mStepView.UpdateSummary(totalDistance, stepLength, numberOfSteps);
	}

	private void Calibrate() {
		// Assume all sensors are at default angles (90, 0, x)
		// Average over 500 recordings, find position error

		// upperLeftLeg
		upperLeftLegError.x = (upperLeftLegError.x * standingCount + (upperLeftLegData.x - 90))
				/ standingCount;
		upperLeftLegError.y = (upperLeftLegError.y * standingCount + (upperLeftLegData.y - 0))
				/ standingCount;
		// lowerLeftLeg
		lowerLeftLegError.x = (lowerLeftLegError.x * standingCount + (lowerLeftLegData.x - 90))
				/ standingCount;
		lowerLeftLegError.y = (lowerLeftLegError.y * standingCount + (lowerLeftLegData.y - 0))
				/ standingCount;
		// upperRightLeg
		upperRightLegError.x = (upperRightLegError.x * standingCount + (upperRightLegData.x - 90))
				/ standingCount;
		upperRightLegError.y = (upperRightLegError.y * standingCount + (upperRightLegData.y - 0))
				/ standingCount;
		// lowerRightLeg
		lowerRightLegError.x = (lowerRightLegError.x * standingCount + (lowerRightLegData.x - 90))
				/ standingCount;
		lowerRightLegError.y = (lowerRightLegError.y * standingCount + (lowerRightLegData.y - 0))
				/ standingCount;

		standingCount++;
		if (standingCount >= 500) {
			isCalibrating = false;
			Toast.makeText(context, "Calibration Finished!", Toast.LENGTH_SHORT)
					.show();
			return;
		}
	}
}
