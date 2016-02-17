package ir.assignments.helpers;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.lang.Math;


/**
 * A collection of utility methods for text processing.
 */
public class Utilities {

	/**
	 * Reads the input text file and splits it into alphanumeric tokens.
	 * Returns an ArrayList of these tokens, ordered according to their
	 * occurrence in the original text file.
	 * 
	 * Non-alphanumeric characters delineate tokens, and are discarded.
	 *
	 * Words are also normalized to lower case. 
	 * 
	 * Example:
	 * 
	 * Given this input string
	 * "An input string, this is! (or is it?)"
	 * 
	 * The output list of strings should be
	 * ["an", "input", "string", "this", "is", "or", "is", "it"]
	 * 
	 * @param input The file to read in and tokenize.
	 * @return The list of tokens (words) from the input file, ordered by occurrence.
	 */
	public static ArrayList<String> tokenizeFile(File input) {
		/*
			Uses a set of delimeters (anything not a letter or number)
			to tokenize input file.
		*/
		ArrayList<String> returnList = new ArrayList<String>();
		String nextWord;
		try {
			Scanner s = new Scanner(input).useDelimiter("[^A-Za-z0-9']");
			while(s.hasNext()) {
				nextWord = s.next().toLowerCase();
				if (nextWord.length() > 0) {
					returnList.add(nextWord);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not Found :(");
		}

		return returnList;
		
	}

	public static ArrayList<String> tokenizeString(String input) {
		/*
			Uses a set of delimeters (anything not a letter or number)
			to tokenize input file.
		*/
		ArrayList<String> returnList = new ArrayList<String>();
		StopWords stopWords = new StopWords();
		String nextWord;
		try {
			Scanner s = new Scanner(input).useDelimiter("[^A-Za-z0-9']");
			while(s.hasNext()) {
				nextWord = s.next().toLowerCase();
				if (nextWord.length() > 0) {
					returnList.add(nextWord);
				}

			}
		} catch (Exception e) {
			System.out.println("File not Found :(");
		}

		return returnList;

	}
	
	/**
	 * Takes a list of {@link Frequency}s and prints it to standard out. It also
	 * prints out the total number of items, and the total number of unique items.
	 * 
	 * Example one:
	 * 
	 * Given the input list of word frequencies
	 * ["sentence:2", "the:1", "this:1", "repeats:1",  "word:1"]
	 * 
	 * The following should be printed to standard out
	 * 
	 * Total item count: 6
	 * Unique item count: 5
	 * 
	 * sentence	2
	 * the		1
	 * this		1
	 * repeats	1
	 * word		1
	 * 
	 * 
	 * Example two:
	 * 
	 * Given the input list of 2-gram frequencies
	 * ["you think:2", "how you:1", "know how:1", "think you:1", "you know:1"]
	 * 
	 * The following should be printed to standard out
	 * 
	 * Total 2-gram count: 6
	 * Unique 2-gram count: 5
	 * 
	 * you think	2
	 * how you		1
	 * know how		1
	 * think you	1
	 * you know		1
	 *
	 * param frequencies A list of frequencies.
	 */
	
	public static class FreqComparator implements Comparator<Frequency> {
		/*
			Comparator created to sort frequency. First compares on 
			frequency. If there is a tie, compares on text.
		*/
		public int compare(Frequency freq1, Frequency freq2) {
			if (freq1.getFrequency() == freq2.getFrequency()) {
				return freq1.getText().compareTo(freq2.getText());
			} else {
				return freq2.getFrequency() - freq1.getFrequency();
			}
		}
	}

	public static class PostingsListComparator implements Comparator<PostingsEntry> {
		public int compare(PostingsEntry postingsEntry1, PostingsEntry postingsEntry2) {
			return Double.compare(postingsEntry2.getTfidf(), postingsEntry1.getTfidf());
		}
	}

	public static class InfluenceComparator implements Comparator<Influence> {
		public int compare(Influence influenceEntry1, Influence influenceEntry2) {
			return Double.compare(influenceEntry1.getInfluence(), influenceEntry2.getInfluence());
		}
	}

	public static class AlphabeticalComparator implements Comparator<Frequency> {
		/*
			Comparator created to sort a frequency list alphabetically
		*/
		public int compare(Frequency freq1, Frequency freq2) {
			return freq1.getText().compareTo(freq2.getText());
		}
	}

	public static void printFrequencies(List<Frequency> frequencies) {
		/*
			Sorts list of frequencies then iterates through sorted list
			and prints out frequencies.
		*/
		int total_count = 0;
		int unique_count = 0;
		int longest_word = 0;
		String type = "item";
		
		if (frequencies.get(0).getFrequency() == -1) {
			type = frequencies.get(0).getText();
			frequencies.remove(0);
		}
		
		Collections.sort(frequencies, new FreqComparator());
		for (Frequency freq: frequencies) {
			unique_count += 1;
			total_count += freq.getFrequency();
			longest_word = Math.max(longest_word, freq.getText().length());
		}

		System.out.format("Total " + type + " count: %d\n", total_count);
		System.out.format("Unique " + type + " count: %d\n", unique_count);
		
		longest_word += 2;
		for (Frequency freq: frequencies) {
			System.out.format("%-"+ longest_word +"s%1d\n", freq.getText(), freq.getFrequency());
		}
	}

	public static void printFrequenciesToFile(List<Frequency> frequencies, String fileName, String url, Boolean alphaNumeric) {
		/*
			Sorts list of frequencies then iterates through sorted list
			and prints out frequencies.
		*/
		int total_count = 0;
		int unique_count = 0;
		int longest_word = 0;
		String type = "item";
		String totalString = "Total " + type + " count: %d\n";
		String uniqueString = "Unique " + type +  " count: %d\n";
		String wordCountString;
		if (alphaNumeric) {
			Collections.sort(frequencies, new AlphabeticalComparator());
		} else {
			Collections.sort(frequencies, new FreqComparator());
		}
		for (Frequency freq: frequencies) {
			unique_count += 1;
			total_count += freq.getFrequency();
			longest_word = Math.max(longest_word, freq.getText().length());
		}



		longest_word += 2;
		wordCountString = "%-" + longest_word +"s%1d\n";

		try (BufferedWriter freqWriter = new BufferedWriter(new FileWriter(fileName, true))) {
			freqWriter.write(String.format(totalString, total_count));
			freqWriter.write(String.format(uniqueString, unique_count));
			for (Frequency freq: frequencies) {
				freqWriter.write(String.format(wordCountString, freq.getText(), freq.getFrequency()));
				freqWriter.flush();
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public static void writeDocumentFrequencyToFile(List<Frequency> frequencies, String folderName, String url, Boolean alphaNumeric) {
		/*
			Sorts list of frequencies then iterates through sorted list
			and prints out frequencies.
		*/
		int total_count = 0;
		int unique_count = 0;
		int longest_word = 0;
		String type = "item";
		String totalString = "Total " + type + " count: %d\n";
		String uniqueString = "Unique " + type +  " count: %d\n";
		String wordCountString;
		String fileName;

		File dfFile;

		Collections.sort(frequencies, new AlphabeticalComparator());

		for (Frequency freq: frequencies) {
			unique_count += 1;
			total_count += freq.getFrequency();
			longest_word = Math.max(longest_word, freq.getText().length());
		}

		longest_word += 2;
		wordCountString = "%-" + longest_word +"s%1d\n";
		for (Frequency freq: frequencies) {
			fileName = folderName + freq.getText().charAt(0) + ".txt";
			dfFile = new File(fileName);
			if (!dfFile.exists()) {
				try {
					dfFile.createNewFile();
				} catch (IOException e) {
					System.err.println(e);
				}

			}
			try (BufferedWriter freqWriter = new BufferedWriter(new FileWriter(fileName, true))) {
				freqWriter.write(String.format(wordCountString, freq.getText(), freq.getFrequency()));
				freqWriter.flush();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

	public static void printSubdomainFrequenciesToFile(List<Frequency> frequencies, String fileName) {
		int total_subdomains = 0;
		int longest_word = 0;
		String domain = ".ics.uci.edu";

		Collections.sort(frequencies, new AlphabeticalComparator());
		for (Frequency freq : frequencies) {
			total_subdomains++;
			longest_word = Math.max(longest_word, freq.getText().length());
		}
		longest_word += domain.length() + 2;
		String wordCountString = "%-" + longest_word +"s%1d\n";

		try (BufferedWriter freqWriter = new BufferedWriter(new FileWriter(fileName))) {
			freqWriter.write("Number of subdomains found: " + total_subdomains + "\n\n");
			freqWriter.write(String.format("%-" + longest_word + "s%s", "subdomain","unique pages in subdomain"));
			freqWriter.write("\n--------------------------------------------------------\n");
			for (Frequency freq: frequencies) {
				freqWriter.write(String.format(wordCountString, freq.getText()+ domain + ", ", freq.getFrequency()));
				freqWriter.flush();
			}
		}
		catch (IOException e) {
			System.err.println(e);
		}
	}

	public static String stringCombiner(Integer startIndex, Integer endIndex, Boolean spaces, List<String> words) {
		/*
			Helper function to concatenate separate strings from list of words
			given the start index and index from list. Option to include space
			while concatenating strings
		*/
		StringBuilder sb = new StringBuilder(words.get(startIndex));
		startIndex += 1;
		for (int index = startIndex; index < endIndex + 1; index++) {
			if (spaces) {
				sb.append(" ");
			}
			sb.append(words.get(index));
		}
		return sb.toString();
	}

	public static Integer getDocumentFrequency(String searchTerm) {
		String pathString = Paths.get("").toAbsolutePath().toString();
		String dfFolder = pathString + "/documentFrequencies/";
		String dfFile = dfFolder + searchTerm.charAt(0) + ".txt";
		Integer frequency = -1;
		String term = "this is dog";
		String line;
		searchTerm = searchTerm.trim();
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(dfFile));
			while (((line = fileReader.readLine()) != null)) {
				List<String> wordAndCount = Arrays.asList(line.split("\\s+"));
				term = Utilities.stringCombiner(0, wordAndCount.size() - 2, true, wordAndCount).trim();
				frequency = Integer.parseInt(wordAndCount.get(wordAndCount.size() - 1));
				if (term.equals(searchTerm)) {
					break;
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}
		return frequency;
	}

	public static HashMap<String, Integer> getDocumentFrequencyMap() {
		HashMap<String, Integer> documentFrequency= new HashMap<String, Integer>();
		String pathString = Paths.get("").toAbsolutePath().toString();
		String dfFile = pathString + "/documentFrequencies.txt";
		String line;
		String term;
		Integer frequency;
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(dfFile));
			while (((line = fileReader.readLine()) != null)) {
				List<String> wordAndCount = Arrays.asList(line.split("\\s+"));
				term = Utilities.stringCombiner(0, wordAndCount.size() - 2, true, wordAndCount).trim();
				frequency = Integer.parseInt(wordAndCount.get(wordAndCount.size() - 1));
				documentFrequency.put(term, frequency);
			}
			fileReader.close();
		} catch (IOException e) {
			System.err.println(e);
			System.exit(1);
		}
		return documentFrequency;
	}

	public static void writePostingsListToFile(TreeMap<String, List<PostingsEntry>> postingsList, String postingsListFolder) {
		for (String key : postingsList.keySet()) {
			String postingsFileName = postingsListFolder + key.charAt(0) + ".txt";
			File postingsFile = new File(postingsFileName);
			if (!postingsFile.exists()) {
				try {
					postingsFile.createNewFile();
				} catch (IOException e) {
					System.err.println(e);
				}
			}
			List<PostingsEntry> aPostingsList = postingsList.get(key);
			Collections.sort(aPostingsList, new PostingsListComparator());
			try (BufferedWriter freqWriter = new BufferedWriter(new FileWriter(postingsFileName, true))) {
				freqWriter.write(key + " : ");
				freqWriter.write(aPostingsList.toString());
				freqWriter.newLine();
				freqWriter.flush();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

	public static void writeInfluenceToFile(HashMap<String, Influence> linkInFluence) {
		String pathString = Paths.get("").toAbsolutePath().toString();
		String influenceFileName = pathString + "/influenceFilev5.txt";
		File postingsFile = new File(influenceFileName);
		ArrayList<Influence> influenceList = new ArrayList<Influence>();
		if (!postingsFile.exists()) {
			try {
				postingsFile.createNewFile();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
		for (String influence : linkInFluence.keySet()) {
			influenceList.add(linkInFluence.get(influence));
		}
		for (Influence influence : influenceList) {
			try (BufferedWriter freqWriter = new BufferedWriter(new FileWriter(influenceFileName, true))) {
				freqWriter.write(influence.getUrlHashCode() + " : ");
				freqWriter.write(String.valueOf(influence.getInfluence()));
				freqWriter.newLine();
				freqWriter.flush();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

	public static void writeAnchorListsToFile(HashMap<String, List<String>> anchorTextsMap) {
		String pathString = Paths.get("").toAbsolutePath().toString();
		String anchorTextFileName = pathString + "/anchorTextsFile.txt";
		File anchorTextFile = new File(anchorTextFileName);
		if (!anchorTextFile.exists()) {
			try {
				anchorTextFile.createNewFile();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
		for (String urlHashcode : anchorTextsMap.keySet()) {
			try (BufferedWriter freqWriter = new BufferedWriter(new FileWriter(anchorTextFileName, true))) {
				freqWriter.write(urlHashcode + " : ");
				freqWriter.write(anchorTextsMap.get(urlHashcode).toString());
				freqWriter.newLine();
				freqWriter.flush();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

	public static void writeReduceFile(String filename, HashMap<String, List<String>> reducedMap) {
		try (BufferedWriter reduceWriter = new BufferedWriter(new FileWriter(filename, true))) {
			for (Map.Entry<String, List<String>> entry : reducedMap.entrySet()) {
				reduceWriter.write(entry.getKey() + " : " + entry.getValue());
				reduceWriter.newLine();
				reduceWriter.flush();
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
