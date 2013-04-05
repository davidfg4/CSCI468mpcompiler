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
	public int size, offset, nestLevel;	// for semantics
	public String label;
	
	public Symbol() {/*for semantic records */}
	
	public Symbol(String l, Kind k, Type t)
	{
		lexeme = l;
		kind = k;
		type = t;
		if (k == Kind.PROCEDURE || k == Kind.FUNCTION)
			parameters = new ArrayList<Symbol>();
		// The machine spec seems to indicate that ints, floats, 
		// and strings all have size of 1, but the following
		// should make for easy adjustment if this is not the case.
		switch(type) {
			case INTEGER:
			case FLOAT:
			case STRING:
			case BOOLEAN:
				size = 1;	
				break;
		}
	}

	public String toString() {
		return lexeme + " - kind: " + kind + ", type: " + type;
	}
}