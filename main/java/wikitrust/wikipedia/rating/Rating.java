package wikitrust.wikipedia.rating;

/**
 * Rating represents a rating found at https://ckannet-storage.commondatastorage.googleapis.com/2012-10-22T184507/aft4.tsv.gz
 * 
 * @author Ke Wang
 *
 */
public class Rating {
	
	private String timestamp;
	private int articleId;
	private String articleTitle;
	private int articleNamespace;
	private long revisionId;
	private int userId;

	// four kinds of ratings, 1-5
	// 0 represents not rated
	private int trustworthy;
	private int objective;
	private int complete;
	private int wellwritten;
	
	public Rating(String timestamp, int articleId, String articleTitle,
			int articleNamespace, long revisionId, int userId, int trustworthy,
			int objective, int complete, int wellwritten) {
		this.timestamp = timestamp;
		this.articleId = articleId;
		this.articleTitle = articleTitle;
		this.articleNamespace = articleNamespace;
		this.revisionId = revisionId;
		this.userId = userId;
		this.trustworthy = trustworthy;
		this.objective = objective;
		this.complete = complete;
		this.wellwritten = wellwritten;
	}
	
	public String getTimestamp() {
		return timestamp;
	}

	public int getArticleId() {
		return articleId;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public int getArticleNamespace() {
		return articleNamespace;
	}

	public long getRevisionId() {
		return revisionId;
	}

	public int getUserId() {
		return userId;
	}

	public int getTrustworthy() {
		return trustworthy;
	}

	public int getObjective() {
		return objective;
	}

	public int getComplete() {
		return complete;
	}

	public int getWellwritten() {
		return wellwritten;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(150);
		sb.append(this.timestamp);
		sb.append("\t");
		sb.append(this.articleId);
		sb.append("\t");
		sb.append(this.articleTitle);
		sb.append("\t");
		sb.append(this.articleNamespace);
		sb.append("\t");
		sb.append(this.revisionId);
		sb.append("\t");
		sb.append(this.userId);
		sb.append("\t");
		sb.append(this.trustworthy);
		sb.append("\t");
		sb.append(this.objective);
		sb.append("\t");
		sb.append(this.complete);
		sb.append("\t");
		sb.append(this.wellwritten);
		return sb.toString();
	}

}
