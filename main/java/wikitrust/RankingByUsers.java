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

	public static int MINIMUM_NUMBER_OF_USERS = 200;
	public static int MINIMUM_NUMBER_OF_REGISTERED_USERS = 15;
	
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
		
		int getNumberOfRegisteredUsers() {
			return this.numberOfRegisteredUsers;
		}
		
		double getAverageRating() {
			return this.averageRating;
		}
		
		double getAverageRatingByRegisteredUsers() {
			return this.averageRatingByRegisteredUsers;
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
				if (temp.getNumberOfUsers() >= MINIMUM_NUMBER_OF_USERS) {
					list.add(temp);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println(f);
			System.exit(1);
		}
		
		// order by average trustworthy rating 
		Collections.sort(list, (e1, e2)->{
			if (((RankInfo)e1).getAverageTrustworthyRating() > ((RankInfo)e2).getAverageTrustworthyRating()) {
				return -1;
			} else if (((RankInfo)e1).getAverageTrustworthyRating() < ((RankInfo)e2).getAverageTrustworthyRating()) {
				return 1;
			} else {
				if (e1.getNumberOfUsers() > e2.getNumberOfUsers()) {
					return -1;
				} else if (e1.getNumberOfUsers() < e2.getNumberOfUsers()) {
					return 1;
				} else {
					return 0;
				}
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
		
		// order by average rating
		Collections.sort(list, (e1, e2)->{
			if (e1.getAverageRating() > e2.getAverageRating()) {
				return -1;
			} else if (e1.getAverageRating() < e2.getAverageRating()) {
				return 1;
			} else {
				if (e1.getNumberOfUsers() > e2.getNumberOfUsers()) {
					return -1;
				} else if (e1.getNumberOfUsers() < e2.getNumberOfUsers()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("share/data/ratingOrderByAverageRating.tsv"))) {
			for (RankInfo r : list) {
				bw.write(r.getId()+"\t"
						+r.getArticle()+"\t"
						+r.getNumberOfUsers()+"\t"
						+r.getAverageRating()+"\n");		
			}
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
		
		// consider articles with registered users more than default
		List<RankInfo> newList = new ArrayList<RankInfo>();
		for (RankInfo r : list) {
			if (r.getNumberOfRegisteredUsers() >= MINIMUM_NUMBER_OF_REGISTERED_USERS) {
				newList.add(r);
			}
		}
		list = newList;
		
		// order by number of registered users, average rating by registered users
		Collections.sort(list, (e1,e2)->{
			if (e1.getNumberOfRegisteredUsers() > e2.getNumberOfRegisteredUsers()) {
				return -1;
			} else if (e1.getNumberOfRegisteredUsers() < e2.getNumberOfRegisteredUsers()) {
				return 1;
			} else {
				if (e1.getAverageRatingByRegisteredUsers() < e2.getAverageRatingByRegisteredUsers()) {
					return -1;
				} else if (e1.getAverageRatingByRegisteredUsers() > e2.getAverageRatingByRegisteredUsers()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("share/data/ratingOrderByNumberOfRegisteredUsers.tsv"))) {
			for (RankInfo r : list) {
				bw.write(r.getId()+"\t"
						+r.getArticle()+"\t"
						+r.getNumberOfRegisteredUsers()+"\t"
						+r.getAverageRatingByRegisteredUsers()+"\n");		
			}
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
		
		// order by rating by registered users, number of registered users
		Collections.sort(list, (e1,e2)->{
			if (e1.getAverageRatingByRegisteredUsers() > e2.getAverageRatingByRegisteredUsers()) {
				return -1;
			} else if (e1.getAverageRatingByRegisteredUsers() < e2.getAverageRatingByRegisteredUsers()) {
				return 1;
			} else {
				if (e1.getNumberOfRegisteredUsers() < e2.getNumberOfRegisteredUsers()) {
					return -1;
				} else if (e1.getNumberOfRegisteredUsers() > e2.getNumberOfRegisteredUsers()) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("share/data/ratingOrderByAverageRatingByRegisteredUsers.tsv"))) {
			for (RankInfo r : list) {
				bw.write(r.getId()+"\t"
						+r.getArticle()+"\t"
						+r.getNumberOfRegisteredUsers()+"\t"
						+r.getAverageRatingByRegisteredUsers()+"\n");		
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
