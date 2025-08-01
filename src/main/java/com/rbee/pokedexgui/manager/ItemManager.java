package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.item.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;

/**
 * The type Item manager.
 */
@SuppressWarnings("squid:S106")
public class ItemManager {

    private static volatile ItemManager instance; // Thread-safe singleton

    private final ObservableList<Item> itemList = FXCollections.observableArrayList();

    // Private constructor for singleton
    private ItemManager() {
        populateInitialItems();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
// Thread-safe singleton accessor
    public static ItemManager getInstance() {
        if (instance == null) {
            synchronized (ItemManager.class) {
                if (instance == null) {
                    instance = new ItemManager();
                }
            }
        }
        return instance;
    }


    /**
     * Populate initial items.
     */
    public void populateInitialItems() {
        // Vitamins (boost EVs)
        itemList.add(new Item("Calcium", "Vitamin", "A nutritious drink for Pokémon.",
                "+10 Special Attack EVs", 10000, 5000, 10));
        itemList.add(new Item("Carbos", "Vitamin", "A nutritious drink for Pokémon.",
                "+10 Speed EVs", 10000, 5000, 10));
        itemList.add(new Item("HP Up", "Vitamin", "A nutritious drink for Pokémon.",
                "+10 HP EVs", 10000, 5000, 10));
        itemList.add(new Item("Iron", "Vitamin", "A nutritious drink for Pokémon.",
                "+10 Defense EVs", 10000, 5000, 10));
        itemList.add(new Item("Protein", "Vitamin", "A nutritious drink for Pokémon.",
                "+10 Attack EVs", 10000, 5000, 10));
        itemList.add(new Item("Zinc", "Vitamin", "A nutritious drink for Pokémon.",
                "+10 Special Defense EVs", 10000, 5000, 10));

        // Feathers (slightly increase EVs)
        itemList.add(new Item("Health Feather", "Feather", "Slightly increases HP.",
                "+1 HP EV", 300, 150, 10));
        itemList.add(new Item("Muscle Feather", "Feather", "Slightly increases Attack.",
                "+1 Attack EV", 300, 150, 10));
        itemList.add(new Item("Resist Feather", "Feather", "Slightly increases Defense.",
                "+1 Defense EV", 300, 150, 10));
        itemList.add(new Item("Swift Feather", "Feather", "Slightly increases Speed.",
                "+1 Speed EV", 300, 150, 10));
        itemList.add(new Item("Genius Feather", "Feather", "Slightly increases Special Attack.",
                "+1 Special Attack EV", 300, 150, 10));
        itemList.add(new Item("Clever Feather", "Feather", "Slightly increases Special Defense.",
                "+1 Special Defense EV", 300, 150, 10));

        // Other
        itemList.add(new Item("Rare Candy", "Leveling Item", "A candy packed with energy.",
                "Increases level by 1", 10000, 2400, 10));

        // Evolution Stones
        itemList.add(new Item("Fire Stone", "Evolution Stone", "Radiates heat.",
                "Evolves Vulpix, Growlithe, Eevee, etc.", 3000, 1500, 10));
        itemList.add(new Item("Water Stone", "Evolution Stone", "Blue, watery appearance.",
                "Evolves Poliwhirl, Shellder, Eevee, etc.", 3000, 1500, 10));
        itemList.add(new Item("Thunder Stone", "Evolution Stone", "Sparkles with electricity.",
                "Evolves Pikachu, Eelektrik, Eevee, etc.", 3000, 1500, 10));
        itemList.add(new Item("Leaf Stone", "Evolution Stone", "Leaf pattern.",
                "Evolves Gloom, Weepinbell, Exeggcute etc.", 3000, 1500, 10));
        itemList.add(new Item("Moon Stone", "Evolution Stone", "Glows faintly.",
                "Evolves Nidorina, Clefairy, Jigglypuff, etc.", -1, 1500, 10));
        itemList.add(new Item("Sun Stone", "Evolution Stone", "Glows like the sun.",
                "Evolves Gloom, Sunkern, Cottonee, etc.", 3000, 1500, 10));
        itemList.add(new Item("Shiny Stone", "Evolution Stone", "Sparkles brightly.",
                "Evolves Togetic, Roselia, Minccino, etc.", 3000, 1500, 10));
        itemList.add(new Item("Dusk Stone", "Evolution Stone", "Ominous appearance.",
                "Evolves Murkrow, Misdreavus, Doublade, etc.", 3000, 1500, 10));
        itemList.add(new Item("Dawn Stone", "Evolution Stone", "Sparkles like the morning sky.",
                "Evolves male Kirlia and female Snorunt.", 3000, 1500, 10));
        itemList.add(new Item("Ice Stone", "Evolution Stone", "Cold to the touch.",
                "Evolves Alolan Vulpix, etc.", 3000, 1500, 10));
    }

    /**
     * Gets item list.
     *
     * @return the item list
     */
    public ObservableList<Item> getItemList() {
        return itemList;
    }

    /**
     * Gets item by name.
     *
     * @param itemName the item name
     *
     * @return the item by name
     */
    public Item getItemByName(String itemName) {
        if (itemName == null || itemName.isEmpty()) {
            return null;
        }
        for (Item item : itemList) {
            if (itemName.equalsIgnoreCase(item.getName())) {
                return item;
            }
        }
        return null; // Not found
    }
}
