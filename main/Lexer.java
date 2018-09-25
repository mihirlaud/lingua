import java.util.Scanner;
import java.util.ArrayList;

public class Lexer {

	private Scanner sc;
	private ArrayList<String> tokens;

	public Lexer(Scanner sc) {
		this.sc = sc;
		this.tokens = new ArrayList<String>();
	}
	
	public void lexFile() {
		while(sc.hasNext()) {
			tokens.add(sc.nextLine());
		}
		System.out.println(tokens);
	}

}