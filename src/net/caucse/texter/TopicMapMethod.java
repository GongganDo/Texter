package net.caucse.texter;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.caucse.paperlibrary.IndexSet;
import net.caucse.paperlibrary.WordList;

public class TopicMapMethod implements Method {
	
	private HashMap<Integer, Map<Integer, Double>> cword;
	private double avg;

	@Override
	public void run(Collection<WordList> docs, IndexSet<String> words) {

		//n of word in doc
		HashMap<Integer, Integer> nword = new HashMap<Integer, Integer>();
		for (String w : words) {
			int n = 0;
			for (WordList d : docs) {
				if (d.contains(w)) ++n;
			}
			int index = words.getIndex(w);
			nword.put(index, n);
		}

		System.err.println("Number of word: " + words.size());
		System.err.println("End Number of Word");
		
		cword = new HashMap<Integer, Map<Integer, Double>>();
		for (WordList d : docs) {
			/*String[] dword = d.keySet().toArray(new String[0]);
			int len = dword.length;
			for (int i = 0; i < len; i++) {
				for (int j = i+1; j < len; j++) {*/
			
			HashSet<Integer> wofd = new HashSet<Integer>();
			for (String w : d) {
				int i = words.getIndex(w);
				wofd.add(i);
			}
			for (int iIndex : wofd) {
				for (int jIndex : wofd) {
					if (iIndex == jIndex) continue;
					//String wi = dword[i], wj = dword[j];
					if (cword.containsKey(iIndex)) {
						Map<Integer, Double> inmap = cword.get(iIndex);
						if (inmap.containsKey(iIndex)) {
							double value = inmap.get(jIndex);
							inmap.put(jIndex, ++value);
						} else {
							inmap.put(jIndex, 1.0);
						}
					} else {
						Map<Integer, Double> inmap = new HashMap<>();
						inmap.put(jIndex, 1.0);
						cword.put(iIndex, inmap);
					}
				}
			}
		}
		System.err.println("End Relationing of Word");
		
		//RelationTable rTable = new RelationTable();
		int n = docs.size();
		for (int i : cword.keySet()) {
			Map<Integer, Double> inmap = cword.get(i);
			for (int j : inmap.keySet()) {
				if (i == j) continue;
				try {
					double cij = inmap.get(j);
					int ni = nword.get(i);
					int nj = nword.get(j);
					double result = (cij / ni) * Math.log(n / nj);
					inmap.put(j, result);
					//rTable.put(i, j, result);
				} catch (NullPointerException e) {
					System.err.printf("(%d,%d) ", i, j);
				}
				
			}
		}
		System.err.println("End Calculate");
		
		//double min = rTable.normalize();
		double sum = 0.0;
		int count = 0;
		for (int i : cword.keySet()) {
			Map<Integer, Double> inmap = cword.get(i);
			double max = Collections.max(inmap.values());
			double min = Collections.min(inmap.values());
			//System.err.printf("%d: [%f, %f] => ", i, min, max);
			for (int j : inmap.keySet()) {
				double value = inmap.get(j);
				value = (value - min) / (max - min);
				if (Double.isNaN(value)) value = 0;
				inmap.put(j, value);
				sum += value;
				++count;
			}
			//System.err.println(sum);
		}
		avg = sum / count;
		
		System.err.println("Avg: " + avg);
		System.err.println("End Normalize");
	}
	
	@Override
	public void print(PrintStream out) {
		print(out, null);
	}
	
	@Override
	public void print(PrintStream out, IndexSet<String> words) {
		
		if (cword == null || cword.isEmpty()) {
			System.err.println("NOT CALCULATED");
			return;
		}

		boolean printString = !(words == null || words.isEmpty());
		
		//rTable.printConnection(min, ps_v, ps_e);
		int c = 0;
		for (int i : cword.keySet()) {
			Map<Integer, Double> inmap = cword.get(i);
			for (int j : inmap.keySet()) {
				double value = inmap.get(j);
				if (avg < value) {
					try {
						//double rValue = cword.get(key2).get(key);
						//if (avg < rValue) {
							if (printString) {
								String iStr = words.getAsIndex(i);
								String jStr = words.getAsIndex(j);
								out.printf("%s %s\n", iStr, jStr);
							} else {
								out.printf("%d %d\n", i, j);
							}
						//}
					} catch (NullPointerException e) {
						++c;
					}
				}
			}
		}
		System.err.println(" - NullPointerException: " + c);
		
		System.err.println("End Print");
	}

}
