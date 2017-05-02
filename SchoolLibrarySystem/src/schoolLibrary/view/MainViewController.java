/**
* COMP 580 Final Project (Spring 2017)
* Controller class for the top section, handles the home button click event.
*
* @author  Chi Ho Lee, Bijay Maharjan
* @since   2017-4-26
*/
package schoolLibrary.view;

import java.io.IOException;

import javafx.fxml.FXML;
import schoolLibrary.Main;

public class MainViewController {

	private Main main;
	
	// Handles "Home" button click on the top BorderPane.
	@FXML
	private void goHome() throws IOException {
		main.showMainItems();
	}
	
}
