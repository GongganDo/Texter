package net.caucse.texter;

import java.io.PrintStream;
import java.util.Collection;

import net.caucse.paperlibrary.IndexSet;
import net.caucse.paperlibrary.WordDocument;

public interface Method {
	public void run(Collection<WordDocument> docs, IndexSet<String> word);
	public void print(PrintStream edge);
	public void print(PrintStream edge, IndexSet<String> words);
}
