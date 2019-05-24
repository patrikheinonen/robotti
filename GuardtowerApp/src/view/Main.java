package view;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * 
 * @author Paavo Mattila, Joonas Taivalmaa, Patrik Laine
 * 
 *         This class is the Main class, which is executed. It sets up the GUI
 *         and assigns controller to them
 */
public class Main extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("GuardTower");

		initRootLayout();

		showDefaultOverview();
	}

	/**
	 * This initializes root layout.
	 */
	public void initRootLayout() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method loads up the GUI, assigns a controller to it and and draws a map
	 * on the GUI.
	 */

	public void showDefaultOverview() {
		try {
			// Ladataan DefaultOverview
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("DefaultView.fxml"));
			AnchorPane personOverview = (AnchorPane) loader.load();

			// Määritetään kontrolleri ja piirretään kartta.
			rootLayout.setCenter(personOverview);
			DefaultViewController controller = loader.getController();
			controller.drawMap();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param args This starts the program.
	 */

	public static void main(String[] args) {

		launch(args);
	}
}
