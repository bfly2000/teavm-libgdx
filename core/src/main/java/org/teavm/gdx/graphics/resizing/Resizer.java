
package org.teavm.gdx.graphics.resizing;

import com.badlogic.gdx.Gdx;

/** Resizes current application listener if contains valid data.
 * @author MJ */ // Note that this should not be cached and reused, as resizing events are relatively rare and it is possible to
// get multiple calls during one render cycle, which would likely lead to the same object being scheduled multiple times.
public class Resizer implements Runnable {
	private final int width;
	private final int height;

	public Resizer (final int width, final int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void run () {
		if (Gdx.graphics.getWidth() == width && Gdx.graphics.getHeight() == height) {
			Gdx.app.getApplicationListener().resize(width, height);
			Gdx.gl.glViewport(0, 0, width, height);
		}
	}
}
