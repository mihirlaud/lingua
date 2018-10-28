import java.util.ArrayList;
import java.util.Stack;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Parser {

	private ArrayList<Token[]> tokens;
	private String rootFilename;

	public Parser(ArrayList<Token[]> tokens, String rootFilename) {
		this.tokens = tokens;
		this.rootFilename = rootFilename;
	}

	public Error parseTokens() {
		boolean error = false;
		boolean invalid = false;
		int invalidNum = 0;

		String line = "";
		for(int i = 0; i < tokens.size(); i++) {
			Token[] tline = tokens.get(i);
			for(Token token : tline) {
				if(token.getType().toString().equals("Invalid")) {
					invalid = true;
					invalidNum = i;
				}
				if(!token.getType().toString().equals("NewLine"))
					line += token.getType().toString() + " ";
				else {
					Error e = checkSyntax(line, i);
					if(e != null) {
						return e;
					}
					line = "";
				}
			}
		}

		if(invalid) {
			if(invalidNum == 0)
				return new Error("Lexical Error", invalidNum + 1, null, tokens.get(invalidNum), tokens.get(invalidNum + 1));
			if(invalidNum == tokens.size() - 1)
				return new Error("Lexical Error", invalidNum + 1, tokens.get(invalidNum - 1), tokens.get(invalidNum), null);
			return new Error("Lexical Error", invalidNum + 1, tokens.get(invalidNum - 1), tokens.get(invalidNum), tokens.get(invalidNum + 1));
		}

		Error e = checkSemantics();
		if(e != null) {
			return e;
		}

		return null;

	}

	public Error checkSyntax(String line, int lineNumber) {
		String original = line;
		int pass = 0;
		do {
			String init = line;
			String[][] checkStrings = {
				{"Tab ", ""},
				{"IMPORT Name", "Line"},
				{"Equals Greater", "Comparator"},
				{"Equals Less", "Comparator"},
				{"Greater Equals", "Comparator"},
				{"Less Equals", "Comparator"},
				{"Equals", "Comparator"},
				{"Greater", "Comparator"},
				{"Less", "Comparator"},
				{"EQUALS", "LogicOperator"},
				{"IS", "LogicOperator"},
				{"ISNOT", "LogicOperator"},
				{"AND", "LogicOperator"},
				{" OR", " LogicOperator"},
				{"NOT", "LogicOperator"},
				{"INT Name", "TypePhrase"},
				{"DEC Name", "TypePhrase"},
				{"FOR EVERY TypePhrase FROM Integer TO Integer", "Line"},
				{"FOR EVERY TypePhrase FROM Integer TO Name", "Line"},
				{"FOR EVERY TypePhrase FROM Name TO Integer", "Line"},
				{"FOR EVERY TypePhrase FROM Name TO Name", "Line"},
				{"BOOLEAN Name", "TypePhrase"},
				{"TypePhrase Comma TypePhrase", "ParamList"},
				{"ParamList Comma TypePhrase", "ParamList"},
				{"ParamList Comma ParamList", "ParamList"},
				{"TypePhrase OpenParen ParamList CloseParen", "Header"},
				{"TypePhrase OpenParen TypePhrase CloseParen", "Header"},
				{"VOID Name OpenParen ParamList CloseParen", "Header"},
				{"VOID Name OpenParen TypePhrase CloseParen", "Header"},
				{"TypePhrase", "Header"},
				{"DEFINE Header", "Line"},
				{"ASSIGN Integer TO Name", "Line"},
				{"ASSIGN Decimal TO Name", "Line"},
				{"ASSIGN Name TO Name", "Line"},
				{"ASSIGN Condition TO Name", "Line"},
				{"Name Colon Integer", "Params"},
				{"Name Colon Decimal", "Params"},
				{"Name Colon Boolean", "Params"},
				{"Name Colon Name", "Params"},
				{"Name Colon FunctionResult", "Params"},
				{"Params Comma Params", "Params"},
				{"Name OpenParen Params CloseParen", "FunctionResult"},
				{"Name Colon Colon FunctionResult", "FunctionResult"},
				{"CALL FunctionResult", "Line"},
				{"ASSIGN FunctionResult TO Name", "Line"},
				{"ASSIGN Expression TO Name", "Line"},
				{"Integer Operator Integer", "Expression"},
				{"Integer Operator Decimal", "Expression"},
				{"Decimal Operator Integer", "Expression"},
				{"Decimal Operator Decimal", "Expression"},
				{"Integer Operator Name", "Expression"},
				{"Decimal Operator Name", "Expression"},
				{"Name Operator Integer", "Expression"},
				{"Name Operator Decimal", "Expression"},
				{"Name Operator Name", "Expression"},
				{"Expression Operator Integer", "Expression"},
				{"Expression Operator Decimal", "Expression"},
				{"Expression Operator Name", "Expression"},
				{"Expression Operator Expression", "Expression"},
				{"ASSIGN Boolean TO Name", "Line"},
				{"ASSIGN BooleanExpression TO Name", "Line"},
				{"Name Comparator Name", "BooleanExpression"},
				{"Name Comparator Integer", "BooleanExpression"},
				{"Name Comparator Decimal", "BooleanExpression"},
				{"Integer Comparator Name", "BooleanExpression"},
				{"Decimal Comparator Name", "BooleanExpression"},
				{"Integer Comparator Integer", "BooleanExpression"},
				{"Integer Comparator Decimal", "BooleanExpression"},
				{"Decimal Comparator Integer", "BooleanExpression"},
				{"Decimal Comparator Decimal", "BooleanExpression"},
				{"BooleanExpression Comparator Decimal", "BooleanExpression"},
				{"BooleanExpression Comparator Integer", "BooleanExpression"},
				{"BooleanExpression Comparator Name", "BooleanExpression"},
				{"BooleanExpression Comparator BooleanExpression", "BooleanExpression"},
				{"BooleanExpression", "Boolean"},
				{"Boolean LogicOperator Boolean", "Condition"},
				{"Boolean LogicOperator FunctionResult", "Condition"},
				{"Boolean LogicOperator Name", "Condition"},
				{"FunctionResult LogicOperator FunctionResult", "Condition"},
				{"Name LogicOperator FunctionResult", "Condition"},
				{"Name LogicOperator Name", "Condition"},
				{"FunctionResult LogicOperator Boolean", "Condition"},
				{"Name LogicOperator Boolean", "Condition"},
				{"Condition LogicOperator Boolean", "Condition"},
				{"Condition LogicOperator FunctionResult", "Condition"},
				{"Condition LogicOperator Condition", "Condition"},
				{"ELSE IF Condition THEN", "Line"},
				{"ELSE IF Boolean THEN", "Line"},
				{"ELSE IF FunctionResult THEN", "Line"},
				{"IF Condition THEN", "Line"},
				{"IF Boolean THEN", "Line"},
				{"IF FunctionResult THEN", "Line"},
				{"WHILE Boolean DO", "Line"},
				{"WHILE Condition DO", "Line"},
				{"WHILE FunctionResult DO", "Line"},
				{"ENDIF", "Line"},
				{"ENDELSE", "Line"},
				{"ENDFOR", "Line"},
				{"ENDWHILE", "Line"},
				{"ENDDEF", "Line"},
				{"ELSE", "Line"},
				{"RETURN Name", "Line"},
				{"RETURN Integer", "Line"},
				{"RETURN Decimal", "Line"},
				{"RETURN Boolean", "Line"},
				{"RETURN Expression", "Line"},
				{"RETURN BooleanExpression", "Line"},
				{"RETURN Condition", "Line"},
			};

			for(String[] pair : checkStrings) {
				String check = pair[0];
				String replace = pair[1];
				while(line.indexOf(check) != -1) {
					line = line.substring(0, line.indexOf(check)) + replace + line.substring(line.indexOf(check) + check.length());
				}
			}

			if(init.equals(line)) {
				pass++;
			} else {
				pass = 0;
			}
			//System.out.println(line + " " + pass);
		} while(!line.equals("Line ") && pass < 3);

		if(line.equals("Line ") || line.equals(""))
			return null;
		else {
			if(lineNumber == 0)
				return new Error("Syntax Error", lineNumber + 1, null, tokens.get(lineNumber), tokens.get(lineNumber + 1));
			if(lineNumber == tokens.size() - 1)
				return new Error("Syntax Error", lineNumber + 1, tokens.get(lineNumber - 1), tokens.get(lineNumber), null);
			return new Error("Syntax Error", lineNumber + 1, tokens.get(lineNumber - 1), tokens.get(lineNumber), tokens.get(lineNumber + 1));
		}
	}

	public Error checkSemantics() {
		Stack<String> memory = new Stack<>();
		Stack<String> layer = new Stack<>();
		Stack<String> lastVar = new Stack<>();
		boolean possibleElse = false;
		int tabCount = 0;
		
		for(int i = 0; i < tokens.size(); i++) {
			Token[] line = tokens.get(i);
			Terminal keyword = line[0 + tabCount].getType();
			switch(keyword) {
				case IMPORT:
					String filename = line[1 + tabCount].getValue();
					Scanner sc;
					try {
						if(rootFilename.indexOf("\\") != -1) {
							filename = rootFilename.substring(0, rootFilename.indexOf("\\") + 1) + filename;
						}
						sc = new Scanner(new File(filename + ".java"));
					} catch(FileNotFoundException e) {
						try {
							sc = new Scanner(new File(filename + ".lng"));
						} catch(FileNotFoundException err) {
							if(i == 0)
								return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
							if(i == tokens.size() - 1)
								return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
						}
					}
					while(sc.hasNext()) {
						String f = sc.nextLine();
						if(f.indexOf("///") != -1) {
							memory.push(filename + "|" + f.substring(f.indexOf("///") + 3));
						}
					}
					break;
				case DEFINE:
					possibleElse = false;
					if(line.length == 4 + tabCount) {
						String var = line[2 + tabCount].getValue() + ":" + line[1 + tabCount].getType().toString();
						if(memory.search(var) == -1)
							memory.push(var);
						else {
							if(i == 0)
								return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
							if(i == tokens.size() - 1)
								return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
						}
						break;
					} else {
						String var = line[2 + tabCount].getValue() + ":" + line[1 + tabCount].getType().toString() + "(";
						for(int x = 5 + tabCount; x < line.length - 2; x += 3) {
							var += line[x].getValue() + ":" + line[x - 1].getType().toString();
							if(x < line.length - 3)
								var += ",";
						}
						var += ")";
						if(memory.search(var) == -1) {
							memory.push(var);
							layer.push("DEF");
							lastVar.push(memory.peek());
							for(int x = 5 + tabCount; x < line.length - 2; x += 3) {
								String push = line[x].getValue() + ":" + line[x - 1].getType().toString();
							}
							tabCount++;
						} else {
							if(i == 0)
								return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
							if(i == tokens.size() - 1)
								return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
						}
						break;
					}
				case ENDDEF:
					possibleElse = false;
					if(layer.peek().equals("DEF")) {
						while(!lastVar.peek().equals(memory.peek())) {
							memory.pop();
						}
						layer.pop();
						lastVar.pop();
					} else {
						if(i == 0)
							return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
						if(i == tokens.size() - 1)
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
						return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
					}
					tabCount--;
					break;
				case RETURN:
					possibleElse = false;
					boolean err = false;
					String returnVal = "";
					Terminal tempType = Terminal.Invalid;
					Token[] returnExpression = Arrays.copyOfRange(line, 1 + tabCount, line.length - 1);
					for(Token token : returnExpression) {
						if(token.getType() == Terminal.Integer || token.getType() == Terminal.Decimal || token.getType() == Terminal.Boolean) {
							if(tempType != Terminal.Invalid) {
								if(tempType != token.getType()) {
									err = true;
									break;
								}
							} else {
								tempType = token.getType();
							}
						} else if(token.getType() == Terminal.Name) {
							boolean notInMemory = true;
							for(String mem : memory) {
								if(mem.substring(0, mem.indexOf(":")).equals(token.getValue())) {
									notInMemory = false;
									Terminal varType = Terminal.Invalid;
									switch(mem.substring(mem.indexOf(":") + 1)) {
										case "INT":
											varType = Terminal.Integer;
											break;
										case "DEC":
											varType = Terminal.Decimal;
											break;
										case "BOOLEAN":
											varType = Terminal.Boolean;
											break;
									}
									if(tempType != Terminal.Invalid) {
										if(tempType != varType) {
											err = true;
											break;
										}
									} else {
										tempType = varType;
									}
								}
							}
							if(notInMemory) {
								if(i == 0)
									return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
								if(i == tokens.size() - 1)
									return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
								return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
							}
						}
					}
					if(err) {
						if(i == 0)
							return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
						if(i == tokens.size() - 1)
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
						return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
					}
					switch(tempType) {
						case Integer:
							returnVal = "INT";
							break;
						case Decimal:
							returnVal = "DEC";
							break;
						case Boolean:
							returnVal = "BOOLEAN";
							break;
					}
					String returnType = memory.get(memory.size() - (memory.search(lastVar.peek())));
					returnType = returnType.substring(returnType.indexOf(":") + 1, returnType.indexOf("("));
					if(layer.peek().equals("DEF")) {
						if(returnVal.equals(returnType)) {
							while(!lastVar.peek().equals(memory.peek())) {
								memory.pop();
							}
							layer.pop();
							lastVar.pop();
						} else {
							if(i == 0)
								return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
							if(i == tokens.size() - 1)
								return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
						}
					} else {
						if(i == 0)
							return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
						if(i == tokens.size() - 1)
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
						return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
					}
					tabCount--;
					break;
				case ASSIGN:
					possibleElse = false;
					boolean error = false;
					Terminal type = Terminal.Invalid;
					Token[] expression = Arrays.copyOfRange(line, 1 + tabCount, line.length - 3);
					for(Token token : expression) {
						if(token.getType() == Terminal.Integer || token.getType() == Terminal.Decimal || token.getType() == Terminal.Boolean) {
							if(type != Terminal.Invalid) {
								if(type != token.getType()) {
									error = true;
									break;
								}
							} else {
								type = token.getType();
							}
						} else if(token.getType() == Terminal.Name) {
							boolean notInMemory = true;
							for(String mem : memory) {
								if(mem.substring(0, mem.indexOf(":")).equals(token.getValue())) {
									notInMemory = false;
									Terminal varType = Terminal.Invalid;
									switch(mem.substring(mem.indexOf(":") + 1)) {
										case "INT":
											varType = Terminal.Integer;
											break;
										case "DEC":
											varType = Terminal.Decimal;
											break;
										case "BOOLEAN":
											varType = Terminal.Boolean;
											break;
									}
									if(type != Terminal.Invalid) {
										if(type != varType) {
											error = true;
											break;
										}
									} else {
										type = varType;
									}
								}
							}
							if(notInMemory) {
								if(i == 0)
									return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
								if(i == tokens.size() - 1)
									return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
								return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
							}
						}
					}
					String check = line[line.length - 2].getValue() + ":";
					switch(type) {
						case Integer:
							check += "INT";
							break;
						case Decimal:
							check += "DEC";
							break;
						case Boolean:
							check += "BOOLEAN";
							break;
					}
					if(error) {
						if(i == 0)
							return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
						if(i == tokens.size() - 1)
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
						return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
					}
					if(memory.search(check) != -1) {
						break;
					} else {
						if(i == 0)
							return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
						if(i == tokens.size() - 1)
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
						return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
					}
				case IF:
					possibleElse = false;
					layer.push("IF");
					lastVar.push(memory.peek());
					tabCount++;
					break;
				case ENDIF:
					possibleElse = true;
					if(layer.peek().equals("IF")) {
						while(!lastVar.peek().equals(memory.peek())) {
							memory.pop();
						}
						layer.pop();
						lastVar.pop();
					} else {
						if(i == 0)
							return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
						if(i == tokens.size() - 1)
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
						return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
					}
					tabCount--;
					break;
				case ELSE:
					if(possibleElse) {
						possibleElse = false;
						layer.push("ELSE");
						lastVar.push(memory.peek());
						tabCount++;
						break;
					} else {
						if(i == 0)
							return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
						if(i == tokens.size() - 1)
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
						return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
					}
				case ENDELSE:
					if(layer.peek().equals("ELSE")) {
						while(!lastVar.peek().equals(memory.peek())) {
							memory.pop();
						}
						layer.pop();
						lastVar.pop();
					} else {
						if(i == 0)
							return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
						if(i == tokens.size() - 1)
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
						return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
					}
					tabCount--;
					break;
				case WHILE:
					possibleElse = false;
					layer.push("WHILE");
					lastVar.push(memory.peek());
					tabCount++;
					break;
				case ENDWHILE:
					if(layer.peek().equals("WHILE")) {
						while(!lastVar.peek().equals(memory.peek())) {
							memory.pop();
						}
						layer.pop();
						lastVar.pop();
					} else {
						if(i == 0)
							return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
						if(i == tokens.size() - 1)
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
						return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
					}
					tabCount--;
					break;
				case FOR:
					possibleElse = false;
					layer.push("FOR");
					lastVar.push(memory.peek());
					memory.push(line[3 + tabCount].getValue() + ":" + line[2 + tabCount].getType().toString());
					tabCount++;
					break;
				case ENDFOR:
					if(layer.peek().equals("FOR")) {
						while(!lastVar.peek().equals(memory.peek())) {
							memory.pop();
						}
						layer.pop();
						lastVar.pop();
					} else {
						if(i == 0)
							return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
						if(i == tokens.size() - 1)
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
						return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
					}
					tabCount--;
					break;
				case CALL:
					break;
			}
		}
		
		if(tabCount > 0) {
			return new Error("Semantic Error", tokens.size(), tokens.get(tokens.size() - 2), tokens.get(tokens.size() - 1), null);
		}
		return null;
	}

}