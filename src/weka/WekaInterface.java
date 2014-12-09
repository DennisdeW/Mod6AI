package weka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesSimple;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.NBTree;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class WekaInterface {

	public static final WekaClass[] MALE_FEMALE = new WekaClass[] {
			new WekaClass("Male", "M-", 1.0),
			new WekaClass("Female", "F-", 0.0) };

	public static final WekaClass[] SPAM_HAM = new WekaClass[] {
			new WekaClass("Spam", "spam", 1.0),
			new WekaClass("Ham", "msg", 0.0) };

	public static FilteredClassifier getClassifierFromDirs(Classifier learner,
			WekaClass[] classes, File[] folders) throws Exception {
		FastVector classVec = new FastVector();
		for (WekaClass wc : classes)
			classVec.addElement(wc.getName());
		FastVector attrs = new FastVector();
		attrs.addElement(new Attribute("content", (FastVector) null));
		attrs.addElement(new Attribute("@@class@@", classVec));
		Instances model = new Instances("Blogs", attrs, 0);
		for (File dir : folders)
			buildModel(model, classes, dir);
		model.setClassIndex(model.numAttributes() - 1);

		File file = new File("out.arff");
		PrintWriter out = new PrintWriter(file, "UTF-8");
		out.print(model);
		out.flush();
		out.close();

		StringToWordVector filter = new StringToWordVector();
		filter.setAttributeIndices("first");
		// filter.setInputFormat(model);
		// Instances filtered = Filter.useFilter(model, filter);
		// filtered.setClassIndex(0);

		FilteredClassifier classifier = new FilteredClassifier();
		classifier.setFilter(filter);
		classifier.setClassifier(learner);
		classifier.buildClassifier(model);
		return classifier;
	}

	public static Map<String, FilteredClassifier> getClassifiersFromDirs(
			String[] learners, WekaClass[] classes, File[] folders)
			throws Exception {
		Map<String, FilteredClassifier> map = new HashMap<>();
		for (String s : learners) {
			map.put(s,
					getClassifierFromDirs(Classifier.forName(s, null), classes,
							folders));
		}
		return map;
	}

	private static void buildModel(Instances model, WekaClass[] classes,
			File dir) throws IOException {

		for (File f : dir.listFiles()) {
			Scanner scanner = new Scanner(f);

			if (scanner.hasNext()) {

				WekaClass wc = null;
				for (int i = 0; i < classes.length; i++)
					if (f.getName().contains(classes[i].getSignifier())) {
						wc = classes[i];
						break;
					}
				if (wc == null)
					continue;
				double[] value = new double[model.numAttributes()];
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(f)));
				String text = scanner.useDelimiter("\\Z").next();
				// System.out.println(text);
				value[0] = model.attribute(0).addStringValue(text);
				reader.close();
				value[1] = wc.getValue();
				model.add(new Instance(1.0, value));

			}
			scanner.close();
		}

	}
	
	public static void addToInstances(String text, WekaClass type, Instances data) {
		double[] toAdd = new double[data.numAttributes()];
		toAdd[0] = data.attribute(0).addStringValue(text);
		toAdd[1] = type.getValue();
		data.add(new Instance(1.0, toAdd));
	}

	public static Instances getInstancesFromDirs(String name,
			WekaClass[] classes, File[] folders) throws IOException {
		FastVector classVec = new FastVector();
		for (WekaClass wc : classes)
			classVec.addElement(wc.getName());
		FastVector attrs = new FastVector();
		attrs.addElement(new Attribute("content", (FastVector) null));
		attrs.addElement(new Attribute("@@class@@", classVec));
		Instances res = new Instances(name, attrs, 0);
		for (File dir : folders)
			buildModel(res, classes, dir);
		res.setClassIndex(res.numAttributes() - 1);
		return res;
	}

	public static Set<Result> classify(File folder, Classifier c,
			WekaClass[] classes) throws Exception {
		Set<Result> results = new HashSet<>();
		Instances data = getInstancesFromDirs("internal", classes, arr(folder));
		for (int i = 0; i < data.numInstances(); i++) {
			double val = c.classifyInstance(data.instance(i));
			Result result = new Result(data.instance(i).toString(0), val,
					classes);
			results.add(result);
		}
		return results;
	}

	public static Set<Result> classify(File[] folders, Classifier c,
			WekaClass[] classes) throws Exception {
		Set<Result> results = new HashSet<>();
		for (File dir : folders)
			results.addAll(classify(dir, c, classes));
		return results;
	}

	public static Set<Result> classify(String data, Classifier c,
			WekaClass[] classes) throws Exception {
		File dir = null, f = null;
		try {
			dir = new File("tmp");
			dir.mkdir();
			f = new File("tmp/tmp");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(data.getBytes());
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Set<Result> res = classify(dir, c, classes);
		f.delete();
		dir.delete();
		return res;
	}

	public static Set<Result> classify(String[] data, Classifier c,
			WekaClass[] classes) throws Exception {
		Set<Result> res = new HashSet<>();
		for (String s : data)
			res.addAll(classify(s, c, classes));
		return res;
	}

	@SafeVarargs
	public static <T> T[] arr(T... ts) {
		return ts;
	}

	public static void main(String[] args) {
		try {
			WekaClass[] classes = new WekaClass[2];
			classes[0] = new WekaClass("Male", "M-", 1.0);
			classes[1] = new WekaClass("Female", "F-", 0.0);
			FilteredClassifier fc = getClassifierFromDirs(new J48(), classes,
					arr(new File("blogstrain/F"), new File("blogstrain/M")));
			Set<Result> set = classify(new File("blogstest/F"), fc, classes);
			System.out.println(set);
			/*
			 * Instances test = getInstancesFromDirs("Test", classes, new File[]
			 * { new File("blogstest/F") }); int successes = 0; for (int i = 0;
			 * i < test.numInstances(); i++) { Instance instance =
			 * test.instance(i); boolean correct = fc.classifyInstance(instance)
			 * == 0.0; if (correct) successes++; System.out.println(correct); }
			 * System.out.println(successes + "/25");
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Classifier classifierForName(String name) {
		switch (name) {
		case "J48":
			return new J48();
		case "NaiveBayes":
			return new NaiveBayes();
		case "NBTree":
			return new NBTree();
			default:
			try {
				return (Classifier) Class.forName(name).newInstance();
			} catch (Throwable t) {
				throw new gui.LearnerGui.ConfigurationException(t);
			}
		}
	}
}
