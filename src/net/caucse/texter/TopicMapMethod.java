package net.caucse.texter;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.caucse.paperlibrary.IndexSet;
import net.caucse.paperlibrary.WordDocument;

public class TopicMapMethod implements Method {
	
	private HashMap<Integer, Map<Integer, Float>> cword;
	private double avg;

	@Override
	public void run(Collection<WordDocument> docs, IndexSet<String> words) {

		//n of word in doc
		HashMap<Integer, Integer> nword = new HashMap<Integer, Integer>();
		for (String w : words) {
			int n = 0;
			for (WordDocument d : docs) {
				if (d.containsKey(w)) ++n;
			}
			int index = words.getIndex(w);
			nword.put(index, n);
		}

		System.err.println("Number of word: " + words.size());
		System.err.println("End Number of Word");
		
		cword = new HashMap<Integer, Map<Integer, Float>>();
		for (WordDocument d : docs) {
			/*String[] dword = d.keySet().toArray(new String[0]);
			int len = dword.length;
			for (int i = 0; i < len; i++) {
				for (int j = i+1; j < len; j++) {*/
			Set<String> wofd = d.keySet();
			for (String wi : wofd) {
				int iIndex = words.getIndex(wi);
				for (String wj : wofd) {
					int jIndex = words.getIndex(wj);
					if (iIndex == jIndex) continue;
					//String wi = dword[i], wj = dword[j];
					if (cword.containsKey(iIndex)) {
						Map<Integer, Float> inmap = cword.get(iIndex);
						if (inmap.containsKey(iIndex)) {
							float value = inmap.get(jIndex);
							inmap.put(jIndex, ++value);
						} else {
							inmap.put(jIndex, 1.f);
						}
					} else {
						Map<Integer, Float> inmap = new HashMap<>();
						inmap.put(jIndex, 1.f);
						cword.put(iIndex, inmap);
					}
				}
			}
		}
		System.err.println("End Relationing of Word");
		
		//RelationTable rTable = new RelationTable();
		int n = docs.size();
		for (int i : cword.keySet()) {
			Map<Integer, Float> inmap = cword.get(i);
			for (int j : inmap.keySet()) {
				if (i == j) continue;
				try {
					float cij = inmap.get(j);
					int ni = nword.get(i);
					int nj = nword.get(j);
					float result = (cij / ni) * (float)Math.log(n / nj);
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
			Map<Integer, Float> inmap = cword.get(i);
			float max = Collections.max(inmap.values());
			float min = Collections.min(inmap.values());
			//System.err.printf("%d: [%f, %f] => ", i, min, max);
			for (int j : inmap.keySet()) {
				float value = inmap.get(j);
				value = (value - min) / (max - min);
				if (Float.isNaN(value)) value = 0.f;
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
			Map<Integer, Float> inmap = cword.get(i);
			for (int j : inmap.keySet()) {
				float value = inmap.get(j);
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
