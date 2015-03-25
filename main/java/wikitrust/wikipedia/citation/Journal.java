package wikitrust.wikipedia.citation;

/**
 * Journal as a citation
 * 
 * @author Ke Wang
 *
 */
public class Journal implements Citation {

	private String title;
	private String doi;
	
	public Journal(String title, String doi) {
		this.title = title;
		this.doi = doi;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public String getDoi() {
		return this.doi;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("journal");
		sb.append("\t");
		sb.append(this.title);
		sb.append("\t");
		sb.append(this.doi);
		return sb.toString();
	}
 	
}
