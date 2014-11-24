package classifier;

import java.io.File;
import java.io.FileInputStream;
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

	public Data(File dir) {
		this.dir = dir;
		data = new HashSet<>();
	}

	public void tokenize() {
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
					// Normalise by removing non-word and non-digit characters,
					// and by converting to lower case
					normalised.add(s.replaceAll("[^\\w\\d]", "").toLowerCase());

				// add to the set of normalised tokens
				data.add(normalised);
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public Map<String, Integer> getCounts() {
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
	
	public long totalCount() {
		long res = 0;
		for (List<String> tokens : data)
			res += tokens.size();
		return res;
	}
	
	public Map<String, Double> getNormalised() {
		Map<String, Integer> counts = getCounts();
		Map<String, Double> normalised = new HashMap<>();
		long total = totalCount();
		
		for (Entry<String, Integer> e : counts.entrySet()) {
			//Normalised value = value for this token / total value
			normalised.put(e.getKey(), ((double) e.getValue()) / ((double) total));
		}
		
		return normalised;
	}
}
