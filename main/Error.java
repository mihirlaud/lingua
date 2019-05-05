public class Error {

	String type = "Error";
	int lineNumber;
	String ln1;
	String ln2;
	String ln3;
	Token[] err;
	String message = "ERROR DETECTED";

	public Error(int lineNumber, Token[] tkn1, Token[] tkn2, Token[] tkn3) {
		this.lineNumber = lineNumber;
		this.ln1 = "";
		if(tkn1 != null)
			for(int i = 0; i < tkn1.length - 1; i++)
				this.ln1 += tkn1[i].getValue() + " ";
		this.ln2 = "";
		for(int i = 0; i < tkn2.length - 1; i++)
			this.ln2 += tkn2[i].getValue() + " ";
		this.ln3 = "";
		if(tkn3 != null)
			for(int i = 0; i < tkn3.length - 1; i++)
				this.ln3 += tkn3[i].getValue() + " ";
		this.err = tkn2;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}

	public Token[] getErrorLine() {
		return this.err;
	}

	public String toString() {
		return type + " detected on line " + lineNumber + ":\n" +
			   "----------\n" +
			   (lineNumber - 1) + "\t" + ln1 + "\n" +
			   lineNumber + " -->\t" + ln2 + "\n" +
			   (lineNumber + 1) + "\t" + ln3 + "\n" +
			   "----------\n";
	}

}