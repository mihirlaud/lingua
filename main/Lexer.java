import java.util.Scanner;
import java.util.ArrayList;

public class Lexer {

	private Scanner sc;
	private ArrayList<Token> tokens;

	public Lexer(Scanner sc) {
		this.sc = sc;
		this.tokens = new ArrayList<Token>();
	}

	public void lexFile() {
		while(sc.hasNext()) {
			String line = sc.nextLine();
			String s = "";
			for(int i = 0; i < line.length(); i++) {
				char c = line.charAt(i);
				if(c == ' ' || c == ':' || c == '(' || c == ')' || c == ',') {
					tokens.add(new Token(s));
					s = "";
					if(c != ' ') {
						tokens.add(new Token(s + c));
					}
				} else {
					s += line.charAt(i);
				}
			}
			tokens.add(new Token(s));
			tokens.add(new Token("\n"));
		}
		System.out.println(tokens);
	}

}