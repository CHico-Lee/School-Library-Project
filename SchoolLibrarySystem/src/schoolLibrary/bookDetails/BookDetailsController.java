/**
* COMP 580 Final Project (Spring 2017)
* Controller class to displays detail book information after user selected a book.
*
* @author  Chi Ho Lee, Bijay Maharjan
* @since   2017-4-26
*/
package schoolLibrary.bookDetails;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import schoolLibrary.Main;
import schoolLibrary.borrowBook.BorrowBookController;

public class BookDetailsController {
	
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
	private Label ratingLbl;
	
	@FXML
	private Button selectBtn;
	
	private Connection connection;
	private Main main;
	
	// variable to store Book ISBN going to display.
	private static String selectedIsbn = null;
	/**
	 * This is a static method accept the ISBN before initialize the scene.
	 * @param  isbn  Book ISBN in String format.
	 */
	public static void setIsbn(String isbn) {
		selectedIsbn = isbn;
	}
	
    @FXML
    private void initialize() throws SQLException {
    	// load the sqlite-JDBC driver using the current class loader
    	try {
    		Class.forName( "org.sqlite.JDBC" );
    	} catch (ClassNotFoundException e1) {
    	}
    	
        // connect to the database
        try {
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			
	        // generate parameterized sql to get a single book information, also calculate avg of the rating.
			String sql = "SELECT Book.ISBN AS book_isbn, Book.Title AS book_title, Book.Author AS book_author," + 
							" Book.Publisher AS book_pub, Book.Year AS book_year, Book.Description AS book_des," + 
							" (Book.CategoryId || ' - ' || Category.CategoryName) AS book_cat," +
							" ifnull(ROUND(AVG(Borrow.Rating), 2), 'Not rated') AS rating"+
							" FROM Book" + 
							" JOIN Category USING (CategoryId)" +
							" LEFT JOIN Borrow USING (ISBN)" +
							" WHERE Book.ISBN = ?;";
				
			// prepared statement
			PreparedStatement stmt= connection.prepareStatement( sql );
			stmt.setString( 1, selectedIsbn);
						
			// get results
			ResultSet res = stmt.executeQuery();
			
			// put data into label.
			isbnLbl.setText(selectedIsbn);
			
			// Update corresponding labels from result.
			if ( res.next() ) {
				titleLbl.setText(res.getString("book_title"));
				authorLbl.setText(res.getString("book_author"));
				pubLbl.setText(res.getString("book_pub"));
				yearLbl.setText(res.getString("book_year"));
				desLbl.setText(res.getString("book_des"));
				catLbl.setText(res.getString("book_cat"));
				ratingLbl.setText(res.getString("rating"));
			}
        } catch (SQLException e) {
			handleError(e);
		} finally {
			if (connection != null) {
				try {
		    	// Avoid SQLite_BUSY Error - database is locked
					connection.close();
				} catch (SQLException e) {
					handleError(e);
				}
			}
		} 
    }	// end of initialize()

	private void handleError(Exception e) {
		// Alert the user when things go terribly wrong
		Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
		// Close the program when the user clicks close on the alert
		alert.setOnCloseRequest(event -> Platform.exit());
		// show the alert
		alert.show();
	}
	
	// Method to handle button click to Borrow this book.
	@FXML
	private void clickSelect() throws IOException {
		// Passing ISBN to borrow book controller. 
		BorrowBookController.setIsbn(selectedIsbn);
		main.showBorrowBookScene();
	}

}
