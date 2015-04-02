package hao.motiontracker.main;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StepView extends Fragment {

	private static final String TAG = "StepView";

	public static StepView mInstance = null;

	private DeviceActivity mActivity;
	private List<TextView> mPositions;
	private TextView mSummaryValues;
	private Button mStartButton;
	private Button mStopButton;
	private Button mCalibrateButton;
	private DecimalFormat decimal = new DecimalFormat("+0.00;-0.00");

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.i(TAG, "onCreateView");
		mInstance = this;
		mActivity = (DeviceActivity) getActivity();

		View view = inflater.inflate(R.layout.step_overview, container, false);

		// UI widget
		TextView mPosition;
		mPositions = new ArrayList<TextView>();
		mPosition = (TextView) view.findViewById(R.id.SensorModule1_position);
		mPositions.add(mPosition);
		mPosition = (TextView) view.findViewById(R.id.SensorModule2_position);
		mPositions.add(mPosition);
		mPosition = (TextView) view.findViewById(R.id.SensorModule3_position);
		mPositions.add(mPosition);
		mPosition = (TextView) view.findViewById(R.id.SensorModule4_position);
		mPositions.add(mPosition);
		mPosition = (TextView) view.findViewById(R.id.SensorModule5_position);
		mPositions.add(mPosition);
		mSummaryValues = (TextView) view.findViewById(R.id.summaryTxt);
		
		mStartButton = (Button) view.findViewById(R.id.btn_start);
		mStartButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mActivity.startRecording();
			}
			
		});
		mStopButton = (Button) view.findViewById(R.id.btn_stop);
		mStopButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mActivity.stopRecording();
			}
			
		});
		mCalibrateButton = (Button) view.findViewById(R.id.btn_calibrate);
		mCalibrateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mActivity.startCalibration();
			}
			
		});

		// Set Sensor Position
		setSensorPosition();
		return view;
	}

	public void setSensorPosition() {
		for (int i = 1; i <= 5; i++) {
			int position = mActivity.getSensorPosition("SensorModule"+i);
			mPositions.get(i-1).setText(Position.getString(position));
		}
	}
	
	public void UpdateSummary(double totalDistance, double stepLength, int numOfSteps) {
		String msg = decimal.format(totalDistance + stepLength) + "\n" + numOfSteps + "\n" + decimal.format(stepLength) + "\n";
		mSummaryValues.setText(msg);
	}
}
