package wikitrust.wikipedia.rating;

import java.util.ArrayList;
import java.util.List;

import wikitrust.wikipedia.Article;

public class ArticleWithRatings extends Article {
	
	List<Rating> ratings;
	
	public ArticleWithRatings(int id, String title) {
		this.articleId = id;
		this.articleTitle = title;
		this.ratings = new ArrayList<Rating>();
	}
	
	public void addRating(Rating rating) {
		this.ratings.add(rating);
	}
	
	public int getNumberOfRatings() {
		return this.ratings.size();
	}
	
	public List<Rating> getRatings() {
		return this.ratings;
	}
	
}
