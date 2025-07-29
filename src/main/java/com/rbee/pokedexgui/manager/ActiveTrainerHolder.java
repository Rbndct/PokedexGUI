package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.trainer.Trainer;

public class ActiveTrainerHolder {
    private static Trainer activeTrainer;

    public static Trainer getActiveTrainer() {
        return activeTrainer;
    }

    public static void setActiveTrainer(Trainer trainer) {
        activeTrainer = trainer;
        System.out.println("[DEBUG] ActiveTrainerHolder set to: " + (trainer != null ? trainer.getName() : "null"));
    }
}
