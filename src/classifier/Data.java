package classifier;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Data {

	private Set<List<String>> data;
	private File dir;
	private static final double K = 10;
	private static final double LN2 = 1 / Math.log(2);
	private static final int MIN_COUNT = 5;

	public Data(File dir) {
		// System.out.println("Data.Data()");
		this.dir = dir;
		data = new HashSet<>();
		tokenize();
	}

	public void tokenize() {
		// System.out.println("Data.tokenize()");
		// Get a list of all training files
		List<File> files = Arrays.asList(dir.listFiles());
		for (File f : files)
			try {
				// Read the file to a string
				FileInputStream fis = new FileInputStream(f);
				byte[] raw = new byte[(int) f.length()];
				fis.read(raw);
				fis.close();
				String fullFile = new String(raw);

				// Split at whitespace
				List<String> tokens = Arrays.asList(fullFile.split("\\s+"));

				List<String> normalised = new ArrayList<>();
				for (String s : tokens)
					// Normalise by removing non-word characters,
					// and by converting to lower case
					normalised.add(s.replaceAll("[^\\w\\d]", ""));

				// add to the set of normalised tokens
				data.add(normalised);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public Map<String, Integer> getCounts() {
		// System.out.println("Data.getCounts()");
		Map<String, Integer> counts = new HashMap<>();
		for (List<String> tokens : data) {
			for (String token : tokens) {
				// If there is already a count for this token, increment it by
				// one. Otherwise, set it to 1.
				counts.put(token,
						counts.containsKey(token) ? counts.get(token) + 1 : 1);
			}
		}
		return counts;
	}

	public long totalWords() {
		// System.out.println("Data.totalWords()");
		long res = 0;
		for (Integer i : getCounts().values())
			res += i;
		return res;
	}

	public long vocabularySize() {
		// System.out.println("Data.vocabularySize()");
		return getCounts().size();
	}

	public Map<String, Double> getNormalised() {
		// System.out.println("Data.getNormalised()");
		Map<String, Integer> counts = getCounts();
		Map<String, Double> normalised = new HashMap<>();
		long total = totalWords();
		long vocSize = vocabularySize();

		for (Entry<String, Integer> e : counts.entrySet()) {
			if (e.getValue() < MIN_COUNT)
				continue;
			// Normalised value = (C(w) + K) / (N + K * V)
			normalised.put(e.getKey(), log2(((double) e.getValue() + K)
					/ ((double) (total + K * vocSize))));
		}

		return normalised;
	}

	public double probability(List<String> tokens) {
		// System.out.println("Data.probability()");
		double res = 1.0;
		Map<String, Double> scores = getNormalised();
		double defScore = log2(K
				/ (double) (totalWords() + K * vocabularySize()));
		for (String token : tokens) {
			if (scores.containsKey(token))
				res += scores.get(token);
			else
				res += defScore;
		}
		return res;
	}
	
	public static double log2 (double in) {
		return Math.log(in) * LN2;
	}
}

