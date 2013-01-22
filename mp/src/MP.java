import java.io.FileNotFoundException;
import java.io.IOException;

class MP {
	MP(String filename) {
		MPscanner scanner = new MPscanner();
		try {
			scanner.openFile(filename);
			Token token = scanner.getToken();
			while(token != null) {
				token = scanner.getToken();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Error: File " + filename + " not found");
			System.exit(1);
		} catch (IOException ioe) {
			System.out.println("Error while reading input file.");
			System.exit(1);
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
}