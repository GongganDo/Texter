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

public class TFIDFMethod implements Method {
	
	private ArrayList<ScoreMap<Integer>> score;
	private ArrayList<Double> scoreAvg;
	
	private double docSize;

	@Override
	public void run(Collection<WordList> docs, IndexSet<String> word) {
		score = new ArrayList<ScoreMap<Integer>>();
		scoreAvg = new ArrayList<Double>();
		
		ScoreMap<Integer> idf = new ScoreMap<Integer>();
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
		}
		
		docSize = docs.size();
		for (int i : idf.keySet()) {
			idf.put(i, Math.log(docSize / idf.get(i)));
		}
		
		
		for (WordList doc : docs) {
			// calculate tf-idf
			CountMap<Integer> tf = new CountMap<Integer>();
			for (List<String> list : doc) {
				for (String w : list) {
					int i = word.getIndex(w);
					tf.add(i);
				}
			}
			ScoreMap<Integer> tfidf = new ScoreMap<Integer>();
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
		
		for (ScoreMap<Integer> tfidf : score) {
			//Iterator<Double> avgIter = scoreAvg.iterator();
			for (int i : tfidf.keySet()) {
				double value = tfidf.get(i) / docSize;
				//double avg = avgIter.next();
				//if (value > avg) {
					if (printString) {
						edge.printf("%s %f ", words.getAsIndex(i), value);
					} else {
						edge.printf("%d %f ", i, value);
					}
				//}
			}
			System.out.println();
		}
	}

}
