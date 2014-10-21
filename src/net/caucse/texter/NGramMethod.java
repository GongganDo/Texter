package net.caucse.texter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.caucse.paperlibrary.CountMap;
import net.caucse.paperlibrary.IndexSet;
import net.caucse.paperlibrary.WordList;

public class NGramMethod implements Method{
	
	private IndexSet<List<Integer>> ngramSet;
	private CountMap<Integer> ngramFre;
	
	private int n;
	
	public NGramMethod(int n) {
		this.n = n;
		this.ngramSet = new IndexSet<List<Integer>>();
		this.ngramFre = new CountMap<Integer>();
	}

	@Override
	public void run(Collection<WordList> docs, IndexSet<String> word) {
		for (WordList list : docs) {
			int size = list.size();
			if (size < n) continue;
			for (int i = 0; i < size-n; i++) {
				ArrayList<Integer> arr = new ArrayList<Integer>(n); 
				for (int j = 0; j < n; j++) {
					String w = list.get(j+i);
					arr.add(word.getIndex(w));
				}
				int idx = ngramSet.addReturnIndex(arr);
				ngramFre.add(idx);
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
			System.out.println(ngramFre.get(list));
		}
	}

}
