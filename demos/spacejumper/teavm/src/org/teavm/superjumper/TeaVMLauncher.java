
package org.teavm.superjumper;

import org.teavm.gdx.TeaVMApplication;
import org.teavm.gdx.TeaVMApplicationConfiguration;

import com.badlogicgames.superjumper.SuperJumper;

/** Launches the TeaVM application. */
public class TeaVMLauncher {
	public static void main (final String... args) {
		final TeaVMApplicationConfiguration config = new TeaVMApplicationConfiguration();
		new TeaVMApplication(new SuperJumper(), config).start();
	}
}
