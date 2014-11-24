package classifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import sun.security.pkcs11.P11TlsKeyMaterialGenerator;

public class Classifier {

	public static Map<String, File> directories;

	static {
		directories = new HashMap<>();
		directories.put("Blog-M", new File("blogstrain/M"));
		directories.put("Blog-F", new File("blogstrain/F"));
		directories.put("BlogT-M", new File("blogstest/M"));
		directories.put("BlogT-F", new File("blogstest/F"));
		directories.put("Mail-S", new File("spammail/span"));
		directories.put("Mail-H", new File("spammail/ham"));
	}

	public static void main(String[] args) throws IOException {
		Data test = new Data(directories.get("Blog-M"));
		Data test2 = new Data(directories.get("Blog-F"));
		
		int successes = 0, failures = 0;

		for (int i = 0; i < 25; i++) {
			System.out.print(i + ": ");
			File f = directories.get("BlogT-M").listFiles()[i];
			FileInputStream fis = new FileInputStream(f);
			byte[] data = new byte[(int) f.length()];
			fis.read(data);
			fis.close();

			String message = new String(data);
			int male = (int) test.probability(tokenize(message));
			int female = (int) test2.probability(tokenize(message));
			if (male > female)
				successes++;
			else
				failures++;
			System.out.println(male > female ? "M" : "F");
		}

		for (int i = 0; i < 25; i++) {
			System.out.print(i + 25 + ": ");
			File f = directories.get("BlogT-F").listFiles()[i];
			FileInputStream fis = new FileInputStream(f);
			byte[] data = new byte[(int) f.length()];
			fis.read(data);
			fis.close();

			String message = new String(data);
			int male = (int) test.probability(tokenize(message));
			int female = (int) test2.probability(tokenize(message));
			if (male < female)
				successes++;
			else
				failures++;
			System.out.println(male > female ? "M" : "F");
		}
		
		System.out.println("SUCC: " + successes + "\nFAIL: " + failures);
		
		/*
		 * for (Entry<String, Double> ent : test.getNormalised().entrySet())
		 * System.out.println(ent);
		 * 
		 * 
		 * File f = directories.get("Blog-M").listFiles()[new
		 * Random().nextInt(300)]; FileInputStream fis = new FileInputStream(f);
		 * byte[] data = new byte[(int)f.length()]; fis.read(data); fis.close();
		 * 
		 * String message = new String(data); int p = (int)
		 * test.probability(tokenize(message)); int p2 = (int)
		 * test2.probability(tokenize(message));
		 * 
		 * System.out.println(p); System.out.println(p2); System.out.println();
		 * 
		 * BigDecimal bd1 = new BigDecimal(2); bd1 = bd1.pow(p, new
		 * MathContext(32));
		 * 
		 * BigDecimal bd2 = new BigDecimal(2); bd2 = bd2.pow(p2, new
		 * MathContext(32));
		 * 
		 * System.out.println(bd1); System.out.println(bd2);
		 */
	}

	public static List<String> tokenize(String message) {
		List<String> result = new ArrayList<>();
		for (String token : message.split("\\s+"))
			result.add(token.replaceAll("[^\\w\\d]", ""));
		return result;
	}
}
