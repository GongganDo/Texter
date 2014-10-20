package net.caucse.texter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.caucse.paperlibrary.IndexSet;
import net.caucse.paperlibrary.WordDocument;

public class TFIDFMethod implements Method {
	
	private ArrayList<HashMap<Integer, Double>> score;
	private ArrayList<Double> scoreAvg;

	@Override
	public void run(Collection<WordDocument> docs, IndexSet<String> word) {
		score = new ArrayList<HashMap<Integer, Double>>();
		scoreAvg = new ArrayList<Double>();
		
		HashMap<Integer, Double> idf = new HashMap<Integer, Double>();
		for (WordDocument doc : docs) {
			for (String w : doc.keySet()) {
				int i = word.getIndex(w);
				if (idf.containsKey(i)) {
					idf.put(i, idf.get(i)+1);
				} else {
					idf.put(i, 1.0);
				}
			}
		}
		
		int docSize = docs.size();
		for (int i : idf.keySet()) {
			idf.put(i, Math.log(docSize / idf.get(i)));
		}
		
		
		for (WordDocument doc : docs) {
			// calculate tf-idf
			HashMap<Integer, Double> tfidf = new HashMap<Integer, Double>(word.size());
			double tfidfMax = Double.MIN_VALUE, tfidfMin = Double.MAX_VALUE;
			for (String w : doc.keySet()) {
				int i = word.getIndex(w);
				double tfValue = Math.log(doc.get(w) + 1);
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
			scoreAvg.add(tfidfSum / doc.size());
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
		
		boolean printString = words == null || words.isEmpty();
		
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
