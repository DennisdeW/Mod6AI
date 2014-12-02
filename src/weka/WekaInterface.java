package weka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesSimple;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class WekaInterface {

	public static FilteredClassifier getClassifierForFile(File arff, Classifier learner)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(arff)));
		FastVector classes = new FastVector();
		classes.addElement("Male");
		classes.addElement("Female");
		FastVector attrs = new FastVector();
		attrs.addElement(new Attribute("@@class@@", classes));
		attrs.addElement(new Attribute("content", (FastVector) null));
		Instances model = new Instances("Blogs", attrs, 0);
		buildModel(model, new File("blogstrain/M"));
		buildModel(model, new File("blogstrain/F"));

		Instances instances = new Instances(reader);
		instances.deleteAttributeAt(0);
		instances.insertAttributeAt(new Attribute("@@class@@", classes), 0);
		FilteredClassifier classifier = new FilteredClassifier();
		classifier.setClassifier(learner);
		classifier.buildClassifier(instances);
		return classifier;
	}

	private static void buildModel(Instances model, File dir) {
		for (File f : dir.listFiles()) {
			
		}
		
	}

	public static void main(String[] args) {
		try {
			FilteredClassifier fc = getClassifierForFile(new File(
					"blogs_train_generated.arff"), new NaiveBayesSimple());
			Instances test = new Instances(new BufferedReader(
					new InputStreamReader(new FileInputStream(new File(
							"blogs_test_generated.arff")))));
			for (int i = 0; i < test.numInstances(); i++) {
				Instance instance = test.instance(i);
				System.out.println(instance.toString() + ": " + fc.classifyInstance(instance));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
