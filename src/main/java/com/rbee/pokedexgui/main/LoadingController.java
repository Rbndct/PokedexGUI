package com.rbee.pokedexgui.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class LoadingController {
    @FXML
    private Label welcomeText;

    @FXML
    private Button startButton;

    private Clip clip;

    @FXML
    public void initialize() {
        playSound("/com/rbee/pokedexgui/sounds/opening-theme.wav");

        Font.loadFont(getClass().getResourceAsStream("/com/rbee/pokedexgui/fonts/PressStart2P-Regular.ttf"), 10);
        Font.loadFont(getClass().getResourceAsStream("/com/rbee/pokedexgui/fonts/VT323-Regular.ttf"), 10);

        System.out.println("[DEBUG] Forcing button visible.");
        startButton.setVisible(true); // 💥 Make sure this is true
        startButton.setDisable(false);
        startButton.toFront();        // Make sure it's above background
    }

    private void playSound(String resourcePath) {
        try {
            System.out.println("[DEBUG] LoadingController initialized!");

            System.out.println("Trying to load audio from: " + resourcePath);
            InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
            System.out.println("Audio stream is null? " + (audioSrc == null));

            if (audioSrc == null) {
                System.err.println("Audio file not found: " + resourcePath);
                return;
            }
            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);  // loop the sound

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStartButtonClick() {
        System.out.println("[DEBUG] Start button clicked!");
        stopSound(); // Optional: stop background music

        // TODO: Switch to your main menu scene here
    }

    // Call this method to stop the sound (e.g., when leaving the loading screen)
    public void stopSound() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    @FXML
    private Button muteButton;
    private boolean isMuted = false;

    @FXML
    public void toggleMute() {
        if (clip != null) {
            if (isMuted) {
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                muteButton.setText("🔊");
            } else {
                clip.stop();
                muteButton.setText("🔇");
            }
            isMuted = !isMuted;
        }
    }
}
