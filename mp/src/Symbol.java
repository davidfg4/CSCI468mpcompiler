public class Symbol {
	public String lexeme;
	// also needs type(as an enum(int, float, etc), address
	
	Symbol(String l) {
		lexeme = l;
	}
	
	public String toString() {
		return lexeme + " - more info here";
	}
}