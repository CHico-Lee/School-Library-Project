/**
* COMP 580 Final Project (Spring 2017)
* Controller class to borrow book by inserting a new borrow using SQLite insert query.
*
* @author  Chi Ho Lee, Bijay Maharjan
* @since   2017-4-26
*/
package schoolLibrary.borrowBook;

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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class BorrowBookController {
		
	@FXML
	private TextField memberIdTxtfield;
	@FXML
	private TextField isbnTxtfield;
	
	@FXML
	private Label nameLbl;
	@FXML
	private Label bookLbl;
	@FXML
	private Label dueDateLbl;
	
	@FXML
	private Button borrowBtn;

	private Connection connection;
	private static long DAY_IN_MS = 1000 * 60 * 60 * 24;
	private boolean validMemId;
	private boolean validIsbn;
	
	// variable to store Book ISBN going to display.
	private static String selectedIsbn = null;
	
	/**
	 * This is a static method accept the ISBN before initialize the scene.
	 * @param  isbn  Book ISBN in String format.
	 */
	public static void setIsbn(String isbn) {
		selectedIsbn = isbn;
	}
	
	/**
     * Initializes the controller class. 
     * This method is automatically called after the fxml file has been loaded.
	 * @throws SQLException 
     */
    @FXML
    private void initialize() throws SQLException {
    	// load the sqlite-JDBC driver using the current class loader
    	try {
    		Class.forName( "org.sqlite.JDBC" );
    	} catch (ClassNotFoundException e1) {
    	}
    	        
        // Whenever the user types in something, update
        // Registering event listener
        // obs is query.textProperty()
        // oldText and newText are old and new values of query.getText()
    	memberIdTxtfield.textProperty().addListener(      (obs, oldText, newText) -> {updateName();}    );
    	isbnTxtfield.textProperty().addListener(      (obs, oldText, newText) -> { updateBook(); }    );
        
    	// Add event handler for button.
    	borrowBtn.setOnAction((e)-> { clickBorrow(); } );
        
        // update initially shows everything, like in iTunes
        updateName();
        
        // If ISBN is received, display in the text box.
        if (selectedIsbn == null)
        	updateBook();
        else
        	isbnTxtfield.setText(selectedIsbn);
        	
        // Date for due of the book borrow today, hard-coded to due 7 days later.
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(System.currentTimeMillis() + (7 * DAY_IN_MS));
        dueDateLbl.setText(dateFormat.format(date));
    }	// end of initialize()
    
    /**
     * this method is called when the user changes the TextField's text
     */
	@FXML
	private void updateName() {
		try {
			// connect to the database
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			// The parameter comes from the query TextField
			// at first, it is empty
			validMemId = false;
			nameLbl.setText("");
			String param = memberIdTxtfield.getText();
						
			// generate parameterized sql for member information by Member ID
			String sql = "SELECT Member.FirstName AS fName, Member.LastName AS lName, Student.Major AS major, Faculty.Department As dept" + 
							" FROM Member" +
							" LEFT JOIN Student USING (memberId)" +
							" LEFT JOIN Faculty USING (memberId)" +
							" WHERE Member.MemberId = ?;";

			// prepared statement
			PreparedStatement stmt = connection.prepareStatement( sql );
			// Remove non-digit character.
			stmt.setString( 1, param.trim().replaceAll("[^\\d]", ""));
			
			// get results
			ResultSet res = stmt.executeQuery();
			// Put result into label.
			if ( res.next() ) {
				String memberInfo = "";
				if (res.getString("major")==null)
					memberInfo += String.format("[Faculty - %s] ", res.getString("dept"));
				else
					memberInfo += String.format("[Student - %s] ", res.getString("major"));
				
				memberInfo += res.getString("fName") + " " + res.getString("lName");
				
				nameLbl.setText(memberInfo);
		        validMemId = true;
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
	}	// end of updateName()
	
	@FXML
	private void updateBook() {
		try {
			// connect to the database
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			
			validIsbn = false;
			// The parameter comes from the query TextField
			// at first, it is empty
			bookLbl.setText("");
			String param = isbnTxtfield.getText();
						
			// generate parameterized sql for book information
			String sql = "SELECT Book.ISBN AS book_isbn, Book.Title AS book_title, Book.Author AS book_author" +
					" FROM Book" +
					" WHERE Book.ISBN = ?;";

			// prepared statement
			PreparedStatement stmt = connection.prepareStatement( sql );
			// Remove non-digit character.
			stmt.setString( 1, param.trim().replaceAll("[^\\d]", ""));
			
			// get results
			ResultSet res = stmt.executeQuery();
			// Put result into label.
			if ( res.next() ) {
				bookLbl.setText(res.getString("book_title") + " by " + res.getString("book_author"));
				validIsbn = true;
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
	}	// end of updateBook() 
	
	@FXML
	private void clickBorrow() {
		try {
			// connect to the database
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			
			String paramMemId = memberIdTxtfield.getText();
			String paramIsbn = isbnTxtfield.getText();
						
			// Code below is for check book availability
			
			// generate parameterized sql, notAvailable will be 1 for non-return book; 0 for book available to borrow.
			String sqlAvailable = "SELECT COUNT(*) AS notAvailable FROM Borrow" +
									" WHERE Borrow.ISBN = ? AND Borrow.ReturnedDate IS NULL;";
				
			// prepared statement
			PreparedStatement stmtAvailable = connection.prepareStatement( sqlAvailable );
						
			stmtAvailable.setString( 1, paramIsbn.trim().replaceAll("[^\\d]", ""));
						
			// get results
			ResultSet resAvailable = stmtAvailable.executeQuery();
			
			// Case if Member Id or ISBN is not found.
			if (!validMemId ||!validIsbn){
				AlertMessage(AlertType.ERROR, "Incorrect Member ID or ISBN.", "Please make sure the Member Id and ISBN is correct");
				return;
			}
			// Case if Book is not return yet.
			else if (resAvailable.getString("notAvailable").equals("1") ) {
				AlertMessage(AlertType.ERROR, "This book is currently unavailable to borrow.", "Please look for another book.");
				return;
			}
			
			// Code below is for borrowing book.
			
			// DateFormat to store in the database, eg: 2017-05-01 13:01:00
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date currDate = new Date();
	        Date dueDate = new Date(System.currentTimeMillis() + (7 * DAY_IN_MS));	     		
	        
			// generate parameterized sql for new borrowing record.
			String sql = "INSERT INTO Borrow (ISBN, MemberId, BorrowDate, DueDate, ReturnedDate, Rating)" +
							" VALUES" +
							" (?, ?, ?, ?, NULL, NULL);";
	
			// prepared statement
			PreparedStatement stmt = connection.prepareStatement( sql );
			
			stmt.setString( 1, paramIsbn.trim().replaceAll("[^\\d]", ""));
			stmt.setString( 2, paramMemId.trim().replaceAll("[^\\d]", ""));
			stmt.setString( 3, dateFormat.format(currDate));
			stmt.setString( 4, dateFormat.format(dueDate));
			
			// get results, res will hold the number of records change by the query.
			int res = stmt.executeUpdate();
			
			// If a single record is insert to the borrow table, borrow succeed.
			if (res == 1) {
				// Show confirm message by pop-up window.
				AlertMessage(AlertType.INFORMATION, "Thank You for borrowing!", 
						"Please return the book by " + dateFormat.format(dueDate) + ".");
			}
			else {
				// Show error message by pop-up window.
				AlertMessage(AlertType.ERROR, "There is an Error!", "Please ask an librarian for help.");
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
		
	}	//end of clickBorrow()

	private void handleError(Exception e) {
		// Alert the user when things go terribly wrong
		Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
		// Close the program when the user clicks close on the alert
		alert.setOnCloseRequest(event -> Platform.exit());
		// show the alert
		alert.show();
	}
	
	// Helper method to display Pop-up message.
	private void AlertMessage(AlertType type, String headerMessage, String message) {
		Alert alert = new Alert(type, message, ButtonType.CLOSE);
		alert.setHeaderText(headerMessage);
		// show the alert
		alert.show();
	}

}
