package schoolLibrary.view;

import java.io.IOException;

import javafx.fxml.FXML;
import schoolLibrary.Main;

public class MainItemsController {
	
	private Main main;
	@FXML
	private void goSearchBook() throws IOException {
		main.showSearchBookScene();
	}
	
	@FXML
	private void goBorrowBook() throws IOException {
		main.showBorrowBookScene();
	}
	
	@FXML
	private void goReturnedBook() throws IOException {
		main.showReturnedBookScene();
	}
	
}
