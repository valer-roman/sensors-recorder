/**
 * 
 */
package sensors.recorder;

import org.openintents.sensorsimulator.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * @author valer
 *
 */
public class SensorManagerWrapper {

	private SensorManager sensorManager;
	private SensorManagerSimulator sensorManagerSimulator;
	
	public SensorManagerWrapper(Context context, String sensorManagerName) {
		if (Configuration.SIMULATION) {
			sensorManagerSimulator = SensorManagerSimulator.getSystemService(context, sensorManagerName);
			sensorManagerSimulator.connectSimulator();
		} else {
			sensorManager = (SensorManager) context.getSystemService(sensorManagerName);
		}
	}
	
	SensorWrapper getDefaultSensor(int type) {
		if (Configuration.SIMULATION) {
			org.openintents.sensorsimulator.hardware.Sensor sensor = sensorManagerSimulator.getDefaultSensor(type);
			return new SensorWrapper(sensor)	;
		} else {
			Sensor sensor = sensorManager.getDefaultSensor(type);
			if (sensor == null) {
				return null;
			}
			Log.i("sensor", "Using sensor : " + sensor.getName() + "," + sensor.getType() + "," + sensor.getVendor() + "," + sensor.getVersion() + "," + sensor.getPower() + "," + sensor.getResolution() + "," + sensor.getMaximumRange());
			return new SensorWrapper(sensor);
		}
	}

	public class SensorEventListenerImpl implements android.hardware.SensorEventListener {
		
		private SensorEventListenerWrapper sensorEventListenerWrapper;
		
		public SensorEventListenerImpl(SensorEventListenerWrapper sensorEventListenerWrapper) {
			this.sensorEventListenerWrapper = sensorEventListenerWrapper;
		}
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			sensorEventListenerWrapper.onSensorChanged(new SensorEventWrapper(event));
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			sensorEventListenerWrapper.onAccuracyChanged(new SensorWrapper(sensor), accuracy);
		}
	};

	public class SensorEventListenerSimulatorImpl implements SensorEventListener {
		
		private SensorEventListenerWrapper sensorEventListenerWrapper;
		
		public SensorEventListenerSimulatorImpl(SensorEventListenerWrapper sensorEventListenerWrapper) {
			this.sensorEventListenerWrapper = sensorEventListenerWrapper;
		}
		
		@Override
		public void onSensorChanged(org.openintents.sensorsimulator.hardware.SensorEvent event) {
			sensorEventListenerWrapper.onSensorChanged(new SensorEventWrapper(event));
		}
		
		@Override
		public void onAccuracyChanged(org.openintents.sensorsimulator.hardware.Sensor sensor, int accuracy) {
			sensorEventListenerWrapper.onAccuracyChanged(new SensorWrapper(sensor), accuracy);
		}
	};

	public boolean registerListener(SensorEventListenerWrapper sensorEventListener, SensorWrapper sensor, int rate) {
		if (Configuration.SIMULATION) {
			sensorEventListener.setSensorEventListenerSimulator(new SensorEventListenerSimulatorImpl(sensorEventListener));
			org.openintents.sensorsimulator.hardware.Sensor sensorSimulator = sensor.getSensorSimulator();
			return sensorManagerSimulator.registerListener(sensorEventListener.getSensorEventListenerSimulator(), sensorSimulator, rate);
		} else {
			sensorEventListener.setSensorEventListener(new SensorEventListenerImpl(sensorEventListener));
			Sensor sensorOriginal = sensor.getSensorOriginal();
			return sensorManager.registerListener(sensorEventListener.getSensorEventListener(), sensorOriginal, rate);
		}
	}

	public void unregisterListener(SensorEventListenerWrapper sensorEventListener) {
		if (Configuration.SIMULATION) {
			sensorManagerSimulator.unregisterListener(sensorEventListener.getSensorEventListenerSimulator());
		} else {
			sensorManager.unregisterListener(sensorEventListener.getSensorEventListener());
		}
	}
	
}
