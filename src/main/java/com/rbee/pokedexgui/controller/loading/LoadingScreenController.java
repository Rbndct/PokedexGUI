package com.rbee.pokedexgui.controller.loading;


import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class LoadingScreenController {

    @FXML
    private Button startButton;

    @FXML
    private Text pokedexTitle;

    @FXML
    private Group pokedexTitleGroup;

    private Clip clip;

    @FXML
    private Text loadingMessageText;


    private final String[] loadingMessages = {
            "Scanning nearby Pokémon...",
            "Charging Thunderstone...",
            "Linking to Professor Oak’s database...",
            "Initializing Pokédex modules...",
            "Calibrating Pokéballs...",
            "Fetching trainer data...",
            "Synchronizing regions...",
            "Tuning PokéFlute..."
    };

    private static boolean soundPlayed = false;


    @FXML
    public void initialize() {
        if (!soundPlayed) {
            playSound("/com/rbee/pokedexgui/sounds/opening-theme.wav");
            soundPlayed = true;
        }
        Font.loadFont(getClass().getResourceAsStream("com/rbee/pokedexgui/fonts/PressStart2P-Regular.ttf"), 10);

        Font.loadFont(getClass().getResourceAsStream("/com/rbee/pokedexgui/fonts/VT323-Regular.ttf"), 10);

        System.out.println("[DEBUG] Forcing button visible.");
        startButton.setVisible(true); // 💥 Make sure this is true
        startButton.setDisable(false);
        startButton.toFront();        // Make sure it's above background

        applyPokedexTitleEffects();
        startLoadingMessageLoop();
    }

    private int lastIndex = -1; // Keeps track of the last message shown

    private void startLoadingMessageLoop() {
        // Pre-create fade transitions
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), loadingMessageText);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), loadingMessageText);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), event -> {
                    fadeOut.setOnFinished(e -> {
                        // Select a new message that isn't the same as the previous one
                        int randomIndex;
                        do {
                            randomIndex = new Random().nextInt(loadingMessages.length);
                        } while (randomIndex == lastIndex && loadingMessages.length > 1);
                        lastIndex = randomIndex;

                        loadingMessageText.setText(loadingMessages[randomIndex]);
                        fadeIn.play(); // Fade in the new message
                    });

                    fadeOut.play(); // Begin fade out
                }),
                new KeyFrame(Duration.seconds(3.0)) // Every 3 seconds total
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }


    private void applyPokedexTitleEffects() {
        // 🌈 Gradient fill inside the text (yellow to orange)
        Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#fff33d")),   // bright yellow top
                new Stop(0.5, Color.web("#ffca02")), // golden
                new Stop(1, Color.web("#ff8c00"))    // orange bottom
        };
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops
        );
        pokedexTitle.setFill(gradient);

        // 💡 Outer drop shadow (glow)
        DropShadow outerGlow = new DropShadow();
        outerGlow.setColor(Color.rgb(0, 0, 0, 0.7));
        outerGlow.setRadius(15);
        outerGlow.setOffsetX(3);
        outerGlow.setOffsetY(3);

        // 🕳️ Inner shadow for depth
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.rgb(0, 0, 0, 0.4));
        innerShadow.setRadius(4);
        innerShadow.setOffsetX(0);
        innerShadow.setOffsetY(2);
        innerShadow.setChoke(0.6);

        // Chain effects
        innerShadow.setInput(outerGlow);
        pokedexTitle.setEffect(innerShadow);

        // 🎞 Slide-In + Bounce Animation
        pokedexTitle.setTranslateY(80); // Start below
        Timeline slideBounce = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(pokedexTitle.translateYProperty(), 80)),
                new KeyFrame(Duration.seconds(0.6), new KeyValue(pokedexTitle.translateYProperty(), -20, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(0.9), new KeyValue(pokedexTitle.translateYProperty(), 0, Interpolator.EASE_BOTH))
        );
        slideBounce.setOnFinished(e -> startGlowPulse()); // Start pulse after bounce
        slideBounce.play();
    }

    private void startGlowPulse() {
        // 🌟 Subtle glow pulse (scale + opacity)
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(2.5), pokedexTitle);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);

        FadeTransition fade = new FadeTransition(Duration.seconds(2.5), pokedexTitle);
        fade.setFromValue(1.0);
        fade.setToValue(0.93);
        fade.setAutoReverse(true);
        fade.setCycleCount(Animation.INDEFINITE);

        pulse.play();
        fade.play();
    }



    private void playSound(String resourcePath) {
        try {
            // Stop and close any existing clip
            if (clip != null && clip.isRunning()) {
                clip.stop();
                clip.close();
            }

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

            // Set volume to 50%
            FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            // Convert 50% volume to decibels.
            // Formula: gain = 20 * log10(volumePercentage)
            float volume = 0.1f; // 50%
            float dB = (float)(20.0 * Math.log10(volume));
            volumeControl.setValue(dB); // sets volume

            clip.loop(Clip.LOOP_CONTINUOUSLY);  // Starts and loops the clip

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
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

    public void handleStartButtonClick(javafx.event.ActionEvent actionEvent) {
        System.out.println("[DEBUG] Start button clicked!");
        stopSound(); // Optional: stop background music

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rbee/pokedexgui/view/main/MainView.fxml"));
            Parent mainRoot = loader.load();

            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(mainRoot);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
