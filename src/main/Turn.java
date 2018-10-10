package main;
public class Turn extends Node {
	
	private int direction;
	
	public Turn(int dir) throws IllegalArgumentException {
		this.direction = dir;
		if (!(dir == 1 || dir == -1 || Math.abs(dir) == 2)) {
			throw new IllegalArgumentException("Invalid turn angle");
		}
	}

	@Override
	public String toString() {
		if (this.direction == 1) {
			return "turn right";
		} else if (this.direction == -1) {
			return "turn left";
		}
		return "turn around";
	}

	@Override
	public void execute(Robot robot) {
		if (this.direction == 1) {
			robot.turnRight();
		} else if (this.direction == -1) {
			robot.turnLeft();
		} else if (Math.abs(this.direction) == 2) {
			robot.turnAround();
		}
	}

}
