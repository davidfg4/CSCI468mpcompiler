import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class SemanticAnalyzer {
	
	private StringBuilder output = new StringBuilder();
	private int labelNumber = 0;
	private MPparser parser;
	private SymbolTable symbolTable;
	
	public SemanticAnalyzer(MPparser parser, SymbolTable table) {
		this.parser = parser;
		this.symbolTable = table;
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
		// Not sure if using symbols for semantic records makes sense
		// but they work since they have the pertinent information
		if(id.type != expr.type){
			// TODO cast if possible
			parser.semanticError("Incompatible types encountered for assignement statement.");
		}
		else {
			Symbol var = symbolTable.findSymbol(id.lexeme);
			output.append("pop " + var.offset + "(D" + var.nestLevel + ")\n");
		}
	}

}
