package main;
public class Shield extends Node {

	private boolean shieldActive;
	
	public Shield(boolean shieldActive) {
		this.shieldActive = shieldActive;
	}

	@Override
	public String toString() {
		if (this.shieldActive) {
			return "shield turned on";
		}
		return "shield turned off";
	}

	@Override
	public void execute(Robot r) {
		r.setShield(shieldActive);
	}

}
