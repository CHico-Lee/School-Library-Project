/**
* COMP 580 Final Project (Spring 2017)
* Class to hold the data of each row in Search Book.
*
* @author  Chi Ho Lee, Bijay Maharjan
* @since   2017-4-26
*/
package schoolLibrary.searchBook;

import javafx.beans.property.SimpleStringProperty;

public class Row {

	private SimpleStringProperty isbn;
	private SimpleStringProperty title;
	private SimpleStringProperty author;
	
	public Row(String isbn, String title, String author) {
		this.isbn = new SimpleStringProperty(isbn);
		this.title = new SimpleStringProperty(title);
		this.author = new SimpleStringProperty(author);
	}
	
	public SimpleStringProperty getIsbn() {
		return isbn;
	}
	public SimpleStringProperty getTitle() {
		return title;
	}
	public SimpleStringProperty getAuthor() {
		return author;
	}
	
}
