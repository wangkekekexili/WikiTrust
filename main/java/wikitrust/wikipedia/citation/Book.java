package wikitrust.wikipedia.citation;

/**
 * Book as citation
 * 
 * @author Ke Wang
 *
 */
public class Book implements Citation {

	private String title;
	private String isbn;
	
	public Book(String title, String isbn) {
		this.title = title;
		this.isbn = isbn;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getIsbn() {
		return this.isbn;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("book");
		sb.append("\t");
		sb.append(this.title);
		sb.append("\t");
		sb.append(this.isbn);
		return sb.toString();
	}
	
}
