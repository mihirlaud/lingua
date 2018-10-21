import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

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
			String s1 = "";
			if(invalidNum > 0)
				for(int j = 0; j < tokens.get(invalidNum - 1).length - 1; j++)
					s1 += tokens.get(invalidNum - 1)[j].getValue() + " ";
			String s2 = "";
			for(int j = 0; j < tokens.get(invalidNum).length - 1; j++)
				s2 += tokens.get(invalidNum)[j].getValue() + " ";
			String s3 = "";
			if(invalidNum < tokens.size() - 1)
				for(int j = 0; j < tokens.get(invalidNum + 1).length - 1; j++)
					s3 += tokens.get(invalidNum + 1)[j].getValue() + " ";
			return new Error("Lexical Error", invalidNum + 1, s1, s2, s3);
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
			String s1 = "";
			if(lineNumber > 0)
				for(int j = 0; j < tokens.get(lineNumber - 1).length - 1; j++)
					s1 += tokens.get(lineNumber - 1)[j].getValue() + " ";
			String s2 = "";
			for(int j = 0; j < tokens.get(lineNumber).length - 1; j++)
				s2 += tokens.get(lineNumber)[j].getValue() + " ";
			String s3 = "";
			if(lineNumber < tokens.size() - 1)
				for(int j = 0; j < tokens.get(lineNumber + 1).length - 1; j++)
					s3 += tokens.get(lineNumber + 1)[j].getValue() + " ";
			return new Error("Semantic Error", lineNumber + 1, s1, s2, s3);
		}
	}
	
	public Error checkSemantics() {
		HashMap<String, String> memory = new HashMap<String, String>();
		
		for(int i = 0; i < tokens.size(); i++) {
			Token[] line = tokens.get(i);
			String keyword = line[0].getType().toString();
			switch(keyword) {
				case "DEFINE":
					memory.put(line[2].getValue(), line[1].getValue());
					break;
				case "ASSIGN":
					if(memory.containsKey(line[line.length - 2].getValue())) {
						String lineType = line[1].getType().toString();
						String memType = memory.get(line[line.length - 2].getValue()).toUpperCase();
						if((lineType.equals("Value") && (memType.equals("INT") || memType.equals("DEC")))) {
							break;
						} else if((lineType.equals("TRUE") || lineType.equals("FALSE")) && memType.equals("BOOLEAN")) {
							break;
						} else {
							String s1 = "";
							if(i > 0)
								for(int j = 0; j < tokens.get(i - 1).length - 1; j++)
									s1 += tokens.get(i - 1)[j].getValue() + " ";
							String s2 = "";
							for(int j = 0; j < tokens.get(i).length - 1; j++)
								s2 += tokens.get(i)[j].getValue() + " ";
							String s3 = "";
							if(i < tokens.size() - 1)
								for(int j = 0; j < tokens.get(i + 1).length - 1; j++)
									s3 += tokens.get(i + 1)[j].getValue() + " ";
							return new Error("Semantic Error", i+1, s1, s2, s3);
						}
					} else {
						String s1 = "";
						if(i > 0)
							for(int j = 0; j < tokens.get(i - 1).length - 1; j++)
								s1 += tokens.get(i - 1)[j].getValue() + " ";
						String s2 = "";
						for(int j = 0; j < tokens.get(i).length - 1; j++)
							s2 += tokens.get(i)[j].getValue() + " ";
						String s3 = "";
						if(i < tokens.size() - 1)
							for(int j = 0; j < tokens.get(i + 1).length - 1; j++)
								s3 += tokens.get(i + 1)[j].getValue() + " ";
						return new Error("Semantic Error", i+1, s1, s2, s3);
					}
			}
		}
		
		return null;
	}

}