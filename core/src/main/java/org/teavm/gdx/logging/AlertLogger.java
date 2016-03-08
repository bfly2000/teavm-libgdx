
package org.teavm.gdx.logging;

import org.teavm.jso.JSBody;

/** Delegates all logging to the alert mechanism. Should be used only for debugging.
 *
 * @author MJ */
public class AlertLogger extends AbstractLogger {
	@Override
	public void log (final String tag, final String message) {
		alert(tag + ": " + message);
	}

	@Override
	public void log (final String tag, final String message, final Throwable exception) {
		alert(tag + ": " + message + " " + exception.getMessage());
	}

	/** Alerts the passed data.
	 *
	 * @param msg message to alert. */
	@JSBody(params = "msg", script = "alert(msg);")
	public static native void alert (String msg);
}
