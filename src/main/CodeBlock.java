package main;

import java.util.List;

public class CodeBlock extends Node {

	private List<RobotProgramNode> components;
	
	public CodeBlock(List<RobotProgramNode> codeBlock) {
		this.components = codeBlock;
		for (int i=0; i<this.components.size(); i++) {
			if (this.components.get(i) == null) {
				this.components.remove(i);
			}
		}
	}

	@Override
	public String toString() {
		String str = "";
		for (RobotProgramNode node : this.components) {
			str = str + node.toString() + "\n";
		}
		return str;
	}
	
	public List<RobotProgramNode> getComponents() {
		return this.components;
	}

	@Override
	public void execute(Robot r) {
		for (RobotProgramNode node : this.components) {
			node.execute(r);
		}
	}


}
