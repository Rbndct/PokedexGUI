package com.rbee.pokedexgui.util;

import java.util.List;

/**
 * The type Type utils.
 */
public class TypeUtils {

    private static final String[] VALID_TYPES = {
        "normal", "fire", "water", "electric", "grass", "ice",
        "fighting", "poison", "ground", "flying", "psychic", "bug",
        "rock", "ghost", "dragon", "dark", "steel", "fairy"
    };



    public static String[] getValidTypes() {
        return VALID_TYPES;
    }
}
