package main; 
public class Condition {

	private String operator;
	public Variable variable1;
	public Variable variable2;
	
	public Condition condidtion1;
	public Condition conditional2;
	
	public Condition(String operator, Variable variable1, Variable variable2) throws IllegalArgumentException {
		this.operator = operator;
		if (!(operator.equals("eq")|| operator.equals("gt") || operator.equals("lt"))) {
			throw new IllegalArgumentException("Invalid operator");
		}
		if (variable1.isString() != variable2.isString()) {
			throw new IllegalArgumentException("cannot compare String and Integer");
		}
		if (variable1.isString() && (operator.equals("gt")|| operator.equals("lt"))) {
			throw new IllegalArgumentException("Invalid operator for String");
		}
		this.variable1 = variable1;
		this.variable2 = variable2;
	}
	
	public Condition(String operator, Condition condition1, Condition condition2) throws IllegalArgumentException {
		this.operator = operator;
		if (condition2 == null && !operator.equals("not")) {
			throw new IllegalArgumentException("need more then one condition for operation");
		}
		if (condition2 != null && operator.equals("not")) {
			throw new IllegalArgumentException("Can't negate two conditions");
		}
		
		this.condidtion1 = condition1;
		this.conditional2 = condition2;
	}

	@Override
	public String toString() {
		if (operator.equals("lt")) {
			return variable1 + " < " + variable2;
		} else if (operator.equals("gt")) {
			return variable1 + " > " + variable2;
		} else if (operator.equals("eq")) {
			return variable1 + " == " + variable2;
		} else if (operator.equals("or")) {
			return condidtion1 + " || " + conditional2;
		} else if (operator.equals("and")) {
			return condidtion1 + " && " + conditional2;
		} else if (operator.equals("not")) {
			return "!" + condidtion1;
		}
		return null;
	}

	public boolean hold() {
		if (variable1 != null) {
			return holdVariable();
		}
		return holdConditions();
	}
	
	private boolean holdVariable() {
		if (variable1.isString()) {
			return variable1.getValue().equals(variable2.getValue());
		}
		if (this.operator.equals("lt")) {
			return (int) (variable1.getValue()) < (int) (variable2.getValue());
		} else if (this.operator.equals("gt")) {
			return (int) (variable1.getValue()) > (int) (variable2.getValue());
		} else if (this.operator.equals("eq")) {
			return (int) (variable1.getValue()) == (int) (variable2.getValue());
		}
		return false;
	}
	
	private boolean holdConditions() {
		if (conditional2 != null) {
			switch (operator) {
				case("and"):
					return condidtion1.hold() && conditional2.hold();
				case("or"):
					return condidtion1.hold() || conditional2.hold();
			}
		}
		return !condidtion1.hold();
	}

	public void initialise(Robot robot) {
		if (variable1 != null && variable2 != null) {
			if (variable1.isRobotVariable()) {
				switch (variable1.getName()) {
					case ("barrelLR"):
						variable1.setValueInt(robot.getClosestBarrelLR());
						break;
					case ("barrelFB"):
						variable1.setValueInt(robot.getClosestBarrelFB());
						break;
					case ("fuelLeft"):
						variable1.setValueInt(robot.getFuel());
						break;
					case ("numBarrels"):
						variable1.setValueInt(robot.numBarrels());
						break;
					case ("oppLR"):
						variable1.setValueInt(robot.getOpponentLR());
						break;
					case ("oppFB"):
						variable1.setValueInt(robot.getOpponentFB());
						break;
					case ("wallDist"):
						variable1.setValueInt(robot.getDistanceToWall());
						break;
				}
			}
			if (variable2.isRobotVariable()) {
				switch (variable2.getName()) {
					case ("barrelLR"):
						variable2.setValueInt(robot.getClosestBarrelLR());
						break;
					case ("barrelFB"):
						variable2.setValueInt(robot.getClosestBarrelFB());
						break;
					case ("fuelLeft"):
						variable2.setValueInt(robot.getFuel());
						break;
					case ("numBarrels"):
						variable2.setValueInt(robot.numBarrels());
						break;
					case ("oppLR"):
						variable2.setValueInt(robot.getOpponentLR());
						break;
					case ("oppFB"):
						variable2.setValueInt(robot.getOpponentFB());
						break;
					case ("wallDist"):
						variable2.setValueInt(robot.getDistanceToWall());
						break;
				}
			}
		} else {
			condidtion1.initialise(robot);
			if (conditional2 != null) {
				conditional2.initialise(robot);
			}
		}
	}
}
