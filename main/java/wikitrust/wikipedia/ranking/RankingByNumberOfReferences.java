package wikitrust.wikipedia.ranking;

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
import java.util.regex.Pattern;

import wikitrust.utilities.Pair;

public class RankingByNumberOfReferences {

	public static void main(String[] args) {
		
		Map<Integer, String> idToName = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File("share/data/computer_science_article_names_id")))) {
			String line = null;
			while ((line=br.readLine()) != null) {
				String[] item = line.split(Pattern.quote("\t"));
				idToName.put(Integer.parseInt(item[0]), item[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}		
		
		Map<Integer, Integer> idToCitations = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File("share/data/computer_science_citations")))) {
			String idLine = null;
			while ((idLine=br.readLine()) != null) {
				if (idLine.contains("\t") == false) {
					String line = null;
					int count = 0;
					while ((line=br.readLine())!=null && line.trim().equals("")==false) {
						count++;
					}
					if (count==0) { continue; }
					idToCitations.put(Integer.parseInt(idLine), count);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Pair<Integer, Integer>> list = new ArrayList<>();
		for (Entry<Integer, Integer> entry : idToCitations.entrySet()) {
			list.add(new Pair<Integer, Integer>(entry.getKey(), entry.getValue()));
		}
		Collections.sort(list,(e1,e2)->{
			if (e1.getSecond() > e2.getSecond()) {
				return -1;
			} else if (e1.getSecond() < e2.getSecond()) {
				return 1;
			} else {
				return 0;
			}
		});
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("share/data/computer_science_rank_by_number_of_references")))) {
			for (Pair<Integer, Integer> pair : list) {
				bw.write(""+pair.getFirst());
				bw.write("\t");
				bw.write(idToName.get(pair.getFirst()));
				bw.write("\t");
				bw.write(""+pair.getSecond());
				bw.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
