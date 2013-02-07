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

	private void error() {

	}

	private void systemGoal() {
		switch (lookahead.getToken()) {
		case MP_PROGRAM:
			programHeading();
			match(Token.TokenName.MP_SCOLON);
			block();
			match(Token.TokenName.MP_PERIOD);
			break;
		default:
			break;
		}
	}

	private void program() {
		switch (lookahead.getToken()) {
		}

	}

	private void programHeading() {
		switch (lookahead.getToken()) {
		}
	}

	private void block() {
		switch (lookahead.getToken()) {
		}
	}

	private void variableDeclarationPart() {
		switch (lookahead.getToken()) {
		}
	}

	private void variableDeclarationTail() {
		switch (lookahead.getToken()) {
		}
	}

	private void variableDeclaration() {
		switch (lookahead.getToken()) {
		}
	}

	private void type() {
		switch (lookahead.getToken()) {
		}
	}

	private void procedureAndFunctionDeclarationPart() {
		switch (lookahead.getToken()) {
		}
	}

	private void procedureDeclaration() {
		switch (lookahead.getToken()) {
		}
	}

	private void functionDeclaration() {
		switch (lookahead.getToken()) {
		}
	}

	private void procedureHeading() {
		switch (lookahead.getToken()) {
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
		switch(lookahead.getToken()) {
		}
	}
	
	private void compoundStatement() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void statementSequence() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void statementTail() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void statement() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void emptyStatement() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void readStatement() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void readParameterTail() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void readParameter() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void writeStatement() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void writeParameterTail() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void writeParameter() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void assignmentStatement() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void ifStatement() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void optionalElsePart() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void repeatStatement() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void whileStatement() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void forStatement() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void controlVariable() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void initialValue() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void stepValue() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void finalValue() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void procedureStatement() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void optionalActualParameterList() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void actualParameterTail() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void actualParameter() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void expression() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void optionalRelationalPart() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void relationalOperator() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void simpleExpression() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void termTail() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void optionalSign() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void addingOperator() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void term() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void factorTail() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void multiplyingOperator() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void factor() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void programIdentifier() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void variableIdentifier() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void procedureIdentifier() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void functionIdentifier() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void booleanExpression() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void ordinalExpression() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void identifierList() {
		switch(lookahead.getToken()) {
		}
	}
	
	private void identifierTail() {
		switch(lookahead.getToken()) {
		}
	}
}
