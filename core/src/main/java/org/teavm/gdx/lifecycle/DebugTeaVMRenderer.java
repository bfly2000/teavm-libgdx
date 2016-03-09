
package org.teavm.gdx.lifecycle;

import org.teavm.gdx.TeaVMApplication;

import com.badlogic.gdx.ApplicationListener;

/** Additionally to managing application's main loop, this renderer keeps track of current frame ID and FPS value.
 * @author MJ */
public class DebugTeaVMRenderer extends TeaVMRenderer {
	private long frameId;
	private float timePassed;
	private int frames;
	private int fps;

	/** @param application will be rendered. Its {@link ApplicationListener} will be notified about lifecycle events. */
	public DebugTeaVMRenderer (final TeaVMApplication application) {
		super(application);
	}

	@Override
	public long getFrameId () {
		return frameId;
	}

	@Override
	public int getFramesPerSecond () {
		return fps;
	}

	@Override
	protected void loop () {
		super.loop();
		frameId++;
		frames++;
		timePassed += getDeltaTime();
		while (timePassed >= 1f) {
			timePassed -= 1f;
			fps = frames;
			frames = 0;
		}
	}
}
