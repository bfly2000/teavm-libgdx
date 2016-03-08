
package org.teavm.gdx;

import org.teavm.gdx.audio.TeaVMAudio;
import org.teavm.gdx.clipboard.MockUpClipboard;
import org.teavm.gdx.clipboard.TeaVMClipboard;
import org.teavm.gdx.files.TeaVMFileLoader;
import org.teavm.gdx.files.TeaVMFilePreloadListener;
import org.teavm.gdx.files.TeaVMFiles;
import org.teavm.gdx.graphics.DebugTeaVMGraphics;
import org.teavm.gdx.graphics.TeaVMGraphics;
import org.teavm.gdx.graphics.resizing.ResizeListener;
import org.teavm.gdx.input.TeaVMInput;
import org.teavm.gdx.lifecycle.DebugTeaVMRenderer;
import org.teavm.gdx.lifecycle.LifecycleManager;
import org.teavm.gdx.lifecycle.Renderer;
import org.teavm.gdx.lifecycle.TeaVMRenderer;
import org.teavm.gdx.logging.ConsoleLogger;
import org.teavm.gdx.logging.TeaVMLogger;
import org.teavm.gdx.net.TeaVMNet;
import org.teavm.gdx.preferences.PreferencesResolver;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;

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
import com.badlogic.gdx.utils.GdxRuntimeException;

/** Default base implementation of LibGDX {@link Application}. Extend this class and override any of the create(...) methods to
 * modify the behavior of the application. Use {@link TeaVMApplicationConfiguration} for managing of basic settings. Pass your
 * {@link ApplicationListener} instance in the constructor.
 * <p>
 * Settings to consider: {@link #createClipboard()} returns {@link TeaVMClipboard} which might not be supported by all browsers.
 * While it fallbacks to the same functionality as {@link MockUpClipboard}, it is generally safer to use the second one.
 * {@link #createRenderer()} returns {@link TeaVMRenderer}, which does not keep rendering meta-data, like current frame ID or FPS.
 * Use {@link DebugTeaVMRenderer} instead if you need these. {@link #createLogger()} returns {@link ConsoleLogger} that delegates
 * all logging to the default JS console. See {@link TeaVMLogger} docs for more provided options.
 * @author MJ */
public class TeaVMApplication implements Application {
	/** Used by TeaVM-specific classes to report informations, warnings and errors. */
	public static final String LOGGING_TAG = "TeaVM";

	// LibGDX core classes:
	private final ApplicationListener applicationListener;
	private final Net net;
	private final Audio audio;
	private final Files files;
	private final Input input;
	private final Graphics graphics;
	// Helpers:
	private final HTMLCanvasElement canvas;
	private final TeaVMApplicationConfiguration configuration;
	private final PreferencesResolver preferencesResolver;
	private final LifecycleManager lifecycleManager;
	private final TeaVMLogger logger;
	private final Clipboard clipboard;
	private final Renderer renderer;

	/** @param applicationListener handles application events. Uses default configuration. */
	public TeaVMApplication (final ApplicationListener applicationListener) {
		this(applicationListener, new TeaVMApplicationConfiguration());
	}

	/** @param applicationListener handles application events.
	 * @param configuration configures TeaVM application. */
	public TeaVMApplication (final ApplicationListener applicationListener, final TeaVMApplicationConfiguration configuration) {
		this.applicationListener = applicationListener;
		this.configuration = configuration;

		logger = createLogger();
		canvas = (HTMLCanvasElement)HTMLDocument.current().getElementById(configuration.getCanvasId());
		preferencesResolver = createPreferencesResolver();
		lifecycleManager = createLifecycleManager();
		clipboard = createClipboard();
		renderer = createRenderer();

		net = createNet();
		audio = createAudio();
		files = createFiles();
		input = createInput();
		graphics = createGraphics();
	}

	@Override
	public ApplicationListener getApplicationListener () {
		return applicationListener;
	}

	/** @return a new instance of logger object that prints logs received by the application instance. By default, logs are
	 *         delegated to the console.
	 * @see TeaVMLogger */
	protected TeaVMLogger createLogger () {
		return new ConsoleLogger();
	}

	/** @return a new instance of renderer which manages application's main loop. Note that default TeaVM renderer does not keep
	 *         track of current frame ID and FPS value; use {@link DebugTeaVMRenderer} if you need such data.
	 * @see TeaVMRenderer
	 * @see DebugTeaVMRenderer */
	protected Renderer createRenderer () {
		return new TeaVMRenderer(applicationListener);
	}

	/** @return a new instance of manager that maintains {@link LifecycleListener}s and is notified about application lifecycle
	 *         events. */
	protected LifecycleManager createLifecycleManager () {
		return new LifecycleManager();
	}

	/** @return a new instance of manager that creates and maintains {@link Preferences} instances. Default implementation uses
	 *         local storage when possible and mock up preferences (never saved, unavailable in next sessions) when necessary. */
	protected PreferencesResolver createPreferencesResolver () {
		return new PreferencesResolver();
	}

	/** @return a new instance of clipboard manager. Allows to copy and paste string values.
	 * @see TeaVMClipboard
	 * @see MockUpClipboard */
	protected Clipboard createClipboard () {
		return new TeaVMClipboard();
	}

	/** @return a new instance of {@link Net} implementation, handling application's networking.
	 * @see TeaVMNet */
	protected Net createNet () {
		return new TeaVMNet();
	}

	/** @return a new instance of {@link Audio} implementation, handling application's sound support.
	 * @see TeaVMAudio */
	protected Audio createAudio () {
		return new TeaVMAudio();
	}

	/** @return a new instance of {@link Files} implementation, handling application's files support.
	 * @see TeaVMFiles */
	protected Files createFiles () {
		return new TeaVMFiles();
	}

	/** @return a new instance of {@link Graphics} implementation, handling application's graphics.
	 * @see TeaVMGraphics
	 * @see DebugTeaVMGraphics */
	protected Graphics createGraphics () {
		return new TeaVMGraphics(this);
	}

	/** @return a new instance of {@link Input} implementation, handling user's input.
	 * @see TeaVMInput */
	protected Input createInput () {
		return new TeaVMInput(this);
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
		return net;
	}

	@Override
	public void log (final String tag, final String message) {
		logger.log(LOG_INFO, tag, message);
	}

	@Override
	public void log (final String tag, final String message, final Throwable exception) {
		logger.log(LOG_INFO, tag, message, exception);
	}

	@Override
	public void error (final String tag, final String message) {
		logger.log(LOG_ERROR, tag, message);
	}

	@Override
	public void error (final String tag, final String message, final Throwable exception) {
		logger.log(LOG_ERROR, tag, message, exception);
	}

	@Override
	public void debug (final String tag, final String message) {
		logger.log(LOG_DEBUG, tag, message);
	}

	@Override
	public void debug (final String tag, final String message, final Throwable exception) {
		logger.log(LOG_DEBUG, tag, message, exception);
	}

	@Override
	public void setLogLevel (final int logLevel) {
		logger.setLevel(logLevel);
	}

	@Override
	public int getLogLevel () {
		return logger.getLevel();
	}

	@Override
	public ApplicationType getType () {
		return ApplicationType.WebGL;
	}

	@Override
	public int getVersion () {
		logUnsupported("Application#getVersion");
		return 0;
	}

	@Override
	public long getJavaHeap () {
		logUnsupported("Application#getJavaHeap");
		return 0;
	}

	@Override
	public long getNativeHeap () {
		logUnsupported("Application#getNativeHeap");
		return 0;
	}

	@Override
	public Preferences getPreferences (final String name) {
		return preferencesResolver.getPreferences(name);
	}

	@Override
	public Clipboard getClipboard () {
		return clipboard;
	}

	@Override
	public void postRunnable (final Runnable runnable) {
		renderer.postRunnable(runnable);
	}

	@Override
	public void addLifecycleListener (final LifecycleListener listener) {
		lifecycleManager.addListener(listener);
	}

	@Override
	public void removeLifecycleListener (final LifecycleListener listener) {
		lifecycleManager.removeListener(listener);
	}

	/** @return lifecycle manager that should be notified about every lifecycle event (pause, resume, dispose). */
	public LifecycleManager getLifecycleManager () {
		return lifecycleManager;
	}

	/** @return configuration object used to initiate the application. Modifying most settings after application is already
	 *         initiated does nothing. */
	public TeaVMApplicationConfiguration getConfiguration () {
		return configuration;
	}

	/** @return a renderer implementation, handling application's main loop. */
	public Renderer getRenderer () {
		return renderer;
	}

	/** @return application's main canvas used to render graphics. */
	public HTMLCanvasElement getCanvas () {
		return canvas;
	}

	@Override
	public void exit () {
		renderer.stop();
		lifecycleManager.dispose();
	}

	/** Assigns LibGDX static variables. Preloads application's assets. Invokes {@link ApplicationListener#create()} and then
	 * {@link ApplicationListener#resize(int, int)}. Starts the application shortly after. */
	public void start () {
		assignGdxStatics();
		TeaVMFileLoader.loadFiles(new TeaVMFilePreloadListener() {
			@Override
			public void error () {
			Gdx.app.error(LOGGING_TAG, "Fatal: unable to preload assets.");
			throw new GdxRuntimeException("Unable to preload assets.");
			}

			@Override
			public void complete () {
			startLibGdxApplication();
			}
		});
		assignGdxStatics();
	}

	/** Override this method to customize how {@link Gdx} static fields are initialized. */
	protected void assignGdxStatics () {
		Gdx.app = this;
		Gdx.net = net;
		Gdx.audio = audio;
		Gdx.files = files;
		Gdx.input = input;
		Gdx.graphics = graphics;
		if (configuration.isUsingGl30() && graphics.isGL30Available()) { // Should never happen, but we allow
			Gdx.gl = Gdx.gl20 = Gdx.gl30 = graphics.getGL30(); // the users to simulate/implement GL30.
		} else {
			Gdx.gl = Gdx.gl20 = graphics.getGL20();
		}
	}

	/** Invoked after assets preloading. Does the actual application initiation. */
	protected void startLibGdxApplication () {
		applicationListener.create();
		applyResizing();
		renderer.start();
	}

	/** Resizes current {@link ApplicationListener}. Adds a resize event listener. */
	protected void applyResizing () {
		final int width = graphics.getWidth();
		final int height = graphics.getHeight();
		applicationListener.resize(width, height);
		Window.current().addEventListener("resize", new ResizeListener(width, height));
	}

	/** @param unsupportedFunctionality beginning of the sentence "(...) is unsupported.". Will be logged by the current logger as
	 *           error log with {@link #LOGGING_TAG}. Should be used to log all potentially crucial unsupported functionalities
	 *           that do not have to throw an exception when used, but should nonetheless notify the user. */
	public static void logUnsupported (final String unsupportedFunctionality) {
		Gdx.app.error(LOGGING_TAG, unsupportedFunctionality + " is unsupported.");
	}
}
