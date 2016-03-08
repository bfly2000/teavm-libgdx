
package org.teavm.gdx.audio;

import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLAudioElement;
import org.teavm.jso.dom.html.HTMLDocument;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

/** Default implementation of {@link Music} for TeaVM applications. Uses {@link HTMLAudioElement} to play sounds.
 * @author Alexey Andreev */
public class TeaVMMusic implements Music {
	private HTMLAudioElement element;
	private boolean started;
	private OnCompletionListener listener;

	public TeaVMMusic (final FileHandle file) {
		element = (HTMLAudioElement)HTMLDocument.current().createElement("audio");
		element.setSrc("assets/" + file.path());
		element.addEventListener("ended", new EventListener<Event>() {
			@Override
			public void handleEvent (final Event evt) {
			if (listener != null) {
				listener.onCompletion(TeaVMMusic.this);
			}
			}
		});
		HTMLDocument.current().getBody().appendChild(element);
	}

	private void checkDisposed () {
		if (element == null) {
			throw new IllegalStateException("This music instance is already disposed.");
		}
	}

	@Override
	public void play () {
		checkDisposed();
		element.play();
		started = true;
	}

	@Override
	public void pause () {
		checkDisposed();
		element.pause();
	}

	@Override
	public void stop () {
		checkDisposed();
		element.pause();
		element.setCurrentTime(0);
		started = false;
	}

	@Override
	public boolean isPlaying () {
		checkDisposed();
		return started && !element.isPaused() && element.isEnded();
	}

	@Override
	public void setLooping (final boolean isLooping) {
		checkDisposed();
		element.setLoop(isLooping);
	}

	@Override
	public boolean isLooping () {
		checkDisposed();
		return element.isLoop();
	}

	@Override
	public void setVolume (final float volume) {
		checkDisposed();
		element.setVolume(volume);
	}

	@Override
	public float getVolume () {
		checkDisposed();
		return element.getVolume();
	}

	@Override
	public void setPan (final float pan, final float volume) {
		checkDisposed();
		element.setVolume(volume);
	}

	@Override
	public void setPosition (final float position) {
		checkDisposed();
		element.setCurrentTime(position);
	}

	@Override
	public float getPosition () {
		checkDisposed();
		return (float)element.getCurrentTime();
	}

	@Override
	public void dispose () {
		if (element != null) {
			element.getParentNode().removeChild(element);
			element = null;
		}
	}

	@Override
	public void setOnCompletionListener (final OnCompletionListener listener) {
		this.listener = listener;
	}
}
