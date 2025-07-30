package com.rbee.pokedexgui.model.trainer;

import com.rbee.pokedexgui.model.item.Item;
import com.rbee.pokedexgui.model.pokemon.Pokemon;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Trainer.
 */
public class Trainer {

    private static int nextId = 1;

    private final int trainerId;
    private String name;
    private LocalDate birthdate;
    private Sex sex;
    private String hometown;
    private String description;

    private final DoubleProperty money = new SimpleDoubleProperty(1_000_000.00);

    // Pokémon slots
    private final ObservableList<Pokemon> lineup = FXCollections.observableArrayList(); // Max 6
    private final ObservableList<Pokemon> storage = FXCollections.observableArrayList(); // Overflow

    // Items
    private final ObservableList<Item> itemList = FXCollections.observableArrayList();
    private final Map<Item, Integer> itemQuantities = new HashMap<>();

    /**
     * The enum Sex.
     */
    public enum Sex {
        /**
         * Male sex.
         */
        MALE,
        /**
         * Female sex.
         */
        FEMALE
    }

    /**
     * Instantiates a new Trainer.
     *
     * @param name        the name
     * @param birthdate   the birthdate
     * @param sex         the sex
     * @param hometown    the hometown
     * @param description the description
     */
// Original constructor
    public Trainer(String name, LocalDate birthdate, Sex sex, String hometown, String description) {
        this.trainerId = nextId++;
        this.name = name;
        this.birthdate = birthdate;
        this.sex = sex;
        this.hometown = hometown;
        this.description = description;
    }

    /**
     * Instantiates a new Trainer.
     *
     * @param trainerId   the trainer id
     * @param name        the name
     * @param birthdate   the birthdate
     * @param sex         the sex
     * @param hometown    the hometown
     * @param description the description
     */
// Package-private constructor for DTO restoration
    Trainer(int trainerId, String name, LocalDate birthdate, Sex sex, String hometown, String description) {
        this.trainerId = trainerId;
        this.name = name;
        this.birthdate = birthdate;
        this.sex = sex;
        this.hometown = hometown;
        this.description = description;

        // Update nextId to prevent conflicts
        if (trainerId >= nextId) {
            nextId = trainerId + 1;
        }
    }

    /**
     * Gets next id.
     *
     * @return the next id
     */
// Static methods for managing the ID counter
    public static int getNextId() {
        return nextId;
    }

    /**
     * Sets next id.
     *
     * @param newNextId the new next id
     */
    public static void setNextId(int newNextId) {
        nextId = newNextId;
    }

    /**
     * Gets item quantities.
     *
     * @return the item quantities
     */
    public Map<Item, Integer> getItemQuantities() {
        return itemQuantities;
    }

    // Basic getters

    /**
     * Gets trainer id.
     *
     * @return the trainer id
     */
    public int getTrainerId() {
        return trainerId;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets birthdate.
     *
     * @return the birthdate
     */
    public LocalDate getBirthdate() {
        return birthdate;
    }

    /**
     * Gets sex.
     *
     * @return the sex
     */
    public Sex getSex() {
        return sex;
    }

    /**
     * Gets hometown.
     *
     * @return the hometown
     */
    public String getHometown() {
        return hometown;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets money.
     *
     * @return the money
     */
    public double getMoney() {
        return money.get();
    }

    /**
     * Sets money.
     *
     * @param value the value
     */
    public void setMoney(double value) {
        money.set(value);
    }

    /**
     * Money property double property.
     *
     * @return the double property
     */
    public DoubleProperty moneyProperty() {
        return money;
    }

    // Lineup and storage logic

    /**
     * Gets lineup.
     *
     * @return the lineup
     */
    public ObservableList<Pokemon> getLineup() {
        System.out.println("getLineup called, size = " + lineup.size());
        return lineup;
    }

    /**
     * Gets storage.
     *
     * @return the storage
     */
    public ObservableList<Pokemon> getStorage() {
        System.out.println("getStorage called, size = " + storage.size());
        return storage;
    }


    /**
     * Add pokemon boolean.
     *
     * @param pokemon the pokemon
     *
     * @return the boolean
     */
    public boolean addPokemon(Pokemon pokemon) {
        if (pokemon == null) return false;
        if (lineup.contains(pokemon) || storage.contains(pokemon)) return false;

        if (lineup.size() < 6) {
            return lineup.add(pokemon);
        } else {
            return storage.add(pokemon);
        }
    }

    /**
     * Remove pokemon boolean.
     *
     * @param pokemon the pokemon
     *
     * @return the boolean
     */
    public boolean removePokemon(Pokemon pokemon) {
        if (pokemon == null) return false;
        return lineup.remove(pokemon) || storage.remove(pokemon);
    }

    /**
     * Move to storage boolean.
     *
     * @param pokemon the pokemon
     *
     * @return the boolean
     */
    public boolean moveToStorage(Pokemon pokemon) {
        if (pokemon == null) return false;
        if (lineup.remove(pokemon)) {
            // Avoid duplicates in storage
            if (!storage.contains(pokemon)) {
                storage.add(pokemon);
            }
            return true;
        }
        return false;
    }

    /**
     * Move to lineup boolean.
     *
     * @param pokemon the pokemon
     *
     * @return the boolean
     */
    public boolean moveToLineup(Pokemon pokemon) {
        if (pokemon == null) return false;
        if (storage.contains(pokemon) && lineup.size() < 6) {
            storage.remove(pokemon);
            lineup.add(pokemon);
            return true;
        }
        return false;
    }

    /**
     * Gets lineup count.
     *
     * @return the lineup count
     */
// Returns number of Pokémon in the active lineup (max 6)
    public int getLineupCount() {
        return lineup.size();
    }

    /**
     * Gets storage count.
     *
     * @return the storage count
     */
// Returns number of Pokémon in storage (overflow)
    public int getStorageCount() {
        return storage.size();
    }

    /**
     * Gets total pokemon count.
     *
     * @return the total pokemon count
     */
// Returns total Pokémon owned (lineup + storage)
    public int getTotalPokemonCount() {
        return lineup.size() + storage.size();
    }

    // Items

    /**
     * Gets item list.
     *
     * @return the item list
     */
    public ObservableList<Item> getItemList() {
        return itemList;
    }

    /**
     * Gets item quantity.
     *
     * @param item the item
     *
     * @return the item quantity
     */
    public int getItemQuantity(Item item) {
        return itemQuantities.getOrDefault(item, 0);
    }

    /**
     * Buy item boolean.
     *
     * @param item     the item
     * @param quantity the quantity
     *
     * @return the boolean
     */
    public boolean buyItem(Item item, int quantity) {
        System.out.println("Buying for trainer hashcode: " + this.hashCode());
        if (item == null || quantity <= 0) return false;
        double totalCost = item.getBuyingPrice() * quantity;
        if (totalCost > getMoney()) return false;

        setMoney(getMoney() - totalCost);
        int oldQty = itemQuantities.getOrDefault(item, 0);
        itemQuantities.put(item, oldQty + quantity);

        if (!itemList.contains(item)) {
            itemList.add(item);
        }
        return true;
    }

    /**
     * Sell item boolean.
     *
     * @param item     the item
     * @param quantity the quantity
     *
     * @return the boolean
     */
    public boolean sellItem(Item item, int quantity) {
        if (item == null || quantity <= 0) return false;
        int currentQty = itemQuantities.getOrDefault(item, 0);
        if (currentQty < quantity) return false;

        setMoney(getMoney() + item.getSellingPrice() * quantity);
        int newQty = currentQty - quantity;

        if (newQty > 0) {
            itemQuantities.put(item, newQty);
        } else {
            itemQuantities.remove(item);
            itemList.remove(item);
        }
        return true;
    }

    /**
     * Use item boolean.
     *
     * @param item     the item
     * @param quantity the quantity
     *
     * @return the boolean
     */
    public boolean useItem(Item item, int quantity) {
        if (!itemList.contains(item)) {
            return false; // item not found
        }

        int currentQty = itemQuantities.getOrDefault(item, 0);
        if (currentQty < quantity) {
            return false; // not enough quantity
        }

        int newQty = currentQty - quantity;

        if (newQty <= 0) {
            // Remove the item completely
            itemList.remove(item);
            itemQuantities.remove(item);
        } else {
            // Update quantity map
            itemQuantities.put(item, newQty);
        }

        return true;
    }

    /**
     * Post deserialize.
     */
    public void postDeserialize() {
        for (Pokemon p : lineup) {
            if (p != null) {
                p.initializeTransientFields(); // or p.postDeserialize()
            }
        }
        for (Pokemon p : storage) {
            if (p != null) {
                p.initializeTransientFields();
            }
        }
    }



}