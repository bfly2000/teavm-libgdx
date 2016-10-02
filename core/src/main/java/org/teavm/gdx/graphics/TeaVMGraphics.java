
package org.teavm.gdx.graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import org.teavm.gdx.TeaVMApplication;
import org.teavm.gdx.TeaVMApplicationConfiguration;
import org.teavm.gdx.graphics.resizing.Resizer;
import org.teavm.gdx.graphics.webgl.TeaVMGL20;
import org.teavm.gdx.lifecycle.Renderer;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.jso.core.JSString;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.webgl.WebGLContextAttributes;
import org.teavm.jso.webgl.WebGLRenderingContext;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** Default implementation of {@link Graphics} for TeaVM applications. Wraps around a HTML canvas element and WebGL. Does not
 * support GL30. Allows to go to fullscreen mode only if chosen display mode matches current screen size. Allows to change cursors
 * and current page title. Always reports only one {@link Monitor} and {@link DisplayMode}. Does not support vSync settings.
 * @author MJ
 * @author Alexey Andreev */
public class TeaVMGraphics implements Graphics {
	/** Pixels per inch value. */
	public static final float PPI = 96f;
	public static final float DENSITY = PPI / 160f;
	/** Pixels per centimeters value. */
	public static final float PPC = PPI / 2.54f;
	/** Bits per pixel value for display mode. */
	public static final int BPP = 8;
	/** Refresh rate value for display mode. Roughly matches expected FPS. */
	public static final int REFRESH_RATE = 60;

	private final TeaVMApplicationConfiguration configuration;
	private final HTMLCanvasElement canvas;
	private final WebGLRenderingContext context;
	private final Renderer renderer;
	private final GL20 gl20;
	// Cache:
	private final Monitor monitor = new TeaVMMonitor(0, 0, TeaVMApplication.LOGGING_TAG);
	private final DisplayMode displayMode = new TeaVMDisplayMode(getScreenWidth(), getScreenHeight(), REFRESH_RATE, BPP);
	private GLVersion glVersion;
	private String extensions;
	private int oldWidth;
	private int oldHeight;

	public TeaVMGraphics (final TeaVMApplication application) {
		configuration = application.getConfiguration();
		canvas = application.getCanvas();
		renderer = application.getRenderer();
		oldWidth = canvas.getWidth();
		oldHeight = canvas.getHeight();
		context = (WebGLRenderingContext)canvas.getContext("webgl", getWebGlAttributes());
		context.viewport(0, 0, oldWidth, oldHeight);
		gl20 = createGL20(context);
		glVersion = createGlVersion(gl20);
		addFullscreenModeListener();
	}

	private GLVersion createGlVersion(GL20 gl20) {
		String versionString = gl20.glGetString(GL20.GL_VERSION);
		String vendorString = gl20.glGetString(GL20.GL_VENDOR);
		String rendererString = gl20.glGetString(GL20.GL_RENDERER);
		return new GLVersion(Application.ApplicationType.WebGL, versionString, vendorString, rendererString);
	}

	private void addFullscreenModeListener () {
		final EventListener<Event> fullscreenListener = event -> fullscreenChanged();
		final HTMLDocument document = HTMLDocument.current();
		document.addEventListener("fullscreenchange", fullscreenListener, false);
		document.addEventListener("webkitfullscreenchange", fullscreenListener, false);
		document.addEventListener("mozfullscreenchange", fullscreenListener, false);
		document.addEventListener("msfullscreenchange", fullscreenListener, false);
	}

	/** Sets {@link WebGLContextAttributes} according to {@link TeaVMApplicationConfiguration}.
	 * @return configuration object that should be passed to {@link HTMLCanvasElement#getContext(String, JSObject)} method. */
	protected JSObject getWebGlAttributes () {
		final WebGLContextAttributes attributes = WebGLContextAttributes.create();
		attributes.setAlpha(configuration.isAlphaEnabled());
		attributes.setAntialias(configuration.isAntialiasEnabled());
		attributes.setStencil(configuration.isStencilEnabled());
		attributes.setPremultipliedAlpha(configuration.isPremultipliedAlpha());
		attributes.setPreserveDrawingBuffer(configuration.isDrawingBufferPreserved());
		return attributes;
	}

	/** @param context current WebGL rendering context obtained from the canvas.
	 * @return a new instance of {@link GL20}. */
	protected GL20 createGL20 (final WebGLRenderingContext context) {
		return new TeaVMGL20(context);
	}

	/** @return current WebGL rendering context obtained from the canvas. */
	public WebGLRenderingContext getContext () {
		return context;
	}

	@Override
	public boolean isGL30Available () {
		return false;
	}

	@Override
	public GL30 getGL30 () {
		TeaVMApplication.logUnsupported("GL30");
		return null;
	}

	@Override
	public GL20 getGL20 () {
		return gl20;
	}

	@Override
	public int getWidth () {
		return canvas.getWidth();
	}

	@Override
	public int getHeight () {
		return canvas.getHeight();
	}

	@Override
	public int getBackBufferWidth () {
		return canvas.getWidth();
	}

	@Override
	public int getBackBufferHeight () {
		return canvas.getHeight();
	}

	/** @return width of the whole screen (in pixels) as reported by the browser. */
	public int getScreenWidth () {
		return Window.current().getScreen().getWidth();
	}

	/** @return height of the whole screen (in pixels) as reported by the browser. */
	public int getScreenHeight () {
		return Window.current().getScreen().getHeight();
	}

	@Override
	public long getFrameId () {
		return renderer.getFrameId();
	}

	@Override
	public float getDeltaTime () {
		return renderer.getDeltaTime();
	}

	@Override
	public float getRawDeltaTime () {
		return renderer.getDeltaTime();
	}

	@Override
	public int getFramesPerSecond () {
		return renderer.getFramesPerSecond();
	}

	@Override
	public GraphicsType getType () {
		return GraphicsType.WebGL;
	}

	@Override
	public GLVersion getGLVersion() {
		return glVersion;
	}

	@Override
	public float getPpiX () {
		return PPI;
	}

	@Override
	public float getPpiY () {
		return PPI;
	}

	@Override
	public float getPpcX () {
		return PPC;
	}

	@Override
	public float getPpcY () {
		return PPC;
	}

	@Override
	public float getDensity () {
		return DENSITY;
	}

	@Override
	public boolean supportsDisplayModeChange () {
		return isFullscreenModeSupported();
	}

	/** @return true if application can go to fullscreen mode. */
	@JSBody(params = {}, script = "return document.fullscreenEnabled||document.webkitFullscreenEnabled||document.mozFullScreenEnabled||document.msFullscreenEnabled||false;")
	protected static native boolean isFullscreenModeSupported ();

	@Override
	public Monitor getPrimaryMonitor () {
		return monitor;
	}

	@Override
	public Monitor getMonitor () {
		return monitor;
	}

	@Override
	public Monitor[] getMonitors () {
		return new Monitor[] {monitor};
	}

	@Override
	public DisplayMode[] getDisplayModes () {
		return new DisplayMode[] {displayMode};
	}

	@Override
	public DisplayMode[] getDisplayModes (final Monitor monitor) {
		return new DisplayMode[] {displayMode};
	}

	@Override
	public DisplayMode getDisplayMode () {
		return displayMode;
	}

	@Override
	public DisplayMode getDisplayMode (final Monitor monitor) {
		return displayMode;
	}

	@Override
	public void setTitle (final String title) {
		setDocumentTitle(title);
	}

	@Override
	public void setUndecorated(boolean undecorated) {
        TeaVMApplication.logUnsupported("Graphics#setUndecorated");
	}

	@Override
	public void setResizable(boolean resizable) {
        TeaVMApplication.logUnsupported("Graphics#setResizable");
	}

	/** @param newTitle will become the title of the current HTML document. */
	@JSBody(params = "newTitle", script = "document.title=newTitle")
	protected static native void setDocumentTitle (String newTitle);

	@Override
	public void setVSync (final boolean vsync) {
		TeaVMApplication.logUnsupported("vSync");
	}

	@Override
	public BufferFormat getBufferFormat () { // Mimics GWT backend.
		return new BufferFormat(8, 8, 8, 0, 16, configuration.isStencilEnabled() ? 8 : 0, 0, false);
	}

	@Override
	public boolean supportsExtension (final String extension) {
		final JSArrayReader<JSString> array = context.getSupportedExtensions();
		for (int i = 0; i < array.getLength(); ++i) {
			if (array.get(i).stringValue().equals(extension)) {
			return true;
			}
		}
		return false;
	}

	@Override
	public boolean isContinuousRendering () {
		return true;
	}

	@Override
	public void setContinuousRendering (final boolean isContinuous) {
		if (!isContinuous) {
			TeaVMApplication.logUnsupported("Graphics#setContinuousRendering(false)");
		}
	}

	@Override
	public void requestRendering () {
		TeaVMApplication.logUnsupported("Graphics#requestRendering");
	}

	@Override
	public boolean isFullscreen () {
		return isFullscreenModeOn();
	}

	/** @return true if application is currently in fullscreen mode. */
	@JSBody(params = {}, script = "return document.fullscreenElement!=null||document.msFullscreenElement!=null||document.webkitFullscreenElement!=null||document.mozFullScreenElement!=null||document.webkitIsFullScreen||document.mozFullScreen||false;")
	protected static native boolean isFullscreenModeOn ();

	@Override
	public boolean setFullscreenMode (final DisplayMode displayMode) {
		if (this.displayMode.equals(displayMode)) {
			return enterFullscreen();
		}
		return false;
	}

	/** Attempts to enter fullscreen mode.
	 * @return true if succeeded. */
	public boolean enterFullscreen () {
		if (isFullscreenModeSupported()) {
			oldWidth = canvas.getWidth();
			oldHeight = canvas.getHeight();
			final int screenWidth = getScreenWidth(), screenHeight = getScreenHeight();
			canvas.setWidth(screenWidth);
			canvas.setHeight(screenHeight);
			addResizeEvent(screenWidth, screenHeight);
			switchToFullscreen(canvas);
			return true;
		}
		return false;
	}

	/** @param element will request switching to fullscreen mode. */
	@JSBody(params = "element", script = "if(element.requestFullscreen){element.requestFullscreen();}else if(element.webkitRequestFullScreen){element.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);}else if(element.mozRequestFullScreen){element.mozRequestFullScreen();}else if(element.msRequestFullscreen){element.msRequestFullscreen();}")
	protected static native void switchToFullscreen (HTMLElement element);

	/** Will post an event which will resize the game during the next render call. Width and height have to match current
	 * application size.
	 * @param width current application width.
	 * @param height current application height. */
	protected void addResizeEvent (final int width, final int height) {
		// Change to Gdx.app.getApplicationListener().resize(width, height); for immediate resize.
		Gdx.app.postRunnable(new Resizer(width, height));
	}

	/** Should be invoked each time the application exits fullscreen mode. */
	public void fullscreenChanged () {
		if (isFullscreen()) {
			lockOrientation();
		} else {
			if (canvas.getWidth() != oldWidth || canvas.getHeight() != oldHeight) {
			canvas.setWidth(oldWidth);
			canvas.setHeight(oldHeight);
			addResizeEvent(oldWidth, oldHeight);
			}
			unlockFullscreenOrientation();
		}
	}

	/** Attempts to lock orientation according to application's configuration object. Should be executed after entering fullscreen
	 * mode. */
	public void lockOrientation () {
		final OrientationLockType orientation = configuration.getFullscreenOrientation();
		if (orientation != null) {
			lockOrientation(orientation.getName());
		}
	}

	/** @param orientationType name of the type of orientation to lock. */
	@JSBody(params = "orientationType", script = "var lock=screen.lockOrientation||screen.mozLockOrientation||screen.msLockOrientation||screen.webkitLockOrientation;if(lock){lock(orientationType);}else if(screen.orientation&&screen.orientation.lock){screen.orientation.lock(orientationType);}")
	protected static native void lockOrientation (String orientationType);

	/** Attempts to unlock orientation. Should be executed during exiting from fullscreen mode. */
	public void unlockFullscreenOrientation () {
		if (configuration.getFullscreenOrientation() != null) {
			unlockOrientation();
		}
	}

	/** Attempts to unlock orientation (if set). */
	@JSBody(params = {}, script = "var unlock=screen.unlockOrientation||screen.mozUnlockOrientation||screen.msUnlockOrientation||screen.webkitUnlockOrientation;if(unlock){unlock();}else if(screen.orientation&&screen.orientation.unlock){screen.orientation.unlock();}")
	protected static native void unlockOrientation ();

	@Override
	public boolean setWindowedMode (final int width, final int height) {
		if (isFullscreen()) {
			exitFullscreen();
		}
		canvas.setWidth(width);
		canvas.setHeight(height);
		oldWidth = width;
		oldHeight = height;
		addResizeEvent(width, height);
		return true;
	}

	/** Attempts to exit full screen mode. Should be a no-op if application is currently in windowed mode. */
	@JSBody(params = {}, script = "if(document.exitFullscreen)document.exitFullscreen();if(document.msExitFullscreen)document.msExitFullscreen();if(document.webkitExitFullscreen)document.webkitExitFullscreen();if(document.mozExitFullscreen)document.mozExitFullscreen();if(document.webkitCancelFullScreen)document.webkitCancelFullScreen();")
	public static native void exitFullscreen ();

	@Override
	public Cursor newCursor (final Pixmap pixmap, final int xHotspot, final int yHotspot) {
		return new TeaVMCursor(pixmap, xHotspot, yHotspot);
	}

	@Override
	public void setCursor (final Cursor cursor) {
		if (cursor instanceof TeaVMCursor) {
			setCursor(((TeaVMCursor)cursor).getCss());
		} else {
			throw new GdxRuntimeException("Do not create Cursor instances manually, use Graphics#newCursor instead.");
		}
	}

	@Override
	public void setSystemCursor (final SystemCursor systemCursor) {
		setCursor(TeaVMCursor.getNameForSystemCursor(systemCursor));
	}

	/** @param css will be set in canvas style attribute as "cursor". */
	protected void setCursor (final String css) {
		canvas.getStyle().setProperty("cursor", css);
	}

	/** Allows to support orientation lock during fullscreen mode.
	 * @author MJ */
	public static interface OrientationLockType {
		/** @return actual name of the orientation. */
		String getName ();
	}

	/** Enum values from http://www.w3.org/TR/screen-orientation. Filtered based on what the browsers actually support. */
	public static enum CommonOrientationLockType implements OrientationLockType { // Based on GWT backend.
		LANDSCAPE("landscape"), PORTRAIT("portrait"), PORTRAIT_PRIMARY("portrait-primary"), PORTRAIT_SECONDARY(
			"portrait-secondary"), LANDSCAPE_PRIMARY("landscape-primary"), LANDSCAPE_SECONDARY("landscape-secondary");

		private final String name;

		private CommonOrientationLockType (final String name) {
			this.name = name;
		}

		@Override
		public String getName () {
			return name;
		}
	}

	/** Allows to create an instance of {@link DisplayMode}, which has a protected constructor.
	 *
	 * @author MJ */ // ...for some unknown reason.
	protected static class TeaVMDisplayMode extends DisplayMode {
		public TeaVMDisplayMode (final int width, final int height, final int refreshRate, final int bitsPerPixel) {
			super(width, height, refreshRate, bitsPerPixel);
		}

		@Override
		public boolean equals (final Object object) {
			if (object == this) {
			return true;
			} else if (object instanceof DisplayMode) {
			final DisplayMode other = (DisplayMode)object;
			return width == other.width && height == other.height && refreshRate == other.refreshRate
				&& bitsPerPixel == other.bitsPerPixel;
			}
			return false;
		}

		@Override
		public int hashCode () {
			return width + 13 * height + 53 * refreshRate + 163 * bitsPerPixel;
		}
	}

	/** Allows to create an instance of {@link Monitor}, which has a protected constructor.
	 *
	 * @author MJ */ // ...for some unknown reason.
	protected static class TeaVMMonitor extends Monitor {
		public TeaVMMonitor (final int virtualX, final int virtualY, final String name) {
			super(virtualX, virtualY, name);
		}
	}
}
