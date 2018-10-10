package main;
public class Move extends Node {
	
	int x;

	public Move(int x) {
		this.x = x;
	}

	@Override
	public String toString() {
		return "move";
	}

	@Override
	public void execute(Robot r) {
		for (int i=0; i<x; i++) {
			r.move();
		}
	}

}
