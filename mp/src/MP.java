import java.io.FileNotFoundException;

class MP {
	MP(String filename) {
		MPscanner scanner = new MPscanner();
		try {
			scanner.openFile(filename);
		} catch (FileNotFoundException e) {
			System.out.println("Error: File " + filename + " not found");
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