package wikitrust;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import wikitrust.utilities.Magic;
import wikitrust.utilities.Pair;
import wikitrust.utilities.SearchTree;

public class TopArticlesByNumberOfReferences {

	public static int TOP_N = 100;
	
	public static void main(String[] args) {
		
		// build id to article_name map
		SearchTree<String> searchTree = new SearchTree<String>();
		File idArticleFilePath = new File(Magic.DATA_PATH+Magic.ID_ARTICLE_NAME_FILENAME);
		try (BufferedReader br = new BufferedReader(new FileReader(idArticleFilePath))) {
			String line = null;
			while ((line=br.readLine())!=null) {
				Scanner sc = new Scanner(line);
				
				int id;
				String name;
				
				if (sc.hasNextInt()) {
					id = sc.nextInt();
				} else {
					continue;
				}
				
				if (sc.hasNext()) {
					name = sc.next();
				} else {
					continue;
				}
				
				searchTree.insert(id, name);
				
				sc.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// build id to number_of_references map
		Map<Integer, Integer> idToNumberOfScholarlyReferences = new HashMap<Integer, Integer>();
		File f = new File("share/raw_data/doi-and-pubmed-citations-enwiki-20150112.tsv");
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line = null;
			while ((line=br.readLine()) != null) {
				int id = -1;
				try {
					id = Integer.parseInt(line.split("\t")[0]);
				} catch (NumberFormatException e) {
					continue;
				}
				//System.out.println(id);
				Integer mapGet = idToNumberOfScholarlyReferences.get(id);
				if (mapGet == null) {
					idToNumberOfScholarlyReferences.put(id, 1);
				} else {
					idToNumberOfScholarlyReferences.replace(id, mapGet+1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// map to array
		// sort array
		List<Pair<Integer, Integer>> list = new ArrayList<>();
		for (Entry<Integer, Integer> entry : idToNumberOfScholarlyReferences.entrySet()) {
			list.add(new Pair<Integer, Integer>(entry.getKey(), entry.getValue()));
		}
		Collections.sort(list, (e1, e2)->{
			if (e1.getSecond() > e2.getSecond()) {
				return -1;
			} else if (e1.getSecond() < e2.getSecond()) {
				return 1;
			} else {
				return 0;
			}
			
		});
		
		// output 
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("share/data/rank_by_number_of_references")))) {
			for (int i = 0;i != list.size();i++) {
				bw.write(searchTree.search(list.get(i).getFirst())+" "+list.get(i).getSecond()+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
