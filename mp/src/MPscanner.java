import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class MPscanner {
	private BufferedReader reader;
	private int lineNumber = 1;
	private int columnNumber = 1;
	private int tokenStartColumn;
	private StringBuilder lexeme = new StringBuilder();
	private String markedLexeme = "";
	private int markedLine, markedColumn;
	private Token token;
	
	MPscanner() {
		
	}

    public boolean openFile(String filename) throws FileNotFoundException {
    	FileReader fr = new FileReader(filename);
        reader = new BufferedReader(fr);
    	return true;
    }
    
    public Token getToken() throws IOException {
    	lexeme = new StringBuilder();
    	tokenStartColumn = columnNumber;
    	char ch = getNextChar();

    	if (ch == (char)4) {
    		return returnToken(Token.TokenName.MP_EOF);
    	}
    	else if (ch == '\n' || ch == ' ') {
    		return getToken();
    	}
    	else if (ch == '.') {
    		return returnToken(Token.TokenName.MP_PERIOD);
    	}
    	else if (ch == ',') {
    		return returnToken(Token.TokenName.MP_COMMA);
    	}
    	else if (ch == ';') {
    		return returnToken(Token.TokenName.MP_SCOLON);
    	}
    	else if (ch == '(') {
    		return returnToken(Token.TokenName.MP_LPAREN);
    	}
    	else if (ch == ')') {
    		return returnToken(Token.TokenName.MP_RPAREN);
    	}
    	else if (ch == '=') {
    		return returnToken(Token.TokenName.MP_EQUAL);
    	}
    	else if (ch == '>') {
    		markBuffer();
    		ch = getNextChar();
    		if (ch == '=') {
    			return returnToken(Token.TokenName.MP_GEQUEL);
    		}
    		else {
    			resetBuffer();
        		return returnToken(Token.TokenName.MP_GTHAN);
    		}
    	}
    	else if (ch == '<') {
    		markBuffer();
    		ch = getNextChar();
    		if (ch == '=') {
    			return returnToken(Token.TokenName.MP_LEQUAL);
    		}
    		else if ( ch == '>') {
    			return returnToken(Token.TokenName.MP_NEQUAL);
    		}
    		else {
    			resetBuffer();
    			return returnToken(Token.TokenName.MP_LTHAN);
    		}
    	}
    	else if (ch == ':') {
    		markBuffer();
    		ch = getNextChar();
    		if (ch == '=') {
    			return returnToken(Token.TokenName.MP_ASSIGN);
    		}
    		else {
    			resetBuffer();
    			return returnToken(Token.TokenName.MP_COLON);
    		}
    	}
    	else if (ch == '+') {
    		return returnToken(Token.TokenName.MP_PLUS);
    	}
    	else if (ch == '-') {
    		return returnToken(Token.TokenName.MP_MINUS);
    	}
    	else if (ch == '*') {
    		return returnToken(Token.TokenName.MP_TIMES);
    	}
    	else if (ch == '_') {
    		markBuffer();
    		ch = getNextChar();
        	if (isLetter(ch) || isNumber(ch)) {
       			return findIdentifier();
        	}
        	else {
        		resetBuffer();
        		return returnToken(Token.TokenName.MP_ERROR);
        	}
    	}
    	else if (isLetter(ch)) {
    		return findIdentifier();
    	}
    	else if (isNumber(ch)) {
    		return null;
    	}
    	else {
    		return returnToken(Token.TokenName.MP_ERROR);
    	}
    }
    
    private Token findIdentifier() throws IOException {
    	markBuffer();
    	char ch = getNextChar();
    	if (ch == '_') {
    		ch = getNextChar();
    		if (isLetter(ch) || isNumber(ch)) {
    			return findIdentifier();
    		}
    	}
    	else if (isLetter(ch) || isNumber(ch)) {
    		return findIdentifier();
    	}
    	resetBuffer();
    	Token.TokenName reservedWord = Token.getReservedWord(lexeme.toString());
    	if (reservedWord != null) {
    		return returnToken(reservedWord);
    	}
    	else {
    		return returnToken(Token.TokenName.MP_IDENTIFIER);
    	}
    }
    
    private boolean isLetter(char c) {
    	if(c >= 'a' && c <= 'z' ||
    			c >= 'A' && c <= 'Z') {
    		return true;
    	}
    	return false;
    }
    
    private boolean isNumber(char c) {
    	if(c >= '0' && c <= '9') {
    		return true;
    	}
    	return false;
    }
    
    private char getNextChar() throws IOException {
    	int ch2 = reader.read();
    	if (ch2 == -1) {
    		ch2 = 4;
    	}
    	char ch = (char) ch2;
    	lexeme.append(ch);
    	columnNumber++;
    	if((char)ch == '\n') {
    		lineNumber++;
    		columnNumber = 1;
    	}
    	return ch;
    }
    
    private void markBuffer() {
    	markedLexeme = lexeme.toString();
    	markedLine = lineNumber;
    	markedColumn = columnNumber;
    	try {
			reader.mark(10);
		} catch (IOException e) {
			System.out.println("Error: Failed to mark reader");
			System.exit(1);
		}
    }
    
    private void resetBuffer() {
    	lexeme = new StringBuilder(markedLexeme);
    	lineNumber = markedLine;
    	columnNumber = markedColumn;
    	try {
			reader.reset();
		} catch (IOException e) {
			System.out.println("Error: Failed to reset marker");
			System.exit(1);
		}
    }
    
    private Token returnToken(Token.TokenName tokenName) {
    	token = new Token(tokenName, lineNumber, tokenStartColumn,
    			lexeme.toString());
    	return token;
    }
    
    public String getLexeme() {
    	return token.getLexeme();
    }
    
    public int getLineNumber() {
    	return token.getLine();
    }
    
    public int getColumnNumber() {
    	return token.getColumn();
    }
}