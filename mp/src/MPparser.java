import java.io.IOException;

public class MPparser {

	private Token lookahead;
	private MPscanner scanner;

	public MPparser(MPscanner scanner) {
		this.scanner = scanner;
	}

	// TODO: add pre and post conditions to methods
	// TODO: case statements and respective comments

	private boolean match(Token.TokenName token) {
		try {
			lookahead = scanner.getToken();
		} catch (IOException ioe) {
			System.err.println("Error: Cannot read input file");
		}
		if (lookahead.getToken() == token)
			return true;
		return false;
	}

	private void error(String message) {
		System.out.println(message);
	}

	/**
	 * Pre: SystemGoal is the leftmost nonterminal
	 * Post: SystemGoal has been expanded
	 */
	private void systemGoal() {
		switch (lookahead.getToken()) {
		// SystemGoal --> Program $
		case DUMMY_1:
			program();
			// TODO deal with cash
			break;
		default:
			error("SystemGoal not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: Program is leftmost nonterminal
	 * Post: Program has been expanded
	 */
	private void program() {
		switch (lookahead.getToken()) {
		// Program --> ProgramHeading ";" Block "."
		case DUMMY_1:
			programHeading();
			match(Token.TokenName.MP_SCOLON);
			block();
			match(Token.TokenName.MP_PERIOD);
			break;
		default:
			error("Program not implemented yet.");
			break;
		}

	}

	/**
	 * Pre: ProgramHeading is leftmost nonterminal
	 * Post: ProgramHeading has been expanded
	 */
	private void programHeading() {
		switch (lookahead.getToken()) {
		// ProgramHeading --> "program" ProgramIdentifier
		case DUMMY_1:
			match(Token.TokenName.MP_PROGRAM);
			programIdentifier();
			break;
		default:
			error("ProgramHeading not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: Block is leftmost nonterminal
	 * Post: Block has been expanded
	 */
	private void block() {
		switch (lookahead.getToken()) {
		// Block --> VariableDeclarationPart ProcedureAndFunctionDeclarationPart StatementPart
		case DUMMY_1:
			variableDeclarationPart();
			procedureAndFunctionDeclarationPart();
			statementPart();
			break;
		default:
			error("Block not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: VariableDeclarationPart is leftmost nonterminal
	 * Post: VariableDeclarationPart is expanded
	 */
	private void variableDeclarationPart() {
		switch (lookahead.getToken()) {
		// VariableDeclarationPart --> "var" VariableDeclaration ";" VariableDeclarationTail
		case DUMMY_1:
			match(Token.TokenName.MP_VAR);
			variableDeclaration();
			match(Token.TokenName.MP_SCOLON);
			variableDeclarationTail();
			break;
		// VariableDeclarationPart --> epsilon
		case DUMMY_2:
			break;
		default:
			error("VariableDeclarationPart not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: VariableDeclarationTail is leftmost nonterminal
	 * Post: VariableDeclarationTail is expanded
	 */
	private void variableDeclarationTail() {
		switch (lookahead.getToken()) {
		// VariableDeclarationTail --> VariableDeclaration ";" VariableDeclarationTail
		case DUMMY_1:
			variableDeclaration();
			match(Token.TokenName.MP_SCOLON);
			variableDeclarationTail();
			break;
		// VariableDeclarationTail --> epsilon
		case DUMMY_2:
			break;
		default:
			error("VariableDeclarationTail not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: VariableDeclaration is leftmost nonterminal
	 * Post: VariableDeclaration is expanded
	 */
	private void variableDeclaration() {
		switch (lookahead.getToken()) {
		// VariableDeclaration --> IdentifierList ":" Type
		case DUMMY_1:
			identifierList();
			match(Token.TokenName.MP_COLON);
			type();
		default:
			error("VariableDeclaration not implemented yet.");
		}
	}

	/**
	 * Pre: Type is leftmost nonterminal
	 * Post: Type is expanded
	 */
	private void type() {
		switch (lookahead.getToken()) {
		// Type --> "Integer"
		case DUMMY_1:
			match(Token.TokenName.MP_INTEGER);
			break;
		// Type --> "Float"
		case DUMMY_2:
			match(Token.TokenName.MP_FLOAT);
			break;
		default:
			error("Type not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: ProcedureAndFunctionDeclarationPart is leftmost nonterminal
	 * Post: ProcedureAndFunctionDeclarationPart is expanded
	 */
	private void procedureAndFunctionDeclarationPart() {
		switch (lookahead.getToken()) {
		// ProcedureAndFunctionDeclarationPart --> ProcedureDeclaration ProcedureAndFunctionDeclarationPart 
		case DUMMY_1:
			procedureDeclaration();
			procedureAndFunctionDeclarationPart();
			break;
		// ProcedureAndFunctionDeclarationPart --> FunctionDeclaration ProcedureAndFunctionDeclarationPart
		case DUMMY_2:
			functionDeclaration();
			procedureAndFunctionDeclarationPart();
			break;
		// ProcedureAndFunctionDeclarationPart --> epsilon
		case DUMMY_3:
			break;
		default:
			error("ProcedureAndFunctionDeclarationPart not implemented yet.");
		}
	}

	/**
	 * Pre: ProcedureDeclaration is leftmost nonterminal
	 * Post: ProcedureDeclaration is expanded
	 */
	private void procedureDeclaration() {
		switch (lookahead.getToken()) {
		// ProcedureDeclaration --> ProcedureHeading ";" Block ";" 
		case DUMMY_1:
			procedureHeading();
			match(Token.TokenName.MP_SCOLON);
			block();
			match(Token.TokenName.MP_SCOLON);
			break;
		default:
			error("ProcedureDeclaration not implemented yet.");
		}
	}

	/**
	 * Pre: FunctionDeclaration is leftmost nonterminal
	 * Post: FunctionDeclaration is expanded
	 */
	private void functionDeclaration() {
		switch (lookahead.getToken()) {
		// FunctionDeclaration --> FunctionHeading ";" Block ";"
		case DUMMY_1:
			functionHeading();
			match(Token.TokenName.MP_SCOLON);
			block();
			match(Token.TokenName.MP_SCOLON);
			break;
		default:
			error("FunctionDeclaration not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: ProcedureHeading is leftmost nonterminal
	 * Post: ProcedureHeading is expanded
	 */
	private void procedureHeading() {
		switch (lookahead.getToken()) {
		// ProcedureHeading --> "procedure" ProcedureIdentifier OptionalFormalParameterList
		case DUMMY_1:
			match(Token.TokenName.MP_PROCEDURE);
			procedureIdentifier();
			optionalFormalParameterList();
			break;
		default:
			error("ProcedureHeading not implemented yet.");
		}
	}

	private void functionHeading() {
		switch (lookahead.getToken()) {
		}
	}

	private void optionalFormalParameterList() {
		switch (lookahead.getToken()) {
		}
	}

	private void formatParameterSectionTail() {
		switch (lookahead.getToken()) {
		}
	}

	private void formalParameterSection() {
		switch (lookahead.getToken()) {
		}
	}

	private void valueParameterSection() {
		switch (lookahead.getToken()) {
		}
	}

	private void variableParameterSection() {
		switch (lookahead.getToken()) {
		}
	}

	private void statementPart() {
		switch (lookahead.getToken()) {
		}
	}

	private void compoundStatement() {
		switch (lookahead.getToken()) {
		}
	}

	private void statementSequence() {
		switch (lookahead.getToken()) {
		}
	}

	private void statementTail() {
		switch (lookahead.getToken()) {
		}
	}

	private void statement() {
		switch (lookahead.getToken()) {
		}
	}

	private void emptyStatement() {
		switch (lookahead.getToken()) {
		}
	}

	private void readStatement() {
		switch (lookahead.getToken()) {
		}
	}

	private void readParameterTail() {
		switch (lookahead.getToken()) {
		}
	}

	private void readParameter() {
		switch (lookahead.getToken()) {
		}
	}

	private void writeStatement() {
		switch (lookahead.getToken()) {
		}
	}

	private void writeParameterTail() {
		switch (lookahead.getToken()) {
		}
	}

	private void writeParameter() {
		switch (lookahead.getToken()) {
		}
	}

	private void assignmentStatement() {
		switch (lookahead.getToken()) {
		}
	}

	private void ifStatement() {
		switch (lookahead.getToken()) {
		}
	}

	private void optionalElsePart() {
		switch (lookahead.getToken()) {
		}
	}

	private void repeatStatement() {
		switch (lookahead.getToken()) {
		}
	}

	private void whileStatement() {
		switch (lookahead.getToken()) {
		}
	}

	private void forStatement() {
		switch (lookahead.getToken()) {
		}
	}

	private void controlVariable() {
		switch (lookahead.getToken()) {
		}
	}

	private void initialValue() {
		switch (lookahead.getToken()) {
		}
	}

	private void stepValue() {
		switch (lookahead.getToken()) {
		}
	}

	private void finalValue() {
		switch (lookahead.getToken()) {
		}
	}

	private void procedureStatement() {
		switch (lookahead.getToken()) {
		}
	}

	private void optionalActualParameterList() {
		switch (lookahead.getToken()) {
		}
	}

	private void actualParameterTail() {
		switch (lookahead.getToken()) {
		}
	}

	private void actualParameter() {
		switch (lookahead.getToken()) {
		}
	}

	private void expression() {
		switch (lookahead.getToken()) {
		}
	}

	private void optionalRelationalPart() {
		switch (lookahead.getToken()) {
		}
	}

	private void relationalOperator() {
		switch (lookahead.getToken()) {
		}
	}

	private void simpleExpression() {
		switch (lookahead.getToken()) {
		}
	}

	private void termTail() {
		switch (lookahead.getToken()) {
		}
	}

	private void optionalSign() {
		switch (lookahead.getToken()) {
		}
	}

	private void addingOperator() {
		switch (lookahead.getToken()) {
		}
	}

	private void term() {
		switch (lookahead.getToken()) {
		}
	}

	private void factorTail() {
		switch (lookahead.getToken()) {
		}
	}

	private void multiplyingOperator() {
		switch (lookahead.getToken()) {
		}
	}

	private void factor() {
		switch (lookahead.getToken()) {
		}
	}

	private void programIdentifier() {
		switch (lookahead.getToken()) {
		}
	}

	private void variableIdentifier() {
		switch (lookahead.getToken()) {
		}
	}

	private void procedureIdentifier() {
		switch (lookahead.getToken()) {
		}
	}

	private void functionIdentifier() {
		switch (lookahead.getToken()) {
		}
	}

	private void booleanExpression() {
		switch (lookahead.getToken()) {
		}
	}

	private void ordinalExpression() {
		switch (lookahead.getToken()) {
		}
	}

	private void identifierList() {
		switch (lookahead.getToken()) {
		}
	}

	private void identifierTail() {
		switch (lookahead.getToken()) {
		}
	}
}
