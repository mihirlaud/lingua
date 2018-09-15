import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
	
		if(args.length == 1) {
			try {
				Scanner sc = new Scanner(new File(args[0]));
				
				Lexer lexer = new Lexer(sc);
				lexer.lexFile();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}