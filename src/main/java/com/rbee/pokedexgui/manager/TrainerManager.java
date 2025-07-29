package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.trainer.Trainer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class TrainerManager {

    private final ObservableList<Trainer> trainerList = FXCollections.observableArrayList();

    public ObservableList<Trainer> getTrainerList() {
        return trainerList;
    }

    private final ObservableList<Trainer> recentAdditions = FXCollections.observableArrayList();

    public ObservableList<Trainer> getRecentAdditions() {
        return recentAdditions;
    }

    // Property to hold the active trainer (for easy binding and listeners)
    private final ObjectProperty<Trainer> activeTrainer = new SimpleObjectProperty<>();

    public TrainerManager() {
        // Existing sample trainers here...
        addTrainer(new Trainer(
                "CJ",
                LocalDate.of(2000, 1, 15),
                Trainer.Sex.MALE,
                "Pallet Town",
                "A promising new trainer."
        ));

        addTrainer(new Trainer(
                "JN",
                LocalDate.of(1998, 5, 20),
                Trainer.Sex.FEMALE,
                "New Bark Town",
                "Determined and skilled."
        ));

        addTrainer(new Trainer(
                "Rbee",
                LocalDate.of(1995, 7, 7),
                Trainer.Sex.MALE,
                "Littleroot Town",
                "Legendary in training Pokémon. Known for his unparalleled dedication and strategic mind, Rbee has traveled across regions battling gyms and collecting badges. His expertise in type matchups and Pokémon synergy makes him a formidable opponent. He constantly seeks new challenges and strives to be the very best, like no one ever was."
        ));

        addTrainer(new Trainer(
                "Charles",
                LocalDate.of(1997, 3, 10),
                Trainer.Sex.MALE,
                "Twinleaf Town",
                "Strategic and calm."
        ));

        addTrainer(new Trainer(
                "Alex",
                LocalDate.of(1999, 11, 2),
                Trainer.Sex.MALE,
                "Nuvema Town",
                "Quick learner and adaptable."
        ));
    }

    public boolean isTrainerNameUnique(String name) {
        for (Trainer t : trainerList) {
            if (t.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    public int getTotalTrainerCount() {
        return trainerList.size();
    }

    public void addTrainer(Trainer newTrainer) {
        if (newTrainer != null) {
            trainerList.add(newTrainer);
            recentAdditions.add(0, newTrainer); // Add to front

            if (recentAdditions.size() > 5) {
                recentAdditions.remove(recentAdditions.size() - 1);
            }
        }
    }

    public Trainer getActiveTrainer() {
        return activeTrainer.get();
    }

    public ObjectProperty<Trainer> activeTrainerProperty() {
        return activeTrainer;
    }

    public void setActiveTrainer(Trainer selectedTrainer) {
        if (selectedTrainer != null && trainerList.contains(selectedTrainer)) {
            activeTrainer.set(selectedTrainer);
            // You can add additional logic here if needed when active trainer changes
            System.out.println("Active trainer set to: " + selectedTrainer.getName());
        } else {
            throw new IllegalArgumentException("Selected trainer is null or not in the trainer list.");
        }
    }

}
