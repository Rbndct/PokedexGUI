package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.pokemon.Pokemon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service class that manages Pokémon data.
 */
public class PokemonManager {

    private final ObservableList < Pokemon > pokemonList = FXCollections.observableArrayList();

    public ObservableList < Pokemon > getPokemonList() {
        return pokemonList;
    }
    private static final Image PLACEHOLDER_ICON;



    static {
        InputStream iconStream = PokemonManager.class.getResourceAsStream(
                "/com/rbee/pokedexgui/images/unknown-pokemon.png"
        );
        if (iconStream != null) {
            PLACEHOLDER_ICON = new Image(iconStream, 40, 40, true, true);
        } else {
            System.err.println("❌ Placeholder image missing! Using fallback.");
            PLACEHOLDER_ICON = new Image("https://via.placeholder.com/40"); // backup online image
        }
    }

    private final ObservableList < Pokemon > recentAdditions = FXCollections.observableArrayList();

    public ObservableList < Pokemon > getRecentAdditions() {
        return recentAdditions;
    }

    public PokemonManager() {
        // 5 Starters
        addPokemon(new Pokemon(
                1, "Bulbasaur", "Grass", "Poison",
                new Pokemon.PokemonStats(45, 49, 49, 65, 65, 45),
                new Pokemon.PokemonEvolutionInfo(0, 2, 16),
                ""
        ));

        addPokemon(new Pokemon(
                2, "Ivysaur", "Grass", "Poison",
                new Pokemon.PokemonStats(60, 62, 63, 80, 80, 60),
                new Pokemon.PokemonEvolutionInfo(1, 3, 32),  // prev: Bulbasaur, next: Venusaur lvl 32
                ""
        ));

        addPokemon(new Pokemon(
                3, "Venusaur", "Grass", "Poison",
                new Pokemon.PokemonStats(80, 82, 83, 100, 100, 80),
                new Pokemon.PokemonEvolutionInfo(2, 0, 0),  // prev: Ivysaur, no next evo
                ""
        ));



        addPokemon(new Pokemon(
                4, "Charmander", "Fire", "",
                new Pokemon.PokemonStats(39, 52, 43, 60, 50, 65),
                new Pokemon.PokemonEvolutionInfo(0, 5, 16),
                ""
        ));

        addPokemon(new Pokemon(
                6, "Charizard", "Fire", "Flying",
                new Pokemon.PokemonStats(78, 84, 78, 109, 85, 100),
                new Pokemon.PokemonEvolutionInfo(5, 0, 0),  // prev: Charmeleon, no next evo
                ""
        ));

        addPokemon(new Pokemon(
                7, "Squirtle", "Water", "",
                new Pokemon.PokemonStats(44, 48, 65, 50, 64, 43),
                new Pokemon.PokemonEvolutionInfo(0, 8, 16),
                ""
        ));

        addPokemon(new Pokemon(
                152, "Chikorita", "Grass", "",
                new Pokemon.PokemonStats(45, 49, 65, 49, 65, 45),
                new Pokemon.PokemonEvolutionInfo(0, 153, 16),
                ""
        ));

        addPokemon(new Pokemon(
                255, "Torchic", "Fire", "",
                new Pokemon.PokemonStats(45, 60, 40, 70, 50, 45),
                new Pokemon.PokemonEvolutionInfo(0, 256, 16),
                ""
        ));

        // 5 Legendaries
        addPokemon(new Pokemon(
                150, "Mewtwo", "Psychic", "",
                new Pokemon.PokemonStats(106, 110, 90, 154, 90, 130),
                Pokemon.PokemonEvolutionInfo.NONE,
                ""
        ));

        addPokemon(new Pokemon(
                145, "Zapdos", "Electric", "Flying",
                new Pokemon.PokemonStats(90, 90, 85, 125, 90, 100),
                Pokemon.PokemonEvolutionInfo.NONE,
                ""
        ));

        addPokemon(new Pokemon(
                144, "Articuno", "Ice", "Flying",
                new Pokemon.PokemonStats(90, 85, 100, 95, 125, 85),
                Pokemon.PokemonEvolutionInfo.NONE,
                ""
        ));

        addPokemon(new Pokemon(
                249, "Lugia", "Psychic", "Flying",
                new Pokemon.PokemonStats(106, 90, 130, 90, 154, 110),
                Pokemon.PokemonEvolutionInfo.NONE,
                ""
        ));

        addPokemon(new Pokemon(
                384, "Rayquaza", "Dragon", "Flying",
                new Pokemon.PokemonStats(105, 150, 90, 150, 90, 95),
                Pokemon.PokemonEvolutionInfo.NONE,
                ""
        ));
    }

    /**
     * Is pokedex number unique boolean.
     *
     * @param inputNumber the input number
     * @return the boolean
     */
    public boolean isPokedexNumberUnique(int inputNumber) {
        for (Pokemon p: pokemonList) {
            if (p.getPokedexNumber() == inputNumber) {
                return false; // Not unique — number is already used
            }
        }
        return true; // Unique — no match found
    }

    /**
     * Is pokemon name taken boolean.
     *
     * @param name the name
     * @return the boolean
     */
    public boolean isPokemonNameTaken(String name) {
        for (Pokemon p: pokemonList) {
            if (p.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns total number of Pokémon in the list.
     */
    public int getTotalPokemonCount() {
        return pokemonList.size();
    }

    /**
     * Returns the type with the highest count and its count as a Map.Entry<String, Long>.
     * Returns null if the list is empty.
     */
    public Map.Entry < String, Long > getTopType() {
        Map < String, Long > typeCounts = pokemonList.stream()
                .flatMap(p -> Stream.of(p.getPrimaryType(), p.getSecondaryType()))
                .filter(type -> type != null && !type.isEmpty())
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));

        return typeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);
    }

    /**
     * Returns the Pokemon with the highest base stat total.
     * Returns null if the list is empty.
     */
    public Pokemon getHighestBaseStatPokemon() {
        return pokemonList.stream()
                .max(Comparator.comparingInt(p -> {
                    Pokemon.PokemonStats s = p.getPokemonStats();
                    return s.getHp() + s.getAttack() + s.getDefense() + s.getSpAttack() + s.getSpDefense() + s.getSpeed();
                }))
                .orElse(null);
    }

    public Map < String, Integer > getTypeDistribution() {
        Map < String, Integer > typeCount = new HashMap < > ();

        for (Pokemon p: pokemonList) {
            String primary = p.getPrimaryType();
            String secondary = p.getSecondaryType();

            // Count primary type
            typeCount.put(primary, typeCount.getOrDefault(primary, 0) + 1);

            // Count secondary type if present and not empty
            if (secondary != null && !secondary.isBlank()) {
                typeCount.put(secondary, typeCount.getOrDefault(secondary, 0) + 1);
            }
        }

        return typeCount;
    }

    public void addPokemon(Pokemon newPokemon) {
        if (newPokemon != null) {
            pokemonList.add(newPokemon);
            recentAdditions.add(0, newPokemon); // Add to front (most recent first)

            // Optional: Keep recent list at max 5 entries
            if (recentAdditions.size() > 5) {
                recentAdditions.remove(recentAdditions.size() - 1); // Remove oldest
            }
        }
    }

    public static class PokemonDisplayInfo {
        public final int pokedexNumber;
        public final String displayName;
        public final ImageView icon;
        public final int evolutionLevel;

        public PokemonDisplayInfo(int pokedexNumber, String displayName, ImageView icon, int evolutionLevel) {
            this.pokedexNumber = pokedexNumber;
            this.displayName = displayName;
            this.icon = icon;
            this.evolutionLevel = evolutionLevel;
        }
    }



    private ImageView createPokemonSpriteImageView(int pokeId, double width, double height) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // Use the shared static placeholder icon instead of reloading it each time
        imageView.setImage(PLACEHOLDER_ICON);

        // Build the sprite URL from pokeId
        String url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokeId + ".png";

        // Load the sprite image (background loading = true)
        Image sprite = new Image(url, width, height, true, true, true);

        // Set image if sprite loads successfully
        sprite.progressProperty().addListener((obs, oldProg, newProg) -> {
            if (newProg.doubleValue() >= 1.0 && !sprite.isError()) {
                imageView.setImage(sprite);
            }
        });

        // Keep placeholder if sprite fails to load (already set above)
        sprite.errorProperty().addListener((obs, wasError, isError) -> {
            if (isError) {
                System.err.println("⚠️ Sprite failed to load for #" + pokeId + ", using placeholder.");
                // imageView already uses placeholder, so this is optional.
            }
        });

        return imageView;
    }


    public PokemonDisplayInfo getPokemonDisplayInfo(int pokedexNumber) {
        if (pokedexNumber <= 0) {
            // No evolution
            ImageView placeholderView = new ImageView(PLACEHOLDER_ICON);
            return new PokemonDisplayInfo(0, "None", placeholderView, 0);
        }

        Pokemon p = getByNumber(pokedexNumber);
        if (p == null) {
            // Not found - Unknown with placeholder
            ImageView placeholderView = new ImageView(PLACEHOLDER_ICON);
            return new PokemonDisplayInfo(pokedexNumber, "Unknown (#" + String.format("%03d", pokedexNumber) + ")", placeholderView, 0);
        } else {
            // Found - load real icon using helper
            ImageView iconView = createPokemonSpriteImageView(pokedexNumber, 40, 40);
            int evoLevel = p.getPokemonEvolutionInfo().getEvolutionLevel();
            return new PokemonDisplayInfo(pokedexNumber, p.getName(), iconView, evoLevel);
        }
    }


    public Pokemon getByNumber(int pokedexNumber) {
        for (Pokemon p : pokemonList) {
            if (p.getPokedexNumber() == pokedexNumber) {
                return p;
            }
        }
        return null;
    }






}