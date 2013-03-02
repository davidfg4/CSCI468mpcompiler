import java.util.List;

public class Symbol {
	public enum Kind {
		VARIABLE, PARAMETER, PROCEDURE, FUNCTION
	}
	public enum Type {
		NONE, INTEGER, FIXED, FLOAT, STRING, BOOLEAN
	}
	public enum ParameterMode {
		COPY, REFERENCE
	}
	
	public String lexeme;
	public Kind kind;
	public Type type;
	public ParameterMode mode;
	public List<Symbol> parameters;
	
	//TODO: delete this
	Symbol(String l) {
		lexeme = l;
		kind = Kind.VARIABLE;
		type = Type.NONE;
	}
	
	// For variables and parameters
	Symbol(String l, Kind k, Type t)
	{
		lexeme = l;
		kind = k;
		type = t;
		if (k == Kind.PROCEDURE || k == Kind.FUNCTION) {
			System.err.println("Error: procedures and functions must have a parameter list");
			System.exit(1);
		}
	}
	
	// for functions and procedures
	Symbol(String l, Kind k, Type t, List<Symbol> p)
	{

		this(l,k,t);
		parameters = p;
		if (k == Kind.PROCEDURE && t != Type.NONE) {
			System.err.println("Error: procedures can not have a return type");
			System.exit(1);
		} else if (k == Kind.FUNCTION && t == Type.NONE) {
			System.err.println("Error: functions must have a return type");
			System.exit(1);
		}
	}
	
	public String toString() {
		return lexeme + " - kind: " + kind + ", type: " + type;
	}
}