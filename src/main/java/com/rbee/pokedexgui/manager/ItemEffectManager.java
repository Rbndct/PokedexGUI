package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.item.Item;
import com.rbee.pokedexgui.model.pokemon.Pokemon;
import com.rbee.pokedexgui.util.PokemonConstants;

/**
 * The type Item effect manager.
 */
public class ItemEffectManager {

    private static ItemEffectManager instance;

    private ItemEffectManager() {
        // private constructor for singleton
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ItemEffectManager getInstance() {
        if (instance == null) {
            instance = new ItemEffectManager();
        }
        return instance;
    }

    /**
     * Applies the effect of the given item on the target Pokémon.
     * Returns true if the item effect was successfully applied, false otherwise.
     *
     * @param item    the item
     * @param pokemon the pokemon
     *
     * @return the boolean
     */
    public boolean applyItemEffect(Item item, Pokemon pokemon) {
        if (item == null || pokemon == null) return false;

        String category = item.getCategory();
        String name = item.getName();

        switch (category) {
            case "Vitamin":
            case "Feather":
                return applyEVBoost(item, pokemon);

            case "Leveling Item":
                if ("Rare Candy".equalsIgnoreCase(name)) {
                    return applyRareCandy(pokemon);
                }
                break;

            case "Evolution Stone":
                return applyEvolutionStone(item, pokemon);


            default:
                // Unknown or unsupported item category
                return false;
        }

        return false;
    }

    private boolean applyEVBoost(Item item, Pokemon pokemon) {
        String effect = item.getEffect();
        System.out.println("Applying EV Boost: " + effect + " to " + pokemon.getName());

        try {
            String[] parts = effect.split(" ");  // ["+10", "Speed", "EVs"]
            int boostAmount = Integer.parseInt(parts[0].replace("+", ""));
            String statName = parts[1].toLowerCase();

            switch (statName) {
                case "hp":
                    pokemon.setBaseHP(pokemon.getBaseHP() + boostAmount);
                    break;
                case "attack":
                    pokemon.setBaseAttack(pokemon.getBaseAttack() + boostAmount);
                    break;
                case "defense":
                    pokemon.setBaseDefense(pokemon.getBaseDefense() + boostAmount);
                    break;
                case "special":
                case "specialattack":
                case "special-attack":
                case "special_attack":
                    pokemon.setBaseSpecialAttack(pokemon.getBaseSpecialAttack() + boostAmount);
                    break;
                case "specialdefense":
                case "special-defense":
                case "special_defense":
                    pokemon.setBaseSpecialDefense(pokemon.getBaseSpecialDefense() + boostAmount);
                    break;
                case "speed":
                    pokemon.setBaseSpeed(pokemon.getBaseSpeed() + boostAmount);
                    break;
                default:
                    System.err.println("Unknown stat to boost: " + statName);
                    return false;
            }

            System.out.println("Boost applied successfully.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    private boolean applyRareCandy(Pokemon pokemon) {
        int currentLevel = pokemon.getCurrentLevel();
        if (currentLevel < PokemonConstants.MAX_LEVEL) {
            int newLevel = currentLevel + 1;
            pokemon.setCurrentLevel(newLevel);
            levelUpStats(pokemon);
            System.out.println(pokemon.getName() + " leveled up to " + newLevel);

            // Check if Pokémon can evolve by level up and if new level meets requirement
            if (pokemon.canEvolveByLevelUp() && newLevel >= pokemon.getRequiredLevelForLevelEvolution()) {
                pokemon.evolveByLevelUp();
                System.out.println(pokemon.getName() + " has evolved by leveling up!");
            }
            return true;
        }
        System.out.println(pokemon.getName() + " is already at max level.");
        return false;
    }



    private void levelUpStats(Pokemon pokemon) {
        // Get current base stats
        int hp = pokemon.getBaseHP();
        int attack = pokemon.getBaseAttack();
        int defense = pokemon.getBaseDefense();
        int spAttack = pokemon.getBaseSpecialAttack();
        int spDefense = pokemon.getBaseSpecialDefense();
        int speed = pokemon.getBaseSpeed();

        // Increase each by 10%, rounding down by casting to int
        hp += (int)(hp * 0.10);
        attack += (int)(attack * 0.10);
        defense += (int)(defense * 0.10);
        spAttack += (int)(spAttack * 0.10);
        spDefense += (int)(spDefense * 0.10);
        speed += (int)(speed * 0.10);

        // Update the Pokémon's base stats with new values
        pokemon.setBaseHP(hp);
        pokemon.setBaseAttack(attack);
        pokemon.setBaseDefense(defense);
        pokemon.setBaseSpecialAttack(spAttack);
        pokemon.setBaseSpecialDefense(spDefense);
        pokemon.setBaseSpeed(speed);
    }




    private boolean applyEvolutionStone(Item item, Pokemon pokemon) {
        if (item == null || pokemon == null) return false;

        // Remove current held item if any
        if (pokemon.getHeldItem() != null) {
            System.out.println(pokemon.getName() + " dropped " + pokemon.getHeldItem().getName());
            // Optionally handle the dropped item (e.g., add back to inventory)
        }

        // Set new held item (replace existing)
        pokemon.setHeldItem(item);
        System.out.println(pokemon.getName() + " is now holding " + item.getName());

        // Try to evolve using this stone
        String stoneName = item.getName().toLowerCase();

        if (pokemon.canEvolveWithStone(stoneName)) {
            pokemon.evolveWithStone(stoneName);
            System.out.println(pokemon.getName() + " has evolved using " + item.getName());

            // Consume the stone after evolution
            pokemon.setHeldItem(null);
            System.out.println(item.getName() + " was consumed.");

            return true;
        }

        // No evolution occurred, stone remains held
        return false;
    }




}
