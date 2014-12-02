package weka;

public class Result {
	private String input;
	private WekaClass assignedClass;
	private double value;

	public Result(String input, double value, WekaClass[] classes) {
		this.setInput(input);
		this.setValue(value);
		this.setAssignedClass(getBestMatch(value, classes));
	}

	private static WekaClass getBestMatch(double value, WekaClass[] classes) {
		double minDeviation = Double.MAX_VALUE;
		WekaClass selected = null;
		for (WekaClass wc : classes) {
			double deviation = Math.abs(value - wc.getValue());
			if (deviation < minDeviation) {
				minDeviation = deviation;
				selected = wc;
			}
		}
		return selected;
	}

	public WekaClass getAssignedClass() {
		return assignedClass;
	}

	public void setAssignedClass(WekaClass assignedClass) {
		this.assignedClass = assignedClass;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String toString() {
		return (input.length() > 20 ? input.substring(0, 20) + "..." : input)
				+ ": " + assignedClass.getName() + " (" + value + ")\n";
	}
}
