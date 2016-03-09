
package org.teavm.gdx.clipboard;

import org.teavm.gdx.util.Exceptions;
import org.teavm.jso.JSBody;
import org.teavm.jso.dom.css.CSSStyleDeclaration;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;

import com.badlogic.gdx.utils.Clipboard;

/** Clipboard implementation for TeaVM applications. Tries hard to use the general system clipboard, but might cause issues on
 * some browsers (especially Safari, which does not support copy and paste commands). Most browsers allow to access clipboard only
 * directly after an user's action, and this is not exactly how the TeaVM input processor works. Use {@link MockUpClipboard} if
 * you do not want or need to support global clipboard.
 * @author MJ
 * @see MockUpClipboard */
public class TeaVMClipboard implements Clipboard {
	private final HTMLElement copyElement;
	private final HTMLDocument document;
	private String cachedContent = "";

	public TeaVMClipboard () {
		document = HTMLDocument.current();
		copyElement = document.createElement("textarea");
		final CSSStyleDeclaration style = copyElement.getStyle();
		style.setProperty("position", "fixed");
		style.setProperty("top", "0");
		style.setProperty("left", "0");
		style.setProperty("width", "2em");
		style.setProperty("height", "2em");
		style.setProperty("padding", "0");
		style.setProperty("border", "none");
		style.setProperty("outline", "none");
		style.setProperty("boxShadow", "none");
		style.setProperty("background", "transparent");
	}

	@Override
	public String getContents () {
		try {
			if (isClipboardDataSupported()) {
			cachedContent = getClipboardData();
			} else {
			document.appendChild(copyElement);
			select(copyElement);
			executePaste();
			cachedContent = getValue(copyElement);
			}
		} catch (final Throwable exception) {
			Exceptions.ignore(exception);
		} finally {
			if (copyElement.getParentNode() != null) {
			copyElement.getParentNode().removeChild(copyElement);
			}
		}
		return cachedContent;
	}

	@Override
	public void setContents (final String content) {
		cachedContent = content == null ? "" : content;
		try {
			document.appendChild(copyElement);
			select(copyElement);
			executeCopy();
		} catch (final Throwable exception) {
			Exceptions.ignore(exception);
		} finally {
			if (copyElement.getParentNode() != null) {
			copyElement.getParentNode().removeChild(copyElement);
			}
		}
	}

	@JSBody(params = "element", script = "return element.value;")
	private static native String getValue (HTMLElement element);

	@JSBody(params = "element", script = "element.select();")
	private static native void select (HTMLElement element);

	@JSBody(params = {}, script = "document.execCommand('paste');")
	private static native void executePaste ();

	@JSBody(params = {}, script = "document.execCommand('copy');")
	private static native void executeCopy ();

	/** @return true if {@link #getClipboardData()} can be used. */
	@JSBody(params = {}, script = "return window.clipboardData;")
	public static native boolean isClipboardDataSupported ();

	/** @return current clipboard data. */
	@JSBody(params = {}, script = "return window.clipboardData.getData('Text');")
	public static native String getClipboardData ();
}
