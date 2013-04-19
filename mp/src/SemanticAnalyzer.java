import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class SemanticAnalyzer {
	
	private StringBuilder output = new StringBuilder();
	private int labelNumber = 0;
	private MPparser parser;
	private SymbolTable symbolTable;
	// TODO at some point we should probably clear output if errors encountered; 
	// shouldn't emit erroneous code but it seems it might be useful for testing for now
	
	public SemanticAnalyzer(MPparser parser, SymbolTable table) {
		this.parser = parser;
		this.symbolTable = table;
	}
	
	
	public String generateLabel() {
		return "L" + labelNumber++;
	}
	
	public void writeMachineCodeToFile(String filename) {
		filename = filename.split("\\.")[0] + ".s";
		File outputFile = new File(filename);
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outputFile);
			writer.print(output);
		}
		catch(FileNotFoundException fnfe) { System.err.println("Error wile writing output file " + filename); }
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
		if(id.type == Symbol.Type.FLOAT && expr.type == Symbol.Type.INTEGER) {
			output.append("castsf\n");	// cast expression result (stack top) to float to assign to id
		}
		else if(id.type == Symbol.Type.INTEGER && expr.type == Symbol.Type.FLOAT) {
			output.append("castsi\n");	// cast expression result to integer to assign to id
		}
		else if(id.type != expr.type)
			parser.semanticError("Incompatible types encountered for assignement statement: " + id.type + " := " + expr.type);
		Symbol var = symbolTable.findSymbol(id.lexeme);
		// assuming parser will catch undeclared id's so no need to null check
		output.append("pop " + var.offset + "(D" + var.nestLevel + ")\n" );
	}
	
	/**
	 * Generates code for arithmetic statements
	 * @param leftRec
	 * @param opRec
	 * @param rightRec
	 * @param resultRec
	 */
	public void genArithmetic(Symbol leftRec, Symbol opRec, Symbol rightRec, Symbol resultRec) {
		String operation = null;
		boolean booleanOp = false, 
				relOp = false;		// if relational op, set resulting type to boolean
		switch(opRec.lexeme.toLowerCase()) {
			case "+":
				operation = "adds";
				break;
			case "-":
				operation = "subs";
				break;
			case "*":
				operation = "muls";
				break;
			case "div":
				operation = "divs";
				break;
//			case "/":
//				operation = "divs";	// Not sure how this will work..
//				break;
			case "=":
				operation = "cmpeqs";
				relOp = true;
				break;
			case ">=":
				operation = "cmpges";
				relOp = true;
				break;
			case ">":
				operation = "cmpgts";
				relOp = true;
				break;
			case "<=":
				operation = "cmples";
				relOp = true;
				break;
			case "<":
				operation = "cmplts";
				relOp = true;
				break;
			case "<>":
				operation = "cmpnes";
				relOp = true;
				break;
			case "and":
				operation = "ands";
				booleanOp = true;
				break;
			case "or":
				operation = "ors";
				booleanOp = true;
				break;
		}
		if(booleanOp && leftRec.type == Symbol.Type.BOOLEAN && rightRec.type == Symbol.Type.BOOLEAN) {
			output.append(operation+"\n");
			resultRec.type = leftRec.type;
		}
		// disallow arithmetic/compare operations on boolean and string types
		else if(leftRec.type!= Symbol.Type.BOOLEAN && leftRec.type != Symbol.Type.STRING) {
			if(leftRec.type == rightRec.type) {
				resultRec.type = relOp ? Symbol.Type.BOOLEAN : leftRec.type;
				output.append(operation+"\n");
			}
			// Stack top needs to be casted to float:
			else if(leftRec.type == Symbol.Type.FLOAT && rightRec.type == Symbol.Type.INTEGER) {
				resultRec.type = relOp ? Symbol.Type.BOOLEAN : Symbol.Type.FLOAT;
				output.append("castsf\n"); 
				output.append(operation + "f\n");
			}
			// Second in from top of stack needs to be casted to float:
			else if(leftRec.type == Symbol.Type.INTEGER && rightRec.type == Symbol.Type.FLOAT) {
				resultRec.type = relOp ? Symbol.Type.BOOLEAN : Symbol.Type.FLOAT;
				output.append("push -2(SP)\n");		// Push value below value on top of stack (the int)
				output.append("castsf\n");			// and cast this value to float
				output.append("pop -2(SP)\n");		// then put it back where it was
				output.append(operation + "f\n");
			}
			else if(leftRec.type != rightRec.type) 
				parser.semanticError("Incompatible types encountered for expression: " + leftRec.type + " " + opRec.lexeme + " " + rightRec.type);
		}
		else 
			parser.semanticError("Incompatible types encountered for expression: " + leftRec.type + " " + opRec.lexeme + " " + rightRec.type);
	}
	
	/**
	 * Generates code to push variable onto stack
	 * @param idRec
	 */
	public void genPushId(Symbol idRec, Symbol signRec) {
		Symbol var = symbolTable.findSymbol(idRec.lexeme);
		output.append("push " + var.offset + "(D" + var.nestLevel + ")\n");
		if(signRec.negative)
			this.genNegOp(idRec);
	}
	
	public void genNegOp(Symbol factorRec) {
		// choose appropriate negation operator (boolean, float, int) --> ("nots", "negsf", "negs")
		String negOp = factorRec.type == Symbol.Type.BOOLEAN ? "nots\n" : factorRec.type == Symbol.Type.FLOAT ? "negsf\n" : "negs\n";
		output.append(negOp);	// negate top of stack
	}
	
	/**
	 * Generates code to push primitive literal onto stack
	 * @param literalRec
	 */
	public void genPushLiteral(Symbol literalRec, Symbol signRec) {
		String literal = literalRec.type == Symbol.Type.STRING ? "\"" + literalRec.lexeme + "\"" : literalRec.lexeme;
		if(signRec.negative)
			literal = "-" + literal;
		output.append("push #" + literal + "\n");	// Push primitive literal
	}
	
	/**
	 * Translates "true" to 1 and "false" to 0 and generates code
	 * to push relevant value onto stack
	 * @param boolLit
	 */
	public void genPushBoolLit(Symbol boolLit) {
		String bool = boolLit.lexeme.equalsIgnoreCase("true") ? "1" : "0";
		output.append("push #" + bool + "\n");
	}
	
	/**
	 * Generates code to write (and pop) current value of stack
	 */
	public void genWriteStmt() {
		output.append("wrts\n");
	}
	
	/**
	 * Generates code to write newline
	 */
	public void genWriteLnStmt() {
		output.append("wrtln #\"\"\n");
	}
	
	/**
	 * Generates code to read a value from command line and store into variable
	 * @param paramRec
	 */
	public void genReadStmt(Symbol paramRec) {
		Symbol var = symbolTable.findSymbol(paramRec.lexeme);
		String rdOp = null;
		switch(paramRec.type) {
			case INTEGER:
				rdOp = "rd ";
				break;
			case FLOAT:
				rdOp = "rdf ";
				break;
			case STRING:
				rdOp = "rds ";
				break;
			default:
				parser.semanticError("Unsupported parameter type supplied for read");
				break;
		}
		output.append(rdOp + var.offset + "(D" + var.nestLevel + ")\n");
	}
	
	/**
	 * Generates test code/labels for if statement
	 * @param ifRec
	 */
	public void genIfTest(Symbol ifRec, Symbol exprRec) {
		ifRec.label1 = this.generateLabel();
		ifRec.label2 = this.generateLabel();
		// branch past "then" statements if value on stack is false
		if(exprRec.type == Symbol.Type.BOOLEAN)
			output.append("brfs " + ifRec.label1 + "\n");	
		else
			parser.semanticError("Expected boolean expression result, got " + exprRec.type);
	}
	
	/**
	 * Generates code to branch past "else" statements if "then" part executed and
	 * drops first label generated for if statement
	 * @param ifRec
	 */
	public void processElse(Symbol ifRec) {
		// branch past "else" statements if "then" statements are evaluated
		output.append("br " + ifRec.label2 + "\n");
		output.append(ifRec.label1 + ":\n");
	}
	
	/**
	 * Drops label1 when no else clause present
	 * @param ifRec
	 */
	public void processNoElse(Symbol ifRec) {
		output.append(ifRec.label1 + ":\n");
	}
	
	/**
	 * Drops label2 for end of if statement
	 * @param ifRec
	 */
	public void genFinishIf(Symbol ifRec) {
		output.append(ifRec.label2 + ":\n");
	}
	
	/**
	 * Generates while labels and drops label to begin while statement
	 * @param whileRec
	 */
	public void genBeginWhile(Symbol whileRec) {
		whileRec.label1 = this.generateLabel();
		whileRec.label2 = this.generateLabel();
		output.append(whileRec.label1 + ":\n");
	}
	
	/**
	 * Generates test code for while loop
	 * @param whileRec
	 * @param exprRec
	 */
	public void genWhileTest(Symbol whileRec, Symbol exprRec) {
		if(exprRec.type == Symbol.Type.BOOLEAN)
			output.append("brfs " + whileRec.label2 + "\n");	// skip while block if condition is false
		else
			parser.semanticError("Expected boolean expression result, got " + exprRec.type);
	}
	
	/**
	 * Generates unconditional branch to beginning of while,
	 * drops label for end while
	 * @param whileRec
	 */
	public void genEndWhile(Symbol whileRec) {
		output.append("br " + whileRec.label1 + "\n");
		output.append(whileRec.label2 + ":\n");
	}
	
	/**
	 * Drops begin label for repeat block
	 * @param repeatRec
	 */
	public void genBeginRepeat(Symbol repeatRec) {
		repeatRec.label1 = this.generateLabel();
		output.append(repeatRec.label1 + ":\n");
	}
	
	/**
	 * Generates branch on false to beginning of repeat block
	 * @param repeatRec
	 * @param exprRec
	 */
	public void genEndRepeat(Symbol repeatRec, Symbol exprRec) {
		if(exprRec.type == Symbol.Type.BOOLEAN)
			output.append("brfs " + repeatRec.label1);	// repeat until condition is true
		else
			parser.semanticError("Expected boolean expression result, got " + exprRec.type);
	}
}
