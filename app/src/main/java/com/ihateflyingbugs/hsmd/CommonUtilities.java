package com.ihateflyingbugs.hsmd;

import android.content.Context;
import android.content.Intent;

public class CommonUtilities {
	public static final String SERVER_URL = "http://www.lnslab.com/vocaslide/gcm_test.php";

	// Google project id
	public static final String SENDER_ID = "1050676872115";

	//public static final String SENDER_ID = "82686358156";
	/**
	 * Tag used on log messages.
	 */

	public static final String TAG = "VOCAGCM";

	public static final String DISPLAY_MESSAGE_ACTION =
			"com.ihateflyingbugs.hsmd.DISPLAY_MESSAGE";

	public static final String EXTRA_MESSAGE = "message";

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by
	 * the UI and the background service.
	 *
	 * @param mContext application's mContext.
	 * @param message message to be displayed.
	 */

	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}
