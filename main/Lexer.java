import java.util.Scanner;

public class Lexer {

	private Scanner sc;

	public Lexer(Scanner sc) {
		this.sc = sc;
	}
	
	public void lexFile() {
		while(sc.hasNext()) {
			System.out.println(sc.nextLine());
		}
	}

}