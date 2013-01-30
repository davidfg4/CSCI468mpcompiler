import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

class MP {
	static PrintWriter pw = null;
	
	MP(String filename) {
		MPscanner scanner = new MPscanner();
		Token token = null;
		try {
			scanner.openFile(filename);
			token = scanner.getToken();
		} catch (FileNotFoundException e) {
			printErr("Error: File " + filename + " not found");
			System.exit(1);
		} catch (IOException ioe) {
			printErr("Error: can't read the first char of " + filename);
			System.exit(1);
		}
		while(token != null && token.getToken() != Token.TokenName.MP_EOF) {
			if (token.getToken() == Token.TokenName.MP_ERROR) {
				printErr(scanner.getError(token, "Error: Scanner error"));
			} else if (token.getToken() == Token.TokenName.MP_RUN_COMMENT) {
				printErr(scanner.getError(token, "Error: Unclosed comment"));
			} else if (token.getToken() == Token.TokenName.MP_RUN_STRING) {
				printErr(scanner.getError(token, "Error: Unclosed string"));
			}
			print(pad(token.getToken().name(), 14) + " " +
					pad("" + token.getLine(), 4) + " " +
					pad("" + token.getColumn(), 3) + " " +
					token.getLexeme());
			try {
				token = scanner.getToken();
			} catch (IOException e) {
				printErr("Error: can't read " + filename);
				System.exit(1);
			}
		}
		if (pw != null)
			pw.close();
	}
	
	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println("This program requires one argument, the file to be scanned.");
			System.exit(1);
		} else {
			new MP(args[0]);
		}
	}
	
	public static void print(String s) {
		print(s, true, true);
	}
	
	public static void print(String s, boolean screen, boolean file) {
		if (file && pw == null) {
			String tokenFileName = "token_file.txt";
			File tokenFile = new File(tokenFileName);
			try {
				pw = new PrintWriter(tokenFile);
			} catch (FileNotFoundException e) {
				printErr("Error: cannot write to token file");
				System.exit(1);
			}
		}
		if (screen)
			System.out.println(s);
		if (file)
			pw.print(s + '\n');
	}
	
	public static void printErr(String s) {
		System.err.println(s);
	}
	
	private static String pad(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}
}