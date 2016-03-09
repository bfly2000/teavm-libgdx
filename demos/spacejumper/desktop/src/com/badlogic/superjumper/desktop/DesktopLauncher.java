
package com.badlogic.superjumper.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogicgames.superjumper.SuperJumper;

public class DesktopLauncher {
	public static void main (final String[] arg) {
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Super Jumper";
		config.width = 480;
		config.height = 800;
		createApplication(config);
	}

	private static Application createApplication (final LwjglApplicationConfiguration config) {
		return new LwjglApplication(new SuperJumper(), config);
	}
}
