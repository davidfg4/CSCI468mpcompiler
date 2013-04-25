import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


public class SemanticAnalyzer {
	
	private StringBuilder output = new StringBuilder();
	private int labelNumber = 0;
	private MPparser parser;
	private SymbolTable symbolTable;
	private boolean error = false;
	
	public SemanticAnalyzer(MPparser parser, SymbolTable table) {
		this.parser = parser;
		this.symbolTable = table;
	}
	
	
	public String generateLabel() {
		return "L" + labelNumber++;
	}
	
	public void writeMachineCodeToFile(String filename) {
		if(error) 
			output = new StringBuilder();
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
	
	private void semanticError(String errorMsg) {
		parser.semanticError(errorMsg);
		error = true;
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
			this.semanticError("Incompatible types encountered for assignement statement: " + id.type + " := " + expr.type);
		Symbol var = symbolTable.findSymbol(id.lexeme);
		String dereference = var.mode == Symbol.ParameterMode.REFERENCE ? "@" : "";
		// assuming parser will catch undeclared id's so no need to null check
		output.append("pop " + dereference + var.offset + "(D" + var.nestLevel + ")\n" );
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
			case "/":
				operation = "divs";	// 'f' added below
				break;
			case "mod":
				operation = "mods";
				break;
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
		else if(leftRec.type != Symbol.Type.BOOLEAN && leftRec.type != Symbol.Type.STRING) {
			if(leftRec.type == rightRec.type) {
				resultRec.type = relOp ? Symbol.Type.BOOLEAN : leftRec.type;
				boolean floatDiv = opRec.lexeme.equals("/");
				// Check for valid float division operator
				if(leftRec.type == Symbol.Type.FLOAT) {
					if(!floatDiv)
						this.semanticError("Integer divison operator used on float type");
					else
						operation = operation + "f";
				}
				// Check for valid integer division operator
				else if(leftRec.type == Symbol.Type.INTEGER && floatDiv)
					this.semanticError("Float division operator used on integer type");
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
				this.semanticError("Incompatible types encountered for expression: " + leftRec.type + " " + opRec.lexeme + " " + rightRec.type);
		}
		else 
			this.semanticError("Incompatible types encountered for expression: " + leftRec.type + " " + opRec.lexeme + " " + rightRec.type);
	}
	
	/**
	 * Generates code to push variable onto stack
	 * @param idRec
	 */
	public void genPushId(Symbol idRec, Symbol signRec, Symbol.ParameterMode mode) {
		Symbol var = symbolTable.findSymbol(idRec.lexeme);
		if(mode == Symbol.ParameterMode.REFERENCE && var.mode == Symbol.ParameterMode.REFERENCE) {
			// if a reference mode formal parameter is used as an actual reference mode parameter 
			// in a function call inside of a function, we have already calculated its address 
			output.append("push " + var.offset + "(D" + var.nestLevel + ")\n");
		}
		else if(mode == Symbol.ParameterMode.REFERENCE) {
			output.append("push D" + var.nestLevel + "\n");	
			output.append("push #" + var.offset + "\n");
			output.append("adds\n");	// calculate variable address
		}
		else {
			String dereference = var.mode == Symbol.ParameterMode.REFERENCE ? "@" : "";	// if ref mode, dereference
			output.append("push " + dereference + var.offset + "(D" + var.nestLevel + ")\n");
			if(signRec.negative)
				this.genNegOp(idRec);
		}
	}
	
	/**
	 * Generates code for numeric negation
	 * @param factorRec
	 */
	public void genNegOp(Symbol factorRec) {
		if(factorRec.type == Symbol.Type.FLOAT || factorRec.type == Symbol.Type.INTEGER) {
			// choose appropriate negation operator (float, int) --> ("negsf", "negs")
			String negOp = factorRec.type == Symbol.Type.FLOAT ? "negsf\n" : "negs\n";
			output.append(negOp);	// negate top of stack
		}
		else
			this.semanticError("'-' used for non-numeric expression type");
	}
	
	/**
	 * Generates code for boolean negation
	 * @param factorRec
	 */
	public void genNotOp(Symbol factorRec) {
		if(factorRec.type == Symbol.Type.BOOLEAN)
			output.append("nots\n");
		else
			this.semanticError("'not' used for non-boolean expression type");
	}
	
	/**
	 * Generates code to push primitive literal onto stack
	 * @param literalRec
	 */
	public void genPushLiteral(Symbol literalRec, Symbol signRec, Symbol.ParameterMode mode) {
		if(mode != Symbol.ParameterMode.REFERENCE) {
			String literal = literalRec.type == Symbol.Type.STRING ? "\"" + literalRec.lexeme + "\"" : literalRec.lexeme;
			if(signRec.negative)
				literal = "-" + literal;
			output.append("push #" + literal + "\n");	// Push primitive literal
		}
		else
			this.semanticError("Literal values cannot be used as in-out parameters");
	}
	
	/**
	 * Translates "true" to 1 and "false" to 0 and generates code
	 * to push relevant value onto stack
	 * @param boolLit
	 */
	public void genPushBoolLit(Symbol boolLit, Symbol.ParameterMode mode) {
		if(mode != Symbol.ParameterMode.REFERENCE) {
			String bool = boolLit.lexeme.equalsIgnoreCase("true") ? "1" : "0";
			output.append("push #" + bool + "\n");
		}
		else
			this.semanticError("Boolean literals cannot be used as in-out parameters");
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
				this.semanticError("Unsupported parameter type supplied for read");
				break;
		}
		String dereference = var.mode == Symbol.ParameterMode.REFERENCE ? "@" : "";
		output.append(rdOp + dereference + var.offset + "(D" + var.nestLevel + ")\n");
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
			this.semanticError("Expected boolean expression result, got " + exprRec.type);
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
			this.semanticError("Expected boolean expression result, got " + exprRec.type);
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
			output.append("brfs " + repeatRec.label1 + "\n");	// repeat until condition is true
		else
			this.semanticError("Expected boolean expression result, got " + exprRec.type);
	}
	
	/**
	 * Generates labels for the loop, and IR to initialize control variable and to drop begin label
	 * @param ctrlVarRec
	 * @param initialRec
	 * @param forRec
	 */
	public void genBeginFor(Symbol ctrlVarRec, Symbol initialRec, Symbol forRec) {
		forRec.label1 = this.generateLabel();
		forRec.label2 = this.generateLabel();
		this.genAssignStmt(ctrlVarRec, initialRec);
		output.append(forRec.label1 + ":\n");
	}
	
	/**
	 * Generates IR to push control variable to the stack, to compare control variable and final
	 * expression value (left on top of stack by expression), and to branch on false to end label
	 * @param ctrlVarRec
	 * @param forRec
	 * @param finalRec
	 */
	public void genForTest(Symbol ctrlVarRec, Symbol forRec, Symbol finalRec) {
		Symbol ctrlVar = symbolTable.findSymbol(ctrlVarRec.lexeme);
		String cmpOp = forRec.lexeme.equalsIgnoreCase("to") ? "cmpges\n" : "cmples\n";
		output.append("push " + ctrlVar.offset + "(D" + ctrlVar.nestLevel + ")\n");
		output.append(cmpOp);
		output.append("brfs " + forRec.label2 + "\n");

	}
	
	/**
	 * Generates IR to increment/decrement control variable, and to branch to top of for loop
	 * @param ctrlVarRec
	 * @param forRec
	 */
	public void genEndFor(Symbol ctrlVarRec, Symbol forRec) {
		Symbol ctrlVar = symbolTable.findSymbol(ctrlVarRec.lexeme);
		output.append("push " + ctrlVar.offset + "(D" + ctrlVar.nestLevel + ")\n");
		output.append("push #1\n");
		String stepOp = forRec.lexeme.equalsIgnoreCase("to") ? "adds\n" : "subs\n";
		output.append(stepOp);
		output.append("pop " + ctrlVar.offset + "(D" + ctrlVar.nestLevel + ")\n");
		output.append("br " + forRec.label1 + "\n");
		output.append(forRec.label2 + ":\n");
	}
	
	/**
	 * Generates branch to program's 'begin' block
	 * @param progRec
	 */
	public void genBranchMain(Symbol progRec) {
		progRec.label1 = this.generateLabel();
		output.append("br " + progRec.label1 + "\n");
	}
	
	/**
	 * Checks for too many actual parameters specified, and type matches actual
	 * to formal parameters
	 * @param formalParameter
	 * @param actualParameter
	 */
	public void parameterCheck(Symbol formalParameter, Symbol actualParameter) {
		if(formalParameter == null)
			this.semanticError("Too many actual parameters supplied for function");
		else 
			if(formalParameter.type != actualParameter.type) 
				this.semanticError("Actual parameter type does not match formal parameter type");
	}
	
	/**
	 * Checks for expressions being passed as out-mode actual parameters, which
	 * is semantically incorrect
	 * @param mode
	 */
	public void checkForExprAsOutMode(Symbol.ParameterMode mode) {
		if(mode == Symbol.ParameterMode.REFERENCE)
			this.semanticError("Expressions cannot be supplied as out mode parameters");
	}
	
	/**
	 * Generates IR to save space on stack for return value (for functions) and display register
	 * @param funProcSym
	 */
	public void genCallSetup(Symbol funProcSym) {
		if(funProcSym.kind == Symbol.Kind.FUNCTION)
			output.append("add SP #" + Symbol.Type.INTEGER.size + " SP ; return value\n");// save space for return value for functions
		output.append("add SP #" + Symbol.Type.INTEGER.size + " SP ; display register\n");	// save space for callee's display register
		// parameters are pushed (in order) by expression
	}
	
	/**
	 * Generates IR for calling function and (after function completes)
	 * popping display register/parameters off of stack. This should effectively
	 * leave the return value on top of the stack for functions
	 * @param callee
	 */
	public void genCall(Symbol callee, Symbol signRec) {
		// call function
		output.append("call " + callee.label1 + " ; call " + callee.lexeme + "\n");
		// remove parameters and display register from stack
		int popSize = callee.getParameterOffset() + Symbol.Type.INTEGER.size;
		output.append("sub SP #" + popSize + " SP\n");
		if(signRec.negative) 
			genNegOp(callee); // negate return value if function call preceded by '-'
							  // NOTE: 'not' handled by factor() in parser
	}
	
	/**
	 * Generates IR for func/proc label, to reserve space for variables, save old display register
	 * and to set display register to point to start of activation record.
	 * @param funcProcRec
	 */
	public void genBeginFuncOrProcDeclaration(Symbol funcProcRec) {
		if(funcProcRec.kind != Symbol.Kind.MAIN)
			funcProcRec.label1 = this.generateLabel();	// main's label is already generated
		output.append(funcProcRec.label1 + ": ; " + funcProcRec.lexeme + "\n");	// drop label
		if(funcProcRec.kind == Symbol.Kind.MAIN)
			output.append("add SP #" + Symbol.Type.INTEGER.size + " SP\n");	// leave space for display register for main
		int activationRecordSize = funcProcRec.getActivationRecordSize(); 	
		output.append("add SP #" + funcProcRec.variableOffset + " SP\n");	// leave space in AR for func/proc variables
		output.append("mov D" + funcProcRec.nestLevel + " -" + activationRecordSize + "(SP)\n"); // save old DN in AR
		// set display register to point to start of activation record:
		output.append("sub SP #" + activationRecordSize + " D" + funcProcRec.nestLevel + "\n"); 
	}
	
	/**
	 * Stores return value into space reserved for it in the stack, pops
	 * temporary return value (expression result) from stack.
	 * @param funcRec
	 */
	public void genStoreReturnValue(Symbol funcRec) {
		Symbol funcSymbol = symbolTable.findSymbol(funcRec.lexeme);
		// return value stack address = AR size + display register + return address
		int returnValAddr = -(funcSymbol.getActivationRecordSize() + Symbol.Type.INTEGER.size * 2);
		// move stack top (expression result) to spot reserved
		output.append("mov -1(SP) " + returnValAddr + "(SP)\n");
		output.append("sub SP #1 SP\n");
	}
	
	/**
	 * Generates IR to restore display register's old value, pop local variables, and call ret
	 * which pops top of stack (return addr) into the PC.
	 * Also used for main, in which case no 'ret' is generated and ar pop is 1 larger because
	 * ret pops stack
	 * @param funcProcRec
	 */
	public void genEndFuncOrProcDeclaration(Symbol funcProcRec) {
		int activationRecordSize = funcProcRec.getActivationRecordSize();
		output.append("mov -" + activationRecordSize + "(SP) D" + funcProcRec.nestLevel + "\n");
		if(funcProcRec.kind == Symbol.Kind.FUNCTION || funcProcRec.kind == Symbol.Kind.PROCEDURE) {
			// func/proc teardown
			output.append("sub SP #" + funcProcRec.variableOffset + " SP\n");
			output.append("ret ; -- end " + funcProcRec.lexeme + "\n");
		}
		else { // main teardown
			int popSize = funcProcRec.variableOffset + Symbol.Type.INTEGER.size;
			output.append("sub SP #" + popSize + " SP\n");
			output.append("hlt");
		}
	}
}
