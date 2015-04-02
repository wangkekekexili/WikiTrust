package wikitrust.wikipedia.ranking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PageRankCompator{

	boolean isPageRankLoad;
	Map<Integer, Double> pageRank;
	
	{
		isPageRankLoad = false;
		pageRank = new HashMap<Integer, Double>();
	}
	
	private void loadPageRank() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File("computer_science_article_names_id_order_by_pagerank")));
		String line = null;
		while ((line=br.readLine()) != null) {
			String[] item = line.split(Pattern.quote("\t"));
			pageRank.put(Integer.parseInt(item[0]), Double.parseDouble(item[2]));
		}
		br.close();
		isPageRankLoad = true;
	}

	public int comparePageRank(Integer id1, Integer id2) throws Exception {
		if (isPageRankLoad == false) {
			throw new Exception("PageRank file not loaded");
		}
		
		Double value1 = pageRank.get(id1);
		Double value2 = pageRank.get(id2);
		
		if (value1==null || value2==null) {
			throw new Exception("id not found");
		}
		
		if (value1 > value2) {
			return 1;
		} else if (value1 < value2) {
			return -1;
		} else {
			return 0;
		}
	}

}
