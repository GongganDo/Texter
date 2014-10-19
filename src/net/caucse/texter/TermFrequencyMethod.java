package net.caucse.texter;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;

import net.caucse.paperlibrary.IndexSet;
import net.caucse.paperlibrary.WordDocument;

public class TermFrequencyMethod implements Method {
	private HashMap<Integer, int[]> tf;
	
	private int[] sum;
	private double docSize;

	@Override
	public void run(Collection<WordDocument> docs, IndexSet<String> word) {
		tf = new HashMap<Integer, int[]>(word.size());
		sum = new int[] {0,0};
		for (WordDocument doc : docs) {
			for (String w : doc.keySet()) {
				int idx = word.getIndex(w);
				int val = doc.get(w);
				if (tf.containsKey(idx)) {
					int[] ia = tf.get(idx);
					++ia[0]; ia[1] += val;
				} else {
					tf.put(idx, new int[] {1, val});
				}
				++sum[0];
				sum[1] += val;
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
		
		int nv = words.size();
		
		for (int i : tf.keySet()) {
			int[] ia = tf.get(i);
			// Laplace Smoothing
			double val = (ia[1]+1) / (double)(sum[1]+nv);
			if (printString) {
				edge.printf("%s %g\n", words.getAsIndex(i), val);
			} else {
				edge.printf("%d %g\n", i, val);
			}
		}
		edge.println(sum[1]+nv);
	}

}
