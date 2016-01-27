package ir.assignments.three.helpers;

import ir.assignments.three.helpers.Frequency;
import ir.assignments.three.helpers.Utilities;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Counts the total number of words and their frequencies in a text file.
 */
public final class WordFrequencyCounter {
	/**
	 * This class should not be instantiated.
	 */
	public WordFrequencyCounter() {}
	
	/**
	 * Takes the input list of words and processes it, returning a list
	 * of {@link Frequency}s.
	 * 
	 * This method expects a list of lowercase alphanumeric strings.
	 * If the input list is null, an empty list is returned.
	 * 
	 * There is one frequency in the output list for every 
	 * unique word in the original list. The frequency of each word
	 * is equal to the number of times that word occurs in the original list. 
	 * 
	 * The returned list is ordered by decreasing frequency, with tied words sorted
	 * alphabetically.
	 * 
	 * The original list is not modified.
	 * 
	 * Example:
	 * 
	 * Given the input list of strings 
	 * ["this", "sentence", "repeats", "the", "word", "sentence"]
	 * 
	 * The output list of frequencies should be 
	 * ["sentence:2", "the:1", "this:1", "repeats:1",  "word:1"]
	 *  
	 * @param words A list of words.
	 * @return A list of word frequencies, ordered by decreasing frequency.
	 */
	public static List<Frequency> computeWordFrequencies(List<String> words) {
		/*
			Iterates through list of words creating a frequency and/or
			incrementing frequency
		*/
		List<Frequency> freqList = new ArrayList<Frequency>();
		Map<String, Frequency> freqMap = new HashMap<String, Frequency>();
		//find if frequency already exist for word
			//if yes, update frequency
			//if no, add new frequency

		for (String word : words) {
			if (freqMap.containsKey(word)) {
//				freqMap.put(word, freqMap.get(word) + 1);
				freqMap.get(word).incrementFrequency();
			} else {
				freqMap.put(word, new Frequency(word, 1));
			}
		}
//		freqList.add(new Frequency("item", -1));
		for (String key : freqMap.keySet()) {
//			Frequency newFreq = new Frequency(key, freqMap.get(key));
			freqList.add(freqMap.get(key));
		}

		return freqList;
	}
	
	/**
	 * Runs the word frequency counter. The input should be the path to a text file.
	 * 
	 * @param args The first element should contain the path to a text file.
	 */
	public static void main(String[] args) {
		File file = new File(args[0]);
		List<String> words = Utilities.tokenizeFile(file);
		List<Frequency> frequencies = computeWordFrequencies(words);
		Utilities.printFrequencies(frequencies);
	}
}
