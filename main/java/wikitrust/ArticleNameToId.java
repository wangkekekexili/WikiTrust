package wikitrust;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import wikitrust.utilities.Magic;

/**
 * Interactive mode to get Wikipedia ID from Wikipedia Article Name
 * 
 * @author Ke Wang
 *
 */
public class ArticleNameToId {

	Map<String, Integer> map;
	
	public ArticleNameToId() {
		File idToArticleNameFile = new File(Magic.DATA_PATH+Magic.ID_ARTICLE_NAME_FILENAME);
		map = new HashMap<String, Integer>();
		try (Scanner sc = new Scanner(idToArticleNameFile)) {
			while (sc.hasNextInt()) {
				int id = sc.nextInt();
				String articleName = sc.next();
				map.put(articleName, id);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public int getId(String name) {
		Integer i = map.get(name);
		return i==null?-1:i.intValue();
	}
	
}
