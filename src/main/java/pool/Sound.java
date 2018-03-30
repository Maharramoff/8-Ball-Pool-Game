package pool;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;

public class Sound
{
    public void play(String s)
    {
        String              soundDir = "/sounds/";
        BufferedInputStream path     = new BufferedInputStream(Sound.class.getResourceAsStream(soundDir + "" + s));
        try
        {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(path);
            Clip             clip        = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        }
        catch (UnsupportedAudioFileException | LineUnavailableException | IOException e)
        {
            e.printStackTrace();
        }
    }
}
