package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.move.Move;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;

/**
 * The type Move manager.
 */
@SuppressWarnings("squid:S106")
public class MoveManager {

    private static volatile MoveManager instance; // Thread-safe singleton instance

    private final ObservableList<Move> moveList = FXCollections.observableArrayList();
    private final ObservableList<Move> recentAdditions = FXCollections.observableArrayList();

    // Private constructor for singleton


    /**
     * Gets instance.
     *
     * @return the instance
     */
// Thread-safe singleton accessor
    public static MoveManager getInstance() {
        if (instance == null) {
            synchronized (MoveManager.class) {
                if (instance == null) {
                    instance = new MoveManager();
                }
            }
        }
        return instance;
    }

    private MoveManager() {
        addDefaultMoves();
    }







    private void addDefaultMoves() {
        // --- HMs ---
        moveList.add(new Move("Cut", "Cut down small trees.", Move.Classification.HM, "Normal", null));
        moveList.add(new Move("Strength", "Move heavy objects.", Move.Classification.HM, "Normal", null));

        moveList.add(new Move("Surf", "Ride a wave across water.", Move.Classification.HM, "Water", null));
        moveList.add(new Move("Waterfall", "Ascend waterfalls; may flinch.", Move.Classification.HM, "Water", null));

        moveList.add(new Move("Fly", "Soar to towns and attack.", Move.Classification.HM, "Flying", null));
        moveList.add(new Move("Sky Climb", "Ascend high cliffs.", Move.Classification.HM, "Flying", null)); // Custom

        moveList.add(new Move("Rock Smash", "Break rocks; may lower defense.", Move.Classification.HM, "Fighting", null));
        moveList.add(new Move("Focus Push", "Push boulders with focus.", Move.Classification.HM, "Fighting", null)); // Custom

        moveList.add(new Move("Dig", "Escape caves and attack.", Move.Classification.HM, "Ground", null));
        moveList.add(new Move("Earth Bore", "Tunnel through terrain.", Move.Classification.HM, "Ground", null)); // Custom

        moveList.add(new Move("Flash", "Light up dark areas.", Move.Classification.HM, "Electric", null));
        moveList.add(new Move("Power Spark", "Jump-start machinery.", Move.Classification.HM, "Electric", null)); // Custom

        moveList.add(new Move("Ice Climb", "Scale icy walls.", Move.Classification.HM, "Ice", null)); // Custom
        moveList.add(new Move("Glacier Slide", "Slide down slopes.", Move.Classification.HM, "Ice", null)); // Custom

        moveList.add(new Move("Ember Path", "Melt icy paths.", Move.Classification.HM, "Fire", null)); // Custom
        moveList.add(new Move("Blazing Trail", "Burn through obstacles.", Move.Classification.HM, "Fire", null)); // Custom

        moveList.add(new Move("Vine Swing", "Swing across gaps.", Move.Classification.HM, "Grass", null)); // Custom
        moveList.add(new Move("Nature Shift", "Rearrange terrain.", Move.Classification.HM, "Grass", null)); // Custom

        moveList.add(new Move("Mind Focus", "Unlock sealed doors.", Move.Classification.HM, "Psychic", null)); // Custom
        moveList.add(new Move("Telejump", "Teleport short distance.", Move.Classification.HM, "Psychic", null)); // Custom

        moveList.add(new Move("Shadow Step", "Move through shadows.", Move.Classification.HM, "Ghost", null)); // Custom
        moveList.add(new Move("Specter Drift", "Float over gaps.", Move.Classification.HM, "Ghost", null)); // Custom

        moveList.add(new Move("Toxic Drain", "Clear toxic pools.", Move.Classification.HM, "Poison", null)); // Custom
        moveList.add(new Move("Sludge Sweep", "Move through sludge.", Move.Classification.HM, "Poison", null)); // Custom

        moveList.add(new Move("Rock Climb", "Climb rocky walls.", Move.Classification.HM, "Rock", null));
        moveList.add(new Move("Stone Lift", "Lift heavy rocks.", Move.Classification.HM, "Rock", null)); // Custom

        moveList.add(new Move("Steel Press", "Press heavy metal.", Move.Classification.HM, "Steel", null)); // Custom
        moveList.add(new Move("Metal Melt", "Dissolve barriers.", Move.Classification.HM, "Steel", null)); // Custom

        moveList.add(new Move("Bug Trail", "Crawl through narrow gaps.", Move.Classification.HM, "Bug", null)); // Custom
        moveList.add(new Move("Cocoon Guard", "Defend in tight spaces.", Move.Classification.HM, "Bug", null)); // Custom

        moveList.add(new Move("Dark Veil", "Sneak past guards.", Move.Classification.HM, "Dark", null)); // Custom
        moveList.add(new Move("Night Stalk", "Travel unseen.", Move.Classification.HM, "Dark", null)); // Custom

        moveList.add(new Move("Dragon Climb", "Climb vertical shafts.", Move.Classification.HM, "Dragon", null)); // Custom
        moveList.add(new Move("Wyrm Glide", "Glide between areas.", Move.Classification.HM, "Dragon", null)); // Custom

        moveList.add(new Move("Fairy Drift", "Hover gently.", Move.Classification.HM, "Fairy", null)); // Custom
        moveList.add(new Move("Pixie Lift", "Lift light objects.", Move.Classification.HM, "Fairy", null)); // Custom

        // --- TMs (2 per type = 36 total) ---
        moveList.add(new Move("Hyper Beam", "Powerful normal blast.", Move.Classification.TM, "Normal", null));
        moveList.add(new Move("Quick Attack", "Strikes first.", Move.Classification.TM, "Normal", null));

        moveList.add(new Move("Flamethrower", "Blasts fire.", Move.Classification.TM, "Fire", null));
        moveList.add(new Move("Fire Blast", "Massive fire burst.", Move.Classification.TM, "Fire", null));

        moveList.add(new Move("Surf", "Hits all opponents.", Move.Classification.TM, "Water", null));
        moveList.add(new Move("Hydro Pump", "Strong water blast.", Move.Classification.TM, "Water", null));

        moveList.add(new Move("Thunderbolt", "Electric strike.", Move.Classification.TM, "Electric", null));
        moveList.add(new Move("Volt Switch", "Switch after attacking.", Move.Classification.TM, "Electric", null));

        moveList.add(new Move("Ice Beam", "Freezing blast.", Move.Classification.TM, "Ice", null));
        moveList.add(new Move("Blizzard", "Snowstorm hits all.", Move.Classification.TM, "Ice", null));

        moveList.add(new Move("Leaf Blade", "Sharp grass blade.", Move.Classification.TM, "Grass", null));
        moveList.add(new Move("Solar Beam", "Charges then blasts.", Move.Classification.TM, "Grass", null));

        moveList.add(new Move("Earthquake", "Ground shakes all.", Move.Classification.TM, "Ground", null));
        moveList.add(new Move("Bulldoze", "Damages and lowers speed.", Move.Classification.TM, "Ground", null));

        moveList.add(new Move("Air Slash", "May cause flinching.", Move.Classification.TM, "Flying", null));
        moveList.add(new Move("Acrobatics", "Stronger without item.", Move.Classification.TM, "Flying", null));

        moveList.add(new Move("Close Combat", "Strong but lowers stats.", Move.Classification.TM, "Fighting", null));
        moveList.add(new Move("Aura Sphere", "Never misses.", Move.Classification.TM, "Fighting", null));

        moveList.add(new Move("Sludge Bomb", "May poison.", Move.Classification.TM, "Poison", null));
        moveList.add(new Move("Toxic", "Badly poisons the target.", Move.Classification.TM, "Poison", null));

        moveList.add(new Move("Psychic", "Strong psychic blast.", Move.Classification.TM, "Psychic", null));
        moveList.add(new Move("Calm Mind", "Boosts Sp. Atk and Sp. Def.", Move.Classification.TM, "Psychic", null));

        moveList.add(new Move("Shadow Ball", "May lower Sp. Def.", Move.Classification.TM, "Ghost", null));
        moveList.add(new Move("Hex", "Stronger if statused.", Move.Classification.TM, "Ghost", null));

        moveList.add(new Move("Stone Edge", "High crit rate.", Move.Classification.TM, "Rock", null));
        moveList.add(new Move("Rock Slide", "Hits multiple targets.", Move.Classification.TM, "Rock", null));

        moveList.add(new Move("Iron Tail", "May lower defense.", Move.Classification.TM, "Steel", null));
        moveList.add(new Move("Flash Cannon", "Steel beam that may lower Sp. Def.", Move.Classification.TM, "Steel", null));

        moveList.add(new Move("X-Scissor", "Slashing bug attack.", Move.Classification.TM, "Bug", null));
        moveList.add(new Move("Bug Buzz", "Bug sonic wave.", Move.Classification.TM, "Bug", null));

        moveList.add(new Move("Dark Pulse", "May cause flinching.", Move.Classification.TM, "Dark", null));
        moveList.add(new Move("Foul Play", "Uses target's power.", Move.Classification.TM, "Dark", null));

        moveList.add(new Move("Dragon Pulse", "Releases a dragon aura.", Move.Classification.TM, "Dragon", null));
        moveList.add(new Move("Draco Meteor", "Massive damage, lowers Sp. Atk.", Move.Classification.TM, "Dragon", null));

        moveList.add(new Move("Moonblast", "May lower Sp. Atk.", Move.Classification.TM, "Fairy", null));
        moveList.add(new Move("Dazzling Gleam", "Hits all foes with light.", Move.Classification.TM, "Fairy", null));

        moveList.add(new Move("Tackle", "A physical attack in which the user charges and slams into the target.", Move.Classification.TM, "Normal", null));
        moveList.add(new Move("Defend", "The user braces itself to reduce incoming damage.", Move.Classification.TM, "Normal", null));

    }



    /**
     * Gets move list.
     *
     * @return the move list
     */
    public ObservableList<Move> getMoveList() {
        return moveList;
    }

    /**
     * Gets recent additions.
     *
     * @return the recent additions
     */
    public ObservableList<Move> getRecentAdditions() {
        return recentAdditions;
    }

    /**
     * Is move name taken boolean.
     *
     * @param name the name
     *
     * @return the boolean
     */
    public boolean isMoveNameTaken(String name) {
        for (Move m : moveList) {
            if (m.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add move.
     *
     * @param newMove the new move
     */
    public void addMove(Move newMove) {
        if (newMove != null) {
            moveList.add(newMove);
            recentAdditions.add(0, newMove); // Add to front

            if (recentAdditions.size() > 5) {
                recentAdditions.remove(recentAdditions.size() - 1);
            }
        }
    }

    /**
     * Gets total move count.
     *
     * @return the total move count
     */
    public int getTotalMoveCount() {
        return moveList.size();
    }

    /**
     * Gets total tm count.
     *
     * @return the total tm count
     */
    public long getTotalTMCount() {
        return moveList.stream()
                .filter(move -> move.getClassification() == Move.Classification.TM)
                .count();
    }

    /**
     * Gets total hm count.
     *
     * @return the total hm count
     */
    public long getTotalHMCount() {
        return moveList.stream()
                .filter(move -> move.getClassification() == Move.Classification.HM)
                .count();
    }

    /**
     * Gets move by name.
     *
     * @param name the name
     *
     * @return the move by name
     */
    public Move getMoveByName(String name) {
        for (Move move : moveList) {
            if (move.getName().equalsIgnoreCase(name)) {
                return move;
            }
        }
        return null;
    }
}
