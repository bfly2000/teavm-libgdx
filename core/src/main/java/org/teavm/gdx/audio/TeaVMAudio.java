
package org.teavm.gdx.audio;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** Default {@link Audio} implementation for TeaVM application. Supports {@link Music} and {@link Sound} objects. Does not support
 * {@link AudioDevice} and {@link AudioRecorder}.
 * @author Alexey Andreev */
public class TeaVMAudio implements Audio {
	@Override
	public Sound newSound (final FileHandle fileHandle) {
		return new TeaVMSound(fileHandle);
	}

	@Override
	public Music newMusic (final FileHandle file) {
		return new TeaVMMusic(file);
	}

	@Override
	public AudioDevice newAudioDevice (final int samplingRate, final boolean isMono) {
		throw new GdxRuntimeException("AudioDevice is not supported by TeaVM application.");
	}

	@Override
	public AudioRecorder newAudioRecorder (final int samplingRate, final boolean isMono) {
		throw new GdxRuntimeException("AudioDevice is not supported by TeaVM application.");
	}
}
