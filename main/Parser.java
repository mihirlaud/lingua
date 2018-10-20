import java.util.ArrayList;
import java.util.StringTokenizer;

public class Parser {

	private ArrayList<Token> tokens;

	public Parser(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}

	public boolean parseTokens() {
		boolean error = false;

		String line = "";
		for(Token token : tokens) {
			if(!token.getType().toString().equals("NewLine"))
				line += token.getType().toString() + " ";
			else {
				if(!checkSyntax(line)) {
					System.out.println("LINE ERROR");
					error = true;
					break;
				}
				line = "";
			}
		}

		if(error) {
			return true;
		}

		return false;
	}

	public boolean checkSyntax(String line) {
		int pass = 0;
		do {
			String init = line;
			String[][] checkStrings = {
				{"Tab ", ""},
				{"Equals", "Comparator"},
				{"Greater", "Comparator"},
				{"Less", "Comparator"},
				{"Equals Greater", "Comparator"},
				{"Equals Less", "Comparator"},
				{"Greater Equals", "Comparator"},
				{"Less Equals", "Comparator"},
				{"EQUALS", "LogicOperator"},
				{"IS", "LogicOperator"},
				{"ISNOT", "LogicOperator"},
				{"AND", "LogicOperator"},
				{" OR", " LogicOperator"},
				{"NOT", "LogicOperator"},
				{"TRUE", "Boolean"},
				{"FALSE", "Boolean"},
				{"INT Value", "TypePhrase"},
				{"DEC Value", "TypePhrase"},
				{"FOR EVERY TypePhrase FROM Value TO Value", "Line"},
				{"BOOLEAN Value", "TypePhrase"},
				{"TypePhrase Comma TypePhrase", "ParamList"},
				{"ParamList Comma TypePhrase", "ParamList"},
				{"ParamList Comma ParamList", "ParamList"},
				{"TypePhrase OpenParen ParamList CloseParen", "Header"},
				{"TypePhrase", "Header"},
				{"DEFINE Header", "Line"},
				{"ASSIGN Value TO Value", "Line"},
				{"Value Colon Value", "Params"},
				{"Params Comma Params", "Params"},
				{"Value OpenParen Params CloseParen", "FunctionResult"},
				{"CALL FunctionResult", "Line"},
				{"ASSIGN FunctionResult TO Value", "Line"},
				{"ASSIGN Expression TO Value", "Line"},
				{"Value Operand Value", "Expression"},
				{"Expression Operand Value", "Expression"},
				{"Expression Operand Expression", "Expression"},
				{"ASSIGN Boolean TO Value", "Line"},
				{"Value Comparator Value", "BooleanExpression"},
				{"Value Comparator BooleanExpression", "BooleanExpression"},
				{"BooleanExpression Comparator BooleanExpression", "BooleanExpression"},
				{"BooleanExpression", "Boolean"},
				{"Boolean LogicOperator Boolean", "Condition"},
				{"Boolean LogicOperator FunctionResult", "Condition"},
				{"Boolean LogicOperator Value", "Condition"},
				{"FunctionResult LogicOperator FunctionResult", "Condition"},
				{"Value LogicOperator FunctionResult", "Condition"},
				{"Value LogicOperator Value", "Condition"},
				{"FunctionResult LogicOperator Boolean", "Condition"},
				{"Value LogicOperator Boolean", "Condition"},
				{"Condition LogicOperator Boolean", "Condition"},
				{"Condition LogicOperator FunctionResult", "Condition"},
				{"Condition LogicOperator Condition", "Condition"},
				{"IF Condition THEN", "Line"},
				{"IF Boolean THEN", "Line"},
				{"IF FunctionResult THEN", "Line"},
				{"WHILE Boolean DO", "Line"},
				{"WHILE Condition DO", "Line"},
				{"WHILE FunctionResult DO", "Line"},
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

		return (line.equals("Line ") || line.equals(""));
	}

}