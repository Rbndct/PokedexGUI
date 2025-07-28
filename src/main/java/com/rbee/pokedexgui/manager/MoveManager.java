package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.move.Move;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The type Move manager.
 */
@SuppressWarnings("squid:S106")
public class MoveManager {

    private static MoveManager instance; // Singleton instance

    private final ObservableList<Move> moveList = FXCollections.observableArrayList();
    private final ObservableList<Move> recentAdditions = FXCollections.observableArrayList();

    // Private constructor for singleton
    public MoveManager() {
        moveList.add(new Move("Tackle", "Tackle is one of the most common and basic moves a Pokémon learns. It deals damage with no additional effects.", Move.Classification.TM, "Normal", ""));
        moveList.add(new Move("Defend", "Raises user's defense stat temporarily.", Move.Classification.TM, "Normal", ""));
        moveList.add(new Move("Cut", "A basic HM move that can be used to cut down small trees.", Move.Classification.HM, "Normal", ""));
        moveList.add(new Move("Surf", "Ride a huge wave to strike all targets. Can cross water.", Move.Classification.HM, "Water", ""));
        moveList.add(new Move("Fly", "Soar up and strike on the next turn. Also travels between towns.", Move.Classification.HM, "Flying", ""));
        moveList.add(new Move("Flamethrower", "A powerful blast of fire.", Move.Classification.TM, "Fire", ""));
        moveList.add(new Move("Ice Beam", "Blasts a freezing beam that may freeze the target.", Move.Classification.TM, "Ice", ""));
        moveList.add(new Move("Thunderbolt", "A strong electric blast crashes down on the target.", Move.Classification.TM, "Electric", ""));
    }

    // Singleton accessor
    public static MoveManager getInstance() {
        if (instance == null) {
            instance = new MoveManager();
        }
        return instance;
    }

    public ObservableList<Move> getMoveList() {
        return moveList;
    }

    public ObservableList<Move> getRecentAdditions() {
        return recentAdditions;
    }

    public boolean isMoveNameTaken(String name) {
        for (Move m : moveList) {
            if (m.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void addMove(Move newMove) {
        if (newMove != null) {
            moveList.add(newMove);
            recentAdditions.add(0, newMove); // Add to front

            if (recentAdditions.size() > 5) {
                recentAdditions.remove(recentAdditions.size() - 1);
            }
        }
    }

    public int getTotalMoveCount() {
        return moveList.size();
    }

    public long getTotalTMCount() {
        return moveList.stream()
                .filter(move -> move.getClassification() == Move.Classification.TM)
                .count();
    }

    public long getTotalHMCount() {
        return moveList.stream()
                .filter(move -> move.getClassification() == Move.Classification.HM)
                .count();
    }

    public Move getMoveByName(String name) {
        for (Move move : moveList) {
            if (move.getName().equalsIgnoreCase(name)) {
                return move;
            }
        }
        return null;
    }
}
