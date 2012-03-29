/**
 * 
 */
package sensors.recorder;

import org.openintents.sensorsimulator.hardware.Sensor;

/**
 * @author valer
 *
 */
public class SensorWrapper {

	private android.hardware.Sensor sensorOriginal;
	private Sensor sensorSimulator;
	
	public SensorWrapper(Sensor sensorSimulator) {
		this.sensorSimulator = sensorSimulator;
	}
	
	public SensorWrapper(android.hardware.Sensor sensor) {
		this.sensorOriginal = sensor;
	}
	
	public Sensor getSensorSimulator() {
		return sensorSimulator;
	}
	
	public android.hardware.Sensor getSensorOriginal() {
		return sensorOriginal;
	}
	
}
