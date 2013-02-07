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

	/**
	 * Pre: FunctionHeading is leftmost nonterminal
	 * Post: ProcedureHeading is expanded
	 */
	private void functionHeading() {
		switch (lookahead.getToken()) {
		// FunctionHeading --> "function" FunctionIdentifier OptionalFormalParameterList Type
		case DUMMY_1:
			match(Token.TokenName.MP_FUNCTION);
			functionIdentifier();
			optionalFormalParameterList();
			type();
			break;
		default:
			error("FunctionHeading not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: OptionalFormalParameterList is leftmost nonterminal
	 * Post: OptionalFormatlParameterList is expanded
	 */
	private void optionalFormalParameterList() {
		switch (lookahead.getToken()) {
		// OptionalFormalParameterList --> "(" FormalParameterSection FormalParameterSectionTail ")"
		case DUMMY_1:
			match(Token.TokenName.MP_RPAREN);
			formalParameterSection();
			formalParameterSectionTail();
			match(Token.TokenName.MP_LPAREN);
			break;
		// OptionalFormalParameterList --> epsilon
		case DUMMY_2:
			break;
		default:
			error("OptionalFormalParameterList not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: FormalParameterSectionTail is leftmost nonterminal
	 * Post: FormatParameterSectionTail
	 */
	private void formalParameterSectionTail() {
		switch (lookahead.getToken()) {
		// FormalParameterSectionTail --> ";" FormalParameterSection FormalParameterSectionTail
		case DUMMY_1:
			match(Token.TokenName.MP_SCOLON);
			formalParameterSection();
			formalParameterSectionTail();
			break;
		// FormalParameterSectionTail --> epsilon
		case DUMMY_2:
			break;
		default:
			error("FormalParameterSectionTail not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: FormalParameterSection is leftmost nonterminal
	 * Post: FormalParameterSection is expanded
	 */
	private void formalParameterSection() {
		switch (lookahead.getToken()) {
		// FormalParameterSection --> ValueParameterSection
		case DUMMY_1:
			valueParameterSection();
			break;
		// FormalParameterSection --> VariableParameterSection
		case DUMMY_2:
			variableParameterSection();
			break;
		default:
			error("FormalParameterSection not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: ValueParameterSection is leftmost nonterminal
	 * Post: ValueParameterSection is expanded
	 */
	private void valueParameterSection() {
		switch (lookahead.getToken()) {
		// ValueParameterSection --> IdentifierList ":" Type
		case DUMMY_1:
			identifierList();
			match(Token.TokenName.MP_COLON);
			type();
			break;
		default:
			error("ValueParameterSection not implemented yet");
			break;
		}
	}

	/**
	 * Pre: VariableParameterSection is leftmost nonterminal
	 * Post: VariableParameterSection is expanded
	 */
	private void variableParameterSection() {
		switch (lookahead.getToken()) {
		// VariableParameterSection --> "var" IdentifierList ":" Type
		case DUMMY_1:
			match(Token.TokenName.MP_VAR);
			identifierList();
			match(Token.TokenName.MP_COLON);
			type();
			break;
		default:
			error("VariableParameterSection not implemented yet");
			break;
		}
	}

	/**
	 * Pre: StatementPart is leftmost nonterminal
	 * Post: StatementPart is expanded
	 */
	private void statementPart() {
		switch (lookahead.getToken()) {
		// StatementPart --> CompoundStatement
		case DUMMY_1:
			compoundStatement();
			break;
		default:
			break;
		}
	}

	/**
	 * Pre: CompoundStatement is leftmost nonterminal
	 * Post: CompoundStatement is expanded
	 */
	private void compoundStatement() {
		switch (lookahead.getToken()) {
		// CompoundStatement --> "begin" StatementSequence "end"
		case DUMMY_1:
			match(Token.TokenName.MP_BEGIN);
			statementSequence();
			match(Token.TokenName.MP_END);
			break;
		default:
			error("CompoundStatement not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: StatementSequence is leftmost nonterminal
	 * Post: StatementSequence is expanded
	 */
	private void statementSequence() {
		switch (lookahead.getToken()) {
		// StatementSequence --> Statement StatementTail
		case DUMMY_1:
			statement();
			statementTail();
			break;
		default:
			error("StatementSequence not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: StatementTail is leftmost nonterminal
	 * Post: StatementTail is expanded
	 */
	private void statementTail() {
		switch (lookahead.getToken()) {
		// StatementTail --> ";" Statement StatementTail
		case DUMMY_1:
			match(Token.TokenName.MP_SCOLON);
			statement();
			statementTail();
			break;
		// StatementTail --> epsilon
		case DUMMY_2:
			break;
		default:
			error("StatementTail not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: Statement is leftmost nonterminal
	 * Post: Statement is expanded
	 */
	private void statement() {
		switch (lookahead.getToken()) {
		// Statement --> EmptyStatement
		case DUMMY_1:
			emptyStatement();
			break;
		// Statement --> CompoundStatement
		case DUMMY_2:
			compoundStatement();
			break;
		// Statement --> ReadStatement
		case DUMMY_3:
			readStatement();
			break;
		// Statement --> WriteStatement
		case DUMMY_4:
			writeStatement();
			break;
		// Statement --> AssignmentStatement
		case DUMMY_5:
			assignmentStatement();
			break;
		// Statement --> IfStatement
		case DUMMY_6:
			ifStatement();
			break;
		// Statement --> WhileStatement
		case DUMMY_7:
			whileStatement();
			break;
		// Statement --> RepeatStatement
		case DUMMY_8:
			repeatStatement();
			break;
		// Statement --> ForStatement
		case DUMMY_9:
			forStatement();
			break;
		// Statement --> ProcedureStatement
		case DUMMY_10:
			procedureStatement();
			break;
		default:
			error("Statement not implemented yet.");
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
		case DUMMY_1:
			break;
		default:
			error("EmptyStatement not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: ReadStatement is leftmost nonterminal
	 * Post: ReadStatement is expanded
	 */
	private void readStatement() {
		switch (lookahead.getToken()) {
		// ReadStatement --> "read" "(" ReadParameter ReadParameterTail ")"
		case DUMMY_1:
			match(Token.TokenName.MP_READ);
			match(Token.TokenName.MP_LPAREN);
			readParameter();
			readParameterTail();
			match(Token.TokenName.MP_RPAREN);
			break;
		default:
			error("ReadStatement not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: ReadParameterTail is leftmost nonterminal
	 * Post: ReadParameterTail is expanded
	 */
	private void readParameterTail() {
		switch (lookahead.getToken()) {
		// ReadParameterTail --> "," ReadParameter ReadParameterTail
		case DUMMY_1:
			match(Token.TokenName.MP_COMMA);
			readParameter();
			readParameterTail();
			break;
		// ReadParameterTail --> epsilon
		case DUMMY_2:
			break;
		default: 
			error("ReadParameterTail not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: ReadParameter is leftmost nonterminal
	 * Post: ReadParameter is expanded
	 */
	private void readParameter() {
		switch (lookahead.getToken()) {
		// ReadParameter --> VariableIdentifier
		case DUMMY_1:
			variableIdentifier();
			break;
		default: 
			error("ReadParameter not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: WriteStatement is leftmost nonterminal
	 * Post: WriteStatement is expanded
	 */
	private void writeStatement() {
		switch (lookahead.getToken()) {
		// WriteStatement --> "write" "(" WriteParameter WriteParameterTail ")"
		case DUMMY_1:
			match(Token.TokenName.MP_WRITE);
			match(Token.TokenName.MP_LPAREN);
			writeParameter();
			writeParameterTail();
			match(Token.TokenName.MP_RPAREN);
			break;
		default:
			error("WriteStatement not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: WriteParameterTail is leftmost nonterminal
	 * Post: WriteParameterTail is expanded
	 */
	private void writeParameterTail() {
		switch (lookahead.getToken()) {
		// WriteParameterTail --> "," WriteParameter WriteParameterTail
		case DUMMY_1:
			match(Token.TokenName.MP_COMMA);
			writeParameter();
			writeParameterTail();
			break;
		// WriteParameterTail --> epsilon
		case DUMMY_2:
			break;
		default:
			error("WriteParameterTail not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: WriteParameter is leftmost nonterminal
	 * Post: WriteParameter is expanded
	 */
	private void writeParameter() {
		switch (lookahead.getToken()) {
		// WriteParameter --> OrdinalExpression
		case DUMMY_1:
			ordinalExpression();
			break;
		default:
			error("WriteParameter not implemented yet");
			break;
		}
	}

	/**
	 * Pre: AssignmentStatement is leftmost nonterminal
	 * Post: AssignmentStatement is expanded
	 */
	private void assignmentStatement() {
		switch (lookahead.getToken()) {
		// AssignmentStatement --> VariableIdentifier ":=" Expression
		case DUMMY_1:
			variableIdentifier();
			match(Token.TokenName.MP_ASSIGN);
			expression();
			break;
		// AssignmentStatement --> FunctionIdentifier ":=" Expression
		case DUMMY_2:
			functionIdentifier();
			match(Token.TokenName.MP_ASSIGN);
			expression();
			break;
		default:
			error("AssignmentStatement not implemented yet");
			break;
		}
	}

	/**
	 * Pre: ifStatement is leftmost nonterminal
	 * Post: ifStatement is expanded
	 */
	private void ifStatement() {
		switch (lookahead.getToken()) {
		// IfStatement --> "if" BooleanExpression "then" Statement OptionalElsePart
		case DUMMY_1:
			match(Token.TokenName.MP_IF);
			booleanExpression();
			match(Token.TokenName.MP_THEN);
			statement();
			optionalElsePart();
			break;
		default:
			error("IfStatement not implemented yet.");
			break;
		}
	}

	private void optionalElsePart() {
		switch (lookahead.getToken()) {
		// OptionalElsePart --> "else" Statement
		case DUMMY_1:
			match(Token.TokenName.MP_ELSE);
			statement();
			break;
		// OptionalElsePart --> epsilon
		case DUMMY_2:
			break;
		default:
			error("OptionalElsePart not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: RepeatStatement is leftmost nonterminal
	 * Post: RepeatStatement is expanded
	 */
	private void repeatStatement() {
		switch (lookahead.getToken()) {
		// RepeatStatement --> "repeat" StatementSequence "until" BooleanExpression
		case DUMMY_1:
			match(Token.TokenName.MP_REPEAT);
			statementSequence();
			match(Token.TokenName.MP_UNTIL);
			booleanExpression();
			break;
		default:
			error("RepeatStatement not implemented yet");
			break;
		}
	}

	/**
	 * Pre: WhileStatement is leftmost nonterminal
	 * Post: WhileStatement is expanded
	 */
	private void whileStatement() {
		switch (lookahead.getToken()) {
		// WhileStatement --> "while" BooleanExpression "do" Statement
		case DUMMY_1:
			match(Token.TokenName.MP_WHILE);
			booleanExpression();
			match(Token.TokenName.MP_DO);
			statement();
			break;
		default:
			error("WhileStatement not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: ForStatement is leftmost nonterminal
	 * Post: ForStatement is expanded
	 */
	private void forStatement() {
		switch (lookahead.getToken()) {
		// ForStatement --> "for" ControlVariable ":=" InitialValue StepValue FinalValue "do" Statement
		case DUMMY_1:
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
			error("ForStatement not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: ControlVariable is leftmost nonterminal
	 * Post: ControlVariable is expanded
	 */
	private void controlVariable() {
		switch (lookahead.getToken()) {
		// ControlVariable --> VariableIdentifier
		case DUMMY_1:
			variableIdentifier();
			break;
		default:
			error("ControlVariable not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: InitialValue is leftmost nonterminal
	 * Post: InitialValue is expanded
	 */
	private void initialValue() {
		switch (lookahead.getToken()) {
		// InitialValue --> OrdinalExpression
		case DUMMY_1:
			ordinalExpression();
			break;
		default: 
			error("InitialValue not implemented yet");
			break;
		}
	}

	/**
	 * Pre: StepValue is leftmost nonterminal
	 * Post: StepValue is expanded
	 */
	private void stepValue() {
		switch (lookahead.getToken()) {
		// StepValue --> "to"
		case DUMMY_1:
			match(Token.TokenName.MP_TO);
		// StepValue --> "downto"
		case DUMMY_2:
			match(Token.TokenName.MP_DOWNTO);
			break;
		default:
			error("StepValue not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: FinalValue is leftmost nonterminal
	 * Post: FinalValue is expanded
	 */
	private void finalValue() {
		switch (lookahead.getToken()) {
		// FinalValue --> OrdinalExpression
		case DUMMY_1:
			ordinalExpression();
			break;
		default:
			error("FinalValue not implemented yet");
			break;
		}
	}

	/**
	 * Pre: ProcedureStatement is leftmost nonterminal
	 * Post: ProcedureStatement is expanded
	 */
	private void procedureStatement() {
		switch (lookahead.getToken()) {
		// ProcedureStatement --> ProcedureIdentifier OptionalActualParameterList
		case DUMMY_1:
			procedureIdentifier();
			optionalActualParameterList();
			break;
		default:
			error("ProcedureStatement not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: OptionalActualParameterList is leftmost nonterminal
	 * Post: OptionalActualParameterList is expanded
	 */
	private void optionalActualParameterList() {
		switch (lookahead.getToken()) {
		// OptionalActualParameterList --> "(" ActualParameter ActualParameterTail ")"
		case DUMMY_1:
			match(Token.TokenName.MP_LPAREN);
			actualParameter();
			actualParameterTail();
			match(Token.TokenName.MP_RPAREN);
			break;
		// OptionalActualParameterList --> epsilon
		case DUMMY_2:
			break;
		default:
			error("OptionalActualParameterList not implemented yet.");
			break;
		}
	}

	/**
	 * Pre: ActualParameterTail is leftmost nonterminal
	 * Post: ActualParameterTail is expanded
	 */
	private void actualParameterTail() {
		switch (lookahead.getToken()) {
		// ActualParameterTail --> "," ActualParameter ActualParameterTail
		case DUMMY_1:
			match(Token.TokenName.MP_COMMA);
			actualParameter();
			actualParameterTail();
			break;
		case DUMMY_2:
			break;
		default:
			error("ActualParameterTail not implemented yet");
			break;
		}
	}

	/**
	 * Pre: ActualParameter is leftmost nonterminal
	 * Post: ActualParameter is expanded
	 */
	private void actualParameter() {
		switch (lookahead.getToken()) {
		// ActualParameter --> OrdinalExpression
		case DUMMY_1:
			ordinalExpression();
			break;
		default:
			error("ActualParameter not implemented yet");
			break;
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
