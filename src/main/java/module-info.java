module com.rbee.pokedexgui {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;

    // Additional Java modules
    requires java.desktop;
    requires java.net.http;

    // External libraries
    requires com.jfoenix;
    requires org.json;
    requires com.google.gson;

    // Export main app package
    exports com.rbee.pokedexgui.app;

    // Controller packages for FXML
    exports com.rbee.pokedexgui.controller.loading;
    opens com.rbee.pokedexgui.controller.loading to javafx.fxml;

    exports com.rbee.pokedexgui.controller.main;
    opens com.rbee.pokedexgui.controller.main to javafx.fxml;

    exports com.rbee.pokedexgui.controller.pokemon;
    opens com.rbee.pokedexgui.controller.pokemon to javafx.fxml;

    exports com.rbee.pokedexgui.controller.move;
    opens com.rbee.pokedexgui.controller.move to javafx.fxml;

    exports com.rbee.pokedexgui.controller.item;
    opens com.rbee.pokedexgui.controller.item to javafx.fxml;

    exports com.rbee.pokedexgui.controller.trainer;
    opens com.rbee.pokedexgui.controller.trainer to javafx.fxml;

    // Model packages
    exports com.rbee.pokedexgui.model.pokemon;
    exports com.rbee.pokedexgui.model.trainer;
    exports com.rbee.pokedexgui.model.item;
    exports com.rbee.pokedexgui.model.move;

    // Manager and utility
    exports com.rbee.pokedexgui.manager;
    exports com.rbee.pokedexgui.util;

    // Open model and util packages to Gson for reflection
    opens com.rbee.pokedexgui.model.pokemon to com.google.gson;
    opens com.rbee.pokedexgui.model.trainer to com.google.gson;
    opens com.rbee.pokedexgui.model.item to com.google.gson;
    opens com.rbee.pokedexgui.model.move to com.google.gson;
    opens com.rbee.pokedexgui.util to com.google.gson;
    opens com.rbee.pokedexgui.manager to com.google.gson;
}
