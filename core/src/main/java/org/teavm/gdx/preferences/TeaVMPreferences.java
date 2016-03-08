
package org.teavm.gdx.preferences;

import java.util.Map;

import org.teavm.jso.browser.Storage;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** TeaVM implementation of {@link Preferences} used when local storage is available. Name of the preferences and its keys have to
 * contain valid characters - they will be used as keys in local storage.
 *
 * @author MJ */
public class TeaVMPreferences extends AbstractPreferences {
	private final Storage localStorage;
	private final String name;

	/** @param name used to identify preferences in local storage. Has to contain valid local storage key characters. */
	public TeaVMPreferences (final String name) {
		if (name == null || name.length() == 0) {
			throw new GdxRuntimeException("Preferences name cannot be empty.");
		}
		localStorage = Storage.getLocalStorage();
		this.name = name + ".";
	}

	@Override
	public Preferences putString (final String key, final String val) {
		localStorage.setItem(toKey(key), val);
		return this;
	}

	/** @param key name of a specific preference.
	 * @return actual key value of the preference. */
	protected String toKey (final String key) {
		return name + key;
	}

	@Override
	public String getString (final String key) {
		return localStorage.getItem(toKey(key));
	}

	@Override
	public boolean contains (final String key) {
		return getString(key) != null;
	}

	@Override
	public void remove (final String key) {
		localStorage.removeItem(toKey(key));
	}

	@Override
	public void clear () {
		for (int index = localStorage.getLength() - 1; index >= 0; index--) {
			final String key = localStorage.key(index);
			if (key != null && key.startsWith(name)) {
			localStorage.removeItem(key);
			}
		}
	}

	@Override
	protected void fillPreferences (final Map<String, Object> preferences) {
		for (int index = 0, length = localStorage.getLength(); index < length; index++) {
			final String key = localStorage.key(index);
			if (key != null && key.startsWith(name)) {
			preferences.put(key.substring(name.length()), localStorage.getItem(key));
			}
		}
	}

	@Override
	public void flush () {
		// Storage automatically saves stored preferences.
	}
}
