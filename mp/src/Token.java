import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
	
	private static final Map<String, TokenName> ReservedWords;
	static {
		Map<String, TokenName> tempMap = new HashMap<String, TokenName>();
		tempMap.put("and", TokenName.MP_AND);
		tempMap.put("begin", TokenName.MP_BEGIN);
		tempMap.put("div", TokenName.MP_DIV);
		tempMap.put("do", TokenName.MP_DO);
		tempMap.put("downto", TokenName.MP_DOWNTO);
		tempMap.put("else", TokenName.MP_ELSE);
		tempMap.put("fixed", TokenName.MP_FIXED);
		tempMap.put("float", TokenName.MP_FLOAT);
		tempMap.put("for", TokenName.MP_FOR);
		tempMap.put("function", TokenName.MP_FUNCTION);
		tempMap.put("if", TokenName.MP_IF);
		tempMap.put("integer", TokenName.MP_INTEGER);
		tempMap.put("mod", TokenName.MP_MOD);
		tempMap.put("not", TokenName.MP_NOT);
		tempMap.put("or", TokenName.MP_OR);
		tempMap.put("procedure", TokenName.MP_PROCEDURE);
		tempMap.put("program", TokenName.MP_PROGRAM);
		tempMap.put("read", TokenName.MP_READ);
		tempMap.put("repeat", TokenName.MP_REPEAT);
		tempMap.put("then", TokenName.MP_REPEAT);
		tempMap.put("to", TokenName.MP_TO);
		tempMap.put("until", TokenName.MP_UNTIL);
		tempMap.put("var", TokenName.MP_VAR);
		tempMap.put("while", TokenName.MP_WHILE);
		tempMap.put("write", TokenName.MP_WRITE);
		ReservedWords = Collections.unmodifiableMap(tempMap);
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
	
	public static TokenName getReservedWord(String s) {
		return ReservedWords.get(s);
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
