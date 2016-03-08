
package org.teavm.gdx.logging;

import com.badlogic.gdx.Application;

/** Quiet logger implementation. Ignores all logs.
 *
 * @author MJ */
public class MockUpLogger implements TeaVMLogger {
	@Override
	public int getLevel () {
		return Application.LOG_NONE;
	}

	@Override
	public void setLevel (final int level) {
	}

	@Override
	public void log (final int level, final String tag, final String message) {
	}

	@Override
	public void log (final int level, final String tag, final String message, final Throwable exception) {
	}
}
