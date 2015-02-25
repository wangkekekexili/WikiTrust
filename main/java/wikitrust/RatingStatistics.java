package wikitrust;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import wikitrust.wikipedia.rating.ArticleWithRatings;
import wikitrust.wikipedia.rating.Rating;

public class RatingStatistics {
	
	private static Rating getARating(String line1,
			String line2,
			String line3,
			String line4) {
				
		Scanner lineScanner = new Scanner(line1);
		lineScanner.useDelimiter("\t");
		
		String timestamp = lineScanner.next();
		if (timestamp.length() != 14) {
			lineScanner.close();
			return null;
		}
		int articleId = lineScanner.nextInt();
		String articleTitle = lineScanner.next();
		if (articleTitle.equals("NULL")) {
			lineScanner.close();
			return null;
		}
		int articleNamespace = lineScanner.nextInt();
		long revisionId = lineScanner.nextLong();
		int userId = lineScanner.nextInt();
		lineScanner.nextInt();
		int trustworthy = lineScanner.nextInt();
		
		// only considers main Wikipedia articles
		if (articleNamespace != 0) {
			lineScanner.close();
			return null;
		} 
		
		int objective;
		int complete;
		int wellwritten;
		
		lineScanner.close();
		lineScanner = new Scanner(line2);
		lineScanner.useDelimiter("\t");
		lineScanner.next();
		lineScanner.nextInt();
		lineScanner.next();
		lineScanner.nextInt();
		lineScanner.nextLong();
		lineScanner.nextInt();
		lineScanner.nextInt();
		objective = lineScanner.nextInt();
		
		lineScanner.close();
		lineScanner = new Scanner(line2);
		lineScanner.useDelimiter("\t");
		lineScanner.next();
		lineScanner.nextInt();
		lineScanner.next();
		lineScanner.nextInt();
		lineScanner.nextLong();
		lineScanner.nextInt();
		lineScanner.nextInt();
		complete = lineScanner.nextInt();
		
		lineScanner.close();
		lineScanner = new Scanner(line3);
		lineScanner.useDelimiter("\t");
		lineScanner.next();
		lineScanner.nextInt();
		lineScanner.next();
		lineScanner.nextInt();
		lineScanner.nextLong();
		lineScanner.nextInt();
		lineScanner.nextInt();
		wellwritten = lineScanner.nextInt();
		
		lineScanner.close();
		
		return new Rating(timestamp, articleId, articleTitle, articleNamespace, revisionId, userId, trustworthy, objective, complete, wellwritten);
			
	}
	
	public static void main(String[] args) {
		
		Map<Integer, ArticleWithRatings> map = new TreeMap<Integer, ArticleWithRatings>();
		
		File f = new File("share/raw_data/rating/aft4_clean.tsv");
		try (Scanner sc = new Scanner(f)) {
			while (sc.hasNextLine()) {
				Rating r = getARating(sc.nextLine(), sc.nextLine(), sc.nextLine(), sc.nextLine());
				if (r==null) {
					continue;
				}
				ArticleWithRatings item = map.get(r.getArticleId());
				if (item == null) {
					ArticleWithRatings a = new ArticleWithRatings(r.getArticleId(), r.getArticleTitle());
					a.addRating(r);
					map.put(r.getArticleId(), a);
				} else {
					item.addRating(r);
				}
			}
			
			File outputFile = new File("share/data/rating.tsv");
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
				for (Integer i : map.keySet()) {
					ArticleWithRatings a = map.get(i);
					List<Rating> ratings = a.getRatings();
					int numberOfRatings = 0;
					int numberOfRatingsByRegisteredUsers = 0;
					double totalRating = 0;
					int totalNumberOfRatings = 0;
					double totalRatingByRegisteredUsers = 0;
					int totalNumberOfRatingsByRegisteredUsers = 0;
					double totalTrustworthy = 0;
					int totalNumberOfTrustworthy = 0;
					double totalTrustworthyByRegisteredUsers = 0;
					int totalNumberOfTrustworthyByRegisteredUsers = 0;
					
					for (Rating r : ratings) {
						numberOfRatings++;
						if (r.getTrustworthy() != 0) {
							totalNumberOfRatings++;
							totalNumberOfTrustworthy++;
							totalRating += r.getTrustworthy();
							totalTrustworthy += r.getTrustworthy();
						}
						if (r.getObjective() != 0) {
							totalNumberOfRatings++;
							totalRating += r.getObjective();
						}
						if (r.getComplete() != 0) {
							totalNumberOfRatings++;
							totalRating += r.getComplete();
						}
						if (r.getWellwritten() != 0) {
							totalNumberOfRatings++;
							totalRating += r.getWellwritten();
						}
						
						// for registered users
						if (r.getUserId() == 1) {
							numberOfRatingsByRegisteredUsers++;
							if (r.getTrustworthy() != 0) {
								totalNumberOfRatingsByRegisteredUsers++;
								totalNumberOfTrustworthyByRegisteredUsers++;
								totalRatingByRegisteredUsers += r.getTrustworthy();
								totalTrustworthyByRegisteredUsers += r.getTrustworthy();
							}
							if (r.getObjective() != 0) {
								totalNumberOfRatingsByRegisteredUsers++;
								totalRatingByRegisteredUsers += r.getObjective();
							}
							if (r.getComplete() != 0) {
								totalNumberOfRatingsByRegisteredUsers++;
								totalRatingByRegisteredUsers += r.getComplete();
							}
							if (r.getWellwritten() != 0) {
								totalNumberOfRatingsByRegisteredUsers++;
								totalRatingByRegisteredUsers += r.getWellwritten();
							}
						}
					}
					
					double averageRatingByRegisteredUsers = 0;
					if (totalNumberOfRatingsByRegisteredUsers > 0) {
						averageRatingByRegisteredUsers = totalRatingByRegisteredUsers/totalNumberOfRatingsByRegisteredUsers;
					}
					double averageTrustworthy = 0;
					if (totalNumberOfTrustworthy > 0) {
						averageTrustworthy = totalTrustworthy/totalNumberOfTrustworthy;
					}
					double averageTrustworthyByRegisteredUsers = 0;
					if (totalTrustworthyByRegisteredUsers > 0) {
						averageTrustworthyByRegisteredUsers = totalTrustworthyByRegisteredUsers/totalNumberOfTrustworthyByRegisteredUsers;
					}
					
					bw.write(a.getArticleId()+" "+a.getArticleTitle()+" "
							+numberOfRatings+" "+numberOfRatingsByRegisteredUsers
							+" "+totalRating/totalNumberOfRatings+" "
							+averageRatingByRegisteredUsers+" "
							+averageTrustworthy+" "
							+averageTrustworthyByRegisteredUsers+"\n");
					
				}
			} catch (IOException e) {
				System.err.println(e);
				System.exit(1);
			}
		} catch (FileNotFoundException e) {
			System.err.println(e);
		}
		
	}
}
