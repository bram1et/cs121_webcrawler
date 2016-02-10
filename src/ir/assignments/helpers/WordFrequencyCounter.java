package ir.assignments.helpers;

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
		Map<String, Frequency> oneGrams = new HashMap<String, Frequency>();
		Map<String, Frequency> twoGrams = new HashMap<String, Frequency>();
		Map<String, Frequency> threeGrams = new HashMap<String, Frequency>();

		String current = "";
		String minusOne = "";
		String minusTwo = "";
		String twoGram = "";
		String threeGram = "";

		for (String word : words) {
			minusTwo = minusOne;
			minusOne = current;
			current = word;
			if (!StopWords.isAStopword(current)) {
				if (oneGrams.containsKey(current)) {
					oneGrams.get(current).incrementFrequency();
				} else {
					oneGrams.put(current, new Frequency(current, 1));
				}
			}
			if (minusOne != null && !(StopWords.isAStopword(minusOne) && StopWords.isAStopword(current))) {
				twoGram = minusOne + " " + current;
				if (twoGrams.containsKey(twoGram)) {
					twoGrams.get(twoGram).incrementFrequency();
				} else {
					twoGrams.put(twoGram, new Frequency(twoGram, 1));
				}
			}

			if (minusOne != null && minusTwo != null && !(StopWords.isAStopword(minusTwo) && StopWords.isAStopword(minusOne) && StopWords.isAStopword(current))) {
				threeGram = minusTwo + " " + minusOne + " " + current;
				if (threeGrams.containsKey(threeGram)) {
					threeGrams.get(threeGram).incrementFrequency();
				} else {
					threeGrams.put(threeGram, new Frequency(threeGram, 1));
				}
			}
		}
		for (String key : oneGrams.keySet()) {
			freqList.add(oneGrams.get(key));
		}
		for (String key : twoGrams.keySet()) {
			if (twoGrams.get(key).getFrequency() > 1) {
				freqList.add(twoGrams.get(key));
			}
		}
		for (String key : threeGrams.keySet()) {
			if (threeGrams.get(key).getFrequency() > 1) {
				freqList.add(threeGrams.get(key));
			}
		}
		return freqList;
	}
}
