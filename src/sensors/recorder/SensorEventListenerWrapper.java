/**
 * 
 */
package sensors.recorder;

import android.hardware.SensorEventListener;


/**
 * @author valer
 *
 */
public abstract class SensorEventListenerWrapper {

	private SensorEventListener sensorEventListener;
	private org.openintents.sensorsimulator.hardware.SensorEventListener sensorEventListenerSimulator;

	public abstract void onSensorChanged(SensorEventWrapper sensorEvent);
	
	public abstract void onAccuracyChanged(SensorWrapper sensor, int arg1);

	public SensorEventListener getSensorEventListener() {
		return sensorEventListener;
	}

	public void setSensorEventListener(SensorEventListener sensorEventListener) {
		this.sensorEventListener = sensorEventListener;
	}

	public org.openintents.sensorsimulator.hardware.SensorEventListener getSensorEventListenerSimulator() {
		return sensorEventListenerSimulator;
	}

	public void setSensorEventListenerSimulator(
			org.openintents.sensorsimulator.hardware.SensorEventListener sensorEventListenerSimulator) {
		this.sensorEventListenerSimulator = sensorEventListenerSimulator;
	}

}
