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
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import schoolLibrary.Main;
import schoolLibrary.borrowBook.BorrowBookController;
import javafx.scene.control.Button;


public class ReturnBookController {
	
	private Connection connection;
	
	@FXML
	private TextField query;

	
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
	
	

	// The list of rows to put into the table
	private ObservableList<Row> data;
	
	private Button selectBtn;
	
	private Main main;
	@FXML
	private ComboBox<String> ratingCombox;
	
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
    	
        // Initialize the table with the two columns.
    	// Tells JavaFX how to map Row data to columns
    	// Registering callback function - how to get each cell data (artist name and album name)
    	// similar to registering event listener
    	isbn.setCellValueFactory(cellData -> cellData.getValue().getIsbn());
    	title.setCellValueFactory(cellData -> cellData.getValue().getTitle());
    	author.setCellValueFactory(cellData -> cellData.getValue().getAuthor());
        
        // Whenever the user types in something, update
        // Registering event listener
        // obs is query.textProperty()
        // oldText and newText are old and new values of query.getText()
        query.textProperty().addListener(      (obs, oldText, newText) -> {update();}    );
        
       
        // connect to the database
        try {
			connection = DriverManager.getConnection( "jdbc:sqlite:SchoolLibrarySystem.sqlite");
		} catch (SQLException e) {
			handleError(e);
		}
        
       
		
        // update initially shows everything, like in iTunes
        update();
    }
    
    /**
     * this method is called when the user changes the TextField's text
     */
	@FXML
	private void update() {
		try {
			// The parameter comes from the query TextField
			// at first, it is empty
			String param = query.getText();
			
			// data (a list of Rows) is used to fill in the table
			// at first, raw ArryList() wrapped with FX wrapper (ObservableList<Row>)
			// ObservableList is a super class of observableArrayList
			data = FXCollections.observableArrayList();
			
			// generate parameterized sql
			String sql;
		
				sql = "SELECT Book.ISBN AS book_isbn, Book.Title AS book_title, Book.Author AS book_author" +
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
				Row row = new Row(res.getString("book_isbn"), res.getString("book_title"), res.getString("book_author"));
				// add a row to the list
				data.add(row);
			}
			// the table will now have a list of all the rows we got from the db
			table.setItems(data);
			
			
		} catch (SQLException e) {
			handleError(e);
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
	
	
	
	@FXML
	
	private void clickSelect() throws IOException {
		Row selectedRow = table.getSelectionModel().getSelectedItem();
		
		
		//return function imp
		try {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currDate = new Date();     		
        
		// generate parameterized sql
		String sql = "UPDATE Borrow SET ReturnedDate = ?, Rating = ?"+
					" WHERE ISBN = ? AND MemberId = ? AND ReturnedDate IS NULL";
			
		

		// prepared statement
		PreparedStatement stmt = connection.prepareStatement( sql );
		
		stmt.setString( 1, dateFormat.format(currDate));
		stmt.setString( 2, ratingCombox.getValue());
		stmt.setString( 3, selectedRow.getIsbn().getValue());
		stmt.setString( 4, query.getText().trim());
		
		// get results
		int res = stmt.executeUpdate();
		
		String returnMsgHeader;
		String returnMessage;	
		if (res == 1) {
			returnMsgHeader = "Thank You for returning the book!";
			returnMessage = "Returned accepted on:  " + dateFormat.format(currDate) + ".";
		}
		else {
			returnMsgHeader = "There is an Error!";
			returnMessage = "Please ask an librarian for help.";
		}
			
		
		// Show confirm message.
		Alert alert = new Alert(AlertType.INFORMATION, returnMessage, ButtonType.CLOSE);
		alert.setHeaderText(returnMsgHeader);
		// show the alert
		alert.show();
		update ();
	
	} 
		catch (SQLException e) {
		handleError(e);
		
		
	}
	
	}}

