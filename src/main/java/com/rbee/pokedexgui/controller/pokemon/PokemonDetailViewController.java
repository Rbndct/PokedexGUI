package com.rbee.pokedexgui.controller.pokemon;

import com.rbee.pokedexgui.manager.MoveManager;
import com.rbee.pokedexgui.manager.PokemonManager;
import com.rbee.pokedexgui.model.move.Move;
import com.rbee.pokedexgui.model.pokemon.Pokemon;
import com.rbee.pokedexgui.util.TypeUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import com.jfoenix.controls.JFXListView;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONObject;

public class PokemonDetailViewController {

    @FXML private ImageView pokemonSpriteImageView;
    @FXML private Label pokemonNameLabel;
    @FXML private ImageView primaryTypeIcon;
    @FXML private ImageView secondaryTypeIcon;
    @FXML private ScrollPane descriptionScrollPane;
    @FXML private Label descriptionLabel;

    @FXML private GridPane statsGrid;
    @FXML private Label hpStatLabel;
    @FXML private Label attackStatLabel;
    @FXML private Label defenseStatLabel;
    @FXML private Label spAtkStatLabel;
    @FXML private Label spDefStatLabel;
    @FXML private Label speedStatLabel;
    @FXML private Label totalStatLabel;

    @FXML private FlowPane pokemonWeaknessFlowPane;
    @FXML private JFXListView<Move> pokemonMoveSet;



    @FXML private HBox evolutionHBox;

    private PokemonManager pokemonManager;

    private Pokemon currentPokemon;




    @FXML
    public void initialize() {
        setupPokemonMoveSetCellFactory();
    }

    // call this AFTER FXMLLoader loads and after you set the manager
    public void loadPokemonDetails(Pokemon pokemon) {
        if (pokemonManager == null) {
            System.err.println("pokemonManager is not set yet!");
        }
        setPokemon(pokemon);
    }

    public void setPokemonManager(PokemonManager manager) {
        this.pokemonManager = manager;
    }

    public void setPokemon(Pokemon pokemon) {
        if (pokemon == null) return;
        this.currentPokemon = pokemon;

        // Set name with pokedex number in front
        pokemonNameLabel.setText(String.format("#%03d %s", pokemon.getPokedexNumber(), pokemon.getName()));

        // Show loading text while fetching description
        descriptionLabel.setText("Loading description...");

        // Fetch description asynchronously
        fetchPokemonDescription(pokemon.getPokedexNumber(), pokemon.getName());

        // Set stats using PokemonStats object
        hpStatLabel.setText("HP: " + pokemon.getPokemonStats().getHp());
        attackStatLabel.setText("Attack: " + pokemon.getPokemonStats().getAttack());
        defenseStatLabel.setText("Defense: " + pokemon.getPokemonStats().getDefense());
        spAtkStatLabel.setText("Sp. Atk: " + pokemon.getPokemonStats().getSpAttack());
        spDefStatLabel.setText("Sp. Def: " + pokemon.getPokemonStats().getSpDefense());
        speedStatLabel.setText("Speed: " + pokemon.getPokemonStats().getSpeed());

        // Calculate total stats
        int total = pokemon.getPokemonStats().getHp()
                + pokemon.getPokemonStats().getAttack()
                + pokemon.getPokemonStats().getDefense()
                + pokemon.getPokemonStats().getSpAttack()
                + pokemon.getPokemonStats().getSpDefense()
                + pokemon.getPokemonStats().getSpeed();

        totalStatLabel.setText(String.valueOf(total));

        // Load type icons using TypeUtils
        TypeUtils.loadTypeIcon(pokemon.getPrimaryType(), primaryTypeIcon);
        if (pokemon.getSecondaryType() != null && !pokemon.getSecondaryType().isEmpty()) {
            TypeUtils.loadTypeIcon(pokemon.getSecondaryType(), secondaryTypeIcon);
        } else {
            secondaryTypeIcon.setImage(null);
        }

        // Load sprite
        updatePokemonSprite(pokemon.getPokedexNumber());

        populatePokemonMoves(pokemon);

        displayWeaknesses(pokemon.getPrimaryType(), pokemon.getSecondaryType());
        displayEvolutionChain(pokemon.getPokemonEvolutionInfo());
    }

    private void populatePokemonMoves(Pokemon pokemon) {
        if (pokemon == null) {
            pokemonMoveSet.setItems(FXCollections.emptyObservableList());
            return;
        }

        pokemonMoveSet.setItems(pokemon.getMoveSet());
    }



    private void fetchPokemonDescription(int pokedexNumber, String customName) {
        HttpClient client = HttpClient.newHttpClient();
        String url = "https://pokeapi.co/api/v2/pokemon-species/" + pokedexNumber + "/";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(responseBody -> {
                    String description = parseDescriptionFromJson(responseBody, customName);
                    Platform.runLater(() -> descriptionLabel.setText(description));
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    Platform.runLater(() -> descriptionLabel.setText("Description not available."));
                    return null;
                });
    }




    private String parseDescriptionFromJson(String jsonString, String customName) {
        JSONObject json = new JSONObject(jsonString);

        // Get the official Pokémon name from the JSON
        String officialName = json.getString("name");

        JSONArray entries = json.getJSONArray("flavor_text_entries");
        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.getJSONObject(i);
            if ("en".equals(entry.getJSONObject("language").getString("name"))) {
                String text = entry.getString("flavor_text");
                text = text.replace("\n", " ").replace("\f", " ").trim();

                int periodIndex = text.indexOf('.');
                if (periodIndex != -1) {
                    text = text.substring(0, periodIndex + 1).trim();
                }

                // Replace official name (case-insensitive) with the custom name
                text = text.replaceAll("(?i)\\b" + Pattern.quote(officialName) + "\\b", customName);

                return text;
            }
        }
        return "No description found.";
    }



    private void updatePokemonSprite(int pokedexNumber) {
        String spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokedexNumber + ".png";

        Image placeholder = new Image(
                getClass().getResourceAsStream("/com/rbee/pokedexgui/images/multiplepokeballs-logo.png"),
                96, 96, true, true
        );

        Image sprite = new Image(spriteUrl, 96, 96, true, true, true);

        sprite.errorProperty().addListener((obs, wasError, isError) -> {
            if (isError) {
                pokemonSpriteImageView.setImage(placeholder);
            }
        });

        sprite.progressProperty().addListener((obs, oldProgress, newProgress) -> {
            if (newProgress.doubleValue() >= 1.0 && !sprite.isError()) {
                pokemonSpriteImageView.setImage(sprite);
            }
        });

        pokemonSpriteImageView.setImage(placeholder);
    }

    private ImageView createTypeImageView() {
        ImageView iv = new ImageView();
        iv.setFitWidth(24);
        iv.setFitHeight(24);
        iv.setPreserveRatio(true);
        return iv;
    }

    private void loadTypeIcon(String type, ImageView imageView) {
        if (type == null || type.isBlank()) {
            imageView.setImage(null);
            return;
        }

        String lowerType = type.toLowerCase();
        String url = "https://raw.githubusercontent.com/msikma/pokesprite/master/misc/types/masters/" + lowerType + ".png";

        Image image = new Image(url, 24, 24, true, true, true);

        image.errorProperty().addListener((obs, wasError, isError) -> {
            if (isError) imageView.setImage(null);
        });

        image.progressProperty().addListener((obs, oldProg, newProg) -> {
            if (newProg.doubleValue() >= 1.0 && !image.isError()) {
                imageView.setImage(image);
            }
        });
    }

    private void setupPokemonMoveSetCellFactory() {
        pokemonMoveSet.setCellFactory(lv -> new ListCell<Move>() {
            private final ImageView primaryTypeImageView = createTypeImageView();
            private final ImageView secondaryTypeImageView = createTypeImageView();
            private final Label nameLabel = new Label();
            private final HBox iconBox = new HBox(4);
            private final HBox container = new HBox(8);

            {
                iconBox.getChildren().addAll(primaryTypeImageView, secondaryTypeImageView);
                iconBox.setAlignment(Pos.CENTER_LEFT);

                container.getChildren().addAll(iconBox, nameLabel);
                container.setAlignment(Pos.CENTER_LEFT);

                // Set smaller fixed height for the cell
                setPrefHeight(30);
                setMinHeight(30);
                setMaxHeight(30);

                nameLabel.setTextFill(Color.WHITE);
                nameLabel.setStyle("-fx-font-weight: normal; -fx-font-size: 12px;"); // smaller font

                // Optional: Reduce padding/margin if needed
                container.setPadding(new Insets(2, 0, 2, 0));
            }

            @Override
            protected void updateItem(Move move, boolean empty) {
                super.updateItem(move, empty);

                if (empty || move == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                nameLabel.setText(move.getName());

                loadTypeIcon(move.getPrimaryType(), primaryTypeImageView);

                if (move.getSecondaryType() != null && !move.getSecondaryType().isEmpty()) {
                    loadTypeIcon(move.getSecondaryType(), secondaryTypeImageView);
                } else {
                    secondaryTypeImageView.setImage(null);
                }

                setText(null);
                setGraphic(container);
            }
        });
    }




    private void displayWeaknesses(String primaryType, String secondaryType) {
        TypeUtils.fetchWeaknessesAsync(primaryType, secondaryType)
                .thenAccept(weaknesses -> {
                    Platform.runLater(() -> {
                        pokemonWeaknessFlowPane.getChildren().clear();

                        for (String type : weaknesses.keySet()) {
                            ImageView icon = new ImageView();
                            TypeUtils.loadTypeIcon(type, icon);
                            icon.setFitWidth(70);  // Adjust width to fit ~3 per row in 260px width
                            icon.setFitHeight(40);
                            pokemonWeaknessFlowPane.getChildren().add(icon);
                        }
                    });
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private void displayEvolutionChain(Pokemon.PokemonEvolutionInfo evoInfo) {
        evolutionHBox.getChildren().clear();

        if (evoInfo == null || evoInfo == Pokemon.PokemonEvolutionInfo.NONE || currentPokemon == null) {
            return; // No evolution info to display
        }

        List<Integer> chain = new ArrayList<>();
        if (evoInfo.getEvolvesFromNumber() > 0)
            chain.add(evoInfo.getEvolvesFromNumber());

        chain.add(currentPokemon.getPokedexNumber()); // ✅ Use the correct field

        if (evoInfo.getEvolvesToNumber() > 0)
            chain.add(evoInfo.getEvolvesToNumber());

        for (int i = 0; i < chain.size(); i++) {
            int pokeNum = chain.get(i);
            PokemonManager.PokemonDisplayInfo info = pokemonManager.getPokemonDisplayInfo(pokeNum);

            VBox pokeBox = new VBox(5);
            pokeBox.setAlignment(Pos.CENTER);

            ImageView iconView = info.icon;
            Label nameLabel = new Label(info.displayName);
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");

            pokeBox.getChildren().addAll(iconView, nameLabel);
            evolutionHBox.getChildren().add(pokeBox);

            // Add arrow and evolution level label between Pokémon, if not last
            if (i < chain.size() - 1) {
                // Get the current Pokemon in the chain
                Pokemon current = pokemonManager.getByNumber(chain.get(i));
                int evoLevel = 0;
                if (current != null) {
                    evoLevel = current.getPokemonEvolutionInfo().getEvolutionLevel();
                }

                // Arrow label
                Label arrow = new Label("→");
                arrow.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

                // Evolution level label below arrow
                Label levelLabel = new Label(evoLevel > 0 ? "Lv. " + evoLevel : "");
                levelLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");

                // VBox for arrow + level
                VBox arrowBox = new VBox(0);
                arrowBox.setAlignment(Pos.CENTER);
                arrowBox.getChildren().addAll(arrow, levelLabel);

                evolutionHBox.getChildren().add(arrowBox);
            }
        }
    }
}
