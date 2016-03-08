
package org.teavm.gdx.logging;

import org.teavm.jso.JSBody;

/** Default TeaVM logger. Delegates logs to the console.
 *
 * @author MJ */
public class ConsoleLogger extends AbstractLogger {
	@Override
	public void log (final String tag, final String message) {
		consoleLog(tag, message);
	}

	@Override
	public void log (final String tag, final String message, final Throwable exception) {
		consoleLog(tag, message, exception);
	}

	/** Prints passed data to the console.
	 *
	 * @param tag logging tag.
	 * @param msg message to log. */
	@JSBody(params = {"tag", "msg"}, script = "console.log(tag, msg);")
	public static native void consoleLog (String tag, String msg);

	/** Prints passed data to the console.
	 *
	 * @param tag logging tag.
	 * @param msg message to log.
	 * @param ex cause of the log. */
	@JSBody(params = {"tag", "msg", "ex"}, script = "console.log(tag, msg, ex);")
	public static native void consoleLog (String tag, String msg, Throwable ex);
}
