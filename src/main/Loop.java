package main;
public class Loop extends Node {

	private CodeBlock codeBlock;
	
	public Loop(CodeBlock codeBlock) {
		this.codeBlock = codeBlock;
	}

	@Override
	public String toString() {
		String str = "loop {\n";
		for (RobotProgramNode node : this.codeBlock.getComponents()) {
			str += "	" + node.toString() + "\n";
		}
		str += "}\n";
		return str;
	}

	@Override
	public void execute(Robot r) {
		this.codeBlock.execute(r);
	}
	
	public CodeBlock getBlock() {
		return this.codeBlock;
	}

}
