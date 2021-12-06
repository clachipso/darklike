
package clachipso.gamelib;

import javafx.scene.media.AudioClip;

import java.net.URI;
import java.nio.file.Paths;

public class Sound {
    private AudioClip clip;

    public Sound(String path) {

        try {
            clip = new AudioClip(Sound.class.getResource(path).toURI().toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        return clip.isPlaying();
    }

    public void play() {
        clip.play();
    }

    public void stop() {
        clip.stop();
    }
}
