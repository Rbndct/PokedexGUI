package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.item.Item;
import com.rbee.pokedexgui.model.move.Move;
import com.rbee.pokedexgui.model.pokemon.Pokemon;
import com.rbee.pokedexgui.model.trainer.Trainer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;


/**
 * The type Trainer manager.
 */
public class TrainerManager {
    private static TrainerManager instance;




    private final ObservableList<Trainer> trainerList = FXCollections.observableArrayList();


    /**
     * Gets trainer list.
     *
     * @return the trainer list
     */
    public ObservableList<Trainer> getTrainerList() {
        return trainerList;
    }

    private final ObservableList<Trainer> recentAdditions = FXCollections.observableArrayList();

    /**
     * Gets recent additions.
     *
     * @return the recent additions
     */
    public ObservableList<Trainer> getRecentAdditions() {
        return recentAdditions;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static TrainerManager getInstance() {
        if (instance == null) {
            instance = new TrainerManager();
        }
        return instance;
    }

    // Property to hold the active trainer (for easy binding and listeners)
    private final ObjectProperty<Trainer> activeTrainer = new SimpleObjectProperty<>();

    /**
     * Instantiates a new Trainer manager.
     */
    public TrainerManager() {

    }


    /**
     * Is trainer name unique boolean.
     *
     * @param name the name
     *
     * @return the boolean
     */
    public boolean isTrainerNameUnique(String name) {
        for (Trainer t : trainerList) {
            if (t.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets total trainer count.
     *
     * @return the total trainer count
     */
    public int getTotalTrainerCount() {
        return trainerList.size();
    }

    /**
     * Add trainer.
     *
     * @param newTrainer the new trainer
     */
    public void addTrainer(Trainer newTrainer) {
        if (newTrainer != null) {
            trainerList.add(newTrainer);
            recentAdditions.add(0, newTrainer); // Add to front

            if (recentAdditions.size() > 5) {
                recentAdditions.remove(recentAdditions.size() - 1);
            }
        }
    }

    /**
     * Gets active trainer.
     *
     * @return the active trainer
     */
    public Trainer getActiveTrainer() {
        return activeTrainer.get();
    }

    /**
     * Active trainer property object property.
     *
     * @return the object property
     */
    public ObjectProperty<Trainer> activeTrainerProperty() {
        return activeTrainer;
    }

    /**
     * Sets active trainer.
     *
     * @param selectedTrainer the selected trainer
     */
    public void setActiveTrainer(Trainer selectedTrainer) {
        if (selectedTrainer != null && trainerList.contains(selectedTrainer)) {
            activeTrainer.set(selectedTrainer);
            // You can add additional logic here if needed when active trainer changes
            System.out.println("Active trainer set to: " + selectedTrainer.getName());
        } else {
            throw new IllegalArgumentException("Selected trainer is null or not in the trainer list.");
        }
    }












    private void debugLoadedTrainerPokemon(List<Trainer> trainers) {
        int totalPokemon = 0;
        for (Trainer trainer : trainers) {
            int lineupCount = trainer.getLineup() != null ? trainer.getLineup().size() : 0;
            int storageCount = trainer.getStorage() != null ? trainer.getStorage().size() : 0;
            totalPokemon += lineupCount + storageCount;

            System.out.println("👤 Trainer: " + trainer.getName() +
                    " | Lineup: " + lineupCount +
                    " | Storage: " + storageCount);
        }
        System.out.println("🐾 Total Pokémon loaded from all trainers: " + totalPokemon);
    }





    private void updateRecentAdditions() {
        recentAdditions.clear();
        int startIndex = Math.max(0, trainerList.size() - 5);
        for (int i = trainerList.size() - 1; i >= startIndex; i--) {
            recentAdditions.add(trainerList.get(i));
        }
    }

    /**
     * Create trainer one.
     *
     * @param pokemonManager the pokemon manager
     * @param itemManager    the item manager
     * @param moveManager    the move manager
     */
    public void createTrainerOne(
            PokemonManager pokemonManager,
            ItemManager itemManager,
            MoveManager moveManager) {

        // Create Trainer
        Trainer trainer = new Trainer(
                "Ash Ketchum",
                LocalDate.of(1990, 5, 22),
                Trainer.Sex.MALE,
                "Pallet Town",
                "A passionate Pokémon trainer."
        );

        // Get Pokémon references
        Pokemon pikachu = pokemonManager.getByNumber(25);    // Pikachu (evolves by stone)
        Pokemon bulbasaur = pokemonManager.getByNumber(1);   // Bulbasaur (evolves by level)
        Pokemon charmander = pokemonManager.getByNumber(4);
        Pokemon squirtle = pokemonManager.getByNumber(7);
        Pokemon eevee = pokemonManager.getByNumber(133);

        // Add moves to Pikachu from MoveManager's moveList
        Move hmStrength = moveManager.getMoveByName("Strength");
        Move hmRockSmash = moveManager.getMoveByName("Rock Smash");
        Move tmTackle = moveManager.getMoveByName("Tackle");
        Move tmFlamethrower = moveManager.getMoveByName("Flamethrower");

        // Defensive null checks (in case any move isn't found)
        if (hmStrength != null) pikachu.getMoveSet().add(hmStrength);
        if (hmRockSmash != null) pikachu.getMoveSet().add(hmRockSmash);
        if (tmTackle != null) pikachu.getMoveSet().add(tmTackle);
        if (tmFlamethrower != null) pikachu.getMoveSet().add(tmFlamethrower);

        // Add lineup Pokémon (max 6 allowed, here 5)
        trainer.getLineup().addAll(pikachu, bulbasaur, charmander, squirtle, eevee);

        // Add 5 Pokémon to storage
        trainer.getStorage().addAll(
                pokemonManager.getByNumber(2),  // Ivysaur
                pokemonManager.getByNumber(3),  // Venusaur
                pokemonManager.getByNumber(5),  // Charmeleon
                pokemonManager.getByNumber(6),  // Charizard
                pokemonManager.getByNumber(8)   // Wartortle
        );

        // Add items from ItemManager by name
        Item rareCandy = itemManager.getItemByName("Rare Candy");
        Item fireStone = itemManager.getItemByName("Fire Stone");
        Item waterStone = itemManager.getItemByName("Water Stone");
        Item thunderStone = itemManager.getItemByName("Thunder Stone");
        Item sunStone = itemManager.getItemByName("Sun Stone");
        Item calcium = itemManager.getItemByName("Calcium");
        Item protein = itemManager.getItemByName("Protein");
        Item iron = itemManager.getItemByName("Iron");
        Item carbos = itemManager.getItemByName("Carbos");

        trainer.getItemList().addAll(
                rareCandy, fireStone, waterStone, thunderStone, sunStone,
                calcium, protein, iron, carbos
        );

        // Set quantities for items
        trainer.getItemQuantities().put(rareCandy, 5);
        trainer.getItemQuantities().put(fireStone, 1);
        trainer.getItemQuantities().put(waterStone, 1);
        trainer.getItemQuantities().put(thunderStone, 1);
        trainer.getItemQuantities().put(sunStone, 1);
        trainer.getItemQuantities().put(calcium, 3);
        trainer.getItemQuantities().put(protein, 2);
        trainer.getItemQuantities().put(iron, 4);
        trainer.getItemQuantities().put(carbos, 2);

        trainerList.add(trainer);
    }






}
