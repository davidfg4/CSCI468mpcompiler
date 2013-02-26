public class Symbol {
	public String lexeme;
	// also needs type(as an enum(int, float, etc), address
	
	Symbol(String l) {
		lexeme = l;
	}
	
	public int hashCode() {
		return lexeme.hashCode();
	}
}