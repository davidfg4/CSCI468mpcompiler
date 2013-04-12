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
	
	/**
	 * Populate copy record with relevant information from existing record
	 * @param copy
	 * @param existing
	 */
	public void copy(Symbol existing, Symbol copy) {
		copy.type = existing.type;
		copy.lexeme = existing.lexeme;
	}
	
	/**
	 * Generates code for assignment statements 
	 * @param id
	 * @param expr
	 */
	public void genAssignStmt(Symbol id, Symbol expr) {
		if(id.type != expr.type){
			// TODO cast if possible
			parser.semanticError("Incompatible types encountered for assignement statement: " + id.type + " := " + expr.type);
		}
		else {
			Symbol var = symbolTable.findSymbol(id.lexeme);
			output.append("pop " + var.offset + "(D" + var.nestLevel + ")\n" );
		}
	}
	
	/**
	 * Generates code for arithmetic statements
	 * @param leftRec
	 * @param opRec
	 * @param rightRec
	 * @param resultRec
	 */
	public void genArithmetic(Symbol leftRec, Symbol opRec, Symbol rightRec, Symbol resultRec) {
		if(leftRec.type == rightRec.type) {
			resultRec.type = leftRec.type;
		}
		else if(leftRec.type == Symbol.Type.FLOAT && rightRec.type == Symbol.Type.INTEGER) {
			// TODO  implement cast
			resultRec.type = Symbol.Type.FLOAT;
		}
		else if(leftRec.type == Symbol.Type.INTEGER && rightRec.type == Symbol.Type.FLOAT) {
			// TODO implement cast
			resultRec.type = Symbol.Type.FLOAT;
		}
	}
	
	/**
	 * Generates code to push variable onto stack
	 * @param idRec
	 */
	public void genPushId(Symbol idRec) {
		Symbol var = symbolTable.findSymbol(idRec.lexeme);
		// TODO check that variable is proper type to push
	}
	
	/**
	 * Generates code to push literal onto stack
	 * @param literalRec
	 */
	public void genPushLiteral(Symbol literalRec) {
		// TODO semantic checks? 
	}

}
