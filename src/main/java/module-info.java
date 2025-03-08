module fxmlconverter {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.base;
    requires transitive javafx.graphics;
    requires java.desktop;
	requires java.xml;

    opens fxmlconverter.view to javafx.fxml;
    opens fxmlconverter.controller to javafx.fxml;
    exports fxmlconverter.view;
}
