/**
 * 
 */
package sensors.recorder;

import android.hardware.SensorEvent;

/**
 * @author valer
 *
 */
public class SensorEventWrapper {

	private SensorEvent sensorEvent;
	private org.openintents.sensorsimulator.hardware.SensorEvent sensorEventSimulator;
	
	public SensorEventWrapper(SensorEvent sensorEvent) {
		this.sensorEvent = sensorEvent;
	}
	
	public SensorEventWrapper(org.openintents.sensorsimulator.hardware.SensorEvent sensorEvent) {
		this.sensorEventSimulator = sensorEvent;
	}
	
	public SensorEvent getSensorEvent() {
		return sensorEvent;
	}
	
	public org.openintents.sensorsimulator.hardware.SensorEvent getSensorEventSimulator() {
		return sensorEventSimulator;
	}
	
	public int getType() {
		if (sensorEvent != null) {
			return sensorEvent.sensor.getType();
		} else {
			return sensorEventSimulator.type;
		}
	}
	
	public String getTimestamp() {
		if (sensorEvent != null) {
			return String.valueOf(sensorEvent.timestamp);
		} else {
			return sensorEventSimulator.time;
		}		
	}
	
	public float[] getValues() {
		if (sensorEvent != null) {
			return sensorEvent.values;
		} else {
			return sensorEventSimulator.values;
		}				
	}
}
