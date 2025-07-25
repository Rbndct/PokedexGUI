package com.rbee.pokedexgui.model.pokemon;

import com.rbee.pokedexgui.util.PokemonConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Pokemon.
 */
@SuppressWarnings("squid:S106")

public class Pokemon {

    private final int pokedexNumber;

    private final PokemonStats pokemonStats;
    private final PokemonEvolutionInfo pokemonEvolutionInfo;
    private final String name;
    private final String primaryType;
    private final String secondaryType; // optional
    private final int baseLevel;


    private ArrayList<String> moveSet = new ArrayList<>();
    private String heldItem;

    /**
     * Instantiates a new Pokemon.
     *
     * @param pokedexNumber        the pokedex number
     * @param name                 the name
     * @param primaryType          the primary type
     * @param secondaryType        the secondary type
     * @param pokemonStats         the pokemon stats
     * @param pokemonEvolutionInfo the pokemon evolution info
     * @param heldItem             the held item
     */
    public Pokemon(int pokedexNumber, String name, String primaryType, String secondaryType,
                   PokemonStats pokemonStats,
                   PokemonEvolutionInfo pokemonEvolutionInfo,
                   String heldItem) {
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
        this.baseLevel = PokemonConstants.DEFAULT_BASE_LEVEL;
        this.pokemonStats = pokemonStats;
        this.pokemonEvolutionInfo = pokemonEvolutionInfo;
        this.heldItem = heldItem;

        this.moveSet = new ArrayList<>();
        // Default moves:
        addDefaultMoves();

    }

    /**
     * Gets pokedex number.
     *
     * @return the pokedex number
     */
    public int getPokedexNumber() {
        return pokedexNumber;
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
     * Gets primary type.
     *
     * @return the primary type
     */
    public String getPrimaryType() {
        return primaryType;
    }

    /**
     * Gets secondary type.
     *
     * @return the secondary type
     */
    public String getSecondaryType() {
        return secondaryType;
    }

    /**
     * Gets base level.
     *
     * @return the base level
     */
    public int getBaseLevel() {
        return baseLevel;
    }

    /**
     * Gets pokemon stats.
     *
     * @return the pokemon stats
     */
    public PokemonStats getPokemonStats() {
        return pokemonStats;
    }

    /**
     * Gets pokemon evolution info.
     *
     * @return the pokemon evolution info
     */
    public PokemonEvolutionInfo getPokemonEvolutionInfo() {
        return pokemonEvolutionInfo;
    }

    /**
     * Gets move set.
     *
     * @return the move set
     */
    public List<String> getMoveSet() {
        return new ArrayList<>(moveSet);
    }

    /**
     * Gets held item.
     *
     * @return the held item
     */
    public String getHeldItem() {
        return heldItem;
    }

    /**
     * Sets held item.
     *
     * @param heldItem the held item
     */
    public void setHeldItem(String heldItem) {
        this.heldItem = heldItem;
    }


    /**
     * Sets move set.
     *
     * @param moveSet the move set
     */
    public void setMoveSet(ArrayList<String> moveSet) {
        this.moveSet = moveSet;
    }

    private void addDefaultMoves() {
        moveSet.add("Tackle");
        moveSet.add("Defend");
    }

    /**
     * The type Pokemon stats.
     */
    public static class PokemonStats {

        private final int hp;
        private final int attack;
        private final int defense;
        private final int spAttack;
        private final int spDefense;
        private final int speed;

        /**
         * Instantiates a new Pokemon stats.
         *
         * @param hp        the hp
         * @param attack    the attack
         * @param defense   the defense
         * @param spAttack  the sp attack
         * @param spDefense the sp defense
         * @param speed     the speed
         */
        public PokemonStats(int hp, int attack, int defense, int spAttack, int spDefense,
                            int speed) {
            this.hp = hp;
            this.attack = attack;
            this.defense = defense;
            this.spAttack = spAttack;
            this.spDefense = spDefense;
            this.speed = speed;
        }

        /**
         * Gets hp.
         *
         * @return the hp
         */
        public int getHp() {
            return hp;
        }

        /**
         * Gets attack.
         *
         * @return the attack
         */
        public int getAttack() {
            return attack;
        }

        /**
         * Gets defense.
         *
         * @return the defense
         */
        public int getDefense() {
            return defense;
        }

        /**
         * Gets sp attack.
         *
         * @return the sp attack
         */
        public int getSpAttack() {
            return spAttack;
        }


        /**
         * Gets sp defense.
         *
         * @return the sp defense
         */
        public int getSpDefense() {
            return spDefense;
        }

        /**
         * Gets speed.
         *
         * @return the speed
         */
        public int getSpeed() {
            return speed;
        }

        public int getTotalBaseStats() {
            return hp + attack + defense + spAttack + spDefense + speed;
        }
    }

    /**
     * The type Pokemon evolution info.
     */
    public static class PokemonEvolutionInfo {

        /**
         * The constant NONE.
         */
        public static final PokemonEvolutionInfo NONE = new PokemonEvolutionInfo(
                PokemonConstants.NO_EVOLUTION,
                PokemonConstants.NO_EVOLUTION,
                PokemonConstants.NO_EVOLUTION
        );

        private final int evolvesFromNumber;
        private final int evolvesToNumber;
        private final int evolutionLevel;

        /**
         * Instantiates a new Pokemon evolution info.
         *
         * @param evolvesFromNumber the evolves from number
         * @param evolvesToNumber   the evolves to number
         * @param evolutionLevel    the evolution level
         */
        public PokemonEvolutionInfo(int evolvesFromNumber, int evolvesToNumber,
                                    int evolutionLevel) {
            this.evolvesFromNumber = evolvesFromNumber;
            this.evolvesToNumber = evolvesToNumber;
            this.evolutionLevel = evolutionLevel;
        }

        /**
         * Gets evolves from number.
         *
         * @return the evolves from number
         */
        public int getEvolvesFromNumber() {
            return evolvesFromNumber;
        }

        /**
         * Gets evolves to number.
         *
         * @return the evolves to number
         */
        public int getEvolvesToNumber() {
            return evolvesToNumber;
        }

        /**
         * Gets evolution level.
         *
         * @return the evolution level
         */
        public int getEvolutionLevel() {
            return evolutionLevel;
        }
    }

    public void display() {
        PokemonStats s = this.getPokemonStats();

        int total = s.getHp() +
                s.getAttack() +
                s.getDefense() +
                s.getSpAttack() +
                s.getSpDefense() +
                s.getSpeed();

        String types = this.getPrimaryType();
        if (this.getSecondaryType() != null && !this.getSecondaryType().isEmpty()) {
            types += "/" + this.getSecondaryType();
        }

        String formatRow = "%-12s %-12s %-15s %-7d %-5d %-8d %-8d %-14d %-14d %-6d\n";
        System.out.printf(formatRow,
                String.format("%03d", this.getPokedexNumber()),
                this.getName(),
                types,
                total,
                s.getHp(),
                s.getAttack(),
                s.getDefense(),
                s.getSpAttack(),
                s.getSpDefense(),
                s.getSpeed()
        );
    }

}