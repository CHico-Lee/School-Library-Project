/**
* COMP 580 Final Project (Spring 2017)
* A School Library Database System using SQLite, support feature to searching,
* borrowing, and returning books.
*
* @author  Chi Ho Lee, Bijay Maharjan
* @since   2017-4-26
*/
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

	// MainView is the top panel with a Home button and Library name.
	private void showMainView() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/MainView.fxml"));
		mainLayout = loader.load();
		Scene scene = new Scene(mainLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	// MainItem is the center section with 3 buttons
	public static void showMainItems() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("view/MainItems.fxml"));
		BorderPane mainItems = loader.load();
		mainLayout.setCenter(mainItems);
	}
	
	// SearchBookScene will replace the center section.
	public static void showSearchBookScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("searchBook/SearchBook.fxml"));
		BorderPane searchBook = loader.load();
		mainLayout.setCenter(searchBook);
	}
	
	// BorrowBookScene will replace the center section.
	public static void showBorrowBookScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("borrowBook/BorrowBook.fxml"));
		BorderPane borrowBook = loader.load();
		mainLayout.setCenter(borrowBook);
	}
	
	// ReturnBookScene will replace the center section.
	public static void showReturnBookScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("returnBook/ReturnBook.fxml"));
		BorderPane returnBook = loader.load();
		mainLayout.setCenter(returnBook);
	}
	
	// BookDetailsScene will replace the center section.
	public static void showBookDetailsScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("bookDetails/BookDetails.fxml"));
		BorderPane bookDetails = loader.load();
		mainLayout.setCenter(bookDetails);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
