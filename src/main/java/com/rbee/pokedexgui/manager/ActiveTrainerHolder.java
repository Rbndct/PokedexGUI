package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.trainer.Trainer;

/**
 * The type Active trainer holder.
 */
public class ActiveTrainerHolder {
    private static Trainer activeTrainer;

    /**
     * Gets active trainer.
     *
     * @return the active trainer
     */
    public static Trainer getActiveTrainer() {
        return activeTrainer;
    }

    /**
     * Sets active trainer.
     *
     * @param trainer the trainer
     */
    public static void setActiveTrainer(Trainer trainer) {
        activeTrainer = trainer;
        System.out.println("[DEBUG] ActiveTrainerHolder set to: " + (trainer != null ? trainer.getName() : "null"));
    }
}
