package schoolLibrary;


import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;
	private static BorderPane mainLayout;
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("School Library System");
		
		showMainView();
		showMainItems();
	}

	private void showMainView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/MainView.fxml"));
		mainLayout = loader.load();
		Scene scene = new Scene(mainLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void showMainItems() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/MainItems.fxml"));
		BorderPane mainItems = loader.load();
		mainLayout.setCenter(mainItems);
	}
	
	public static void showSearchBookScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("searchBook/SearchBook.fxml"));
		BorderPane searchBook = loader.load();
		mainLayout.setCenter(searchBook);
	}
	
	public static void showBorrowBookScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("borrowBook/BorrowBook.fxml"));
		BorderPane borrowBook = loader.load();
		mainLayout.setCenter(borrowBook);
	}
	
	public static void showReturnedBookScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("returnedBook/returnedBook.fxml"));
		BorderPane returnedBook = loader.load();
		mainLayout.setCenter(returnedBook);
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
