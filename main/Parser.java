import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Arrays;

public class Parser {

	private ArrayList<Token[]> tokens;

	public Parser(ArrayList<Token[]> tokens) {
		this.tokens = tokens;
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
				{"TRUE", "Boolean"},
				{"FALSE", "Boolean"},
				{"INT Name", "TypePhrase"},
				{"DEC Name", "TypePhrase"},
				{"FOR EVERY TypePhrase FROM Value TO Value", "Line"},
				{"BOOLEAN Name", "TypePhrase"},
				{"TypePhrase Comma TypePhrase", "ParamList"},
				{"ParamList Comma TypePhrase", "ParamList"},
				{"ParamList Comma ParamList", "ParamList"},
				{"TypePhrase OpenParen ParamList CloseParen", "Header"},
				{"TypePhrase", "Header"},
				{"DEFINE Header", "Line"},
				{"ASSIGN Value TO Name", "Line"},
				{"Name Colon Value", "Params"},
				{"Name Colon Name", "Params"},
				{"Params Comma Params", "Params"},
				{"Name OpenParen Params CloseParen", "FunctionResult"},
				{"CALL FunctionResult", "Line"},
				{"ASSIGN FunctionResult TO Name", "Line"},
				{"ASSIGN Expression TO Name", "Line"},
				{"Value Operator Value", "Expression"},
				{"Value Operator Name", "Expression"},
				{"Name Operator Value", "Expression"},
				{"Name Operator Name", "Expression"},
				{"Expression Operator Value", "Expression"},
				{"Expression Operator Name", "Expression"},
				{"Expression Operator Expression", "Expression"},
				{"ASSIGN Boolean TO Name", "Line"},
				{"ASSIGN BooleanExpression TO Name", "Line"},
				{"Name Comparator Name", "BooleanExpression"},
				{"Name Comparator Value", "BooleanExpression"},
				{"Value Comparator Name", "BooleanExpression"},
				{"Value Comparator Value", "BooleanExpression"},
				{"Name Comparator BooleanExpression", "BooleanExpression"},
				{"Value Comparator BooleanExpression", "BooleanExpression"},
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
		HashMap<String, String> memory = new HashMap<String, String>();
		int tabCount = 0;

		for(int i = 0; i < tokens.size(); i++) {
			Token[] line = tokens.get(i);
			String keyword = line[0 + tabCount].getType().toString();
			switch(keyword) {
				case "DEFINE":
					memory.put(line[2 + tabCount].getValue(), line[1 + tabCount].getValue());
					break;
				case "ASSIGN":
					if(memory.containsKey(line[line.length - 2].getValue())) {
						if(line.length == 5 + tabCount) {
							String lineType = line[1 + tabCount].getType().toString();
							String memType = memory.get(line[line.length - 2].getValue()).toUpperCase();
							if((lineType.equals("Value") && (memType.equals("INT") || memType.equals("DEC")))) {
								break;
							} else if((lineType.equals("TRUE") || lineType.equals("FALSE")) && memType.equals("BOOLEAN")) {
								break;
							} else {
								if(i == 0)
									return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
								if(i == tokens.size() - 1)
									return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
								return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
							}
						} else {
							Token[] expression = Arrays.copyOfRange(line, 1 + tabCount, line.length - 3);
							String memType = memory.get(line[line.length - 2].getValue()).toUpperCase();
							boolean error = false;
							for(Token t : expression) {
								if(memType.toUpperCase().equals(memory.get(t.getValue()).toUpperCase())) {
									continue;
								} else if(t.getType() == Terminal.Operator || t.getType() == Terminal.Value) {
									continue;
								} else {
									error = true;
									break;
								}
							}
							if(error) {
								if(i == 0)
									return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
								if(i == tokens.size() - 1)
									return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
								return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
							}
							break;
						}
					} else {
						if(i == 0)
							return new Error("Semantic Error", i + 1, null, tokens.get(i), tokens.get(i + 1));
						if(i == tokens.size() - 1)
							return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), null);
						return new Error("Semantic Error", i + 1, tokens.get(i - 1), tokens.get(i), tokens.get(i + 1));
					}
				case "IF":
					tabCount++;
					break;
				case "ELSE":
					tabCount++;
					break;
				case "FOR":
					tabCount++;
					break;
				case "WHILE":
					tabCount++;
					break;
				case "ENDIF":
					tabCount--;
					break;
				case "ENDELSE":
					tabCount--;
					break;
				case "ENDFOR":
					tabCount--;
					break;
				case "ENDWHILE":
					tabCount--;
					break;
			}
		}

		return null;
	}

}