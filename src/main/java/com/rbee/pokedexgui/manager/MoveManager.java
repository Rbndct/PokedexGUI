package com.rbee.pokedexgui.manager;

import com.rbee.pokedexgui.model.move.Move;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Scanner;

/**
 * The type Move manager.
 */
@SuppressWarnings("squid:S106")
public class MoveManager {

    private final ObservableList<Move> moveList;


    public MoveManager() {
        moveList = FXCollections.observableArrayList();
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
     * Load default moves.
     */
    public void loadDefaultMoves() {
        moveList.add(new Move("Tackle",
                "Tackle is one of the most common and basic moves a Pokémon learns. It deals damage with no additional effects.",
                Move.Classification.TM, "Normal", ""));
        moveList.add(new Move("Defend",
                "Raises user's defense stat temporarily.",
                Move.Classification.TM, "Normal", ""));
        moveList.add(new Move("Cut",
                "A basic HM move that can be used to cut down small trees.",
                Move.Classification.HM, "Normal", ""));
        moveList.add(new Move("Surf",
                "Ride a huge wave to strike all targets. Can cross water.",
                Move.Classification.HM, "Water", ""));
        moveList.add(new Move("Fly",
                "Soar up and strike on the next turn. Also travels between towns.",
                Move.Classification.HM, "Flying", ""));
        moveList.add(new Move("Flamethrower",
                "A powerful blast of fire.",
                Move.Classification.TM, "Fire", ""));
        moveList.add(new Move("Ice Beam",
                "Blasts a freezing beam that may freeze the target.",
                Move.Classification.TM, "Ice", ""));
        moveList.add(new Move("Thunderbolt",
                "A strong electric blast crashes down on the target.",
                Move.Classification.TM, "Electric", ""));
    }

    // You can add getter for moveList if needed for bindings:
    public ObservableList<Move> getMoveList() {
        return moveList;
    }
}
