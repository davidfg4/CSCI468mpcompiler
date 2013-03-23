import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class SemanticAnalyzer {
	
	private StringBuilder output = new StringBuilder();
	private int labelNumber = 0;
	private MPparser parser;
	
	public SemanticAnalyzer(MPparser parser) {
		this.parser = parser;
	}
	
	
	public String generateLabel() {
		return "L" + labelNumber++;
	}
	
	public void writeMachineCodeToFile() {
		String fileName = "a.out";
		File outputFile = new File(fileName);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outputFile);
			writer.print(output);
		}
		catch(FileNotFoundException fnfe) { System.err.println("Error wile writing output file " + fileName); }
		finally { 
			if(writer != null)
				writer.close();
		}
	}
	
	public void genAssignStmt(Symbol id, Symbol expr) {
		if(id.type == expr.type){
			// TODO stack-based or register based?
		}
		// TODO cast if possible
		else {
			parser.semanticError("Incompatible types encountered for assignement statement.");
		}
	}

}
