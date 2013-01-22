import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class MPscanner {
	private BufferedReader reader;
	private int lineNumber = 1;
	private int columnNumber = 1;
	private Token token;
	
	MPscanner() {
		
	}

    public boolean openFile(String filename) throws FileNotFoundException {
    	FileReader fr = new FileReader(filename);
        reader = new BufferedReader(fr);
    	return true;
    }
    
    public Token getToken() throws IOException {
    	//Token token = new Token();
    	int ch = reader.read();
    	
    	if(ch < 0) {
    		return new Token(Token.TokenName.MP_EOF, lineNumber, columnNumber, String.valueOf(ch) );
    	}
    	char nextChar = (char) ch;
    	
    	
    	return token;
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