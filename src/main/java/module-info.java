module com.rbee.pokedexgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.jfoenix;

    exports com.rbee.pokedexgui;

    exports com.rbee.pokedexgui.controller.loading;
    opens com.rbee.pokedexgui.controller.loading to javafx.fxml;

    exports com.rbee.pokedexgui.controller.main;
    opens com.rbee.pokedexgui.controller.main to javafx.fxml;
}
