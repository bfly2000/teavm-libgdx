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

package org.teavm.gdx.emu;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.teavm.gdx.files.TeaVMFileHandle;
import org.teavm.javascript.spi.GeneratedBy;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Uint8ClampedArray;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** @author Alexey Andreev */
public class PixmapEmulator implements Disposable {
	public static Map<Integer, PixmapEmulator> pixmaps = new HashMap<>();
	static int nextId = 0;
	int width;
	int height;
	Format format;
	HTMLCanvasElement canvas;
	CanvasRenderingContext2D context;
	int id;
	IntBuffer buffer;
	int r = 255, g = 255, b = 255;
	float a;
	String color = make(r, g, b, a);
	static String clearColor = make(255, 255, 255, 1.0f);
	static Blending blending;
	Uint8ClampedArray pixels;
	private ByteBuffer pixelsBuffer;

	public PixmapEmulator (final FileHandle file) {
		final TeaVMFileHandle teavmFile = (TeaVMFileHandle)file;
		final TeaVMFileHandle.FSEntry entry = teavmFile.entry();
		final HTMLImageElement img = entry.imageElem;
		if (img == null) {
			throw new GdxRuntimeException("Couldn't load image '" + file.path() + "', file does not exist");
		}
		create(img.getWidth(), img.getHeight(), Format.RGBA8888);
		context.setGlobalCompositeOperation("copy");
		context.drawImage(img, 0, 0);
		context.setGlobalCompositeOperation("source-over");
	}

	public PixmapEmulator (final HTMLImageElement img) {
		create(img.getWidth(), img.getHeight(), Format.RGBA8888);
		context.drawImage(img, 0, 0);
	}

	public PixmapEmulator (final int width, final int height, final Pixmap.Format format) {
		create(width, height, format);
	}

	private void create (final int width, final int height, final Pixmap.Format format2) {
		this.width = width;
		this.height = height;
		format = Format.RGBA8888;
		canvas = (HTMLCanvasElement)HTMLDocument.current().createElement("canvas");
		canvas.getStyle().setProperty("display", "none");
		HTMLDocument.current().getBody().appendChild(canvas);
		canvas.setWidth(width);
		canvas.setHeight(height);
		context = (CanvasRenderingContext2D)canvas.getContext("2d");
		context.setGlobalCompositeOperation("source-over");
		id = nextId++;
		pixmaps.put(id, this);
	}

	public static String make (final int r2, final int g2, final int b2, final float a2) {
		return "rgba(" + r2 + "," + g2 + "," + b2 + "," + a2 + ")";
	}

	public static void setBlending (final Blending blending) {
		PixmapEmulator.blending = blending;
		for (final PixmapEmulator pixmap : pixmaps.values()) {
			pixmap.context.setGlobalCompositeOperation("source-over");
		}
	}

	public static Blending getBlending () {
		return blending;
	}

	public static void setFilter (final Filter filter) {
	}

	public Format getFormat () {
		return format;
	}

	public int getGLInternalFormat () {
		return GL20.GL_RGBA;
	}

	public int getGLFormat () {
		return GL20.GL_RGBA;
	}

	public int getGLType () {
		return GL20.GL_UNSIGNED_BYTE;
	}

	public int getWidth () {
		return width;
	}

	public int getHeight () {
		return height;
	}

	@Override
	public void dispose () {
		final PixmapEmulator pixmap = pixmaps.remove(id);
		if (pixmap.canvas != null) {
			pixmap.canvas.getParentNode().removeChild(pixmap.canvas);
		}
	}

	public void setColor (final int color) {
		r = color >>> 24 & 0xff;
		g = color >>> 16 & 0xff;
		b = color >>> 8 & 0xff;
		a = (color & 0xff) / 255f;
		this.color = make(r, g, b, a);
		context.setFillStyle(this.color);
		context.setStrokeStyle(this.color);
	}

	public void setColor (final float r, final float g, final float b, final float a) {
		this.r = (int)(r * 255);
		this.g = (int)(g * 255);
		this.b = (int)(b * 255);
		this.a = a;
		color = make(this.r, this.g, this.b, this.a);
		context.setFillStyle(color);
		context.setStrokeStyle(color);
	}

	public void setColor (final Color color) {
		setColor(color.r, color.g, color.b, color.a);
	}

	public void fill () {
		rectangle(0, 0, getWidth(), getHeight(), DrawType.FILL);
	}

	public void drawLine (final int x, final int y, final int x2, final int y2) {
		line(x, y, x2, y2, DrawType.STROKE);
	}

	public void drawRectangle (final int x, final int y, final int width, final int height) {
		rectangle(x, y, width, height, DrawType.STROKE);
	}

	public void drawPixmap (final PixmapEmulator pixmap, final int x, final int y) {
		final HTMLCanvasElement image = pixmap.canvas;
		image(image, 0, 0, image.getWidth(), image.getHeight(), x, y, image.getWidth(), image.getHeight());
	}

	public void drawPixmap (final PixmapEmulator pixmap, final int x, final int y, final int srcx, final int srcy,
		final int srcWidth, final int srcHeight) {
		final HTMLCanvasElement image = pixmap.canvas;
		image(image, srcx, srcy, srcWidth, srcHeight, x, y, srcWidth, srcHeight);
	}

	public void drawPixmap (final PixmapEmulator pixmap, final int srcx, final int srcy, final int srcWidth, final int srcHeight,
		final int dstx, final int dsty, final int dstWidth, final int dstHeight) {
		image(pixmap.canvas, srcx, srcy, srcWidth, srcHeight, dstx, dsty, dstWidth, dstHeight);
	}

	public void fillRectangle (final int x, final int y, final int width, final int height) {
		rectangle(x, y, width, height, DrawType.FILL);
	}

	public void drawCircle (final int x, final int y, final int radius) {
		circle(x, y, radius, DrawType.STROKE);
	}

	public void fillCircle (final int x, final int y, final int radius) {
		circle(x, y, radius, DrawType.FILL);
	}

	public void fillTriangle (final int x1, final int y1, final int x2, final int y2, final int x3, final int y3) {
		triangle(x1, y1, x2, y2, x3, y3, DrawType.FILL);
	}

	public int getPixel (final int x, final int y) {
		if (pixels == null) {
			pixels = context.getImageData(0, 0, width, height).getData();
		}
		final int i = x * 4 + y * width * 4;
		final int r = pixels.get(i + 0) & 0xff;
		final int g = pixels.get(i + 1) & 0xff;
		final int b = pixels.get(i + 2) & 0xff;
		final int a = pixels.get(i + 3) & 0xff;
		return r << 24 | g << 16 | b << 8 | a;
	}

	public ByteBuffer getPixels () {
		if (pixels == null) {
			pixels = context.getImageData(0, 0, width, height).getData();
		}
		return ByteBuffer.wrap(bufferAsArray(pixels.getBuffer()));
	}

	@GeneratedBy(PixmapNativeGenerator.class)
	private native byte[] bufferAsArray (ArrayBuffer array);

	public void drawPixel (final int x, final int y) {
		rectangle(x, y, 1, 1, DrawType.FILL);
	}

	public void drawPixel (final int x, final int y, final int color) {
		setColor(color);
		drawPixel(x, y);
	}

	private void circle (final int x, final int y, final int radius, final DrawType drawType) {
		if (blending == Blending.None) {
			context.setFillStyle(clearColor);
			context.setStrokeStyle(clearColor);
			context.setGlobalCompositeOperation("clear");
			context.beginPath();
			context.arc(x, y, radius, 0, 2 * Math.PI, false);
			fillOrStrokePath(drawType);
			context.closePath();
			context.setFillStyle(color);
			context.setStrokeStyle(color);
			context.setGlobalCompositeOperation("source-over");
		}
		context.beginPath();
		context.arc(x, y, radius, 0, 2 * Math.PI, false);
		fillOrStrokePath(drawType);
		context.closePath();
		pixels = null;
	}

	private void line (final int x, final int y, final int x2, final int y2, final DrawType drawType) {
		if (blending == Blending.None) {
			context.setFillStyle(clearColor);
			context.setStrokeStyle(clearColor);
			context.setGlobalCompositeOperation("clear");
			context.beginPath();
			context.moveTo(x, y);
			context.lineTo(x2, y2);
			fillOrStrokePath(drawType);
			context.closePath();
			context.setFillStyle(color);
			context.setStrokeStyle(color);
			context.setGlobalCompositeOperation("source-over");
		}
		context.beginPath();
		context.moveTo(x, y);
		context.lineTo(x2, y2);
		fillOrStrokePath(drawType);
		context.closePath();
	}

	private void rectangle (final int x, final int y, final int width, final int height, final DrawType drawType) {
		if (blending == Blending.None) {
			context.setFillStyle(clearColor);
			context.setStrokeStyle(clearColor);
			context.setGlobalCompositeOperation("clear");
			context.beginPath();
			context.rect(x, y, width, height);
			fillOrStrokePath(drawType);
			context.closePath();
			context.setFillStyle(color);
			context.setStrokeStyle(color);
			context.setGlobalCompositeOperation("source-over");
		}
		context.beginPath();
		context.rect(x, y, width, height);
		fillOrStrokePath(drawType);
		context.closePath();
		pixels = null;
	}

	private void triangle (final int x1, final int y1, final int x2, final int y2, final int x3, final int y3,
		final DrawType drawType) {
		if (blending == Blending.None) {
			context.setFillStyle(clearColor);
			context.setStrokeStyle(clearColor);
			context.setGlobalCompositeOperation("clear");
			context.beginPath();
			context.moveTo(x1, y1);
			context.lineTo(x2, y2);
			context.lineTo(x3, y3);
			context.lineTo(x1, y1);
			fillOrStrokePath(drawType);
			context.closePath();
			context.setFillStyle(color);
			context.setStrokeStyle(color);
			context.setGlobalCompositeOperation("source-over");
		}
		context.beginPath();
		context.moveTo(x1, y1);
		context.lineTo(x2, y2);
		context.lineTo(x3, y3);
		context.lineTo(x1, y1);
		fillOrStrokePath(drawType);
		context.closePath();
		pixels = null;
	}

	private void image (final HTMLCanvasElement image, final int srcX, final int srcY, final int srcWidth, final int srcHeight,
		final int dstX, final int dstY, final int dstWidth, final int dstHeight) {
		if (blending == Blending.None) {
			context.setFillStyle(clearColor);
			context.setStrokeStyle(clearColor);
			context.setGlobalCompositeOperation("clear");
			context.beginPath();
			context.rect(dstX, dstY, dstWidth, dstHeight);
			fillOrStrokePath(DrawType.FILL);
			context.closePath();
			context.setFillStyle(color);
			context.setStrokeStyle(color);
			context.setGlobalCompositeOperation("source-over");
		}
		context.drawImage(image, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
		pixels = null;
	}

	private void fillOrStrokePath (final DrawType drawType) {
		switch (drawType) {
		case FILL:
			context.fill();
			break;
		case STROKE:
			context.stroke();
			break;
		}
	}

	private enum DrawType {
		FILL, STROKE
	}
}
