import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class MPscanner {
	private String filename;
	private BufferedReader reader;
	private int lineNumber = 1;
	private int columnNumber = 1;
	private int tokenStartLine;
	private int tokenStartColumn;
	private List<String> lines = new ArrayList<String>();
	private StringBuilder lexeme = new StringBuilder();
	private String markedLexeme = "";
	private int markedLine, markedColumn;
	private Token token;
	private boolean fileComplete = false;
	
	MPscanner() {
		
	}

	public boolean openFile(String filename) {
		this.filename = filename;
		// read in all the lines so we know them beforehand for error reporting
		FileReader fr = openFileReader(filename);
		reader = new BufferedReader(fr);
		String line;
		lines.add(0, "");
		try {
			line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			System.err.println("Error: could not read " + filename);
			System.exit(1);
		}
		// reset the file reader to the start
		fr = openFileReader(filename);
		reader = new BufferedReader(fr);
		return true;
	}
	
	public FileReader openFileReader(String filename) {
		FileReader fr = null;
		try {
			fr = new FileReader(filename);
		} catch (FileNotFoundException e) {
			System.err.println("Error: File " + filename + " not found");
			System.exit(1);
		}
		return fr;
	}
	
	public Token getToken() {
		lexeme = new StringBuilder();
		tokenStartLine = lineNumber;
		tokenStartColumn = columnNumber;
		char ch = getNextChar();
	
		if (ch == (char)4) {
			if (fileComplete)
				return null;
			else {
				fileComplete = true;
				return returnToken(Token.TokenName.MP_EOF);
			}
		}
		else if (ch == '\n' || ch == '\r' || ch == '\t' || ch == ' ')
			return getToken();
		else if (ch == '.')
			return returnToken(Token.TokenName.MP_PERIOD);
		else if (ch == ',')
			return returnToken(Token.TokenName.MP_COMMA);
		else if (ch == ';')
			return returnToken(Token.TokenName.MP_SCOLON);
		else if (ch == '(')
			return returnToken(Token.TokenName.MP_LPAREN);
		else if (ch == ')')
			return returnToken(Token.TokenName.MP_RPAREN);
		else if (ch == '=')
			return returnToken(Token.TokenName.MP_EQUAL);
		else if (ch == '>') {
			markBuffer();
			ch = getNextChar();
			if (ch == '=')
				return returnToken(Token.TokenName.MP_GEQUAL);
			else {
				resetBuffer();
				return returnToken(Token.TokenName.MP_GTHAN);
			}
		}
		else if (ch == '<') {
			markBuffer();
			ch = getNextChar();
			if (ch == '=')
				return returnToken(Token.TokenName.MP_LEQUAL);
			else if ( ch == '>')
				return returnToken(Token.TokenName.MP_NEQUAL);
			else {
				resetBuffer();
				return returnToken(Token.TokenName.MP_LTHAN);
			}
		}
		else if (ch == ':') {
			markBuffer();
			ch = getNextChar();
			if (ch == '=')
				return returnToken(Token.TokenName.MP_ASSIGN);
			else {
				resetBuffer();
				return returnToken(Token.TokenName.MP_COLON);
			}
		}
		else if (ch == '+')
			return returnToken(Token.TokenName.MP_PLUS);
		else if (ch == '-')
			return returnToken(Token.TokenName.MP_MINUS);
		else if (ch == '*')
			return returnToken(Token.TokenName.MP_TIMES);
		else if (ch == '/')
			return returnToken(Token.TokenName.MP_DIV);
		else if (ch == '_') {
			markBuffer();
			ch = getNextChar();
			if (isLetter(ch) || isNumber(ch))
				return findIdentifier();
			else {
				resetBuffer();
				return returnToken(Token.TokenName.MP_ERROR);
			}
		}
		else if (isLetter(ch))
			return findIdentifier();
		else if (isNumber(ch))
			return findInteger();
		else if (ch == '\'') {
			return findString();
		}
		else if (ch == '{') 
			return ignoreComment();
		else
			return returnToken(Token.TokenName.MP_ERROR);
	}

	private Token ignoreComment() {
		markBuffer();
		char ch = getNextChar();
		while(ch != '}') {
			if(ch == (char)4 ) {
				// Return just the first line of the run-on comment.
				// The file pointer stays at the EOF.
				lexeme = new StringBuilder(lexeme.toString().split("\n")[0]);
				return returnToken(Token.TokenName.MP_RUN_COMMENT);	// comment not closed before EOF
			} else if (ch == '{') {
				System.err.println(getError(filename, lineNumber, columnNumber, "Warning: Comment started within comment"));
			}
			ch = getNextChar();
		}
		return getToken();	// ignore comment
	}
	
	private Token findIdentifier() {
		markBuffer();
		char ch = getNextChar();
		if (ch == '_') {
			ch = getNextChar();
			if (isLetter(ch) || isNumber(ch))
				return findIdentifier();
		} else if (isLetter(ch) || isNumber(ch)) {
			return findIdentifier();
		}
		resetBuffer();
		Token.TokenName reservedWord = Token.getReservedWord(lexeme.toString());
		if (reservedWord != null)
			return returnToken(reservedWord);
		else
			return returnToken(Token.TokenName.MP_IDENTIFIER);
	}
	
	private Token findInteger() {
		markBuffer();
		char ch = getNextChar();
		if(isNumber(ch))
			return findInteger();
		else if (ch == '.') {
			ch = getNextChar();
			if (isNumber(ch))
				return findFixed();
			else {
				resetBuffer();
				return returnToken(Token.TokenName.MP_INTEGER_LIT);
			}
		}
		else if (ch == 'e' || ch == 'E') {
			ch = getNextChar();
			if (ch == '+' || ch == '-')
				ch = getNextChar();
			if (isNumber(ch))
				return findFloat();
			else
				return returnToken(Token.TokenName.MP_INTEGER_LIT);
		}
		else {
			resetBuffer();
			return returnToken(Token.TokenName.MP_INTEGER_LIT);
		}
	}
	
	private Token findFixed() {
		markBuffer();
		char ch = getNextChar();
		if(isNumber(ch))
			return findFixed();
		else if (ch == 'e' || ch == 'E') {
			ch = getNextChar();
			if (ch == '+' || ch == '-')
				ch = getNextChar();
			if (isNumber(ch))
				return findFloat();
			else
				return returnToken(Token.TokenName.MP_FIXED_LIT);
		}
		else {
			resetBuffer();
			return returnToken(Token.TokenName.MP_FIXED_LIT);
		}
	}
	
	private Token findFloat() {
		markBuffer();
		char ch = getNextChar();
		if(isNumber(ch))
			return findFloat();
		else {
			resetBuffer();
			return returnToken(Token.TokenName.MP_FLOAT_LIT);
		}
	}
	
	private Token findString() {
		char ch = getNextChar();
		if (ch == '\'') {
			markBuffer();
			ch = getNextChar();
			if (ch == '\'') {
				// delete the double quote to make it a single quote
				lexeme.deleteCharAt(lexeme.length() - 1);
				return findString();
			} else {
				resetBuffer();
				// delete surrounding quotes
				lexeme.deleteCharAt(0);
				lexeme.deleteCharAt(lexeme.length() - 1);
				return returnToken(Token.TokenName.MP_STRING_LIT);
			}
		} else if (ch == (char)4 || ch == '\n') {
			lexeme.deleteCharAt(0);
			lexeme.deleteCharAt(lexeme.length() - 1);
			return returnToken(Token.TokenName.MP_RUN_STRING);
		}
		return findString();
	}
	
	private boolean isLetter(char c) {
		if(c >= 'a' && c <= 'z' ||
				c >= 'A' && c <= 'Z')
			return true;
		return false;
	}
	
	private boolean isNumber(char c) {
		if(c >= '0' && c <= '9')
			return true;
		return false;
	}
	
	private char getNextChar() {
		char ch = ' ';
		try {
			ch = (char) reader.read();
		} catch (IOException e) {
			System.err.println("Error: can't read the first char of " + filename);
			System.exit(1);
		}
		if (ch == (char) -1)
			ch = (char) 4;
		else
			lexeme.append(ch);
		columnNumber++;
		if((char)ch == '\n') {
			lineNumber++;
			columnNumber = 1;
		} else if (ch >= 65 && ch <=90) {
			// convert uppercase to lowercase
			ch = (char) (ch + 32);
		}
		return ch;
	}
	
	private void markBuffer() {
		markedLexeme = lexeme.toString();
		markedLine = lineNumber;
		markedColumn = columnNumber;
		try {
			reader.mark(512);
		} catch (IOException e) {
			System.err.println("Error: Failed to mark reader");
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
			System.err.println("Error: Failed to reset marker");
			System.exit(1);
		}
	}
	
	public String getError(String filename, int line, int col, String errorName) {
		String error = "  File \"" + filename + "\", line " + line + ":\n";
		error += "    " + getLine(line) + "\n";
		error += String.format("    %1$" + (col+1) + "s", "^\n");
		error += errorName + " at column " + col;
		return error;
	}
	
	public String getError(Token t, String errorName) {
		return getError(filename, t.getLine(), t.getColumn(), errorName);
	}
	
	private String getLine(int l) {
		if (l > lines.size()-1)
			return "<end of file>";
		else
			return lines.get(l);
	}
	
	private Token returnToken(Token.TokenName tokenName) {
		token = new Token(tokenName, tokenStartLine, tokenStartColumn,
				lexeme.toString());
		return token;
	}
	
	public String getLexeme() {
		if (token == null)
			return "";
		else
			return token.getLexeme();
	}
	
	public int getLineNumber() {
		if (token == null)
			return 0;
		else
			return token.getLine();
	}
	
	public int getColumnNumber() {
		if (token == null)
			return 0;
		else
			return token.getColumn();
	}
}