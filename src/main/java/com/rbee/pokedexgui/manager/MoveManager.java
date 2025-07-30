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
        // HMs (5)
        moveList.add(new Move("Cut", "A basic HM move that can be used to cut down small trees.", Move.Classification.HM, "Normal", null));
        moveList.add(new Move("Surf", "Ride a huge wave to strike all targets. Can cross water.", Move.Classification.HM, "Water", null));
        moveList.add(new Move("Fly", "Soar up and strike on the next turn. Also travels between towns.", Move.Classification.HM, "Flying", null));
        moveList.add(new Move("Strength", "Use physical power to move heavy objects or deal damage.", Move.Classification.HM, "Normal", null));
        moveList.add(new Move("Rock Smash", "Break rocks and deal damage. May lower defense.", Move.Classification.HM, "Fighting", null));

        // TMs (at least 10)
        moveList.add(new Move("Tackle", "A physical attack in which the user charges and slams into the target.", Move.Classification.TM, "Normal", null));
        moveList.add(new Move("Flamethrower", "A powerful blast of fire.", Move.Classification.TM, "Fire", null));
        moveList.add(new Move("Ice Beam", "Blasts a freezing beam that may freeze the target.", Move.Classification.TM, "Ice", null));
        moveList.add(new Move("Thunderbolt", "A strong electric blast crashes down on the target.", Move.Classification.TM, "Electric", null));
        moveList.add(new Move("Earthquake", "The user sets off an earthquake that strikes all Pokémon around.", Move.Classification.TM, "Ground", null));
        moveList.add(new Move("Sludge Bomb", "Fires a sludge projectile that may poison the target.", Move.Classification.TM, "Poison", null));
        moveList.add(new Move("Psychic", "The target is hit with a strong telekinetic force.", Move.Classification.TM, "Psychic", null));
        moveList.add(new Move("Shadow Ball", "Throws a shadowy blob that may lower special defense.", Move.Classification.TM, "Ghost", null));
        moveList.add(new Move("Dragon Claw", "The user slashes the target with sharp claws.", Move.Classification.TM, "Dragon", null));
        moveList.add(new Move("Solar Beam", "Absorbs sunlight, then attacks on the next turn.", Move.Classification.TM, "Grass", null));

        // Extra TM moves for types not yet covered
        moveList.add(new Move("Iron Tail", "The user attacks with a steel-hard tail.", Move.Classification.TM, "Steel", null));
        moveList.add(new Move("Wild Charge", "User shocks the target at the cost of some recoil damage.", Move.Classification.TM, "Electric", null)); // Electric again for emphasis
        moveList.add(new Move("Dark Pulse", "The user releases a horrible aura imbued with dark thoughts.", Move.Classification.TM, "Dark", null));
        moveList.add(new Move("Bug Buzz", "The user vibrates its wings to generate a damaging noise.", Move.Classification.TM, "Bug", null));
        moveList.add(new Move("Rock Slide", "Large boulders are hurled at opposing Pokémon.", Move.Classification.TM, "Rock", null));
        moveList.add(new Move("Dragon Tail", "The user knocks the target away and drags out another Pokémon.", Move.Classification.TM, "Dragon", null));
        moveList.add(new Move("Waterfall", "The user charges at the target and may make it flinch.", Move.Classification.TM, "Water", null));
        moveList.add(new Move("Flying Press", "User strikes the target with a double slap that combines Fighting and Flying types.", Move.Classification.TM, "Fighting", null));
        moveList.add(new Move("Fairy Wind", "Stirs up a fairy wind that damages the target.", Move.Classification.TM, "Fairy", null));
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
