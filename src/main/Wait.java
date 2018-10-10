package main;
public class Wait extends Node {

	private int milliseconds;

	public Wait() {

	}

	public Wait(int milliseconds) {
		this.milliseconds = milliseconds;
	}

	@Override
	public String toString() {
		if (milliseconds != 0) {
			return "wait for " + milliseconds;
		}
		return "wait";
	}

	@Override
	public void execute(Robot robot) {
		for (int i=0; i<milliseconds; i++) {
			robot.idleWait();
		}
	}

}
