package main;
public class While extends Node implements RobotProgramNode {

	private CodeBlock codeBlock;
	private Condition condition;
	
	public While(CodeBlock codeBlock, Condition condition) {
		this.codeBlock = codeBlock;
		this.condition = condition;
	}
	
	@Override
	public String toString() {
		String str = "while (" + this.condition + ") {\n";
		for (RobotProgramNode node : this.codeBlock.getComponents()) {
			str += node.toString() + "\n";
		}
		str += "}\n";
		return str;
	}
	
	@Override
	public void execute(Robot robot) {
		this.condition.initialise(robot);
		while (this.condition.hold()) {
			this.condition.initialise(robot);
			for (RobotProgramNode node : this.codeBlock.getComponents()) {
				node.execute(robot);
			}
		}
	}

}
