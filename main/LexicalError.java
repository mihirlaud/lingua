public class LexicalError extends Error {

	public LexicalError(int lineNumber, Token[] tkn1, Token[] tkn2, Token[] tkn3) {
		super(lineNumber, tkn1, tkn2, tkn3);
		setType("Lexical Error");
		setMessage(analyzeError());
	}
	
	public String analyzeError() {
		for(Token token : err) {
			if(token.getType().equals(Terminal.Invalid)) {
				return token.getValue() + " is not a valid name for a variable. \n"+
					   "Make sure that your variable names only contains letters and numbers.";
			}
		}
		return "";
	}
	
	public String toString() {
		return super.toString() + getMessage();
	}

}