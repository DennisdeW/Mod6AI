package weka;

public class WekaClass {
	private String name, signifier;
	private double value;
	
	public WekaClass(String name, String signifier, double value) {
		this.name = name;
		this.setSignifier(signifier);
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSignifier() {
		return signifier;
	}

	public void setSignifier(String signifier) {
		this.signifier = signifier;
	}
}
