package main;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.*;
import javax.swing.JFileChooser;


/**
 * The parser and interpreter. The top level parse function, a main method for
 * testing, and several utility methods are provided. You need to implement
 * parseProgram and all the rest of the parser.
 */
public class Parser {

	/**
	 * Top level parse method, called by the World
	 */
	static RobotProgramNode parseFile(File code) {
		Scanner scan = null;
		try {
			scan = new Scanner(code);

			// the only time tokens can be next to each other is
			// when one of them is one of (){},;
			scan.useDelimiter("\\s+|(?=[{}(),;])|(?<=[{}(),;])");

			RobotProgramNode n = parseProgram(scan); // You need to implement this!!!

			scan.close();
			return n;
		} catch (FileNotFoundException e) {
			System.out.println("Robot program source file not found");
		} catch (ParserFailureException e) {
			System.out.println("Parser error:");
			System.out.println(e.getMessage());
			scan.close();
		}
		return null;
	}

	/** For testing the parser without requiring the world */

	public static void main(String[] args) {
		if (args.length > 0) {
			for (String arg : args) {
				File f = new File(arg);
				if (f.exists()) {
					System.out.println("Parsing '" + f + "'");
					RobotProgramNode prog = parseFile(f);
					System.out.println("Parsing completed ");
					if (prog != null) {
						System.out.println("================\nProgram:");
						System.out.println(prog);
					}
					System.out.println("=================");
				} else {
					System.out.println("Can't find file '" + f + "'");
				}
			}
		} else {
			while (true) {
				JFileChooser chooser = new JFileChooser(".");// System.getProperty("user.dir"));
				int res = chooser.showOpenDialog(null);
				if (res != JFileChooser.APPROVE_OPTION) {
					break;
				}
				RobotProgramNode prog = parseFile(chooser.getSelectedFile());
				System.out.println("Parsing completed");
				if (prog != null) {
					System.out.println("Program: \n" + prog);
				}
				System.out.println("=================");
			}
		}
		System.out.println("Done");
	}

	// Useful Patterns

	static Pattern NUMPAT = Pattern.compile("-?\\d+"); // ("-?(0|[1-9][0-9]*)");
	static Pattern OPENPAREN = Pattern.compile("\\(");
	static Pattern CLOSEPAREN = Pattern.compile("\\)");
	static Pattern OPENBRACE = Pattern.compile("\\{");
	static Pattern CLOSEBRACE = Pattern.compile("\\}");

	/**
	 * PROG ::= STMT+
	 */
	static RobotProgramNode parseProgram(Scanner s) {
		List<RobotProgramNode> statement = new ArrayList<>();
		if (!s.hasNext()) {
			return null;
		}
		while (s.hasNext()) {
			statement.add(parse(s));
		}
		return new CodeBlock(statement);
	}

	private static RobotProgramNode parse(Scanner scan) {
		String statement = scan.next();
		switch (statement) {
			case ("if"):
				return ifParser(scan, null, true);
			case("while"):
				return whileParser(scan);
			case ("loop"):
				return loopParser(scan);
		}
		return actionParser(statement, scan);
	}
	
	private static If ifParser(Scanner scan, If parentIf, boolean hasCond) {
		List<RobotProgramNode> codeBlock = new ArrayList<>();
		Condition condition = null;
		if (hasCond) {
			if (!checkFor(OPENPAREN, scan)) {
				fail("Expected a open parenthesis", scan);
			}
			condition = conditionParser(scan);
			if (!checkFor(CLOSEPAREN, scan)) {
				fail("Expected a closed parenthesis", scan);
			}
		}
		if (!checkFor(OPENBRACE, scan)) {
			fail("Expected a open brace", scan);
		}
		while (!checkFor(CLOSEBRACE, scan)) {
			codeBlock.add(parse(scan));
		}
		if (codeBlock.size() <= 0) {
			fail("need at least one statement", scan);
		}
		If node = new If(new CodeBlock(codeBlock), condition);
		if (scan.hasNext("else if")) {
			scan.next();
			if (parentIf == null) {
				node.elseList.add(ifParser(scan, node, true));
			} else {
				parentIf.elseList.add(ifParser(scan, parentIf, true));
			}
		} else if (scan.hasNext("else")) {
			node.elseList.add(elseParser(scan));
		}
		return node;
	}
	
	private static If elseParser(Scanner scan) {
		List<RobotProgramNode> codeBlock = new ArrayList<>();
		scan.next();
		
		if (!checkFor(OPENBRACE, scan)) {
			fail("Expected a open brace", scan);
		}
		while (!checkFor(CLOSEBRACE, scan)) {
			codeBlock.add(parse(scan));
		}
		if (codeBlock.size() <= 0) {
			fail("need at least one statement", scan);
		}
		return new If(new CodeBlock(codeBlock), null);
	}
	
	private static Condition conditionParser(Scanner scan) {
		String operator = scan.next();
		if (!checkFor(OPENPAREN, scan)) {
			fail("Expected a open parenthesis", scan);
		}
		switch(operator) {
			case("eq"):
				break;
			case("gt"):
				break;
			case("lt"):
				break;
			case("not"):
				Condition condition = new Condition(operator, conditionParser(scan), null);
				if (!checkFor(CLOSEPAREN, scan)) {
					fail("Expected a closed parenthesis", scan);
				}
				return condition;
			default:
				Condition condition1 = conditionParser(scan);
				if (!checkFor(",", scan)) {
					fail("Expected a comma", scan);
				}
				Condition condition2 = conditionParser(scan);
				if (!checkFor(CLOSEPAREN, scan)) {
					fail("Expected a closed parenthesis", scan);
				}
				return new Condition(operator, condition1, condition2);
		}
		Variable variable1 = variableParser(scan.next(), scan);
		if (!checkFor(",", scan)) {
			fail("Expected a comma", scan);
		}
		Variable variable2 = variableParser(scan.next(), scan);
		if (!checkFor(CLOSEPAREN, scan)) {
			fail("Expected a closed parenthesis", scan);
		}
		if (variable1.isRobotVariable() && variable2.isRobotVariable()) {
			fail("Cannot compare two robot variables", scan);
		}
		if (variable1.toString().equals(variable2.toString())) {
			fail("Condition is never false", scan);
		}
		return new Condition(operator, variable1, variable2);
	}
	
	private static Variable variableParser(String str, Scanner scan) {
		switch (str) {
			case ("barrelLR"):
				return new Variable("barrelLR", 0, true);
			case ("barrelFB"):
				return new Variable("barrelFB", 0, true);
			case ("fuelLeft"):
				return new Variable("fuelLeft", 0, true);
			case ("numBarrels"):
				return new Variable("numBarrels", 0, true);
			case ("oppLR"):
				return new Variable("oppLR", 0, true);
			case ("oppFB"):
				return new Variable("oppFB", 0, true);
			case ("wallDist"):
				return new Variable("wallDist", 0, true);
			default:
				if (isInteger(str)) {
					return new Variable(str, Integer.parseInt(str), false);
				}
				if (str.equals("add") || str.equals("sub")|| str.equals("mul")|| str.equals("div")) {
					if (!checkFor(OPENPAREN, scan)) {
						fail("need arguments", scan);
					}
					Variable variable1 = variableParser(scan.next(), scan);
					if (!checkFor(",", scan)) {
						fail("need two arguments", scan);
					}
					Variable variable2 = variableParser(scan.next(), scan);
					
					Variable variable = new Variable(str, variable1, variable2);
					if (!checkFor(CLOSEPAREN, scan)) {
						fail("need a closing parenthesis", scan);
					}
					return variable;
				}
		}
		fail("Invalid variable", scan);
		return null;
	}

	private static Variable expressionParser(Scanner scan) {
		String str = scan.next();
		switch(str) {
			case("add"):
				break;
			case("sub"):
				break;
			case("div"):
				break;
			case("mul"):
				break;
			default:
				return variableParser(str, scan);
		}
		
		if (!checkFor(OPENPAREN, scan)) {
			fail("Expression needs arguments", scan);
		}
		Variable variable1 = expressionParser(scan);
		if (!checkFor(",", scan)) {
			fail("need comma", scan);
		}
		Variable variable2 = expressionParser(scan);
		if (!checkFor(CLOSEPAREN, scan)) {
			fail("need a closed parenthesis", scan);
		}
		return new Variable(str, variable1, variable2);
	}

	private static RobotProgramNode whileParser(Scanner scan) {
		List<RobotProgramNode> codeBlock = new ArrayList<>();

		if (!checkFor(OPENPAREN, scan)) {
			fail("Expected a open parenthesis", scan);
		}
		Condition condition = conditionParser(scan);
		if (!checkFor(CLOSEPAREN, scan)) {
			fail("Expected a closed parenthesis", scan);
		}
		if (!checkFor(OPENBRACE, scan)) {
			fail("Expected a open brace", scan);
		}
		while (!checkFor(CLOSEBRACE, scan)) {
			codeBlock.add(parse(scan));
		}
		if (codeBlock.size() <= 0) {
			fail("needs at least one statement", scan);
		}
		if (condition.variable1 != null) {
			if (condition.variable1.getName().equals(condition.variable2.getName())) {
				fail("Infinite loop found", scan);
			}
		}
		return new While(new CodeBlock(codeBlock), condition);
	}
	
	private static RobotProgramNode loopParser(Scanner scan) {
		List<RobotProgramNode> codeBlock = new ArrayList<>();
		scan.next();
		while (!checkFor(CLOSEBRACE, scan)) {
			codeBlock.add(parse(scan));
		}
		if (codeBlock.size() <= 0) {
			fail("need at least one statement", scan);
		}
		return new Loop(new CodeBlock(codeBlock));
	}
	
	static RobotProgramNode actionParser(String str, Scanner scan) {
		RobotProgramNode robotNode = null;
		switch (str) {
			case ("move"):
				robotNode = moveParser(scan);
				break;
			case ("shieldOn"):
				robotNode = new Shield(true);
				break;
			case("shieldOff"):
				robotNode = new Shield(false);
				break;
			case ("takeFuel"):
				robotNode = new TakeFuel();
				break;
			case ("turnL"):
				robotNode = new Turn(-1);
				break;
			case ("turnR"):
				robotNode = new Turn(1);
				break;
			case("turnAround"):
				robotNode = new Turn(2);
				break;
			case ("wait"):
				robotNode = waitParser(scan);
				break;
			default:
				fail("could not find command: " + str, scan);
				break;
		}
		require(";", "could not find a semicolon", scan);
		return robotNode;
	}
	
	private static RobotProgramNode waitParser(Scanner scan) {
		if (checkFor(OPENPAREN, scan)) {
			int x = (int) expressionParser(scan).getValue();
			Wait wait = new Wait(x);
			if (!checkFor(CLOSEPAREN, scan)) {
				fail("need a close parenthesis", scan);
			}
			return wait;
		}
		return new Wait(1);
	}
	
	private static RobotProgramNode moveParser(Scanner scan) {
		if (checkFor(OPENPAREN, scan)) {
			int x = (int) expressionParser(scan).getValue();
			Move move = new Move(Math.max(x, 1));
			if (!checkFor(CLOSEPAREN, scan)) {
				fail("need a close parenthesis", scan);
			}
			return move;
		}
		return new Move(1);
	}
	
	public static boolean isInteger(String str) { //Not my code - got from http://stackoverflow.com/questions/237159/whats-the-best-way-to-check-to-see-if-a-string-represents-an-integer-in-java
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	// utility methods for the parser

	/**
	 * Report a failure in the parser.
	 */
	static void fail(String message, Scanner s) {
		String msg = message + "\n   @ ...";
		for (int i = 0; i < 5 && s.hasNext(); i++) {
			msg += " " + s.next();
		}
		throw new ParserFailureException(msg + "...");
	}

	/**
	 * Requires that the next token matches a pattern if it matches, it consumes
	 * and returns the token, if not, it throws an exception with an error
	 * message
	 */
	static String require(String p, String message, Scanner s) {
		if (s.hasNext(p)) {
			return s.next();
		}
		fail(message, s);
		return null;
	}

	static String require(Pattern p, String message, Scanner s) {
		if (s.hasNext(p)) {
			return s.next();
		}
		fail(message, s);
		return null;
	}

	/**
	 * Requires that the next token matches a pattern (which should only match a
	 * number) if it matches, it consumes and returns the token as an integer if
	 * not, it throws an exception with an error message
	 */
	static int requireInt(String p, String message, Scanner s) {
		if (s.hasNext(p) && s.hasNextInt()) {
			return s.nextInt();
		}
		fail(message, s);
		return -1;
	}

	static int requireInt(Pattern p, String message, Scanner s) {
		if (s.hasNext(p) && s.hasNextInt()) {
			return s.nextInt();
		}
		fail(message, s);
		return -1;
	}

	/**
	 * Checks whether the next token in the scanner matches the specified
	 * pattern, if so, consumes the token and return true. Otherwise returns
	 * false without consuming anything.
	 */
	static boolean checkFor(String p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

	static boolean checkFor(Pattern p, Scanner s) {
		if (s.hasNext(p)) {
			s.next();
			return true;
		} else {
			return false;
		}
	}

}

// You could add the node classes here, as long as they are not declared public (or private)
