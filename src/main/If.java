package main;
import java.util.ArrayList;
import java.util.List;

public class If extends Node {

	private CodeBlock codeBlock;
	private Condition condition;
	public List<If> elseList;

	public If(CodeBlock codeBlock, Condition condition) {
		this.codeBlock = codeBlock;
		this.condition = condition;
		this.elseList = new ArrayList<>();
	}

	@Override
	public String toString() {
		String str = "";
		if (this.condition == null) {
			str = "else {\n";
		} else {
			str = "if (" + this.condition + ") {\n";
		}
		
		for (RobotProgramNode node : this.codeBlock.getComponents()) {
			str += node.toString() + "\n";
		}
		
		if (this.elseList.isEmpty()) {
			str += "}\n";
		} else {
			str += "} ";
			for (int i=0; i<this.elseList.size(); i++) {
				str += this.elseList.get(i).toString();
			}
		}
		return str;
	}

	@Override
	public void execute(Robot robot) {
		if (this.condition != null) {
			this.condition.initialise(robot);
			if (this.condition.hold()) {
				for (RobotProgramNode node : this.codeBlock.getComponents()) {
					node.execute(robot);
				}
			} else {
				for (If n : elseList) {
					for (RobotProgramNode node2 : n.codeBlock.getComponents()) {
						node2.execute(robot);
					}
				}
			}
		} else {
			for (RobotProgramNode node : this.codeBlock.getComponents()) {
				node.execute(robot);
			}
		}
	}

}
