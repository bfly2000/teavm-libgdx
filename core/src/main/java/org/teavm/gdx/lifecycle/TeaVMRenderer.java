
package org.teavm.gdx.lifecycle;

import org.teavm.jso.JSBody;
import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.utils.Array;

/** Default {@link Renderer} implementation for TeaVM applications. Manages main loop. Note that it always reports 0 for every
 * {@link #getFrameId()} and {@link #getFramesPerSecond()}; if you want to keep track of rendering meta-data, use
 * {@link DebugTeaVMRenderer}.
 * @author MJ */
public class TeaVMRenderer implements Renderer {
	private final ApplicationListener listener;
	private final Array<Runnable> runnables = new Array<>();
	private final Array<Runnable> runnablesToInvoke = new Array<>();
	private float deltaTime;
	private long lastRender;
	private int timerId;

	/** @param listener will be informed of rendering events. */
	public TeaVMRenderer (final ApplicationListener listener) {
		this.listener = listener;
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
		// TODO warning
		return 0L;
	}

	@Override
	public int getFramesPerSecond () {
		return 0;
	}

	@Override
	public float getDeltaTime () {
		return deltaTime;
	}

	@Override
	public void start () {
		lastRender = System.currentTimeMillis(); // TODO does it invoke Date.now()?
		// frameId = requestAnimationFrame();
		timerId = Window.setInterval(new TimerHandler() {
			@Override
			public void onTimer () {
			loop();
			}
		}, 17);
	}

	@Override
	public void stop () {
		Window.clearInterval(timerId);
		// cancelAnimationFrame(0L);
	}

	/** @param runnable will be executed as the animation frame. If it is the main loop runnable, it should recursively invoke
	 *           this method passing itself as the parameter.
	 * @return ID of the frame. */
	protected long requestAnimationFrame (final Runnable runnable) {
		// TODO implement requestAnimationFrame
		return 0L;
	}

	/** @param id frame with this ID will be cancelled. */
	protected void cancelAnimationFrame (final long id) {
		// TODO implement
	}

	/** Main application's loop. */
	protected void loop () {
		final long now = System.currentTimeMillis();
		deltaTime = (now - lastRender) / 1000f;
		lastRender = now;
		if (runnables.size > 0) {
			runnablesToInvoke.addAll(runnables);
			runnables.clear();
			for (final Runnable runnable : runnablesToInvoke) {
			runnable.run();
			}
			runnablesToInvoke.clear();
		}
		listener.render();
		// frameId = requestAnimationFrame();
	}

	@Override
	public void postRunnable (final Runnable runnable) {
		runnables.add(runnable);
	}
}
