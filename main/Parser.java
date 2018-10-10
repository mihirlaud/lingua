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
				{"INT Value", "TypePhrase"},
				{"DEC Value", "TypePhrase"},
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
		} while(!line.equals("Line ") && pass < 3);
		
		return (line.equals("Line ") || line.equals(""));
	}

}