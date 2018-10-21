import java.util.Scanner;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Lexer {

	private Scanner sc;
	private ArrayList<Token[]> tokens;

	public Lexer(Scanner sc) {
		this.sc = sc;
		this.tokens = new ArrayList<Token[]>();
	}

	public ArrayList<Token[]> lexFile() {
		while(sc.hasNext()) {
			ArrayList<Token> line = new ArrayList<Token>();
			StringTokenizer t = new StringTokenizer(sc.nextLine(), "\t (),:+-/*=<>", true);
			while(t.hasMoreTokens()) {
				String str = t.nextToken();
				if(!str.equals(" "))
					line.add(new Token(str));
			}
			line.add(new Token("\n"));
			tokens.add(line.toArray(new Token[1]));
		}
		
		return tokens;
	}

}