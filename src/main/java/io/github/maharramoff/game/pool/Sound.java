package io.github.maharramoff.game.pool;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public final class Sound
{
    public static final float HIGH_VOLUME = 1.0f;
    public static final float MEDIUM_VOLUME = -3.0f;
    public static final float LOW_VOLUME = -6.0f;
    public static final float VERY_LOW_VOLUME = -10.0f;
    public static final float DEFAULT_VOLUME = -20.0f;

    Logger log = Logger.getLogger("Sound");

    public void play(String s)
    {
        getPlay(s, 0.0f);
    }

    public void play(String s, Float f)
    {
        getPlay(s, f);
    }

    private void getPlay(String s, float f)
    {
        String              soundDir = "/sounds/";
        BufferedInputStream path     = new BufferedInputStream(Objects.requireNonNull(Sound.class.getResourceAsStream(soundDir + s)));
        try
        {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(path);
            Clip             clip        = AudioSystem.getClip();
            clip.open(audioStream);
            if (clip.isRunning())
            {
                clip.stop();
            }
            clip.setFramePosition(0);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            if (f != 0.0f)
            {
                gainControl.setValue(f); // Reduce volume by given decibels.
            }
            clip.start();
        }
        catch (UnsupportedAudioFileException | LineUnavailableException | IOException e)
        {
            log.severe(e.getMessage());
        }
    }
}
