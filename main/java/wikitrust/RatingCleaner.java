package wikitrust;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Clean the data collected from https://ckannet-storage.commondatastorage.googleapis.com/2012-10-22T184507/aft4.tsv.gz
 * 
 * @author Ke Wang
 *
 */
public class RatingCleaner {
	
	/**
	 * Remove all lines with NULL page namespace
	 * 
	 * @param source the source file path
	 * @param destination the destination file path
	 */
	public static void removeNULL(String source, String destination) {
		File input = new File(source);
		File output = new File(destination);
		
		try (Scanner sc = new Scanner(input)) {
			if (output.exists() == false) {
				try {
					output.createNewFile();
				} catch (IOException e) {
					System.err.println(e);
					System.exit(1);
				}
			}
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(output))) {
				sc.nextLine();
				while (sc.hasNextLine() == true) {
					String line = sc.nextLine();
					Scanner lsc = new Scanner(line);
					lsc.useDelimiter("\t");
					String timestamp = lsc.next();
					if (timestamp.startsWith("201")==false || timestamp.length() != 14) {
						lsc.close();
						continue;
					}
					lsc.next();
					lsc.next();
					String pageNamespace = lsc.next();
					if (pageNamespace.equals("NULL")) {
						lsc.close();
						continue;
					}
					lsc.close();
					bw.write(line);
					bw.write("\n");
				}
			} catch (IOException e) {
				System.err.println(e);
				System.exit(1);
			}
		} catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		RatingCleaner.removeNULL("share/raw_data/rating/aft4.tsv", "share/raw_data/rating/aft4_clean.tsv");
	}
	
}
