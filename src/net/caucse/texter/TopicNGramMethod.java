package net.caucse.texter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.caucse.paperlibrary.CountMap;
import net.caucse.paperlibrary.IndexSet;
import net.caucse.paperlibrary.ScoreMap;
import net.caucse.paperlibrary.WordList;

public class TopicNGramMethod implements Method {

	private IndexSet<List<Integer>> ngramSet;
	ScoreMap<Integer> ngramFre;
	
	private int n;
	
	public TopicNGramMethod(int n) {
		this.n = n;
		this.ngramSet = new IndexSet<List<Integer>>();
		this.ngramFre = new ScoreMap<Integer>();
	}

	@Override
	public void run(Collection<WordList> docs, IndexSet<String> word) {
		
		CountMap<Integer> idf = new CountMap<Integer>();
		for (WordList doc : docs) {
			HashSet<String> wordSet = new HashSet<String>();
			for (List<String> list : doc) {
				for (String w : list) {
					if (wordSet.contains(w)) {
						continue;
					}
					
					int i = word.getIndex(w);
					idf.add(i);
					wordSet.add(w);
				}
			}
			wordSet.clear();
		}
		
		int docSize = docs.size();
		
		for (WordList list : docs) {
			for (List<String> l : list) {
				int size = l.size();
				if (size < n) continue;
				
				ScoreMap<Integer> tfidf = new ScoreMap<Integer>();
				double sum = 0.0;
				
				for (String w : l) {
					int i = word.getIndex(w);
					tfidf.add(i);
				}
				
				for (int i : tfidf.keySet()) {
					double tfidfValue = Math.log(tfidf.get(i) + 1.0) * Math.log(docSize / idf.get(i));
					tfidf.put(i, tfidfValue);
					sum += tfidfValue;
				}
				
				double avg = sum / docSize;
				
				for (int i = 0; i < size-n; i++) {
					ArrayList<Integer> arr = new ArrayList<Integer>(n); 
					for (int j = 0; j < n; j++) {
						String w = l.get(j+i);
						arr.add(word.getIndex(w));
					}
					
					double maxTfidf = Double.MIN_VALUE;
					
					boolean ok = false;
					for (int j : tfidf.keySet()) {
						if (tfidf.get(j) > avg) {
							if (arr.contains(j)) {
								ok = true;
								maxTfidf = Math.max(maxTfidf, tfidf.get(j));
							}
						}
					}
					
					if (ok) {
						int idx = ngramSet.addReturnIndex(arr);
						ngramFre.add(idx, maxTfidf);
					}
				}
			}
		}
	}

	@Override
	public void print(PrintStream edge) {
		print(edge, null);
	}

	@Override
	public void print(PrintStream edge, IndexSet<String> words) {
		if (ngramSet == null || ngramSet.isEmpty()) {
			System.err.println("NOT CALCULATED");
			return;
		}
		
		boolean printString = !(words == null || words.isEmpty());
		
		for (List<Integer> list : ngramSet) {
			for (int i : list) {
				if (printString) {
					edge.printf("%s ", words.getAsIndex(i));
				} else {
					edge.printf("%d ", i);
				}
			}
			int idx = ngramSet.getIndex(list);
			System.out.println(ngramFre.get(idx));
		}
	}

}
