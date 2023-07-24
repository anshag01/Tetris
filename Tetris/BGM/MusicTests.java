package BGM;

import org.junit.jupiter.api.Test;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class MusicTests {

    @Test
    void testBGMClassic() throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResource("BGM" + File.separator + "653714__josefpres__8-bit-game-loop-003-simple-mix-1-long-16-bit-120-bpm.wav"));
        Clip clip = AudioSystem.getClip();
        clip.open(inputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        JOptionPane.showMessageDialog(null, "Press OK to stop playing");
        clip.close();
        assertEquals(clip.getClass().toString(), "class com.sun.media.sound.DirectAudioDevice$DirectClip");
    }

    @Test
    void testBGMArcade() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResource("BGM" + File.separator + "mixkit-game-level-music-689_0.wav"));
        Clip clip = AudioSystem.getClip();
        clip.open(inputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        JOptionPane.showMessageDialog(null, "Press OK to stop playing");
        assertEquals(clip.getClass().toString(), "class com.sun.media.sound.DirectAudioDevice$DirectClip");
    }

    @Test
    void testBGMMenu() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResource("BGM" + File.separator + "tobu.wav"));
        Clip clip = AudioSystem.getClip();
        clip.open(inputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        JOptionPane.showMessageDialog(null, "Press OK to stop playing");
        assertEquals(clip.getClass().toString(), "class com.sun.media.sound.DirectAudioDevice$DirectClip");
    }
}
