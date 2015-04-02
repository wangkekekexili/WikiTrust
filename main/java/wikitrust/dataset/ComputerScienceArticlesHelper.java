package wikitrust.dataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import wikitrust.algorithm.pagerank.PageRank;
import wikitrust.wikipedia.Category;

class Pair {
	Category category;
	int level;
	Pair(Category category, int level) {
		this.category = category;
		this.level = level;
	}
}

class ValuePair<T1, T2> {
	T1 t1;
	T2 t2;
	ValuePair(T1 t1, T2 t2) {
		this.t1 = t1;
		this.t2 = t2;
	}
}

public class ComputerScienceArticlesHelper {

	/**
	 * Extract articles related to computer science
	 * 
	 * @param dest the file path to save results
	 * @throws Exception
	 */
	public static void extractComputerScienceFromWeb(String dest) throws Exception {
		
		Category rootCategory = new Category("Computer_science", "https://en.wikipedia.org/wiki/Category:Computer_science");
		
		Set<String> categorySet = new HashSet<>();
		
		Queue<Pair> queue = new LinkedList<>();
		queue.add(new Pair(rootCategory,0));
		categorySet.add(rootCategory.getCategoryName());
		
		int maxLevel = 5;
		
		while (queue.isEmpty() == false) {
			Pair currentPair = queue.poll();
			int currentLevel = currentPair.level;
			Category currentCategory = currentPair.category;
			
			// get HTML page of the current category page
			URL currentCategoryUrl = new URL(currentCategory.getUrl());
			
			StringBuilder htmlBuilder = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(currentCategoryUrl.openStream()));
			int ch = -1;
			while ((ch=br.read()) != -1) {
				htmlBuilder.append((char)ch);
			}
			br.close();
			
			Document document = Jsoup.parse(htmlBuilder.toString());
			
			// handle subcategories
			if (currentLevel < maxLevel) {
				Element sub = document.getElementById("mw-subcategories");
				if (sub != null) {
					Elements subGroups = sub.getElementsByClass("mw-category-group");
					for (Element subGroup : subGroups) {
						Elements as = subGroup.getElementsByTag("a");
						for (Element a : as) {
							String newSubcategory = a.attr("href");
							String newUrl = "https://en.wikipedia.org"+newSubcategory;
							String newCategoryName = newSubcategory.substring(15);
							if (categorySet.contains(newCategoryName) == false) {
								System.out.println(newCategoryName);
								Category newCategory = new Category(newCategoryName, newUrl);
								currentCategory.addSubcategories(newCategory);
								queue.add(new Pair(newCategory, currentLevel+1));
								categorySet.add(newCategoryName);
							}
						}
					}
				}
			}
			
			// handle pages
			Element page = document.getElementById("mw-pages");
			if (page != null) {
				Elements pageGroups = page.getElementsByClass("mw-category-group");
				for (Element pageGroup : pageGroups) {
					Elements as = pageGroup.getElementsByTag("a");
					for (Element a : as) {
						String content = a.attr("href");
						//String newUrl = "https://en.wikipedia.org"+content;
						String newArticleName = content.substring(6);
						currentCategory.addArticleName(newArticleName);
					}
				}
			}
		
		}
		
		Set<String> set = new HashSet<String>();
		Queue<Category> q = new LinkedList<>();
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dest)))) {
			q.add(rootCategory);
			while (q.isEmpty() == false) {
				Category current = q.poll();
				for (String s : current.getArticleNames()) {
					if (set.contains(s)) { continue; } else {
						bw.write(s+"\n");
						set.add(s);
					}
				}
				for (Category c : current.getSubcategories()) {
					q.add(c);
				}
			}
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void getIdForComputerScienceArticles() {
		Map<String, Integer> map = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File("share/data/id_articlename")))) {
			String line = null;
			while ((line=br.readLine()) != null) {
				String[] item = line.split(Pattern.quote("\t"));
				map.put(item[1], Integer.parseInt(item[0]));
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File("share/data/computer_science_article_names")));
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("share/data/computer_science_article_names_id")));
				) {
			String line = null;
			while ((line=br.readLine()) != null) {
				line = line.replaceAll("%27", "'");
				Integer i = map.get(line);
				if (i != null) {
					bw.write(""+i.intValue());
					bw.write("\t");
					bw.write(line);
					bw.write("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void sortComputerScienceArticlesByName(){
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File("share/data/computer_science_article_names_id")));
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("share/data/computer_science_article_names_id_order_by_name")));
				) {
			String line = null;
			Map<String, Integer> map = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
			while ((line=br.readLine()) != null) {
				String[] item = line.split(Pattern.quote("\t"));
				map.put(item[1], Integer.parseInt(item[0]));
			}
			for (Entry<String, Integer> entry : map.entrySet()) {
				bw.write(""+entry.getValue());
				bw.write("\t");
				bw.write(entry.getKey());
				bw.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void sortComputerScienceArticlesById() {
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File("share/data/computer_science_article_names_id")));
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("share/data/computer_science_article_names_id_order_by_id")));
				) {
			String line = null;
			Map<Integer, String> map = new TreeMap<Integer, String>((e1,e2)->{
				if (e1.intValue() > e2.intValue()) {
					return 1;
				} else if (e1.intValue() < e2.intValue()) {
					return -1;
				} else {
					return 0;
				}
			});
			while ((line=br.readLine()) != null) {
				String[] item = line.split(Pattern.quote("\t"));
				map.put(Integer.parseInt(item[0]), item[1]);
			}
			for (Entry<Integer, String> entry : map.entrySet()) {
				bw.write(""+entry.getKey());
				bw.write("\t");
				bw.write(entry.getValue());
				bw.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void extractLinks() {
		Set<Integer> idSet = new HashSet<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File("share/data/computer_science_article_names_id")))) {
			String line = null;
			while ((line=br.readLine()) != null) {
				String[] item = line.split(Pattern.quote("\t"));
				idSet.add(Integer.parseInt(item[0]));
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// check to see if a link has its both ends related to computer science
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File("share/data/pagelinks_id_to_id_one_link_a_line")));
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("share/data/computer_science_pagelinks")));
				) {
			String line = null;
			while ((line=br.readLine()) != null) {
				String[] item = line.split(Pattern.quote(" "));
				int idFrom = Integer.parseInt(item[0]);
				int idTo = Integer.parseInt(item[1]);
				if (idSet.contains(idFrom) && idSet.contains(idTo)) {
					bw.write(""+idFrom+"\t"+idTo+"\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void runPageRank() {

		Map<Integer, String> idToName = new HashMap<>();
		
		// get node ids
		List<Integer> nodeIds = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File("share/data/computer_science_article_names_id")))) {
			String line = null;
			while ((line=br.readLine()) != null) {
				String[] item = line.split(Pattern.quote("\t"));
				nodeIds.add(new Integer(item[0]));
				idToName.put(Integer.parseInt(item[0]), item[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// get edges
		List<int[]> edges = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File("share/data/computer_science_pagelinks")))) {
			String line = null;
			while ((line=br.readLine()) != null) {
				String[] item = line.split(Pattern.quote("\t"));
				int[] link = new int[2];
				link[0] = Integer.parseInt(item[0]);
				link[1] = Integer.parseInt(item[1]);
				edges.add(link);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Map<Integer, Double> result = PageRank.runPageRank(nodeIds, edges);
		
		class Pair {
			double d;
			int i;
			Pair(double d, int i) {
				this.d = d;
				this.i = i;
			}
		}
		
		List<Pair> resultList = new ArrayList<>();
		result.forEach((i,d)->{
			resultList.add(new Pair(d.doubleValue(), i.intValue()));
		});
		Collections.sort(resultList, (e1,e2)->{
			if (e1.d > e2.d) {
				return -1;
			} else if (e1.d < e2.d) {
				return 1;
			} else {
				return 0;
			}
		});
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("share/data/computer_science_article_names_id_order_by_pagerank")))) {
			for (Pair p : resultList) {
				int id = p.i;
				double value = p.d;
				String name = idToName.get(id);
				bw.write(""+id+"\t"+name+"\t"+value+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	public static void main(String[] args) throws Exception {

		Map<Integer, String> idToName = new HashMap<>();
		
		// get node ids
		List<Integer> nodeIds = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File("share/data/computer_science_article_names_id")))) {
			String line = null;
			while ((line=br.readLine()) != null) {
				String[] item = line.split(Pattern.quote("\t"));
				nodeIds.add(new Integer(item[0]));
				idToName.put(Integer.parseInt(item[0]), item[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Map<Integer, Double> rating = new HashMap<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(new File("share/data/ratingOrderByAverageRating.tsv")))) {
			String line = null;
			while ((line=br.readLine()) != null) {
				String[] item = line.split(Pattern.quote("\t"));
				int currentId = Integer.parseInt(item[0]);
				if (nodeIds.contains(currentId)) {
					rating.put(currentId, Double.parseDouble(item[3]));
				}
			}
		}
		
		List<ValuePair<Integer, Double>> list = new ArrayList<>();
		for (Entry<Integer, Double> entry : rating.entrySet()) {
			list.add(new ValuePair<Integer, Double>(entry.getKey(), entry.getValue()));
		}
		Collections.sort(list, (e1,e2)->{
			if (e1.t2 > e2.t2) {
				return -1;
			} else if (e1.t2 < e2.t2) {
				return 1;
			} else {
				return 0;
			}
		});
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("share/data/computer_science_rating")))) {
			for (ValuePair<Integer, Double> pair : list) {
				bw.write(""+pair.t1+"\t");
				bw.write(idToName.get(pair.t1));
				bw.write("\t");
				bw.write(""+pair.t2);
				bw.write("\n");
			}
		}
		
	}

}
