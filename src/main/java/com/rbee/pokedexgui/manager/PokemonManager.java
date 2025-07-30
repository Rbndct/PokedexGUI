package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.pokemon.Pokemon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service class that manages Pokémon data.
 */
public class PokemonManager {

    private final ObservableList < Pokemon > pokemonList = FXCollections.observableArrayList();

    private static PokemonManager instance;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static PokemonManager getInstance() {
        if (instance == null) {
            instance = new PokemonManager();
        }
        return instance;
    }

    /**
     * Gets pokemon list.
     *
     * @return the pokemon list
     */
    public ObservableList < Pokemon > getPokemonList() {
        return pokemonList;
    }
    private static final Image PLACEHOLDER_ICON;




    private String escapeCSV(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            field = field.replace("\"", "\"\"");
            return "\"" + field + "\"";
        }
        return field;
    }

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


    /**
     * Gets recent additions.
     *
     * @return the recent additions
     */
    public ObservableList < Pokemon > getRecentAdditions() {
        return recentAdditions;
    }


    /**
     * Instantiates a new Pokemon manager.
     */
    public PokemonManager() {
    addDefaultPokemon();
    }



    private void addDefaultPokemon() {
        addPokemon(new Pokemon(
                1, "Bulbasaur", "Grass", "Poison",
                new Pokemon.PokemonStats(45, 49, 49, 65, 65, 45),
                new Pokemon.PokemonEvolutionInfo(0, 2, 16, false),
                null
        ));

        addPokemon(new Pokemon(
                2, "Ivysaur", "Grass", "Poison",
                new Pokemon.PokemonStats(60, 62, 63, 80, 80, 60),
                new Pokemon.PokemonEvolutionInfo(1, 3, 32, false),
                null
        ));

        addPokemon(new Pokemon(
                3, "Venusaur", "Grass", "Poison",
                new Pokemon.PokemonStats(80, 82, 83, 100, 100, 80),
                new Pokemon.PokemonEvolutionInfo(2, 0, 0, false),
                null
        ));

        addPokemon(new Pokemon(
                4, "Charmander", "Fire", "",
                new Pokemon.PokemonStats(39, 52, 43, 60, 50, 65),
                new Pokemon.PokemonEvolutionInfo(0, 5, 16, false),
                null
        ));

        addPokemon(new Pokemon(
                5, "Charmeleon", "Fire", "",
                new Pokemon.PokemonStats(58, 64, 58, 80, 65, 80),
                new Pokemon.PokemonEvolutionInfo(4, 6, 36, false),
                null
        ));

        addPokemon(new Pokemon(
                6, "Charizard", "Fire", "Flying",
                new Pokemon.PokemonStats(78, 84, 78, 109, 85, 100),
                new Pokemon.PokemonEvolutionInfo(5, 0, 0, false),
                null
        ));

        addPokemon(new Pokemon(
                7, "Squirtle", "Water", "",
                new Pokemon.PokemonStats(44, 48, 65, 50, 64, 43),
                new Pokemon.PokemonEvolutionInfo(0, 8, 16, false),
                null
        ));

        addPokemon(new Pokemon(
                8, "Wartortle", "Water", "",
                new Pokemon.PokemonStats(59, 63, 80, 65, 80, 58),
                new Pokemon.PokemonEvolutionInfo(7, 9, 36, false),
                null
        ));

        addPokemon(new Pokemon(
                9, "Blastoise", "Water", "",
                new Pokemon.PokemonStats(79, 83, 100, 85, 105, 78),
                new Pokemon.PokemonEvolutionInfo(8, 0, 0, false),
                null
        ));

        addPokemon(new Pokemon(
                10, "Caterpie", "Bug", "",
                new Pokemon.PokemonStats(45, 30, 35, 20, 20, 45),
                new Pokemon.PokemonEvolutionInfo(0, 11, 7, false),
                null
        ));

        addPokemon(new Pokemon(
                11, "Metapod", "Bug", "",
                new Pokemon.PokemonStats(50, 20, 55, 25, 25, 30),
                new Pokemon.PokemonEvolutionInfo(10, 12, 10, false),
                null
        ));

        addPokemon(new Pokemon(
                12, "Butterfree", "Bug", "Flying",
                new Pokemon.PokemonStats(60, 45, 50, 90, 80, 70),
                new Pokemon.PokemonEvolutionInfo(11, 0, 0, false),
                null
        ));

        addPokemon(new Pokemon(
                13, "Weedle", "Bug", "Poison",
                new Pokemon.PokemonStats(40, 35, 30, 20, 20, 50),
                new Pokemon.PokemonEvolutionInfo(0, 14, 7, false),
                null
        ));

        addPokemon(new Pokemon(
                14, "Kakuna", "Bug", "Poison",
                new Pokemon.PokemonStats(45, 25, 50, 25, 25, 35),
                new Pokemon.PokemonEvolutionInfo(13, 15, 10, false),
                null
        ));

        addPokemon(new Pokemon(
                15, "Beedrill", "Bug", "Poison",
                new Pokemon.PokemonStats(65, 90, 40, 45, 80, 75),
                new Pokemon.PokemonEvolutionInfo(14, 0, 0, false),
                null
        ));

        addPokemon(new Pokemon(
                16, "Pidgey", "Normal", "Flying",
                new Pokemon.PokemonStats(40, 45, 40, 35, 35, 56),
                new Pokemon.PokemonEvolutionInfo(0, 17, 18, false),
                null
        ));

        addPokemon(new Pokemon(
                17, "Pidgeotto", "Normal", "Flying",
                new Pokemon.PokemonStats(63, 60, 55, 50, 50, 71),
                new Pokemon.PokemonEvolutionInfo(16, 18, 36, false),
                null
        ));

        addPokemon(new Pokemon(
                18, "Pidgeot", "Normal", "Flying",
                new Pokemon.PokemonStats(83, 80, 75, 70, 70, 101),
                new Pokemon.PokemonEvolutionInfo(17, 0, 0, false),
                null
        ));

        addPokemon(new Pokemon(
                19, "Rattata", "Normal", "",
                new Pokemon.PokemonStats(30, 56, 35, 25, 35, 72),
                new Pokemon.PokemonEvolutionInfo(0, 20, 20, false),
                null
        ));

        addPokemon(new Pokemon(
                20, "Raticate", "Normal", "",
                new Pokemon.PokemonStats(55, 81, 60, 50, 70, 97),
                new Pokemon.PokemonEvolutionInfo(19, 0, 0, false),
                null
        ));

        // Five with stone evolution (evolvesByStone = true)

        addPokemon(new Pokemon(
                25, "Pikachu", "Electric", "",
                new Pokemon.PokemonStats(35, 55, 40, 50, 50, 90),
                new Pokemon.PokemonEvolutionInfo(0, 26, 0, true),
                null
        ));

        addPokemon(new Pokemon(
                26, "Raichu", "Electric", "",
                new Pokemon.PokemonStats(60, 90, 55, 90, 80, 110),
                new Pokemon.PokemonEvolutionInfo(25, 0, 0, false),
                null
        ));

        addPokemon(new Pokemon(
                37, "Vulpix", "Fire", "",
                new Pokemon.PokemonStats(38, 41, 40, 50, 65, 65),
                new Pokemon.PokemonEvolutionInfo(0, 38, 0, true),
                null
        ));

        addPokemon(new Pokemon(
                38, "Ninetales", "Fire", "",
                new Pokemon.PokemonStats(73, 76, 75, 81, 100, 100),
                new Pokemon.PokemonEvolutionInfo(37, 0, 0, false),
                null
        ));

        addPokemon(new Pokemon(
                61, "Poliwhirl", "Water", "",
                new Pokemon.PokemonStats(65, 65, 65, 50, 50, 90),
                new Pokemon.PokemonEvolutionInfo(0, 62, 0, true),
                null
        ));

        addPokemon(new Pokemon(
                62, "Poliwrath", "Water", "Fighting",
                new Pokemon.PokemonStats(90, 95, 95, 70, 90, 70),
                new Pokemon.PokemonEvolutionInfo(61, 0, 0, false),
                null
        ));

        addPokemon(new Pokemon(
                44, "Gloom", "Grass", "Poison",
                new Pokemon.PokemonStats(60, 65, 70, 85, 75, 40),
                new Pokemon.PokemonEvolutionInfo(0, 182, 0, true),
                null
        ));

        addPokemon(new Pokemon(
                182, "Bellossom", "Grass", "",
                new Pokemon.PokemonStats(75, 80, 95, 90, 100, 50),
                new Pokemon.PokemonEvolutionInfo(44, 0, 0, false),
                null
        ));

        // The rest with normal level evolutions or no evolutions

        addPokemon(new Pokemon(
                27, "Sandshrew", "Ground", "",
                new Pokemon.PokemonStats(50, 75, 85, 20, 30, 40),
                new Pokemon.PokemonEvolutionInfo(0, 28, 22, false),
                null
        ));

        addPokemon(new Pokemon(
                28, "Sandslash", "Ground", "",
                new Pokemon.PokemonStats(75, 100, 110, 45, 55, 65),
                new Pokemon.PokemonEvolutionInfo(27, 0, 0, false),
                null
        ));

        addPokemon(new Pokemon(
                29, "Nidoran♀", "Poison", "",
                new Pokemon.PokemonStats(55, 47, 52, 40, 40, 41),
                new Pokemon.PokemonEvolutionInfo(0, 30, 16, false),
                null
        ));

        addPokemon(new Pokemon(
                30, "Nidorina", "Poison", "",
                new Pokemon.PokemonStats(70, 62, 67, 55, 55, 56),
                new Pokemon.PokemonEvolutionInfo(29, 31, 32, false),
                null
        ));

        // Set the PokemonManager instance if required
        Pokemon.setPokemonManager(this);
    }


    /**
     * Is pokedex number unique boolean.
     *
     * @param inputNumber the input number
     *
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
     *
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
     *
     * @return the total pokemon count
     */
    public int getTotalPokemonCount() {
        return pokemonList.size();
    }

    /**
     * Returns the type with the highest count and its count as a Map.Entry<String, Long>.
     * Returns null if the list is empty.
     *
     * @return the top type
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
     *
     * @return the highest base stat pokemon
     */
    public Pokemon getHighestBaseStatPokemon() {
        return pokemonList.stream()
                .max(Comparator.comparingInt(p -> {
                    Pokemon.PokemonStats s = p.getPokemonStats();
                    return s.getHp() + s.getAttack() + s.getDefense() + s.getSpAttack() + s.getSpDefense() + s.getSpeed();
                }))
                .orElse(null);
    }

    /**
     * Gets type distribution.
     *
     * @return the type distribution
     */
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

    /**
     * Add pokemon.
     *
     * @param newPokemon the new pokemon
     */
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

    /**
     * The type Pokemon display info.
     */
    public static class PokemonDisplayInfo {
        /**
         * The Pokedex number.
         */
        public final int pokedexNumber;
        /**
         * The Display name.
         */
        public final String displayName;
        /**
         * The Icon.
         */
        public final ImageView icon;
        /**
         * The Evolution level.
         */
        public final int evolutionLevel;

        /**
         * Instantiates a new Pokemon display info.
         *
         * @param pokedexNumber  the pokedex number
         * @param displayName    the display name
         * @param icon           the icon
         * @param evolutionLevel the evolution level
         */
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


    /**
     * Gets pokemon display info.
     *
     * @param pokedexNumber the pokedex number
     *
     * @return the pokemon display info
     */
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


    /**
     * Gets by number.
     *
     * @param pokedexNumber the pokedex number
     *
     * @return the by number
     */
    public Pokemon getByNumber(int pokedexNumber) {
        for (Pokemon p : pokemonList) {
            if (p.getPokedexNumber() == pokedexNumber) {
                return p;
            }
        }
        return null;
    }







}