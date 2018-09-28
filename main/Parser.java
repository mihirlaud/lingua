import java.util.ArrayList;

public class Parser {

	private ArrayList<Token> tokens;

	public Parser(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}

	public void parseTokens() {
		System.out.println(tokens);
	}

}