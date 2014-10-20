package net.caucse.texter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import net.caucse.paperlibrary.IndexSet;
import net.caucse.paperlibrary.WordDocument;
import net.caucse.paperlibrary.WordDocumentReader;


public class Main {
	public static void main(String[] args) {
		
		Method method = null;
		
		if (args.length < 1) {
			printUsageAndExit();
		}
		
		boolean inputVertexFilename = false;
		String wordDocumentFilename = null;
		String vertexFilename = null;
		
		for (String arg : args) {
			if (inputVertexFilename) {
				vertexFilename = new String(arg);
				inputVertexFilename = false;
			} else if (arg.charAt(0) == '-') {
				if (arg.length() == 1) {
					printUsageAndExit();
				}
				switch (arg.charAt(1)) {
				case 'T':
				case 't':
					method = new TopicMapMethod();
					break;
				case 'F':
				case 'f':
					method = new TermFrequencyMethod();
					break;
				case 'D':
				case 'd':
					if (arg.length() != 6) {
						printUsageAndExit();
					}
					try {
						int md = Integer.parseInt(arg.substring(2));
						int month = md / 100, day = md % 100;
						if (month < 1 || month > 12 || day < 1 || day > 31) {
							printUsageAndExit();
						}
						method = new DailyIssueMethod(month, day);
					} catch (NumberFormatException e) {
						printUsageAndExit();
					}
					break;
				case 'V':
				case 'v':
					inputVertexFilename = true;
					break;
				default:
					printUsageAndExit();
				}
			} else {
				wordDocumentFilename = new String(arg);
			}
		}
		
		if (wordDocumentFilename == null || inputVertexFilename) {
			printUsageAndExit();
		}
		
		if (method == null) {
			// default method
			method = new TopicMapMethod();
		}
		
		long time = System.currentTimeMillis();
		
		PrintStream ps_v = null, ps_e = System.out;
		try {
			ArrayList<WordDocument> docs = new ArrayList<WordDocument>();
			IndexSet<String> word = new IndexSet<String>();
			WordDocumentReader wdr = new WordDocumentReader(wordDocumentFilename);
			WordDocument doc;
			while ((doc = wdr.read()) != null) {
				word.addAll(doc.keySet());
				docs.add(doc);
			}
			wdr.close();
			System.err.println("End Input");

			method.run(docs, word);
			
			if (vertexFilename != null) {
				method.print(ps_e);
				ps_v = new PrintStream(vertexFilename);
				
				for (String w : word) {
					ps_v.printf("%d %s\n", word.getIndex(w), w);
				}
			} else {
				method.print(ps_e, word);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ps_v != null) {
				ps_v.close();
			}
			if (ps_e != System.out) {
				ps_e.close();
			}
			
			time = System.currentTimeMillis() - time;
			System.err.println(time/3600000 + "h " + time%3600000/60000 + "m " + time%60000/1000 + "s");
		}
	}
	
	public static void printUsageAndExit() {
		System.err.println("[Usage] java (-t | -f | -d%mm%dd) (-v vertex_file) words_filename");
		System.err.println("[Options]");
		System.err.println(" methods: if not set, topic map is selected as default value");
		System.err.println("  -t calculate topic map. default value");
		System.err.println("  -f calculate term frequency");
		System.err.println("  -i calculate tf-idf and print at least average");
		System.err.println("  -d%mm%dd calculate daily issue using month and day (e.g. -d0301 -> 1st March)");
		System.err.println(" print option: if set, print index-value pair and to vertex_file, if not set, print string-value pair");
		System.err.println("  -v filename string-index pair");
		System.exit(1);
	}
}
