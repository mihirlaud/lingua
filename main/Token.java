import java.util.regex.Pattern;

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

	public Terminal getType() {
		return this.t;
	}

	public void determineType() {
		if(Pattern.matches("^[_a-z]\\w*$", this.value))
			this.t = Terminal.Name;

		if(Pattern.matches("^([-]?)(\\d+)", this.value))
			this.t = Terminal.Value;

		switch(this.value) {
			case "\t":
				this.t = Terminal.Tab;
				break;
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
			case "+":
				this.t = Terminal.Operator;
				break;
			case "-":
				this.t = Terminal.Operator;
				break;
			case "*":
				this.t = Terminal.Operator;
				break;
			case "/":
				this.t = Terminal.Operator;
				break;
			case "=":
				this.t = Terminal.Equals;
				break;
			case ">":
				this.t = Terminal.Greater;
				break;
			case "<":
				this.t = Terminal.Less;
				break;
		}

		for(Terminal terminal : Terminal.values()) {
			if(this.value.toUpperCase().equals(terminal.toString())) {
				this.t = terminal;
				break;
			}
		}

		if(this.t == null)
			this.t = Terminal.Invalid;

	}

	public String toString() {
		return this.value + " : " + this.t.toString();
	}

}