package net.caucse.texter;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.caucse.paperlibrary.WordDocument;


public class RelationTable<T> {
	private Map<T, Map<T, Double>> table;
	public RelationTable() {
		table = new TreeMap<T, Map<T, Double>>();
	}
	
	public double get(T first, T second) {
		return table.get(first).get(second);
	}
	
	public void put(T first, T second, double value) {
		if (table.containsKey(first)) {
			table.get(first).put(second, value);
		} else {
			Map<T, Double> inmap = new HashMap<T, Double>();
			inmap.put(second, value);
			table.put(first, inmap);
		}
	}
	
	public double calculate(T first, T second, Collection<? extends WordDocument> docs) {
		int ni=0, nj=0, cij=0;
		for (WordDocument doc : docs) {
			Set<String> keys = doc.keySet();
			boolean firstContains = keys.contains(first);
			boolean secondContains = keys.contains(second);
			if (firstContains) {
				if (secondContains) {
					++cij;
				}
				++ni;
			}
			if (secondContains) {
				++nj;
			}
		}
		
		double result = (cij / ni) * Math.log(docs.size() / nj);
		if (result > 0) {
			put(first, second, result);
		}
		return result;
	}
	
	public double normalize() {
		double sum = 0.0;
		int count = 0;
		for (T key : table.keySet()) {
			Map<T, Double> inmap = table.get(key);
			double max = Double.MIN_VALUE, min = Double.MAX_VALUE;
			for (double value : inmap.values()) {
				max = Math.max(max, value);
				min = Math.min(min, value);
			}
			for (T key2 : inmap.keySet()) {
				double value = inmap.get(key2);
				inmap.put(key2, (value - min) / (max - min));
				++count;
			}
		}
		return sum / count;
	}
	
	public void print(double min, PrintStream ps) {
		for (T key : table.keySet()) {
			Map<T, Double> inmap = table.get(key);
			for (T key2 : inmap.keySet()) {
				double value = inmap.get(key2);
				if (min < value) {
					ps.printf("%s, %s: %f\n", key.toString(), key2.toString(), value);
				}
			}
		}
	}
	
	/*public void printConnection(double min, PrintStream ps_v, PrintStream ps_e) {
		HashMap<T, Integer> wordmap = new HashMap<T, Integer>();
		TreeMap<Integer, T> rwordmap = new TreeMap<Integer, T>();
		int c = 0;
		for (T key : table.keySet()) {
			Map<T, Double> inmap = table.get(key);
			for (T key2 : inmap.keySet()) {
				double value = inmap.get(key2);
				if (min < value) {
					try {
						//double rValue = table.get(key2).get(key);
						//if (min < rValue) {
							int key1_key, key2_key;
							if (!wordmap.containsKey(key)) {
								key1_key = c;
								wordmap.put(key, c);
								rwordmap.put(c++, key);
							} else {
								key1_key = wordmap.get(key);
							}
							
							if (!wordmap.containsKey(key2)) {
								key2_key = c;
								wordmap.put(key2, c);
								rwordmap.put(c++, key2);
							} else {
								key2_key = wordmap.get(key2);
							}
							
							ps_e.printf("%d %d\n", key1_key, key2_key);
						//}
					} catch (NullPointerException e) {
						++c;
					}
				}
			}
		}
		
		for (int i : rwordmap.keySet()) {
			ps_v.printf("%d %s\n", i, rwordmap.get(i));
		}
		System.out.println("NullPointerException: " + c);
	}*/
}
