package main;
public class Variable {
	
	private String name;
	private String valueString;
	private String valueInt;
	private boolean robotVariable = false;
	
	private String operator;
	private Variable variable1;
	private Variable variable2;
	
	public Variable(String name, String value) {
		this.name = name;
		this.valueString = value;
	}
	
	public Variable(String name, int value, boolean robotVar) {
		this.name = name;
		this.valueInt = value + "";
		this.robotVariable = robotVar;
	}
	
	public Variable(String operator, Variable variable1, Variable variable2) {
		this.operator = operator;
		this.variable1 = variable1;
		this.variable2 = variable2;
	}

	@Override
	public String toString() {
		if (this.variable1 != null) {
			switch (this.operator) {
	            case ("add"):
	                return "(" + variable1.toString() + " + " + variable2.toString() + ")";
	            case ("sub"):
	                return "(" + variable1.toString() + " - " + variable2.toString() + ")";
	            case ("div"):
	                return "(" + variable1.toString() + " / " + variable2.toString() + ")";
	            case ("mul"):
	                return "(" + variable1.toString() + " * " + variable2.toString() + ")";
	            
	        }
		}
		return name;
	}

	public boolean isString() {
		return this.valueString != null;
	}
	
	public boolean isRobotVariable() {
		return this.robotVariable;
	}
	
	public Object getValue() throws NullPointerException {
		if (this.variable1 != null && this.variable2 != null) {
			return this.evaluate();
		}
		if (this.valueString == null) {
			return Integer.parseInt(this.valueInt);
		} else if (this.valueInt == null) {
			return this.valueString.toString();
		}
		throw new NullPointerException("Variable has no value");
	}
	
	public void setValueString(String s) throws IllegalArgumentException {
		if (valueString == null && valueInt != null) {
			throw new IllegalArgumentException("Invalid variable type: expected Integer");
		}
		this.valueString = s;
	}
	
	public void setValueInt(int i) throws IllegalArgumentException {
		if (valueString != null && valueInt == null) {
			throw new IllegalArgumentException("Invalid variable type: expected String");
		}
		this.valueInt = i + "";
	}

	public String getName() {
		return this.name;
	}
	
	private int evaluate() {
		switch(this.operator) {
			case("add"):
				return (int) (variable1.getValue()) + (int) (variable2.getValue());
			case("sub"):
				return (int) (variable1.getValue()) - (int) (variable2.getValue());
			case("div"):
				return (int) (variable1.getValue()) / (int) (variable2.getValue());
			case("mul"):
				return (int) (variable1.getValue()) * (int) (variable2.getValue());

		}
		return 0;
	}
	
}
