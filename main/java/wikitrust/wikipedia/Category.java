package wikitrust.wikipedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a category on Wikipedia
 * 
 * @author Ke Wang
 *
 */
public class Category {

	private String categoryName;
	private String url;
	private List<String> articleNames;
	private List<Category> subcategories;
	
	{
		categoryName = null;
		url = null;
		articleNames = new ArrayList<String>();
		subcategories = new ArrayList<Category>();
	}
	
	public Category(String categoryName, String url) {
		this.categoryName = categoryName;
		this.url = url;
	}
	
	public String getCategoryName() {
		return this.categoryName;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public void addArticleName(String name) {
		this.articleNames.add(name);
	}
	
	public void addSubcategories(Category subcategory) {
		this.subcategories.add(subcategory);
	}
	
	public List<String> getArticleNames() {
		return this.articleNames;
	}
	
	public List<Category> getSubcategories() {
		return this.subcategories;
	}
	
}
