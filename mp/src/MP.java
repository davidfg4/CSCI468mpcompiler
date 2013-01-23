import java.io.FileNotFoundException;
import java.io.IOException;

class MP {
	MP(String filename) {
		MPscanner scanner = new MPscanner();
		Token token = null;
		try {
			scanner.openFile(filename);
			token = scanner.getToken();
		} catch (FileNotFoundException e) {
			System.out.println("Error: File " + filename + " not found");
			System.exit(1);
		} catch (IOException ioe) {
			System.out.println("Error while reading the first char of " + filename);
			System.exit(1);
		}
		while(token != null && token.getToken() != Token.TokenName.MP_EOF) {
			if (token.getToken() == Token.TokenName.MP_ERROR) {
				scanner.printError(token);
			}
			System.out.println(pad(token.getToken().name(), 14) + " " +
					pad("" + token.getLine(), 4) + " " +
					pad("" + token.getColumn(), 3) + " " +
					token.getLexeme());
			try {
				token = scanner.getToken();
			} catch (IOException e) {
				System.out.println("Error while reading " + filename);
				System.exit(1);
			}
		}
	}
	
	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println("This program requires one argument, the file to be scanned.");
			System.exit(1);
		} else {
			new MP(args[0]);
		}
	}
	
	public static String pad(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}
}