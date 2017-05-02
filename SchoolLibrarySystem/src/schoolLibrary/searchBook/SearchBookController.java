/**
* COMP 580 Final Project (Spring 2017)
* Controller class to search book using SQLite select query.
* Book can be searched by ISBN, title, author or category. 
*
* @author  Bijay Maharjan, Chi Ho Lee
* @since   2017-4-26
*/
package schoolLibrary.searchBook;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import schoolLibrary.bookDetails.BookDetailsController;
import javafx.scene.control.Button;

public class SearchBookController {
	
	@FXML
	private TextField searchTxtfield;
	
	@FXML
	private ComboBox<String> categoryComBox;
	
	@FXML
	private TableView<Row> table;
	
	@FXML
	private Button selectBtn;

	// TableColumn maps a row type to a cell type
	// In this case, we map Row to a String corresponding to the 
	@FXML
	private TableColumn<Row,String> isbn;
	@FXML
	private TableColumn<Row,String> title;
	@FXML
	private TableColumn<Row,String> author;

	// The list of rows to put into the table
	private ObservableList<Row> data;
	
	private Connection connection;
	private Main main;
	
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
    	
        // Initialize the table with the 3 columns.
    	// Tells JavaFX how to map Row data to columns
    	// Registering callback function - how to get each cell data
    	// similar to registering event listener
    	isbn.setCellValueFactory(cellData -> cellData.getValue().getIsbn());
    	title.setCellValueFactory(cellData -> cellData.getValue().getTitle());
    	author.setCellValueFactory(cellData -> cellData.getValue().getAuthor());
        
        // Whenever the user types in something, update
        // Registering event listener
        // obs is query.textProperty()
        // oldText and newText are old and new values of query.getText()
        searchTxtfield.textProperty().addListener(      (obs, oldText, newText) -> {update();}    );
        
        // Add event handler when comboBox changed.
        categoryComBox.setOnAction((e) -> {update();} );
        
        // connect to the database
        try {
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
		
	        // Code below is to Read Category Name and put into ComboBox. 
	        // Default case search by all category.
	        categoryComBox.getItems().addAll(
	                "All Categories");
	        // SQL to get every category name.
	        String listCatSql = "SELECT CategoryName As catName FROM Category ORDER BY CategoryName;";
	        PreparedStatement stmt = connection.prepareStatement( listCatSql );
	        // get results
			ResultSet res = stmt.executeQuery();
			// Add result into the ComboBox.
			while ( res.next() ) {
				categoryComBox.getItems().addAll(
						res.getString("catName"));
			}
			// "All Categories" will be selected as default.
			categoryComBox.setValue("All Categories");
		
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
		
        // update initially shows everything, like in iTunes
        update();
    }	// end of initialize()
    
    /**
     * this method is called when the user changes the TextField's text
     */
	@FXML
	private void update() {
		try {
			
			// connect to the database
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
			
			// The parameter comes from the query TextField
			// at first, it is empty
			String param = searchTxtfield.getText();
			
			// data (a list of Rows) is used to fill in the table
			// at first, raw ArryList() wrapped with FX wrapper (ObservableList<Row>)
			// ObservableList is a super class of observableArrayList
			data = FXCollections.observableArrayList();
			
			// generate parameterized sql
			String sql;
			
			// instead of n/a, just use the empty string
			if (param.trim().equals("") && categoryComBox.getValue().equals("All Categories")) {
				// sql to list all books.
				sql = "SELECT Book.ISBN AS book_isbn, Book.Title AS book_title, Book.Author AS book_author" +
						" FROM Book" +
						" ORDER BY book_isbn";
			}
			else if (param.trim().equals("")) {
				// sql to allow searching by ? for category Name
				sql = "SELECT Book.ISBN AS book_isbn, Book.Title AS book_title, Book.Author AS book_author" +
						" FROM Book" +
						" JOIN Category USING (CategoryId)" +
						" WHere Category.CategoryName Like ?" +
						" ORDER BY book_isbn";
			}
			else if (categoryComBox.getValue().equals("All Categories"))
			{
				// sql to searching book by ? for from all categories.
				sql = "SELECT Book.ISBN AS book_isbn, Book.Title AS book_title, Book.Author AS book_author" +
						" FROM Book" +
						" WHERE Book.ISBN LIKE ? or Book.Title LIKE ? or Book.Author LIKE ?" +
						" ORDER BY book_isbn";
			}
			else
			{
				// sql to searching book by ? and from category by ?.
				sql = "SELECT Book.ISBN AS book_isbn, Book.Title AS book_title, Book.Author AS book_author" +
						" FROM Book" +
						" JOIN Category USING (CategoryId)" +
						" WHERE (Book.ISBN LIKE ? or Book.Title LIKE ? or Book.Author LIKE ?) and Category.CategoryName Like ?" + 
						" ORDER BY book_isbn";
			}
			
			// prepared statement
			PreparedStatement stmt = connection.prepareStatement( sql );
			
			// bind parameter(s)
			// In SQLite "?" is a place holder (act like variable)
			// 1 is the first place holder, 2 is the second place holder, etc
			if ( !param.trim().equals("")) {
				// the % before and after the parameter searches anywhere in the database column
				// For sql to searching book by ? for from all categories.
				stmt.setString( 1, "%" + param.trim() + "%" );
				stmt.setString( 2, "%" + param.trim() + "%" );
				stmt.setString( 3, "%" + param.trim() + "%" );
				
				if (!categoryComBox.getValue().equals("All Categories")) {
					// For sql to searching book by ? and from category by ?.
					stmt.setString( 4, "%" + categoryComBox.getValue() + "%" );
				}
			}
			else if (!categoryComBox.getValue().equals("All Categories")) {
				// For sql to allow searching by ? for category Name
				stmt.setString( 1, "%" + categoryComBox.getValue() + "%" );
			}
				
			// get results
			ResultSet res = stmt.executeQuery();
			while ( res.next() ) {
				Row row = new Row(res.getString("book_isbn"), res.getString("book_title"), res.getString("book_author"));
				// add a row to the list
				data.add(row);
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
	}	// end of update()
	
	private void handleError(Exception e) {
		// Alert the user when things go terribly wrong
		Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
		// Close the program when the user clicks close on the alert
		alert.setOnCloseRequest(event -> Platform.exit());
		// show the alert
		alert.show();
	}
	
	// Method to handle button click to view book details information.
	@FXML
	private void clickSelect() throws IOException {
		try {
			// Get current selected row.
			Row selectedRow = table.getSelectionModel().getSelectedItem();	
			// Get ISBN from selected row and passing it to Book Details Controller.
			BookDetailsController.setIsbn(selectedRow.getIsbn().getValue());
			// invoke method in main to show scene.
			main.showBookDetailsScene();
		} catch (NullPointerException e) {
			// Alert the user when no row is selected in table.
			Alert alert = new Alert(AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
			alert.setHeaderText("Please select a book.");
			// show the alert
			alert.show();
		}
	}
	
}
