import java.util.Collection;
import java.util.LinkedList;

public class MPparser {

	private Token lookahead;
	private Token secondLookahead;
	private MPscanner scanner;
	private SymbolTable symbolTable;
	
	//TODO: add rule numbers to each case comment

	public MPparser(String filename) {
		scanner = new MPscanner();
		symbolTable = new SymbolTable();
		secondLookahead = null;
		scanner.openFile(filename);
		lookahead = scanner.getToken();
		checkForScannerErrors(lookahead);
		systemGoal();
		System.out.println("Successfully parsed! No scanner or parser errors found.");
		//System.out.println(symbolTable);
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
			matchIdentifiersColonType();
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
	private void type() {
		switch (lookahead.getToken()) {
		// rule 10: Type --> "Integer"
		case MP_INTEGER:
			match(Token.TokenName.MP_INTEGER);
			break;
		// rule 11: Type --> "Float"
		case MP_FLOAT:
			match(Token.TokenName.MP_FLOAT);
			break;
		// rule ?: Type --> "Boolean"
		case MP_BOOLEAN:
			match(Token.TokenName.MP_BOOLEAN);
			break;
		default:
			syntaxErrorExpected("variable type, 'integer', 'float', or 'boolean'");
			break;
		}
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
			String procedureName = procedureHeading();
			match(Token.TokenName.MP_SCOLON);
			try {
				symbolTable.insertSymbol(new Symbol(procedureName));
				symbolTable.createSymbolTable(procedureName);
			} catch (SymbolTable.SymbolAlreadyExistsException e) {
				syntaxErrorGeneric("Error: Procedure '" + procedureName + "' already exists in the current scope.");
			}
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
			String functionName = functionHeading();
			match(Token.TokenName.MP_SCOLON);
			try {
				symbolTable.insertSymbol(new Symbol(functionName));
				symbolTable.createSymbolTable(functionName);
			} catch (SymbolTable.SymbolAlreadyExistsException e) {
				syntaxErrorGeneric("Error: Function '" + functionName + "' already exists in the current scope.");
			}
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
	private String procedureHeading() {
		String procedureName = "";
		switch (lookahead.getToken()) {
		// rule 17: ProcedureHeading --> "procedure" ProcedureIdentifier OptionalFormalParameterList
		case MP_PROCEDURE:
			match(Token.TokenName.MP_PROCEDURE);
			procedureName = lookahead.getLexeme();
			procedureIdentifier();
			optionalFormalParameterList();
			break;
		default:
			syntaxErrorExpected("'procedure'");
		}
		return procedureName;
	}

	/**
	 * Pre: FunctionHeading is leftmost nonterminal
	 * Post: ProcedureHeading is expanded
	 */
	private String functionHeading() {
		String functionName = "";
		switch (lookahead.getToken()) {
		// rule 18:FunctionHeading --> "function" FunctionIdentifier OptionalFormalParameterList Type
		case MP_FUNCTION:
			match(Token.TokenName.MP_FUNCTION);
			functionName = lookahead.getLexeme();
			functionIdentifier();
			optionalFormalParameterList();
			match(Token.TokenName.MP_COLON);
			type();
			break;
		default:
			syntaxErrorExpected("'function'");
			break;
		}
		return functionName;
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
			matchIdentifiersColonType();
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
			matchIdentifiersColonType();
			break;
		default:
			syntaxErrorExpected("'var'");
			break;
		}
	}
	
	private void matchIdentifiersColonType()
	{
		Collection<String> identifiers = identifierList();
		match(Token.TokenName.MP_COLON);
		type();
		for (String identifier : identifiers)
		{
			try {
				symbolTable.insertSymbol(new Symbol(identifier));
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
		switch (lookahead.getToken()) {
		// rule 46: ReadParameter --> VariableIdentifier
		case MP_IDENTIFIER:
			variableIdentifier();
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
		case MP_WRITE:
			match(Token.TokenName.MP_WRITE);
			match(Token.TokenName.MP_LPAREN);
			writeParameter();
			writeParameterTail();
			match(Token.TokenName.MP_RPAREN);
			break;
		default:
			syntaxErrorExpected("'write'");
			break;
		}
	}

	/**
	 * Pre: WriteParameterTail is leftmost nonterminal
	 * Post: WriteParameterTail is expanded
	 */
	private void writeParameterTail() {
		switch (lookahead.getToken()) {
		// rule 48: WriteParameterTail --> "," WriteParameter WriteParameterTail
		case MP_COMMA:
			match(Token.TokenName.MP_COMMA);
			writeParameter();
			writeParameterTail();
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
	private void writeParameter() {
		switch (lookahead.getToken()) {
		// rule 50: WriteParameter --> OrdinalExpression
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
	 * Pre: AssignmentStatement is leftmost nonterminal
	 * Post: AssignmentStatement is expanded
	 */
	private void assignmentStatement() {
		switch (lookahead.getToken()) {
		// TODO: How do we determine which to do?
		// rule 51: AssignmentStatement --> VariableIdentifier ":=" Expression
		case MP_IDENTIFIER: // TODO: For now assume they are all variables. THIS IS WRONG.
			variableIdentifier();
			match(Token.TokenName.MP_ASSIGN);
			expression();
			break;
		// rule 52: AssignmentStatement --> FunctionIdentifier ":=" Expression
		case DUMMY_1:
			functionIdentifier();
			match(Token.TokenName.MP_ASSIGN);
			expression();
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
		switch (lookahead.getToken()) {
		// rule 53: IfStatement --> "if" BooleanExpression "then" Statement OptionalElsePart
		case MP_IF:
			match(Token.TokenName.MP_IF);
			booleanExpression();
			match(Token.TokenName.MP_THEN);
			statement();
			optionalElsePart();
			break;
		default:
			syntaxErrorExpected("'if'");
			break;
		}
	}

	private void optionalElsePart() {
		switch (lookahead.getToken()) {
		// It is possible for "case MP_ELSE" to be epsilon, but for simplicity,
		// and to always match the closest if statement, 'else' always matches here.
		// rule 54: OptionalElsePart --> "else" Statement
		case MP_ELSE:
			match(Token.TokenName.MP_ELSE);
			statement();
			break;
		// rule 55: OptionalElsePart --> epsilon
		case MP_SCOLON:
		case MP_END:
		case MP_UNTIL:
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
		switch (lookahead.getToken()) {
		// rule 56: RepeatStatement --> "repeat" StatementSequence "until" BooleanExpression
		case MP_REPEAT:
			match(Token.TokenName.MP_REPEAT);
			statementSequence();
			match(Token.TokenName.MP_UNTIL);
			booleanExpression();
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
		switch (lookahead.getToken()) {
		// rule 57: WhileStatement --> "while" BooleanExpression "do" Statement
		case MP_WHILE:
			match(Token.TokenName.MP_WHILE);
			booleanExpression();
			match(Token.TokenName.MP_DO);
			statement();
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
			variableIdentifier();
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
	private void expression() {
		switch (lookahead.getToken()) {
		// rule 70: Expression --> SimpleExpression OptionalRelationalPart
		case MP_LPAREN:
		case MP_PLUS:
		case MP_MINUS:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_NOT:
			simpleExpression();
			optionalRelationalPart();
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
	private void optionalRelationalPart() {
		switch (lookahead.getToken()) {
		// rule 71: OptionalRelationalPart --> RelationalOperator SimpleExpression
		case MP_EQUAL:
		case MP_LTHAN:
		case MP_GTHAN:
		case MP_LEQUAL:
		case MP_GEQUAL:
		case MP_NEQUAL:
			relationalOperator();
			simpleExpression();
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
	private void relationalOperator() {
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
	private void simpleExpression() {
		switch (lookahead.getToken()) {
		// rule 79: SimpleExpression --> OptionalSign Term TermTail
		case MP_LPAREN:
		case MP_PLUS:
		case MP_MINUS:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_NOT:
			optionalSign();
			term();
			termTail();
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
	private void termTail() {
		switch (lookahead.getToken()) {
		// rule 80: TermTail --> AddingOperator Term TermTail
		case MP_PLUS:
		case MP_MINUS:
		case MP_OR:
			addingOperator();
			term();
			termTail();
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
	private void optionalSign() {
		switch (lookahead.getToken()) {
		// rule 82: OptionalSign --> "+"
		case MP_PLUS:
			match(Token.TokenName.MP_PLUS);
			break;
		// rule 83: OptionalSign --> "-"
		case MP_MINUS:
			match(Token.TokenName.MP_MINUS);
			break;
		// rule 84: OptionalSign --> epsilon
		case MP_LPAREN:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
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
	private void addingOperator() {
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
	private void term() {
		switch (lookahead.getToken()) {
		// rule 88: Term --> Factor FactorTail
		case MP_LPAREN:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_NOT:
			factor();
			factorTail();
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
	private void factorTail() {
		switch (lookahead.getToken()) {
		// rule 89: FactorTail --> MultiplyingOperator Factor FactorTail
		case MP_TIMES:
		case MP_AND:
		case MP_DIV:
		case MP_MOD:
			multiplyingOperator();
			factor();
			factorTail();
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
	private void multiplyingOperator() {
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
	private void factor() {
		switch (lookahead.getToken()) {
		// TODO: there is an conflict here between matching VariableIdentifier
		// and FunctionIdentifier.
		// Factor --> UnsignedInteger
		case MP_INTEGER_LIT:
			match(Token.TokenName.MP_INTEGER_LIT);
			break;
		// Factor --> VariableIdentifier
		case DUMMY_1:
			variableIdentifier();
			break;
		// Factor --> "not" Factor
		case MP_NOT:
			match(Token.TokenName.MP_NOT);
			factor();
			break;
		// Factor --> "(" Expression ")"
		case MP_LPAREN:
			match(Token.TokenName.MP_LPAREN);
			expression();
			match(Token.TokenName.MP_RPAREN);
			break;
		// Factor --> FunctionIdentifier OptionalActualParameterList
		case MP_IDENTIFIER: // TODO: for now assume all identifiers are functions. THIS IS WRONG.
			functionIdentifier();
			optionalActualParameterList();
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
	private void variableIdentifier() {
		switch (lookahead.getToken()) {
		// rule 1-1: VariableIdentifier --> Identifier
		case MP_IDENTIFIER:
			try {
				symbolTable.insertSymbol(new Symbol(lookahead.getLexeme()));
			} catch (SymbolTable.SymbolAlreadyExistsException e) {
				syntaxErrorGeneric("Error: Identifier '" + lookahead.getLexeme() + "' already exists in the current scope.");
			}
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
	private void functionIdentifier() {
		switch (lookahead.getToken()) {
		// rule 103: FunctionIdentifier --> Identifier
		case MP_IDENTIFIER:
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
	private void booleanExpression() {
		switch (lookahead.getToken()) {
		// rule 104: BooleanExpression --> Expression
		case MP_LPAREN:
		case MP_PLUS:
		case MP_MINUS:
		case MP_IDENTIFIER:
		case MP_INTEGER_LIT:
		case MP_NOT:
			expression();
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
		case MP_NOT:
			expression();
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
