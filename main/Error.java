public class Error {

	private String type;
	private int lineNumber;
	private String ln1;
	private String ln2;
	private String ln3;
	private String message;
	
	public Error(String type, int lineNumber, String ln1, String ln2, String ln3) {
		this.type = type;
		this.lineNumber = lineNumber;
		this.ln1 = ln1;
		this.ln2 = ln2;
		this.ln3 = ln3;
		this.message = analyzeError();
	}
	
	public String toString() {
		return type + " detected on line " + lineNumber + ":\n" +
			   "----------\n" +
			   (lineNumber - 1) + "\t" + ln1 + "\n" + 
			   lineNumber + " -->  " + ln2 + "\n" + 
			   (lineNumber + 1) + "\t" + ln3 + "\n" +
			   "----------\n" +
			   message;
	}
	
	public String analyzeError() {
		return "ERROR DETECTED";
	}

}