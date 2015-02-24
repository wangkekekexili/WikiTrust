package wikitrust.dataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Extract Wikipedia ID and article names from share/raw_data/enwiki-latest-page.sql
 * 
 * @author Ke Wang
 *
 */
final class ExtractIDAndArticleName {

	public static void main(String[] args) {
		Path p = Paths.get("share/raw_data/enwiki-latest-page.sql");
		Path q = Paths.get("share/data/id_articlename");
		try (BufferedReader br = Files.newBufferedReader(p);
				BufferedWriter bw = Files.newBufferedWriter(q, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
			String line = null;
			while ((line=br.readLine())!=null) {
				if (line.startsWith("INSERT INTO `page` VALUES")) {
					int index = 0;
					while (index < line.length()) {
						while (index < line.length() && line.charAt(index) != '(') {
							index++;
						}
						if (index >= line.length()) {
							break;
						}
						index++;
						int endIndex = index+1;
						while (Character.isDigit(line.charAt(endIndex))) {
							endIndex++;
						}
						int id = Integer.parseInt(line.substring(index, endIndex));
						index = endIndex+1;
						endIndex = index+1;
						while (Character.isDigit(line.charAt(endIndex))) {
							endIndex++;
						}
						int namespace = Integer.parseInt(line.substring(index, endIndex));
						index = endIndex+2;
						endIndex = index+1;
						while (line.charAt(endIndex) != '\'' || (line.charAt(endIndex) == '\'' && line.charAt(endIndex-1) == '\\')) {
							endIndex++;
						}
						String articleName = line.substring(index, endIndex);
						
						if (namespace == 0) {
							bw.write(""+id+"\t"+articleName.replaceAll("\\\\", "")+"\n");
						}
						
						index = endIndex+1;
					}
				} else {
					continue;
				}
			}
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}

}
