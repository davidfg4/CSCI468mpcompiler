public class Token {
	public enum TokenName {
		// Reserved words
		MP_AND, MP_BEGIN, MP_DIV, MP_DO, MP_DOWNTO, MP_ELSE, MPEND, MP_FIXED,
		MP_FLOAT, MP_FOR, MP_FUNCTION, MP_IF, MP_INTEGER, MP_MOD, MP_NOT,
		MP_OR, MP_PROCEDURE, MP_PROGRAM, MP_READ, MP_REPEAT, MP_THEN, MP_TO,
		MP_UNTIL, MP_VAR, MP_WHILE, MP_WRITE,
		// Identifiers and literals
		MP_IDENTIFIER, MP_INTERGER_LIT, MP_FIXED_LT, MP_FLOAT_LIT,
		MP_STRING_LIT,
		// Symbols
		MP_PERIOD, MP_COMMA, MP_SCOLON, MP_LPAREN, MP_RPAREN, MP_EQUAL,
		MP_GTHAN, MP_GEQUEL, MP_LTHAN, MP_LEQUAL, MP_NEQUAL, MP_ASSIGN,
		MP_PLUS, MP_MINUS, MP_TIMES, MP_COLON,
		// End of file
		MP_EOF,
		// Errors
		MP_RUN_COMMENT, MP_RUN_STRING, MP_ERROR
	}
	
	private TokenName token;
	private int line, column;
	private String lexeme;
	
	Token(TokenName t, int l, int c, String lex) {
		token = t;
		line = l;
		column = c;
		lexeme = lex;
	}
	
	public TokenName getToken() {
		return token;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}
	
	public String getLexeme() {
		return lexeme;
	}
}
