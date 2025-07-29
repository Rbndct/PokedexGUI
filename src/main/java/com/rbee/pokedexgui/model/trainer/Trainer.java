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

    public enum Sex {
        MALE, FEMALE
    }

    public Trainer(String name, LocalDate birthdate, Sex sex, String hometown, String description) {
        this.trainerId = nextId++;
        this.name = name;
        this.birthdate = birthdate;
        this.sex = sex;
        this.hometown = hometown;
        this.description = description;
    }

    // Basic getters

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
        return money.get();
    }

    public void setMoney(double value) {
        money.set(value);
    }

    public DoubleProperty moneyProperty() {
        return money;
    }

    // Lineup and storage logic

    public ObservableList<Pokemon> getLineup() {
        return lineup;
    }

    public ObservableList<Pokemon> getStorage() {
        return storage;
    }

    public boolean addPokemon(Pokemon pokemon) {
        if (pokemon == null) return false;
        if (lineup.contains(pokemon) || storage.contains(pokemon)) return false;

        if (lineup.size() < 6) {
            return lineup.add(pokemon);
        } else {
            return storage.add(pokemon);
        }
    }

    public boolean removePokemon(Pokemon pokemon) {
        if (pokemon == null) return false;
        return lineup.remove(pokemon) || storage.remove(pokemon);
    }

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

    public boolean moveToLineup(Pokemon pokemon) {
        if (pokemon == null) return false;
        if (storage.contains(pokemon) && lineup.size() < 6) {
            storage.remove(pokemon);
            lineup.add(pokemon);
            return true;
        }
        return false;
    }

    // Returns number of Pokémon in the active lineup (max 6)
    public int getLineupCount() {
        return lineup.size();
    }

    // Returns number of Pokémon in storage (overflow)
    public int getStorageCount() {
        return storage.size();
    }

    // Returns total Pokémon owned (lineup + storage)
    public int getTotalPokemonCount() {
        return lineup.size() + storage.size();
    }

    // Items

    public ObservableList<Item> getItemList() {
        return itemList;
    }

    public int getItemQuantity(Item item) {
        return itemQuantities.getOrDefault(item, 0);
    }

    public boolean buyItem(Item item, int quantity) {
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

    public void addItem(Item item) {
        if (item != null && !itemList.contains(item)) {
            itemList.add(item);
            itemQuantities.putIfAbsent(item, 1);
        }
    }

    public void removeItem(Item item) {
        if (itemList.contains(item)) {
            itemList.remove(item);
            itemQuantities.remove(item);
        }
    }

    public int getItemCount() {
        return itemList.size();
    }
}
