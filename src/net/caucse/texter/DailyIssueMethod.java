package net.caucse.texter;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.caucse.paperlibrary.IndexSet;
import net.caucse.paperlibrary.WordList;

public class DailyIssueMethod implements Method {
	
	private int month, day;
	private HashMap<Integer, Double> score;
	
	public DailyIssueMethod(int month, int day) {
		this.month = month; this.day = day;
	}

	@Override
	public void run(Collection<WordList> docs, IndexSet<String> word) {
		ArrayList<WordList> list = new ArrayList<WordList>();
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
		
		for (int i : score.keySet()) {
			if (printString) {
				edge.printf("%s %f\n", words.getAsIndex(i), score.get(i));
			} else {
				edge.printf("%d %f\n", i, score.get(i));
			}
		}
	}

}
