/**
* COMP 580 Final Project (Spring 2017)
* Class to hold the data of each row in ReturnBook
*
* @author  Bijay Maharjan, Chi Ho Lee 
* @since   2017-4-26
*/
package schoolLibrary.returnBook;

import javafx.beans.property.SimpleStringProperty;

public class Row {

	private SimpleStringProperty isbn;
	private SimpleStringProperty title;
	private SimpleStringProperty author;
	private SimpleStringProperty dueDate;
	
	public Row(String isbn, String title, String author, String dueDate) {
		this.isbn = new SimpleStringProperty(isbn);
		this.title = new SimpleStringProperty(title);
		this.author = new SimpleStringProperty(author);
		this.dueDate = new SimpleStringProperty(dueDate);
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
	public SimpleStringProperty getDueDate() {
		return dueDate;
	}
	
}
