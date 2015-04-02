package wikitrust.dataset;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import wikitrust.wikipedia.citation.Book;
import wikitrust.wikipedia.citation.Citation;
import wikitrust.wikipedia.citation.Journal;

class ArticleCitations {
	int id;
	List<Citation> citations;
	
	public ArticleCitations(int id) {
		this.id = id;
		this.citations = new ArrayList<Citation>();
	}
	
	public int getId() {
		return this.id;
	}
	
	public void addCitation(Citation c) {
		this.citations.add(c);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.id);
		sb.append("\n");
		for (Citation c : this.citations) {
			sb.append(c.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
}

class ArticleWordCount {
	int id;
	int wordCount;
	
	public ArticleWordCount(int id) {
		this.id = id;
	}
	
	public ArticleWordCount(int id, int wordCount) {
		this.id = id;
		this.wordCount = wordCount;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}
	
	public int getWordCount() {
		return this.wordCount;
	}
	
	public String toString() {
		return ""+this.id+"\t"+this.wordCount;
	}
}

public class DumpFileHelper {

	
	public static void extractCitations() throws FileNotFoundException, XMLStreamException {
		
		// get computer science ids
		Set<Integer> computerScienceIds = new HashSet<Integer>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File("share/data/computer_science_article_names_id")))) {
			String line = null;
			while ((line=br.readLine()) != null) {
				String[] item = line.split(Pattern.quote("\t"));
				computerScienceIds.add(Integer.parseInt(item[0]));
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		List<ArticleCitations> computerScienceArticles = new ArrayList<>();
		XMLInputFactory factory = XMLInputFactory.newInstance();

		File dumpFileDirectory = new File("share/raw_data/dump");
		for (File dumpFile : dumpFileDirectory.listFiles()) {
			
			XMLStreamReader parser = factory.createXMLStreamReader(new FileInputStream(dumpFile));

			
			while (parser.hasNext()) {
				
				int event = parser.next();
				
				switch (event) {
				
				case XMLStreamConstants.END_DOCUMENT:
					parser.close();
					break;
					
				case XMLStreamConstants.START_ELEMENT:
					
					// parse a namespace, filter out non-zero
					String tagName = parser.getLocalName();
					if (tagName.equals("ns")) {
						parser.next();
						int namespace = Integer.parseInt(parser.getText());
						if (namespace != 0) { continue; }

						parser.next();
						parser.next();
						parser.next();
						parser.next();
						
						int currentArticleId = Integer.parseInt(parser.getText());
						if (computerScienceIds.contains(currentArticleId) == false) {
							break;
						}
						
						// the article that we need!
						ArticleCitations article = new ArticleCitations(currentArticleId);
						computerScienceArticles.add(article);
						
						while (parser.hasNext()) {
							int iWantStartElementEvent = parser.next();
							if (iWantStartElementEvent == XMLStreamConstants.START_ELEMENT) {
								String tagName2 = parser.getLocalName();
								if (tagName2.equals("text")) {
									StringBuilder sb = new StringBuilder();
									while (parser.hasNext()) {
										parser.next();
										if (parser.hasText()) {
											sb.append(parser.getText());
										} else {
											break;
										}
									}
									String text = sb.toString().toLowerCase();
									
									// parse text
									// find cite journal
									int index = 0;
									while ((index=text.indexOf("{{cite journal", index))!=-1) {
										int endIndex = text.indexOf("}}", index);
										String citation = text.substring(index, endIndex+2).replaceAll(Pattern.quote("\n"), "");
										//System.out.println(citation);
										String title = "";
										String doi = "";
										String[] items = citation.substring(2, citation.length()-2).split(Pattern.quote("|"));
										for (String item : items) {
											item = item.trim();
											if (item.startsWith("title")) {
												title = item.substring(item.indexOf('=')+1);
											} else if (item.startsWith("doi")) {
												doi = item.substring(item.indexOf('=')+1);
											}
										}
										article.addCitation(new Journal(title, doi));
										index = endIndex+2;;
									}
									
									// find cite book
									index = 0;
									while ((index=text.indexOf("{{cite book", index))!=-1) {
										int endIndex = text.indexOf("}}", index);
										String citation = text.substring(index, endIndex+2).replaceAll(Pattern.quote("\n"), "");
										//System.out.println(citation);
										String title = "";
										String isbn = "";
										String[] items = citation.substring(2, citation.length()-2).split(Pattern.quote("|"));
										for (String item : items) {
											item = item.trim();
											if (item.startsWith("title")) {
												title = item.substring(item.indexOf('=')+1);
											} else if (item.startsWith("isbn")) {
												isbn = item.substring(item.indexOf('=')+1);
											}
										}
										article.addCitation(new Book(title, isbn));
										index = endIndex+2;;
									}
									break;
								}
								//flag = false;
							}
						}
					}
					break;
				}
			}
		}
		
		
		
		
		// output result 
		try (
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("share/data/computer_science_citations")));
				) {
			for (ArticleCitations a : computerScienceArticles) {
				bw.write(a.toString());
				bw.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		
		Map<Integer, String> idToName = new HashMap<>();
		
		// get computer science ids
		Set<Integer> computerScienceIds = new HashSet<Integer>();
		try (BufferedReader br = new BufferedReader(new FileReader(new File("share/data/computer_science_article_names_id")))) {
			String line = null;
			while ((line=br.readLine()) != null) {
				String[] item = line.split(Pattern.quote("\t"));
				computerScienceIds.add(Integer.parseInt(item[0]));
				idToName.put(Integer.parseInt(item[0]), item[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		List<ArticleWordCount> computerScienceArticles = new ArrayList<>();
		XMLInputFactory factory = XMLInputFactory.newInstance();

		File dumpFileDirectory = new File("share/raw_data/dump");
		for (File dumpFile : dumpFileDirectory.listFiles()) {
			
			XMLStreamReader parser = factory.createXMLStreamReader(new FileInputStream(dumpFile));

			
			while (parser.hasNext()) {
				
				int event = parser.next();
				
				switch (event) {
				
				case XMLStreamConstants.END_DOCUMENT:
					parser.close();
					break;
					
				case XMLStreamConstants.START_ELEMENT:
					
					// parse a namespace, filter out non-zero
					String tagName = parser.getLocalName();
					if (tagName.equals("ns")) {
						parser.next();
						int namespace = Integer.parseInt(parser.getText());
						if (namespace != 0) { continue; }

						parser.next();
						parser.next();
						parser.next();
						parser.next();
						
						int currentArticleId = Integer.parseInt(parser.getText());
						if (computerScienceIds.contains(currentArticleId) == false) {
							break;
						}
						
						// the article that we need!
						ArticleWordCount article = new ArticleWordCount(currentArticleId);
						computerScienceArticles.add(article);
						
						while (parser.hasNext()) {
							int iWantStartElementEvent = parser.next();
							if (iWantStartElementEvent == XMLStreamConstants.START_ELEMENT) {
								String tagName2 = parser.getLocalName();
								if (tagName2.equals("text")) {
									StringBuilder sb = new StringBuilder();
									while (parser.hasNext()) {
										parser.next();
										if (parser.hasText()) {
											sb.append(parser.getText());
										} else {
											break;
										}
									}
									String text = sb.toString().toLowerCase();
									
									article.setWordCount(text.length());
									
									break;
								}
								//flag = false;
							}
						}
					}
					break;
				}
			}
		}
		
		
		Collections.sort(computerScienceArticles, (e1,e2)->{
			if (e1.getWordCount() > e2.getWordCount()) {
				return -1;
			} else if (e1.getWordCount() < e2.getWordCount()) {
				return 1;
			} else {
				return 0;
			}
		});
		
		// output result 
		try (
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File("share/data/computer_science_word_count")));
				) {
			for (ArticleWordCount a : computerScienceArticles) {
				bw.write(""+a.getId());
				bw.write("\t");
				bw.write(idToName.get(a.getId()));
				bw.write("\t");
				bw.write(""+a.getWordCount());
				bw.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		

	}
	
}
