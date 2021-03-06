import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		if(args.length == 1) {
			try {
				Scanner sc = new Scanner(new File(args[0]));

				Lexer lexer = new Lexer(sc);
				Parser parser = new Parser(lexer.lexFile(), args[0]);
				Error err = parser.parseTokens();
				if(err != null)
					System.out.println(err);
				else {
					Generator generator = new Generator(lexer.lexFile(), args[0]);
					generator.generateJasminFile();
					generator.executeJasminFile();
				}
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}