package wikitrust.utilities;

/**
 * A tree for search
 * 
 * @author Ke Wang
 *
 * @param <Content>
 */
public class SearchTree<Content> {
	private SearchTreeNode<Content> root;
	{
		root = new SearchTreeNode<Content>(null);
	}
	
	/**
	 * Insert an key-value entry into the search tree
	 * 
	 * @param key the key of the entry
	 * @param content the value of the entry
	 * @return the old content if exists, else null
	 */
	public Content insert(String key, Content content) {
		return this.root.insert(key, content);
	}
	
	public Content insert(int key, Content content) {
		return this.root.insert(""+key, content);
	}
	
	/**
	 * Search for the key in the search tree
	 * 
	 * @param key the key to search
	 * @return the value if found, null if not found
	 */
	public Content search(String key) {
		return this.root.search(key);
		
	}
	
	public Content search(int key) {
		return this.root.search(""+key);
	}
	
}
