
package org.teavm.gdx.lifecycle;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/** Allows to pass callbacks to {@code requestAnimationFrame} function.
 * @author MJ */
@JSFunctor
public interface AnimationFrame extends JSObject {
	/** Executed as {@code requestAnimationFrame} callback. */
	void onFrame ();
}
