public class symbol {
	public String lexeme;
	// also needs type(as an enum(int, float, etc), address
	
	symbol(String l) {
		lexeme = l;
	}
	
	public int hashCode() {
		return lexeme.hashCode();
	}
}