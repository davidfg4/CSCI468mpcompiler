import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Token {
	public enum TokenName {
		// Reserved words
		MP_AND, MP_BEGIN, MP_BOOLEAN, MP_DIV, MP_DO, MP_DOWNTO, MP_ELSE, MP_END, MP_FIXED,
		MP_FLOAT, MP_FOR, MP_FUNCTION, MP_IF, MP_INTEGER, MP_MOD, MP_NOT,
		MP_OR, MP_PROCEDURE, MP_PROGRAM, MP_READ, MP_REPEAT, MP_THEN, MP_TO,
		MP_UNTIL, MP_VAR, MP_WHILE, MP_WRITE,
		// Identifiers and literals
		MP_IDENTIFIER, MP_INTEGER_LIT, MP_FIXED_LIT, MP_FLOAT_LIT,
		MP_STRING_LIT,
		// Symbols
		MP_PERIOD, MP_COMMA, MP_SCOLON, MP_LPAREN, MP_RPAREN, MP_EQUAL,
		MP_GTHAN, MP_GEQUAL, MP_LTHAN, MP_LEQUAL, MP_NEQUAL, MP_ASSIGN,
		MP_PLUS, MP_MINUS, MP_TIMES, MP_COLON,
		// End of file
		MP_EOF,
		// Errors
		MP_RUN_COMMENT, MP_RUN_STRING, MP_ERROR,
		// TODO remove dummies
		DUMMY_1, DUMMY_2, DUMMY_3, DUMMY_4, DUMMY_5, DUMMY_6, DUMMY_7, DUMMY_8,
		DUMMY_9, DUMMY_10, LOOKAHEADDUMMY
	}
	
	private static final Map<String, TokenName> ReservedWords;
	static {
		Map<String, TokenName> tempMap = new HashMap<String, TokenName>();
		tempMap.put("and", TokenName.MP_AND);
		tempMap.put("begin", TokenName.MP_BEGIN);
		tempMap.put("boolean", TokenName.MP_BOOLEAN);
		tempMap.put("div", TokenName.MP_DIV);
		tempMap.put("do", TokenName.MP_DO);
		tempMap.put("downto", TokenName.MP_DOWNTO);
		tempMap.put("else", TokenName.MP_ELSE);
		tempMap.put("end", TokenName.MP_END);
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
		tempMap.put("then", TokenName.MP_THEN);
		tempMap.put("to", TokenName.MP_TO);
		tempMap.put("until", TokenName.MP_UNTIL);
		tempMap.put("var", TokenName.MP_VAR);
		tempMap.put("while", TokenName.MP_WHILE);
		tempMap.put("write", TokenName.MP_WRITE);
		ReservedWords = Collections.unmodifiableMap(tempMap);
	}
	
	private static Map<TokenName, String> reverseReservedWords = null;

	private TokenName token;
	private int line, column;
	private String lexeme;
	
	Token(TokenName t, int l, int c, String lex) {
		if (reverseReservedWords == null)
			createReverseReservedWords();
		token = t;
		line = l;
		column = c;
		lexeme = lex;
	}
	
	private static void createReverseReservedWords() {
		 Set<Map.Entry<String, TokenName>> reservedWordsSet = ReservedWords.entrySet();
		 reverseReservedWords = new HashMap<TokenName, String>();
		 for (Map.Entry<String, TokenName> entry : reservedWordsSet)
		 {
			 reverseReservedWords.put(entry.getValue(), "'" + entry.getKey() + "'");
		 }
		 reverseReservedWords.put(TokenName.MP_IDENTIFIER, "an identifier");
		 reverseReservedWords.put(TokenName.MP_INTEGER_LIT, "an integer");
		 reverseReservedWords.put(TokenName.MP_FIXED_LIT, "a fixed number");
		 reverseReservedWords.put(TokenName.MP_FLOAT_LIT, "a float");
		 reverseReservedWords.put(TokenName.MP_STRING_LIT, "a string");
		 reverseReservedWords.put(TokenName.MP_PERIOD, "'.'");
		 reverseReservedWords.put(TokenName.MP_COMMA, "','");
		 reverseReservedWords.put(TokenName.MP_SCOLON, "';'");
		 reverseReservedWords.put(TokenName.MP_LPAREN, "'('");
		 reverseReservedWords.put(TokenName.MP_RPAREN, "')'");
		 reverseReservedWords.put(TokenName.MP_EQUAL, "'='");
		 reverseReservedWords.put(TokenName.MP_GTHAN, "'>'");
		 reverseReservedWords.put(TokenName.MP_GEQUAL, "'>='");
		 reverseReservedWords.put(TokenName.MP_LTHAN, "'<'");
		 reverseReservedWords.put(TokenName.MP_LEQUAL, "'<='");
		 reverseReservedWords.put(TokenName.MP_NEQUAL, "'<>'");
		 reverseReservedWords.put(TokenName.MP_ASSIGN, "':='");
		 reverseReservedWords.put(TokenName.MP_PLUS, "'+'");
		 reverseReservedWords.put(TokenName.MP_MINUS, "'-'");
		 reverseReservedWords.put(TokenName.MP_TIMES, "'*'");
		 reverseReservedWords.put(TokenName.MP_COLON, "':'");
		 reverseReservedWords.put(TokenName.MP_EOF, "end of file");
	}
	
	public static TokenName getReservedWord(String s) {
		return ReservedWords.get(s.toLowerCase());
	}
	
	public static String getReverseReservedWord(TokenName t) {
		if (reverseReservedWords == null)
			return "";
		return reverseReservedWords.get(t);
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
