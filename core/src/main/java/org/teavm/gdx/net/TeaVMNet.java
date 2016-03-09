
package org.teavm.gdx.net;

import java.util.Map.Entry;

import org.teavm.jso.JSBody;
import org.teavm.jso.ajax.ReadyStateChangeHandler;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.Window;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;

/** {@link Net} for TeaVM backend. Opens URIs in a separate tab. Allows to send and cancel HTTP request. Does not support TCP
 * server or client sockets. If you need a more robust networking solution, try doing research on web sockets.
 * @author MJ */
public class TeaVMNet implements Net {
	private final ObjectMap<HttpRequest, XMLHttpRequest> requests = new ObjectMap<>();
	private final ObjectMap<HttpRequest, HttpResponseListener> listeners = new ObjectMap<>();

	@Override
	public boolean openURI (final String URI) {
		Window.current().open(URI, "_blank");
		return true;
	}

	@Override
	public void sendHttpRequest (final HttpRequest httpRequest, final HttpResponseListener httpResponseListener) {
		if (httpRequest.getUrl() == null || httpRequest.getMethod() == null || httpResponseListener == null) {
			httpResponseListener.failed(new GdxRuntimeException("Unable to send request without URL, method or listener."));
			return;
		}
		final XMLHttpRequest request = XMLHttpRequest.create();
		request.setOnReadyStateChange(new ReadyStateChangeHandler() {
			@Override
			public void stateChanged () {
			if (request.getReadyState() != XMLHttpRequest.DONE) {
				return;
			}
			httpResponseListener.handleHttpResponse(new TeaVMHttpResponse(request));
			requests.remove(httpRequest);
			listeners.remove(httpRequest);
			}
		});
		final boolean valueInUrl = HttpMethods.GET.equalsIgnoreCase(httpRequest.getMethod())
			|| HttpMethods.DELETE.equalsIgnoreCase(httpRequest.getMethod());
		for (final Entry<String, String> header : httpRequest.getHeaders().entrySet()) {
			request.setRequestHeader(header.getKey(), header.getValue());
		}
		setTimeOut(request, httpRequest.getTimeOut());
		setIncludeCredentials(request, httpRequest.getIncludeCredentials());
		if (valueInUrl) {
			request.open(httpRequest.getMethod().toUpperCase(), httpRequest.getUrl() + "?" + httpRequest.getContent());
			request.send();
		} else {
			request.open(httpRequest.getMethod().toUpperCase(), httpRequest.getUrl());
			request.send(httpRequest.getContent());
		}
	}

	/** @param request will be modified.
	 * @param timeOut will be set as "timeout" property. */
	@JSBody(params = {"request", "timeOut"}, script = "request.timeout=timeOut;")
	protected static native void setTimeOut (XMLHttpRequest request, int timeOut);

	/** @param request will be modified.
	 * @param includeCredentials true to include credentials. */
	@JSBody(params = {"request", "includeCredentials"}, script = "request.withCredentials=includeCredentials;")
	protected static native void setIncludeCredentials (XMLHttpRequest request, boolean includeCredentials);

	@Override
	public void cancelHttpRequest (final HttpRequest httpRequest) {
		final XMLHttpRequest request = requests.remove(httpRequest);
		if (request != null) {
			cancelRequest(request);
		}
		final HttpResponseListener listener = listeners.remove(httpRequest);
		if (listener != null) {
			listener.cancelled();
		}
	}

	/** @param request will be aborted. */
	@JSBody(params = "request", script = "request.abort();")
	protected static native void cancelRequest (XMLHttpRequest request);

	@Override
	public ServerSocket newServerSocket (final Protocol protocol, final String hostname, final int port,
		final ServerSocketHints hints) {
		throw new UnsupportedOperationException("TeaVM application cannot create server sockets.");
	}

	@Override
	public ServerSocket newServerSocket (final Protocol protocol, final int port, final ServerSocketHints hints) {
		throw new UnsupportedOperationException("TeaVM application cannot create server sockets.");
	}

	@Override
	public Socket newClientSocket (final Protocol protocol, final String host, final int port, final SocketHints hints) {
		throw new UnsupportedOperationException(
			"TeaVM application cannot create TCP client sockets. Look into web socket implementations.");
	}
}
