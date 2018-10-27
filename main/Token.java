import java.util.regex.Pattern;

public class Token {

	private String value;
	private Terminal type;

	public Token(String value) {
		this.value = value;
		determineType();
	}

	public String getValue() {
		return this.value;
	}

	public Terminal getType() {
		return this.type;
	}

	public void determineType() {
		if(Pattern.matches("^[_a-z]\\w*$", this.value))
			this.type = Terminal.Name;

		if(Pattern.matches("^([-]?)(\\d+)", this.value))
			this.type = Terminal.Integer;
		
		if(Pattern.matches("^([-]?)\\d*\\.\\d+|([-]?)\\d+\\.\\d*$", this.value))
			this.type = Terminal.Decimal;

		switch(this.value.toUpperCase()) {
			case "\t":
				this.type = Terminal.Tab;
				break;
			case "\n":
				this.type = Terminal.NewLine;
				break;
			case "(":
				this.type = Terminal.OpenParen;
				break;
			case ")":
				this.type = Terminal.CloseParen;
				break;
			case ",":
				this.type = Terminal.Comma;
				break;
			case ":":
				this.type = Terminal.Colon;
				break;
			case "+":
				this.type = Terminal.Operator;
				break;
			case "-":
				this.type = Terminal.Operator;
				break;
			case "*":
				this.type = Terminal.Operator;
				break;
			case "/":
				this.type = Terminal.Operator;
				break;
			case "=":
				this.type = Terminal.Equals;
				break;
			case ">":
				this.type = Terminal.Greater;
				break;
			case "<":
				this.type = Terminal.Less;
				break;
			case "TRUE":
				this.type = Terminal.Boolean;
				break;
			case "FALSE":
				this.type = Terminal.Boolean;
				break;
		}

		for(Terminal terminal : Terminal.values()) {
			if(this.value.toUpperCase().equals(terminal.toString())) {
				this.type = terminal;
				break;
			}
		}

		if(this.type == null)
			this.type = Terminal.Invalid;

	}

	public String toString() {
		return this.value + " : " + this.type.toString();
	}

}