
package org.teavm.gdx.clipboard;

import com.badlogic.gdx.utils.Clipboard;

/** Mock-up clipboard implementation. Text can be copied and pasted only within TeaVM application. Note that this matches GWT
 * implementation.
 * @author MJ */
public class MockUpClipboard implements Clipboard {
	private String content = "";

	@Override
	public String getContents () {
		return content;
	}

	@Override
	public void setContents (final String content) {
		this.content = content == null ? "" : content;
	}
}
