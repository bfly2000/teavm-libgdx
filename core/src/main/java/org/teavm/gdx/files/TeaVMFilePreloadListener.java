
package org.teavm.gdx.files;

/** Allows to hook methods after the finish of assets preloading.
 * @author Alexey Andreev */
public interface TeaVMFilePreloadListener {
	/** Invoked when unable to preload files. */
	void error ();

	/** Invoked when files are successfully preloaded. */
	void complete ();
}
