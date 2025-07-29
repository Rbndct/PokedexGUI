package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.pokemon.Pokemon;
import com.rbee.pokedexgui.model.trainer.Trainer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TrainerManager {

    private  final ObservableList <Trainer> trainerList = javafx.collections.FXCollections.observableArrayList();

    public ObservableList<Trainer> getTrainerList() {
        return  trainerList;
    }

    private final ObservableList <Trainer> recentAdditions = FXCollections.observableArrayList();

    public ObservableList < Trainer > getRecentAdditions() {
        return recentAdditions;
    }

//    public trainerManager() {
//
//    }

    public  boolean isTrainerNameUnique(String name) {
        for (Trainer t : trainerList) {
            if (t.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    public  int getTotalTrainerCount() {
        return trainerList.size();
    }

    public  void addTrainer(Trainer newTrainer) {
        if (newTrainer != null) {
            trainerList.add(newTrainer);
            recentAdditions.add(0, newTrainer); // Add to front

            if (recentAdditions.size() > 5) {
                recentAdditions.remove(recentAdditions.size() - 1);
            }
        }
    }
}
