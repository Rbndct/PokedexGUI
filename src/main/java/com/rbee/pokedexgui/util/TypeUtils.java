package com.rbee.pokedexgui.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The type Type utils.
 */
public class TypeUtils {

    private static final String[] VALID_TYPES = {
            "normal", "fire", "water", "electric", "grass", "ice",
            "fighting", "poison", "ground", "flying", "psychic", "bug",
            "rock", "ghost", "dragon", "dark", "steel", "fairy"
    };

    private static final Image PLACEHOLDER_IMAGE = new Image(
            TypeUtils.class.getResourceAsStream("/com/rbee/pokedexgui/images/multiplepokeballs-logo.png"),
            24, 24, true, true);

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static String[] getValidTypes() {
        return VALID_TYPES;
    }

    /**
     * Loads and sets the type icon image into the given ImageView.
     * Falls back to a placeholder image if loading fails.
     *
     * @param type      the Pokémon type name (e.g., "fire")
     * @param imageView the ImageView to set the icon on
     */
    public static void loadTypeIcon(String type, ImageView imageView) {
        if (type == null || type.isBlank()) {
            imageView.setImage(null);
            return;
        }

        imageView.setFitWidth(120);
        imageView.setFitHeight(46);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        String lowerType = type.toLowerCase();
        String url = "https://raw.githubusercontent.com/msikma/pokesprite/master/misc/types/masters/" + lowerType + ".png";

        Image image = new Image(url, 120, 46, true, true, true);

        image.errorProperty().addListener((obs, wasError, isError) -> {
            if (isError) imageView.setImage(PLACEHOLDER_IMAGE);
        });

        image.progressProperty().addListener((obs, oldProg, newProg) -> {
            if (newProg.doubleValue() >= 1.0 && !image.isError()) {
                imageView.setImage(image);
            }
        });
    }

    /**
     * Asynchronously fetches weaknesses for a Pokémon given its primary and secondary types.
     * Returns a CompletableFuture of a Map of type names to combined damage multipliers.
     *
     * Example usage:
     * TypeUtils.fetchWeaknessesAsync("fire", "ice").thenAccept(weaknesses -> {
     *     // Use the weaknesses map here in JavaFX Application Thread (or wrap with Platform.runLater)
     * });
     *
     * @param primaryType   Primary Pokémon type (e.g., "fire")
     * @param secondaryType Secondary Pokémon type, may be null or blank
     * @return CompletableFuture<Map<String, Double>> with damage multipliers for types > 1.0
     */
    public static CompletableFuture<Map<String, Double>> fetchWeaknessesAsync(String primaryType, String secondaryType) {
        CompletableFuture<Map<String, Double>> primaryFuture = fetchTypeDamageRelations(primaryType);
        CompletableFuture<Map<String, Double>> secondaryFuture = (secondaryType == null || secondaryType.isBlank())
                ? CompletableFuture.completedFuture(Collections.emptyMap())
                : fetchTypeDamageRelations(secondaryType);

        return primaryFuture.thenCombine(secondaryFuture, (primaryMap, secondaryMap) -> {
            Map<String, Double> combined = new HashMap<>();

            // Initialize all types with multiplier 1.0
            for (String type : VALID_TYPES) {
                combined.put(type, 1.0);
            }

            // Multiply primary type multipliers
            for (Map.Entry<String, Double> e : primaryMap.entrySet()) {
                combined.put(e.getKey(), combined.getOrDefault(e.getKey(), 1.0) * e.getValue());
            }

            // Multiply secondary type multipliers
            for (Map.Entry<String, Double> e : secondaryMap.entrySet()) {
                combined.put(e.getKey(), combined.getOrDefault(e.getKey(), 1.0) * e.getValue());
            }

            // Filter to only weaknesses (>1.0 multiplier)
            combined.entrySet().removeIf(entry -> entry.getValue() <= 1.0);

            return combined;
        });
    }

    /**
     * Helper method to fetch damage relations for a given type from PokeAPI asynchronously.
     * Returns a map of attacking type -> damage multiplier against the given type.
     *
     * @param type Pokémon type to fetch (e.g., "fire")
     * @return CompletableFuture<Map<String, Double>> of damage multipliers
     */
    private static CompletableFuture<Map<String, Double>> fetchTypeDamageRelations(String type) {
        String url = "https://pokeapi.co/api/v2/type/" + type.toLowerCase();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> {
                    Map<String, Double> multipliers = new HashMap<>();
                    try {
                        JSONObject json = new JSONObject(body);
                        JSONObject damageRelations = json.getJSONObject("damage_relations");

                        JSONArray doubleDamageFrom = damageRelations.getJSONArray("double_damage_from");
                        JSONArray halfDamageFrom = damageRelations.getJSONArray("half_damage_from");
                        JSONArray noDamageFrom = damageRelations.getJSONArray("no_damage_from");

                        // super effective = 2.0
                        for (int i = 0; i < doubleDamageFrom.length(); i++) {
                            String t = doubleDamageFrom.getJSONObject(i).getString("name");
                            multipliers.put(t, 2.0);
                        }
                        // not very effective = 0.5
                        for (int i = 0; i < halfDamageFrom.length(); i++) {
                            String t = halfDamageFrom.getJSONObject(i).getString("name");
                            multipliers.put(t, 0.5);
                        }
                        // immune = 0.0
                        for (int i = 0; i < noDamageFrom.length(); i++) {
                            String t = noDamageFrom.getJSONObject(i).getString("name");
                            multipliers.put(t, 0.0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return multipliers;
                });
    }
}
