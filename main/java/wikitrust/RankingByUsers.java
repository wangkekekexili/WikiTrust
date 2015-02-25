package wikitrust;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class RankingByUsers {

	class RankInfo {
		int id;
		String article;
		int numberOfUsers;
		int numberOfRegisteredUsers;
		double averageRating;
		double averageRatingByRegisteredUsers;
		double averageTrustworthyRating;
		double averageTrustworthyRatingByRegisteredUsers;
		
		RankInfo(String line) {
			Scanner sc = new Scanner(line);
			sc.useDelimiter(" ");
			this.id = sc.nextInt();
			this.article = sc.next();
			this.numberOfUsers = sc.nextInt();
			this.numberOfRegisteredUsers = sc.nextInt();
			this.averageRating = sc.nextDouble();
			this.averageRatingByRegisteredUsers = sc.nextDouble();
			this.averageTrustworthyRating = sc.nextDouble();
			this.averageTrustworthyRatingByRegisteredUsers = sc.nextDouble();
			sc.close();
		}
		
		int getId() {
			return this.id;
		}
		
		String getArticle() {
			return this.article;
		}
		
		int getNumberOfUsers() {
			return this.numberOfUsers;
		}
		
		double getAverageTrustworthyRating() {
			return this.averageTrustworthyRating;
		}
		
	}
	
	public void run() {
		File f = new File("share/data/rating");
		List<RankInfo> list = new ArrayList<RankInfo>();
		try (Scanner sc = new Scanner(f)) {
			while (sc.hasNextLine()) {
				RankInfo temp = new RankInfo(sc.nextLine());
				if (temp.getNumberOfUsers() >= 50) {
					list.add(temp);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println(f);
			System.exit(1);
		}
		Collections.sort(list, (e1, e2)->{
			if (((RankInfo)e1).getAverageTrustworthyRating() > ((RankInfo)e2).getAverageTrustworthyRating()) {
				return -1;
			} else if (((RankInfo)e1).getAverageTrustworthyRating() < ((RankInfo)e2).getAverageTrustworthyRating()) {
				return 1;
			} else {
				return 0;
			}
		});

		try (BufferedWriter bw = new BufferedWriter(new FileWriter("share/data/ratingOrderByAverageTrustworthyRating.tsv"))) {
			for (RankInfo r : list) {
				bw.write(r.getId()+"\t"
					+r.getArticle()+"\t"
					+r.getNumberOfUsers()+"\t"
					+r.getAverageTrustworthyRating()+"\n");
			}
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
		
	}

	public static void main(String[] args) {
		new RankingByUsers().run();
	}
	
}
