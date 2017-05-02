/**
* COMP 580 Final Project (Spring 2017)
* Controller Class for 3 buttons on the main menu.
*
* @author  Chi Ho Lee, Bijay Maharjan
* @since   2017-4-26
*/
package schoolLibrary.view;

import java.io.IOException;

import javafx.fxml.FXML;
import schoolLibrary.Main;

public class MainItemsController {
	
	private Main main;
	
	// Handles "Search Book" button click on the main menu.
	@FXML
	private void goSearchBook() throws IOException {
		main.showSearchBookScene();
	}
	
	// Handles "Borrow Book" button click on the main menu.
	@FXML
	private void goBorrowBook() throws IOException {
		main.showBorrowBookScene();
	}
	
	// Handles "Return Book" button click on the main menu.
	@FXML
	private void goReturnBook() throws IOException {
		main.showReturnBookScene();
	}
	
}
