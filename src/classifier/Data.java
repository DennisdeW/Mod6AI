package classifier;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Data {

	private Set<List<String>> data;
	private File dir;

	public Data(File dir) {
		this.dir = dir;
		data = new HashSet<>();
	}

	public void tokenize() {
		List<File> files = Arrays.asList(dir.listFiles());
		for (File f : files)
			try {
				FileInputStream fis = new FileInputStream(f);
				byte[] raw = new byte[(int) f.length()];
				fis.read(raw);
				fis.close();
				String fullFile = new String(raw);
				List<String> tokens = Arrays.asList(fullFile.split("\\s+"));
				List<String> normalised = new ArrayList<>();
				for (String s : tokens)
					normalised.add(s.replaceAll("[^\\w\\d]", "").toLowerCase());
				data.add(normalised);
			} catch (Exception e) {
				e.printStackTrace();
			}

	}
}
