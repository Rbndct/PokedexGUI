package com.rbee.pokedexgui.model.pokemon;

import com.rbee.pokedexgui.manager.ActiveTrainerHolder;
import com.rbee.pokedexgui.manager.MoveManager;
import com.rbee.pokedexgui.manager.PokemonManager;
import com.rbee.pokedexgui.model.item.Item;
import com.rbee.pokedexgui.model.move.Move;
import com.rbee.pokedexgui.model.trainer.Trainer;
import com.rbee.pokedexgui.util.PokemonConstants;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * The type Pokemon.
 */
@SuppressWarnings("squid:S106")

public class Pokemon {

    private int pokedexNumber;

    private PokemonStats pokemonStats;
    private PokemonEvolutionInfo pokemonEvolutionInfo;
    private String name;
    private String primaryType;
    private String secondaryType; // optional

    // Make baseLevel property transient for Gson, with primitive fallback
    private transient SimpleIntegerProperty baseLevel;
    private int baseLevelValue;

    // Make currentLevel property transient for Gson, with primitive fallback
    private transient IntegerProperty currentLevel;
    private int currentLevelValue;

    private boolean canEvolveByLevel;
    private int requiredLevel;
    private int evolvedPokedexNumber; // For level-based evolution

    private static PokemonManager pokemonManager;

    // Transient for FX collections (moves)
    private transient ObservableList<Move> moveSet;

    // For JSON deserialization of moves
    private List<Move> moveSetForDeserialization;

    private Item heldItem;


    /**
     * Sets pokemon manager.
     *
     * @param manager the manager
     */
    public static void setPokemonManager(PokemonManager manager) {
        pokemonManager = manager;
    }


    /**
     * Instantiates a new Pokemon.
     *
     * @param pokedexNumber        the pokedex number
     * @param name                 the name of the Pokemon
     * @param primaryType          the primary type of the Pokemon
     * @param secondaryType        the secondary type of the Pokemon (nullable)
     * @param pokemonStats         the stats of the Pokemon
     * @param pokemonEvolutionInfo the evolution info of the Pokemon
     * @param heldItem             the item held by the Pokemon
     */
    public Pokemon(int pokedexNumber, String name, String primaryType, String secondaryType,
                   PokemonStats pokemonStats,
                   PokemonEvolutionInfo pokemonEvolutionInfo,
                   Item heldItem) {

        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;

        this.baseLevelValue = PokemonConstants.DEFAULT_BASE_LEVEL;
        this.baseLevel = new SimpleIntegerProperty(baseLevelValue);

        this.currentLevel = new SimpleIntegerProperty(baseLevelValue);

        this.pokemonStats = pokemonStats;
        this.pokemonEvolutionInfo = pokemonEvolutionInfo;
        this.heldItem = heldItem;

        this.moveSet = FXCollections.observableArrayList();

        addDefaultMoves();
    }

    /**
     * Initialize transient fields.
     */
// After Gson deserialization, call this to initialize transient fields
    public void initializeTransientFields() {
        // If baseLevel is null, create new property with the deserialized baseLevelValue fallback
        if (this.baseLevel == null) {
            this.baseLevel = new SimpleIntegerProperty(baseLevelValue);
        }
        // Similarly for currentLevel
        if (this.currentLevel == null) {
            this.currentLevel = new SimpleIntegerProperty(currentLevelValue);
        }

        // Initialize other transient fields if needed
        if (this.moveSet == null) {
            this.moveSet = FXCollections.observableArrayList();
        }
    }


    /**
     * Instantiates a new Pokemon.
     *
     * @param other the other
     */
    public Pokemon(Pokemon other) {
        this.pokedexNumber = other.pokedexNumber;
        this.name = other.name;
        this.primaryType = other.primaryType;
        this.secondaryType = other.secondaryType;

        this.baseLevelValue = other.baseLevelValue;
        this.baseLevel = new SimpleIntegerProperty(this.baseLevelValue);
        this.currentLevel = new SimpleIntegerProperty(other.getCurrentLevel());

        // Deep copies
        this.pokemonStats = new PokemonStats(other.pokemonStats);
        this.pokemonEvolutionInfo = new PokemonEvolutionInfo(other.pokemonEvolutionInfo);

        // Copy the item reference (or deep copy if needed)
        this.heldItem = other.heldItem;

        // Copy moves
        this.moveSet = FXCollections.observableArrayList();
        for (Move move : other.getMoveSet()) {
            this.moveSet.add(new Move(move));
        }

        // You can skip addDefaultMoves() since moves are copied above
    }


    /**
     * Gets the base level.
     *
     * @return the base level
     */
    public int getBaseLevel() {
        return baseLevel != null ? baseLevel.get() : baseLevelValue;
    }

    /**
     * Sets the base level.
     *
     * @param level the base level to set
     */
    public void setBaseLevel(int level) {
        if (baseLevel == null) {
            baseLevel = new SimpleIntegerProperty(level);
        } else {
            baseLevel.set(level);
        }
        baseLevelValue = level;
    }

    /**
     * Gets the base level property.
     *
     * @return the base level property
     */
    public IntegerProperty baseLevelProperty() {
        if (baseLevel == null) {
            baseLevel = new SimpleIntegerProperty(baseLevelValue);
        }
        return baseLevel;
    }

    /**
     * Gets the Pokedex number.
     *
     * @return the Pokedex number
     */
    public int getPokedexNumber() {
        return pokedexNumber;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the primary type.
     *
     * @return the primary type
     */
    public String getPrimaryType() {
        return primaryType;
    }

    /**
     * Gets the secondary type.
     *
     * @return the secondary type
     */
    public String getSecondaryType() {
        return secondaryType;
    }

    /**
     * Gets the Pokemon stats.
     *
     * @return the Pokemon stats
     */
    public PokemonStats getPokemonStats() {
        return pokemonStats;
    }

    /**
     * Gets the Pokemon evolution info.
     *
     * @return the Pokemon evolution info
     */
    public PokemonEvolutionInfo getPokemonEvolutionInfo() {
        return pokemonEvolutionInfo;
    }

    /**
     * Gets the held item.
     *
     * @return the held item
     */
    public Item getHeldItem() {
        return heldItem;
    }

    /**
     * Sets the held item.
     *
     * @param item the held item to set
     */
    public void setHeldItem(Item item) {
        this.heldItem = item;
    }

    /**
     * Gets the current level.
     *
     * @return the current level
     */
    public int getCurrentLevel() {
        return currentLevel != null ? currentLevel.get() : baseLevelValue;
    }

    /**
     * Sets the current level.
     *
     * @param level the current level to set
     */
    public void setCurrentLevel(int level) {
        if (currentLevel == null) {
            currentLevel = new SimpleIntegerProperty(level);
        } else {
            currentLevel.set(level);
        }
    }

    /**
     * Gets the current level property.
     *
     * @return the current level property
     */
    public IntegerProperty currentLevelProperty() {
        if (currentLevel == null) {
            currentLevel = new SimpleIntegerProperty(baseLevelValue);
        }
        return currentLevel;
    }

    /**
     * Gets the move set.
     *
     * @return the observable list of moves
     */
    public ObservableList<Move> getMoveSet() {
        if (moveSet == null) {
            moveSet = FXCollections.observableArrayList();
        }
        return moveSet;
    }

    /**
     * Adds default moves to the move set.
     */
    private void addDefaultMoves() {
        MoveManager moveManager = MoveManager.getInstance(); // get singleton instance

        Move tackle = moveManager.getMoveByName("Tackle");
        Move defend = moveManager.getMoveByName("Defend");

        if (tackle != null) moveSet.add(tackle);
        if (defend != null) moveSet.add(defend);
    }

    /**
     * Sets the PokemonStats.
     *
     * @param pokemonStats the new PokemonStats
     */
    public void setPokemonStats(PokemonStats pokemonStats) {
        this.pokemonStats = pokemonStats;
    }

    /**
     * Gets base HP.
     *
     * @return base HP
     */
    public int getBaseHP() {
        return pokemonStats.getHp();
    }

    /**
     * Sets base HP.
     *
     * @param newHp the new HP value
     */
    public void setBaseHP(int newHp) {
        this.pokemonStats = new PokemonStats(
                newHp,
                pokemonStats.getAttack(),
                pokemonStats.getDefense(),
                pokemonStats.getSpAttack(),
                pokemonStats.getSpDefense(),
                pokemonStats.getSpeed()
        );
    }

    /**
     * Gets base Attack.
     *
     * @return base Attack
     */
    public int getBaseAttack() {
        return pokemonStats.getAttack();
    }

    /**
     * Sets base Attack.
     *
     * @param newAttack the new Attack value
     */
    public void setBaseAttack(int newAttack) {
        this.pokemonStats = new PokemonStats(
                pokemonStats.getHp(),
                newAttack,
                pokemonStats.getDefense(),
                pokemonStats.getSpAttack(),
                pokemonStats.getSpDefense(),
                pokemonStats.getSpeed()
        );
    }

    /**
     * Gets base Defense.
     *
     * @return base Defense
     */
    public int getBaseDefense() {
        return pokemonStats.getDefense();
    }

    /**
     * Sets base Defense.
     *
     * @param newDefense the new Defense value
     */
    public void setBaseDefense(int newDefense) {
        this.pokemonStats = new PokemonStats(
                pokemonStats.getHp(),
                pokemonStats.getAttack(),
                newDefense,
                pokemonStats.getSpAttack(),
                pokemonStats.getSpDefense(),
                pokemonStats.getSpeed()
        );
    }

    /**
     * Gets base Special Attack.
     *
     * @return base Special Attack
     */
    public int getBaseSpecialAttack() {
        return pokemonStats.getSpAttack();
    }

    /**
     * Sets base Special Attack.
     *
     * @param newSpAttack the new Special Attack value
     */
    public void setBaseSpecialAttack(int newSpAttack) {
        this.pokemonStats = new PokemonStats(
                pokemonStats.getHp(),
                pokemonStats.getAttack(),
                pokemonStats.getDefense(),
                newSpAttack,
                pokemonStats.getSpDefense(),
                pokemonStats.getSpeed()
        );
    }

    /**
     * Gets base Special Defense.
     *
     * @return base Special Defense
     */
    public int getBaseSpecialDefense() {
        return pokemonStats.getSpDefense();
    }

    /**
     * Sets base Special Defense.
     *
     * @param newSpDefense the new Special Defense value
     */
    public void setBaseSpecialDefense(int newSpDefense) {
        this.pokemonStats = new PokemonStats(
                pokemonStats.getHp(),
                pokemonStats.getAttack(),
                pokemonStats.getDefense(),
                pokemonStats.getSpAttack(),
                newSpDefense,
                pokemonStats.getSpeed()
        );
    }

    /**
     * Gets base Speed.
     *
     * @return base Speed
     */
    public int getBaseSpeed() {
        return pokemonStats.getSpeed();
    }

    /**
     * Sets base Speed.
     *
     * @param newSpeed the new Speed value
     */
    public void setBaseSpeed(int newSpeed) {
        this.pokemonStats = new PokemonStats(
                pokemonStats.getHp(),
                pokemonStats.getAttack(),
                pokemonStats.getDefense(),
                pokemonStats.getSpAttack(),
                pokemonStats.getSpDefense(),
                newSpeed
        );
    }

    /**
     * Determines if this Pokemon can evolve using the specified evolution stone.
     *
     * @param stoneName the name of the evolution stone
     *
     * @return true if the Pokemon can evolve with this stone, false otherwise
     */
    public boolean canEvolveWithStone(String stoneName) {
        Map<Integer, String> stoneEvoMap = Map.of(
                37, "fire stone",    // Vulpix -> Ninetales
                133, "water stone",  // Eevee -> Vaporeon
                39, "moon stone",    // Jigglypuff -> Wigglytuff
                25, "thunder stone", // Pikachu -> Raichu
                30, "moon stone"     // Nidorina -> Nidoqueen
        );

        String requiredStone = stoneEvoMap.get(this.pokedexNumber);
        return requiredStone != null && requiredStone.equalsIgnoreCase(stoneName);
    }

    /**
     * Attempts to evolve this Pokémon using the specified evolution stone.
     *
     * @param stoneName the name of the evolution stone
     */
    public void evolveWithStone(String stoneName) {
        if (pokemonManager == null) {
            System.err.println("PokemonManager not set. Cannot evolve.");
            return;
        }

        if (!canEvolveWithStone(stoneName)) {
            System.out.println(this.name + " cannot evolve with " + stoneName);
            return;
        }

        Integer evolvedPokedexNumber = getEvolvedPokedexNumber();
        if (evolvedPokedexNumber == null) {
            System.err.println("No evolution mapping found for " + this.name);
            return;
        }

        Pokemon evolvedFormBase = pokemonManager.getByNumber(evolvedPokedexNumber);
        if (evolvedFormBase == null) {
            System.err.println("Evolved form not found for Pokedex #" + evolvedPokedexNumber);
            return;
        }

        Pokemon evolvedForm = createEvolvedInstance(evolvedFormBase);

        replaceCurrentPokemonInTrainerList(evolvedForm);

        System.out.println(this.name + " has evolved using " + stoneName + "!");
    }

    // Helper: get the evolved pokedex number
    private Integer getEvolvedPokedexNumber() {
        Map<Integer, Integer> evolutionMap = Map.of(
                37, 38,
                133, 134,
                39, 40,
                25, 26,
                30, 31
        );
        return evolutionMap.get(this.pokedexNumber);
    }

    // Helper: create a new evolved Pokémon instance copying relevant state
    private Pokemon createEvolvedInstance(Pokemon evolvedFormBase) {
        Pokemon evolvedForm = new Pokemon(evolvedFormBase);
        evolvedForm.setCurrentLevel(this.getCurrentLevel());
        evolvedForm.setMoveSet(FXCollections.observableArrayList(this.getMoveSet()));
        evolvedForm.setHeldItem(this.getHeldItem());
        return evolvedForm;
    }

    private void setMoveSet(ObservableList<Move> moves) {
        this.moveSet = FXCollections.observableArrayList(moves);
    }


    // Helper: replace the current Pokémon in trainer lineup or storage list
    private void replaceCurrentPokemonInTrainerList(Pokemon evolvedForm) {
        Trainer trainer = ActiveTrainerHolder.getActiveTrainer();
        if (trainer == null) {
            System.err.println("No active trainer to replace Pokémon");
            return;
        }

        ObservableList<Pokemon> lineup = trainer.getLineup();
        ObservableList<Pokemon> storage = trainer.getStorage();

        int idx = lineup.indexOf(this);
        if (idx >= 0) {
            lineup.set(idx, evolvedForm);
            System.out.println("Replaced Pokémon in lineup.");
            return;
        }

        idx = storage.indexOf(this);
        if (idx >= 0) {
            storage.set(idx, evolvedForm);
            System.out.println("Replaced Pokémon in storage.");
        } else {
            System.err.println("Current Pokémon not found in lineup or storage.");
        }
    }


    /**
     * Checks if this Pokémon can evolve by leveling up.
     *
     * @return true if it can evolve by level up, false otherwise
     */
    public boolean canEvolveByLevelUp() {
        PokemonEvolutionInfo evoInfo = getPokemonEvolutionInfo();
        return evoInfo != null
            && evoInfo.getEvolutionLevel() > 0
            && evoInfo.getEvolvesToNumber() > 0;
    }



    /**
     * Gets the required level for level-based evolution.
     *
     * @return the required level for evolution
     */
    public int getRequiredLevelForLevelEvolution() {
        return requiredLevel;
    }

    /**
     * Evolves this Pokémon by leveling up, if possible.
     */
    public void evolveByLevelUp() {
        if (pokemonManager == null) {
            System.err.println("PokemonManager not set. Cannot evolve.");
            return;
        }

        int evolvesToNumber = getPokemonEvolutionInfo().getEvolvesToNumber();
        if (evolvesToNumber <= 0) {
            System.err.println("No evolution available.");
            return;
        }

        Pokemon evolvedFormBase = pokemonManager.getByNumber(evolvesToNumber);
        if (evolvedFormBase != null) {
            Pokemon evolvedForm = createEvolvedInstance(evolvedFormBase);

            // Replace current Pokémon's data with evolved instance data
            copyEvolutionDataFrom(evolvedForm); // or replace references in your trainer list if using new instance

            System.out.println(this.name + " has evolved by level up!");
            this.canEvolveByLevel = false; // disable further level evolutions if needed
        } else {
            System.err.println("Evolved form not found for Pokedex #" + evolvesToNumber);
        }
    }



    /**
     * Helper method to copy evolution-related data from another Pokemon instance.
     *
     * @param evolvedForm the evolved Pokemon to copy data from
     */
    private void copyEvolutionDataFrom(Pokemon evolvedForm) {
        this.name = evolvedForm.getName();
        this.pokedexNumber = evolvedForm.getPokedexNumber();
        this.primaryType = evolvedForm.getPrimaryType();
        this.secondaryType = evolvedForm.getSecondaryType();
        this.pokemonStats = evolvedForm.getPokemonStats();
        this.pokemonEvolutionInfo = evolvedForm.getPokemonEvolutionInfo();
        this.heldItem = evolvedForm.getHeldItem();

        // Do NOT change currentLevel, currentHp, IVs, EVs, or any personal stats.
        // Just keep them exactly as they are.

        System.out.println(this.name + " has evolved, keeping current stats unchanged.");
    }


    /**
     * Represents base stats of a Pokémon.
     */
    public static class PokemonStats {

        private final int hp;
        private final int attack;
        private final int defense;
        private final int spAttack;
        private final int spDefense;
        private final int speed;

        /**
         * Constructs Pokémon stats with specified values.
         *
         * @param hp        HP stat
         * @param attack    Attack stat
         * @param defense   Defense stat
         * @param spAttack  Special Attack stat
         * @param spDefense Special Defense stat
         * @param speed     Speed stat
         */
        public PokemonStats(int hp, int attack, int defense, int spAttack, int spDefense, int speed) {
            this.hp = hp;
            this.attack = attack;
            this.defense = defense;
            this.spAttack = spAttack;
            this.spDefense = spDefense;
            this.speed = speed;
        }

        /**
         * Instantiates a new Pokemon stats.
         *
         * @param other the other
         */
        public PokemonStats(PokemonStats other) {
            this.hp = other.hp;
            this.attack = other.attack;
            this.defense = other.defense;
            this.spAttack = other.spAttack;
            this.spDefense = other.spDefense;
            this.speed = other.speed;
        }


        /**
         * Gets hp.
         *
         * @return the hp
         */
        public int getHp() { return hp; }

        /**
         * Gets attack.
         *
         * @return the attack
         */
        public int getAttack() { return attack; }

        /**
         * Gets defense.
         *
         * @return the defense
         */
        public int getDefense() { return defense; }

        /**
         * Gets sp attack.
         *
         * @return the sp attack
         */
        public int getSpAttack() { return spAttack; }

        /**
         * Gets sp defense.
         *
         * @return the sp defense
         */
        public int getSpDefense() { return spDefense; }

        /**
         * Gets speed.
         *
         * @return the speed
         */
        public int getSpeed() { return speed; }

        /**
         * Calculates total base stats.
         *
         * @return sum of all base stats
         */
        public int getTotalBaseStats() {
            return hp + attack + defense + spAttack + spDefense + speed;
        }
    }

    /**
     * Represents evolution information for a Pokémon.
     */
    public static class PokemonEvolutionInfo {
        /**
         * The constant NONE.
         */
        public static final PokemonEvolutionInfo NONE = new PokemonEvolutionInfo(
                PokemonConstants.NO_EVOLUTION,
                PokemonConstants.NO_EVOLUTION,
                PokemonConstants.NO_EVOLUTION,
                false // evolvesByStone
        );

        private final int evolvesFromNumber;
        private final int evolvesToNumber;
        private final int evolutionLevel;
        private final boolean evolvesByStone;

        /**
         * Instantiates a new Pokemon evolution info.
         *
         * @param evolvesFromNumber the evolves from number
         * @param evolvesToNumber   the evolves to number
         * @param evolutionLevel    the evolution level
         * @param evolvesByStone    the evolves by stone
         */
        public PokemonEvolutionInfo(int evolvesFromNumber, int evolvesToNumber, int evolutionLevel, boolean evolvesByStone) {
            this.evolvesFromNumber = evolvesFromNumber;
            this.evolvesToNumber = evolvesToNumber;
            this.evolutionLevel = evolutionLevel;
            this.evolvesByStone = evolvesByStone;
        }

        /**
         * Instantiates a new Pokemon evolution info.
         *
         * @param other the other
         */
        public PokemonEvolutionInfo(PokemonEvolutionInfo other) {
            this.evolvesFromNumber = other.evolvesFromNumber;
            this.evolvesToNumber = other.evolvesToNumber;
            this.evolutionLevel = other.evolutionLevel;
            this.evolvesByStone = other.evolvesByStone;
        }


        /**
         * Gets evolves from number.
         *
         * @return the evolves from number
         */
        public int getEvolvesFromNumber() { return evolvesFromNumber; }

        /**
         * Gets evolves to number.
         *
         * @return the evolves to number
         */
        public int getEvolvesToNumber() { return evolvesToNumber; }

        /**
         * Gets evolution level.
         *
         * @return the evolution level
         */
        public int getEvolutionLevel() { return evolutionLevel; }

        /**
         * Is evolves by stone boolean.
         *
         * @return the boolean
         */
        public boolean isEvolvesByStone() { return evolvesByStone; }
    }

    /**
     * Should be called after deserialization to restore transient fields.
     */
    public void postDeserialize() {
        moveSet = FXCollections.observableArrayList();
        if (moveSetForDeserialization != null) {
            moveSet.addAll(moveSetForDeserialization);
        }

        baseLevel = new SimpleIntegerProperty(baseLevelValue);
        currentLevel = new SimpleIntegerProperty(currentLevelValue);
    }


    /**
     * Should be called before serialization to store data from transient fields.
     */
    public void preSerialize() {
        moveSetForDeserialization = (moveSet != null) ? new ArrayList<>(moveSet) : new ArrayList<>();

        // Also handle currentLevel/baseLevel values
        baseLevelValue = (baseLevel != null) ? baseLevel.get() : 1;
        currentLevelValue = (currentLevel != null) ? currentLevel.get() : baseLevelValue;
    }


    public Pokemon clone() {
        return new Pokemon(this);
    }




}