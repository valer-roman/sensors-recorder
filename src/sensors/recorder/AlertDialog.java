/**
 * 
 */
package sensors.recorder;

import android.content.Context;
import android.content.DialogInterface;

/**
 * @author valer
 *
 */
public class AlertDialog {

	public static void show(Context c, String title, String message) {
		android.app.AlertDialog.Builder ad = new android.app.AlertDialog.Builder(c);
		ad.setTitle(title);
		ad.setMessage(message);
		ad.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						dialog.dismiss();
					}
				});
		ad.create().show();
	}
	
}
