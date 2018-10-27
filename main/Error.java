public class Error {

	private String type;
	private int lineNumber;
	private String ln1;
	private String ln2;
	private String ln3;
	private String message;

	public Error(String type, int lineNumber, Token[] tkn1, Token[] tkn2, Token[] tkn3) {
		this.type = type;
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
		this.message = analyzeError();
	}

	public String toString() {
		return type + " detected on line " + lineNumber + ":\n" +
			   "----------\n" +
			   (lineNumber - 1) + "\t" + ln1 + "\n" +
			   lineNumber + " -->\t" + ln2 + "\n" +
			   (lineNumber + 1) + "\t" + ln3 + "\n" +
			   "----------\n" +
			   message;
	}

	public String analyzeError() {
		return "ERROR DETECTED";
	}

}