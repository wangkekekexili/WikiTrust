package wikitrust.dataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Extract page links from share/raw_data/enwiki-latest-pagelinks.sql
 * 
 * @author Ke Wang
 *
 */
public class ExtractPageLinks {

	private static boolean noEvenSlashes(String s, int index) {
		int count = 0;
		int i = index-1;
		while (s.charAt(i)=='\\') {
			count++;
			i--;
		}
		if (count % 2 == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public static void main(String[] args) {
		try (
				BufferedReader br = new BufferedReader(new FileReader(new File("share/raw_data/enwiki-latest-pagelinks.sql")));
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("share/data/pagelinks_id_to_article_name")));
				) {
			String line = null;
			while ((line=br.readLine())!=null) {
				if (line.startsWith("INSERT INTO `pagelinks` VALUES")) {
					int index = 0;
					while (index < line.length()) {
						// extract a tuple
						// (fromId, toNamespace, toTitle, fromNamespace)
						
						int fromId;
						int toNameSpace;
						String toTitle;
						int fromNamespace;
						
						while (index < line.length() && line.charAt(index) != '(') {
							index++;
						}
						if (index >= line.length()) { break; }
						index++; // find a '('
						int endIndex = index+1;
						while (Character.isDigit(line.charAt(endIndex))) {
							endIndex++;
						}
						fromId = Integer.parseInt(line.substring(index, endIndex));
						index = endIndex+1; 
						endIndex = index+1;
						while (Character.isDigit(line.charAt(endIndex))) {
							endIndex++;
						}
						toNameSpace = Integer.parseInt(line.substring(index, endIndex));
						index = endIndex+2;
						endIndex = index;
						while (line.charAt(endIndex) != '\'' || (line.charAt(endIndex) == '\'' && noEvenSlashes(line, endIndex)==true)) {
							endIndex++;
						}
						//System.out.println(line.substring(index, endIndex));
						toTitle = line.substring(index, endIndex);
						index = endIndex + 2;
						endIndex = index + 1;
						while (Character.isDigit(line.charAt(endIndex))) {
							endIndex++;
						}
						//System.out.println(line.substring(index, endIndex));
						fromNamespace = Integer.parseInt(line.substring(index, endIndex));
						index = endIndex+1;
						
						// write to the file
						if (fromId!=0 && toNameSpace==0 && fromNamespace==0) {
							bw.write(""+fromId+"\t"+toTitle.replaceAll("\\\\", "")+"\n");
						}
						
					}
				} else {
					//continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			//System.err.println(e);
			System.exit(1);
		}
	}

}
