public class Token {

	private String value;
	private Terminal t;

	public Token(String value) {
		this.value = value;
		determineType();
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void determineType() {
		for(Terminal terminal : Terminal.values()) {
			if(this.value.toUpperCase().equals(terminal.toString())) {
				this.t = terminal;
				break;
			}
		}
		
		switch(this.value) {
			case "\n":
				this.t = Terminal.NewLine;
				break;
			case "(":
				this.t = Terminal.OpenParen;
				break;
			case ")":
				this.t = Terminal.CloseParen;
				break;
			case ",":
				this.t = Terminal.Comma;
				break;
			case ":":
				this.t = Terminal.Colon;
				break;
		}
	}

}