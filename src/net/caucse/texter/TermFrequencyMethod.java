package net.caucse.texter;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.caucse.paperlibrary.IndexSet;
import net.caucse.paperlibrary.WordList;

public class TermFrequencyMethod implements Method {
	private HashMap<Integer, Integer> tf;
	
	private int docSize;

	@Override
	public void run(Collection<WordList> docs, IndexSet<String> word) {
		tf = new HashMap<Integer, Integer>(word.size());
		for (WordList doc : docs) {
			HashSet<String> wordSet = new HashSet<String>();
			for (List<String> list : doc) {
				for (String w : list) {
					if (wordSet.contains(w)) {
						continue;
					}
					
					int idx = word.getIndex(w);
					if (tf.containsKey(idx)) {
						int ia = tf.get(idx);
						tf.put(idx, ++ia);
					} else {
						tf.put(idx, 1);
					}
					wordSet.add(w);
				}
			}
		}
		
		docSize = docs.size();
	}

	@Override
	public void print(PrintStream edge) {
		print(edge, null);
	}

	@Override
	public void print(PrintStream edge, IndexSet<String> words) {
		if (tf == null || tf.isEmpty()) {
			System.err.println("NOT CALCULATED");
			return;
		}
		
		boolean printString = !(words == null || words.isEmpty());
		
		//int nv = words.size();
		
		for (int i : tf.keySet()) {
			int ia = tf.get(i);
			// Laplace Smoothing
			//double val = (ia+1) / (double)(sum[1]+nv);
			double val = (double)ia / docSize;
			if (printString) {
				edge.printf("%s %g\n", words.getAsIndex(i), val);
			} else {
				edge.printf("%d %g\n", i, val);
			}
		}
		//edge.println(sum[1]+nv);
	}

}
