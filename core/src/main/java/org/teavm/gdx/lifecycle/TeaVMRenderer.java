
package org.teavm.gdx.lifecycle;

import org.teavm.gdx.TeaVMApplication;
import org.teavm.gdx.input.ResettableInput;
import org.teavm.jso.JSBody;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.utils.Array;

/** Default {@link Renderer} implementation for TeaVM applications. Manages main loop. Note that it always reports 0 for every
 * {@link #getFrameId()} and {@link #getFramesPerSecond()}; if you want to keep track of rendering meta-data, use
 * {@link DebugTeaVMRenderer}.
 * @author MJ */
public class TeaVMRenderer implements Renderer {
	private final ApplicationListener listener;
	private final ResettableInput input;
	private final Array<Runnable> runnables = new Array<>();
	private final Array<Runnable> runnablesToInvoke = new Array<>();
	private float deltaTime;
	private double lastRender;
	private int timerId;
	private final AnimationFrame callback = this::loop;

	/** @param application will be rendered. Its {@link ApplicationListener} will be notified about lifecycle events. */
	public TeaVMRenderer (final TeaVMApplication application) {
		listener = application.getApplicationListener();
		input = application.getInput();
		addDatePolyfill();
		addAnimationPolyfill();
	}

	/** Ensures {@code Date.now()} is available. */
	@JSBody(params = {}, script = "if(!Date.now){Date.now=function now(){return new Date().getTime();};};")
	private static native void addDatePolyfill ();

	/** Ensures {@code requestAnimationFrame} and {@code cancelAnimationFrame} are available.
	 * @author Erik Moller, Paul Irish and Tino Zijdel */
	@JSBody(params = {}, script = "(function(){var lastTime=0;var vendors=['ms','moz','webkit','o'];for(var x=0;x<vendors.length&&!window.requestAnimationFrame;++x) {window.requestAnimationFrame = window[vendors[x]+'RequestAnimationFrame'];window.cancelAnimationFrame = window[vendors[x]+'CancelAnimationFrame']||window[vendors[x]+'CancelRequestAnimationFrame'];}if(!window.requestAnimationFrame)window.requestAnimationFrame=function(callback,element){var currTime=Date.now();var timeToCall=Math.max(0,16-(currTime-lastTime));var id=window.setTimeout(function(){callback(currTime + timeToCall);},timeToCall);lastTime=currTime+timeToCall;return id;};if(!window.cancelAnimationFrame)window.cancelAnimationFrame=function(id){clearTimeout(id);};}());")
	private static native void addAnimationPolyfill ();

	@Override
	public long getFrameId () {
		TeaVMApplication.logUnsupported("Frame ID without debug renderer");
		return 0L;
	}

	@Override
	public int getFramesPerSecond () {
		TeaVMApplication.logUnsupported("FPS without debug renderer");
		return 0;
	}

	@Override
	public float getDeltaTime () {
		return deltaTime;
	}

	@Override
	public void start () {
		lastRender = now();
		timerId = requestAnimationFrame(callback);
	}

	/** @return current date in milliseconds. */
	@JSBody(params = {}, script = "return Date.now();")
	protected static native double now ();

	@Override
	public void stop () {
		cancelAnimationFrame(timerId);
	}

	/** @param callback will be executed as the animation frame.
	 * @return ID of the request. */
	@JSBody(params = "callback", script = "return window.requestAnimationFrame(callback);")
	public static native int requestAnimationFrame (AnimationFrame callback);

	/** @param requestId ID of the request to be cancelled. */
	@JSBody(params = "requestId", script = "window.cancelAnimationFrame(requestId);")
	public static native void cancelAnimationFrame (int requestId);

	/** Main application's loop. */
	protected void loop () {
		final double now = now();
		deltaTime = (float)(now - lastRender) / 1000f;
		lastRender = now;
		if (runnables.size > 0) {
			runnablesToInvoke.addAll(runnables); // Prevents from clearing runnables scheduled by runnables.
			runnables.clear();
			for (final Runnable runnable : runnablesToInvoke) {
			runnable.run();
			}
			runnablesToInvoke.clear();
		}
		listener.render();
		input.reset();
		timerId = requestAnimationFrame(callback);
	}

	@Override
	public void postRunnable (final Runnable runnable) {
		runnables.add(runnable);
	}
}
