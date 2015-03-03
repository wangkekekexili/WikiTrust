package wikitrust.dataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import wikitrust.ArticleNameToId;
import wikitrust.utilities.Pair;

public class ExtractPageLinksFromIdToId {

	public static void main(String[] args) {

		ArticleNameToId articleNameToId = new ArticleNameToId();
		System.out.println("Index Done");
		
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File("share/data/pagelinks_id_to_article_name")));
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("share/data/pagelinks_id_to_id_one_link_a_line")));
				) {
			String line = null;
			
			Pair<String, Integer> cache = new Pair<String, Integer>();
			
			while ((line=br.readLine())!=null) {
				Scanner lineScanner = new Scanner(line);
				int fromId;
				if (lineScanner.hasNextInt()) {
					fromId = lineScanner.nextInt();
				} else {
					continue;
				}
				String name = null;
				if (lineScanner.hasNext()) {
					name = lineScanner.next();
				} else {
					continue;
				}
				int toId = -1;
				if (name.equals(cache.getFirst())) {
					toId = cache.getSecond();
				} else {
					toId = articleNameToId.getId(name);
					cache.setFirst(name);
					cache.setSecond(toId);
				}
				if (toId != -1) {
					bw.write(""+fromId+" "+toId+"\n");
				}
				lineScanner.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}

}
