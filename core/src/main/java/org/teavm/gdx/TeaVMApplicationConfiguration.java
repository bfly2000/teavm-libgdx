
package org.teavm.gdx;

import org.teavm.gdx.graphics.TeaVMGraphics.OrientationLockType;

import com.badlogic.gdx.Graphics;

/** Allows to configure {@link TeaVMApplication}.
 * @author MJ
 * @author Alexey Andreev */
public class TeaVMApplicationConfiguration {
	private OrientationLockType fullscreenOrientation;
	private String canvasId = "teavm";
	private boolean useGl30;
	private boolean antialiasEnabled;
	private boolean stencilEnabled;
	private boolean alphaEnabled;
	private boolean premultipliedAlpha;
	private boolean drawingBufferPreserved;

	/** @return true if antialias should be enabled. */
	public boolean isAntialiasEnabled () {
		return antialiasEnabled;
	}

	/** @param antialiasEnabled true will enable antialiasing. */
	public void setAntialiasEnabled (final boolean antialiasEnabled) {
		this.antialiasEnabled = antialiasEnabled;
	}

	/** @return if true, should use stencil buffer format. */
	public boolean isStencilEnabled () {
		return stencilEnabled;
	}

	/** @param stencilEnabled true to enable stencil. */
	public void setStencilEnabled (final boolean stencilEnabled) {
		this.stencilEnabled = stencilEnabled;
	}

	/** @return true if alpha channel should be included in the color buffer. */
	public boolean isAlphaEnabled () {
		return alphaEnabled;
	}

	/** @param alphaEnabled if true, an alpha channel will be included in the color buffer to combine the color buffer with the
	 *           rest of the webpage - effectively allowing transparent backgrounds at a performance cost. */
	public void setAlphaEnabled (final boolean alphaEnabled) {
		this.alphaEnabled = alphaEnabled;
	}

	/** @return true if premultiplied alpha should be used. */
	public boolean isPremultipliedAlpha () {
		return premultipliedAlpha;
	}

	/** @param premultipliedAlpha true to used premultiplied alpha. Might have performance impact. */
	public void setPremultipliedAlpha (final boolean premultipliedAlpha) {
		this.premultipliedAlpha = premultipliedAlpha;
	}

	/** @return true to preserve back buffer. */
	public boolean isDrawingBufferPreserved () {
		return drawingBufferPreserved;
	}

	/** @param drawingBufferPreserved true to preserve back buffer which allows to take screenshots via {@code canvas#toDataUrl}.
	 *           May have performance impact. */
	public void setDrawingBufferPreserved (final boolean drawingBufferPreserved) {
		this.drawingBufferPreserved = drawingBufferPreserved;
	}

	/** @return orientation type that should be used during fullscreen mode. Can be null. */
	public OrientationLockType getFullscreenOrientation () {
		return fullscreenOrientation;
	}

	/** @param fullscreenOrientation if not null, will attempt locking as the application enters full-screen-mode.
	 * @see org.teavm.gdx.graphics.TeaVMGraphics.CommonOrientationLockType */
	public void setFullscreenOrientation (final OrientationLockType fullscreenOrientation) {
		this.fullscreenOrientation = fullscreenOrientation;
	}

	/** @return ID of the HTML canvas element. */
	public String getCanvasId () {
		return canvasId;
	}

	/** @param canvasId ID that should return link to a single HTML canvas element. "teavm" by default. */
	public void setCanvasId (final String canvasId) {
		this.canvasId = canvasId;
	}

	/** @return true if GL30 should be used instead of GL20. Most likely not supported. */
	public boolean isUsingGl30 () {
		return useGl30;
	}

	/** @param useGl30 if true, GL30 will be used where available. Defaults to false. WebGL currently seems to support only GL20,
	 *           so this value is mostly ignored by default. Set to true if you added custom GL30 implementation to
	 *           {@link Graphics}. */
	public void setUseGl30 (final boolean useGl30) {
		this.useGl30 = useGl30;
	}
}
