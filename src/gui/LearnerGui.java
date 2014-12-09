package gui;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import weka.WekaClass;
import weka.WekaInterface;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import static weka.WekaInterface.arr;

public class LearnerGui extends JFrame {
	private static final long serialVersionUID = 2091772728630113286L;

	public static final WekaClass[] DEFAULT_CLASSES = new WekaClass[] {
			new WekaClass("Male", "M-", 1.0),
			new WekaClass("Female", "F-", 0.0) };

	public static final File[] DEFAULT_LEARN_DIRS = new File[] {
			new File("blogstrain/F"), new File("blogstrain/M") };

	public static final Classifier DEFAULT_CLASSIFIER = new J48();

	private JButton btnClassifyTrue;
	private JButton btnClassifyFalse;
	private JButton btnCompute;
	private JButton btnExit;
	private JButton btnClear;
	private JScrollPane scrollUserText;
	private JScrollPane scrollOutputText;
	private JTextArea userText;
	private JTextArea outputText;
	private Instances model;
	private FilteredClassifier classifier;
	private Map<Boolean, WekaClass> classes;

	public LearnerGui(Classifier classifier, File[] toLearn, WekaClass[] classes)
			throws Exception {
		model = WekaInterface.getInstancesFromDirs("Interactive", classes,
				arr(toLearn));
		this.classifier = WekaInterface.getClassifierFromDirs(classifier,
				classes, arr(toLearn));
		this.classes = new HashMap<>();
		this.classes.put(false, classes[0]);
		this.classes.put(true, classes[1]);
		init();
	}

	public LearnerGui() throws Exception {
		this(DEFAULT_CLASSIFIER, DEFAULT_LEARN_DIRS, DEFAULT_CLASSES);
	}

	private LearnerGui(Configuration config) throws Exception {
		Properties props = config.props;
		this.classes = new HashMap<>();
		classes.put(
				false,
				new WekaClass(props.getProperty("ClassAName"), props
						.getProperty("ClassASignifier"), 0.0));
		classes.put(
				true,
				new WekaClass(props.getProperty("ClassBName"), props
						.getProperty("ClassBSignifier"), 1.0));
		model = WekaInterface.getInstancesFromDirs(
				"Interactive",
				classes.values().toArray(new WekaClass[] {}),
				arr(new File(props.getProperty("ClassAFolder")),
						new File(props.getProperty("ClassBFolder"))));
		classifier = WekaInterface.getClassifierFromDirs(
				WekaInterface.classifierForName(props
						.getProperty("BaseClassifier")),
				classes.values().toArray(new WekaClass[] {}),
				arr(new File(props.getProperty("ClassAFolder")),
						new File(props.getProperty("ClassBFolder"))));
		init();

	}

	private void init() {
		setTitle("Interactive Learner Group 9A");
		scrollUserText = new javax.swing.JScrollPane();
		userText = new javax.swing.JTextArea();
		scrollOutputText = new javax.swing.JScrollPane();
		outputText = new javax.swing.JTextArea();
		btnClassifyTrue = new javax.swing.JButton();
		btnClassifyFalse = new javax.swing.JButton();
		btnCompute = new javax.swing.JButton();
		btnExit = new javax.swing.JButton();
		btnClear = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);

		scrollUserText
				.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		userText.setColumns(20);
		userText.setLineWrap(true);
		userText.setRows(5);
		scrollUserText.setViewportView(userText);

		scrollOutputText
				.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollOutputText
				.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		outputText.setEditable(false);
		outputText.setColumns(20);
		outputText.setRows(5);
		scrollOutputText.setViewportView(outputText);

		btnClassifyTrue.setText("Classify as " + classes.get(true).getName());
		btnClassifyTrue.setPreferredSize(new Dimension(btnClassifyTrue.getText().length() * 20, 50));
		btnClassifyTrue.addActionListener((e) -> {
			try {
				processClassify(userText.getText(), true);
			} catch (Exception e1) {
				outputText.setText("An error occurred!\n" + e1);
				e1.printStackTrace();
			}
		});

		btnClassifyFalse.setText("Classify as " + classes.get(false).getName());
		btnClassifyFalse.setPreferredSize(new Dimension(btnClassifyFalse.getText().length() * 20, 50));
		btnClassifyFalse.addActionListener((e) -> {
			try {
				processClassify(userText.getText(), false);
			} catch (Exception e1) {
				outputText.setText("An error occurred!\n" + e1);
				e1.printStackTrace();
			}
		});

		btnCompute.setText("Compute Classification");
		btnCompute.addActionListener((e) -> {
			try {
				classifyInput(userText.getText());
			} catch (Exception e1) {
				outputText.setText("An error occurred!\n" + e1);
				e1.printStackTrace();
			}
		});

		btnExit.setText("Exit");
		btnExit.addActionListener((e) -> System.exit(0));

		btnClear.setText("Clear Text");
		btnClear.addActionListener((e) -> {
			userText.setText("");
			outputText.setText("");
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(scrollUserText)
												.addComponent(scrollOutputText)
												.addGroup(
														layout.createSequentialGroup()
																.addGap(10, 10,
																		10)
																.addComponent(
																		btnCompute)
																.addGap(21, 21,
																		21)
																.addComponent(
																		btnClassifyFalse,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		-2,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addGap(35, 35,
																		35)
																.addComponent(
																		btnClassifyTrue, -2, -2, -2)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		233,
																		Short.MAX_VALUE)
																.addComponent(
																		btnClear)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		btnExit)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)
								.addComponent(scrollUserText,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										386,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(scrollOutputText,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										44,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(btnClassifyTrue)
												.addComponent(btnClassifyFalse)
												.addComponent(btnCompute)
												.addComponent(btnExit)
												.addComponent(btnClear))
								.addGap(20, 20, 20)));

		pack();
	}

	private void processClassify(String text, boolean type) throws Exception {
		outputText.setText("Rebuilding...");
		this.invalidate();
		WekaInterface.addToInstances(text, classes.get(type), model);
		classifier.buildClassifier(model);
		outputText.setText("Input included in new model!");
	}

	private void classifyInput(String text) throws Exception {
		if (text == null || text.equals(""))
			return;
		/*
		 * Set<Result> results = WekaInterface.classify(text, classifier,
		 * classes .values().toArray(new WekaClass[] {})); WekaClass assigned =
		 * results .stream() .findAny() .orElseThrow( () -> new
		 * RuntimeException( "classify returned empty result!"))
		 * .getAssignedClass();
		 */
		double[] data = new double[2];
		data[0] = model.attribute(0).addStringValue(text);
		data[1] = 0.5;
		Instance instance = new Instance(1.0, data);
		instance.setDataset(model);
		double r = classifier.classifyInstance(instance);
		WekaClass assigned = getBestMatch(r);
		outputText.setText("Input was classified as " + assigned.getName());
	}

	private WekaClass getBestMatch(double r) {
		double minDiff = Double.MAX_VALUE;
		WekaClass minClass = null;
		double diff;
		for (WekaClass currClass : this.classes.values())
			if ((diff = Math.abs(r - currClass.getValue())) < minDiff) {
				minDiff = diff;
				minClass = currClass;
			}
		return minClass;
	}

	public static void main(String[] args) {
		Splash splash = new Splash();
		splash.setAlwaysOnTop(true);
		splash.setVisible(true);
		LearnerGui gui;
		boolean hideSplash = true;
		try {
			gui = new LearnerGui(Configuration.readConfig());
			gui.setVisible(true);
		} catch (Exception e) {
			splash.changeText("An error occurred during loading!");
			e.printStackTrace();
			hideSplash = false;
		}
		if (hideSplash) {
			splash.setVisible(false);
			splash = null;
		}
	}

	private static class Configuration {
		private Properties props;

		private Configuration(File xml) throws SAXException, IOException,
				ParserConfigurationException {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xml);

			Element root = (Element) doc.getElementsByTagName("LearnerConfig")
					.item(0);
			props = new Properties();

			Element classA = (Element) doc.getElementsByTagName("ClassA").item(
					0);
			props.put("ClassAName", classA.getTextContent());
			props.put("ClassAFolder", classA.getAttribute("TrainFolder"));
			props.put("ClassASignifier", classA.getAttribute("TrainSignifier"));

			Element classB = (Element) doc.getElementsByTagName("ClassB").item(
					0);
			props.put("ClassBName", classB.getTextContent());
			props.put("ClassBFolder", classB.getAttribute("TrainFolder"));
			props.put("ClassBSignifier", classB.getAttribute("TrainSignifier"));

			Element baseClassifier = (Element) doc.getElementsByTagName(
					"BaseClassifier").item(0);
			props.put("BaseClassifier", baseClassifier.getTextContent());
		}

		private static Configuration readConfig() {
			File xml = new File("config.xml");
			try {
				return new Configuration(xml);
			} catch (SAXException | IOException | ParserConfigurationException e) {
				throw new ConfigurationException(e);
			}
		}
	}

	public static class ConfigurationException extends RuntimeException {

		private static final long serialVersionUID = 2494413510746315900L;

		public ConfigurationException() {
			super();
		}

		public ConfigurationException(String msg) {
			super(msg);
		}

		public ConfigurationException(Throwable cause) {
			super();
			this.initCause(cause);
		}

	}
}