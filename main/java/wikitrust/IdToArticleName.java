package wikitrust;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import wikitrust.utilities.SearchTree;

/**
 * Interactive mode to get Wikipedia article name from Wikipedia ID
 * TODO how to make this process more efficient? 
 * 
 * @author Ke Wang
 *
 */
public class IdToArticleName {

	private static void help() {
		System.out.println("No arguments to enter interactive mode");
		System.out.println("Or specify one argument of Wikipedia ID");
	}
	
	public static void main(String[] args) {
		
		
		if (args.length >= 3) {
			IdToArticleName.help();
		}
		
		Path filePath = Paths.get("share/data/id_articlename");
		SearchTree<String> tree = new SearchTree<String>();
		try (Scanner sc = new Scanner(filePath.toFile())) {
			String line = null;
			while ((line=sc.nextLine()) != null) {
				int firstIndexOfComma = line.indexOf(',');
				if (firstIndexOfComma == -1) {
					break;
				}
				tree.insert(line.substring(0, firstIndexOfComma), line.substring(firstIndexOfComma+1));
			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} 
		
		if (args.length == 0) {
			Scanner sc = new Scanner(System.in);
			while (true) {
				System.out.print(">> ");
				String inputLine = sc.nextLine();
				
				if (inputLine.equals("quit")) {
					sc.close();
					break;
				}
				
				int inputKey;
				
				try {
					inputKey = Integer.parseInt(inputLine);
				} catch (NumberFormatException e) {
					System.out.println(e);
					continue;
				}
				
				System.out.println(tree.search(inputKey));
			}
		} else if (args.length == 1) {
			String result = tree.search(args[0]);
			if (result != null) {
				System.out.println(result);
			}
		}
		
	}

}
