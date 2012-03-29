package sensors.recorder;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html.TagHandler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class SensorsRecorderActivity extends Activity {
	
	private static final String TAG = "SensorRecorderActivity";
	
    //private SensorManagerSimulator sensorManager;
	private SensorManagerWrapper sensorManager;
    private Handler threadHandler;

    private FileWriter fwAccelerator = new FileWriter();
    private FileWriter fwGyroscope = new FileWriter();
    private FileWriter fwMagneticField = new FileWriter();

	private RadioButton rbFastest;
	private RadioButton rbGame;
	private RadioButton rbNormal;
	private RadioButton rbUI;

    private class SensorEventListenerWrapperImpl extends SensorEventListenerWrapper {
		
    	private FileWriter fw;
    	
    	public SensorEventListenerWrapperImpl(FileWriter fw) {
			this.fw = fw;
		}
    	
		public void onSensorChanged(SensorEventWrapper sensorEvent) {
			if (!fw.write(sensorEvent)) {
				turnOffSensors(this);
			}
		}
		
		public void onAccuracyChanged(
				SensorWrapper sensor, int rate) {
			System.out.println("accuracy changed to " + rate);
		}
	};
	
	private SensorEventListenerWrapperImpl accSensorEventListener = new SensorEventListenerWrapperImpl(fwAccelerator);
	private SensorEventListenerWrapperImpl gyrSensorEventListener = new SensorEventListenerWrapperImpl(fwGyroscope);
	private SensorEventListenerWrapperImpl magSensorEventListener = new SensorEventListenerWrapperImpl(fwMagneticField);
	
	private void turnOffSensors(SensorEventListenerWrapperImpl sensorEventListener) {
		sensorManager.unregisterListener(sensorEventListener);
		
		Button sensorsSwitch = (Button) findViewById(R.id.sensorsSwitch);
		sensorsSwitch.setText(getString(R.string.sensors_switch_off));
	}
	
	private String getSensitivityLabel() {
		if (rbFastest.isChecked()) {
			return "fastest";
		} else if (rbGame.isChecked()) {
			return "game";
		} else if (rbNormal.isChecked()) {
			return "normal";
		} else  if (rbUI.isChecked()) {
			return "ui";
		}
		return "";
	}
	
    private OnClickListener sensorsSwitchOnClickListener = new OnClickListener() {
		private boolean on;
		
		public void onClick(View v) {
			if (!on) {
				String sensitivityLabel = getSensitivityLabel();
				if (!(fwAccelerator.open("acc", sensitivityLabel)) || !(fwGyroscope.open("gyr", sensitivityLabel)) || 
						!(fwMagneticField.open("mag", sensitivityLabel))) {
					sensors.recorder.AlertDialog.show(v.getContext(), "Error", "Could not open files, check logs!");
				}
				new Thread(delay).start();
			} else {
				if (!(fwAccelerator.close()) || !(fwGyroscope.close()) || !(fwMagneticField.close())) {
					sensors.recorder.AlertDialog.show(v.getContext(), "Error", "Could not close file, check logs!");
				}
				turnOffSensors(accSensorEventListener);
				turnOffSensors(gyrSensorEventListener);
				turnOffSensors(magSensorEventListener);
			}
			on = !on;
		}
		
	};
	
	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private Runnable startSensors = new Runnable() {
		
		public void run() {
			int sensorDelay = -1;
			if (rbFastest.isChecked()) {
				sensorDelay = SensorManager.SENSOR_DELAY_FASTEST;
			} else if (rbGame.isChecked()) {
				sensorDelay = SensorManager.SENSOR_DELAY_GAME;
			} else if (rbNormal.isChecked()) {
				sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
			} else  if (rbUI.isChecked()) {
				sensorDelay = SensorManager.SENSOR_DELAY_UI;
			}
			SensorWrapper sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (sensorAccelerometer == null || !sensorManager.registerListener(accSensorEventListener, sensorAccelerometer, sensorDelay)) {
				Log.e(TAG, "Could not register event on accelerator!");
				sensors.recorder.AlertDialog.show(SensorsRecorderActivity.this, "Error", "Could not register event on accelerator!");
				return;
			}
			
			SensorWrapper sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			if (sensorGyroscope == null || !sensorManager.registerListener(gyrSensorEventListener, sensorGyroscope, sensorDelay)) {
				Log.e(TAG, "Could not register event on gyroscope!");
				sensors.recorder.AlertDialog.show(SensorsRecorderActivity.this, "Error", "Could not register event on gyroscope!");
				return;
			}

			SensorWrapper sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			if (sensorMagneticField == null || !sensorManager.registerListener(magSensorEventListener, sensorMagneticField, sensorDelay)) {
				Log.e(TAG, "Could not register event on magnetic field!");
				sensors.recorder.AlertDialog.show(SensorsRecorderActivity.this, "Error", "Could not register event on magnetic field!");
				return;
			}
		}
		
	};
	
	private class SensorsSwitchTextUpdater implements Runnable {

		private String text;
		
		SensorsSwitchTextUpdater(String text) {
			this.text = text;
		}
		
		public void run() {
			Button sensorsSwitch = (Button) findViewById(R.id.sensorsSwitch);
			sensorsSwitch.setText(text);
		}
		
	}
	
	private Runnable delay = new Runnable() {
		
		public void run() {
			
			threadHandler.post(new SensorsSwitchTextUpdater(getString(R.string.sensors_switch_prepare)));
			sleep();
			threadHandler.post(new SensorsSwitchTextUpdater(getString(R.string.sensors_switch_3)));
			sleep();
			threadHandler.post(new SensorsSwitchTextUpdater(getString(R.string.sensors_switch_2)));
			sleep();
			threadHandler.post(new SensorsSwitchTextUpdater(getString(R.string.sensors_switch_1)));
			
			threadHandler.post(startSensors);
			
			sleep();
			threadHandler.post(new SensorsSwitchTextUpdater(getString(R.string.sensors_switch_go)));
			sleep();
			threadHandler.post(new SensorsSwitchTextUpdater(getString(R.string.sensors_switch_on)));
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        threadHandler = new Handler();
        
		sensorManager = new SensorManagerWrapper(this, SENSOR_SERVICE); 
		
		Button sensorsSwitch = (Button) findViewById(R.id.sensorsSwitch);
		sensorsSwitch.setOnClickListener(sensorsSwitchOnClickListener);
		
		rbFastest = (RadioButton) findViewById(R.id.radioFastest);
		rbGame = (RadioButton) findViewById(R.id.radioGame);
		rbNormal = (RadioButton) findViewById(R.id.radioNormal);
		rbUI = (RadioButton) findViewById(R.id.radioUI);
		rbNormal.setChecked(true);
    }

	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(accSensorEventListener);
		sensorManager.unregisterListener(gyrSensorEventListener);
		sensorManager.unregisterListener(magSensorEventListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
}