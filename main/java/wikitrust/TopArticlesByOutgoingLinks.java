package wikitrust;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import wikitrust.utilities.Magic;
import wikitrust.utilities.SearchTree;

public class TopArticlesByOutgoingLinks {

	public static int TOP_N  = 100;
	
	public static void main(String[] args) {
		
		// build search tree from id to article name
		SearchTree<String> idToName = new SearchTree<String>();
		File idArticleFilePath = new File(Magic.DATA_PATH+Magic.ID_ARTICLE_NAME_FILENAME);
		try (BufferedReader br = new BufferedReader(new FileReader(idArticleFilePath))) {
			String line = null;
			while ((line=br.readLine()) != null) {
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
				
				idToName.insert(id, name);
				
				sc.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// scan top n from outgoinglinks file
		File in = new File("share/data/pagelinks_outgoing");
		File output = new File("share/data/rank_by_number_of_outgoing_edges");
		int count = 0;
		try (
				BufferedReader br = new BufferedReader(new FileReader(in));
				BufferedWriter bw = new BufferedWriter(new FileWriter(output));
				) {
			String line = null;
			while (count < TOP_N && (line=br.readLine()) != null) {
				count++;
				String[] splits = line.split(" ");
				int id = Integer.parseInt(splits[0]);
				int n = Integer.parseInt(splits[1]);
				bw.write(idToName.search(id)+" "+n+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
