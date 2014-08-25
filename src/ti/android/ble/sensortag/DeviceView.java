/**************************************************************************************************
  Filename:       DeviceView.java
  Revised:        $Date: 2013-08-30 12:02:37 +0200 (fr, 30 aug 2013) $
  Revision:       $Revision: 27470 $

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

import static ti.android.ble.sensortag.R.drawable.buttonsoffoff;
import static ti.android.ble.sensortag.R.drawable.buttonsoffon;
import static ti.android.ble.sensortag.R.drawable.buttonsonoff;
import static ti.android.ble.sensortag.R.drawable.buttonsonon;
import static ti.android.ble.sensortag.SensorTag.UUID_ACC_DATA;
import static ti.android.ble.sensortag.SensorTag.UUID_EUL_DATA;
import static ti.android.ble.sensortag.SensorTag.UUID_GYR_DATA;
import static ti.android.ble.sensortag.SensorTag.UUID_KEY_DATA;
import static ti.android.ble.sensortag.SensorTag.UUID_MAG_DATA;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ti.android.util.Point3D;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

// Fragment for Device View
public class DeviceView extends Fragment {

	private static final String TAG = "DeviceView";

	// Sensor table; the iD corresponds to row number
	private static final int ID_OFFSET = 0;
	private static final int ID_KEY = 0;
	private static final int ID_ACC = 1;
	private static final int ID_MAG = 2;
	private static final int ID_GYR = 3;
	private static final int ID_EUL = 4;

	public static DeviceView mInstance = null;

	// GUI
	private TableLayout table;
	private TextView mAccValue;
	private TextView mMagValue;
	private TextView mGyrValue;
	private List<TextView> mEulValues;
	private List<TextView> mNames;
	private ImageView mButton;
	private TextView mStatus;
	private TableRow mMagPanel;
	private int n;

	// House-keeping
	private DecimalFormat decimal = new DecimalFormat("+0.00;-0.00");
	private DeviceActivity mActivity;
	private static final double PA_PER_METER = 12.0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		mInstance = this;
		mActivity = (DeviceActivity) getActivity();

		// The last two arguments ensure LayoutParams are inflated properly.
		View view = inflater.inflate(R.layout.services_browser, container,
				false);

		// Hide all Sensors initially (but show the last line for status)
		table = (TableLayout) view.findViewById(R.id.services_browser_layout);

		// UI widgets
		mAccValue = (TextView) view.findViewById(R.id.accelerometerTxt);
		mMagValue = (TextView) view.findViewById(R.id.magnetometerTxt);
		mGyrValue = (TextView) view.findViewById(R.id.gyroscopeTxt);
		mButton = (ImageView) view.findViewById(R.id.buttons);
		mStatus = (TextView) view.findViewById(R.id.status);

		// UI widges of euler angle
		TextView mEulValue;
		TextView mName;
		mNames = new ArrayList<TextView>();
		mEulValues = new ArrayList<TextView>();
		mName = (TextView) view.findViewById(R.id.eulName1);
		mEulValue = (TextView) view.findViewById(R.id.eulerTxt1);
		mNames.add(mName);
		mEulValues.add(mEulValue);
		mName = (TextView) view.findViewById(R.id.eulName2);
		mEulValue = (TextView) view.findViewById(R.id.eulerTxt2);
		mNames.add(mName);
		mEulValues.add(mEulValue);
		mName = (TextView) view.findViewById(R.id.eulName3);
		mEulValue = (TextView) view.findViewById(R.id.eulerTxt3);
		mNames.add(mName);
		mEulValues.add(mEulValue);
		mName = (TextView) view.findViewById(R.id.eulName4);
		mEulValue = (TextView) view.findViewById(R.id.eulerTxt4);
		mNames.add(mName);
		mEulValues.add(mEulValue);
		mName = (TextView) view.findViewById(R.id.eulName5);
		mEulValue = (TextView) view.findViewById(R.id.eulerTxt5);
		mNames.add(mName);
		mEulValues.add(mEulValue);

		// Support for calibration
		mMagPanel = (TableRow) view.findViewById(R.id.magPanel);
		OnClickListener cl = new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.magPanel:
					mActivity.calibrateMagnetometer();
					break;
				default:
				}
			}
		};

		mMagPanel.setOnClickListener(cl);

		// Notify activity that UI has been inflated
		mActivity.onViewInflated(view);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateVisibility(n);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void setNames(List<String> Names) {
		for (int i = 0; i < Names.size(); i++) {
			mNames.get(i).setText(Names.get(i));
		}
	}

	/**
	 * Handle changes in sensor values
	 * */
	public void onCharacteristicChanged(String uuidStr, byte[] rawValue,
			int index) {
		Point3D v;
		String msg;

		if (uuidStr.equals(UUID_ACC_DATA.toString())) {
			v = Sensor.ACCELEROMETER.convert(rawValue);
			msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
					+ decimal.format(v.z) + "\n";
			mAccValue.setText(msg);
		}

		if (uuidStr.equals(UUID_MAG_DATA.toString())) {
			v = Sensor.MAGNETOMETER.convert(rawValue);
			msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
					+ decimal.format(v.z) + "\n";
			mMagValue.setText(msg);
		}

		if (uuidStr.equals(UUID_GYR_DATA.toString())) {
			v = Sensor.GYROSCOPE.convert(rawValue);
			msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
					+ decimal.format(v.z) + "\n";
			mGyrValue.setText(msg);
		}

		if (uuidStr.equals(UUID_EUL_DATA.toString())) {
			v = Sensor.EULER_ANGLE.convert(rawValue);
			msg = decimal.format(v.x) + "\n" + decimal.format(v.y) + "\n"
					+ decimal.format(v.z) + "\n";
			if (mEulValues != null) {
				mEulValues.get(index).setText(msg);
			}
		}

		if (uuidStr.equals(UUID_KEY_DATA.toString())) {
			SimpleKeysStatus s;
			final int img;
			s = Sensor.SIMPLE_KEYS.convertKeys(rawValue);

			switch (s) {
			case OFF_OFF:
				img = buttonsoffoff;
				break;
			case OFF_ON:
				img = buttonsoffon;
				break;
			case ON_OFF:
				img = buttonsonoff;
				break;
			case ON_ON:
				img = buttonsonon;
				break;
			default:
				throw new UnsupportedOperationException();
			}

			mButton.setImageResource(img);
		}
	}

	void updateVisibility(int numModules) {
		showItem(ID_KEY, mActivity.isEnabledByPrefs(Sensor.SIMPLE_KEYS));
		showItem(ID_ACC, mActivity.isEnabledByPrefs(Sensor.ACCELEROMETER));
		showItem(ID_MAG, mActivity.isEnabledByPrefs(Sensor.MAGNETOMETER));
		showItem(ID_GYR, mActivity.isEnabledByPrefs(Sensor.GYROSCOPE));
		if (mActivity.isEnabledByPrefs(Sensor.EULER_ANGLE)) {
			n = numModules;
			for (int i = 0; i < numModules; i++) {
				showItem(ID_EUL + i,
						mActivity.isEnabledByPrefs(Sensor.EULER_ANGLE));
			}
		}
	}

	private void showItem(int id, boolean visible) {
		View hdr = table.getChildAt(id * 2 + ID_OFFSET);
		View txt = table.getChildAt(id * 2 + ID_OFFSET + 1);
		int vc = visible ? View.VISIBLE : View.GONE;
		hdr.setVisibility(vc);
		txt.setVisibility(vc);
	}

	void setStatus(String txt) {
		mStatus.setText(txt);
		mStatus.setTextAppearance(mActivity, R.style.statusStyle_Success);
	}

	void setError(String txt) {
		mStatus.setText(txt);
		mStatus.setTextAppearance(mActivity, R.style.statusStyle_Failure);
	}

	void setBusy(boolean f) {
		if (f)
			mStatus.setTextAppearance(mActivity, R.style.statusStyle_Busy);
		else
			mStatus.setTextAppearance(mActivity, R.style.statusStyle);
	}

}
