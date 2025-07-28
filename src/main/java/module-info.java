module com.rbee.pokedexgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.jfoenix;
    requires java.net.http;

    // Add JSON library
    requires org.json;

    // Export main application package (assuming your main app class is here)
    exports com.rbee.pokedexgui.app;

    // Export and open controller packages to javafx.fxml for FXML loading
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

    // Added trainer controller package export and open for FXMLLoader
    exports com.rbee.pokedexgui.controller.trainer;
    opens com.rbee.pokedexgui.controller.trainer to javafx.fxml;
}
