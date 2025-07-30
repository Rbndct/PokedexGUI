package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.item.Item;
import com.rbee.pokedexgui.model.move.Move;
import com.rbee.pokedexgui.model.pokemon.Pokemon;
import com.rbee.pokedexgui.model.trainer.Trainer;
import com.rbee.pokedexgui.model.trainer.Trainer.Sex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
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

    public TrainerManager() {
        // Create Trainers
        Trainer ash = new Trainer("Ash Ketchum", LocalDate.of(1990, 5, 22), Sex.MALE, "Pallet Town", "Determined to become a Pokémon Master.");
        Trainer lyra = new Trainer("Lyra Oakley", LocalDate.of(1993, 4, 17), Sex.FEMALE, "New Bark Town", "A cheerful trainer who loves exploring Johto.");
        Trainer brendan = new Trainer("Brendan Birch", LocalDate.of(1992, 11, 3), Sex.MALE, "Littleroot Town", "Son of a Pokémon Professor, eager to prove himself.");
        Trainer dawn = new Trainer("Dawn Berlitz", LocalDate.of(1995, 9, 9), Sex.FEMALE, "Twinleaf Town", "A stylish coordinator with a strong sense of adventure.");
        Trainer turo = new Trainer("Turo Nieve", LocalDate.of(1985, 2, 1), Sex.MALE, "Mesagoza", "A professor from Paldea with deep knowledge of ancient Pokémon.");

        addTrainer(ash);
        addTrainer(lyra);
        addTrainer(brendan);
        addTrainer(dawn);
        addTrainer(turo);

        List<Pokemon> masterPokemonList = PokemonManager.getInstance().getPokemonList();

        assignFivePokemonWithStarter(ash, masterPokemonList);
        assignItemsToTrainer(ash);

        assignFivePokemonWithStarter(lyra, masterPokemonList);
        assignItemsToTrainer(lyra);

        assignFivePokemonWithStarter(brendan, masterPokemonList);
        assignItemsToTrainer(brendan);

        assignFivePokemonWithStarter(dawn, masterPokemonList);
        assignItemsToTrainer(dawn);

        assignFivePokemonWithStarter(turo, masterPokemonList);
        assignItemsToTrainer(turo);
    }


    private void assignFivePokemonWithStarter(Trainer trainer, List<Pokemon> masterList) {
        if (masterList.size() < 5) {
            System.out.println("Not enough Pokémon to assign.");
            return;
        }

        // Starter base forms dex numbers
        List<Integer> starterDexNumbers = Arrays.asList(4, 7, 1);
        Collections.shuffle(starterDexNumbers);

        Pokemon starter = null;
        for (Integer dexNum : starterDexNumbers) {
            starter = masterList.stream()
                .filter(p -> p.getPokedexNumber() == dexNum)
                .findFirst()
                .map(Pokemon::new)
                .orElse(null);
            if (starter != null) break;
        }

        if (starter == null) {
            System.out.println("Starter Pokémon not found!");
            return;
        }

        addHMsToStarter(starter);
        trainer.getLineup().add(starter);

        // Stone evolution base forms
        List<Pokemon> stoneEvoCandidates = masterList.stream()
            .filter(p -> p.getPokemonEvolutionInfo() != null && p.getPokemonEvolutionInfo().isEvolvesByStone())
            .filter(p -> p.getPokemonEvolutionInfo().getEvolvesFromNumber() == 0) // base form only
            .map(Pokemon::new)
            .collect(Collectors.toList());

        Collections.shuffle(stoneEvoCandidates);

        Pokemon stoneEvo = null;
        if (!stoneEvoCandidates.isEmpty()) {
            stoneEvo = stoneEvoCandidates.get(0);
            trainer.getLineup().add(stoneEvo);
        } else {
            System.out.println("No stone evolution Pokémon found.");
        }

        // Collect all dex numbers from evolution lines of starter and stone evo
        Set<Integer> excludedDexNumbers = new HashSet<>();
        excludedDexNumbers.addAll(getFullEvolutionLineDexNumbers(starter, masterList));
        if (stoneEvo != null) {
            excludedDexNumbers.addAll(getFullEvolutionLineDexNumbers(stoneEvo, masterList));
        }

        // Remove all excluded dex numbers from remaining Pokémon
        List<Pokemon> remainingPokemon = new ArrayList<>(masterList);
        remainingPokemon.removeIf(p -> excludedDexNumbers.contains(p.getPokedexNumber()));

        Collections.shuffle(remainingPokemon);

        // Fill lineup to 5 Pokémon total
        while (trainer.getLineup().size() < 5 && !remainingPokemon.isEmpty()) {
            trainer.getLineup().add(new Pokemon(remainingPokemon.remove(0)));
        }

        System.out.println("Assigned starter (with HMs), stone evolution, and other Pokémon to " + trainer.getName());

        // Collect all dex numbers from lineup evolution lines (to exclude from storage)
        Set<Integer> storageExclusions = new HashSet<>();
        for (Pokemon p : trainer.getLineup()) {
            storageExclusions.addAll(getFullEvolutionLineDexNumbers(p, masterList));
        }

// Prepare storage candidates excluding all lineup evolution lines
        List<Pokemon> storageCandidates = masterList.stream()
            .filter(p -> !storageExclusions.contains(p.getPokedexNumber()))
            .map(Pokemon::new) // clone
            .collect(Collectors.toList());

        Collections.shuffle(storageCandidates);

// Fill storage with at least 5 Pokémon
        while (trainer.getStorage().size() < 5 && !storageCandidates.isEmpty()) {
            trainer.getStorage().add(storageCandidates.remove(0));
        }

        System.out.println("Assigned storage Pokémon to " + trainer.getName());

    }

    // Helper: Given a Pokémon, find all Dex numbers in its full evolution line (both pre and post evolutions)
    private Set<Integer> getFullEvolutionLineDexNumbers(Pokemon pokemon, List<Pokemon> masterList) {
        Set<Integer> evolutionLine = new HashSet<>();
        Queue<Integer> toCheck = new LinkedList<>();

        evolutionLine.add(pokemon.getPokedexNumber());
        toCheck.add(pokemon.getPokedexNumber());

        while (!toCheck.isEmpty()) {
            int currentDex = toCheck.poll();

            // Find Pokémon whose preEvolutionId == currentDex (next evolutions)
            List<Integer> nextEvos = masterList.stream()
                .filter(p -> p.getPokemonEvolutionInfo() != null)
                .filter(p -> p.getPokemonEvolutionInfo().getEvolvesFromNumber() == currentDex)
                .map(Pokemon::getPokedexNumber)
                .collect(Collectors.toList());

            for (Integer nextDex : nextEvos) {
                if (!evolutionLine.contains(nextDex)) {
                    evolutionLine.add(nextDex);
                    toCheck.add(nextDex);
                }
            }

            // Find Pokémon whose Dex number == currentDex and add their preEvolution (if any)
            masterList.stream()
                .filter(p -> p.getPokedexNumber() == currentDex)
                .findFirst()
                .ifPresent(p -> {
                    int preEvo = p.getPokemonEvolutionInfo() != null ? p.getPokemonEvolutionInfo().getEvolvesFromNumber() : 0;
                    if (preEvo != 0 && !evolutionLine.contains(preEvo)) {
                        evolutionLine.add(preEvo);
                        toCheck.add(preEvo);
                    }
                });
        }
        return evolutionLine;
    }



    private void addHMsToStarter(Pokemon starter) {
        MoveManager moveManager = MoveManager.getInstance();
        ObservableList<Move> allMoves = moveManager.getMoveList();

        String primaryType = starter.getPrimaryType();
        String secondaryType = starter.getSecondaryType();

        List<Move> candidateHMs = allMoves.stream()
            .filter(m -> m.getClassification() == Move.Classification.HM)
            .filter(m -> {
                String moveType = m.getPrimaryType();
                if (moveType == null) return false;
                // Check if move type matches starter's primary or secondary type
                if (moveType.equalsIgnoreCase(primaryType)) return true;
                if (secondaryType != null && !secondaryType.isEmpty() && moveType.equalsIgnoreCase(secondaryType)) return true;
                return false;
            })
            .collect(Collectors.toList());

        int added = 0;
        for (Move hm : candidateHMs) {
            boolean alreadyHas = starter.getMoveSet().stream()
                .anyMatch(m -> m.getName().equalsIgnoreCase(hm.getName()));
            if (!alreadyHas) {
                starter.getMoveSet().add(new Move(hm)); // clone move
                added++;
                if (added >= 2) break; // add only two HMs
            }
        }

        System.out.println("Added " + added + " HM moves to starter " + starter.getName());
    }

    public void assignItemsToTrainer(Trainer trainer) {
        List<Item> allItems = new ArrayList<>(ItemManager.getInstance().getItemList());
        if (allItems.size() < 9) {
            System.out.println("Not enough items to assign.");
            return;
        }

        // Make sure Rare Candy is included
        Optional<Item> rareCandyOpt = allItems.stream()
            .filter(i -> i.getName().equalsIgnoreCase("Rare Candy"))
            .findFirst();

        if (!rareCandyOpt.isPresent()) {
            System.out.println("Rare Candy not found in items!");
            return;
        }

        Item rareCandy = rareCandyOpt.get();
        allItems.remove(rareCandy);

        Collections.shuffle(allItems);

        // Add rare candy first
        trainer.addItem(rareCandy, 49); // Assuming addItem(Item, quantity)

        // Add other 8 unique items
        for (int i = 0; i < 8; i++) {
            trainer.addItem(allItems.get(i), 1);
        }

        System.out.println("Assigned 9 unique items including Rare Candy to " + trainer.getName());
    }



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
