import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class sound {
    public static void playSound(String soundFileName) {
        try {
            // Open an audio input stream.
            File soundFile = new File("src/Everything/" + soundFileName);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);

            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();

            // Open the clip and load samples from the audio input stream.
            clip.open(audioIn);

            // Play the sound clip.
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
