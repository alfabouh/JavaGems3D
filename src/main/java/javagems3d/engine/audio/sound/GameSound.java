/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.audio.sound;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.openal.AL10;
import javagems3d.engine.audio.SoundManager;
import javagems3d.engine.audio.sound.data.SoundType;
import javagems3d.engine.physics.world.basic.WorldItem;

import java.nio.IntBuffer;

public class GameSound {
    private final SoundType soundType;
    private final SoundBuffer soundBuffer;
    private int source;
    private WorldItem attachedTo;
    private float volume;
    private float pitch;
    private float rollOff;

    private GameSound(@NotNull SoundBuffer soundBuffer, SoundType soundType, float pitch, float volume, float rollOff, WorldItem attachedTo) {
        this.soundBuffer = soundBuffer;
        this.soundType = soundType;
        this.attachedTo = attachedTo;
        this.source = AL10.AL_NONE;

        this.setVolume(volume);
        this.setPitch(pitch);
        this.setRollOff(rollOff);
        this.setupSound();
    }

    public static GameSound createSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff, WorldItem attachedTo) {
        if (soundBuffer == null) {
            return null;
        }
        return new GameSound(soundBuffer, soundType, pitch, gain, rollOff, attachedTo);
    }

    public static GameSound createSound(SoundBuffer soundBuffer, SoundType soundType, float pitch, float gain, float rollOff) {
        if (soundBuffer == null) {
            return null;
        }
        return new GameSound(soundBuffer, soundType, pitch, gain, rollOff, null);
    }

    private void setupSound() {
        this.source = AL10.alGenSources();

        SoundManager.checkALonErrors();
        AL10.alSourcei(this.source, AL10.AL_SOURCE_RELATIVE, this.getSoundType().getSoundData().isLocatedInWorld() ? AL10.AL_FALSE : AL10.AL_TRUE);
        AL10.alSourcei(this.source, AL10.AL_LOOPING, this.getSoundType().getSoundData().isLooped() ? AL10.AL_TRUE : AL10.AL_FALSE);
        AL10.alSourcei(this.source, AL10.AL_BUFFER, this.getSoundBuffer().getBuffer());
        AL10.alSourcef(this.source, AL10.AL_REFERENCE_DISTANCE, Math.max(Math.max(this.getVolume(), 0.0f) * 2.0f, 1.0f));
        SoundManager.checkALonErrors();

        if (this.getAttachedTo() != null) {
            this.setPosition(this.getAttachedTo().getPosition());
        } else {
            this.setPosition(new Vector3f(0.0f));
        }

        this.setVelocity(new Vector3f(0.0f, 0.0f, 0.0f));

        SoundManager.checkALonErrors();
        this.updateParams();
        SoundManager.checkALonErrors();
        SoundManager.sounds.add(this);
    }

    private void updateParams() {
        AL10.alSourcef(this.source, AL10.AL_ROLLOFF_FACTOR, this.getRollOff());
        AL10.alSourcef(this.source, AL10.AL_GAIN, this.getVolume());
        AL10.alSourcef(this.source, AL10.AL_PITCH, this.getPitch());
        SoundManager.checkALonErrors();
    }

    public void updateSound() {
        SoundManager.checkALonErrors();
        this.updateParams();
        if (this.isPaused() || this.isStopped()) {
            return;
        }
        if (this.getAttachedTo() != null) {
            this.setPosition(this.getAttachedTo().getPosition());
            if (this.getAttachedTo().isDead()) {
                this.stopSound();
                return;
            }
        }
        SoundManager.checkALonErrors();
    }

    @Override
    protected void finalize() {
        this.cleanUp();
    }

    public void setPosition(Vector3f vector3f) {
        AL10.alSource3f(this.source, AL10.AL_POSITION, vector3f.x, vector3f.y, vector3f.z);
    }

    public void setVelocity(Vector3f vector3f) {
        AL10.alSource3f(this.source, AL10.AL_VELOCITY, vector3f.x, vector3f.y, vector3f.z);
    }

    public float getRollOff() {
        return Math.max(this.rollOff, 0.0f);
    }

    public void setRollOff(float rollOff) {
        this.rollOff = rollOff;
    }

    public float getVolume() {
        return Math.max(this.volume, 0.0f);
    }

    public void setVolume(float gain) {
        this.volume = gain;
    }

    public float getPitch() {
        return Math.max(this.pitch, 0.0f);
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isPaused() {
        if (!this.isValid()) {
            return false;
        }
        return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED;
    }

    public boolean isStopped() {
        if (!this.isValid()) {
            return false;
        }
        return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_STOPPED;
    }

    public boolean isPlaying() {
        if (!this.isValid()) {
            return false;
        }
        return AL10.alGetSourcei(this.source, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public void playSound() {
        if (!this.isValid()) {
            this.setupSound();
            SoundManager.checkALonErrors();
        }
        AL10.alSourcePlay(this.source);
    }

    public void pauseSound() {
        if (this.isValid()) {
            AL10.alSourcePause(this.source);
        }
    }

    public void stopSound() {
        if (this.isValid()) {
            AL10.alSourceStop(this.source);
        }
    }

    public void cleanUp() {
        if (this.isValid()) {
            int bufferProcessed = AL10.alGetSourcei(this.source, AL10.AL_BUFFERS_PROCESSED);
            while (bufferProcessed-- > 0) {
                IntBuffer buffer = IntBuffer.allocate(1);
                AL10.alSourceUnqueueBuffers(this.source, buffer);
            }
            AL10.alSourcei(this.source, AL10.AL_BUFFER, AL10.AL_NONE);
            AL10.alDeleteSources(this.source);
            this.source = AL10.AL_NONE;
            SoundManager.checkALonErrors();
        }
    }

    public WorldItem getAttachedTo() {
        return this.attachedTo;
    }

    public void setAttachedTo(WorldItem attachedTo) {
        this.attachedTo = attachedTo;
    }

    public SoundBuffer getSoundBuffer() {
        return this.soundBuffer;
    }

    public SoundType getSoundType() {
        return this.soundType;
    }

    public boolean isValid() {
        return this.source != AL10.AL_NONE;
    }
}
