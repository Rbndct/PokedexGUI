package com.rbee.pokedexgui.model.trainer;

import java.time.LocalDate;

/**
 * Represents a Pokémon Trainer with attributes and auto-incremented unique ID.
 */
public class Trainer {

    // Static counter to generate unique Trainer IDs automatically
    private static int nextId = 1;

    // Attributes
    private final int trainerId;       // Auto-incremented unique ID
    private String name;
    private LocalDate birthdate;       // Best type to work with JavaFX DatePicker
    private Sex sex;                   // Enum for stronger type safety and UI binding ease
    private String hometown;           // Could be linked to ComboBox options externally
    private String description;
    private final double money;        // Fixed initial funds (₱1,000,000.00)

    /**
     * Sex enum for strong typing and easier ComboBox binding
     */
    public enum Sex {
        MALE, FEMALE, OTHER
    }

    /**
     * Constructor for Trainer.
     * The trainerId is auto-assigned to ensure uniqueness.
     *
     * @param name       Trainer's name
     * @param birthdate  Trainer's birthdate (use LocalDate to align with DatePicker)
     * @param sex        Trainer's sex (Enum)
     * @param hometown   Trainer's hometown (free-text or validated from ComboBox options)
     * @param description Trainer's description or notes
     */
    public Trainer(String name, LocalDate birthdate, Sex sex, String hometown, String description) {
        this.trainerId = nextId++;
        this.name = name;
        this.birthdate = birthdate;
        this.sex = sex;
        this.hometown = hometown;
        this.description = description;
        this.money = 1_000_000.00;  // Initial fixed funds
    }

    // Getters

    public int getTrainerId() {
        return trainerId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public Sex getSex() {
        return sex;
    }

    public String getHometown() {
        return hometown;
    }

    public String getDescription() {
        return description;
    }

    public double getMoney() {
        return money;
    }


}