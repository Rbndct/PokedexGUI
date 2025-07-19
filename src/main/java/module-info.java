module com.rbee.pokedexgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    exports com.rbee.pokedexgui.main;
    opens com.rbee.pokedexgui.main to javafx.fxml;

}