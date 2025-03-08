package fxmlconverter.view;

import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import fxmlconverter.resources.Resources;
import fxmlconverter.controller.MainController;

public class MainView extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("start");
		ResourceBundle bundle = Resources.getBundle("strings", Locale.FRENCH);
		//ResourceBundle bundle = ResourceBundle.getBundle("bundles.strings", Locale.FRENCH);

		FXMLLoader fxmlLoader = new FXMLLoader(Resources.getURL("views/MainView.fxml"), bundle);
		Scene scene = new Scene(fxmlLoader.load());
		
		MainController controller = (MainController) fxmlLoader.getController();
		controller.setStage(primaryStage);
		
		scene.getStylesheets().add(Resources.getURL("views/css/application.css").toExternalForm());
		scene.setOnKeyReleased(ev -> {
			if (ev.getCode() == KeyCode.F11)
				controller.fullScreen();
		});
		
        System.out.println(System.getProperty("java.version"));
        System.out.println(System.getProperty("javafx.version"));

		System.out.println("resources");
		InputStream input = Resources.getImage("icon.png");
		System.out.println("icon");
		primaryStage.getIcons().add(new Image(input));
		primaryStage.setTitle(bundle.getString("window.title"));
		primaryStage.setScene(scene);
		System.out.println("show");
		primaryStage.show();
		System.out.println("end");
	}

	public static void main(String[] args) {
		System.out.println("main");
		launch();
	}

}
