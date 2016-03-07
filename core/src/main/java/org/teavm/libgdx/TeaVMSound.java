package org.teavm.libgdx;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Music.OnCompletionListener;
import com.badlogic.gdx.audio.Sound;

/** @author Alexey Andreev */
public class TeaVMSound implements Sound {
    private final TeaVMFileHandle file;
    private final Map<Long, TeaVMMusic> instances = new HashMap<>();
    private long nextId;
    private final float volume = 1;
    private final float pitch = 1;
    private final float pan = 0.5f;

    public TeaVMSound(final TeaVMFileHandle file) {
        this.file = file;
    }

    @Override
    public long play() {
        return play(volume);
    }

    @Override
    public long play(final float volume) {
        return play(volume, pitch, pan);
    }

    @Override
    public long play(final float volume, final float pitch, final float pan) {
        return play(volume, pitch, pan, false);
    }

    @Override
    public long loop() {
        return loop(volume);
    }

    @Override
    public long loop(final float volume) {
        return loop(volume, pitch, pan);
    }

    @Override
    public long loop(final float volume, final float pitch, final float pan) {
        return play(volume, pitch, pan, true);
    }

    private long play(final float volume, final float pitch, final float pan, final boolean loop) {
        final long id = nextId++;
        final TeaVMMusic instance = new TeaVMMusic(file);
        instance.setVolume(volume);
        instance.setPan(pan, volume);
        instance.setLooping(loop);
        instance.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(final Music music) {
                instances.remove(id);
                instance.dispose();
            }
        });
        instances.put(id, instance);
        instance.play();
        return id;
    }

    @Override
    public void stop() {
        for (final TeaVMMusic music : instances.values()) {
            music.dispose();
        }
        instances.clear();
    }

    @Override
    public void pause() {
        for (final TeaVMMusic music : instances.values()) {
            music.pause();
        }
    }

    @Override
    public void resume() {
        for (final TeaVMMusic music : instances.values()) {
            music.play();
        }
    }

    @Override
    public void dispose() {
        stop();
    }

    @Override
    public void stop(final long soundId) {
        final TeaVMMusic music = instances.get(soundId);
        if (music != null) {
            music.stop();
        }
    }

    @Override
    public void pause(final long soundId) {
        final TeaVMMusic music = instances.get(soundId);
        if (music != null) {
            music.pause();
        }
    }

    @Override
    public void resume(final long soundId) {
        final TeaVMMusic music = instances.get(soundId);
        if (music != null) {
            music.play();
        }
    }

    @Override
    public void setLooping(final long soundId, final boolean looping) {
        final TeaVMMusic music = instances.get(soundId);
        if (music != null) {
            music.setLooping(looping);
        }
    }

    @Override
    public void setPitch(final long soundId, final float pitch) {
    }

    @Override
    public void setVolume(final long soundId, final float volume) {
        final TeaVMMusic music = instances.get(soundId);
        if (music != null) {
            music.setVolume(volume);
        }
    }

    @Override
    public void setPan(final long soundId, final float pan, final float volume) {
        final TeaVMMusic music = instances.get(soundId);
        if (music != null) {
            music.setPan(pan, volume);
        }
    }
}
