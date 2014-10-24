package net.caucse.texter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.caucse.paperlibrary.IndexSet;
import net.caucse.paperlibrary.WordList;

public class TFIDFMethod implements Method {
	
	private ArrayList<HashMap<Integer, Double>> score;
	private ArrayList<Double> scoreAvg;

	@Override
	public void run(Collection<WordList> docs, IndexSet<String> word) {
		score = new ArrayList<HashMap<Integer, Double>>();
		scoreAvg = new ArrayList<Double>();
		
		HashMap<Integer, Double> idf = new HashMap<Integer, Double>();
		for (WordList doc : docs) {
			HashSet<String> wordSet = new HashSet<String>();
			for (List<String> list : doc) {
				for (String w : list) {
					if (wordSet.contains(w)) {
						continue;
					}
					
					int i = word.getIndex(w);
					if (idf.containsKey(i)) {
						idf.put(i, idf.get(i)+1);
					} else {
						idf.put(i, 1.0);
					}
					wordSet.add(w);
				}
			}
		}
		
		int docSize = docs.size();
		for (int i : idf.keySet()) {
			idf.put(i, Math.log(docSize / idf.get(i)));
		}
		
		
		for (WordList doc : docs) {
			// calculate tf-idf
			HashMap<Integer, Integer> tf = new HashMap<Integer, Integer>();
			for (List<String> list : doc) {
				for (String w : list) {
					int i = word.getIndex(w);
					if (tf.containsKey(i)) {
						tf.put(i, tf.get(i)+1);
					} else {
						tf.put(i, 1);
					}
				}
			}
			HashMap<Integer, Double> tfidf = new HashMap<Integer, Double>(word.size());
			double tfidfMax = Double.MIN_VALUE, tfidfMin = Double.MAX_VALUE;
			for (int i : tf.keySet()) {
				double tfValue = Math.log(tf.get(i) + 1);
				double tfidfValue = tfValue * idf.get(i);
				tfidf.put(i, tfidfValue);
				tfidfMax = Math.max(tfidfMax, tfidfValue);
				tfidfMin = Math.min(tfidfMin, tfidfValue);
			}
			
			// normalize
			double tfidfSum = 0.0;
			for (int i : tfidf.keySet()) {
				double tfidfValue = (tfidf.get(i) - tfidfMin) / (tfidfMax - tfidfMin);
				tfidfSum += tfidfValue;
				tfidf.put(i, tfidfValue);
				
			}
			score.add(tfidf);
			scoreAvg.add(tfidfSum / doc.wordSize());
		}
	}

	@Override
	public void print(PrintStream edge) {
		print(edge, null);
	}

	@Override
	public void print(PrintStream edge, IndexSet<String> words) {
		if (score == null || score.isEmpty()) {
			System.err.println("NOT CALCULATED");
			return;
		}
		
		boolean printString = !(words == null || words.isEmpty());
		
		for (HashMap<Integer, Double> tfidf : score) {
			Iterator<Double> avgIter = scoreAvg.iterator();
			for (int i : tfidf.keySet()) {
				double value = tfidf.get(i);
				double avg = avgIter.next();
				if (value > avg) {
					if (printString) {
						edge.printf("%s %f ", words.getAsIndex(i), value);
					} else {
						edge.printf("%d %f ", i, value);
					}
				}
			}
			System.out.println();
		}
	}

}
