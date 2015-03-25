package wikitrust;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class ScholarlyReferences {

	public static void main(String[] args) {
		File f = new File("share/data/rank_by_number_of_references");
		Map<Integer, Integer> numberOfReferencesToNumbers = new TreeMap<Integer, Integer>((k1,k2)->{
			if (k1.intValue() > k2.intValue()) {
				return 1;
			} else if (k1.intValue() < k2.intValue()) {
				return -1;
			} else {
				return 0;
			}
		});
		try (Scanner sc = new Scanner(f)) {
			while (sc.hasNext()) {
				sc.next();
				int numberOfReferences = sc.nextInt();
				Integer getCurrent = numberOfReferencesToNumbers.get(numberOfReferences);
				if (getCurrent == null) {
					numberOfReferencesToNumbers.put(numberOfReferences, 1);
				} else {
					numberOfReferencesToNumbers.replace(numberOfReferences, getCurrent.intValue()+1);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		for (Entry<Integer, Integer> entry : numberOfReferencesToNumbers.entrySet()) {
			System.out.println(entry.getKey()+"\t"+entry.getValue());
		}
		System.out.println();
		int count = 0;
		for (Entry<Integer, Integer> entry : numberOfReferencesToNumbers.entrySet()) {
			count += entry.getValue();
		}
		System.out.println(count);
	}

}
