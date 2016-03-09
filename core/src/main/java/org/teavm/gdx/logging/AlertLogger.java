
package org.teavm.gdx.logging;

import org.teavm.jso.browser.Window;

/** Delegates all logging to the alert mechanism. Should be used only for debugging.
 *
 * @author MJ */
public class AlertLogger extends AbstractLogger {
	@Override
	public void log (final String tag, final String message) {
		Window.alert(tag + ": " + message);
	}

	@Override
	public void log (final String tag, final String message, final Throwable exception) {
		Window.alert(tag + ": " + message + " " + exception.getMessage());
	}
}
