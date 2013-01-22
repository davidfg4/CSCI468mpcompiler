import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class MPscanner {
	private BufferedReader reader;
	private int lineNumber = 1;
	private int columnNumber = 1;
	private StringBuilder lexeme = new StringBuilder();
	private String markedLexeme = "";
	private Token token;
	
	MPscanner() {
		
	}

    public boolean openFile(String filename) throws FileNotFoundException {
    	FileReader fr = new FileReader(filename);
        reader = new BufferedReader(fr);
    	return true;
    }
    
    public Token getToken() throws IOException {
    	char ch = getNextChar();

    	if (ch == (char)4) {
    		return returnToken(Token.TokenName.MP_EOF);
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
    	else if(ch >= 'a' && ch <= 'z' ||
    			ch >= 'A' && ch <= 'Z') {
    		return null;
    	}
    	else if(ch >= '0' && ch <= '9') {
    		return null;
    	}
    	else {
    		return returnToken(Token.TokenName.MP_ERROR);
    	}
    }
    
    private char getNextChar() throws IOException {
    	//TODO get rid of whitespace
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
    	try {
			reader.mark(10);
		} catch (IOException e) {
			System.out.println("Error: Failed to mark reader");
			System.exit(1);
		}
    }
    
    private void resetBuffer() {
    	lexeme = new StringBuilder(markedLexeme);
    	try {
			reader.reset();
		} catch (IOException e) {
			System.out.println("Error: Failed to reset marker");
			System.exit(1);
		}
    }
    
    private Token returnToken(Token.TokenName tokenName) {
    	token = new Token(tokenName, lineNumber, columnNumber,
    			lexeme.toString());
    	lexeme = new StringBuilder();
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