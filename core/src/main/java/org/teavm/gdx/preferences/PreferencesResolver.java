
package org.teavm.gdx.preferences;

import org.teavm.jso.JSBody;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.ObjectMap;

/** Allows to create and maintain {@link Preferences} instances.
 *
 * @author MJ */
public class PreferencesResolver {
	protected final ObjectMap<String, Preferences> preferences = new ObjectMap<>();

	/** @return true if {@code localStorage} is supported by the browser. If this method returns false, {@link MockUpPreferences}
	 *         are used - these are not saved between sessions. */
	@JSBody(params = {}, script = "return localStorage!=null;")
	public static native boolean isLocalStorageSupported ();

	/** @param name ID of the preferences. Has to be valid local storage key.
	 * @return {@link Preferences} implementation for the given ID. If locale storage is not supported, mock up preferences will
	 *         be provided. Never returns null. */
	public Preferences getPreferences (final String name) {
		if (preferences.containsKey(name)) {
			return preferences.get(name);
		}
		final Preferences preference = createPreferences(name);
		preferences.put(name, preference);
		return preference;
	}

	/** @param name ID of the preferences. Has to be valid local storage key.
	 * @return {@link TeaVMPreferences} if local storage is supported. {@link MockUpPreferences} otherwise. */
	protected AbstractPreferences createPreferences (final String name) {
		return isLocalStorageSupported() ? new TeaVMPreferences(name) : new MockUpPreferences();
	}
}
