import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class MPscanner {
	private BufferedReader reader;
	private int lineNumber = 1;
	private int columnNumber = 1;
	private StringBuilder lexeme;
	private Token token;
	
	MPscanner() {
		
	}

    public boolean openFile(String filename) throws FileNotFoundException {
    	FileReader fr = new FileReader(filename);
        reader = new BufferedReader(fr);
    	return true;
    }
    
    public Token getToken() throws IOException {
    	Token token = null;
    	int ch = getNextChar();
    	
    	if(ch < 0) {
    		return new Token(Token.TokenName.MP_EOF, lineNumber, columnNumber, String.valueOf(ch) );
    	}
    	char nextChar = (char) ch;
    	if(nextChar >= 'a' && nextChar <= 'z' || nextChar >= 'A' && nextChar <= 'Z') {
    	}
    	
    	else if(nextChar >= '0' && nextChar <= '9') {
    	}
    	
    	return token;
    }
    private int getNextChar() throws IOException {
    	//TODO get rid of whitespace
    	int ch = reader.read();
    	lexeme.append(ch);
    	columnNumber++;
    	if((char)ch == '\n') {
    		lineNumber++;
    		columnNumber = 1;
    	}
    	return ch;
    }
    
    public void getLexeme() {
    	
    }
    
    public int getLineNumber() {
    	return 0;
    }
    
    public int getColumnNumber() {
    	return 0;
    }
}