import java.util.ArrayList;
import java.util.List;

public class Symbol {
	public enum Kind {
		VARIABLE, PARAMETER, PROCEDURE, FUNCTION
	}
	public enum Type {
		NONE, INTEGER, FLOAT, STRING, BOOLEAN
	}
	public enum ParameterMode {
		COPY, REFERENCE
	}

	public String lexeme;
	public Kind kind;
	public Type type;
	public ParameterMode mode;
	public List<Symbol> parameters;

	// For variables and parameters
	Symbol(String l, Kind k, Type t)
	{
		lexeme = l;
		kind = k;
		type = t;
		if (k == Kind.PROCEDURE || k == Kind.FUNCTION)
			parameters = new ArrayList<Symbol>();
	}

	public String toString() {
		return lexeme + " - kind: " + kind + ", type: " + type;
	}
}