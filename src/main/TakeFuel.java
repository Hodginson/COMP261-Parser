package main;
public class TakeFuel extends Node {

	public TakeFuel() {

	}

	@Override
	public String toString() {
		return "take fuel";
	}

	@Override
	public void execute(Robot r) {
		r.takeFuel();
	}

}
