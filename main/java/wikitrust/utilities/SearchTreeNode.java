package wikitrust.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Node for SearchTree
 * 
 * @author Ke Wang
 *
 * @param <Content>
 */
class SearchTreeNode<Content> {
	private Content content;
	private Map<Character, SearchTreeNode<Content>> map;
	{
		content = null;
		map = new HashMap<Character, SearchTreeNode<Content>>();
	}
	
	SearchTreeNode() {
	}
	
	SearchTreeNode(Content c) {
		this.content = c;
	}
	
	Content insert(String s, Content c) {
		if (s.length() == 0) {
			if (this.content == null) {
				this.content = c;
				return null;
			} else {
				Content oldContent = this.content;
				this.content = c;
				return oldContent;
			}
		} else {
			char indexThisLevel = s.charAt(0);
			if (map.containsKey(indexThisLevel)) {
				return map.get(indexThisLevel).insert(s.substring(1), c);
			} else {
				SearchTreeNode<Content> childNode = new SearchTreeNode<Content>();
				map.put(indexThisLevel, childNode);
				childNode.insert(s.substring(1), c);
				return null;
			}
		}
	}
	
	Content search(String s) {
		if (s.equals("")) {
			return this.content;
		}
		char indexThisLevel = s.charAt(0);
		SearchTreeNode<Content> childNode = map.get(indexThisLevel);
		if (childNode == null) {
			return null;
		} else {
			return childNode.search(s.substring(1));
		}
	}
}
