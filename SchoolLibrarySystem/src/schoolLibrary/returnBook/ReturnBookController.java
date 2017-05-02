/**
* COMP 580 Final Project (Spring 2017)
* Controller class to return book using SQLite update query.
*
* @author  Bijay Maharjan, Chi Ho Lee
* @since   2017-4-26
*/
package schoolLibrary.returnBook;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import schoolLibrary.Main;
import javafx.scene.control.Button;


public class ReturnBookController {
	
	@FXML
	private TextField memberIdTxtfield;

	@FXML
	private Button selectBtn;
	
	@FXML
	private ComboBox<String> ratingCombox;
	
	@FXML
	private TableView<Row> table;

	// TableColumn maps a row type to a cell type
	// In this case, we map Row to a String corresponding to the 
	@FXML
	private TableColumn<Row,String> isbn;
	@FXML
	private TableColumn<Row,String> title;
	@FXML
	private TableColumn<Row,String> author;
	@FXML
	private TableColumn<Row,String> dueDate;

	// The list of rows to put into the table
	private ObservableList<Row> data;
	
	private Connection connection;
	private Main main;
	private boolean validMemId;
	
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
    	} 
    	catch (ClassNotFoundException e1) {
    	}
    	
    	// Get Rating value into ComboBox. 
        ratingCombox.getItems().addAll(
        		"1",
        		"2",
        		"3",
        		"4",
        		"5",
        		"6",
        		"7",
        		"8",
        		"9",
        		"10"
        		);
    	
        // Initialize the table with the 4 columns.
    	// Tells JavaFX how to map Row data to columns
    	// Registering callback function - how to get each cell data
    	// similar to registering event listener
    	isbn.setCellValueFactory(cellData -> cellData.getValue().getIsbn());
    	title.setCellValueFactory(cellData -> cellData.getValue().getTitle());
    	author.setCellValueFactory(cellData -> cellData.getValue().getAuthor());
    	dueDate.setCellValueFactory(cellData -> cellData.getValue().getDueDate());
        
        // Whenever the user types in something, update
        // Registering event listener
        // obs is query.textProperty()
        // oldText and newText are old and new values of query.getText()
        memberIdTxtfield.textProperty().addListener(      (obs, oldText, newText) -> {update();}    );
                
        // update initially shows everything, like in iTunes
        update();
    } // end of initialize()
    
    /**
     * this method is called when the user changes the TextField's text
     */
	@FXML
	private void update() {
		try {
			// connect to the database
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			
			validMemId = false;
			// The parameter comes from the query TextField
			// at first, it is empty
			String param = memberIdTxtfield.getText();
			
			// data (a list of Rows) is used to fill in the table
			// at first, raw ArryList() wrapped with FX wrapper (ObservableList<Row>)
			// ObservableList is a super class of observableArrayList
			data = FXCollections.observableArrayList();
			
			// generate parameterized sql
			String sql = "SELECT Book.ISBN AS book_isbn, Book.Title AS book_title, Book.Author AS book_author, Borrow.DueDate AS book_due" +
						" FROM Borrow" +
						" JOIN Book USING (ISBN)"+
						" WHERE Borrow.MemberId = ? AND Borrow.ReturnedDate IS NULL" +
						" ORDER BY book_isbn";
			
			// prepared statement
			PreparedStatement stmt = connection.prepareStatement( sql );
			
			// bind parameter(s)
			// In SQLite "?" is a place holder (act like variable)
			// 1 is the first place holder, 2 is the second place holder, etc
			stmt.setString( 1, param.trim() );				

			// get results
			ResultSet res = stmt.executeQuery();
			while ( res.next() ) {
				Row row = new Row(res.getString("book_isbn"), res.getString("book_title"), res.getString("book_author"), res.getString("book_due"));
				// add a row to the list
				data.add(row);
				validMemId = true;
			}
			// the table will now have a list of all the rows we got from the db
			table.setItems(data);
			
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
	} // end of update()
		
	@FXML
	private void clickSelect() throws IOException {
					
		//return function imp
		try {
			
			// connect to the database
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			
			// Get current selected row.
			Row selectedRow = table.getSelectionModel().getSelectedItem();
				
			// Check Member Id is corrected.
			if (!validMemId)
			{
				AlertMessage(AlertType.ERROR, "Incorrect Member ID.", "Please make sure the Member Id is correct");
				return;
			}
				
			// DateFormat to store in the database, eg: 2017-05-01 13:01:00
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date currDate = new Date();     		
	        
			// generate parameterized sql
			String sql = "UPDATE Borrow SET ReturnedDate = ?, Rating = ?"+
						" WHERE ISBN = ? AND MemberId = ? AND ReturnedDate IS NULL";
				
			// prepared statement
			PreparedStatement stmt = connection.prepareStatement( sql );
			
			stmt.setString( 1, dateFormat.format(currDate));
			stmt.setString( 2, ratingCombox.getValue());			// get rating from comboBox.
			stmt.setString( 3, selectedRow.getIsbn().getValue());	// get ISBN from selected row.
			stmt.setString( 4, memberIdTxtfield.getText().trim());	
			
			// get results, res will hold the number of records change by the query.
			int res = stmt.executeUpdate();
			
			// If a single record is update to the borrow table, book return successfully.
			if (res == 1) {
				// Show confirm message.
				AlertMessage(AlertType.INFORMATION, "Thank You for returning the book!", 
						"Returned accepted on:  " + dateFormat.format(currDate) + ".");
			}
			else {
				// Show error message.
				AlertMessage(AlertType.ERROR, "There is an Error!", "Please ask an librarian for help.");
			}
			// Update table to remove returned book from the list.
			update ();
		} 
		catch (SQLException e) {
			handleError(e);
		} 
		// catch exception when return book is not selected from the table.
		catch (NullPointerException e) {
			AlertMessage(AlertType.ERROR, "Please select a book.", e.getMessage());
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
	
	} // end of clickSelect()
	
	private void handleError(Exception e) {
		// Alert the user when things go terribly wrong
		Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
		// Close the program when the user clicks close on the alert
		alert.setOnCloseRequest(event -> Platform.exit());
		// show the alert
		alert.show();
	} // end of handleError(Exception e) 
	
	// Helper method to display Pop-up message.
	private void AlertMessage(AlertType type, String headerMessage, String message) {
		Alert alert = new Alert(type, message, ButtonType.CLOSE);
		alert.setHeaderText(headerMessage);
		// show the alert
		alert.show();
	}
} // end of class ReturnBookController

