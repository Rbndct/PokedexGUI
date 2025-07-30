package com.rbee.pokedexgui.model.move;


/**
 * The type Move.
 */
@SuppressWarnings("squid:S106")
public class Move {

    private final String name;
    private final String description;
    private final Classification classification;
    private final String primaryType;
    private final String secondaryType;

    /**
     * Instantiates a new Move.
     *
     * @param name           the name
     * @param description    the description
     * @param classification the classification
     * @param primaryType    the primary type
     * @param secondaryType  the secondary type
     */
    public Move(String name, String description, Classification classification, String primaryType,
                String secondaryType) {
        this.name = name;
        this.description = description;
        this.classification = classification;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
    }

    /**
     * Instantiates a new Move.
     *
     * @param other the other
     */
    public Move(Move other) {
        this.name = other.name;
        this.description = other.description;
        this.classification = other.classification; // assuming Classification is an enum or immutable
        this.primaryType = other.primaryType;
        this.secondaryType = other.secondaryType;
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
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets classification.
     *
     * @return the classification
     */
    public Classification getClassification() {
        return classification;
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
     * The enum Classification.
     */
    public enum Classification {
        /**
         * Hm classification.
         */
        HM,
        /**
         * Tm classification.
         */
        TM,
    }

    /**
     * Gets types.
     *
     * @return the types
     */
    public String getTypes() {
        if (secondaryType != null && !secondaryType.isEmpty()) {
            return primaryType + "/" + secondaryType;
        }
        return primaryType;
    }


}