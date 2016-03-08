/*
 *  Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.teavm.gdx.graphics;

import org.teavm.gdx.TeaVMApplicationConfiguration;
import org.teavm.gdx.graphics.webgl.TeaVMGL20;
import org.teavm.jso.browser.Screen;
import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.jso.core.JSString;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.webgl.WebGLContextAttributes;
import org.teavm.jso.webgl.WebGLRenderingContext;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;

/** @author Alexey Andreev */
public class TeaVMGraphics implements Graphics {
	private final HTMLCanvasElement element;
	private final TeaVMApplicationConfiguration config;
	private final WebGLRenderingContext context;
	public long frameId = -1;
	float deltaTime;
	long lastTimeStamp;
	long time;
	int frames;
	float fps;
	private final TeaVMGL20 gl20;

	public TeaVMGraphics (final HTMLCanvasElement element, final TeaVMApplicationConfiguration config) {
		this.element = element;
		this.config = config;

		final WebGLContextAttributes attr = WebGLContextAttributes.create();
		attr.setAlpha(config.isAlphaEnabled());
		attr.setAntialias(config.isAntialiasEnabled());
		attr.setStencil(config.isStencilEnabled());
		attr.setPremultipliedAlpha(config.isPremultipliedAlpha());
		attr.setPreserveDrawingBuffer(config.isDrawingBufferPreserved());

		context = (WebGLRenderingContext)element.getContext("webgl");
		context.viewport(0, 0, element.getWidth(), element.getHeight());
		gl20 = new TeaVMGL20(context);
	}

	@Override
	public boolean isGL30Available () {
		return false;
	}

	@Override
	public GL20 getGL20 () {
		return gl20;
	}

	@Override
	public GL30 getGL30 () {
		return null;
	}

	@Override
	public int getWidth () {
		return element.getWidth();
	}

	@Override
	public int getHeight () {
		return element.getHeight();
	}

	@Override
	public long getFrameId () {
		return frameId;
	}

	@Override
	public float getDeltaTime () {
		return deltaTime;
	}

	@Override
	public float getRawDeltaTime () {
		return deltaTime;
	}

	@Override
	public int getFramesPerSecond () {
		return (int)fps;
	}

	@Override
	public GraphicsType getType () {
		return GraphicsType.WebGL;
	}

	@Override
	public float getPpiX () {
		return 96;
	}

	@Override
	public float getPpiY () {
		return 96;
	}

	@Override
	public float getPpcX () {
		return 96 / 2.54f;
	}

	@Override
	public float getPpcY () {
		return 96 / 2.54f;
	}

	@Override
	public float getDensity () {
		return 0;
	}

	@Override
	public boolean supportsDisplayModeChange () {
		return true;
	}

	@Override
	public DisplayMode[] getDisplayModes () {
		final Screen screen = Window.current().getScreen();
		return new DisplayMode[] {new DisplayMode(screen.getWidth(), screen.getHeight(), 60, 8) {}};
	}

	@Override
	public void setTitle (final String title) {
	}

	@Override
	public void setVSync (final boolean vsync) {
	}

	@Override
	public BufferFormat getBufferFormat () {
		return new BufferFormat(8, 8, 8, 0, 16, config.isStencilEnabled() ? 8 : 0, 0, false);
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
	public void setContinuousRendering (final boolean isContinuous) {
	}

	@Override
	public boolean isContinuousRendering () {
		return false;
	}

	@Override
	public void requestRendering () {
	}

	@Override
	public boolean isFullscreen () {
		return false;
	}

	public void update () {
		final long currTimeStamp = System.currentTimeMillis();
		deltaTime = (currTimeStamp - lastTimeStamp) / 1000.0f;
		lastTimeStamp = currTimeStamp;
		time += deltaTime;
		frames++;
		if (time > 1) {
			fps = frames;
			time = 0;
			frames = 0;
		}
	}

	@Override
	public int getBackBufferWidth () {
		return 0;
	}

	@Override
	public int getBackBufferHeight () {
		return 0;
	}

	@Override
	public Monitor getPrimaryMonitor () {
		return null;
	}

	@Override
	public Monitor getMonitor () {
		return null;
	}

	@Override
	public Monitor[] getMonitors () {
		return null;
	}

	@Override
	public DisplayMode[] getDisplayModes (final Monitor monitor) {
		return null;
	}

	@Override
	public DisplayMode getDisplayMode () {
		return null;
	}

	@Override
	public DisplayMode getDisplayMode (final Monitor monitor) {
		return null;
	}

	@Override
	public boolean setFullscreenMode (final DisplayMode displayMode) {
		return false;
	}

	@Override
	public boolean setWindowedMode (final int width, final int height) {
		return false;
	}

	@Override
	public Cursor newCursor (final Pixmap pixmap, final int xHotspot, final int yHotspot) {
		return null;
	}

	@Override
	public void setCursor (final Cursor cursor) {
	}

	@Override
	public void setSystemCursor (final SystemCursor systemCursor) {
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
}
