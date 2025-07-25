package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.pokemon.Pokemon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class that manages Pokémon data.
 */
public class PokemonManager {

    private final ObservableList<Pokemon> pokemonList = FXCollections.observableArrayList();

    public ObservableList<Pokemon> getPokemonList() {
        return pokemonList;
    }

    public PokemonManager() {
        // 5 Starters
        pokemonList.add(new Pokemon(
                1, "Bulbasaur", "Grass", "Poison",
                new Pokemon.PokemonStats(45, 49, 49, 65, 65, 45),
                new Pokemon.PokemonEvolutionInfo(0, 2, 16),
                ""
        ));

        pokemonList.add(new Pokemon(
                4, "Charmander", "Fire", "",
                new Pokemon.PokemonStats(39, 52, 43, 60, 50, 65),
                new Pokemon.PokemonEvolutionInfo(0, 5, 16),
                ""
        ));

        pokemonList.add(new Pokemon(
                7, "Squirtle", "Water", "",
                new Pokemon.PokemonStats(44, 48, 65, 50, 64, 43),
                new Pokemon.PokemonEvolutionInfo(0, 8, 16),
                ""
        ));

        pokemonList.add(new Pokemon(
                152, "Chikorita", "Grass", "",
                new Pokemon.PokemonStats(45, 49, 65, 49, 65, 45),
                new Pokemon.PokemonEvolutionInfo(0, 153, 16),
                ""
        ));

        pokemonList.add(new Pokemon(
                255, "Torchic", "Fire", "",
                new Pokemon.PokemonStats(45, 60, 40, 70, 50, 45),
                new Pokemon.PokemonEvolutionInfo(0, 256, 16),
                ""
        ));

        // 5 Legendaries
        pokemonList.add(new Pokemon(
                150, "Mewtwo", "Psychic", "",
                new Pokemon.PokemonStats(106, 110, 90, 154, 90, 130),
                Pokemon.PokemonEvolutionInfo.NONE,
                ""
        ));

        pokemonList.add(new Pokemon(
                145, "Zapdos", "Electric", "Flying",
                new Pokemon.PokemonStats(90, 90, 85, 125, 90, 100),
                Pokemon.PokemonEvolutionInfo.NONE,
                ""
        ));

        pokemonList.add(new Pokemon(
                144, "Articuno", "Ice", "Flying",
                new Pokemon.PokemonStats(90, 85, 100, 95, 125, 85),
                Pokemon.PokemonEvolutionInfo.NONE,
                ""
        ));

        pokemonList.add(new Pokemon(
                249, "Lugia", "Psychic", "Flying",
                new Pokemon.PokemonStats(106, 90, 130, 90, 154, 110),
                Pokemon.PokemonEvolutionInfo.NONE,
                ""
        ));

        pokemonList.add(new Pokemon(
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
     *
     * @return the boolean
     */
    public boolean isPokedexNumberUnique(int inputNumber) {
        boolean isTaken = false;

        for (Pokemon p : pokemonList) {
            if (p.getPokedexNumber() == inputNumber) {
                isTaken = true;
                break;
            }
        }
        return isTaken;
    }

    /**
     * Is pokemon name taken boolean.
     *
     * @param name the name
     *
     * @return the boolean
     */
    public boolean isPokemonNameTaken(String name) {
        boolean taken = false;
        for (Pokemon p : pokemonList) {
            if (p.getName().equalsIgnoreCase(name)) {
                taken = true;
                break;
            }
        }
        return taken;
    }


    // Search Pokemon by name substring (case-insensitive)
    public List<Pokemon> searchByName(String nameQuery) {
        return pokemonList.stream()
                .filter(p -> p.getName().toLowerCase().contains(nameQuery.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Search Pokemon by primary or secondary type (case-insensitive)
    public List<Pokemon> searchByType(String typeQuery) {
        return pokemonList.stream()
                .filter(p -> p.getPrimaryType().equalsIgnoreCase(typeQuery)
                        || (p.getSecondaryType() != null && p.getSecondaryType().equalsIgnoreCase(typeQuery)))
                .collect(Collectors.toList());
    }

    // Search Pokemon by Pokedex number
    public Pokemon searchByPokedexNumber(int pokedexNumber) {
        return pokemonList.stream()
                .filter(p -> p.getPokedexNumber() == pokedexNumber)
                .findFirst()
                .orElse(null);
    }

}
