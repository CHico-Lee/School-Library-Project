package schoolLibrary.bookDetails;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import schoolLibrary.Main;
import schoolLibrary.searchBook.Row;

public class BookDetailsController {

	private Connection connection;
	
	@FXML
	private Label isbnLbl;
	@FXML
	private Label titleLbl;
	@FXML
	private Label authorLbl;
	@FXML
	private Label pubLbl;
	@FXML
	private Label yearLbl;
	@FXML
	private Label desLbl;
	@FXML
	private Label catLbl;
	
	@FXML
	private Button selectBtn;
	
	private Main main;
	
	
	private static String selectedIsbn = null;
	
	public static void setIsbn(String isbn) {
		selectedIsbn = isbn;
	}
	
	 
	    @FXML
	    private void initialize() throws SQLException {
	    	// load the sqlite-JDBC driver using the current class loader
	    	try {
	    		Class.forName( "org.sqlite.JDBC" );
	    	} catch (ClassNotFoundException e1) {
	    	};
	    	
	        // connect to the database
	        try {
				connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			} catch (SQLException e) {
				handleError(e);
			}
	        
	        // generate parameterized sql
 			String sql = "SELECT Book.ISBN AS book_isbn, Book.Title AS book_title, Book.Author AS book_author," + 
 							" Book.Publisher AS book_pub, Book.Year AS book_year, Book.Description AS book_des," + 
 							" Book.CategoryId AS book_cat" +
 							" FROM BOOK" + 
 							" JOIN CATEGORY USING (CategoryId)" +
 							" WHERE Book.ISBN = ?;";
 				
 			// prepared statement
 			PreparedStatement stmt= connection.prepareStatement( sql );
 						
 			stmt.setString( 1, selectedIsbn);
 						
 			// get results
 			ResultSet res = stmt.executeQuery();
 			
 			isbnLbl.setText(selectedIsbn);
 			
 			while ( res.next() ) {
 				titleLbl.setText(res.getString("book_title"));
 				authorLbl.setText(res.getString("book_author"));
 				pubLbl.setText(res.getString("book_pub"));
 				yearLbl.setText(res.getString("book_year"));
 				desLbl.setText(res.getString("book_des"));
 				catLbl.setText(res.getString("book_cat"));
 			}
	    }
	
		private void handleError(Exception e) {
			// Alert the user when things go terribly wrong
			Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
			// Close the program when the user clicks close on the alert
			alert.setOnCloseRequest(event -> Platform.exit());
			// show the alert
			alert.show();
		}

}
