import java.util.Scanner;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Lexer {

	private Scanner sc;
	private ArrayList<Token> tokens;

	public Lexer(Scanner sc) {
		this.sc = sc;
		this.tokens = new ArrayList<Token>();
	}

	public ArrayList<Token> lexFile() {
		while(sc.hasNext()) {
			StringTokenizer t = new StringTokenizer(sc.nextLine(), " (),:+-*/=<>", true);
			while(t.hasMoreTokens()) {
				String str = t.nextToken();
				if(!str.equals(" "))
					tokens.add(new Token(str));
			}
			tokens.add(new Token("\n"));
		}

		return tokens;
	}

}