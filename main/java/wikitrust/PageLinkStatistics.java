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

import wikitrust.utilities.Pair;

public class PageLinkStatistics {
	
	public static void main(String[] args) {
		File f = new File("share/data/pagelinks_id_to_id_one_link_a_line");
		long numberOflinks = 0;
		Map<Integer, Integer> outgoingLinks = new HashMap<Integer, Integer>();
		Map<Integer, Integer> incomingLinks = new HashMap<Integer, Integer>();
		File out = new File("share/data/pagelinks_outgoing");
		File in = new File("share/data/pagelinks_in");
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			String line = null;
			while ((line=br.readLine())!=null) {
				
				numberOflinks++;
				
				String[] splits = line.split(" ");
				int fromId = Integer.parseInt(splits[0]);
				int toId = Integer.parseInt(splits[1]);
				
				Integer currentNumberOfOutLinks = outgoingLinks.get(fromId);
				if (currentNumberOfOutLinks == null) {
					outgoingLinks.put(fromId, 1);
				} else {
					outgoingLinks.replace(fromId, currentNumberOfOutLinks+1);
				}
				
				Integer currentNumberOfInLinks = incomingLinks.get(toId);
				if (currentNumberOfInLinks == null) {
					incomingLinks.put(toId, 1);
				} else {
					incomingLinks.replace(toId, currentNumberOfInLinks+1);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// put maps into arrays
		// sort them
		List<Pair<Integer, Integer>> outgoingLinksList = new ArrayList<Pair<Integer, Integer>>();
		List<Pair<Integer, Integer>> incomingLinksList = new ArrayList<Pair<Integer, Integer>>();
		for (Entry<Integer, Integer> entry : outgoingLinks.entrySet()) {
			outgoingLinksList.add(new Pair<Integer, Integer>(entry.getKey(), entry.getValue()));
		}
		for (Entry<Integer, Integer> entry : incomingLinks.entrySet()) {
			incomingLinksList.add(new Pair<Integer, Integer>(entry.getKey(), entry.getValue()));
		}
		Collections.sort(outgoingLinksList, (e1,e2)->{
			if (e1.getSecond() > e2.getSecond()) {
				return -1;
			} else if (e1.getSecond() < e2.getSecond()) {
				return 1;
			} else {
				return 0;
			}
		});
		Collections.sort(incomingLinksList, (e1,e2)->{
			if (e1.getSecond() > e2.getSecond()) {
				return -1;
			} else if (e1.getSecond() < e2.getSecond()) {
				return 1;
			} else {
				return 0;
			}
		});
		
		// output
		System.out.println("Number of total links: " + numberOflinks);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(out))) {
			for (Pair<Integer, Integer> p : outgoingLinksList) {
				bw.write(""+p.getFirst()+" "+p.getSecond()+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(in))) {
			for (Pair<Integer, Integer> p : incomingLinksList) {
				bw.write(""+p.getFirst()+" "+p.getSecond()+"\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
}
