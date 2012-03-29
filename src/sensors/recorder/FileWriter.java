/**
 * 
 */
package sensors.recorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.hardware.Sensor;
import android.os.Environment;
import android.util.Log;

/**
 * @author valer
 *
 */
public class FileWriter {

	private static final String TAG = "FileWriter";

	private static final String FILENAME = "sd-{1}-{0}-{2}.csv";
	
	FileOutputStream fos = null;
	String file = null;
	
	public boolean open(String sensitivity, String sensor) {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;

		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		if (!mExternalStorageAvailable || !mExternalStorageWriteable) { 
			Log.e(TAG, "External storage not writable");
			return false;
		}
		
		DateFormat ds = new SimpleDateFormat("yyMMddHHmmss");
		file = FILENAME.replace("{0}", sensor).replace("{1}", sensitivity).replace("{2}", ds.format(new Date()));
		File dataFile = new File(Environment.getExternalStorageDirectory(), file);
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				Log.e(TAG, "Exception creating file " + dataFile.getAbsolutePath() + ": " + e.getMessage());
				return false;
			}
		}
		try {
			fos = new FileOutputStream(dataFile);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Exception opening file " + dataFile.getAbsolutePath() + ": " + e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean close() {
		try {
			fos.close();
		} catch (IOException e) {
			Log.e(TAG, "Exception closing file " + file + ": " + e.getMessage());
			return false;
		}
		/*
		File dataFile = new File(Environment.getExternalStorageDirectory(), file);
		try {
			BufferedReader br = new BufferedReader(new FileReader(dataFile), 1024*8);
			String line = null;
			while((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Exception closing file " + file + ": " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "Exception closing file " + file + ": " + e.getMessage());
		}
		*/
		return true;
	}
	
	public boolean write(SensorEventWrapper sensorEvent) {
		String buf = null;
		if (sensorEvent.getType() == Sensor.TYPE_ACCELEROMETER) {
			buf = "acc, " + sensorEvent.getTimestamp() + ", " + sensorEvent.getValues()[0] + ", " + sensorEvent.getValues()[1] + ", " + sensorEvent.getValues()[2] + "\n";			
		} else if (sensorEvent.getType() == Sensor.TYPE_GYROSCOPE) {
			buf = "gyr, " + sensorEvent.getTimestamp() + ", " + sensorEvent.getValues()[0] + ", " + sensorEvent.getValues()[1] + ", " + sensorEvent.getValues()[2] + "\n";
		} else if (sensorEvent.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			buf = "mag, " + sensorEvent.getTimestamp() + ", " + sensorEvent.getValues()[0] + ", " + sensorEvent.getValues()[1] + ", " + sensorEvent.getValues()[2] + "\n";
		}
		try {
			fos.write(buf.getBytes());
		} catch (IOException e) {
			Log.e(TAG, "Exception writing to file " + file + ": " + e.getMessage());
			return false;
		}
		return true;
	}
}
