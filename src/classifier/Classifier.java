package classifier;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Classifier {

	public static Map<String, File> directories;
		
	
	static {
		directories = new HashMap<>();
		directories.put("Blog-M", new File("blogstrain/M"));
		directories.put("Blog-F", new File("blogstrain/F"));
		directories.put("Mail-S", new File("spammail/span"));
		directories.put("Mail-H", new File("spammail/ham"));
	}
	
	public static void main(String[] args) {
		
	}
}
