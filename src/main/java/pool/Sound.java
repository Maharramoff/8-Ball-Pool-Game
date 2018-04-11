package pool;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;

public class Sound
{
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
        BufferedInputStream path     = new BufferedInputStream(Sound.class.getResourceAsStream(soundDir + "" + s));
        try
        {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(path);
            Clip             clip        = AudioSystem.getClip();
            clip.open(audioStream);
            if (clip.isRunning())
                clip.stop();
            clip.setFramePosition(0);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            if (f != 0.0f)
                gainControl.setValue(f); // Reduce volume by given decibels.
            clip.start();
        }
        catch (UnsupportedAudioFileException | LineUnavailableException | IOException e)
        {
            e.printStackTrace();
        }
    }
}
