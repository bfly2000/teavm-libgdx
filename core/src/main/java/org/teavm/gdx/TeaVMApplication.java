
package org.teavm.gdx;

import java.util.ArrayList;
import java.util.List;

import org.teavm.gdx.audio.TeaVMAudio;
import org.teavm.gdx.files.TeaVMFileLoader;
import org.teavm.gdx.files.TeaVMFilePreloadListener;
import org.teavm.gdx.files.TeaVMFiles;
import org.teavm.gdx.graphics.TeaVMGraphics;
import org.teavm.gdx.input.TeaVMInput;
import org.teavm.jso.JSBody;
import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLCanvasElement;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;

public class TeaVMApplication implements Application {
	private final ApplicationListener listener;
	private final TeaVMApplicationConfiguration config;
	private HTMLCanvasElement canvas;
	private TeaVMGraphics graphics;
	private TeaVMFiles files;
	private TeaVMAudio audio;
	private TeaVMInput input;
	private int logLevel = LOG_ERROR;
	private final List<LifecycleListener> lifecycleListeners = new ArrayList<>();

	public TeaVMApplication (final ApplicationListener listener, final TeaVMApplicationConfiguration config) {
		this.listener = listener;
		this.config = config;
	}

	public void start () {
		TeaVMFileLoader.loadFiles(new TeaVMFilePreloadListener() {
			@Override
			public void error () {
			}

			@Override
			public void complete () {
			startGdx();
			}
		});
	}

	private void startGdx () {
		canvas = config.getCanvas();
		graphics = new TeaVMGraphics(canvas, config);
		files = new TeaVMFiles();
		audio = new TeaVMAudio();
		input = new TeaVMInput(canvas);
		Gdx.app = this;
		Gdx.graphics = graphics;
		Gdx.gl = graphics.getGL20();
		Gdx.gl20 = graphics.getGL20();
		Gdx.files = files;
		Gdx.audio = audio;
		Gdx.input = input;
		listener.create();
		listener.resize(canvas.getWidth(), canvas.getHeight());
		delayedStep();
	}

	private void delayedStep () {
		Window.setTimeout(new TimerHandler() {
			@Override
			public void onTimer () {
			step();
			}
		}, 10);
	}

	private void step () {
		graphics.update();
		graphics.frameId++;
		listener.resize(canvas.getWidth(), canvas.getHeight());
		listener.render();
		input.reset();
		delayedStep();
	}

	@Override
	public ApplicationListener getApplicationListener () {
		return listener;
	}

	@Override
	public Graphics getGraphics () {
		return graphics;
	}

	@Override
	public Audio getAudio () {
		return audio;
	}

	@Override
	public Input getInput () {
		return input;
	}

	@Override
	public Files getFiles () {
		return files;
	}

	@Override
	public Net getNet () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void log (final String tag, final String message) {
		if (logLevel > LOG_INFO) {
			consoleLog("Info " + tag + ": " + message);
		}
	}

	@Override
	public void log (final String tag, final String message, final Throwable exception) {
		if (logLevel > LOG_INFO) {
			consoleLog("Info " + tag + ": " + message);
		}
	}

	@Override
	public void error (final String tag, final String message) {
		if (logLevel > LOG_ERROR) {
			consoleLog("Error " + tag + ": " + message);
		}
	}

	@Override
	public void error (final String tag, final String message, final Throwable exception) {
		if (logLevel > LOG_ERROR) {
			consoleLog("Error " + tag + ": " + message);
		}
	}

	@Override
	public void debug (final String tag, final String message) {
		if (logLevel >= LOG_DEBUG) {
			consoleLog("Debug " + tag + ": " + message);
		}
	}

	@Override
	public void debug (final String tag, final String message, final Throwable exception) {
		if (logLevel > LOG_DEBUG) {
			consoleLog("Debug " + tag + ": " + message);
		}
	}

	@Override
	public void setLogLevel (final int logLevel) {
		this.logLevel = logLevel;
	}

	@Override
	public int getLogLevel () {
		return logLevel;
	}

	@Override
	public ApplicationType getType () {
		return ApplicationType.WebGL;
	}

	@Override
	public int getVersion () {
		return 0;
	}

	@Override
	public long getJavaHeap () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNativeHeap () {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Preferences getPreferences (final String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Clipboard getClipboard () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void postRunnable (final Runnable runnable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit () {
	}

	@Override
	public void addLifecycleListener (final LifecycleListener listener) {
		lifecycleListeners.add(listener);
	}

	@Override
	public void removeLifecycleListener (final LifecycleListener listener) {
		lifecycleListeners.remove(listener);
	}

	@JSBody(params = "message", script = "console.log(\"TeaVM: \" + message);")
	native static public void consoleLog (String message);
}