
package org.teavm.gdx.input;

import org.teavm.gdx.TeaVMApplication;
import org.teavm.jso.JSBody;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.KeyboardEvent;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntSet;

/** Handles {@link Input} events in the TeaVM application. Supports only keyboard and touch events.
 * @author Alexey Andreev
 * @author MJ */
public class TeaVMInput implements ResettableInput, EventListener<Event> {
	private static final int MAX_TOUCHES = 20;
	private boolean justTouched = false;
	private final boolean[] touched = new boolean[MAX_TOUCHES];
	private final int[] touchX = new int[MAX_TOUCHES];
	private final int[] touchY = new int[MAX_TOUCHES];
	private final int[] deltaX = new int[MAX_TOUCHES];
	private final int[] deltaY = new int[MAX_TOUCHES];
	private final IntSet pressedButtons = new IntSet();
	private int pressedKeyCount = 0;
	private final boolean[] pressedKeys = new boolean[256];
	private boolean keyJustPressed = false;
	private final boolean[] justPressedKeys = new boolean[256];
	private char lastKeyCharPressed;
	private float keyRepeatTimer;
	private long currentEventTimeStamp;
	private final HTMLCanvasElement canvas;
	private boolean hasFocus = true;

	private InputProcessor processor;

	public TeaVMInput (final TeaVMApplication application) {
		canvas = application.getCanvas();
		hookEvents();
	}

	/** Adds event listeners to current document and canvas. */
	protected void hookEvents () {
		final HTMLDocument document = canvas.getOwnerDocument();
		canvas.addEventListener("mousedown", this, true);
		document.addEventListener("mousedown", this, true);
		canvas.addEventListener("mouseup", this, true);
		document.addEventListener("mouseup", this, true);
		canvas.addEventListener("mousemove", this, true);
		document.addEventListener("mousemove", this, true);
		canvas.addEventListener("mousewheel", this, true);
		document.addEventListener("keydown", this, false);
		document.addEventListener("keyup", this, false);
		document.addEventListener("keypress", this, false);

		canvas.addEventListener("touchstart", this);
		canvas.addEventListener("touchmove", this);
		canvas.addEventListener("touchcancel", this);
		canvas.addEventListener("touchend", this);
	}

	@Override
	public void setInputProcessor (final InputProcessor processor) {
		this.processor = processor;
	}

	@Override
	public InputProcessor getInputProcessor () {
		return processor;
	}

	@Override
	public void reset () {
		justTouched = false;
		if (keyJustPressed) {
			keyJustPressed = false;
			for (int i = 0; i < justPressedKeys.length; i++) {
			justPressedKeys[i] = false;
			}
		}
	}

	@Override
	public int getX () {
		return touchX[0];
	}

	@Override
	public int getX (final int pointer) {
		return touchX[pointer];
	}

	@Override
	public int getDeltaX () {
		return deltaX[0];
	}

	@Override
	public int getDeltaX (final int pointer) {
		return deltaX[pointer];
	}

	@Override
	public int getY () {
		return touchY[0];
	}

	@Override
	public int getY (final int pointer) {
		return touchY[pointer];
	}

	@Override
	public int getDeltaY () {
		return deltaY[0];
	}

	@Override
	public int getDeltaY (final int pointer) {
		return deltaY[pointer];
	}

	@Override
	public boolean isTouched () {
		for (int pointer = 0; pointer < MAX_TOUCHES; pointer++) {
			if (touched[pointer]) {
			return true;
			}
		}
		return false;
	}

	@Override
	public boolean justTouched () {
		return justTouched;
	}

	@Override
	public boolean isTouched (final int pointer) {
		return touched[pointer];
	}

	@Override
	public boolean isButtonPressed (final int button) {
		return button == Buttons.LEFT && touched[0];
	}

	@Override
	public boolean isKeyPressed (final int key) {
		if (key == Keys.ANY_KEY) {
			return pressedKeyCount > 0;
		}
		if (key < 0 || key > 255) {
			return false;
		}
		return pressedKeys[key];
	}

	@Override
	public boolean isKeyJustPressed (final int key) {
		if (key == Keys.ANY_KEY) {
			return keyJustPressed;
		}
		if (key < 0 || key > 255) {
			return false;
		}
		return justPressedKeys[key];
	}

	@Override
	public long getCurrentEventTime () {
		return currentEventTimeStamp;
	}

	@Override
	public boolean isPeripheralAvailable (final Peripheral peripheral) {
		return peripheral == Peripheral.HardwareKeyboard || peripheral == Peripheral.MultitouchScreen && isTouchScreen();
	}

	@Override
	public Orientation getNativeOrientation () {
		return Orientation.Landscape;
	}

	/** @param event native event.
	 * @return movement X extracted from the event. */
	@JSBody(params = "event", script = "return event.movementX||event.webkitMovementX||0;")
	protected static native float getMovementX (final Event event);

	/** @param event native event.
	 * @return movement Y extracted from the event. */
	@JSBody(params = "event", script = "return event.movementY||event.webkitMovementY||0;")
	protected static native float getMovementY (final Event event);

	/** @return true if browser supports touch screen. */
	@JSBody(params = {}, script = "return (('ontouchstart' in window)||(navigator.msMaxTouchPoints > 0));")
	public static native boolean isTouchScreen ();

	/** Works only for Chrome (version 18+) with enabled Mouse Lock in about:flags or when started with the --enable-pointer-lock
	 * flag.
	 * @param catched attempts to catch or release the cursor. */
	@Override
	public void setCursorCatched (final boolean catched) {
		if (catched) {
			catchCursor(canvas);
		} else {
			releaseCursor();
		}
	}

	/** @param element attempts to catch cursor with this element. */
	@JSBody(params = "element", script = "if(!navigator.pointer){navigator.pointer=navigator.webkitPointer||navigator.mozPointer;}"
		+ "if(!element.requestPointerLock){element.requestPointerLock=(function(){return element.webkitRequestPointerLock||element.mozRequestPointerLock||function(){if(navigator.pointer){navigator.pointer.lock(element);}};})();}"
		+ "element.requestPointerLock();")
	protected static native void catchCursor (HTMLElement element);

	/** Attempts to release the cursor. */
	@JSBody(params = {}, script = "if(!document.exitPointerLock){document.exitPointerLock=(function(){return document.webkitExitPointerLock||document.mozExitPointerLock||function(){if(navigator.pointer){var elem = this;navigator.pointer.unlock();}};})();}"
		+ "document.exitPointerLock();")
	protected static native void releaseCursor ();

	@Override
	public boolean isCursorCatched () {
		return isCursorCurrentlyCatched();
	}

	/** @return true if cursor is cached. False if not or if unable to determine. */
	@JSBody(params = {}, script = "if(!navigator.pointer){navigator.pointer=navigator.webkitPointer||navigator.mozPointer;}"
		+ "if(navigator.pointer){if(typeof (navigator.pointer.isLocked)==='boolean'){return navigator.pointer.isLocked;}else if(typeof (navigator.pointer.isLocked)==='function'){return navigator.pointer.isLocked();} else if(typeof (navigator.pointer.islocked)==='function'){return navigator.pointer.islocked();}}return false;")
	protected static native boolean isCursorCurrentlyCatched ();

	@Override
	public void setCursorPosition (final int x, final int y) {
		TeaVMApplication.logUnsupported("Input#setCursorPosition");
	}

	private static float getMouseWheelVelocity (final Event evt) {
		// TODO Implement. Highly dependant on the browser.
		return 0;
	}

	/** @return name of the mouse wheel event. */
	@JSBody(params = {}, script = "return navigator.userAgent.toLowerCase().indexOf('firefox')!=-1?'DOMMouseScroll':'mousewheel';")
	protected static native String getMouseWheelEvent ();

	// Kindly borrowed from PlayN.
	/** @param event native event.
	 * @return relative X. */
	protected int getRelativeX (final MouseEvent event) {
		final float xScaleRatio = canvas.getWidth() * 1f / canvas.getClientWidth();
		return Math.round(xScaleRatio * (event.getClientX() - canvas.getAbsoluteLeft() + canvas.getScrollLeft()
			+ canvas.getOwnerDocument().getScrollLeft()));
	}

	/// Kindly borrowed from PlayN.
	/** @param event native event.
	 * @return relative Y. */
	protected int getRelativeY (final MouseEvent event) {
		final float yScaleRatio = canvas.getHeight() * 1f / canvas.getClientHeight();
		return Math.round(yScaleRatio
			* (event.getClientY() - canvas.getAbsoluteTop() + canvas.getScrollTop() + canvas.getOwnerDocument().getScrollTop()));
	}

	private static int getButton (final int button) {
		if (button == MouseEvent.LEFT_BUTTON) {
			return Buttons.LEFT;
		} else if (button == MouseEvent.RIGHT_BUTTON) {
			return Buttons.RIGHT;
		} else if (button == MouseEvent.MIDDLE_BUTTON) {
			return Buttons.MIDDLE;
		}
		return Buttons.LEFT;
	}

	@Override
	public void handleEvent (final Event e) {
		// TODO Separate into multiple event handlers.
		if (e.getType().equals("mousedown")) {
			final MouseEvent mouseEvent = (MouseEvent)e;
			if (e.getTarget() != canvas || touched[0]) {
			final float mouseX = getRelativeX(mouseEvent);
			final float mouseY = getRelativeY(mouseEvent);
			if (mouseX < 0 || mouseX > Gdx.graphics.getWidth() || mouseY < 0 || mouseY > Gdx.graphics.getHeight()) {
				hasFocus = false;
			}
			return;
			}
			hasFocus = true;
			justTouched = true;
			touched[0] = true;
			pressedButtons.add(getButton(mouseEvent.getButton()));
			deltaX[0] = 0;
			deltaY[0] = 0;
			if (isCursorCatched()) {
			touchX[0] += getMovementX(e);
			touchY[0] += getMovementY(e);
			} else {
			touchX[0] = getRelativeX(mouseEvent);
			touchY[0] = getRelativeY(mouseEvent);
			}
			if (processor != null) {
			processor.touchDown(touchX[0], touchY[0], 0, getButton(mouseEvent.getButton()));
			}
		}

		if (e.getType().equals("mousemove")) {
			final MouseEvent mouseEvent = (MouseEvent)e;
			if (isCursorCatched()) {
			deltaX[0] = (int)getMovementX(e);
			deltaY[0] = (int)getMovementY(e);
			touchX[0] += getMovementX(e);
			touchY[0] += getMovementY(e);
			} else {
			deltaX[0] = getRelativeX(mouseEvent) - touchX[0];
			deltaY[0] = getRelativeY(mouseEvent) - touchY[0];
			touchX[0] = getRelativeX(mouseEvent);
			touchY[0] = getRelativeY(mouseEvent);
			}
			if (processor != null) {
			if (touched[0]) {
				processor.touchDragged(touchX[0], touchY[0], 0);
			} else {
				processor.mouseMoved(touchX[0], touchY[0]);
			}
			}
		}

		if (e.getType().equals("mouseup")) {
			if (!touched[0]) {
			return;
			}
			final MouseEvent mouseEvent = (MouseEvent)e;
			pressedButtons.remove(getButton(mouseEvent.getButton()));
			touched[0] = pressedButtons.size > 0;
			if (isCursorCatched()) {
			deltaX[0] = (int)getMovementX(e);
			deltaY[0] = (int)getMovementY(e);
			touchX[0] += getMovementX(e);
			touchY[0] += getMovementY(e);
			} else {
			deltaX[0] = getRelativeX(mouseEvent) - touchX[0];
			deltaY[0] = getRelativeY(mouseEvent) - touchY[0];
			touchX[0] = getRelativeX(mouseEvent);
			touchY[0] = getRelativeY(mouseEvent);
			}
			touched[0] = false;
			if (processor != null) {
			processor.touchUp(touchX[0], touchY[0], 0, getButton(mouseEvent.getButton()));
			}
		}
		if (e.getType().equals(getMouseWheelEvent())) {
			if (processor != null) {
			processor.scrolled((int)getMouseWheelVelocity(e));
			}
			e.preventDefault();
		}
		if (e.getType().equals("keydown") && hasFocus) {
			final KeyboardEvent keyEvent = (KeyboardEvent)e;
			final int code = KeyCodes.toKey(keyEvent.getKeyCode());
			if (code == 67) {
			e.preventDefault();
			if (processor != null) {
				processor.keyDown(code);
				processor.keyTyped('\b');
			}
			} else {
			if (!pressedKeys[code]) {
				pressedKeyCount++;
				pressedKeys[code] = true;
				keyJustPressed = true;
				justPressedKeys[code] = true;
				if (processor != null) {
					processor.keyDown(code);
				}
			}
			}
		}

		if (e.getType().equals("keypress") && hasFocus) {
			final KeyboardEvent keyEvent = (KeyboardEvent)e;
			final char c = (char)keyEvent.getCharCode();
			if (processor != null) {
			processor.keyTyped(c);
			}
		}

		if (e.getType().equals("keyup") && hasFocus) {
			final KeyboardEvent keyEvent = (KeyboardEvent)e;
			final int code = KeyCodes.toKey(keyEvent.getKeyCode());
			if (pressedKeys[code]) {
			pressedKeyCount--;
			pressedKeys[code] = false;
			}
			if (processor != null) {
			processor.keyUp(code);
			}
		}

		// TODO Handle other events.
	}

	@Override
	public void getTextInput (final TextInputListener listener, final String title, final String text, final String hint) {
	}

	@Override
	public float getAccelerometerX () {
		TeaVMApplication.logUnsupported("Accelerometer");
		return 0f;
	}

	@Override
	public float getAccelerometerY () {
		TeaVMApplication.logUnsupported("Accelerometer");
		return 0f;
	}

	@Override
	public float getAccelerometerZ () {
		TeaVMApplication.logUnsupported("Accelerometer");
		return 0f;
	}

	@Override
	public float getGyroscopeX () {
		TeaVMApplication.logUnsupported("Gyroscope");
		return 0f;
	}

	@Override
	public float getGyroscopeY () {
		TeaVMApplication.logUnsupported("Gyroscope");
		return 0f;
	}

	@Override
	public float getGyroscopeZ () {
		TeaVMApplication.logUnsupported("Gyroscope");
		return 0f;
	}

	@Override
	public void setOnscreenKeyboardVisible (final boolean visible) {
		TeaVMApplication.logUnsupported("On screen keyboard");
	}

	@Override
	public void vibrate (final int milliseconds) {
		TeaVMApplication.logUnsupported("Vibrate");
	}

	@Override
	public void vibrate (final long[] pattern, final int repeat) {
		TeaVMApplication.logUnsupported("Vibrate");
	}

	@Override
	public void cancelVibrate () {
		TeaVMApplication.logUnsupported("Vibrate");
	}

	@Override
	public float getAzimuth () {
		TeaVMApplication.logUnsupported("Azimuth");
		return 0f;
	}

	@Override
	public float getPitch () {
		TeaVMApplication.logUnsupported("Pitch");
		return 0;
	}

	@Override
	public float getRoll () {
		TeaVMApplication.logUnsupported("Roll");
		return 0;
	}

	@Override
	public void getRotationMatrix (final float[] matrix) {
		TeaVMApplication.logUnsupported("Rotation");
	}

	@Override
	public int getRotation () {
		TeaVMApplication.logUnsupported("Rotation");
		return 0;
	}

	@Override
	public void setCatchBackKey (final boolean catchBack) {
		TeaVMApplication.logUnsupported("Back key");
	}

	@Override
	public boolean isCatchBackKey () {
		TeaVMApplication.logUnsupported("Back key");
		return false;
	}

	@Override
	public void setCatchMenuKey (final boolean catchMenu) {
		TeaVMApplication.logUnsupported("Menu key");
	}

	@Override
	public boolean isCatchMenuKey () {
		TeaVMApplication.logUnsupported("Menu key");
		return false;
	}
}
