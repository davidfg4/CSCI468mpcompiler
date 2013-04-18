import java.util.Collection;
import java.util.LinkedList;

public class MPparser {

	private Token lookahead;
	private Token secondLookahead;
	private MPscanner scanner;
	private SymbolTable symbolTable;
	private SemanticAnalyzer analyzer;
	

	public MPparser(String filename) {
		scanner = new MPscanner();
		symbolTable = new SymbolTable();
		analyzer = new SemanticAnalyzer(this, symbolTable);
		secondLookahead = null;
		scanner.openFile(filename);
		lookahead = scanner.getToken();
		checkForScannerErrors(lookahead);
		systemGoal();
		System.out.println("Successfully parsed! No scanner or parser errors found.");
		analyzer.writeMachineCodeToFile(filename);
	}

	public static void main(String args[]) {
		if (args.length < 1) {
			System.out.println("This program requires one argument, the file to be scanned.");
			System.exit(1);
		} else {
			new MPparser(args[0]);
		}
	}

	private void match(Token.TokenName token) {
		if (lookahead.getToken() != token)
			syntaxErrorExpected(Token.getReverseReservedWord(token));
		if (secondLookahead != null) {
			lookahead = secondLookahead;
			secondLookahead = null;
		} else {
			lookahead = scanner.getToken();
			checkForScannerErrors(lookahead);
		}
	}

	private Token getSecondLookahead()
	{
		if (secondLookahead != null)
			return secondLookahead;
		secondLookahead = scanner.getToken();
		checkForScannerErrors(secondLookahead);
		return secondLookahead;
	}

	private void checkForScannerErrors(Token t) {
		if (t == null)
			return;
		if (t.getToken() == Token.TokenName.MP_ERROR) {
			System.err.println(scanner.getError(t, "Error: Scanner error"));
		} else if (t.getToken() == Token.TokenName.MP_RUN_COMMENT) {
			System.err.println(scanner.getError(t, "Error: Unterminated comment"));
		} else if (t.getToken() == Token.TokenName.MP_RUN_STRING) {
			System.err.println(scanner.getError(t, "Error: Unterminated string"));
		}
	}

	private void syntaxErrorExpected(String expected) {
		System.err.println(scanner.getError(lookahead, "Syntax Error: Expected " +
				expected + ", got '" + lookahead.getLexeme() + "' instead"));
		System.exit(1);
	}
	
	private void syntaxErrorGeneric(String error)
	{
		System.err.println(error);
		System.exit(1);
	}
	
	public void semanticError(String error) {
		System.err.println(scanner.getError(lookahead, "Semantic Error: ") + error);
	}

	/***************************************************************************
	 * NON-TERMINAL FUNCTIONS BELOW
	 ***************************************************************************
	 */

	/**
	 * Pre: SystemGoal is the leftmost nonterminal
	 * Post: SystemGoal has been expanded
	 */
	private void systemGoal() {
		switch (lookahead.getToken()) {
		// rule 1: SystemGoal --> Program eof
		case MP_PROGRAM:
			program();
			match(Token.TokenName.MP_EOF);
			break;
		default:
			syntaxErrorExpected("'program'");
			break;
		}
	}

	/**
	 * Pre: Program is leftmost nonterminal
	 * Post: Program has been expanded
	 */
	private void program() {
		switch (lookahead.getToken()) {
		// rule 2: Program --> ProgramHeading ";" Block "."
		case MP_PROGRAM:
			String programName = programHeading();
			match(Token.TokenName.MP_SCOLON);
			symbolTable.createSymbolTable(programName);
			block();
			match(Token.TokenName.MP_PERIOD);
			//System.out.println(symbolTable);
			symbolTable.deleteSymbolTable();
			break;
		default:
			syntaxErrorExpected("'program'");
			break;
		}

	}

	/**
	 * Pre: ProgramHeading is leftmost nonterminal
	 * Post: ProgramHeading has been expanded
	 */
	private String programHeading() {
		String programName = "";
		switch (lookahead.getToken()) {
		// rule 3: ProgramHeading --> "program" ProgramIdentifier
		case MP_PROGRAM:
			match(Token.TokenName.MP_PROGRAM);
			programName = programIdentifier();
			break;
		default:
			syntaxErrorExpected("'program'");
			break;
		}
		return programName;
	}

	/**
	 * Pre: Block is leftmost nonterminal
	 * Post: Block has been expanded
	 */
	private void block() {
		switch (lookahead.getToken()) {
		// rule 4: Block --> VariableDeclarationPart ProcedureAndFunctionDeclarationPart StatementPart
		case MP_BEGIN:
		case MP_FUNCTION:
		case MP_PROCEDURE:
		case MP_VAR:
			variableDeclarationPart();
			procedureAndFunctionDeclarationPart();
			statementPart();
			break;
		default:
			syntaxErrorExpected("'begin', 'function', 'procedure', or 'var'");
			break;
		}
	}

	/**
	 * Pre: VariableDeclarationPart is leftmost nonterminal
	 * Post: VariableDeclarationPart is expanded
	 */
	private void variableDeclarationPart() {
		switch (lookahead.getToken()) {
		// rule 5: VariableDeclarationPart --> "var" VariableDeclaration ";" VariableDeclarationTail
		case MP_VAR:
			match(Token.TokenName.MP_VAR);
			variableDeclaration();
			match(Token.TokenName.MP_SCOLON);
			variableDeclarationTail();
			break;
		// rule 6: VariableDeclarationPart  --> eplison
		case MP_BEGIN:
		case MP_FUNCTION:
		case MP_PROCEDURE:
			break;
		default:
			syntaxErrorExpected("start of variable declerations('var') or start of program('begin', 'function', 'procedure')");
			break;
		}
	}

	/**
	 * Pre: VariableDeclarationTail is leftmost nonterminal
	 * Post: VariableDeclarationTail is expanded
	 */
	private void variableDeclarationTail() {
		switch (lookahead.getToken()) {
		// rule 7: VariableDeclarationTail --> VariableDeclaration ";" VariableDeclarationTail
		case MP_IDENTIFIER:
			variableDeclaration();
			match(Token.TokenName.MP_SCOLON);
			variableDeclarationTail();
			break;
		// rule 8: VariableDeclarationTail --> epsilon
		case MP_BEGIN:
		case MP_FUNCTION:
		case MP_PROCEDURE:
			break;
		default:
			syntaxErrorExpected("variable declerations('identifier') or start of program('begin', 'frunction', 'procedure')");
			break;
		}
	}

	/**
	 * Pre: VariableDeclaration is leftmost nonterminal
	 * Post: VariableDeclaration is expanded
	 */
	private void variableDeclaration() {
		switch (lookahead.getToken()) {
		// rule 9: VariableDeclaration --> IdentifierList ":" Type
		case MP_IDENTIFIER:
			matchIdentifiersColonType(Symbol.Kind.VARIABLE);
			break;
		default:
			syntaxErrorExpected("'identifier'(1)");
			break;
		}
	}

	/**
	 * Pre: Type is leftmost nonterminal
	 * Post: Type is expanded
	 */
	private Symbol.Type type() {
		Symbol.Type type = null;
		switch (lookahead.getToken()) {
		// rule 10: Type --> "Integer"
		case MP_INTEGER:
			match(Token.TokenName.MP_INTEGER);
			type = Symbol.Type.INTEGER;
			break;
		// rule 11: Type --> "Float"
		case MP_FLOAT:
			match(Token.TokenName.MP_FLOAT);
			type = Symbol.Type.FLOAT;
			break;
		// rule ?: Type --> "Boolean"
		case MP_BOOLEAN:
			match(Token.TokenName.MP_BOOLEAN);
			type = Symbol.Type.BOOLEAN;
			break;
		// rule 109: Type --> "String"
		case MP_STRING:
			match(Token.TokenName.MP_STRING);
			type = Symbol.Type.STRING;
			break;
		default:
			syntaxErrorExpected("variable type, 'integer', 'float', or 'boolean'");
			break;
		}
		return type;
	}

	/**
	 * Pre: ProcedureAndFunctionDeclarationPart is leftmost nonterminal
	 * Post: ProcedureAndFunctionDeclarationPart is expanded
	 */
	private void procedureAndFunctionDeclarationPart() {
		switch (lookahead.getToken()) {
		// rule 12: ProcedureAndFunctionDeclarationPart --> ProcedureDeclaration ProcedureAndFunctionDeclarationPart 
		case MP_PROCEDURE:
			procedureDeclaration();
			procedureAndFunctionDeclarationPart();
			break;
		// rule 13: ProcedureAndFunctionDeclarationPart --> FunctionDeclaration ProcedureAndFunctionDeclarationPart
		case MP_FUNCTION:
			functionDeclaration();
			procedureAndFunctionDeclarationPart();
			break;
		// rule 14: ProcedureAndFunctionDeclarationPart --> epsilon
		case MP_BEGIN:
			break;
		default:
			syntaxErrorExpected("'procedure', 'function', or 'begin'");
		}
	}

	/**
	 * Pre: ProcedureDeclaration is leftmost nonterminal
	 * Post: ProcedureDeclaration is expanded
	 */
	private void procedureDeclaration() {
		switch (lookahead.getToken()) {
		// rule 15: ProcedureDeclaration --> ProcedureHeading ";" Block ";" 
		case MP_PROCEDURE:
			// new Symbol Table created within here:
			procedureHeading();
			match(Token.TokenName.MP_SCOLON);
			block();
			match(Token.TokenName.MP_SCOLON);
			symbolTable.deleteSymbolTable();
			break;
		default:
			syntaxErrorExpected("'procedure'");
		}
	}

	/**
	 * Pre: FunctionDeclaration is leftmost nonterminal
	 * Post: FunctionDeclaration is expanded
	 */
	private void functionDeclaration() {
		switch (lookahead.getToken()) {
		// rule 16: FunctionDeclaration --> FunctionHeading ";" Block ";"
		case MP_FUNCTION:
			// new Symbol Table created within here:
			functionHeading();
			match(Token.TokenName.MP_SCOLON);
			block();
			match(Token.TokenName.MP_SCOLON);
			symbolTable.deleteSymbolTable();
			break;
		default:
			syntaxErrorExpected("'function'");
			break;
		}
	}

	/**
	 * Pre: ProcedureHeading is leftmost nonterminal
	 * Post: ProcedureHeading is expanded
	 */
	private void procedureHeading() {
		String procedureName = "";
		switch (lookahead.getToken()) {
		// rule 17: ProcedureHeading --> "procedure" ProcedureIdentifier OptionalFormalParameterList
		case MP_PROCEDURE:
			match(Token.TokenName.MP_PROCEDURE);
			procedureName = lookahead.getLexeme();
			try {
				symbolTable.insertSymbol(new Symbol(procedureName, Symbol.Kind.PROCEDURE, Symbol.Type.NONE));
				symbolTable.createSymbolTable(procedureName);
			} catch (SymbolTable.SymbolAlreadyExistsException e) {
				syntaxErrorGeneric("Error: Procedure '" + procedureName + "' already exists in the current scope.");
			}
			procedureIdentifier();
			optionalFormalParameterList();
			break;
		default:
			syntaxErrorExpected("'procedure'");
		}
	}

	/**
	 * Pre: FunctionHeading is leftmost nonterminal
	 * Post: ProcedureHeading is expanded
	 */
	private void functionHeading() {
		String functionName = "";
		Symbol.Type type = null;
		Symbol functionSymbol = null;
		switch (lookahead.getToken()) {
		// rule 18:FunctionHeading --> "function" FunctionIdentifier OptionalFormalParameterList Type
		case MP_FUNCTION:
			match(Token.TokenName.MP_FUNCTION);
			functionName = lookahead.getLexeme();
			try {
				functionSymbol = new Symbol(functionName, Symbol.Kind.FUNCTION, Symbol.Type.NONE);
				symbolTable.insertSymbol(functionSymbol);
				symbolTable.createSymbolTable(functionName);
			} catch (SymbolTable.SymbolAlreadyExistsException e) {
				syntaxErrorGeneric("Error: Function '" + functionName + "' already exists in the current scope.");
			}
			functionIdentifier(new Symbol());
			optionalFormalParameterList();
			match(Token.TokenName.MP_COLON);
			type = type();
			functionSymbol.type = type;
			break;
		default:
			syntaxErrorExpected("'function'");
			break;
		}
	}

	/**
	 * Pre: OptionalFormalParameterList is leftmost nonterminal
	 * Post: OptionalFormatlParameterList is expanded
	 */
	private void optionalFormalParameterList() {
		switch (lookahead.getToken()) {
		// rule 19: OptionalFormalParameterList --> "(" FormalParameterSection FormalParameterSectionTail ")"
		case MP_LPAREN:
			match(Token.TokenName.MP_LPAREN);
			formalParameterSection();
			formalParameterSectionTail();
			match(Token.TokenName.MP_RPAREN);
			break;
		// rule 20: OptionalFormalParameterList --> epsilon
		case MP_SCOLON:
		case MP_COLON:
			break;
		default:
			syntaxErrorExpected("parameter list in parentheses or start of function");
			break;
		}
	}

	/**
	 * Pre: FormalParameterSectionTail is leftmost nonterminal
	 * Post: FormatParameterSectionTail
	 */
	private void formalParameterSectionTail() {
		switch (lookahead.getToken()) {
		// rule 21: FormalParameterSectionTail --> ";" FormalParameterSection FormalParameterSectionTail
		case MP_SCOLON:
			match(Token.TokenName.MP_SCOLON);
			formalParameterSection();
			formalParameterSectionTail();
			break;
		// rule 22: FormalParameterSectionTail --> epsilon
		case MP_RPAREN:
			break;
		default:
			syntaxErrorExpected("';' or ')'");
			break;
		}
	}

	/**
	 * Pre: FormalParameterSection is leftmost nonterminal
	 * Post: FormalParameterSection is expanded
	 */
	private void formalParameterSection() {
		switch (lookahead.getToken()) {
		// rule 23: FormalParameterSection --> ValueParameterSection
		case MP_IDENTIFIER:
			valueParameterSection();
			break;
		// rule 24: FormalParameterSection --> VariableParameterSection
		case MP_VAR:
			variableParameterSection();
			break;
		default:
			syntaxErrorExpected("'identifier' or 'var'");
			break;
		}
	}

	/**
	 * Pre: ValueParameterSection is leftmost nonterminal
	 * Post: ValueParameterSection is expanded
	 */
	private void valueParameterSection() {
		switch (lookahead.getToken()) {
		// rule 25: ValueParameterSection --> IdentifierList ":" Type
		case MP_IDENTIFIER:
			matchIdentifiersColonType(Symbol.Kind.PARAMETER);
			break;
		default:
			syntaxErrorExpected("'identifier'(2)");
			break;
		}
	}

	/**
	 * Pre: VariableParameterSection is leftmost nonterminal
	 * Post: VariableParameterSection is expanded
	 */
	private void variableParameterSection() {
		switch (lookahead.getToken()) {
		// rule 26: VariableParameterSection --> "var" IdentifierList ":" Type
		case MP_VAR:
			match(Token.TokenName.MP_VAR);
			matchIdentifiersColonType(Symbol.Kind.PARAMETER);
			break;
		default:
			syntaxErrorExpected("'var'");
			break;
		}
	}
	
	private void matchIdentifiersColonType(Symbol.Kind kind)
	{
		Collection<String> identifiers = identifierList();
		match(Token.TokenName.MP_COLON);
		Symbol.Type type = type();
		for (String identifier : identifiers)
		{
			try {
				symbolTable.insertSymbol(new Symbol(identifier, kind, type));
			} catch (SymbolTable.SymbolAlreadyExistsException e) {
				syntaxErrorGeneric("Error: Identifier '" + identifier + "' already exists in the current scope.");
			}
		}
	}

	/**
	 * Pre: StatementPart is leftmost nonterminal
	 * Post: StatementPart is expanded
	 */
	private void statementPart() {
		switch (lookahead.getToken()) {
		// rule 27: StatementPart --> CompoundStatement
		case MP_BEGIN:
			compoundStatement();
			break;
		default:
			syntaxErrorExpected("'begin'");
			break;
		}
	}

	/**
	 * Pre: CompoundStatement is leftmost nonterminal
	 * Post: CompoundStatement is expanded
	 */
	private void compoundStatement() {
		switch (lookahead.getToken()) {
		// rule 28: CompoundStatement --> "begin" StatementSequence "end"
		case MP_BEGIN:
			match(Token.TokenName.MP_BEGIN);
			statementSequence();
			match(Token.TokenName.MP_END);
			break;
		default:
			syntaxErrorExpected("'begin'");
			break;
		}
	}

	/**
	 * Pre: StatementSequence is leftmost nonterminal
	 * Post: StatementSequence is expanded
	 */
	private void statementSequence() {
		switch (lookahead.getToken()) {
		// rule 29: StatementSequence --> Statement StatementTail
		case MP_SCOLON:
		case MP_BEGIN:
		case MP_END:
		case MP_FLOAT_LIT:
		case MP_IDENTIFIER:
		case MP_IF:
		case MP_READ:
		case MP_REPEAT:
		case MP_UNTIL:
		case MP_WHILE:
		case MP_WRITE:
		case MP_WRITELN:
			statement();
			statementTail();
			break;
		default:
			syntaxErrorExpected("start of statement sequence");
			break;
		}
	}

	/**
	 * Pre: StatementTail is leftmost nonterminal
	 * Post: StatementTail is expanded
	 */
	private void statementTail() {
		switch (lookahead.getToken()) {
		// rules 30: StatementTail --> ";" Statement StatementTail
		case MP_SCOLON:
			match(Token.TokenName.MP_SCOLON);
			statement();
			statementTail();
			break;
		// rule 31: StatementTail --> epsilon
		case MP_END:
		case MP_UNTIL:
			break;
		default:
			syntaxErrorExpected("statement tail(';', 'end, 'until')");
			break;
		}
	}

	/**
	 * Pre: Statement is leftmost nonterminal
	 * Post: Statement is expanded
	 */
	private void statement() {
		switch (lookahead.getToken()) {
		// rule 32: Statement --> EmptyStatement
		case MP_SCOLON:
		case MP_ELSE:
		case MP_END:
		case MP_UNTIL:
			emptyStatement();
			break;
		// rules 33: Statement --> CompoundStatement
		case MP_BEGIN:
			compoundStatement();
			break;
		// rule 34: Statement --> ReadStatement
		case MP_READ:
			readStatement();
			break;
		// rule 35: Statement --> WriteStatement
		case MP_WRITE:
		case MP_WRITELN:
			writeStatement();
			break;
		// rule 36: Statement --> AssignmentStatement
		// rule 41: Statement --> ProcedureStatement
		case MP_IDENTIFIER:
			if (getSecondLookahead().getToken() == Token.TokenName.MP_ASSIGN)
			{
				assignmentStatement();
			} else {
				procedureStatement();
			}
			break;
		// rule 37: Statement --> IfStatement
		case MP_IF:
			ifStatement();
			break;
		// rule 38: Statement --> WhileStatement
		case MP_WHILE:
			whileStatement();
			break;
		// rule 39: Statement --> RepeatStatement
		case MP_REPEAT:
			repeatStatement();
			break;
		// rule 40: Statement --> ForStatement
		case MP_FOR:
			forStatement();
			break;
		default:
			syntaxErrorExpected("statement");
			break;
		}
	}

	/**
	 * Pre: EmptyStatement is leftmost nonterminal
	 * Post: EmptyStatement is expanded
	 */
	private void emptyStatement() {
		switch (lookahead.getToken()) {
		// EmptyStatement --> epsilon
		case MP_SCOLON:
		case MP_ELSE:
		case MP_END:
		case MP_UNTIL:
			break;
		default:
			syntaxErrorExpected("empty statement");
			break;
		}
	}

	/**
	 * Pre: ReadStatement is leftmost nonterminal
	 * Post: ReadStatement is expanded
	 */
	private void readStatement() {
		switch (lookahead.getToken()) {
		// rule 43: ReadStatement --> "read" "(" ReadParameter ReadParameterTail ")"
		case MP_READ:
			match(Token.TokenName.MP_READ);
			match(Token.TokenName.MP_LPAREN);
			readParameter();
			readParameterTail();
			match(Token.TokenName.MP_RPAREN);
			break;
		default:
			syntaxErrorExpected("'read'");
			break;
		}
	}

	/**
	 * Pre: ReadParameterTail is leftmost nonterminal
	 * Post: ReadParameterTail is expanded
	 */
	private void readParameterTail() {
		switch (lookahead.getToken()) {
		// rule 44: ReadParameterTail --> "," ReadParameter ReadParameterTail
		case MP_COMMA:
			match(Token.TokenName.MP_COMMA);
			readParameter();
			readParameterTail();
			break;
		// rule 45: ReadParameterTail --> epsilon
		case MP_RPAREN:
			break;
		default: 
			syntaxErrorExpected("',' or ')'");
			break;
		}
	}

	/**
	 * Pre: ReadParameter is leftmost nonterminal
	 * Post: ReadParameter is expanded
	 */
	private void readParameter() {
		Symbol readParamRec = new Symbol();
		switch (lookahead.getToken()) {
		// rule 46: ReadParameter --> VariableIdentifier
		case MP_IDENTIFIER:
			variableIdentifier(readParamRec);
			analyzer.genReadStmt(readParamRec);
			break;
		default: 
			syntaxErrorExpected("'identifier'(3)");
			break;
		}
	}

	/**
	 * Pre: WriteStatement is leftmost nonterminal
	 * Post: WriteStatement is expanded
	 */
	private void writeStatement() {
		switch (lookahead.getToken()) {
		// rule 47: WriteStatement --> "write" "(" WriteParameter WriteParameterTail ")"
		// rule 111: WriteStatement --> "writeln" "(" WriteParameter WriteParameterTail ")"
		case MP_WRITE:
		case MP_WRITELN:
			boolean isWriteLn = lookahead.getToken() == Token.TokenName.MP_WRITELN;
			if(isWriteLn)
				match(Token.TokenName.MP_WRITELN);
			else
				match(Token.TokenName.MP_WRITE);
			match(Token.TokenName.MP_LPAREN);
			writeParameter(isWriteLn);
			writeParameterTail(isWriteLn);
			match(Token.TokenName.MP_RPAREN);
			break;
		default:
			syntaxErrorExpected("'write' or 'writeln'");
			break;
		}
	}

	/**
	 * Pre: WriteParameterTail is leftmost nonterminal
	 * Post: WriteParameterTail is expanded
	 */
	private void writeParameterTail(boolean writeLn) {
		switch (lookahead.getToken()) {
		// rule 48: WriteParameterTail --> "," WriteParameter WriteParameterTail
		case MP_COMMA:
			match(Token.TokenName.MP_COMMA);
			writeParameter(writeLn);
			writeParameterTail(writeLn);
			break;
		// rule 49: WriteParameterTail --> epsilon
		case MP_RPAREN:
			break;
		default:
			syntaxErrorExpected("',' or ')'");
			break;
		}
	}

	/**
	 * Pre: WriteParameter is leftmost nonterminal
	 * Post: WriteParameter is expanded
	 */
	private void writeParameter(boolean writeLn) {
		switch (lookahead.getToken()) {
		// rule 50: WriteParameter --> OrdinalExpression
		case MP_LPAREN:
		case MP_PLUS:
		case MP_MINUS:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_FLOAT_LIT:
		case MP_FIXED_LIT:
		case MP_STRING_LIT:
		case MP_NOT:
			ordinalExpression();
			analyzer.genWriteStmt(writeLn);	// value of param expression should be on stack top
			break;
		default:
			syntaxErrorExpected("an expression");
			break;
		}
	}

	/**
	 * Pre: AssignmentStatement is leftmost nonterminal
	 * Post: AssignmentStatement is expanded
	 */
	private void assignmentStatement() {
		Symbol idRecord = new Symbol();
		Symbol exprRecord = new Symbol();
		switch (lookahead.getToken()) {
		// rule 51: AssignmentStatement --> VariableIdentifier ":=" Expression
		// rule 52: AssignmentStatement --> FunctionIdentifier ":=" Expression
		case MP_IDENTIFIER:
			Symbol assignedVar = symbolTable.findSymbol(lookahead.getLexeme());
			if (assignedVar == null) {
				semanticError("Undeclared identifier.");
			} else if (assignedVar.kind == Symbol.Kind.VARIABLE  || assignedVar.kind == Symbol.Kind.PARAMETER) {
				// rule 51: AssignmentStatement --> VariableIdentifier ":=" Expression
				variableIdentifier(idRecord);
				match(Token.TokenName.MP_ASSIGN);
				expression(exprRecord, new Symbol());
				analyzer.genAssignStmt(idRecord, exprRecord);
			} else if (assignedVar.kind == Symbol.Kind.FUNCTION) {
				// rule 52: AssignmentStatement --> FunctionIdentifier ":=" Expression
				// personal note: how do you assign something to a function? what is going on here?
				functionIdentifier(idRecord);
				match(Token.TokenName.MP_ASSIGN);
				expression(exprRecord, new Symbol());
				analyzer.genAssignStmt(idRecord, exprRecord);	// ?? 
			}
			break;
		default:
			syntaxErrorExpected("a variable or function identifier");
			break;
		}
	}

	/**
	 * Pre: ifStatement is leftmost nonterminal
	 * Post: ifStatement is expanded
	 */
	private void ifStatement() {
		Symbol exprRec = new Symbol();
		Symbol ifRec = new Symbol();
		switch (lookahead.getToken()) {
		// rule 53: IfStatement --> "if" BooleanExpression "then" Statement OptionalElsePart
		case MP_IF:
			match(Token.TokenName.MP_IF);
			booleanExpression(exprRec);
			analyzer.genIfTest(ifRec, exprRec);
			match(Token.TokenName.MP_THEN);
			statement();
			optionalElsePart(ifRec);
			analyzer.genFinishIf(ifRec);
			break;
		default:
			syntaxErrorExpected("'if'");
			break;
		}
	}

	private void optionalElsePart(Symbol optElseRec) {
		switch (lookahead.getToken()) {
		// It is possible for "case MP_ELSE" to be epsilon, but for simplicity,
		// and to always match the closest if statement, 'else' always matches here.
		// rule 54: OptionalElsePart --> "else" Statement
		case MP_ELSE:
			match(Token.TokenName.MP_ELSE);
			analyzer.processElse(optElseRec);
			statement();
			break;
		// rule 55: OptionalElsePart --> epsilon
		case MP_SCOLON:
		case MP_END:
		case MP_UNTIL:
			analyzer.processNoElse(optElseRec);
			break;
		default:
			syntaxErrorExpected("'else' or end of statement");
			break;
		}
	}

	/**
	 * Pre: RepeatStatement is leftmost nonterminal
	 * Post: RepeatStatement is expanded
	 */
	private void repeatStatement() {
		Symbol exprRec = new Symbol();
		Symbol repeatRec = new Symbol();
		switch (lookahead.getToken()) {
		// rule 56: RepeatStatement --> "repeat" StatementSequence "until" BooleanExpression
		case MP_REPEAT:
			match(Token.TokenName.MP_REPEAT);
			analyzer.genBeginRepeat(repeatRec);
			statementSequence();
			match(Token.TokenName.MP_UNTIL);
			booleanExpression(exprRec);
			analyzer.genEndRepeat(repeatRec, exprRec);
			break;
		default:
			syntaxErrorExpected("'repeat'");
			break;
		}
	}

	/**
	 * Pre: WhileStatement is leftmost nonterminal
	 * Post: WhileStatement is expanded
	 */
	private void whileStatement() {
		Symbol exprRec = new Symbol();
		Symbol whileRec = new Symbol();
		switch (lookahead.getToken()) {
		// rule 57: WhileStatement --> "while" BooleanExpression "do" Statement
		case MP_WHILE:
			match(Token.TokenName.MP_WHILE);
			analyzer.genBeginWhile(whileRec);
			booleanExpression(exprRec);
			analyzer.genWhileTest(whileRec, exprRec);
			match(Token.TokenName.MP_DO);
			statement();
			analyzer.genEndWhile(whileRec);
			break;
		default:
			syntaxErrorExpected("'while'");
			break;
		}
	}

	/**
	 * Pre: ForStatement is leftmost nonterminal
	 * Post: ForStatement is expanded
	 */
	private void forStatement() {
		switch (lookahead.getToken()) {
		// rule 58: ForStatement --> "for" ControlVariable ":=" InitialValue StepValue FinalValue "do" Statement
		case MP_FOR:
			match(Token.TokenName.MP_FOR);
			controlVariable();
			match(Token.TokenName.MP_ASSIGN);
			initialValue();
			stepValue();
			finalValue();
			match(Token.TokenName.MP_DO);
			statement();
			break;
		default:
			syntaxErrorExpected("'for'");
			break;
		}
	}

	/**
	 * Pre: ControlVariable is leftmost nonterminal
	 * Post: ControlVariable is expanded
	 */
	private void controlVariable() {
		switch (lookahead.getToken()) {
		// rule 59: ControlVariable --> VariableIdentifier
		case MP_IDENTIFIER:
			variableIdentifier(new Symbol());
			break;
		default:
			syntaxErrorExpected("'identifier'(4)");
			break;
		}
	}

	/**
	 * Pre: InitialValue is leftmost nonterminal
	 * Post: InitialValue is expanded
	 */
	private void initialValue() {
		switch (lookahead.getToken()) {
		// rule 60: InitialValue --> OrdinalExpression
		case MP_LPAREN:
		case MP_PLUS:
		case MP_MINUS:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_NOT:
			ordinalExpression();
			break;
		default: 
			syntaxErrorExpected("an expression");
			break;
		}
	}

	/**
	 * Pre: StepValue is leftmost nonterminal
	 * Post: StepValue is expanded
	 */
	private void stepValue() {
		switch (lookahead.getToken()) {
		// rule 61: StepValue --> "to"
		case MP_TO:
			match(Token.TokenName.MP_TO);
		// rule 62: StepValue --> "downto"
		case MP_DOWNTO:
			match(Token.TokenName.MP_DOWNTO);
			break;
		default:
			syntaxErrorExpected("'to' or 'downto'");
			break;
		}
	}

	/**
	 * Pre: FinalValue is leftmost nonterminal
	 * Post: FinalValue is expanded
	 */
	private void finalValue() {
		switch (lookahead.getToken()) {
		// rule 63: FinalValue --> OrdinalExpression
		case MP_LPAREN:
		case MP_PLUS:
		case MP_MINUS:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_NOT:
			ordinalExpression();
			break;
		default:
			syntaxErrorExpected("an expression");
			break;
		}
	}

	/**
	 * Pre: ProcedureStatement is leftmost nonterminal
	 * Post: ProcedureStatement is expanded
	 */
	private void procedureStatement() {
		switch (lookahead.getToken()) {
		// rule 64: ProcedureStatement --> ProcedureIdentifier OptionalActualParameterList
		case MP_IDENTIFIER:
			procedureIdentifier();
			optionalActualParameterList();
			break;
		default:
			syntaxErrorExpected("a procedure identifier");
			break;
		}
	}

	/**
	 * Pre: OptionalActualParameterList is leftmost nonterminal
	 * Post: OptionalActualParameterList is expanded
	 */
	private void optionalActualParameterList() {
		switch (lookahead.getToken()) {
		// rule 65: OptionalActualParameterList --> "(" ActualParameter ActualParameterTail ")"
		case MP_LPAREN:
			match(Token.TokenName.MP_LPAREN);
			actualParameter();
			actualParameterTail();
			match(Token.TokenName.MP_RPAREN);
			break;
		// OptionalActualParameterList --> epsilon
		case MP_COMMA:
		case MP_SCOLON:
		case MP_RPAREN:
		case MP_EQUAL:
		case MP_LTHAN:
		case MP_GTHAN:
		case MP_LEQUAL:
		case MP_GEQUAL:
		case MP_NEQUAL:
		case MP_PLUS:
		case MP_MINUS:
		case MP_TIMES:
		case MP_AND:
		case MP_DIV:
		case MP_DO:
		case MP_DOWNTO:
		case MP_ELSE:
		case MP_END:
		case MP_MOD:
		case MP_OR:
		case MP_THEN:
		case MP_TO:
		case MP_UNTIL:
			break;
		default:
			syntaxErrorExpected("procedure parameters in parentheses, another statememt, or 'end'");
			break;
		}
	}

	/**
	 * Pre: ActualParameterTail is leftmost nonterminal
	 * Post: ActualParameterTail is expanded
	 */
	private void actualParameterTail() {
		switch (lookahead.getToken()) {
		// rule 67: ActualParameterTail --> "," ActualParameter ActualParameterTail
		case MP_COMMA:
			match(Token.TokenName.MP_COMMA);
			actualParameter();
			actualParameterTail();
			break;
		// rule 68: ActualParameterTail --> epsilon
		case MP_RPAREN:
			break;
		default:
			syntaxErrorExpected("another parameter (',') or the end of the parameter list (')')");
			break;
		}
	}

	/**
	 * Pre: ActualParameter is leftmost nonterminal
	 * Post: ActualParameter is expanded
	 */
	private void actualParameter() {
		switch (lookahead.getToken()) {
		// rule 69: ActualParameter --> OrdinalExpression
		case MP_LPAREN:
		case MP_PLUS:
		case MP_MINUS:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_FLOAT_LIT:
		case MP_FIXED_LIT:
		case MP_TRUE:
		case MP_FALSE:
		case MP_STRING_LIT:
		case MP_NOT:
			ordinalExpression();
			break;
		default:
			syntaxErrorExpected("an expression");
			break;
		}
	}

	/**
	 * Pre: Expression is leftmost nonterminal
	 * Post: Expression is expanded
	 */
	private void expression(Symbol exprRec, Symbol signRec) {
		switch (lookahead.getToken()) {
		// rule 70: Expression --> SimpleExpression OptionalRelationalPart
		case MP_LPAREN:
		case MP_PLUS:
		case MP_MINUS:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_FLOAT_LIT:
		case MP_FIXED_LIT:
		case MP_TRUE:
		case MP_FALSE:
		case MP_STRING_LIT:
		case MP_NOT:
			simpleExpression(exprRec, signRec);
			optionalRelationalPart(exprRec);
			break;
		default:
			syntaxErrorExpected("an expression");
			break;
		}
	}

	/**
	 * Pre: OptionalRelationalPart is leftmost nonterminal
	 * Post: OptionalRelationalPart is expanded
	 */
	private void optionalRelationalPart(Symbol leftSideRec) {
		Symbol relOp = new Symbol();
		Symbol rightSideRec = new Symbol();
		Symbol resultRec = new Symbol();
		switch (lookahead.getToken()) {
		// rule 71: OptionalRelationalPart --> RelationalOperator SimpleExpression
		case MP_EQUAL:
		case MP_LTHAN:
		case MP_GTHAN:
		case MP_LEQUAL:
		case MP_GEQUAL:
		case MP_NEQUAL:
			relationalOperator(relOp);
			simpleExpression(rightSideRec, new Symbol());
			analyzer.genArithmetic(leftSideRec, relOp, rightSideRec, resultRec);
			analyzer.copy(resultRec, leftSideRec);
			break;
		// rule 72: OptionalRelationalPart --> epsilon
		case MP_COMMA:
		case MP_SCOLON:
		case MP_RPAREN:
		case MP_DO:
		case MP_DOWNTO:
		case MP_ELSE:
		case MP_END:
		case MP_THEN:
		case MP_TO:
		case MP_UNTIL:
			break;
		default:
			syntaxErrorExpected("a relational operator ('>', '<', etc)");
			break;
		}
	}

	/**
	 * Pre: RelationalOperator is leftmost nonterminal
	 * Post: RelationalOperator is expanded
	 */
	private void relationalOperator(Symbol relOp) {
		relOp.lexeme = lookahead.getLexeme();
		switch (lookahead.getToken()) {
		// rule 73: RelationalOperator --> "="
		case MP_EQUAL:
			match(Token.TokenName.MP_EQUAL);
			break;
		// rule 74: RelationalOperator --> "<"
		case MP_LTHAN:
			match(Token.TokenName.MP_LTHAN);
			break;
		// rule 75: RelationalOperator --> ">"
		case MP_GTHAN:
			match(Token.TokenName.MP_GTHAN);
			break;
		// rule 76: RelationalOperator --> "<="
		case MP_LEQUAL:
			match(Token.TokenName.MP_LEQUAL);
			break;
		// rule 77: RelationalOperator --> ">="
		case MP_GEQUAL:
			match(Token.TokenName.MP_GEQUAL);
			break;
		// rule 76: RelationalOperator --> "<>"
		case MP_NEQUAL:
			match(Token.TokenName.MP_NEQUAL);
			break;
		default:
			syntaxErrorExpected("a relational operator ('>', '<', etc)");
			break;
		}
	}

	/**
	 * Pre: SimpleExpression is leftmost nonterminal
	 * Post: SimpleExpression is expanded
	 */
	private void simpleExpression(Symbol exprRec, Symbol signRec) {
		Symbol termRec = new Symbol();
		Symbol termTailRec = new Symbol();
		switch (lookahead.getToken()) {
		// rule 79: SimpleExpression --> OptionalSign Term TermTail
		case MP_LPAREN:
		case MP_PLUS:
		case MP_MINUS:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_FLOAT_LIT:
		case MP_FIXED_LIT:
			optionalSign(signRec);
		case MP_NOT:					// No sign for true, false, not..
		case MP_TRUE:
		case MP_FALSE:
		case MP_STRING_LIT:
			term(termRec, signRec);
			analyzer.copy(termRec, termTailRec); 	// pass LHS (termRec) type down
			termTail(termTailRec);
			analyzer.copy(termTailRec, exprRec);	// pass result (termTailRec) up
			break;
		default:
			syntaxErrorExpected("an expression");
			break;
		}
	}

	/**
	 * Pre: TermTail is leftmost nonterminal
	 * Post: TermTail is expanded
	 */
	private void termTail(Symbol leftSideRec) {
		Symbol rightSideRec = new Symbol();
		Symbol operatorRec = new Symbol();
		Symbol resultRec = new Symbol();	
		switch (lookahead.getToken()) {
		// rule 80: TermTail --> AddingOperator Term TermTail
		case MP_PLUS:
		case MP_MINUS:
		case MP_OR:
			addingOperator(operatorRec);
			term(rightSideRec, new Symbol());
			analyzer.genArithmetic(leftSideRec, operatorRec, rightSideRec, resultRec);
			termTail(resultRec);
			analyzer.copy(resultRec, leftSideRec);	// Pass sub-expression (resultRec) type up
			break;
		// rule 81: TermTail --> epsilon
		case MP_COMMA:
		case MP_SCOLON:
		case MP_RPAREN:
		case MP_EQUAL:
		case MP_LTHAN:
		case MP_GTHAN:
		case MP_LEQUAL:
		case MP_GEQUAL:
		case MP_NEQUAL:
		case MP_DO:
		case MP_DOWNTO:
		case MP_ELSE:
		case MP_END:
		case MP_THEN:
		case MP_TO:
		case MP_UNTIL:
			break;
		default:
			syntaxErrorExpected("term tail");
			break;
		}
	}

	/**
	 * Pre: OptionalSign is leftmost nonterminal
	 * Post: OptionalSign is expanded
	 */
	private void optionalSign(Symbol signRec) {
		switch (lookahead.getToken()) {
		// rule 82: OptionalSign --> "+"
		case MP_PLUS:
			match(Token.TokenName.MP_PLUS);
			break;
		// rule 83: OptionalSign --> "-"
		case MP_MINUS:
			match(Token.TokenName.MP_MINUS);
			signRec.negative = true;
			break;
		// rule 84: OptionalSign --> epsilon
		case MP_LPAREN:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_FLOAT_LIT:
		case MP_FIXED_LIT:
		case MP_NOT:
			break;
		default:
			syntaxErrorExpected("sign ('+' or '-') or start of term");
			break;
		}
	}

	/**
	 * Pre: AddingOperator is leftmost nonterminal
	 * Post: AddingOperator is expanded
	 */
	private void addingOperator(Symbol operatorRec) {
		operatorRec.lexeme = lookahead.getLexeme();	// return operator type
		switch (lookahead.getToken()) {
		// rule 85: AddingOperator --> "+"
		case MP_PLUS:
			match(Token.TokenName.MP_PLUS);
			break;
		// rule 86: AddingOperator --> "-"
		case MP_MINUS:
			match(Token.TokenName.MP_MINUS);
			break;
		// rule 87: AddingOperator --> "or"
		case MP_OR:
			match(Token.TokenName.MP_OR);
			break;
		default:
			syntaxErrorExpected("'+', '-', or 'or'");
			break;
		}
	}

	/**
	 * Pre: Term is leftmost nonterminal
	 * Post: Term is expanded
	 */
	private void term(Symbol termRec, Symbol signRec) {
		switch (lookahead.getToken()) {
		// rule 88: Term --> Factor FactorTail
		case MP_LPAREN:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_FLOAT_LIT:
		case MP_FIXED_LIT:
		case MP_TRUE:
		case MP_FALSE:
		case MP_STRING_LIT:
		case MP_NOT:
			factor(termRec, signRec);
			factorTail(termRec);
			break;
		default:
			syntaxErrorExpected("a term");
			break;
		}
	}

	/**
	 * Pre: FactorTail is leftmost nonterminal
	 * Post: FactorTail is expanded
	 */
	private void factorTail(Symbol leftSideRec) {
		Symbol rightSideRec = new Symbol();
		Symbol operatorRec = new Symbol();
		Symbol resultRec = new Symbol();
		switch (lookahead.getToken()) {
		// rule 89: FactorTail --> MultiplyingOperator Factor FactorTail
		case MP_TIMES:
		case MP_AND:
		case MP_DIV:
		case MP_MOD:
			multiplyingOperator(operatorRec);
			factor(rightSideRec, new Symbol());
			analyzer.genArithmetic(leftSideRec, operatorRec, rightSideRec, resultRec);
			factorTail(resultRec);
			analyzer.copy(resultRec, leftSideRec);	// Pass sub-expression (resultRec) type up
			break;
		// rule 90: FactorTail --> epsilon
		case MP_COMMA:
		case MP_SCOLON:
		case MP_RPAREN:
		case MP_EQUAL:
		case MP_LTHAN:
		case MP_GTHAN:
		case MP_LEQUAL:
		case MP_GEQUAL:
		case MP_NEQUAL:
		case MP_PLUS:
		case MP_MINUS:
		case MP_DO:
		case MP_DOWNTO:
		case MP_ELSE:
		case MP_END:
		case MP_OR:
		case MP_THEN:
		case MP_TO:
		case MP_UNTIL:
			break;
		default:
			syntaxErrorExpected("'*', 'and', '/', '%', or something else");
			break;
		}
	}

	/**
	 * Pre: MultiplyingOperator is leftmost nonterminal
	 * Post: MultiplnyingOperator is expanded
	 */
	private void multiplyingOperator(Symbol operatorRec) {
		operatorRec.lexeme = lookahead.getLexeme();	// return operator type
		switch (lookahead.getToken()) {
		// rule 91: MultiplyingOperator --> "*"
		case MP_TIMES:
			match(Token.TokenName.MP_TIMES);
			break;
		// rule 92: MultiplyingOperator --> "div"
		case MP_DIV:
			match(Token.TokenName.MP_DIV);
			break;
		// rule 93: MultiplyingOperator --> "mod"
		case MP_MOD:
			match(Token.TokenName.MP_MOD);
			break;
		// rule 94: MultiplyingOperator --> "and"
		case MP_AND:
			match(Token.TokenName.MP_AND);
			break;
		default:
			syntaxErrorExpected("'*', 'and', '/', or '%'");
			break;
		}
	}

	/**
	 * Pre: Factor is leftmost nonterminal
	 * Post: Factor is expanded
	 */
	private void factor(Symbol factorRec, Symbol signRec) {
		switch (lookahead.getToken()) {
		// Factor --> UnsignedInteger
		case MP_INTEGER_LIT:
			factorRec.type = Symbol.Type.INTEGER;
			factorRec.lexeme = lookahead.getLexeme();
			analyzer.genPushLiteral(factorRec, signRec);
			match(Token.TokenName.MP_INTEGER_LIT);
			break;
		// rule 113: Factor --> UnsignedFloat
		case MP_FLOAT_LIT:
			factorRec.type = Symbol.Type.FLOAT;
			factorRec.lexeme = lookahead.getLexeme();
			analyzer.genPushLiteral(factorRec, signRec);
			match(Token.TokenName.MP_FLOAT_LIT);
			break;
		// rule 113: Factor --> UnsignedFloat
		case MP_FIXED_LIT: 
			factorRec.type = Symbol.Type.FLOAT;
			factorRec.lexeme = lookahead.getLexeme();
			analyzer.genPushLiteral(factorRec, signRec);
			match(Token.TokenName.MP_FIXED_LIT);
			break;
		// rule 114: Factor --> StringLiteral
		case MP_STRING_LIT:
			factorRec.type = Symbol.Type.STRING;
			factorRec.lexeme = lookahead.getLexeme();
			analyzer.genPushLiteral(factorRec, new Symbol());
			match(Token.TokenName.MP_STRING_LIT);
			break;
		// rule 115: Factor --> "True"
		case MP_TRUE:
			factorRec.type = Symbol.Type.BOOLEAN;
			factorRec.lexeme = lookahead.getLexeme();
			analyzer.genPushBoolLit(factorRec);
			match(Token.TokenName.MP_TRUE);
			break;
		// rule 116: Factor --> "False"
		case MP_FALSE:
			factorRec.type = Symbol.Type.BOOLEAN;
			factorRec.lexeme = lookahead.getLexeme();
			analyzer.genPushBoolLit(factorRec);
			match(Token.TokenName.MP_FALSE);
			break;
		// Factor --> "not" Factor
		case MP_NOT:
			factorRec.negative = true;
			match(Token.TokenName.MP_NOT);
			factor(factorRec, new Symbol());
			analyzer.genNegOp(factorRec);
			break;
		// Factor --> "(" Expression ")"
		case MP_LPAREN:
			match(Token.TokenName.MP_LPAREN);
			expression(factorRec, new Symbol());
			match(Token.TokenName.MP_RPAREN);
			if(signRec.negative)
				analyzer.genNegOp(factorRec);
			break;
		case MP_IDENTIFIER:
			Symbol assignedVar = symbolTable.findSymbol(lookahead.getLexeme());
			if (assignedVar == null) {
				semanticError("Undeclared identifier.");
			} else if (assignedVar.kind == Symbol.Kind.VARIABLE || assignedVar.kind == Symbol.Kind.PARAMETER) {
				// Factor --> VariableIdentifier
				variableIdentifier(factorRec);
				analyzer.genPushId(factorRec, signRec);
			} else if (assignedVar.kind == Symbol.Kind.FUNCTION) {
				// Factor --> FunctionIdentifier OptionalActualParameterList
				functionIdentifier(new Symbol());
				optionalActualParameterList();
			}
			break;
		default:
			syntaxErrorExpected("a factor");
			break;
		}
	}

	/**
	 * Pre: ProgramIdentifier is leftmost nonterminal
	 * Post: ProgramIdentifier is expanded
	 */
	private String programIdentifier() {
		String programName = "";
		switch (lookahead.getToken()) {
		// rule 100: ProgramIdentifier --> Identifier
		case MP_IDENTIFIER:
			programName = lookahead.getLexeme();
			match(Token.TokenName.MP_IDENTIFIER);
			break;
		default:
			syntaxErrorExpected("a program identifier");
			break;
		}
		return programName;
	}

	/**
	 * Pre: VariableIdentifier is leftmost nonterminal
	 * Post: VariableIdentifier is expanded
	 */
	private void variableIdentifier(Symbol idRecord) {
		switch (lookahead.getToken()) {
		// rule 1-1: VariableIdentifier --> Identifier
		case MP_IDENTIFIER:
			idRecord.lexeme = lookahead.getLexeme();
			idRecord.type = symbolTable.findSymbol(lookahead.getLexeme()).type;
			match(Token.TokenName.MP_IDENTIFIER);
			break;
		default:
			syntaxErrorExpected("a variable identifier");
			break;
		}
	}

	/**
	 * Pre: ProcedureIdentifier is leftmost nonterminal
	 * Post: ProcedureIdentifier is expanded
	 */
	private void procedureIdentifier() {
		switch (lookahead.getToken()) {
		// rule 102: ProcedureIdentifier --> Identifier
		case MP_IDENTIFIER:
			match(Token.TokenName.MP_IDENTIFIER);
			break;
		default:
			syntaxErrorExpected("a procedure identifier");
			break;
		}
	}

	/**
	 * Pre: FunctionIdentifier is leftmost nonterminal
	 * Post: FunctionIdentifier is expanded
	 */
	private void functionIdentifier(Symbol funIdRecord) {
		switch (lookahead.getToken()) {
		// rule 103: FunctionIdentifier --> Identifier
		case MP_IDENTIFIER:
			funIdRecord.lexeme = lookahead.getLexeme();
			funIdRecord.type = symbolTable.findSymbol(lookahead.getLexeme()).type;
			match(Token.TokenName.MP_IDENTIFIER);
			break;
		default:
			syntaxErrorExpected("a function identifier");
			break;
		}
	}

	/**
	 * Pre: BooleanExpression is leftmost nonterminal
	 * Post: BooleanExpression is expanded
	 */
	private void booleanExpression(Symbol exprRec) {
		switch (lookahead.getToken()) {
		// rule 104: BooleanExpression --> Expression
		case MP_LPAREN:
		case MP_PLUS:
		case MP_MINUS:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_FLOAT_LIT:	
		case MP_FIXED_LIT:	
		case MP_TRUE:
		case MP_FALSE:
		case MP_NOT:
			expression(exprRec, new Symbol());
			break;
		default:
			syntaxErrorExpected("a boolean expression");
			break;
		}
	}

	/**
	 * Pre: OrdinalExpression is leftmost nonterminal
	 * Post: OrdinalExpression is expanded
	 */
	private void ordinalExpression() {
		switch (lookahead.getToken()) {
		// rule 105: OrdinalExpression --> Expression
		case MP_LPAREN:
		case MP_PLUS:
		case MP_MINUS:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_FLOAT_LIT:
		case MP_FIXED_LIT:
		case MP_TRUE:
		case MP_FALSE:
		case MP_STRING_LIT:
		case MP_NOT:
			expression(new Symbol(), new Symbol());
			break;
		default:
			syntaxErrorExpected("a ordianl expression");
			break;
		}
	}

	/**
	 * Pre: IdentifierList is leftmost nonterminal
	 * Post: IdentifierList is expanded
	 */
	private Collection<String> identifierList() {
		Collection<String> identifiers = new LinkedList<String>();
		switch (lookahead.getToken()) {
		// rule 106: IdentifierList --> Identifier IdentifierTail
		case MP_IDENTIFIER:
			identifiers.add(lookahead.getLexeme());
			match(Token.TokenName.MP_IDENTIFIER);
			identifierTail(identifiers);
			break;
		default:
			syntaxErrorExpected("'identifier'(5)");
			break;
		}
		return identifiers;
	}

	/**
	 * Pre: IdentifierTail is leftmost nonterminal
	 * Post: IdentifierTail is expanded
	 */
	private void identifierTail(Collection<String> identifiers) {
		switch (lookahead.getToken()) {
		// rule 107: IdentifierTail --> Identifier
		case MP_COMMA:
			match(Token.TokenName.MP_COMMA);
			identifiers.add(lookahead.getLexeme());
			match(Token.TokenName.MP_IDENTIFIER);
			identifierTail(identifiers);
			break;
		// rule 108: IdentifierTail --> epsilon
		case MP_COLON:
			break;
		default:
			syntaxErrorExpected("',' and more identifiers, or ':' and a variable type");
			break;
		}
	}
}
