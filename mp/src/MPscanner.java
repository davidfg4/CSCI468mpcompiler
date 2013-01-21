import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

class MPscanner {
	BufferedReader reader;
	
	MPscanner() {
		
	}

    public boolean openFile(String filename) throws FileNotFoundException {
    	FileReader fr = new FileReader(filename);
        reader = new BufferedReader(fr);
    	return true;
    }
    
    public void getToken() {
    	
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