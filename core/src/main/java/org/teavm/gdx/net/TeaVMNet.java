
package org.teavm.gdx.net;

import org.teavm.jso.browser.Window;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;

/** {@link Net} for TeaVM backend. Opens URIs in a separate tab. Allows to send and cancel HTTP request. Does not support TCP
 * server or client sockets. If you need a more robust networking solution, do some research on web sockets.
 * @author MJ */
public class TeaVMNet implements Net {
	@Override
	public boolean openURI (final String URI) {
		Window.current().open(URI, "_blank");
		return true;
	}

	@Override
	public void sendHttpRequest (final HttpRequest httpRequest, final HttpResponseListener httpResponseListener) {
		// TODO send HTTP request
	}

	@Override
	public void cancelHttpRequest (final HttpRequest httpRequest) {
		// TODO cancel HTTP request
	}

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
