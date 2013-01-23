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
		Map<String, TokenName> aMap = new HashMap<String, TokenName>();
		aMap.put("and", TokenName.MP_AND);
		aMap.put("begin", TokenName.MP_BEGIN);
		aMap.put("div", TokenName.MP_DIV);
		aMap.put("do", TokenName.MP_DO);
		aMap.put("downto", TokenName.MP_DOWNTO);
		aMap.put("else", TokenName.MP_ELSE);
		aMap.put("fixed", TokenName.MP_FIXED);
		aMap.put("float", TokenName.MP_FLOAT);
		aMap.put("for", TokenName.MP_FOR);
		aMap.put("function", TokenName.MP_FUNCTION);
		aMap.put("if", TokenName.MP_IF);
		aMap.put("integer", TokenName.MP_INTEGER);
		aMap.put("mod", TokenName.MP_MOD);
		aMap.put("not", TokenName.MP_NOT);
		aMap.put("or", TokenName.MP_OR);
		aMap.put("proceture", TokenName.MP_PROCEDURE);
		aMap.put("program", TokenName.MP_PROGRAM);
		aMap.put("read", TokenName.MP_READ);
		aMap.put("repeat", TokenName.MP_REPEAT);
		aMap.put("then", TokenName.MP_REPEAT);
		aMap.put("to", TokenName.MP_TO);
		aMap.put("until", TokenName.MP_UNTIL);
		aMap.put("var", TokenName.MP_VAR);
		aMap.put("while", TokenName.MP_WHILE);
		aMap.put("write", TokenName.MP_WRITE);
		ReservedWords = Collections.unmodifiableMap(aMap);
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
