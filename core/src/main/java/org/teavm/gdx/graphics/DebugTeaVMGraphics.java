
package org.teavm.gdx.graphics;

import org.teavm.gdx.TeaVMApplication;
import org.teavm.gdx.graphics.webgl.DebugTeaVMGL20;
import org.teavm.jso.webgl.WebGLRenderingContext;

import com.badlogic.gdx.graphics.GL20;

/** Uses {@link DebugTeaVMGL20} instead of the regular implementation. Throws runtime exceptions in case of any encountered WebGL
 * errors. Use for debugging.
 * @author MJ */
public class DebugTeaVMGraphics extends TeaVMGraphics {
	public DebugTeaVMGraphics (final TeaVMApplication application) {
		super(application);
	}

	@Override
	protected GL20 createGL20 (final WebGLRenderingContext context) {
		return new DebugTeaVMGL20(context);
	}
}
