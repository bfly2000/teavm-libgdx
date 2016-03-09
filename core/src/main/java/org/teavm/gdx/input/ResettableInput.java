
package org.teavm.gdx.input;

import com.badlogic.gdx.Input;

/** Extends the {@link Input} interface with {@link #reset()} method that should be called every frame.
 * @author MJ */
public interface ResettableInput extends Input {
	/** Should be called every frame. Resets current input values. */
	void reset ();
}
